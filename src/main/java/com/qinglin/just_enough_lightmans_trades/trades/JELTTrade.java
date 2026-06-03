package com.qinglin.just_enough_lightmans_trades.trades;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class JELTTrade {

    private final String traderId;
    private final String traderName;

    private final String tradeType;

    private final ItemStack item;

    private final List<ItemStack> price;

    public JELTTrade(
            String traderId,
            String traderName,
            String tradeType,
            ItemStack item,
            List<ItemStack> price)
    {
        this.traderId = traderId;
        this.traderName = traderName;
        this.tradeType = tradeType;
        this.item = item;
        this.price = price;
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

    public ItemStack getItem() {
        return item;
    }

    public List<ItemStack> getPrice() {
        return price;
    }
}