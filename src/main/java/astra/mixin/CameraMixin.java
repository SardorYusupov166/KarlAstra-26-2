package astra.mixin;

import astra.module.ModuleManager;
import astra.module.modules.movement.SprintFov;
import astra.module.modules.movement.ZoomModule;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void astra$fov(float partialTick, CallbackInfoReturnable<Float> cir) {
        float fov = cir.getReturnValueF();
        SprintFov sprint = ModuleManager.get(SprintFov.class);
        if (sprint != null && sprint.isEnabled()) fov += sprint.extraDegrees();
        ZoomModule zoom = ModuleManager.get(ZoomModule.class);
        if (zoom != null && zoom.isEnabled()) fov /= zoom.currentFactor();
        cir.setReturnValue(Math.max(1, Math.min(170, fov)));
    }
}
