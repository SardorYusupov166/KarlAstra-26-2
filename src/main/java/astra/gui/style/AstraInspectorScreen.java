/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  org.joml.Matrix3x2fStack
 */
package astra.gui.style;

import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.Popup;
import astra.gui.components.SettingRows;
import astra.gui.components.Widget;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Render2D;
import astra.settings.Setting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

public abstract class AstraInspectorScreen
extends Screen
implements PopupHost {
    protected final UiContext ui = new UiContext();
    protected final List<Widget> rows = new ArrayList<Widget>();
    protected float s = 1.0f;
    protected float dw;
    protected float dh;
    protected float scroll;
    protected float contentH;
    protected String status = "\u041b\u041a\u041c \u2014 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u00b7 \u0442\u044f\u043d\u0443\u0442\u044c \u2014 \u0434\u0432\u0438\u0433\u0430\u0442\u044c \u00b7 \u0443\u0433\u043b\u044b \u2014 \u0440\u0430\u0437\u043c\u0435\u0440 \u00b7 Shift \u2014 \u0431\u0435\u0437 \u043f\u0440\u0438\u0432\u044f\u0437\u043a\u0438 (\u0443 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438 \u2014 \u0441 \u043f\u0440\u043e\u043f\u043e\u0440\u0446\u0438\u044f\u043c\u0438)";
    private Popup popup;
    private Widget focus;

    protected AstraInspectorScreen(String title) {
        super((Component)Component.literal((String)title));
    }

    protected abstract List<Setting<?>> properties();

    protected abstract void workspace(GuiGraphicsExtractor var1, int var2, int var3, float var4);

    protected abstract boolean workspaceClick(double var1, double var3, int var5);

    protected abstract boolean workspaceDrag(double var1, double var3, int var5);

    protected abstract boolean workspaceRelease();

    protected void finishChange() {
    }

    protected void beforeChange() {
    }

    protected void undoAction() {
    }

    protected void deleteAction() {
    }

    protected void rebuildRows() {
        this.rows.clear();
        this.focus = null;
        this.popup = null;
        for (Setting<?> st : this.properties()) {
            this.rows.add(SettingRows.create(st, this));
        }
    }

    private void scale() {
        this.s = Math.max(0.15f, (float)this.height * 0.86f / 600.0f);
        this.dw = (float)this.width / this.s;
        this.dh = (float)this.height / this.s;
    }

    protected void init() {
        this.scale();
        this.rebuildRows();
    }

    protected float workspaceLeft() {
        return 240.0f * this.s;
    }

    public void extractBackground(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        if (this.minecraft.level == null) {
            this.extractPanorama(ctx, delta);
        } else {
            ctx.fill(0, 0, this.width, this.height, 0x20000000);
        }
    }

    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
        this.scale();
        this.workspace(ctx, mx, my, delta);
        Theme t = ThemeManager.get();
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.scale(this.s, this.s);
        this.ui.ctx = ctx;
        this.ui.theme = t;
        this.ui.dt = 0.016f;
        this.ui.mx = (float)mx / this.s;
        this.ui.my = (float)my / this.s;
        this.ui.blocked = this.popup != null;
        Render2D.roundRect(ctx, 8.0f, 8.0f, 220.0f, this.dh - 16.0f, 12.0f, ColorUtil.withAlpha(t.bg2(), 0.96f));
        Render2D.outline(ctx, 8.0f, 8.0f, 220.0f, this.dh - 16.0f, 12.0f, 1.0f, t.stroke());
        ArtManager.drawLogo(ctx, 22.0f, 10.0f, 130.0f, 1.0f, t.accent(), t.accent2());
        Fonts.draw(ctx, this.title.getString(), 20.0f, 67.0f, 10.0f, t.text(), true);
        Fonts.draw(ctx, "\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435 \u0438 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u044b", 20.0f, 83.0f, 7.0f, t.textDim(), false);
        float top = 104.0f;
        float viewH = this.dh - 151.0f;
        this.scroll = Math.max(0.0f, Math.min(this.scroll, Math.max(0.0f, this.contentH - viewH)));
        float y = top - this.scroll;
        for (Widget row : this.rows) {
            if (!row.isVisible()) continue;
            y += row.layout(20.0f, y, 196.0f) + 4.0f;
        }
        this.contentH = y + this.scroll - top;
        this.ui.setClip(12.0f, top, 212.0f, viewH);
        ctx.enableScissor(12, (int)top, 224, (int)(top + viewH));
        for (Widget row : this.rows) {
            if (!row.isVisible() || !(row.y + row.h >= top) || !(row.y < top + viewH)) continue;
            row.render(this.ui);
        }
        ctx.disableScissor();
        this.ui.clearClip();
        if (this.contentH > viewH) {
            float bar = Math.max(18.0f, viewH * viewH / this.contentH);
            Render2D.roundRect(ctx, 222.0f, top + (viewH - bar) * this.scroll / Math.max(1.0f, this.contentH - viewH), 2.0f, bar, 1.0f, t.accent());
        }
        Render2D.roundRect(ctx, 20.0f, this.dh - 35.0f, 196.0f, 20.0f, 8.0f, ColorUtil.withAlpha(t.accent(), 0.18f));
        Fonts.drawCentered(ctx, "\u0413\u043e\u0442\u043e\u0432\u043e", 118.0f, this.dh - 29.0f, 8.0f, t.text(), true);
        Render2D.roundRect(ctx, 244.0f, 8.0f, Math.max(80.0f, this.dw - 254.0f), 28.0f, 9.0f, ColorUtil.withAlpha(t.bg2(), 0.9f));
        Fonts.draw(ctx, Fonts.trim(this.status, this.dw - 280.0f, 8.0f, false), 254.0f, 18.0f, 8.0f, t.textDim(), false);
        if (this.popup != null) {
            this.ui.blocked = false;
            this.popup.render(this.ui);
        }
        m.popMatrix();
    }

    public boolean inputClick(double x, double y, int button) {
        double ux = x / (double)this.s;
        double uy = y / (double)this.s;
        if (this.popup != null) {
            if (!this.popup.mouseClicked(this.ui, ux, uy, button)) {
                this.closePopup();
            }
            return true;
        }
        if (ux < 236.0) {
            if (uy >= (double)(this.dh - 37.0f)) {
                this.finishChange();
                this.onClose();
                return true;
            }
            if (uy >= 104.0 && uy < (double)(this.dh - 47.0f)) {
                for (Widget row : this.rows) {
                    if (!row.isVisible() || !row.contains(ux, uy) || !this.clickRow(row, ux, uy, button)) continue;
                    return true;
                }
            }
            return true;
        }
        this.finishChange();
        this.setFocus(null);
        return this.workspaceClick(x, y, button);
    }

    private boolean clickRow(Widget row, double x, double y, int b) {
        this.beforeChange();
        return row.mouseClicked(this.ui, x, y, b);
    }

    public boolean inputDrag(double x, double y, int button) {
        if (this.popup != null) {
            return this.popup.mouseDragged(this.ui, x / (double)this.s, y / (double)this.s, button);
        }
        for (Widget row : this.rows) {
            if (!row.mouseDragged(this.ui, x / (double)this.s, y / (double)this.s, button)) continue;
            return true;
        }
        return this.workspaceDrag(x, y, button);
    }

    public boolean inputRelease(double x, double y, int button) {
        if (this.popup != null) {
            this.popup.mouseReleased(this.ui, x / (double)this.s, y / (double)this.s, button);
        }
        for (Widget row : this.rows) {
            row.mouseReleased(this.ui, x / (double)this.s, y / (double)this.s, button);
        }
        boolean used = this.workspaceRelease();
        this.finishChange();
        return used || x / (double)this.s < 236.0;
    }

    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        if (this.popup != null) {
            return this.popup.mouseScrolled(x / (double)this.s, y / (double)this.s, vertical);
        }
        if (x / (double)this.s < 236.0) {
            this.scroll = (float)((double)this.scroll - vertical * 32.0);
            return true;
        }
        return false;
    }

    public boolean inputKey(int key, int scan, int mods) {
        if (this.focus != null && this.focus.keyPressed(key, scan, mods)) {
            return true;
        }
        if (key == 256) {
            if (this.popup != null) {
                this.closePopup();
            } else {
                this.finishChange();
                this.onClose();
            }
            return true;
        }
        if (key == 90 && (mods & 2) != 0) {
            this.undoAction();
            return true;
        }
        if (key == 261 && this.focus == null) {
            this.deleteAction();
            return true;
        }
        return false;
    }

    public boolean inputChar(char ch, int mods) {
        return this.focus != null && this.focus.charTyped(ch, mods);
    }

    @Override
    public void openPopup(Popup p) {
        this.popup = p;
        p.fit(Math.round(this.dw), Math.round(this.dh));
    }

    @Override
    public void closePopup() {
        this.popup = null;
    }

    @Override
    public Popup currentPopup() {
        return this.popup;
    }

    @Override
    public void toast(String text) {
        this.status = text;
    }

    @Override
    public void setFocus(Widget widget) {
        this.focus = widget;
    }

    public boolean mouseClicked(MouseButtonEvent c, boolean doubled) {
        return this.inputClick(c.x(), c.y(), c.button());
    }

    public boolean mouseDragged(MouseButtonEvent c, double dx, double dy) {
        return this.inputDrag(c.x(), c.y(), c.button());
    }

    public boolean mouseReleased(MouseButtonEvent c) {
        return this.inputRelease(c.x(), c.y(), c.button());
    }

    public boolean keyPressed(KeyEvent k) {
        return this.inputKey(k.key(), k.scancode(), k.modifiers());
    }

    public boolean charTyped(CharacterEvent c) {
        boolean used = false;
        for (char ch : c.codepointAsString().toCharArray()) {
            used |= this.inputChar(ch, 0);
        }
        return used;
    }

    @Override
    public Widget getFocus() {
        return this.focus;
    }
}

