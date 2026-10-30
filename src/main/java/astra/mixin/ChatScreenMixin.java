/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.gui.ActiveTextCollector$ClickableStyleFinder
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.ChatInputHud;
import astra.module.modules.hud.ChatLayoutHud;
import astra.utils.ChatPatches;
import astra.utils.ChatScrollbar;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ChatScreen.class})
public abstract class ChatScreenMixin
extends Screen {
    @Shadow
    protected EditBox input;
    @Unique
    private final ChatScrollbar astra$scrollbar = new ChatScrollbar();

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(method={"init"}, at=@At(value="TAIL"))
    private void astra$placeInput(CallbackInfo ci) {
        ChatInputHud layout = ChatInputHud.get();
        if (layout != null && layout.isEnabled()) {
            layout.place(this.input);
        }
    }

    public boolean mouseDragged(MouseButtonEvent click, double dx, double dy) {
        if (click.button() == 0 && this.astra$scrollbar.drag(click.y())) {
            return true;
        }
        return super.mouseDragged(click, dx, dy);
    }

    public boolean mouseReleased(MouseButtonEvent click) {
        if (click.button() == 0 && this.astra$scrollbar.release()) {
            return true;
        }
        return super.mouseReleased(click);
    }

    @Inject(method={"removed"}, at=@At(value="HEAD"))
    private void astra$stopDrag(CallbackInfo ci) {
        this.astra$scrollbar.release();
    }

    @Inject(method={"mouseClicked"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$copy(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        ChatInputHud input = ChatInputHud.get();
        if (button == 0 && input != null && input.isEnabled() && input.bounds().contains(mouseX, mouseY)) {
            this.input.mouseClicked(click, doubled);
            cir.setReturnValue(true);
            return;
        }
        if (button == 0 && this.astra$scrollbar.click(mouseX, mouseY)) {
            cir.setReturnValue(true);
            return;
        }
        if (button == 1 && ChatPatches.copyAt(mouseX, mouseY)) {
            cir.setReturnValue(true);
        }
    }

    @WrapOperation(method={"mouseClicked"}, at=@At(value="NEW", target="(Lnet/minecraft/client/gui/Font;II)Lnet/minecraft/client/gui/ActiveTextCollector$ClickableStyleFinder;"))
    private ActiveTextCollector.ClickableStyleFinder astra$clickHandler(Font tr, int x, int y, Operation<ActiveTextCollector.ClickableStyleFinder> original) {
        return (ActiveTextCollector.ClickableStyleFinder)original.call(new Object[]{tr, (int)ChatLayoutHud.unmapX(x), (int)ChatLayoutHud.unmapY(y)});
    }

    @Redirect(method={"extractRenderState"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"), require=0)
    private void astra$input(GuiGraphicsExtractor ctx, int x1, int y1, int x2, int y2, int color) {
        ChatInputHud input = ChatInputHud.get();
        if (input != null && input.isEnabled()) {
            ChatInputHud.Bounds b = input.bounds();
            input.drawBackground(ctx, b.x(), b.y(), b.width(), b.height());
        } else if (AnimeChatModule.chatStyled()) {
            AnimeChatModule.chatInput(ctx, x1, y1, x2, y2);
        } else {
            ctx.fill(x1, y1, x2, y2, color);
        }
    }
}

