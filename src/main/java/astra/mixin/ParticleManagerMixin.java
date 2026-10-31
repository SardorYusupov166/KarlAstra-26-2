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
        ParticleType type = effect.getType();
        return type == ParticleTypes.CRIT || type == ParticleTypes.ENCHANTED_HIT || type == ParticleTypes.SWEEP_ATTACK || type == ParticleTypes.DAMAGE_INDICATOR;
    }

    @Inject(method={"createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$hideHitParticle(ParticleOptions effect, double x, double y, double z, double vx, double vy, double vz, CallbackInfoReturnable<Particle> cir) {
        if (ParticleManagerMixin.astra$isHitParticle(effect) && TargetEffects.hidesVanillaHitParticles() || PvpTweaks.hideParticle(effect, x, y, z)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method={"createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;)V"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$hideHitEmitter(Entity entity, ParticleOptions effect, CallbackInfo ci) {
        if (ParticleManagerMixin.astra$isHitParticle(effect) && TargetEffects.hidesVanillaHitParticles()) {
            ci.cancel();
        } else if (PvpTweaks.hideParticle(effect, Double.NaN, 0.0, 0.0)) {
            ci.cancel();
        }
    }

    @Inject(method={"createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$hideHitEmitterTimed(Entity entity, ParticleOptions effect, int maxAge, CallbackInfo ci) {
        if (ParticleManagerMixin.astra$isHitParticle(effect) && TargetEffects.hidesVanillaHitParticles()) {
            ci.cancel();
        } else if (PvpTweaks.hideParticle(effect, Double.NaN, 0.0, 0.0)) {
            ci.cancel();
        }
    }
}

