/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.ScreenEffectRenderer
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.module.modules.pvp.PvpTweaks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ScreenEffectRenderer.class})
public abstract class InGameOverlayRendererMixin {
    @Unique
    private boolean astra$totemScaled;
    @Unique
    private static boolean astra$fireMoved;

    @Inject(method={"method_70939"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$totemHead(PoseStack matrices, float tickDelta, SubmitNodeCollector queue, CallbackInfo ci) {
        if (PvpTweaks.hideTotemAnimation()) {
            ci.cancel();
            return;
        }
        float s = PvpTweaks.totemAnimationScale();
        boolean bl = this.astra$totemScaled = s != 1.0f;
        if (this.astra$totemScaled) {
            matrices.method_22903();
            matrices.method_22905(s, s, 1.0f);
        }
    }

    @Inject(method={"method_70939"}, at={@At(value="RETURN")})
    private void astra$totemTail(PoseStack matrices, float tickDelta, SubmitNodeCollector queue, CallbackInfo ci) {
        if (this.astra$totemScaled) {
            matrices.method_22909();
            this.astra$totemScaled = false;
        }
    }

    @Inject(method={"method_23070"}, at={@At(value="HEAD")}, cancellable=true)
    private static void astra$fireHead(PoseStack matrices, MultiBufferSource consumers, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (PvpTweaks.hideFire()) {
            ci.cancel();
            return;
        }
        float offset = PvpTweaks.fireOffset();
        boolean bl = astra$fireMoved = offset > 0.0f;
        if (astra$fireMoved) {
            matrices.method_22903();
            matrices.method_46416(0.0f, -offset, 0.0f);
        }
    }

    @Inject(method={"method_23070"}, at={@At(value="RETURN")})
    private static void astra$fireTail(PoseStack matrices, MultiBufferSource consumers, TextureAtlasSprite sprite, CallbackInfo ci) {
        if (astra$fireMoved) {
            matrices.method_22909();
            astra$fireMoved = false;
        }
    }

    @Inject(method={"method_23068"}, at={@At(value="HEAD")}, cancellable=true)
    private static void astra$inWall(TextureAtlasSprite sprite, PoseStack matrices, MultiBufferSource consumers, CallbackInfo ci) {
        if (PvpTweaks.hideInWall()) {
            ci.cancel();
        }
    }

    @Inject(method={"method_23069"}, at={@At(value="HEAD")}, cancellable=true)
    private static void astra$underwater(Minecraft client, PoseStack matrices, MultiBufferSource consumers, CallbackInfo ci) {
        if (PvpTweaks.hideUnderwater()) {
            ci.cancel();
        }
    }
}

