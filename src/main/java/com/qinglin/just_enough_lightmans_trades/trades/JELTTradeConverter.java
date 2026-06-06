package com.qinglin.just_enough_lightmans_trades.trades;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class JELTTradeConverter {

    private static ItemStack parseItem(ItemEntry entry)
    {
        if(entry == null || entry.ID == null || entry.ID.isBlank())
            return ItemStack.EMPTY;

        ResourceLocation key =
                ResourceLocation.tryParse(entry.ID);

        if(key == null)
            return ItemStack.EMPTY;

        Item item =
                BuiltInRegistries.ITEM.get(key);

        if(item == null)
            return ItemStack.EMPTY;

        ItemStack stack =
                new ItemStack(
                        item,
                        Math.max(1, entry.Count)
                );

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

    private static FluidStack parseFluid(
            ProductEntry product,
            int quantity)
    {
        if(product == null || product.id == null)
            return FluidStack.EMPTY;

        ResourceLocation key =
                ResourceLocation.tryParse(product.id);

        if(key == null)
            return FluidStack.EMPTY;

        Fluid fluid =
                ForgeRegistries.FLUIDS.getValue(key);

        if(fluid == null)
            return FluidStack.EMPTY;

        return new FluidStack(
                fluid,
                Math.max(1, product.amount)
                        * Math.max(1, quantity)
        );
    }

    private static void addIfPresent(
            List<ItemStack> list,
            ItemEntry entry)
    {
        if(entry == null)
            return;

        ItemStack stack = parseItem(entry);

        if(!stack.isEmpty())
            list.add(stack);
    }

    private static void addPriceAsItems(
            List<ItemStack> list,
            PriceEntry price)
    {
        if(price == null || price.Value == null)
            return;

        for(CoinEntry coin : price.Value)
        {
            if(coin == null || coin.Coin == null)
                continue;

            ItemEntry temp = new ItemEntry();
            temp.ID = coin.Coin;
            temp.Count = coin.Amount;

            ItemStack stack = parseItem(temp);

            if(!stack.isEmpty())
                list.add(stack);
        }
    }

    public static JELTTrade convert(
            TraderEntry trader,
            TradeEntry trade)
    {
        if(trader == null
                || trade == null
                || trade.TradeType == null)
        {
            return null;
        }

        List<ItemStack> itemInputs =
                new ArrayList<>();

        List<ItemStack> itemOutputs =
                new ArrayList<>();

        List<FluidStack> fluidInputs =
                new ArrayList<>();

        List<FluidStack> fluidOutputs =
                new ArrayList<>();

        switch(trade.TradeType)
        {
            case "SALE" ->
            {
                addPriceAsItems(
                        itemInputs,
                        trade.Price
                );

                addIfPresent(
                        itemOutputs,
                        trade.SellItem
                );

                addIfPresent(
                        itemOutputs,
                        trade.SellItem2
                );

                if(trade.Product != null)
                {
                    FluidStack fluid =
                            parseFluid(
                                    trade.Product,
                                    trade.Quantity
                            );

                    if(!fluid.isEmpty())
                        fluidOutputs.add(fluid);
                }
            }

            case "PURCHASE" ->
            {
                addIfPresent(
                        itemInputs,
                        trade.SellItem
                );

                addIfPresent(
                        itemInputs,
                        trade.SellItem2
                );

                if(trade.Product != null)
                {
                    FluidStack fluid =
                            parseFluid(
                                    trade.Product,
                                    trade.Quantity
                            );

                    if(!fluid.isEmpty())
                        fluidInputs.add(fluid);
                }

                addPriceAsItems(
                        itemOutputs,
                        trade.Price
                );
            }

            case "BARTER" ->
            {

                addIfPresent(
                        itemInputs,
                        trade.BarterItem
                );

                addIfPresent(
                        itemInputs,
                        trade.BarterItem2
                );

                addIfPresent(
                        itemOutputs,
                        trade.SellItem
                );

                addIfPresent(
                        itemOutputs,
                        trade.SellItem2
                );
            }

            default ->
            {
                return null;
            }
        }

        return new JELTTrade(
                trader.ID != null
                        ? trader.ID
                        : "unknown",

                trader.Name != null
                        ? trader.Name
                        : "unknown",

                trader.OwnerName != null
                        ? trader.OwnerName
                        : "",

                trade.TradeType,

                itemInputs,
                itemOutputs,

                fluidInputs,
                fluidOutputs
        );
    }

    public static List<JELTTrade> convertAll(
            PersistentTraderFile file)
    {
        List<JELTTrade> result =
                new ArrayList<>();

        if(file == null
                || file.Traders == null)
        {
            return result;
        }

        for(TraderEntry trader : file.Traders)
        {
            if(trader == null
                    || trader.Trades == null)
            {
                continue;
            }

            for(TradeEntry trade : trader.Trades)
            {
                try
                {
                    JELTTrade converted =
                            convert(trader, trade);

                    if(converted != null)
                    {
                        result.add(converted);
                    }
                }
                catch(Exception e)
                {
                    System.err.println(
                            "[JELT] Failed to convert trade: "
                                    + e.getMessage()
                    );
                }
            }
        }

        return result;
    }
}