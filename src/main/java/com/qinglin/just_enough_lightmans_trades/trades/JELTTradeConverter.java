package com.qinglin.just_enough_lightmans_trades.trades;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class JELTTradeConverter {
    private static List<ItemEntry> getItemSeries(
            TradeEntry trade,
            String prefix)
    {
        class IndexedItem
        {
            int index;
            ItemEntry item;

            IndexedItem(int index, ItemEntry item)
            {
                this.index = index;
                this.item = item;
            }
        }

        List<IndexedItem> temp = new ArrayList<>();

        for(Field field : TradeEntry.class.getFields())
        {
            String name = field.getName();

            if(!name.startsWith(prefix))
                continue;

            try
            {
                Object value = field.get(trade);

                if(!(value instanceof ItemEntry item))
                    continue;

                String suffix =
                        name.substring(prefix.length());

                int index;

                if(suffix.isEmpty())
                {
                    index = 1;
                }
                else
                {
                    index = Integer.parseInt(suffix);
                }

                temp.add(
                        new IndexedItem(index, item)
                );
            }
            catch(Exception ignored)
            {
            }
        }

        temp.sort(
                Comparator.comparingInt(a -> a.index)
        );

        List<ItemEntry> result = new ArrayList<>();

        for(IndexedItem entry : temp)
        {
            result.add(entry.item);
        }

        return result;
    }

    private static ItemStack parseItem(ItemEntry entry)
    {
        if (entry == null || entry.ID == null || entry.ID.isBlank())
            return ItemStack.EMPTY;

        ResourceLocation key = ResourceLocation.tryParse(entry.ID);
        if (key == null)
            return ItemStack.EMPTY;

        Item item = BuiltInRegistries.ITEM.get(key);
        if (item == null)
            return ItemStack.EMPTY;

        int count = Math.max(1, entry.Count);

        ItemStack stack = new ItemStack(item, count);

        if (entry.Tag != null && !entry.Tag.isBlank())
        {
            try
            {
                CompoundTag tag = TagParser.parseTag(entry.Tag);
                stack.setTag(tag);
            }
            catch (CommandSyntaxException ignored)
            {
                // ignore invalid NBT
            }
        }

        return stack;
    }

    public static JELTTrade convert(
            TraderEntry trader,
            TradeEntry trade)
    {
        if (trader == null || trade == null || trade.TradeType == null)
            return null;

        List<ItemStack> inputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();

        switch (trade.TradeType)
        {
            case "SALE" ->
            {
                if (trade.Price != null && trade.Price.Value != null)
                {
                    for (CoinEntry coin : trade.Price.Value)
                    {
                        if (coin == null || coin.Coin == null)
                            continue;

                        ItemEntry temp = new ItemEntry();
                        temp.ID = coin.Coin;
                        temp.Count = coin.Amount;

                        inputs.add(parseItem(temp));
                    }
                }

                if (trade.SellItem != null)
                    outputs.add(parseItem(trade.SellItem));
            }

            case "PURCHASE" ->
            {
                if (trade.SellItem != null)
                    inputs.add(parseItem(trade.SellItem));

                if (trade.Price != null && trade.Price.Value != null)
                {
                    for (CoinEntry coin : trade.Price.Value)
                    {
                        if (coin == null || coin.Coin == null)
                            continue;

                        ItemEntry temp = new ItemEntry();
                        temp.ID = coin.Coin;
                        temp.Count = coin.Amount;

                        outputs.add(parseItem(temp));
                    }
                }
            }

            case "BARTER" ->
            {
                for(ItemEntry item :
                        getItemSeries(trade, "BarterItem"))
                {
                    ItemStack stack = parseItem(item);

                    if(!stack.isEmpty())
                        inputs.add(stack);
                }

                for(ItemEntry item :
                        getItemSeries(trade, "SellItem"))
                {
                    ItemStack stack = parseItem(item);

                    if(!stack.isEmpty())
                        outputs.add(stack);
                }
            }

            default ->
            {
                return null;
            }
        }

        return new JELTTrade(
                trader.ID != null ? trader.ID : "unknown",
                trader.Name != null ? trader.Name : "unknown",
                trade.TradeType,
                inputs,
                outputs
        );
    }

    public static List<JELTTrade> convertAll(PersistentTraderFile file)
    {
        List<JELTTrade> result = new ArrayList<>();

        if (file == null || file.Traders == null)
            return result;

        for (TraderEntry trader : file.Traders)
        {
            if (trader == null || trader.Trades == null)
                continue;

            for (TradeEntry trade : trader.Trades)
            {
                try
                {
                    JELTTrade converted = convert(trader, trade);
                    if (converted != null)
                        result.add(converted);
                }
                catch (Exception e)
                {
                    // 防止单条坏数据炸整个 mod
                    System.err.println("[JELT] Failed to convert trade: " + e.getMessage());
                }
            }
        }

        return result;
    }
}