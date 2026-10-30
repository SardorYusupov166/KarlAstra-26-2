/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.client.input.MouseButtonInfo
 *  net.minecraft.world.phys.HitResult$Type
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package astra.mixin;

import astra.module.ModuleManager;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.movement.ZoomModule;
import astra.utils.ClickTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={MouseHandler.class})
public abstract class MouseMixin {
    @Shadow
    @Final
    private Minecraft field_1779;

    @Inject(method={"method_1601"}, at={@At(value="HEAD")})
    private void astra$countClicks(long window, MouseButtonInfo input, int action, CallbackInfo ci) {
        int button = input.comp_4801();
        if (action == 1 && this.field_1779.field_1755 == null && window == this.field_1779.method_22683().method_4490()) {
            SoundsModule sounds;
            ClickTracker.onClick(button);
            if (button == 0 && this.field_1779.field_1724 != null && this.field_1779.field_1765 != null && this.field_1779.field_1765.method_17783() == HitResult.Type.field_1333 && (sounds = SoundsModule.get()) != null) {
                sounds.onMiss();
            }
        }
    }

    @Inject(method={"method_1598"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$zoomScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (this.field_1779.field_1755 != null || vertical == 0.0) {
            return;
        }
        ZoomModule zoom = ModuleManager.get(ZoomModule.class);
        if (zoom != null && zoom.onScroll(vertical)) {
            ci.cancel();
        }
    }

    @ModifyArgs(method={"method_1606"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/player/LocalPlayer;method_5872(DD)V")})
    private void astra$zoomSensitivity(Args args) {
        ZoomModule zoom = ModuleManager.get(ZoomModule.class);
        if (zoom == null) {
            return;
        }
        double k = zoom.sensitivityFactor();
        if (k == 1.0) {
            return;
        }
        args.set(0, (Object)((Double)args.get(0) * k));
        args.set(1, (Object)((Double)args.get(1) * k));
    }
}

