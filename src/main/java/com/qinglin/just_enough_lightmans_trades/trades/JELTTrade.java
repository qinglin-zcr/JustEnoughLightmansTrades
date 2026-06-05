package com.qinglin.just_enough_lightmans_trades.trades;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class JELTTrade {

    private final String traderId;

    private final String traderName;

    private final String traderOwnerName;

    private final String tradeType;

    private final List<ItemStack> itemInputs;
    private final List<ItemStack> itemOutputs;

    private final List<FluidStack> fluidInputs;
    private final List<FluidStack> fluidOutputs;

    public JELTTrade(
            String traderId,
            String traderName,
            String traderOwnerName,
            String tradeType,
            List<ItemStack> itemInputs,
            List<ItemStack> itemOutputs,
            List<FluidStack> fluidInputs,
            List<FluidStack> fluidOutputs)
    {
        this.traderId = traderId;
        this.traderName = traderName;
        this.traderOwnerName = traderOwnerName;
        this.tradeType = tradeType;

        this.itemInputs = itemInputs;
        this.itemOutputs = itemOutputs;

        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public String getOwnerName() {return traderOwnerName;}

    public String getTradeType() {
        return tradeType;
    }

    public List<ItemStack> getItemInputs()
    {
        return itemInputs;
    }

    public List<ItemStack> getItemOutputs()
    {
        return itemOutputs;
    }

    public List<FluidStack> getFluidInputs()
    {
        return fluidInputs;
    }

    public List<FluidStack> getFluidOutputs()
    {
        return fluidOutputs;
    }
}