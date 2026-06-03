package com.qinglin.just_enough_lightmans_trades;

import com.mojang.logging.LogUtils;
import com.qinglin.just_enough_lightmans_trades.trades.*;
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
        TradeManager.setTrades(trades);

        LOGGER.info(
                "Loaded {} trades",
                trades.size()
        );

        for(JELTTrade trade : trades)
        {
            LOGGER.info(
                    "{} {} inputs={} outputs={}",
                    trade.getTraderName(),
                    trade.getTradeType(),
                    trade.getInputs().size(),
                    trade.getOutputs().size()
            );
        }
    }
}