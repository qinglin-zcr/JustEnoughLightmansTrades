package com.qinglin.just_enough_lightmans_trades.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class JELTRecipeCategory implements IRecipeCategory<JELTTrade> {

    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public JELTRecipeCategory(IGuiHelper guiHelper)
    {
        this.icon =
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        new ItemStack(Items.EMERALD)
                );
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<JELTTrade> getRecipeType()
    {
        return JELTRecipeTypes.TRADES;
    }

    @Override
    public Component getTitle()
    {
        return Component.translatable("jei.just_enough_lightmans_trades.trades");
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public int getWidth() {
        return 150; // 稍微加宽一点（原150），给中间的文本、箭头以及两边的多物品矩阵留出空间
    }

    @Override
    public int getHeight() {
        return 60; // 稍微加高一点（原54），方便顶部容纳 Trader Name，下方留出两行物品的空间
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JELTTrade recipe, IFocusGroup focuses) {
        int xStart = 5;
        int yStart = 18;

        int perRow = 3;
        int index = 0;

        for (ItemStack input : recipe.getInputs())
        {
            int x = xStart + (index % perRow) * 18;
            int y = yStart + (index / perRow) * 18;

            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .addItemStack(input);

            index++;
        }

        int outStartX = 95;
        int outStartY = 18;

        int outIndex = 0;

        for (ItemStack output : recipe.getOutputs())
        {
            int x = outStartX + (outIndex % perRow) * 18;
            int y = outStartY + (outIndex / perRow) * 18;

            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(output);

            outIndex++;
        }

    }


    @Override
    public void draw(
            JELTTrade recipe,
            IRecipeSlotsView slotsView,
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY)
    {
        String text =
                recipe.getTraderName();

        guiGraphics.drawString(
                Minecraft.getInstance().font,
                text,
                5,
                2,
                0x404040,
                false
        );

        arrow.draw(guiGraphics, 70, 20);
    }

}
