package com.qinglin.just_enough_lightmans_trades.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qinglin.just_enough_lightmans_trades.trades.JELTTrade;
import com.qinglin.just_enough_lightmans_trades.trades.TradeManager;
import io.github.lightman314.lightmanscurrency.api.money.coins.CoinAPI;
import io.github.lightman314.lightmanscurrency.common.core.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
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

import java.util.List;

public class JELTRecipeCategory implements IRecipeCategory<JELTTrade> {

    private final IDrawable icon;

    private final IDrawableStatic arrow;
    private final int priceSlotCount;

    public JELTRecipeCategory(IGuiHelper guiHelper){
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.TRADING_CORE.get())
        );

        this.arrow = guiHelper.getRecipeArrow();
        // Reserve room for change, even when the base price is a single denomination.
        int slots = 6;
        for(JELTTrade recipe : TradeManager.getTrades()) {
            if(!"BARTER".equals(recipe.getTradeType()))
                slots = Math.max(slots, LiveTradePrice.getBasePrice(recipe).size());
        }
        for(var chain : CoinAPI.getApi().AllChainData())
            slots = Math.max(slots, chain.getCoreChain().size());
        this.priceSlotCount = slots;
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
    public int getHeight() {return Math.max(60, 24 + ((priceSlotCount + 2) / 3) * 18);}

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,JELTTrade recipe,IFocusGroup focuses) {
        final int lx=4,rx=90,sy=24;int idx=0;
        if("SALE".equals(recipe.getTradeType())) {
            addPriceSlots(builder, recipe, RecipeIngredientRole.INPUT, lx, sy);
        } else for(ItemStack stack:recipe.getItemInputs()){
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
        if("PURCHASE".equals(recipe.getTradeType())) {
            addPriceSlots(builder, recipe, RecipeIngredientRole.OUTPUT, rx, sy);
        } else for(ItemStack stack:recipe.getItemOutputs()){
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

    private void addPriceSlots(IRecipeLayoutBuilder builder, JELTTrade recipe,
                              RecipeIngredientRole role, int x, int y) {
        List<ItemStack> basePrice = LiveTradePrice.getBasePrice(recipe);
        for(int i = 0; i < priceSlotCount; i++) {
            var slot = builder.addSlot(role, x + (i % 3) * 18, y + (i / 3) * 18)
                    .setSlotName("price_" + i);
            if(i < basePrice.size())
                slot.addItemStack(basePrice.get(i));
        }
    }

    @Override
    public void onDisplayedIngredientsUpdate(JELTTrade recipe, List<IRecipeSlotDrawable> slots,
                                             IFocusGroup focuses) {
        if("BARTER".equals(recipe.getTradeType()))
            return;
        List<ItemStack> price = LiveTradePrice.getPrice(recipe);
        for(IRecipeSlotDrawable slot : slots) {
            String name = slot.getSlotName().orElse("");
            if(!name.startsWith("price_"))
                continue;
            int index = Integer.parseInt(name.substring(6));
            // Empty overrides clear denominations that disappeared after another fluctuation.
            slot.createDisplayOverrides().addItemStacks(index < price.size()
                    ? List.of(price.get(index)) : List.of());
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

        if(recipe.getItemInputs().isEmpty()&&recipe.getFluidInputs().isEmpty()
                &&recipe.getQuantity()!=-1){
            String tx = recipe.getQuantity() + "FE";
            int w = mc.font.width(tx);
            graphics.drawString(
                    mc.font,tx,31-(w/2),40-mc.font.lineHeight/2,
                    0xFFFFFF,true
            );
        }
        if(recipe.getItemOutputs().isEmpty()&&recipe.getFluidOutputs().isEmpty()
                &&recipe.getQuantity()!=-1){
            String tx = recipe.getQuantity() + " FE";
            int w = mc.font.width(tx);
            graphics.drawString(
                    mc.font,tx,117-(w/2),40-mc.font.lineHeight/2,
                    0xFFFFFF,true
            );
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
        final float scale=0.625F;
        final float inv=1.6F;
        pose.scale(scale, scale, 1.0F);
        int textWidth = mc.font.width(text);
        graphics.drawString(
                mc.font,
                text,
                Math.round(x * inv + 16 * inv - textWidth),
                Math.round(y * inv + 12 * inv),
                0xFFFFFF,
                true
        );
        pose.popPose();
    }
}
