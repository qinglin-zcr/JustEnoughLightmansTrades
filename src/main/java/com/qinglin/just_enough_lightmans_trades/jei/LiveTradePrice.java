package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import io.github.lightman314.lightmanscurrency.api.money.value.IItemBasedValue;
import io.github.lightman314.lightmanscurrency.api.money.value.MoneyValue;
import io.github.lightman314.lightmanscurrency.api.traders.TradeContext;
import io.github.lightman314.lightmanscurrency.api.traders.TraderData;
import io.github.lightman314.lightmanscurrency.api.traders.trade.TradeData;
import io.github.lightman314.lightmanscurrency.common.data.types.TraderDataCache;
import io.github.lightman314.lightmanscurrency.common.traders.rules.IRuleLoadListener;
import io.github.lightman314.lightmanscurrency.common.traders.rules.ITradeRuleHost;
import io.github.lightman314.lightmanscurrency.common.traders.rules.TradeRule;
import io.github.lightman314.lightmanscurrency.common.traders.rules.types.PriceFluctuation;
import io.github.lightman314.lightmanscurrency.common.traders.rules.types.TimedSale;
import io.github.lightman314.lightmanscurrency.common.traders.rules.types.FreeSample;
import io.github.lightman314.lightmanscurrency.common.traders.rules.types.DiscountCodes;
import io.github.lightman314.lightmanscurrency.api.network.LazyPacketData;
import net.minecraft.nbt.Tag;
import io.github.lightman314.lightmanscurrency.util.TimeUtil;
import com.qinglin.just_enough_lightmans_trades.JustEnoughLightmansTrades;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Resolves prices using the same rules and client-side trader data as the shop screen. */
@Mod.EventBusSubscriber(modid = JustEnoughLightmansTrades.MOD_ID, value = Dist.CLIENT)
public final class LiveTradePrice {

    private static final AtomicLong RULE_REVISION = new AtomicLong();
    private static final Map<ITradeRuleHost, Long> RULE_REVISIONS =
            java.util.Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<JELTTrade, CachedQuote> QUOTES = new WeakHashMap<>();
    private static final Map<String, TraderData> TRADERS = new java.util.HashMap<>();

    static {
        // LC invokes this when synced trader/trade rules are loaded; do not recalculate here.
        TradeRule.addLoadListener(new IRuleLoadListener() {
            @Override
            public void afterLoading(ITradeRuleHost host, List<CompoundTag> tags, List<TradeRule> rules) {
                RULE_REVISIONS.put(host, RULE_REVISION.incrementAndGet());
            }
        });
    }

    private LiveTradePrice() {}

    static List<ItemStack> getBasePrice(JELTTrade recipe) {
        return "SALE".equals(recipe.getTradeType())
                ? recipe.getItemInputs() : recipe.getItemOutputs();
    }

    static List<ItemStack> getPrice(JELTTrade recipe) {
        List<ItemStack> fallback = getBasePrice(recipe);
        Minecraft mc = Minecraft.getInstance();
        // JEI also builds layouts at startup, before any server has synced its traders.
        if(mc.player == null || recipe.getTradeIndex() < 0) {
            QUOTES.remove(recipe);
            return fallback;
        }

        TraderDataCache cache = TraderDataCache.TYPE.get(true);
        if(cache == null) {
            QUOTES.remove(recipe);
            return fallback;
        }
        TraderData trader = TRADERS.get(recipe.getTraderId());
        if(trader == null || cache.getTrader(trader.getID()) != trader
                || !recipe.getTraderId().equals(trader.getPersistentID())) {
            trader = cache.getTrader(recipe.getTraderId());
            if(trader == null)
                TRADERS.remove(recipe.getTraderId());
            else
                TRADERS.put(recipe.getTraderId(), trader);
        }
        if(trader == null || recipe.getTradeIndex() >= trader.getTradeData().size()) {
            QUOTES.remove(recipe);
            return fallback;
        }

        TradeData trade = trader.getTrade(recipe.getTradeIndex());
        if(trade == null || !trade.getTradeDirection().name().equals(recipe.getTradeType())) {
            QUOTES.remove(recipe);
            return fallback;
        }

        long now = TimeUtil.getCurrentTime();
        QuoteState state = new QuoteState(trader, trade, trade.getCost(), mc.player.getUUID(),
                RULE_REVISIONS.getOrDefault(trader, 0L), RULE_REVISIONS.getOrDefault(trade, 0L));
        CachedQuote cached = QUOTES.get(recipe);
        if(cached == null || !state.equals(cached.state())) {
            cached = new CachedQuote(state, hasDiscountCodes(trader, trade), new PriceQuoteCache<>());
            QUOTES.put(recipe, cached);
        }
        int inventoryVersion = cached.watchInventory() ? mc.player.getInventory().getTimesChanged() : 0;
        TraderData currentTrader = trader;
        return cached.quote().get(inventoryVersion, now,
                () -> nextRefreshAt(currentTrader, trade, now), () -> calculatePrice(currentTrader, trade, fallback));
    }

    private static List<ItemStack> calculatePrice(TraderData trader, TradeData trade, List<ItemStack> fallback) {
        Minecraft mc = Minecraft.getInstance();
        // false avoids constructing payment handlers; this context is only used to quote a price.
        TradeContext context = TradeContext.create(trader, mc.player, false).build();
        MoneyValue cost = trade.getCost(context);
        if(cost.isFree() || cost.isEmpty())
            return List.of();
        if(cost instanceof IItemBasedValue itemValue)
            return itemValue.getAsItemList();
        return fallback;
    }

    private static long nextRefreshAt(TraderData trader, TradeData trade, long now) {
        List<TradeRule> rules = new ArrayList<>(trader.getRules());
        rules.addAll(trade.getRules());
        long next = Long.MAX_VALUE;
        for(TradeRule rule : rules) {
            if(!rule.isActive())
                continue;
            if(rule instanceof PriceFluctuation fluctuation) {
                next = Math.min(next, PriceQuoteCache.nextBoundary(now, fluctuation.getDuration()));
            } else if(rule instanceof TimedSale sale) {
                if(sale.getStartTime() > now)
                    next = Math.min(next, sale.getStartTime());
                if(sale.getStartTime() != 0) {
                    next = Math.min(next, PriceQuoteCache.nextExpiry(
                            new long[]{sale.getStartTime()}, sale.getDuration(), now));
                }
            } else if(rule instanceof FreeSample sample && sample.getTimeLimit() > 0) {
                // Serialize only when calculating a quote, not on every JEI display callback.
                // LC's public API exposes the settings but not the per-player memory.
                var memory = sample.save().getList("Memory", Tag.TAG_COMPOUND);
                UUID player = Minecraft.getInstance().player.getUUID();
                for(int i = 0; i < memory.size(); i++) {
                    CompoundTag entry = memory.getCompound(i);
                    if(entry.hasUUID("ID") && player.equals(entry.getUUID("ID"))) {
                        next = Math.min(next, PriceQuoteCache.nextExpiry(
                                entry.getLongArray("Times"), sample.getTimeLimit(), now));
                        break;
                    }
                }
            }
            // DemandPricing and PlayerDiscounts change with synced trader data/player identity.
            // Unknown rules also reuse quotes until a known input changes; no polling fallback.
        }
        return next;
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        QUOTES.clear();
        TRADERS.clear();
        RULE_REVISIONS.clear();
    }

    /** Called after LC has applied a client-side trader synchronization packet. */
    public static void onTraderSync(TraderDataCache cache, LazyPacketData data) {
        if(data.contains("ClearTraders")) {
            QUOTES.clear();
            TRADERS.clear();
            RULE_REVISIONS.clear();
        }
        if(data.contains("DeleteTrader")) {
            long id = data.getLong("DeleteTrader");
            QUOTES.values().removeIf(quote -> quote.state().trader().getID() == id);
            TRADERS.values().removeIf(trader -> trader.getID() == id);
        }
        if(data.contains("UpdateTrader")) {
            TraderData trader = cache.getTrader(data.getNBT("UpdateTrader").getLong("ID"));
            if(trader != null)
                RULE_REVISIONS.put(trader, RULE_REVISION.incrementAndGet());
        }
    }

    private static boolean hasDiscountCodes(TraderData trader, TradeData trade) {
        return java.util.stream.Stream.concat(trader.getRules().stream(), trade.getRules().stream())
                .anyMatch(rule -> rule.isActive() && rule instanceof DiscountCodes);
    }

    private record CachedQuote(QuoteState state, boolean watchInventory,
                               PriceQuoteCache<List<ItemStack>> quote) {}

    private record QuoteState(TraderData trader, TradeData trade, MoneyValue basePrice,
                              UUID player, long traderRuleRevision, long tradeRuleRevision) {}
}
