/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
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
import net.minecraft.client.gui.GuiGraphics;
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
    public int field_22789;
    @Shadow
    public int field_22790;

    @Inject(method={"method_57728"}, at={@At(value="TAIL")})
    private void astra$menuBackground(GuiGraphics ctx, float delta, CallbackInfo ci) {
        if (this instanceof ClickGuiScreen) {
            return;
        }
        MenuStyle.drawMenuBackground(ctx, this.field_22789, this.field_22790);
    }

    @Inject(method={"method_25420"}, at={@At(value="TAIL")})
    private void astra$pauseBackdrop(GuiGraphics ctx, int mx, int my, float delta, CallbackInfo ci) {
        if (this instanceof PauseScreen) {
            Screen screen = (Screen)this;
            NativeScreenLayouts.backdrop(screen, ctx);
            float aw = Math.min((float)this.field_22789 * 0.26f, (float)this.field_22790 * 0.5f);
            NativeScreenLayouts.art(screen, ctx, ThemeManager.get(), (float)this.field_22789 - aw - 6.0f, this.field_22790, aw, (float)this.field_22790 * 0.82f, 1.0f);
        }
    }
}

