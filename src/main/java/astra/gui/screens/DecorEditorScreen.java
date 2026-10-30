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
 *  net.minecraft.util.Util
 *  org.joml.Matrix3x2fStack
 */
package astra.gui.screens;

import astra.animation.Animation;
import astra.decor.DecorLayer;
import astra.decor.DecorManager;
import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.DropdownPopup;
import astra.gui.components.Popup;
import astra.gui.components.SettingRows;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.gui.screens.ClickGuiScreen;
import astra.gui.style.GuiStyle;
import astra.module.ClientSettings;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.lang.invoke.StringConcatFactory;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;

public class DecorEditorScreen
extends Screen
implements PopupHost {
    private static final float HANDLE = 4.5f;
    private static final float SNAP = 4.0f;
    private final Screen parent;
    private final ClickGuiScreen preview = new ClickGuiScreen(null, true);
    private final UiContext ui = new UiContext();
    private final List<Widget> rows = new ArrayList<Widget>();
    private final Deque<String> undo = new ArrayDeque<String>();
    private final Animation[] btnHover = new Animation[8];
    private String target = "menu";
    private DecorLayer selected;
    private DecorLayer rowsFor;
    private boolean panelShown = true;
    private Popup popup;
    private Widget focus;
    private String toast;
    private long toastAt;
    private long lastFrame;
    private float scroll;
    private String element;
    private String rowsForElement;
    private boolean elementsOpen;
    private int clickCount;
    private long lastClickAt;
    private double lastClickX;
    private double lastClickY;
    private float startDx;
    private float startDy;
    private float startScale;
    private Drag drag = Drag.NONE;
    private int handleX;
    private int handleY;
    private float startMx;
    private float startMy;
    private float[] startGeom;
    private float startRot;
    private boolean snapX;
    private boolean snapY;
    private final List<float[]> buttons = new ArrayList<float[]>();
    private final List<float[]> layerRows = new ArrayList<float[]>();

    public DecorEditorScreen(Screen parent) {
        super((Component)Component.literal((String)"\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f"));
        this.parent = parent;
        for (int i = 0; i < this.btnHover.length; ++i) {
            this.btnHover[i] = new Animation(0.0f, 16.0f);
        }
    }

    protected void init() {
        this.preview.init(this.width, this.height);
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        DecorManager.editing = false;
        DecorManager.save();
        GuiStyle.save();
        this.minecraft.gui.setScreen(this.parent);
    }

    public void removed() {
        DecorManager.editing = false;
        DecorManager.save();
        GuiStyle.save();
    }

    private float panelW() {
        return this.panelShown ? Math.min(200.0f, (float)this.width * 0.34f) : 0.0f;
    }

    private float[] menuView() {
        return this.preview.previewTransform(10.0f, 10.0f, (float)this.width - this.panelW() - 20.0f, this.height - 20);
    }

    private float[] geom(DecorLayer l) {
        if (l.isMenu()) {
            float[] v = this.menuView();
            float dw = this.preview.layoutDesignWidth();
            return new float[]{v[0] + l.x * dw * v[2], v[1] + l.y * v[2], l.w * v[2], l.h * v[2]};
        }
        return new float[]{l.x * (float)this.width, l.y * (float)this.height, l.w * (float)this.height, l.h * (float)this.height};
    }

    private void setGeom(DecorLayer l, float cx, float cy, float w, float h) {
        w = Math.max(4.0f, w);
        h = Math.max(4.0f, h);
        if (l.isMenu()) {
            float[] v = this.menuView();
            float dw = this.preview.layoutDesignWidth();
            l.x = (cx - v[0]) / v[2] / dw;
            l.y = (cy - v[1]) / v[2];
            l.w = w / v[2];
            l.h = h / v[2];
        } else {
            l.x = cx / (float)this.width;
            l.y = cy / (float)this.height;
            l.w = w / (float)this.height;
            l.h = h / (float)this.height;
        }
    }

    private static float[] toLocal(float[] g, float rotDeg, float px, float py) {
        double a = Math.toRadians(-rotDeg);
        float dx = px - g[0];
        float dy = py - g[1];
        return new float[]{(float)((double)dx * Math.cos(a) - (double)dy * Math.sin(a)), (float)((double)dx * Math.sin(a) + (double)dy * Math.cos(a))};
    }

    private static float[] toScreen(float[] g, float rotDeg, float lx, float ly) {
        double a = Math.toRadians(rotDeg);
        return new float[]{g[0] + (float)((double)lx * Math.cos(a) - (double)ly * Math.sin(a)), g[1] + (float)((double)lx * Math.sin(a) + (double)ly * Math.cos(a))};
    }

    private List<DecorLayer> current() {
        ArrayList<DecorLayer> list = new ArrayList<DecorLayer>();
        for (DecorLayer l : DecorManager.layers()) {
            if (!l.target.equals(this.target)) continue;
            list.add(l);
        }
        return list;
    }

    public void extractBackground(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        Theme t = ThemeManager.get();
        if ("menu".equals(this.target)) {
            if (this.minecraft.level == null) {
                this.extractPanorama(ctx, delta);
            } else if (this.minecraft.options.getMenuBackgroundBlurriness() >= 1) {
                ctx.blurBeforeThisStratum();
            }
            ctx.fillGradient(0, 0, this.width, this.height, ColorUtil.withAlpha(t.bg(), 0.55f), ColorUtil.withAlpha(t.bg(), 0.75f));
        } else if (this.minecraft.level == null) {
            this.extractPanorama(ctx, delta);
        }
    }

    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        super.extractRenderState(ctx, mouseX, mouseY, delta);
        long now = System.nanoTime();
        float dt = this.lastFrame == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrame) / 1.0E9f);
        this.lastFrame = now;
        Theme t = ThemeManager.get();
        this.ui.ctx = ctx;
        this.ui.dt = dt;
        this.ui.theme = t;
        this.ui.mx = mouseX;
        this.ui.my = mouseY;
        this.ui.blocked = this.popup != null && this.popup.contains(mouseX, mouseY);
        boolean bl = DecorManager.editing = !"menu".equals(this.target);
        if ("menu".equals(this.target)) {
            float[] v = this.menuView();
            this.preview.renderPreview(ctx, v[0], v[1], v[2], dt);
        } else {
            float w = this.width;
            float h = this.height;
            DecorManager.renderScreen(ctx, "\u041f\u043e\u0434 HUD", w, h, true);
            DecorManager.renderScreen(ctx, "\u041f\u043e\u0432\u0435\u0440\u0445 HUD", w, h, true);
            Render2D.outline(ctx, 1.0f, 1.0f, w - 2.0f, h - 2.0f, 0.0f, 1.0f, ColorUtil.withAlpha(t.accent(), 0.5f));
        }
        this.renderSelection(ctx, t);
        this.renderElementSelection(ctx, t);
        if (this.panelShown) {
            this.renderPanel(ctx, t);
        } else {
            Fonts.draw(ctx, "Tab \u2014 \u043f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u043f\u0430\u043d\u0435\u043b\u044c", 8.0f, this.height - 14, 7.0f, ColorUtil.withAlpha(t.text(), 0.8f), true);
        }
        if (this.popup != null) {
            this.ui.blocked = false;
            this.popup.render(this.ui);
        }
        this.renderToast(ctx, t);
    }

    private void renderSelection(GuiGraphicsExtractor ctx, Theme t) {
        if (this.selected == null || !this.selected.target.equals(this.target)) {
            return;
        }
        float[] g = this.geom(this.selected);
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(g[0], g[1]);
        m.rotate((float)Math.toRadians(this.selected.rotation));
        float x0 = -g[2] / 2.0f;
        float y0 = -g[3] / 2.0f;
        Render2D.outline(ctx, x0, y0, g[2], g[3], 0.0f, 1.0f, t.accent2());
        Render2D.line(ctx, 0.0f, y0, 0.0f, y0 - 14.0f, 1.0f, t.accent2());
        Render2D.circle(ctx, 0.0f, y0 - 16.0f, 4.0f, -1);
        Render2D.ring(ctx, 0.0f, y0 - 16.0f, 4.0f, 1.0f, t.accent2());
        for (int hx = -1; hx <= 1; ++hx) {
            for (int hy = -1; hy <= 1; ++hy) {
                if (hx == 0 && hy == 0) continue;
                float px = (float)hx * g[2] / 2.0f;
                float py = (float)hy * g[3] / 2.0f;
                boolean corner = hx != 0 && hy != 0;
                Render2D.roundRect(ctx, px - 2.25f, py - 2.25f, 4.5f, 4.5f, corner ? 1.0f : 2.25f, -1);
                Render2D.outline(ctx, px - 2.25f, py - 2.25f, 4.5f, 4.5f, corner ? 1.0f : 2.25f, 1.0f, t.accent());
            }
        }
        m.popMatrix();
        if (this.drag == Drag.MOVE && (this.snapX || this.snapY)) {
            int c = ColorUtil.withAlpha(t.accent2(), 0.8f);
            if (this.snapX) {
                Render2D.line(ctx, g[0], 0.0f, g[0], this.height, 0.8f, c);
            }
            if (this.snapY) {
                Render2D.line(ctx, 0.0f, g[1], this.width, g[1], 0.8f, c);
            }
        }
    }

    private List<GuiStyle.Hit> elementHits(String id) {
        ArrayList<GuiStyle.Hit> out = new ArrayList<GuiStyle.Hit>();
        for (GuiStyle.Hit h : GuiStyle.HITS) {
            if (!h.id().equals(id)) continue;
            out.add(h);
        }
        return out;
    }

    private void renderElementSelection(GuiGraphicsExtractor ctx, Theme t) {
        if (this.element == null || !"menu".equals(this.target)) {
            return;
        }
        GuiStyle.Element info = GuiStyle.ELEMENTS.get(this.element);
        List<GuiStyle.Hit> hits = this.elementHits(this.element);
        float pulse = 0.55f + 0.45f * (float)Math.sin((double)System.currentTimeMillis() / 180.0);
        for (GuiStyle.Hit hit : hits) {
            Render2D.outline(ctx, hit.x0() - 1.0f, hit.y0() - 1.0f, hit.x1() - hit.x0() + 2.0f, hit.y1() - hit.y0() + 2.0f, 2.0f, 1.0f, ColorUtil.withAlpha(t.accent2(), info != null && info.movable() ? 1.0f : 0.5f + 0.4f * pulse));
        }
        if (hits.isEmpty() || info == null) {
            return;
        }
        GuiStyle.Hit h = hits.get(0);
        if (info.movable()) {
            for (float[] c : new float[][]{{h.x0(), h.y0()}, {h.x1(), h.y0()}, {h.x0(), h.y1()}, {h.x1(), h.y1()}}) {
                Render2D.roundRect(ctx, c[0] - 2.25f, c[1] - 2.25f, 4.5f, 4.5f, 1.0f, -1);
                Render2D.outline(ctx, c[0] - 2.25f, c[1] - 2.25f, 4.5f, 4.5f, 1.0f, 1.0f, t.accent());
            }
        }
        String string = info.label() + (String)(info.movable() ? "" : " (" + hits.size() + ")");
        float lw = Fonts.width(string, 6.5f, true) + 10.0f;
        float ly = Math.max(2.0f, h.y0() - 14.0f);
        Render2D.roundRect(ctx, h.x0(), ly, lw, 11.0f, 3.0f, t.accent2());
        Fonts.draw(ctx, string, h.x0() + 5.0f, ly + 2.5f, 6.5f, -1, true);
    }

    /*
     * WARNING - void declaration
     */
    private void renderPanel(GuiGraphicsExtractor ctx, Theme t) {
        String[] hints;
        String sideImage;
        this.buttons.clear();
        this.layerRows.clear();
        float pw = this.panelW();
        float px = (float)this.width - pw;
        Render2D.rect(ctx, px, 0.0f, pw, (float)this.height, Paint.horizontal(px, this.width, ColorUtil.withAlpha(t.bg(), 0.93f), ColorUtil.withAlpha(t.bg2(), 0.96f)));
        Render2D.rect(ctx, px, 0.0f, 1.0f, (float)this.height, Paint.vertical(0.0f, this.height, t.accent(), t.accent2()));
        float x = px + 9.0f;
        float w = pw - 18.0f;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(0.0f, -this.scroll);
        float my = this.ui.my;
        this.ui.my = my + this.scroll;
        float y = 9.0f;
        Fonts.draw(ctx, "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f", x, y, 9.5f, t.text(), true);
        Fonts.draw(ctx, "\u0421\u0432\u043e\u0438 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438 \u0432 \u043c\u0435\u043d\u044e \u0438 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", x, y += 13.0f, 6.2f, t.textMuted(), false);
        float tw = (w - 4.0f) / 2.0f;
        this.tab(ctx, t, x, y += 13.0f, tw, "\u041c\u0435\u043d\u044e Astra", "menu");
        this.tab(ctx, t, x + tw + 4.0f, y, tw, "\u042d\u043a\u0440\u0430\u043d \u0438\u0433\u0440\u044b", "screen");
        this.button(ctx, t, 0, x, y += 22.0f, w * 0.62f - 2.0f, "\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", Icon.PLUS, true);
        this.button(ctx, t, 1, x + w * 0.62f + 2.0f, y, w * 0.38f - 2.0f, "\u041f\u0430\u043f\u043a\u0430", Icon.FOLDER, false);
        List<DecorLayer> list = this.current();
        Fonts.draw(ctx, "\u0421\u043b\u043e\u0438 (" + list.size() + ")", x, (y += 21.0f) + 2.0f, 7.0f, t.textDim(), true);
        y += 13.0f;
        String string = sideImage = "menu".equals(this.target) ? ArtManager.sidebarImageForDecor() : null;
        if (sideImage != null) {
            boolean hov = this.ui.hover(x, y, w, 25.0f);
            Render2D.roundRect(ctx, x, y, w, 25.0f, 4.0f, ColorUtil.withAlpha(t.accent2(), hov ? 0.22f : 0.12f));
            Render2D.outline(ctx, x, y, w, 25.0f, 4.0f, 0.7f, ColorUtil.withAlpha(t.accent2(), 0.6f));
            DecorManager.Tex tex = DecorManager.texture(sideImage);
            if (tex != null) {
                Render2D.textureRoundRect(ctx, tex.id(), tex.aspect(), x + 3.0f, y + 3.0f, 19.0f, 19.0f, 3.0f, -1);
            }
            Fonts.draw(ctx, "\u0410\u0440\u0442 \u0431\u043e\u043a\u043e\u0432\u043e\u0439 \u043f\u0430\u043d\u0435\u043b\u0438", x + 26.0f, y + 4.0f, 6.8f, t.text(), true);
            Fonts.draw(ctx, "\u043d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u0434\u0432\u0438\u0433\u0430\u0442\u044c \u0438 \u0440\u0430\u0441\u0442\u044f\u0433\u0438\u0432\u0430\u0442\u044c", x + 26.0f, y + 14.0f, 5.8f, t.textMuted(), false);
            this.buttons.add(new float[]{x, y, w, 25.0f, 200.0f});
            y += 29.0f;
        }
        if (list.isEmpty()) {
            Fonts.draw(ctx, "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u00ab\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430\u00bb, \u0447\u0442\u043e\u0431\u044b \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c.", x, y, 6.4f, t.textMuted(), false);
            Fonts.draw(ctx, "\u0421\u0432\u043e\u0438 \u0444\u0430\u0439\u043b\u044b \u2014 \u0432 config/astra/art", x, y += 9.0f, 6.4f, t.textMuted(), false);
            y += 14.0f;
        }
        for (int i = list.size() - 1; i >= 0; --i) {
            DecorManager.Tex tex;
            DecorLayer decorLayer = list.get(i);
            boolean sel = decorLayer == this.selected;
            boolean hov = this.ui.hover(x, y, w, 17.0f);
            if (sel || hov) {
                Render2D.roundRect(ctx, x, y, w, 17.0f, 4.0f, ColorUtil.withAlpha(t.accent(), sel ? 0.28f : 0.12f));
            }
            if (sel) {
                Render2D.outline(ctx, x, y, w, 17.0f, 4.0f, 0.8f, ColorUtil.withAlpha(t.accent2(), 0.7f));
            }
            if ((tex = DecorManager.texture(decorLayer.image)) != null) {
                Render2D.textureRoundRect(ctx, tex.id(), tex.aspect(), x + 2.0f, y + 2.0f, 13.0f, 13.0f, 2.0f, -1);
            } else {
                Render2D.roundRect(ctx, x + 2.0f, y + 2.0f, 13.0f, 13.0f, 2.0f, ColorUtil.withAlpha(t.textMuted(), 0.4f));
            }
            Fonts.draw(ctx, Fonts.trim(decorLayer.image.isEmpty() ? "\u0431\u0435\u0437 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438" : decorLayer.image, w - 42.0f, 6.6f, sel), x + 19.0f, y + 5.0f, 6.6f, decorLayer.visible ? t.text() : t.textMuted(), sel);
            Fonts.draw(ctx, decorLayer.visible ? "\u25cf" : "\u25cb", x + w - 10.0f, y + 4.0f, 7.0f, decorLayer.visible ? t.accent2() : t.textMuted(), true);
            this.layerRows.add(new float[]{x, y, w, 17.0f, DecorManager.layers().indexOf(decorLayer)});
            y += 19.0f;
        }
        y += 4.0f;
        if ("menu".equals(this.target)) {
            boolean hovE = this.ui.hover(x, y, w, 15.0f);
            Render2D.roundRect(ctx, x, y, w, 15.0f, 4.0f, ColorUtil.withAlpha(t.card(), hovE ? 1.0f : 0.75f));
            Fonts.draw(ctx, (this.elementsOpen ? "\u2212 " : "+ ") + "\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430", x + 5.0f, y + 4.0f, 6.8f, t.text(), true);
            this.buttons.add(new float[]{x, y, w, 15.0f, 300.0f});
            y += 18.0f;
            if (this.elementsOpen) {
                int elementIndex = 0;
                for (GuiStyle.Element e : GuiStyle.ELEMENTS.values()) {
                    GuiStyle.Override o;
                    boolean sel = e.id().equals(this.element);
                    boolean hov = this.ui.hover(x, y, w, 13.0f);
                    if (sel || hov) {
                        Render2D.roundRect(ctx, x, y, w, 13.0f, 3.0f, ColorUtil.withAlpha(t.accent(), sel ? 0.3f : 0.12f));
                    }
                    String mark = (o = GuiStyle.get(e.id())) != null && !o.isDefault() ? " \u2022" : "";
                    Fonts.draw(ctx, e.label() + mark, x + 6.0f, y + 3.5f, 6.4f, sel ? t.text() : t.textDim(), sel);
                    this.buttons.add(new float[]{x, y, w, 13.0f, 400 + elementIndex});
                    y += 14.0f;
                    ++elementIndex;
                }
                y += 4.0f;
            }
        }
        if (this.element != null && "menu".equals(this.target)) {
            if (!this.element.equals(this.rowsForElement)) {
                this.buildElementRows(this.element);
            }
            GuiStyle.Element info = GuiStyle.ELEMENTS.get(this.element);
            Render2D.rect(ctx, x, y, w, 0.8f, Paint.horizontal(x, x + w, t.accent2(), ColorUtil.withAlpha(t.accent(), 0.0f)));
            Fonts.draw(ctx, info != null ? info.label() : this.element, x, y += 5.0f, 8.0f, t.text(), true);
            y += 12.0f;
            for (Widget r : this.rows) {
                if (!r.isVisible()) continue;
                float rh = r.layout(x, y, w);
                r.render(this.ui);
                y += rh + 3.0f;
            }
        } else if (this.selected != null && this.selected.target.equals(this.target)) {
            if (this.rowsFor != this.selected) {
                this.buildRows(this.selected);
            }
            Render2D.rect(ctx, x, y, w, 0.8f, Paint.horizontal(x, x + w, t.accent2(), ColorUtil.withAlpha(t.accent(), 0.0f)));
            y += 6.0f;
            for (Widget widget : this.rows) {
                if (!widget.isVisible()) continue;
                float rh = widget.layout(x, y, w);
                widget.render(this.ui);
                y += rh + 3.0f;
            }
        } else if ("menu".equals(this.target)) {
            if (this.rowsFor != null || this.rows.isEmpty()) {
                this.buildGlobalRows();
            }
            for (Widget widget : this.rows) {
                if (!widget.isVisible()) continue;
                float rh = widget.layout(x, y, w);
                widget.render(this.ui);
                y += rh + 3.0f;
            }
        }
        y += 8.0f;
        for (String h : hints = new String[]{"\u041b\u041a\u041c \u2014 \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u0440\u0430\u0437\u0434\u0435\u043b; 2\u00d7 \u2014 \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u044d\u043b\u0435\u043c\u0435\u043d\u0442", "3\u00d7 \u043a\u043b\u0438\u043a \u2014 \u0441\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0443", "\u0422\u044f\u043d\u0443\u0442\u044c \u2014 \u0434\u0432\u0438\u0433\u0430\u0442\u044c, \u0443\u0433\u043b\u044b \u2014 \u0440\u0430\u0437\u043c\u0435\u0440", "Shift \u2014 \u0431\u0435\u0437 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u044f \u043f\u0440\u043e\u043f\u043e\u0440\u0446\u0438\u0439", "\u041a\u0440\u0443\u0436\u043e\u043a \u0441\u0432\u0435\u0440\u0445\u0443 \u2014 \u043f\u043e\u0432\u043e\u0440\u043e\u0442", "\u041a\u043e\u043b\u0451\u0441\u0438\u043a\u043e \u2014 \u0440\u0430\u0437\u043c\u0435\u0440, \u0441\u0442\u0440\u0435\u043b\u043a\u0438 \u2014 \u0441\u0434\u0432\u0438\u0433", "Ctrl+D \u043a\u043e\u043f\u0438\u044f, Del \u0443\u0434\u0430\u043b\u0438\u0442\u044c, Ctrl+Z", "Tab \u2014 \u0441\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043d\u0435\u043b\u044c, Esc \u2014 \u0432\u044b\u0445\u043e\u0434"}) {
            Fonts.draw(ctx, h, x, y, 5.9f, t.textMuted(), false);
            y += 8.5f;
        }
        float f = y + 6.0f;
        this.ui.my = my;
        m.popMatrix();
        this.scroll = Math.max(0.0f, Math.min(this.scroll, f - (float)this.height));
    }

    private void tab(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, String label, String tgt) {
        boolean active = this.target.equals(tgt);
        boolean hov = this.ui.hover(x, y, w, 17.0f);
        Render2D.roundRect(ctx, x, y, w, 17.0f, 5.0f, active ? Paint.horizontal(x, x + w, t.accent(), ColorUtil.lerp(t.accent(), t.accent2(), 0.7f)) : Paint.solid(ColorUtil.withAlpha(t.card(), hov ? 1.0f : 0.7f)));
        Fonts.drawCentered(ctx, label, x + w / 2.0f, y + 5.0f, 7.0f, active ? -1 : t.textDim(), active);
        this.buttons.add(new float[]{x, y, w, 17.0f, "menu".equals(tgt) ? 100.0f : 101.0f});
    }

    private void button(GuiGraphicsExtractor ctx, Theme t, int id, float x, float y, float w, String label, Icon icon, boolean primary) {
        float hv = this.btnHover[id].update(this.ui.hover(x, y, w, 17.0f) ? 1.0f : 0.0f, this.ui.dt);
        UiDraw.button(ctx, t, x, y, w, 17.0f, label, icon, hv, primary);
        this.buttons.add(new float[]{x, y, w, 17.0f, id});
    }

    private void renderToast(GuiGraphicsExtractor ctx, Theme t) {
        if (this.toast == null) {
            return;
        }
        long age = System.currentTimeMillis() - this.toastAt;
        if (age > 2200L) {
            this.toast = null;
            return;
        }
        float a = Math.min(1.0f, Math.min((float)age / 150.0f, (float)(2200L - age) / 300.0f));
        float tw = Fonts.width(this.toast, 7.5f, true) + 20.0f;
        float x = ((float)this.width - this.panelW() - tw) / 2.0f;
        float y = this.height - 34;
        Render2D.roundRect(ctx, x, y, tw, 18.0f, 9.0f, ColorUtil.withAlpha(t.bg2(), 0.95f * a));
        Render2D.outline(ctx, x, y, tw, 18.0f, 9.0f, 0.8f, ColorUtil.withAlpha(t.accent2(), 0.8f * a));
        Fonts.drawCentered(ctx, this.toast, x + tw / 2.0f, y + 5.5f, 7.5f, ColorUtil.withAlpha(t.text(), a), true);
    }

    private void buildRows(DecorLayer l) {
        this.rowsFor = l;
        this.rows.clear();
        ArrayList<Setting<?>> s = new ArrayList<>();
        s.add(new ModeSetting("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", l.image, l.image).dynamic(DecorManager::images).bind(() -> l.image, v -> {
            l.image = v;
        }));
        s.add(DecorEditorScreen.num("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 0.0, 100.0, 5.0, "%", () -> Float.valueOf(l.opacity), v -> {
            l.opacity = v.floatValue();
        }));
        s.add(DecorEditorScreen.num("\u041f\u043e\u0432\u043e\u0440\u043e\u0442", -180.0, 180.0, 1.0, "\u00b0", () -> Float.valueOf(l.rotation), v -> {
            l.rotation = v.floatValue();
        }));
        s.add(DecorEditorScreen.num("\u0421\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u0435", 0.0, 60.0, 1.0, "", () -> Float.valueOf(l.radius), v -> {
            l.radius = v.floatValue();
        }));
        s.add(new ModeSetting("\u0412\u043f\u0438\u0441\u0430\u0442\u044c", "\u0420\u0430\u0441\u0442\u044f\u043d\u0443\u0442\u044c", "\u0420\u0430\u0441\u0442\u044f\u043d\u0443\u0442\u044c", "\u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u044c (\u043e\u0431\u0440\u0435\u0437\u0430\u0442\u044c)").bind(() -> l.fit, v -> {
            l.fit = v;
        }));
        s.add(l.isMenu() ? new ModeSetting("\u0413\u043b\u0443\u0431\u0438\u043d\u0430", "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e", "\u041f\u043e\u0434 \u043e\u043a\u043d\u043e\u043c", "\u0424\u043e\u043d \u043e\u043a\u043d\u0430", "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e").bind(() -> l.depth, v -> {
            l.depth = v;
        }) : new ModeSetting("\u0413\u043b\u0443\u0431\u0438\u043d\u0430", "\u041f\u043e\u0432\u0435\u0440\u0445 HUD", "\u041f\u043e\u0434 HUD", "\u041f\u043e\u0432\u0435\u0440\u0445 HUD").bind(() -> l.depth, v -> {
            l.depth = v;
        }));
        s.add(new BooleanSetting("\u041d\u0435\u043e\u043d\u043e\u0432\u043e\u0435 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435", false).bind(() -> l.glow, v -> {
            l.glow = v;
        }));
        s.add(DecorEditorScreen.num("\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", 0.0, 100.0, 5.0, "%", () -> Float.valueOf(l.tint), v -> {
            l.tint = v.floatValue();
        }));
        s.add(DecorEditorScreen.num("\u0417\u0430\u0442\u0443\u0445\u0430\u043d\u0438\u0435 \u0441\u0432\u0435\u0440\u0445\u0443", 0.0, 100.0, 5.0, "%", () -> Float.valueOf(l.fadeTop), v -> {
            l.fadeTop = v.floatValue();
        }));
        s.add(new ModeSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f", "\u041d\u0435\u0442", DecorLayer.ANIMS).bind(() -> l.animation, v -> {
            l.animation = v;
        }));
        s.add(DecorEditorScreen.num("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", 10.0, 300.0, 10.0, "%", () -> Float.valueOf(l.animSpeed), v -> {
            l.animSpeed = v.floatValue();
        }).visibleWhen(() -> !"\u041d\u0435\u0442".equals(l.animation)));
        s.add(new BooleanSetting("\u041e\u0442\u0440\u0430\u0437\u0438\u0442\u044c \u2194", false).bind(() -> l.flipX, v -> {
            l.flipX = v;
        }));
        s.add(new BooleanSetting("\u041e\u0442\u0440\u0430\u0437\u0438\u0442\u044c \u2195", false).bind(() -> l.flipY, v -> {
            l.flipY = v;
        }));
        s.add(new ButtonSetting("\u041f\u0440\u043e\u043f\u043e\u0440\u0446\u0438\u0438", "\u0418\u0441\u0445\u043e\u0434\u043d\u044b\u0435 \u043f\u0440\u043e\u043f\u043e\u0440\u0446\u0438\u0438 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438", () -> {
            this.pushUndo();
            DecorManager.Tex tex = DecorManager.texture(l.image);
            if (tex != null) {
                l.w = l.h * tex.aspect();
            }
        }));
        s.add(new ButtonSetting("\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443", () -> {
            this.pushUndo();
            l.x = 0.5f;
            l.y = l.isMenu() ? 300.0f : 0.5f;
        }));
        s.add(new ButtonSetting("\u041f\u043e\u0440\u044f\u0434\u043e\u043a", "\u0412\u044b\u0448\u0435 \u0432\u0441\u0435\u0445", () -> {
            this.pushUndo();
            DecorManager.layers().remove(l);
            DecorManager.layers().add(l);
        }));
        s.add(new ButtonSetting("\u041f\u043e\u0440\u044f\u0434\u043e\u043a ", "\u041d\u0438\u0436\u0435 \u0432\u0441\u0435\u0445", () -> {
            this.pushUndo();
            DecorManager.layers().remove(l);
            DecorManager.layers().add(0, l);
        }));
        s.add(new ButtonSetting("\u041a\u043e\u043f\u0438\u044f", "\u0414\u0443\u0431\u043b\u0438\u0440\u043e\u0432\u0430\u0442\u044c (Ctrl+D)", this::duplicate));
        s.add(new ButtonSetting("\u0423\u0434\u0430\u043b\u0438\u0442\u044c", "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u043b\u043e\u0439 (Del)", this::deleteSelected));
        for (Setting setting : s) {
            this.rows.add(SettingRows.create(setting, this));
        }
    }

    private void buildElementRows(String id) {
        boolean movable;
        this.rowsForElement = id;
        this.rowsFor = null;
        this.rows.clear();
        GuiStyle.Element info = GuiStyle.ELEMENTS.get(id);
        GuiStyle.Override o = GuiStyle.edit(id);
        ArrayList<Setting> s = new ArrayList<Setting>();
        boolean bl = movable = info == null || info.movable();
        if (movable) {
            s.add(DecorEditorScreen.num("\u0421\u0434\u0432\u0438\u0433 X", -600.0, 600.0, 1.0, "", () -> Float.valueOf(o.dx), v -> {
                o.dx = v.floatValue();
            }));
            s.add(DecorEditorScreen.num("\u0421\u0434\u0432\u0438\u0433 Y", -400.0, 400.0, 1.0, "", () -> Float.valueOf(o.dy), v -> {
                o.dy = v.floatValue();
            }));
            s.add(DecorEditorScreen.num("\u041c\u0430\u0441\u0448\u0442\u0430\u0431", 20.0, 400.0, 5.0, "%", () -> Float.valueOf(o.scale), v -> {
                o.scale = v.floatValue();
            }));
        }
        s.add(DecorEditorScreen.num("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 0.0, 100.0, 5.0, "%", () -> Float.valueOf(o.opacity), v -> {
            o.opacity = v.floatValue();
        }));
        if (movable) {
            s.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c", false).bind(() -> o.hidden, v -> {
                o.hidden = v;
            }));
        }
        s.add(new BooleanSetting("\u0421\u0432\u043e\u0438 \u0446\u0432\u0435\u0442\u0430", false).bind(() -> o.customColor, v -> {
            o.customColor = v;
        }));
        s.add((Setting)(new ColorSetting("\u0426\u0432\u0435\u0442 1", o.color).bind(() -> o.color, v -> {
            o.color = v;
        })).visibleWhen(() -> o.customColor));
        s.add((Setting)(new ColorSetting("\u0426\u0432\u0435\u0442 2", o.color2).bind(() -> o.color2, v -> {
            o.color2 = v;
        })).visibleWhen(() -> o.customColor));
        if (id.equals("style.card") || id.equals("style.button") || id.equals("style.dropdown")) {
            s.add((Setting)DecorEditorScreen.num("\u0421\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u0435", -1.0, 30.0, 1.0, "", () -> Float.valueOf(o.radius), v -> {
                o.radius = v.floatValue();
            }).desc("\u22121 \u2014 \u043a\u0430\u043a \u0431\u044b\u043b\u043e"));
        }
        if (info != null && info.image()) {
            List<String> base = List.of("\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f");
            s.add(new ModeSetting("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f", "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f").dynamic(() -> {
                ArrayList<String> l = new ArrayList<String>(base);
                l.addAll(DecorManager.images());
                return l;
            }).bind(() -> o.image.isEmpty() ? "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f" : o.image, v -> {
                o.image = "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f".equals(v) ? "" : v;
            }));
        }
        s.add(new ButtonSetting("\u0421\u0431\u0440\u043e\u0441", "\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u043a\u0430\u043a \u0431\u044b\u043b\u043e", () -> {
            this.pushUndo();
            GuiStyle.reset(id);
            this.rowsForElement = null;
            this.toast("\u00ab" + (info != null ? info.label() : id) + "\u00bb \u2014 \u043a\u0430\u043a \u0431\u044b\u043b\u043e");
        }));
        for (Setting st : s) {
            this.rows.add(SettingRows.create(st, this));
        }
    }

    private void selectElement(String id) {
        this.element = id;
        this.selected = null;
        this.rowsForElement = null;
        this.rowsFor = null;
        this.rows.clear();
        GuiStyle.Element info = GuiStyle.ELEMENTS.get(id);
        this.toast("\u0412\u044b\u0431\u0440\u0430\u043d\u043e: " + (info != null ? info.label() : id) + (info != null && info.image() ? " \u2014 3\u00d7 \u043a\u043b\u0438\u043a: \u0441\u043c\u0435\u043d\u0438\u0442\u044c \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0443" : ""));
    }

    private void pickImage(double mx, double my) {
        Consumer<String> apply;
        ArrayList<String> options = new ArrayList<String>();
        if (this.selected != null) {
            DecorLayer l = this.selected;
            options.addAll(DecorManager.images());
            apply = v -> {
                this.pushUndo();
                l.image = v;
            };
        } else if (this.element != null && GuiStyle.ELEMENTS.get(this.element) != null && GuiStyle.ELEMENTS.get(this.element).image()) {
            String id = this.element;
            options.add("\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f");
            options.addAll(DecorManager.images());
            apply = v -> {
                this.pushUndo();
                GuiStyle.edit((String)id).image = "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u0430\u044f".equals(v) ? "" : v;
                this.rowsForElement = null;
            };
        } else {
            this.toast("\u0423 \u044d\u0442\u043e\u0433\u043e \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430 \u043d\u0435\u0442 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438 \u2014 \u0434\u0432\u043e\u0439\u043d\u043e\u0439 \u043a\u043b\u0438\u043a \u043f\u043e \u043b\u043e\u0433\u043e\u0442\u0438\u043f\u0443 \u0438\u043b\u0438 \u0444\u043e\u043d\u0443 \u043e\u043a\u043d\u0430");
            return;
        }
        this.openPopup(new DropdownPopup((float)mx, (float)my, 170.0f, options, () -> "", v -> {
            apply.accept((String)v);
            this.closePopup();
        }, this::closePopup));
        this.popup.fit(this.width, this.height);
    }

    private void buildGlobalRows() {
        this.rowsFor = null;
        this.rows.clear();
        ClientSettings cs = ClientSettings.get();
        if (cs == null) {
            return;
        }
        this.rows.add(SettingRows.create(cs.sidebarArt, this));
        this.rows.add(SettingRows.create(cs.artBackground, this));
        this.rows.add(SettingRows.create(cs.artBackgroundThemed, this));
        this.rows.add(SettingRows.create(cs.artBackgroundAlpha, this));
        this.rows.add(SettingRows.create(cs.petals, this));
    }

    private static NumberSetting num(String name, double min, double max, double step, String unit, Supplier<Float> get, Consumer<Float> set) {
        return (NumberSetting)new NumberSetting(name, get.get().floatValue(), min, max, step).unit(unit).bind(() -> get.get().doubleValue(), v -> set.accept(Float.valueOf(v.floatValue())));
    }

    private void pushUndo() {
        this.undo.push(DecorManager.toJson() + "\u0001" + GuiStyle.toJson());
        while (this.undo.size() > 60) {
            this.undo.removeLast();
        }
    }

    private void undo() {
        if (this.undo.isEmpty()) {
            return;
        }
        int idx = this.selected != null ? DecorManager.layers().indexOf(this.selected) : -1;
        String[] parts = this.undo.pop().split("\u0001", 2);
        DecorManager.fromJson(parts[0]);
        if (parts.length > 1) {
            GuiStyle.fromJson(parts[1]);
        }
        this.rowsForElement = null;
        this.selected = idx >= 0 && idx < DecorManager.layers().size() ? DecorManager.layers().get(idx) : null;
        this.rowsFor = null;
        this.rows.clear();
        this.toast("\u041e\u0442\u043c\u0435\u043d\u0435\u043d\u043e");
    }

    private void addImage(String image) {
        float aspect;
        this.pushUndo();
        DecorLayer l = new DecorLayer();
        l.image = image;
        l.target = this.target;
        DecorManager.Tex tex = DecorManager.texture(image);
        float f = aspect = tex != null ? tex.aspect() : 1.0f;
        if (l.isMenu()) {
            l.depth = "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e";
            l.h = 220.0f;
            l.x = 0.5f;
            l.y = 300.0f;
        } else {
            l.depth = "\u041f\u043e\u0432\u0435\u0440\u0445 HUD";
            l.h = 0.3f;
            l.x = 0.5f;
            l.y = 0.5f;
        }
        l.w = l.h * aspect;
        DecorManager.layers().add(l);
        this.selected = l;
        this.toast("\u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u043e: " + image);
    }

    private void convertSidebarArt() {
        String image = ArtManager.sidebarImageForDecor();
        if (image == null) {
            return;
        }
        this.pushUndo();
        DecorManager.Tex tex = DecorManager.texture(image);
        float aspect = tex != null ? tex.aspect() : 0.66f;
        float dw = this.preview.layoutDesignWidth();
        float H = 600.0f;
        float w = 192.0f;
        float h = w / aspect;
        float maxH = H * 0.62f;
        if (h > maxH) {
            h = maxH;
            w = h * aspect;
        }
        float bottom = H - 92.0f;
        DecorLayer l = new DecorLayer();
        l.image = image;
        l.target = "menu";
        l.depth = "\u0424\u043e\u043d \u043e\u043a\u043d\u0430";
        l.w = w;
        l.h = h;
        l.x = 98.0f / dw;
        l.y = bottom - h / 2.0f;
        l.opacity = 85.0f;
        l.fadeTop = 45.0f;
        DecorManager.layers().add(0, l);
        ClientSettings cs = ClientSettings.get();
        if (cs != null) {
            cs.sidebarArt.set("\u0411\u0435\u0437 \u0430\u0440\u0442\u0430");
        }
        ArtManager.reload();
        this.selected = l;
        this.rowsFor = null;
        this.rows.clear();
        this.toast("\u0410\u0440\u0442 \u043f\u0430\u043d\u0435\u043b\u0438 \u0442\u0435\u043f\u0435\u0440\u044c \u0441\u043b\u043e\u0439 \u2014 \u0434\u0432\u0438\u0433\u0430\u0439\u0442\u0435 \u0438 \u0440\u0430\u0441\u0442\u044f\u0433\u0438\u0432\u0430\u0439\u0442\u0435");
    }

    private void duplicate() {
        if (this.selected == null) {
            return;
        }
        this.pushUndo();
        DecorLayer c = this.selected.copy();
        if (c.isMenu()) {
            c.x += 0.03f;
            c.y += 18.0f;
        } else {
            c.x += 0.03f;
            c.y += 0.03f;
        }
        DecorManager.layers().add(c);
        this.selected = c;
    }

    private void deleteSelected() {
        if (this.selected == null) {
            return;
        }
        this.pushUndo();
        DecorManager.layers().remove(this.selected);
        this.selected = null;
        this.rowsFor = null;
        this.rows.clear();
        this.toast("\u0421\u043b\u043e\u0439 \u0443\u0434\u0430\u043b\u0451\u043d (Ctrl+Z \u2014 \u0432\u0435\u0440\u043d\u0443\u0442\u044c)");
    }

    private boolean overPanel(double mx) {
        return this.panelShown && mx >= (double)((float)this.width - this.panelW());
    }

    public boolean mouseClicked(double mx, double my, int button) {
        float[] view;
        if (this.popup != null) {
            if (this.popup.mouseClicked(this.ui, mx, my, button)) {
                return true;
            }
            this.closePopup();
            return true;
        }
        if (this.overPanel(mx)) {
            return this.panelClick(mx, my + (double)this.scroll, button);
        }
        if (button != 0) {
            return false;
        }
        long now = System.currentTimeMillis();
        this.clickCount = now - this.lastClickAt < 380L && Math.hypot(mx - this.lastClickX, my - this.lastClickY) < 6.0 ? this.clickCount + 1 : 1;
        this.lastClickAt = now;
        this.lastClickX = mx;
        this.lastClickY = my;
        if (this.clickCount == 1 && "menu".equals(this.target) && this.preview.navigatePreview(mx, my, (view = this.menuView())[0], view[1], view[2])) {
            this.selected = null;
            this.element = null;
            this.rowsFor = null;
            this.rowsForElement = null;
            this.scroll = 0.0f;
            this.drag = Drag.NONE;
            return true;
        }
        if (this.clickCount >= 3) {
            this.pickImage(mx, my);
            return true;
        }
        if (this.clickCount == 2 && "menu".equals(this.target) && (this.selected == null || !this.layerAt(mx, my, this.selected))) {
            String found = null;
            for (GuiStyle.Hit h : GuiStyle.HITS) {
                if (!h.contains(mx, my)) continue;
                found = h.id();
            }
            if (found != null) {
                this.selectElement(found);
                return true;
            }
        }
        if (this.element != null && "menu".equals(this.target)) {
            GuiStyle.Element info = GuiStyle.ELEMENTS.get(this.element);
            List<GuiStyle.Hit> hits = this.elementHits(this.element);
            if (info != null && info.movable() && !hits.isEmpty()) {
                GuiStyle.Hit h;
                h = hits.get(0);
                GuiStyle.Override o = GuiStyle.edit(this.element);
                boolean corner = false;
                for (float[] c : new float[][]{{h.x0(), h.y0()}, {h.x1(), h.y0()}, {h.x0(), h.y1()}, {h.x1(), h.y1()}}) {
                    if (!(Math.abs(mx - (double)c[0]) <= 5.5) || !(Math.abs(my - (double)c[1]) <= 5.5)) continue;
                    corner = true;
                }
                if (corner || h.contains(mx, my)) {
                    this.pushUndo();
                    this.drag = corner ? Drag.ELEMENT_SCALE : Drag.ELEMENT_MOVE;
                    this.startMx = (float)mx;
                    this.startMy = (float)my;
                    this.startDx = o.dx;
                    this.startDy = o.dy;
                    this.startScale = o.scale;
                    this.startGeom = new float[]{(h.x0() + h.x1()) / 2.0f, (h.y0() + h.y1()) / 2.0f, h.x1() - h.x0(), h.y1() - h.y0()};
                    return true;
                }
            }
            if (this.clickCount == 1) {
                this.element = null;
            }
        }
        if (this.selected != null && this.selected.target.equals(this.target)) {
            float[] g = this.geom(this.selected);
            float[] lp = DecorEditorScreen.toLocal(g, this.selected.rotation, (float)mx, (float)my);
            if (Math.hypot(lp[0], lp[1] - (-g[3] / 2.0f - 16.0f)) <= 6.0) {
                this.startDrag(Drag.ROTATE, mx, my, g);
                return true;
            }
            for (int hx = -1; hx <= 1; ++hx) {
                for (int hy = -1; hy <= 1; ++hy) {
                    if (hx == 0 && hy == 0 || !(Math.abs(lp[0] - (float)hx * g[2] / 2.0f) <= 5.5f) || !(Math.abs(lp[1] - (float)hy * g[3] / 2.0f) <= 5.5f)) continue;
                    this.handleX = hx;
                    this.handleY = hy;
                    this.startDrag(Drag.RESIZE, mx, my, g);
                    return true;
                }
            }
        }
        List<DecorLayer> list = this.current();
        for (int i = list.size() - 1; i >= 0; --i) {
            float[] g;
            float[] lp;
            DecorLayer l = list.get(i);
            if (!l.visible || !(Math.abs((lp = DecorEditorScreen.toLocal(g = this.geom(l), l.rotation, (float)mx, (float)my))[0]) <= g[2] / 2.0f) || !(Math.abs(lp[1]) <= g[3] / 2.0f)) continue;
            this.selected = l;
            this.element = null;
            this.startDrag(Drag.MOVE, mx, my, g);
            return true;
        }
        this.selected = null;
        return true;
    }

    private boolean layerAt(double mx, double my, DecorLayer l) {
        float[] g = this.geom(l);
        float[] lp = DecorEditorScreen.toLocal(g, l.rotation, (float)mx, (float)my);
        return Math.abs(lp[0]) <= g[2] / 2.0f && Math.abs(lp[1]) <= g[3] / 2.0f;
    }

    private void startDrag(Drag d, double mx, double my, float[] g) {
        this.pushUndo();
        this.drag = d;
        this.startMx = (float)mx;
        this.startMy = (float)my;
        this.startGeom = (float[])g.clone();
        this.startRot = this.selected.rotation;
    }

    private boolean panelClick(double mx, double my, int button) {
        for (float[] b : this.buttons) {
            if (!(mx >= (double)b[0]) || !(mx < (double)(b[0] + b[2])) || !(my >= (double)b[1]) || !(my < (double)(b[1] + b[3]))) continue;
            int id = (int)b[4];
            switch (id) {
                case 100: 
                case 101: {
                    this.target = id == 100 ? "menu" : "screen";
                    this.selected = null;
                    this.rowsFor = null;
                    this.rows.clear();
                    break;
                }
                case 0: {
                    List<String> images = DecorManager.images();
                    this.openPopup(new DropdownPopup(b[0], b[1] + b[3] + 2.0f - this.scroll, Math.max(b[2], 150.0f), images, () -> "", this::addImage, this::closePopup));
                    this.popup.fit(this.width, this.height);
                    break;
                }
                case 1: {
                    ArtManager.artDir().toFile().mkdirs();
                    Util.getPlatform().openFile(ArtManager.artDir().toFile());
                    break;
                }
                case 200: {
                    this.convertSidebarArt();
                    break;
                }
                case 300: {
                    this.elementsOpen = !this.elementsOpen;
                    break;
                }
                default: {
                    ArrayList<String> ids;
                    if (id < 400 || id - 400 >= (ids = new ArrayList<String>(GuiStyle.ELEMENTS.keySet())).size()) break;
                    this.selectElement((String)ids.get(id - 400));
                }
            }
            return true;
        }
        for (float[] r : this.layerRows) {
            if (!(mx >= (double)r[0]) || !(mx < (double)(r[0] + r[2])) || !(my >= (double)r[1]) || !(my < (double)(r[1] + r[3]))) continue;
            DecorLayer l = DecorManager.layers().get((int)r[4]);
            if (mx >= (double)(r[0] + r[2] - 16.0f)) {
                this.pushUndo();
                l.visible = !l.visible;
            } else {
                this.selected = l;
                this.element = null;
            }
            return true;
        }
        Iterator<Widget> iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            Widget r = iterator.next();
            if (!r.isVisible() || !r.contains(mx, my)) continue;
            this.pushUndo();
            if (!r.mouseClicked(this.ui, mx, my, button)) continue;
            return true;
        }
        return true;
    }

    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (this.popup != null && this.popup.mouseDragged(this.ui, mx, my, button)) {
            return true;
        }
        if (this.drag == Drag.ELEMENT_MOVE || this.drag == Drag.ELEMENT_SCALE) {
            GuiStyle.Override o = GuiStyle.edit(this.element);
            float s = this.menuView()[2];
            if (this.drag == Drag.ELEMENT_MOVE) {
                o.dx = this.startDx + ((float)mx - this.startMx) / s;
                o.dy = this.startDy + ((float)my - this.startMy) / s;
                if (this.minecraft.hasShiftDown()) {
                    if (Math.abs(o.dx - this.startDx) > Math.abs(o.dy - this.startDy)) {
                        o.dy = this.startDy;
                    } else {
                        o.dx = this.startDx;
                    }
                }
            } else {
                float d0 = (float)Math.hypot(this.startMx - this.startGeom[0], this.startMy - this.startGeom[1]);
                float d1 = (float)Math.hypot(mx - (double)this.startGeom[0], my - (double)this.startGeom[1]);
                o.scale = Math.max(20.0f, Math.min(400.0f, this.startScale * d1 / Math.max(1.0f, d0)));
            }
            return true;
        }
        if (this.drag == Drag.NONE || this.selected == null) {
            for (Widget r : this.rows) {
                if (!r.isVisible() || !r.mouseDragged(this.ui, mx, my + (double)this.scroll, button)) continue;
                return true;
            }
            return false;
        }
        float[] g = this.startGeom;
        boolean shift = this.minecraft.hasShiftDown();
        switch (this.drag.ordinal()) {
            case 1: {
                float cx = g[0] + (float)mx - this.startMx;
                float cy = g[1] + (float)my - this.startMy;
                float[] center = this.canvasCenter();
                this.snapX = Math.abs(cx - center[0]) < 4.0f;
                boolean bl = this.snapY = Math.abs(cy - center[1]) < 4.0f;
                if (this.snapX) {
                    cx = center[0];
                }
                if (this.snapY) {
                    cy = center[1];
                }
                this.setGeom(this.selected, cx, cy, g[2], g[3]);
                break;
            }
            case 3: {
                float a = (float)Math.toDegrees(Math.atan2(my - (double)g[1], mx - (double)g[0])) + 90.0f;
                if (shift) {
                    a = (float)Math.round(a / 15.0f) * 15.0f;
                }
                this.selected.rotation = a = ((a + 180.0f) % 360.0f + 360.0f) % 360.0f - 180.0f;
                break;
            }
            case 2: {
                this.resize((float)mx, (float)my, g, shift);
                break;
            }
        }
        return true;
    }

    private void resize(float mx, float my, float[] g, boolean free) {
        float nh;
        float[] lp = DecorEditorScreen.toLocal(g, this.startRot, mx, my);
        float w = g[2];
        float h = g[3];
        float oppX = (float)(-this.handleX) * w / 2.0f;
        float oppY = (float)(-this.handleY) * h / 2.0f;
        float nw = this.handleX != 0 ? Math.max(6.0f, Math.abs(lp[0] - oppX)) : w;
        float f = nh = this.handleY != 0 ? Math.max(6.0f, Math.abs(lp[1] - oppY)) : h;
        if (this.handleX != 0 && this.handleY != 0 && !free) {
            float k = Math.max(nw / w, nh / h);
            nw = w * k;
            nh = h * k;
        }
        float lcx = this.handleX != 0 ? oppX + (float)this.handleX * nw / 2.0f : 0.0f;
        float lcy = this.handleY != 0 ? oppY + (float)this.handleY * nh / 2.0f : 0.0f;
        float[] c = DecorEditorScreen.toScreen(g, this.startRot, lcx, lcy);
        this.setGeom(this.selected, c[0], c[1], nw, nh);
    }

    private float[] canvasCenter() {
        if ("menu".equals(this.target)) {
            float[] v = this.menuView();
            return new float[]{v[0] + this.preview.layoutDesignWidth() * v[2] / 2.0f, v[1] + 600.0f * v[2] / 2.0f};
        }
        return new float[]{(float)this.width / 2.0f, (float)this.height / 2.0f};
    }

    public boolean mouseReleased(double mx, double my, int button) {
        if (this.popup != null) {
            this.popup.mouseReleased(this.ui, mx, my, button);
        }
        for (Widget r : this.rows) {
            if (!r.isVisible()) continue;
            r.mouseReleased(this.ui, mx, my + (double)this.scroll, button);
        }
        if (this.drag != Drag.NONE) {
            this.drag = Drag.NONE;
            this.snapY = false;
            this.snapX = false;
            DecorManager.save();
            GuiStyle.save();
        }
        return true;
    }

    public boolean mouseScrolled(double mx, double my, double h, double v) {
        GuiStyle.Element info;
        if (this.popup != null && this.popup.mouseScrolled(mx, my, v)) {
            return true;
        }
        if (this.overPanel(mx)) {
            this.scroll = Math.max(0.0f, this.scroll - (float)v * 20.0f);
            return true;
        }
        if (this.element != null && "menu".equals(this.target) && this.selected == null && (info = GuiStyle.ELEMENTS.get(this.element)) != null && info.movable()) {
            this.pushUndo();
            GuiStyle.Override o = GuiStyle.edit(this.element);
            o.scale = Math.max(20.0f, Math.min(400.0f, o.scale + (float)(v > 0.0 ? 5 : -5)));
            return true;
        }
        if (this.selected != null && this.selected.target.equals(this.target)) {
            this.pushUndo();
            float k = v > 0.0 ? 1.06f : 0.9433963f;
            float[] g = this.geom(this.selected);
            this.setGeom(this.selected, g[0], g[1], g[2] * k, g[3] * k);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int key, int scan, int mods) {
        if (this.focus != null && this.focus.keyPressed(key, scan, mods)) {
            return true;
        }
        boolean ctrl = (mods & 2) != 0;
        boolean shift = (mods & 1) != 0;
        switch (key) {
            case 256: {
                if (this.popup != null) {
                    this.closePopup();
                } else {
                    this.onClose();
                }
                return true;
            }
            case 258: {
                this.panelShown = !this.panelShown;
                return true;
            }
            case 259: 
            case 261: {
                if (this.selected == null && this.element != null) {
                    GuiStyle.Element info = GuiStyle.ELEMENTS.get(this.element);
                    if (info != null && info.movable()) {
                        this.pushUndo();
                        GuiStyle.Override o = GuiStyle.edit(this.element);
                        o.hidden = !o.hidden;
                        this.toast(o.hidden ? "\u0421\u043a\u0440\u044b\u0442\u043e (Del \u2014 \u0432\u0435\u0440\u043d\u0443\u0442\u044c)" : "\u0421\u043d\u043e\u0432\u0430 \u0432\u0438\u0434\u043d\u043e");
                    }
                    return true;
                }
                this.deleteSelected();
                return true;
            }
            case 90: {
                if (ctrl) {
                    this.undo();
                }
                return true;
            }
            case 68: {
                if (ctrl) {
                    this.duplicate();
                }
                return true;
            }
            case 262: 
            case 263: 
            case 264: 
            case 265: {
                float ddx;
                if (this.selected == null && this.element != null) {
                    float st;
                    this.pushUndo();
                    GuiStyle.Override o = GuiStyle.edit(this.element);
                    float f = st = shift ? 10.0f : 1.0f;
                    if (key == 263) {
                        o.dx -= st;
                    }
                    if (key == 262) {
                        o.dx += st;
                    }
                    if (key == 265) {
                        o.dy -= st;
                    }
                    if (key == 264) {
                        o.dy += st;
                    }
                    return true;
                }
                if (this.selected == null) {
                    return true;
                }
                this.pushUndo();
                float step = shift ? 10.0f : 1.0f;
                float[] g = this.geom(this.selected);
                float f = key == 263 ? -step : (ddx = key == 262 ? step : 0.0f);
                float ddy = key == 265 ? -step : (key == 264 ? step : 0.0f);
                this.setGeom(this.selected, g[0] + ddx, g[1] + ddy, g[2], g[3]);
                return true;
            }
        }
        return super.keyPressed(new KeyEvent(key, scan, mods));
    }

    public boolean charTyped(char chr, int modifiers) {
        return this.focus != null && this.focus.charTyped(chr, modifiers);
    }

    public void devSelect(DecorLayer l, String tgt) {
        this.target = tgt;
        this.selected = l;
        this.rowsFor = null;
        this.rows.clear();
    }

    public void devClick(double x, double y) {
        this.mouseClicked(x, y, 0);
        this.mouseReleased(x, y, 0);
    }

    public String devElement() {
        return this.element;
    }

    public void devConvertSidebar() {
        this.convertSidebarArt();
    }

    public DecorLayer devSelected() {
        return this.selected;
    }

    public float[] devGeom(DecorLayer l) {
        return this.geom(l);
    }

    @Override
    public void openPopup(Popup p) {
        this.popup = p;
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
    public void toast(String message) {
        this.toast = message;
        this.toastAt = System.currentTimeMillis();
    }

    @Override
    public void setFocus(Widget widget) {
        this.focus = widget;
    }

    @Override
    public Widget getFocus() {
        return this.focus;
    }

    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        return this.mouseClicked(click.x(), click.y(), click.button());
    }

    public boolean mouseReleased(MouseButtonEvent click) {
        return this.mouseReleased(click.x(), click.y(), click.button());
    }

    public boolean mouseDragged(MouseButtonEvent click, double dx, double dy) {
        return this.mouseDragged(click.x(), click.y(), click.button(), dx, dy);
    }

    public boolean keyPressed(KeyEvent input) {
        return this.keyPressed(input.key(), input.scancode(), input.modifiers());
    }

    public boolean charTyped(CharacterEvent input) {
        boolean handled = false;
        for (char ch : input.codepointAsString().toCharArray()) {
            handled |= this.charTyped(ch, 0);
        }
        return handled;
    }

    private static enum Drag {
        NONE,
        MOVE,
        RESIZE,
        ROTATE,
        ELEMENT_MOVE,
        ELEMENT_SCALE;

    }
}

