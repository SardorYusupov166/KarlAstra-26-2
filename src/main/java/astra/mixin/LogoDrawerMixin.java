/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.LogoRenderer
 *  net.minecraft.client.gui.screens.Screen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.gui.style.NativeScreenLayouts;
import astra.gui.vanilla.AstraWidgets;
import astra.render.ArtManager;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LogoRenderer.class})
public abstract class LogoDrawerMixin {
    @Inject(method={"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IFI)V"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$logo(GuiGraphicsExtractor ctx, int screenWidth, float alpha, int y, CallbackInfo ci) {
        if (!AstraWidgets.enabled()) {
            return;
        }
        Theme t = ThemeManager.get();
        float w = 250.0f;
        Screen screen = NativeScreenLayouts.current();
        if (!NativeScreenLayouts.key(screen).isEmpty()) {
            NativeScreenLayouts.logo(screen, ctx, ((float)screenWidth - w) / 2.0f, y - 14, w, alpha);
        } else {
            ArtManager.drawLogo(ctx, ((float)screenWidth - w) / 2.0f, y - 14, w, alpha, t.accent(), t.accent2());
        }
        ci.cancel();
    }
}

