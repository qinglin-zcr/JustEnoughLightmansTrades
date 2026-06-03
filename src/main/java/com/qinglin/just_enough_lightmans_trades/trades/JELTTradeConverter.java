package com.qinglin.just_enough_lightmans_trades.trades;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JELTTradeConverter {

    private static ItemStack parseItem(String id, int count)
    {
        ResourceLocation key =
                ResourceLocation.tryParse(id);

        if(key == null)
            return ItemStack.EMPTY;

        Item item =
                BuiltInRegistries.ITEM.get(key);

        return new ItemStack(item, count);
    }

    public static JELTTrade convert(
            TraderEntry trader,
            TradeEntry trade)
    {
        ItemStack sellItem =
                parseItem(
                        trade.SellItem.ID,
                        trade.SellItem.Count
                );

        List<ItemStack> priceItems =
                new ArrayList<>();

        if(trade.Price != null
                && trade.Price.Value != null)
        {
            for(CoinEntry coin : trade.Price.Value)
            {
                ItemStack stack =
                        parseItem(
                                coin.Coin,
                                coin.Amount
                        );

                if(!stack.isEmpty())
                    priceItems.add(stack);
            }
        }

        return new JELTTrade(
                trader.ID,
                trader.Name,
                trade.TradeType,
                sellItem,
                priceItems
        );
    }

    public static List<JELTTrade> convertAll(
            PersistentTraderFile file)
    {
        List<JELTTrade> result =
                new ArrayList<>();

        if(file == null || file.Traders == null)
            return result;

        for(TraderEntry trader : file.Traders)
        {
            if(trader.Trades == null)
                continue;

            for(TradeEntry trade : trader.Trades)
            {
                result.add(
                        convert(trader, trade)
                );
            }
        }

        return result;
    }

}