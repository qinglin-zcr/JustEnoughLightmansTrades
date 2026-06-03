package com.qinglin.just_enough_lightmans_trades;

import com.mojang.logging.LogUtils;
import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import com.qinglin.just_enough_lightmans_trades.trades.JELTTradeConverter;
import com.qinglin.just_enough_lightmans_trades.trades.PersistentTraderFile;
import com.qinglin.just_enough_lightmans_trades.trades.TraderEntry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;

@Mod(JustEnoughLightmansTrades.MOD_ID)
public class JustEnoughLightmansTrades {

    public static final String MOD_ID = "just_enough_lightmans_trades";

    public static final Logger LOGGER =
            LogUtils.getLogger();

    public JustEnoughLightmansTrades()
    {
        Path gameDir = FMLPaths.GAMEDIR.get();

        LOGGER.info("GameDir = {}", gameDir);

        PersistentTraderFile data =
                PersistentTraderLoader.load(gameDir);

        if(data == null)
        {
            LOGGER.info("PersistentTraders.json not found");
            return;
        }

        List<JELTTrade> trades =
                JELTTradeConverter.convertAll(data);

        LOGGER.info(
                "Loaded {} trades",
                trades.size()
        );

        for(JELTTrade trade : trades)
        {
            LOGGER.info(
                    "Trader={} Type={} Item={} x{} PriceEntries={}",
                    trade.getTraderName(),
                    trade.getTradeType(),
                    trade.getItem().getItem(),
                    trade.getItem().getCount(),
                    trade.getPrice().size()
            );
        }
    }
}