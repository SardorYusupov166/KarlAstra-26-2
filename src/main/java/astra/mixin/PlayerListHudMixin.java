/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.PlayerTabOverlay
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.world.scores.Objective
 *  net.minecraft.world.scores.Scoreboard
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.TabLayoutHud;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerTabOverlay.class})
public abstract class PlayerListHudMixin {
    @Unique
    private boolean astra$moved;

    @Inject(method={"render"}, at={@At(value="HEAD")})
    private void astra$begin(GuiGraphics ctx, int sw, Scoreboard sb, Objective obj, CallbackInfo ci) {
        this.astra$moved = TabLayoutHud.begin(ctx);
    }

    @Inject(method={"render"}, at={@At(value="RETURN")})
    private void astra$end(GuiGraphics ctx, int sw, Scoreboard sb, Objective obj, CallbackInfo ci) {
        if (this.astra$moved) {
            ctx.pose().popMatrix();
        }
        this.astra$moved = false;
    }

    @Redirect(method={"render"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V")})
    private void astra$fill(GuiGraphics ctx, int x1, int y1, int x2, int y2, int color) {
        if (AnimeChatModule.tabStyled()) {
            AnimeChatModule.tabFill(ctx, x1, y1, x2, y2, color);
        } else {
            ctx.fill(x1, y1, x2, y2, color);
        }
    }

    @Inject(method={"renderPingIcon"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$ping(GuiGraphics ctx, int width, int x, int y, PlayerInfo entry, CallbackInfo ci) {
        if (!AnimeChatModule.pingNumbers()) {
            return;
        }
        AnimeChatModule.ping(ctx, width, x, y, entry.getLatency());
        ci.cancel();
    }
}

