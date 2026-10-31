/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package astra.mixin;

import astra.module.modules.hud.AnimeChatModule;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"net/minecraft/client/gui/components/ChatComponent$DrawingBackgroundGraphicsAccess", "net/minecraft/client/gui/components/ChatComponent$DrawingFocusedGraphicsAccess"})
public abstract class ChatHudBackendMixin {
    @Redirect(method={"fill"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void astra$fill(GuiGraphicsExtractor ctx, int x1, int y1, int x2, int y2, int color) {
        if (!AnimeChatModule.chatStyled()) {
            ctx.fill(x1, y1, x2, y2, color);
            return;
        }
        if (Math.abs(x2 - x1) <= 3) {
            return;
        }
        AnimeChatModule.chatFill(ctx, x1, y1, x2, y2, color);
    }
}

