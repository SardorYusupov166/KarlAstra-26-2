/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.item.ItemStackRenderState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.render.TintedItemState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemStackRenderState.class})
public abstract class ItemRenderStateMixin
implements TintedItemState {
    @Unique
    private int astra$tint;

    @Override
    public int astra$getTint() {
        return this.astra$tint;
    }

    @Override
    public void astra$setTint(int tint) {
        this.astra$tint = tint;
    }

    @Inject(method={"method_65605"}, at={@At(value="HEAD")})
    private void astra$clearTint(CallbackInfo ci) {
        this.astra$tint = 0;
    }

    @Inject(method={"method_65604"}, at={@At(value="HEAD")})
    private void astra$beginTint(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, int outline, CallbackInfo ci) {
        TintedItemState.CURRENT[0] = this.astra$tint;
    }

    @Inject(method={"method_65604"}, at={@At(value="RETURN")})
    private void astra$endTint(PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, int outline, CallbackInfo ci) {
        TintedItemState.CURRENT[0] = 0;
    }
}

