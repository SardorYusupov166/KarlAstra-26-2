/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.render.state.GuiRenderState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.render.Render2D;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiRenderState.class})
public abstract class GuiRenderStateMixin {
    @Inject(method={"submitItem", "submitText", "submitPicturesInPictureState", "submitGuiElement", "submitBlitToCurrentLayer", "submitGlyphToCurrentLayer", "nextStratum", "up", "blurBeforeThisStratum"}, at={@At(value="HEAD")})
    private void astra$flushShapes(CallbackInfo ci) {
        if (!Render2D.flushing) {
            Render2D.flushBatch();
        }
    }

    @Inject(method={"reset"}, at={@At(value="HEAD")})
    private void astra$newFrame(CallbackInfo ci) {
        Render2D.dropBatch();
    }
}

