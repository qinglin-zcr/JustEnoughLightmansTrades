package com.qinglin.just_enough_lightmans_trades.trades;

public class TradeEntry {

    public String TradeType;

    // 已有字段
    public ItemEntry SellItem;
    public ItemEntry BarterItem;

    // 允许 Gson 自动映射
    public ItemEntry SellItem2;
    public ItemEntry SellItem3;
    public ItemEntry SellItem4;

    public ItemEntry BarterItem2;
    public ItemEntry BarterItem3;
    public ItemEntry BarterItem4;

    public PriceEntry Price;
}