/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.world.entity.Entity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.modules.pvp.PvpTweaks;
import astra.module.modules.pvp.TargetEffects;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ParticleEngine.class})
public abstract class ParticleManagerMixin {
    private static boolean astra$isHitParticle(ParticleOptions effect) {
        ParticleType type = effect.method_10295();
        return type == ParticleTypes.field_11205 || type == ParticleTypes.field_11208 || type == ParticleTypes.field_11227 || type == ParticleTypes.field_11209;
    }

    @Inject(method={"method_3056(Lnet/minecraft/class_2394;DDDDDD)Lnet/minecraft/class_703;"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$hideHitParticle(ParticleOptions effect, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<Particle> cir) {
        if (ParticleManagerMixin.astra$isHitParticle(effect) && TargetEffects.hidesVanillaHitParticles() || PvpTweaks.hideParticle(effect, x, y, z)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method={"method_3061(Lnet/minecraft/class_1297;Lnet/minecraft/class_2394;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$hideHitEmitter(Entity entity, ParticleOptions effect, CallbackInfo ci) {
        if (ParticleManagerMixin.astra$isHitParticle(effect) && TargetEffects.hidesVanillaHitParticles()) {
            ci.cancel();
        } else if (PvpTweaks.hideParticle(effect, Double.NaN, 0.0, 0.0)) {
            ci.cancel();
        }
    }

    @Inject(method={"method_3051(Lnet/minecraft/class_1297;Lnet/minecraft/class_2394;I)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$hideHitEmitterTimed(Entity entity, ParticleOptions effect, int maxAge, CallbackInfo ci) {
        if (ParticleManagerMixin.astra$isHitParticle(effect) && TargetEffects.hidesVanillaHitParticles()) {
            ci.cancel();
        } else if (PvpTweaks.hideParticle(effect, Double.NaN, 0.0, 0.0)) {
            ci.cancel();
        }
    }
}

