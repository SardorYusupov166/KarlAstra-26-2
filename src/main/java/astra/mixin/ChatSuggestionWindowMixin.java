/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.CommandSuggestions$SuggestionsList
 *  net.minecraft.client.gui.screens.ChatScreen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArgs
 *  org.spongepowered.asm.mixin.injection.invoke.arg.Args
 */
package astra.mixin;

import astra.module.modules.hud.ChatInputHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value={CommandSuggestions.SuggestionsList.class})
public abstract class ChatSuggestionWindowMixin {
    @ModifyArgs(method={"<init>"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/Rect2i;<init>(IIII)V")})
    private void astra$placeSuggestions(Args args) {
        Minecraft mc = Minecraft.getInstance();
        ChatInputHud input = ChatInputHud.get();
        if (!(mc.screen instanceof ChatScreen) || input == null || !input.isEnabled()) {
            return;
        }
        ChatInputHud.Bounds b = input.bounds();
        int width = (Integer)args.get(2);
        int height = (Integer)args.get(3);
        int x = (Integer)args.get(0);
        int y = b.y() - height - 3;
        if (y < 0) {
            y = b.y() + b.height() + 2;
        }
        args.set(0, (Object)Math.max(0, Math.min(x, mc.getWindow().getGuiScaledWidth() - width)));
        args.set(1, (Object)Math.max(0, Math.min(y, mc.getWindow().getGuiScaledHeight() - height)));
    }
}

