package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class JELTRecipeCategory implements IRecipeCategory<JELTTrade> {

    private final IDrawable icon;

    public JELTRecipeCategory(IGuiHelper guiHelper)
    {
        this.icon =
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(Items.EMERALD)
                );
    }

    @Override
    public RecipeType<JELTTrade> getRecipeType()
    {
        return JELTRecipeTypes.TRADES;
    }

    @Override
    public Component getTitle()
    {
        return Component.literal("Lightman's Trades");
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public int getWidth()
    {
        return 150;
    }

    @Override
    public int getHeight()
    {
        return 54;
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JELTTrade recipe,
            IFocusGroup focuses)
    {
    }
}