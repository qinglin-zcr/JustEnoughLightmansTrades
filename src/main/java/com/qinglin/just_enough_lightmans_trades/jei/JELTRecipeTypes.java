package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.JustEnoughLightmansTrades;
import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import mezz.jei.api.recipe.RecipeType;

public final class JELTRecipeTypes {

    private JELTRecipeTypes() {}

    public static final RecipeType<JELTTrade> TRADES =
            RecipeType.create(
                    JustEnoughLightmansTrades.MOD_ID,
                    "lightman_trades",
                    JELTTrade.class
            );

}