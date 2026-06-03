package com.qinglin.just_enough_lightmans_trades.trades;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class JELTTrade {

    private final String traderId;

    private final String traderName;

    private final String tradeType;

    private final List<ItemStack> inputs;

    private final List<ItemStack> outputs;

    public JELTTrade(
            String traderId,
            String traderName,
            String tradeType,
            List<ItemStack> inputs,
            List<ItemStack> outputs)
    {
        this.traderId = traderId;
        this.traderName = traderName;
        this.tradeType = tradeType;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public String getTraderId() {
        return traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public String getTradeType() {
        return tradeType;
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }
}