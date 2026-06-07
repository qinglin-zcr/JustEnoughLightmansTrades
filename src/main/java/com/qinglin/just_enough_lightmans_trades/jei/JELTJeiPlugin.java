package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.JustEnoughLightmansTrades;
import com.qinglin.just_enough_lightmans_trades.trades.TradeManager;
import io.github.lightman314.lightmanscurrency.common.core.ModBlocks;
import io.github.lightman314.lightmanscurrency.common.core.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.world.item.ItemStack;

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

    @Override
    public void registerRecipeCatalysts(
            IRecipeCatalystRegistration registration)
    {


        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.TERMINAL.get()),
                JELTRecipeTypes.TRADES
        );
        registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.GEM_TERMINAL.get()),
                JELTRecipeTypes.TRADES
        );
        registration.addRecipeCatalyst(
                new ItemStack(ModItems.PORTABLE_TERMINAL.get()),
                JELTRecipeTypes.TRADES
        );
        registration.addRecipeCatalyst(
                new ItemStack(ModItems.PORTABLE_GEM_TERMINAL.get()),
                JELTRecipeTypes.TRADES
        );

    }
}