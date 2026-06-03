package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.JustEnoughLightmansTrades;
import com.qinglin.just_enough_lightmans_trades.trades.TradeManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;

@mezz.jei.api.JeiPlugin
public class JELTJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID =
            new ResourceLocation(
                    JustEnoughLightmansTrades.MOD_ID,
                    "jei_plugin"
            );

    @Override
    public ResourceLocation getPluginUid()
    {
        return UID;
    }

    @Override
    public void registerRecipes(
            IRecipeRegistration registration)
    {
        registration.addRecipes(
                JELTRecipeTypes.TRADES,
                TradeManager.getTrades()
        );

        JustEnoughLightmansTrades.LOGGER.info(
                "Registered {} JELT trades",
                TradeManager.getTrades().size()
        );
    }

    @Override
    public void registerCategories(
            IRecipeCategoryRegistration registration)
    {
        IGuiHelper guiHelper =
                registration.getJeiHelpers()
                        .getGuiHelper();

        registration.addRecipeCategories(
                new JELTRecipeCategory(guiHelper)
        );
    }
}