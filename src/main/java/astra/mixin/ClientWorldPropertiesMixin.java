/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel$ClientLevelData
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.modules.visual.WorldVisuals;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ClientLevel.ClientLevelData.class})
public abstract class ClientWorldPropertiesMixin {
    @Inject(method={"getDayTime"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$timeOfDay(CallbackInfoReturnable<Long> cir) {
        long time = WorldVisuals.overrideTime();
        if (time >= 0L) {
            cir.setReturnValue((Object)time);
        }
    }
}

