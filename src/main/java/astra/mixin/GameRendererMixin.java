/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  net.minecraft.client.Camera
 *  net.minecraft.client.renderer.GameRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.ModuleManager;
import astra.module.modules.movement.SprintFov;
import astra.module.modules.movement.ViewBobbing;
import astra.module.modules.movement.ZoomModule;
import astra.module.modules.pvp.PvpTweaks;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={GameRenderer.class})
public abstract class GameRendererMixin {
    @Inject(method={"getFov"}, at={@At(value="RETURN")}, cancellable=true)
    private void astra$fov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        float factor;
        ZoomModule zoom;
        if (!changingFov) {
            return;
        }
        float fov = cir.getReturnValueF();
        SprintFov sprint = ModuleManager.get(SprintFov.class);
        if (sprint != null && sprint.isEnabled()) {
            fov += sprint.extraDegrees();
        }
        if ((zoom = ModuleManager.get(ZoomModule.class)) != null && zoom.isEnabled() && (factor = zoom.currentFactor()) != 1.0f) {
            fov /= factor;
        }
        cir.setReturnValue((Object)Float.valueOf(Math.max(1.0f, Math.min(170.0f, fov))));
    }

    @ModifyExpressionValue(method={"bobView"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/entity/ClientAvatarState;getInterpolatedBob(F)F")})
    private float astra$bobIntensity(float stride) {
        return stride * ViewBobbing.factor();
    }

    @ModifyExpressionValue(method={"bobHurt"}, at={@At(value="INVOKE", target="Ljava/lang/Double;doubleValue()D")})
    private double astra$hurtTilt(double strength) {
        return strength * (double)PvpTweaks.hurtTiltFactor();
    }
}

