/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.modules.visual.WorldVisuals;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Level.class})
public abstract class WorldMixin {
    @Inject(method={"getRainLevel", "getThunderLevel"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$clearWeather(float delta, CallbackInfoReturnable<Float> cir) {
        Level self = (Level)(Object)this;
        if (self.isClientSide() && WorldVisuals.clearWeather()) {
            cir.setReturnValue(Float.valueOf(0.0f));
        }
    }
}

