package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import io.github.lightman314.lightmanscurrency.common.core.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;

public class JELTRecipeCategory
        implements IRecipeCategory<JELTTrade>
{

    private final IDrawable icon;

    private final IDrawableStatic arrow;

    public JELTRecipeCategory(
            IGuiHelper guiHelper)
    {
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.PORTABLE_TERMINAL.get())
        );

        this.arrow =
                guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<JELTTrade> getRecipeType()
    {
        return JELTRecipeTypes.TRADES;
    }

    @Override
    public Component getTitle()
    {
        return Component.translatable(
                "jei.just_enough_lightmans_trades.trades"
        );
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
        return 60;
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            JELTTrade recipe,
            IFocusGroup focuses)
    {

        final int leftX = 4;
        final int rightX = 112;

        final int startY = 28;

        final int perRow = 3;

        int index = 0;

        /*
         * Item Inputs
         */
        for(ItemStack stack :
                recipe.getItemInputs())
        {
            int x =
                    leftX
                            + (index % perRow) * 18;

            int y =
                    startY
                            + (index / perRow) * 18;

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            x,
                            y
                    )
                    .addItemStack(stack);

            index++;
        }

        /*
         * Fluid Inputs
         */
        for(FluidStack fluid :
                recipe.getFluidInputs())
        {
            int x =
                    leftX
                            + (index % perRow) * 18;

            int y =
                    startY
                            + (index / perRow) * 18;

            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            x,
                            y
                    )
                    .addIngredient(
                            ForgeTypes.FLUID_STACK,
                            fluid
                    );

            index++;
        }

        int outIndex = 0;

        /*
         * Item Outputs
         */
        for(ItemStack stack :
                recipe.getItemOutputs())
        {
            int x =
                    rightX
                            + (outIndex % perRow) * 18;

            int y =
                    startY
                            + (outIndex / perRow) * 18;

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            x,
                            y
                    )
                    .addItemStack(stack);

            outIndex++;
        }

        /*
         * Fluid Outputs
         */
        for(FluidStack fluid :
                recipe.getFluidOutputs())
        {
            int x =
                    rightX
                            + (outIndex % perRow) * 18;

            int y =
                    startY
                            + (outIndex / perRow) * 18;

            builder.addSlot(
                            RecipeIngredientRole.OUTPUT,
                            x,
                            y
                    )
                    .addIngredient(
                            ForgeTypes.FLUID_STACK,
                            fluid
                    );

            outIndex++;
        }
    }

    @Override
    public void draw(
            JELTTrade recipe,
            IRecipeSlotsView slotsView,
            GuiGraphics graphics,
            double mouseX,
            double mouseY)
    {

        Minecraft mc =
                Minecraft.getInstance();

        graphics.drawString(
                mc.font,
                recipe.getTraderName(),
                4,
                2,
                0x404040,
                false
        );

        graphics.drawString(
                mc.font,
                recipe.getOwnerName(),
                4,
                12,
                0x808080,
                false
        );

        arrow.draw(
                graphics,
                68,
                36
        );
    }
}