/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.module.ModuleManager;
import astra.module.modules.performance.BenchmarkModule;
import astra.utils.FrameStats;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public abstract class MinecraftClientMixin {
    @Inject(method={"runTick"}, at=@At(value="HEAD"))
    private void astra$frame(boolean tick, CallbackInfo ci) {
        FrameStats.onFrame();
        BenchmarkModule bench = ModuleManager.get(BenchmarkModule.class);
        if (bench != null) {
            bench.onFrame();
        }
    }
}

