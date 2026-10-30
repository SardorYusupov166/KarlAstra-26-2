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
    EditBox field_21599;
    @Shadow
    private int field_21608;

    @Redirect(method={"method_23920", "method_44932"}, at={@At(value="FIELD", target="Lnet/minecraft/client/gui/screens/Screen;field_22790:I", opcode=180)})
    private int astra$inputAnchor(Screen owner) {
        ChatInputHud layout = ChatInputHud.get();
        return owner instanceof ChatScreen && layout != null && layout.isEnabled() ? this.field_21599.method_46427() + 12 : owner.field_22790;
    }

    @Redirect(method={"method_44932"}, at={@At(value="FIELD", target="Lnet/minecraft/client/gui/components/CommandSuggestions;field_21608:I", opcode=180)})
    private int astra$usageX(CommandSuggestions self) {
        ChatInputHud layout = ChatInputHud.get();
        return Minecraft.method_1551().field_1755 instanceof ChatScreen && layout != null && layout.isEnabled() ? this.field_21599.method_46426() : this.astra$originalUsageX();
    }

    @Unique
    private int astra$originalUsageX() {
        return this.field_21608;
    }

    @Inject(method={"method_23931"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$maskPassword(String original, int firstCharacterIndex, CallbackInfoReturnable<FormattedCharSequence> cir) {
        FormattedCharSequence masked = StreamerProtect.maskInput(this.field_21599.method_1882(), original, firstCharacterIndex);
        if (masked != null) {
            cir.setReturnValue((Object)masked);
        }
    }
}

