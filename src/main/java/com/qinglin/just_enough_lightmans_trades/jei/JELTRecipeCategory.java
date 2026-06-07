package com.qinglin.just_enough_lightmans_trades.jei;

import com.mojang.blaze3d.vertex.PoseStack;
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
                    .addFluidStack(fluid.getFluid(),fluid.getAmount(),fluid.getTag());
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
                    .addFluidStack(fluid.getFluid(),fluid.getAmount(),fluid.getTag());
            idx++;
        }
    }

    @Override
    public void draw(JELTTrade recipe, IRecipeSlotsView slotsView, GuiGraphics graphics,
            double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        final int lx=4,rx=90,sy=24;int idx=recipe.getItemInputs().size();
        for(FluidStack fluid:recipe.getFluidInputs()){
            int x=lx+(idx%3)*18,y=sy+(idx/3)*18;
            drawFluidAmount(graphics, fluid.getAmount(), x, y);
            idx++;
        }
        idx=recipe.getItemOutputs().size();
        for(FluidStack fluid:recipe.getFluidOutputs()){
            int x=rx+(idx%3)*18,y=sy+(idx/3)*18;
            drawFluidAmount(graphics, fluid.getAmount(), x, y);
            idx++;
        }

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

    private void drawFluidAmount(GuiGraphics graphics, int amount, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        String text;
        if(amount<1000)text=amount + "mB";
        else {
            double t= (double) amount /1000;
            text = String.format("%.2fB", t);
            if(text.endsWith(".00B")){text=text.replace(".00B","B");}
            else if(text.endsWith("0B")) {
                text = text.substring(0, text.length() - 2) + "B";
            }
        }
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 200);
        pose.scale(0.5F, 0.5F, 1.0F);
        int textWidth = mc.font.width(text);
        graphics.drawString(
                mc.font,
                text,
                x * 2 + 32 - textWidth,
                y * 2 + 24,
                0xFFFFFF,
                true
        );
        pose.popPose();
    }
}