/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.gui.screens.ClickGuiScreen;
import astra.gui.style.NativeScreenLayouts;
import astra.gui.vanilla.MenuStyle;
import astra.theme.ThemeManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Screen.class})
public abstract class ScreenBackgroundMixin {
    @Shadow
    public int width;
    @Shadow
    public int height;

    @Inject(method={"renderPanorama"}, at=@At(value="TAIL"))
    private void astra$menuBackground(GuiGraphicsExtractor ctx, float delta, CallbackInfo ci) {
        if ((Object)this instanceof ClickGuiScreen) {
            return;
        }
        MenuStyle.drawMenuBackground(ctx, this.width, this.height);
    }

    @Inject(method={"renderBackground"}, at=@At(value="TAIL"))
    private void astra$pauseBackdrop(GuiGraphicsExtractor ctx, int mx, int my, float delta, CallbackInfo ci) {
        if ((Object)this instanceof PauseScreen) {
            Screen screen = (Screen)(Object)this;
            NativeScreenLayouts.backdrop(screen, ctx);
            float aw = Math.min((float)(Object)this.width * 0.26f, (float)(Object)this.height * 0.5f);
            NativeScreenLayouts.art(screen, ctx, ThemeManager.get(), (float)(Object)this.width - aw - 6.0f, this.height, aw, (float)(Object)this.height * 0.82f, 1.0f);
        }
    }
}

