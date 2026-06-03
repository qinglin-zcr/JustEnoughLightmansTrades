package com.qinglin.just_enough_lightmans_trades;

import com.mojang.logging.LogUtils;
import com.qinglin.just_enough_lightmans_trades.trades.PersistentTraderFile;
import com.qinglin.just_enough_lightmans_trades.trades.TraderEntry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(JustEnoughLightmansTrades.MOD_ID)
public class JustEnoughLightmansTrades {

    public static final String MOD_ID = "jelc_trades";

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

        LOGGER.info(
                "Loaded {} traders",
                data.Traders.size()
        );

        for(TraderEntry trader : data.Traders)
        {
            LOGGER.info(
                    "Trader: {} ({})",
                    trader.Name,
                    trader.ID
            );

            if(trader.Trades != null)
            {
                LOGGER.info(
                        "Trades: {}",
                        trader.Trades.size()
                );
            }
        }
    }
}