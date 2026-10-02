package com.qinglin.just_enough_lightmans_trades.mixin;

import com.qinglin.just_enough_lightmans_trades.jei.LiveTradePrice;
import io.github.lightman314.lightmanscurrency.api.network.LazyPacketData;
import io.github.lightman314.lightmanscurrency.common.data.types.TraderDataCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TraderDataCache.class, remap = false)
public abstract class TraderDataCacheMixin {
    @Inject(method = "parseSyncPacket", at = @At("TAIL"), remap = false)
    private void jelt$afterTraderSync(LazyPacketData data, CallbackInfo ci) {
        LiveTradePrice.onTraderSync((TraderDataCache) (Object) this, data);
    }
}
