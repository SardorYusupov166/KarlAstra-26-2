/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlainTextButton
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.gui.style.NativeScreenLayouts;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlainTextButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlainTextButton.class})
public class NativeCopyrightMixin {
    @Inject(method={"extractContents"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$text(GuiGraphicsExtractor ctx, int mx, int my, float delta, CallbackInfo ci) {
        if (NativeScreenLayouts.textWidget(this, ctx)) {
            ci.cancel();
        }
    }
}

