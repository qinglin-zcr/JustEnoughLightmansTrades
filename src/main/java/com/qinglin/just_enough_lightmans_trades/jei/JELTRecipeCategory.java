package com.qinglin.just_enough_lightmans_trades.jei;

import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import io.github.lightman314.lightmanscurrency.common.core.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class JELTRecipeCategory implements IRecipeCategory<JELTTrade> {

    private final IDrawable icon;

    private final IDrawableStatic arrow;

    public JELTRecipeCategory(IGuiHelper guiHelper){
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.TRADING_CORE.get())
        );

        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<JELTTrade> getRecipeType() {return JELTRecipeTypes.TRADES;}

    @Override
    public Component getTitle() {
        return Component.translatable(
                "jei.just_enough_lightmans_trades.trades");
    }

    @Override
    public IDrawable getIcon() {return icon;}

    @Override
    public int getWidth() {return 150;}

    @Override
    public int getHeight() {return 60;}

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,JELTTrade recipe,IFocusGroup focuses) {
        final int lx=4,rx=90,sy=24;int idx=0;
        for(ItemStack stack:recipe.getItemInputs()){
            int x=lx+(idx%3)*18,y=sy+(idx/3)*18;
            builder.addSlot(RecipeIngredientRole.INPUT,x,y).addItemStack(stack);
            idx++;
        }
        for(FluidStack fluid:recipe.getFluidInputs()){
            int x=lx+(idx%3)*18,y=sy+(idx/3)*18;
            builder.addSlot(RecipeIngredientRole.INPUT,x,y)
                    .addFluidStack(fluid.getFluid(),fluid.getAmount(),fluid.getTag())
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.literal(fluid.getAmount() + " mB").withStyle(net.minecraft.ChatFormatting.GRAY));
                    });
            idx++;
        }
        idx=0;
        for(ItemStack stack:recipe.getItemOutputs()){
            int x=rx+(idx%3)*18,y=sy+(idx/3)*18;
            builder.addSlot(RecipeIngredientRole.OUTPUT,x,y).addItemStack(stack);
            idx++;
        }
        for(FluidStack fluid:recipe.getFluidOutputs()){
            int x=rx+(idx%3)*18,y=sy+(idx/3)*18;
            builder.addSlot(RecipeIngredientRole.OUTPUT,x,y)
                    .addFluidStack(fluid.getFluid(),fluid.getAmount(),fluid.getTag())
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.literal(fluid.getAmount()+" mB").withStyle(net.minecraft.ChatFormatting.GRAY));
                    });
            idx++;
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

        arrow.draw(graphics, 64, 32);
    }
}