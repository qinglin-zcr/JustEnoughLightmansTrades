package com.qinglin.just_enough_lightmans_trades.trades;

import com.qinglin.just_enough_lightmans_trades.JustEnoughLightmansTrades;

import java.util.Collections;
import java.util.List;

public class TradeManager {

    private static PersistentTraderFile rawData;

    private static List<JELTTrade> cachedTrades;

    public static void setRawData(PersistentTraderFile data)
    {
        rawData = data;
        cachedTrades = null;
    }

    public static List<JELTTrade> getTrades()
    {
        if(cachedTrades == null)
        {
            JustEnoughLightmansTrades.LOGGER.info(
                    "Converting trader data..."
            );

            cachedTrades =
                    JELTTradeConverter.convertAll(rawData);

            JustEnoughLightmansTrades.LOGGER.info(
                    "Converted {} trades",
                    cachedTrades.size()
            );
        }

        return cachedTrades;
    }

}