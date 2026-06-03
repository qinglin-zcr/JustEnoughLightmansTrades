package com.qinglin.just_enough_lightmans_trades.trades;

import java.util.ArrayList;
import java.util.List;

public final class TradeManager {

    private static List<JELTTrade> trades =
            new ArrayList<>();

    private TradeManager() {}

    public static void setTrades(List<JELTTrade> newTrades)
    {
        trades = new ArrayList<>(newTrades);
    }

    public static List<JELTTrade> getTrades()
    {
        return trades;
    }
}