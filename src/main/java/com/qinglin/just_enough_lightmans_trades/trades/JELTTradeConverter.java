package com.qinglin.just_enough_lightmans_trades.trades;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JELTTradeConverter {

    private static ItemStack parseItem(ItemEntry entry)
    {
        if(entry == null)
            return ItemStack.EMPTY;

        ResourceLocation key =
                ResourceLocation.tryParse(entry.ID);

        if(key == null)
            return ItemStack.EMPTY;

        Item item =
                BuiltInRegistries.ITEM.get(key);

        ItemStack stack =
                new ItemStack(item, entry.Count);

        if(entry.Tag != null && !entry.Tag.isBlank())
        {
            try
            {
                CompoundTag tag =
                        TagParser.parseTag(entry.Tag);

                stack.setTag(tag);
            }
            catch(CommandSyntaxException ignored)
            {
            }
        }

        return stack;
    }

    public static JELTTrade convert(
            TraderEntry trader,
            TradeEntry trade)
    {
        List<ItemStack> inputs =
                new ArrayList<>();

        List<ItemStack> outputs =
                new ArrayList<>();

        switch(trade.TradeType)
        {
            case "SALE" -> {

                if(trade.Price != null &&
                        trade.Price.Value != null)
                {
                    for(CoinEntry coin : trade.Price.Value)
                    {
                        ItemEntry temp =
                                new ItemEntry();

                        temp.ID = coin.Coin;
                        temp.Count = coin.Amount;

                        inputs.add(parseItem(temp));
                    }
                }

                outputs.add(
                        parseItem(trade.SellItem)
                );
            }

            case "PURCHASE" -> {

                inputs.add(
                        parseItem(trade.SellItem)
                );

                if(trade.Price != null &&
                        trade.Price.Value != null)
                {
                    for(CoinEntry coin : trade.Price.Value)
                    {
                        ItemEntry temp =
                                new ItemEntry();

                        temp.ID = coin.Coin;
                        temp.Count = coin.Amount;

                        outputs.add(parseItem(temp));
                    }
                }
            }

            case "BARTER" -> {

                inputs.add(
                        parseItem(trade.BarterItem)
                );

                outputs.add(
                        parseItem(trade.SellItem)
                );
            }

            default -> {
                return null;
            }
        }

        return new JELTTrade(
                trader.ID,
                trader.Name,
                trade.TradeType,
                inputs,
                outputs
        );
    }

    public static List<JELTTrade> convertAll(
            PersistentTraderFile file)
    {
        List<JELTTrade> result =
                new ArrayList<>();

        if(file == null ||
                file.Traders == null)
            return result;

        for(TraderEntry trader : file.Traders)
        {
            if(trader.Trades == null)
                continue;

            for(TradeEntry trade : trader.Trades)
            {
                JELTTrade converted =
                        convert(trader, trade);

                if(converted != null)
                    result.add(converted);
            }
        }

        return result;
    }
}