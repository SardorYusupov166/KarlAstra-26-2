/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.modules.movement.ArmAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin {
    @Inject(method={"getCurrentSwingDuration"}, at=@At(value="RETURN"), cancellable=true)
    private void astra$swingDuration(CallbackInfoReturnable<Integer> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && (Object)this == mc.player) {
            cir.setReturnValue(ArmAnimation.swingDuration(cir.getReturnValueI()));
        }
    }
}

