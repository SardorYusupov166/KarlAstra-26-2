/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    @Inject(method={"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V"}, at=@At(value="HEAD"))
    private void astra$begin(GuiGraphicsExtractor ctx, Font tr, int tick, int mx, int my, ChatComponent.DisplayMode mode, boolean insert, CallbackInfo ci) {
        this.astra$moved = ChatLayoutHud.begin(ctx);
        AnimeChatModule.chatPanel(ctx, tick, ((ChatComponent)(Object)this).isChatFocused());
    }

    @Inject(method={"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V"}, at=@At(value="RETURN"))
    private void astra$end(GuiGraphicsExtractor ctx, Font tr, int tick, int mx, int my, ChatComponent.DisplayMode mode, boolean insert, CallbackInfo ci) {
        ChatScrollbar.render(ctx, ((ChatComponent)(Object)this).isChatFocused());
        if (this.astra$moved) {
            ctx.pose().popMatrix();
        }
        this.astra$moved = false;
    }
}

