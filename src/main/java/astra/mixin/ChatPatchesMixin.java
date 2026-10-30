/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.ChatComponent
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.utils.ChatPatches;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ChatComponent.class})
public abstract class ChatPatchesMixin {
    private static final String ADD = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V";

    @ModifyVariable(method={"addMessage(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"}, at=@At(value="HEAD"), argsOnly=true, ordinal=0)
    private Component astra$decorate(Component message) {
        return ChatPatches.decorate((ChatComponent)(Object)this, message);
    }

    @Inject(method={"addMessage(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"}, at=@At(value="TAIL"))
    private void astra$afterAdd(CallbackInfo ci) {
        ChatPatches.afterAdd((ChatComponent)(Object)this);
    }

    @ModifyConstant(method={"addMessageToDisplayQueue", "addMessageToQueue(Lnet/minecraft/class_303;)V"}, constant={@Constant(intValue=100)}, require=0)
    private int astra$historyLimit(int original) {
        return ChatPatches.limit();
    }

    @Inject(method={"clearMessages"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$keep(boolean clearHistory, CallbackInfo ci) {
        if (clearHistory && ChatPatches.keepChat()) {
            ci.cancel();
        }
    }
}

