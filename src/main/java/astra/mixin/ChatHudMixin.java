/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.ChatComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.ChatLayoutHud;
import astra.utils.ChatScrollbar;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChatComponent.class})
public abstract class ChatHudMixin {
    @Unique
    private boolean astra$moved;
    private static final String RENDER = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V";

    @Inject(method={"render(Lnet/minecraft/class_332;Lnet/minecraft/class_327;IIIZZ)V"}, at={@At(value="HEAD")})
    private void astra$begin(GuiGraphics ctx, Font tr, int tick, int mx, int my, boolean focused, boolean insert, CallbackInfo ci) {
        this.astra$moved = ChatLayoutHud.begin(ctx);
        AnimeChatModule.chatPanel(ctx, tick, focused);
    }

    @Inject(method={"render(Lnet/minecraft/class_332;Lnet/minecraft/class_327;IIIZZ)V"}, at={@At(value="RETURN")})
    private void astra$end(GuiGraphics ctx, Font tr, int tick, int mx, int my, boolean focused, boolean insert, CallbackInfo ci) {
        ChatScrollbar.render(ctx, focused);
        if (this.astra$moved) {
            ctx.pose().popMatrix();
        }
        this.astra$moved = false;
    }
}

