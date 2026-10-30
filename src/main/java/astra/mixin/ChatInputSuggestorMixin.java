/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.CommandSuggestions
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.util.FormattedCharSequence
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.modules.hud.ChatInputHud;
import astra.utils.StreamerProtect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={CommandSuggestions.class})
public abstract class ChatInputSuggestorMixin {
    @Shadow
    @Final
    EditBox input;
    @Shadow
    private int commandUsagePosition;

    @Redirect(method={"showSuggestions", "extractUsage"}, at=@At(value="FIELD", target="Lnet/minecraft/client/gui/screens/Screen;height:I", opcode=180))
    private int astra$inputAnchor(Screen owner) {
        ChatInputHud layout = ChatInputHud.get();
        return owner instanceof ChatScreen && layout != null && layout.isEnabled() ? this.input.getY() + 12 : owner.height;
    }

    @Redirect(method={"extractUsage"}, at=@At(value="FIELD", target="Lnet/minecraft/client/gui/components/CommandSuggestions;commandUsagePosition:I", opcode=180))
    private int astra$usageX(CommandSuggestions self) {
        ChatInputHud layout = ChatInputHud.get();
        return Minecraft.getInstance().gui.screen() instanceof ChatScreen && layout != null && layout.isEnabled() ? this.input.getX() : this.astra$originalUsageX();
    }

    @Unique
    private int astra$originalUsageX() {
        return this.commandUsagePosition;
    }

    @Inject(method={"formatChat"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$maskPassword(String original, int firstCharacterIndex, CallbackInfoReturnable<FormattedCharSequence> cir) {
        FormattedCharSequence masked = StreamerProtect.maskInput(this.input.getValue(), original, firstCharacterIndex);
        if (masked != null) {
            cir.setReturnValue(masked);
        }
    }
}

