/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.ChatComponent
 *  net.minecraft.world.entity.player.ChatVisiblity
 *  org.joml.Matrix3x2fStack
 */
package astra.utils;

import astra.mixin.ChatHudAccessor;
import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.ChatLayoutHud;
import astra.render.ColorUtil;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.joml.Matrix3x2fStack;

public final class ChatScrollbar {
    private boolean dragging;
    private double grab;

    private static Geometry geometry() {
        Minecraft mc = Minecraft.method_1551();
        if (!AnimeChatModule.chatStyled() || mc.field_1705 == null || mc.field_1690.method_42539().method_41753() == ChatVisiblity.field_7536) {
            return null;
        }
        ChatComponent hud = mc.field_1705.method_1743();
        ChatHudAccessor access = (ChatHudAccessor)hud;
        int total = access.astra$visibleMessages().size();
        int visible = Math.min(total, hud.method_1813());
        if (visible <= 0 || total <= visible) {
            return null;
        }
        double scale = ((ChatHudAccessor)hud).astra$chatScale();
        if (scale <= 0.0) {
            return null;
        }
        double height = visible * access.astra$lineHeight();
        double bottom = Math.floor((double)(mc.method_22683().method_4502() - 40) / scale);
        double thumb = Math.min(height, Math.max(8.0, height * (double)visible / (double)total));
        int max = total - visible;
        double top = bottom - thumb - (height - thumb) * (double)access.astra$scrolledLines() / (double)max;
        return new Geometry(Math.ceil((double)((ChatHudAccessor)hud).astra$width() / scale) + 4.0, bottom - height, bottom, top, thumb, max, scale);
    }

    public boolean click(double x, double y) {
        this.dragging = false;
        Geometry g = ChatScrollbar.geometry();
        if (g == null) {
            return false;
        }
        double localX = ChatLayoutHud.unmapX(x) / g.scale - 4.0;
        double localY = ChatLayoutHud.unmapY(y) / g.scale;
        if (localX < g.x - 3.0 || localX > g.x + 8.0 || localY < g.top || localY > g.bottom) {
            return false;
        }
        this.grab = localY >= g.thumbTop && localY <= g.thumbTop + g.thumbHeight ? localY - g.thumbTop : g.thumbHeight / 2.0;
        this.dragging = true;
        this.drag(y);
        return true;
    }

    public boolean drag(double y) {
        if (!this.dragging) {
            return false;
        }
        Geometry g = ChatScrollbar.geometry();
        if (g == null) {
            this.dragging = false;
            return true;
        }
        double thumbTop = ChatLayoutHud.unmapY(y) / g.scale - this.grab;
        int desired = g.travel() <= 0.0 ? 0 : (int)Math.round(Math.max(0.0, Math.min(1.0, (g.bottom - g.thumbHeight - thumbTop) / g.travel())) * (double)g.maxScroll);
        ChatComponent hud = Minecraft.method_1551().field_1705.method_1743();
        hud.method_1802(desired - ((ChatHudAccessor)hud).astra$scrolledLines());
        return true;
    }

    public boolean release() {
        boolean was = this.dragging;
        this.dragging = false;
        return was;
    }

    public static void render(GuiGraphics ctx, boolean focused) {
        if (!focused) {
            return;
        }
        Geometry g = ChatScrollbar.geometry();
        if (g == null) {
            return;
        }
        Theme t = ThemeManager.get();
        Matrix3x2fStack m = ctx.method_51448();
        m.pushMatrix();
        m.scale((float)g.scale, (float)g.scale);
        m.translate(4.0f, 0.0f);
        Render2D.roundRect(ctx, (float)g.x, (float)g.top, 5.0f, (float)(g.bottom - g.top), 2.0f, ColorUtil.withAlpha(t.bg(), 0.65f));
        Render2D.roundRect(ctx, (float)g.x, (float)g.thumbTop, 5.0f, (float)g.thumbHeight, 2.0f, ColorUtil.withAlpha(t.accent2(), 0.95f));
        m.popMatrix();
    }

    private record Geometry(double x, double top, double bottom, double thumbTop, double thumbHeight, int maxScroll, double scale) {
        double travel() {
            return this.bottom - this.top - this.thumbHeight;
        }
    }
}

