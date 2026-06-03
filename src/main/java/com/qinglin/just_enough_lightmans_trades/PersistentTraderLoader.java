package com.qinglin.just_enough_lightmans_trades;

import com.google.gson.Gson;
import com.qinglin.just_enough_lightmans_trades.trades.PersistentTraderFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersistentTraderLoader {

    private static final Gson GSON = new Gson();

    public static PersistentTraderFile load(Path gameDir)
    {
        Path file = gameDir
                .resolve("config")
                .resolve("lightmanscurrency")
                .resolve("PersistentTraders.json");

        if(!Files.exists(file))
            return null;

        try
        {
            String json = Files.readString(file);

            return GSON.fromJson(
                    json,
                    PersistentTraderFile.class
            );
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}