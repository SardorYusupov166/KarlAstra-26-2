/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Util
 *  org.joml.Matrix3x2fStack
 */
package astra.gui.screens;

import astra.AstraClient;
import astra.animation.Animation;
import astra.animation.AnimationUtils;
import astra.config.ConfigManager;
import astra.decor.DecorManager;
import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.DropdownPopup;
import astra.gui.components.ModuleCard;
import astra.gui.components.Popup;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.gui.pages.Page;
import astra.gui.pages.Pages;
import astra.gui.screens.HudEditorScreen;
import astra.gui.style.GuiStyle;
import astra.module.Category;
import astra.module.ClientSettings;
import astra.module.modules.cosmetic.SoundsModule;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Decorations;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;

public class ClickGuiScreen
extends Screen
implements PopupHost {
    public static final float DESIGN_H = 600.0f;
    public static final float SIDEBAR_W = 196.0f;
    private static Category lastCategory = Category.VISUAL;
    private final Screen parent;
    private final UiContext ui = new UiContext();
    private final Map<Category, Page> pages = new EnumMap<Category, Page>(Category.class);
    private final Map<Category, Animation> itemHover = new EnumMap<Category, Animation>(Category.class);
    private final Animation selector = new Animation(-1.0f, 14.0f);
    private final Animation contentFade = new Animation(1.0f, 9.0f);
    private final Animation closeHover = new Animation(0.0f, 16.0f);
    private final Animation[] configHover = new Animation[]{new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f)};
    private final Decorations petals = new Decorations(26);
    private Category category;
    private Popup popup;
    private Widget focus;
    private String toast;
    private long toastAt;
    private long lastFrame;
    private float openProgress;
    private boolean closing;
    private long deleteArmedAt;
    private float[] closeRect = new float[4];
    private float[] navRect = new float[4];
    private float[] configRect = new float[4];
    private float[] contentRect = new float[4];
    private float scale = 1.0f;
    private float designW = 1000.0f;
    private float originX;
    private float originY;
    private float zoom = 1.0f;
    private final boolean preview;
    private DropdownPopup configPopup;

    public ClickGuiScreen(Screen parent) {
        this(parent, false);
    }

    public ClickGuiScreen(Screen parent, boolean preview) {
        super((Component)Component.literal((String)"Astra"));
        this.parent = parent;
        this.preview = preview;
        this.category = lastCategory;
        for (Category c : Category.values()) {
            this.itemHover.put(c, new Animation(0.0f, 14.0f));
        }
        if (preview) {
            return;
        }
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.onMenuOpen();
        }
    }

    public float layoutDesignWidth() {
        this.computeLayout();
        return this.designW;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderPreview(GuiGraphics ctx, float ox, float oy, float s, float dt) {
        this.computeLayout();
        this.openProgress = 1.0f;
        this.ui.ctx = ctx;
        this.ui.dt = dt;
        this.ui.theme = ThemeManager.get();
        this.ui.mx = -10000.0f;
        this.ui.my = -10000.0f;
        this.ui.blocked = true;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(ox, oy);
        m.scale(s, s);
        GuiStyle.HITS.clear();
        GuiStyle.recording = true;
        try {
            this.renderWindow(this.ui);
        }
        finally {
            GuiStyle.recording = false;
            m.popMatrix();
        }
    }

    public float[] previewTransform(float ax, float ay, float aw, float ah) {
        float w = this.layoutDesignWidth();
        float s = Math.min(aw / w, ah / 600.0f);
        return new float[]{ax + (aw - w * s) / 2.0f, ay + (ah - 600.0f * s) / 2.0f, s};
    }

    public boolean navigatePreview(double screenX, double screenY, float ox, float oy, float scale) {
        if (scale <= 0.0f || GuiStyle.hidden("nav")) {
            return false;
        }
        float mx = (float)((screenX - (double)ox) / (double)scale);
        float my = (float)((screenY - (double)oy) / (double)scale);
        float[] p = ClickGuiScreen.unmap("nav", this.navRect, mx, my);
        Category[] cats = Category.values();
        for (int i = 0; i < cats.length; ++i) {
            float y = 92.0f + (float)i * 32.5f;
            if (!(p[0] >= 12.0f) || !(p[0] < 184.0f) || !(p[1] >= y) || !(p[1] < y + 29.0f)) continue;
            this.setCategory(cats[i]);
            return true;
        }
        return false;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category c) {
        if (c == this.category) {
            return;
        }
        this.category = c;
        lastCategory = c;
        this.closePopup();
        this.setFocus(null);
        this.contentFade.set(0.0f);
        this.page(c).onShow();
    }

    public Page currentPage() {
        return this.page(this.category);
    }

    private Page page(Category c) {
        return this.pages.computeIfAbsent(c, cat -> Pages.create(this, cat));
    }

    protected void init() {
        this.page(this.category).onShow();
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void computeLayout() {
        double gui = this.minecraft.getWindow().getGuiScale();
        float screenPxW = (float)((double)this.width * gui);
        float screenPxH = (float)((double)this.height * gui);
        float sizeMul = ClientSettings.get() != null ? ClientSettings.get().uiScale.getFloat() / 100.0f : 1.0f;
        float pxPerUnit = screenPxH * 0.86f / 600.0f * sizeMul;
        this.designW = Math.min(1180.0f, Math.max(880.0f, screenPxW * 0.9f / pxPerUnit));
        if (this.designW * pxPerUnit > screenPxW * 0.97f) {
            pxPerUnit = screenPxW * 0.97f / this.designW;
        }
        this.scale = (float)((double)pxPerUnit / gui);
        this.originX = ((float)this.width - this.designW * this.scale) / 2.0f;
        this.originY = ((float)this.height - 600.0f * this.scale) / 2.0f;
    }

    private double toDesignX(double guiX) {
        double cx = this.originX + this.designW * this.scale / 2.0f;
        return (guiX - cx) / (double)(this.scale * this.zoom) + (double)(this.designW / 2.0f);
    }

    private double toDesignY(double guiY) {
        double cy = this.originY + 600.0f * this.scale / 2.0f;
        return (guiY - cy) / (double)(this.scale * this.zoom) + 300.0;
    }

    public void renderBackground(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            this.renderPanorama(ctx, delta);
        }
        float e = AnimationUtils.easeOutCubic(this.openProgress);
        ClientSettings cs = ClientSettings.get();
        if ((cs == null || ((Boolean)cs.blur.get()).booleanValue()) && this.minecraft.options.getMenuBackgroundBlurriness() >= 1) {
            ctx.blurBeforeThisStratum();
        }
        float dim = cs != null ? cs.dim.getFloat() / 100.0f : 0.45f;
        Theme t = ThemeManager.get();
        int top = ColorUtil.withAlpha(t.bg(), dim * 0.75f * e);
        int bottom = ColorUtil.withAlpha(ColorUtil.lerp(t.bg(), t.accent(), 0.12f), dim * e);
        ctx.fillGradient(0, 0, this.width, this.height, top, bottom);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        long now = System.nanoTime();
        float dt = this.lastFrame == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrame) / 1.0E9f);
        this.lastFrame = now;
        float animSpeed = AnimationUtils.speedMultiplier();
        if (this.closing) {
            float f = this.openProgress = animSpeed <= 0.0f ? 0.0f : this.openProgress - dt * 5.5f * animSpeed;
            if (this.openProgress <= 0.0f) {
                this.finishClose();
                return;
            }
        } else {
            this.openProgress = animSpeed <= 0.0f ? 1.0f : Math.min(1.0f, this.openProgress + dt * 4.2f * animSpeed);
        }
        float e = this.closing ? AnimationUtils.easeOutCubic(this.openProgress) : AnimationUtils.easeOutBack(this.openProgress);
        float alpha = AnimationUtils.easeOutCubic(this.openProgress);
        this.computeLayout();
        this.zoom = 0.92f + 0.08f * e;
        double gmx = this.minecraft.mouseHandler.xpos() * (double)this.width / (double)this.minecraft.getWindow().getScreenWidth();
        double gmy = this.minecraft.mouseHandler.ypos() * (double)this.height / (double)this.minecraft.getWindow().getScreenHeight();
        this.ui.ctx = ctx;
        this.ui.dt = dt;
        this.ui.theme = ThemeManager.get();
        this.ui.mx = (float)this.toDesignX(gmx);
        this.ui.my = (float)this.toDesignY(gmy);
        this.ui.blocked = this.popup != null && this.popup.contains(this.ui.mx, this.ui.my);
        Render2D.alpha = alpha;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(this.originX + this.designW * this.scale / 2.0f, this.originY + 600.0f * this.scale / 2.0f);
        m.scale(this.scale * this.zoom, this.scale * this.zoom);
        m.translate(-this.designW / 2.0f, -300.0f);
        try {
            this.renderWindow(this.ui);
        }
        finally {
            m.popMatrix();
            Render2D.alpha = 1.0f;
        }
    }

    private void renderWindow(UiContext ui) {
        Theme t = ui.theme;
        GuiGraphics ctx = ui.ctx;
        float W = this.designW;
        float H = 600.0f;
        float glow = UiDraw.glowStrength();
        DecorManager.renderMenu(ctx, "\u041f\u043e\u0434 \u043e\u043a\u043d\u043e\u043c", W);
        GuiStyle.record(ctx, "window.frame", 0.0f, 0.0f, W, H);
        t = GuiStyle.theme("window.frame", t);
        float frameAlpha = GuiStyle.styleAlpha("window.frame");
        if (glow > 0.0f) {
            Render2D.glow(ctx, 0.0f, 0.0f, W, H, 16.0f, 34.0f, ColorUtil.withAlpha(t.accent(), 0.3f * glow));
            Render2D.glow(ctx, 0.0f, 0.0f, W, H, 16.0f, 12.0f, ColorUtil.withAlpha(t.accent2(), 0.38f * glow));
        }
        Render2D.roundRect(ctx, 0.0f, 0.0f, W, H, 16.0f, Paint.linear(0.0f, 0.0f, W * 0.4f, H, ColorUtil.withAlpha(ColorUtil.lerp(t.bg2(), t.accent(), 0.14f), 0.84f), ColorUtil.withAlpha(t.bg(), 0.88f)));
        Render2D.shadow(ctx, W - 220.0f, 10.0f, 180.0f, 100.0f, 50.0f, 110.0f, ColorUtil.withAlpha(t.accent2(), 0.1f * Math.max(0.5f, glow)));
        Render2D.shadow(ctx, 236.0f, H - 110.0f, 260.0f, 80.0f, 40.0f, 110.0f, ColorUtil.withAlpha(t.accent(), 0.1f * Math.max(0.5f, glow)));
        Render2D.outline(ctx, 0.0f, 0.0f, W, H, 16.0f, 1.6f, Paint.linear(0.0f, 0.0f, W, H, ColorUtil.brighten(t.accent(), 0.15f), t.accent2()));
        Render2D.alpha = frameAlpha;
        t = ui.theme;
        ClientSettings cfg = ClientSettings.get();
        if (cfg == null || ((Boolean)cfg.artBackground.get()).booleanValue()) {
            float bgAlpha;
            float f = bgAlpha = cfg != null ? cfg.artBackgroundAlpha.getFloat() / 100.0f : 0.35f;
            if (GuiStyle.begin(ctx, null, "window.bg", 1.0f, 1.0f, W - 2.0f, H - 2.0f)) {
                DecorManager.Tex tex;
                String img = GuiStyle.image("window.bg");
                DecorManager.Tex tex2 = tex = img != null ? DecorManager.texture(img) : null;
                if (tex != null) {
                    Render2D.textureRoundRect(ctx, tex.id(), tex.aspect(), 1.0f, 1.0f, W - 2.0f, H - 2.0f, 15.0f, ColorUtil.withAlpha(-1, bgAlpha));
                } else {
                    ArtManager.drawBackground(ctx, 1.0f, 1.0f, W - 2.0f, H - 2.0f, 15.0f, bgAlpha);
                }
                GuiStyle.end(ctx, null);
            }
            Render2D.roundRect(ctx, 1.0f, 1.0f, W - 2.0f, H - 2.0f, 15.0f, Paint.horizontal(0.0f, W, ColorUtil.withAlpha(t.bg(), 0.55f * Math.min(1.0f, bgAlpha * 2.0f)), ColorUtil.withAlpha(t.bg(), 0.25f * Math.min(1.0f, bgAlpha * 2.0f))));
        }
        ArtManager.drawSidebarArt(ctx, t, 2.0f, H - 92.0f, 192.0f, H * 0.62f, 0.85f);
        if (ClientSettings.get() == null || ((Boolean)ClientSettings.get().petals.get()).booleanValue()) {
            this.petals.update(ui.dt, W, H);
            ctx.enableScissor(0, 0, (int)W, (int)H);
            this.petals.render(ctx, t, 1.0f);
            ctx.disableScissor();
        }
        ctx.enableScissor(0, 0, (int)W, (int)H);
        DecorManager.renderMenu(ctx, "\u0424\u043e\u043d \u043e\u043a\u043d\u0430", W);
        ctx.disableScissor();
        this.renderSidebar(ui);
        float cx = 196.0f;
        float cy = 12.0f;
        float cw = W - 196.0f - 12.0f;
        float ch = H - 24.0f;
        Render2D.roundRect(ctx, cx, cy, cw, ch, 12.0f, Paint.vertical(cy, cy + ch, ColorUtil.withAlpha(ColorUtil.lerp(t.bg2(), t.accent(), 0.06f), 0.55f), ColorUtil.withAlpha(t.bg(), 0.45f)));
        Render2D.outline(ctx, cx, cy, cw, ch, 12.0f, 0.9f, Paint.linear(cx, cy, cx + cw, cy + ch, ColorUtil.withAlpha(t.accent(), 0.55f), ColorUtil.withAlpha(t.accent2(), 0.4f)));
        float fade = this.contentFade.update(1.0f, ui.dt);
        float hx = cx + 14.0f;
        float hy = cy + 12.0f;
        if (GuiStyle.begin(ctx, ui, "header", hx, hy, cw - 28.0f, 46.0f)) {
            Theme th = GuiStyle.theme("header", t);
            UiDraw.iconBadge(ctx, th, hx, hy, 34.0f, this.category.icon, true);
            Fonts.draw(ctx, this.category.title, hx + 46.0f, hy + 3.0f, 16.0f, th.text(), true);
            Fonts.draw(ctx, Fonts.trim(this.category.subtitle, cw - 120.0f, 7.2f, false), hx + 46.0f, hy + 23.0f, 7.2f, th.textDim(), false);
            Render2D.rect(ctx, hx, hy + 44.0f, cw - 28.0f, 0.8f, Paint.horizontal(hx, hx + cw - 28.0f, th.accent2(), ColorUtil.withAlpha(th.accent(), 0.0f)));
            GuiStyle.end(ctx, ui);
        }
        float closeX = cx + cw - 26.0f;
        float closeY = cy + 10.0f;
        this.closeRect = new float[]{closeX - 3.0f, closeY - 3.0f, 20.0f, 20.0f};
        if (GuiStyle.begin(ctx, ui, "close", closeX - 3.0f, closeY - 3.0f, 20.0f, 20.0f)) {
            Theme tc = GuiStyle.theme("close", t);
            float chv = this.closeHover.update(ui.hover(closeX - 3.0f, closeY - 3.0f, 20.0f, 20.0f) ? 1.0f : 0.0f, ui.dt);
            if (chv > 0.01f) {
                Render2D.circle(ctx, closeX + 7.0f, closeY + 7.0f, 10.0f, ColorUtil.withAlpha(tc.accent(), 0.2f * chv));
            }
            Icons.draw(ctx, Icon.CLOSE, closeX, closeY, 14.0f, ColorUtil.lerp(tc.textDim(), tc.text(), chv));
            GuiStyle.end(ctx, ui);
        }
        Page page = this.page(this.category);
        float py = hy + 52.0f;
        page.setBounds(hx, py, cw - 28.0f - 6.0f, cy + ch - py - 10.0f);
        this.contentRect = new float[]{hx, py, cw - 28.0f - 6.0f, cy + ch - py - 10.0f};
        if (GuiStyle.begin(ctx, ui, "content", hx, py, cw - 28.0f - 6.0f, cy + ch - py - 10.0f)) {
            float prev = Render2D.alpha;
            Render2D.alpha *= fade;
            Matrix3x2fStack m = ctx.pose();
            m.pushMatrix();
            m.translate(0.0f, (1.0f - fade) * 10.0f);
            page.render(ui);
            m.popMatrix();
            Render2D.alpha = prev;
            GuiStyle.end(ctx, ui);
        }
        DecorManager.renderMenu(ctx, "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e", W);
        if (this.popup != null) {
            ui.blocked = false;
            this.popup.render(ui);
        }
        this.renderToast(ui);
    }

    private void renderSidebar(UiContext ui) {
        Theme t = ui.theme;
        GuiGraphics ctx = ui.ctx;
        if (GuiStyle.begin(ctx, ui, "logo", 10.0f, 10.0f, 176.0f, 68.0f)) {
            DecorManager.Tex tex;
            String img = GuiStyle.image("logo");
            DecorManager.Tex tex2 = tex = img != null ? DecorManager.texture(img) : null;
            if (tex != null) {
                float lw = 176.0f;
                float lh = 68.0f;
                float iw = Math.min(lw, lh * tex.aspect());
                float ih = iw / tex.aspect();
                Render2D.texture(ctx, tex.id(), 10.0f + (lw - iw) / 2.0f, 10.0f + (lh - ih) / 2.0f, iw, ih, 0.0f, 0.0f, 1.0f, 1.0f, -1);
            } else {
                ClickGuiScreen.drawLogo(ctx, GuiStyle.theme("logo", t), 10.0f, 10.0f, 1.0f);
            }
            GuiStyle.end(ctx, ui);
        }
        float itemX = 12.0f;
        float itemW = 172.0f;
        float itemH = 29.0f;
        float gap = 3.5f;
        float y0 = 92.0f;
        Category[] cats = Category.values();
        this.navRect = new float[]{itemX, y0, itemW, (float)cats.length * (itemH + gap)};
        Theme sidebarTheme = t;
        if (!GuiStyle.begin(ctx, ui, "nav", itemX, y0, itemW, (float)cats.length * (itemH + gap))) {
            this.renderConfigSwitcher(ui);
            return;
        }
        t = GuiStyle.theme("nav", t);
        int selIndex = this.category.ordinal();
        float target = y0 + (float)selIndex * (itemH + gap);
        if (this.selector.get() < 0.0f) {
            this.selector.set(target);
        }
        float sy = this.selector.update(target, ui.dt);
        if (UiDraw.glowStrength() > 0.0f) {
            Render2D.glow(ctx, itemX, sy, itemW, itemH, 8.0f, 14.0f, ColorUtil.withAlpha(t.accent(), 0.45f * UiDraw.glowStrength()));
            Render2D.glow(ctx, itemX, sy, itemW, itemH, 8.0f, 5.0f, ColorUtil.withAlpha(t.accent2(), 0.4f * UiDraw.glowStrength()));
        }
        Render2D.roundRect(ctx, itemX, sy, itemW, itemH, 8.0f, Paint.horizontal(itemX, itemX + itemW, ColorUtil.withAlpha(t.accent(), 0.9f), ColorUtil.withAlpha(ColorUtil.lerp(t.accent(), t.accent2(), 0.7f), 0.6f)));
        Render2D.outline(ctx, itemX, sy, itemW, itemH, 8.0f, 1.1f, Paint.horizontal(itemX, itemX + itemW, ColorUtil.brighten(t.accent2(), 0.35f), ColorUtil.withAlpha(t.accent2(), 0.7f)));
        for (int i = 0; i < cats.length; ++i) {
            boolean sel;
            float iy;
            boolean hov;
            Category c = cats[i];
            float hv = this.itemHover.get((Object)c).update((hov = ui.hover(itemX, iy = y0 + (float)i * (itemH + gap), itemW, itemH)) ? 1.0f : 0.0f, ui.dt);
            boolean bl = sel = c == this.category;
            if (!sel && hv > 0.01f) {
                Render2D.roundRect(ctx, itemX, iy, itemW, itemH, 8.0f, ColorUtil.withAlpha(t.accent(), 0.12f * hv));
            }
            int col = sel ? t.text() : ColorUtil.lerp(t.textDim(), t.text(), hv);
            Icons.drawNeon(ctx, c.icon, itemX + 11.0f + hv * 1.5f, iy + (itemH - 14.0f) / 2.0f, 14.0f, sel ? -1 : col, sel ? t.accent2() : ColorUtil.lerp(t.accent(), t.accent2(), hv), 0.55f + 0.45f * hv);
            Fonts.draw(ctx, c.title, itemX + 34.0f + hv * 1.5f, iy + (itemH - 8.2f) / 2.0f + 0.5f, 8.2f, col, sel);
            if (!sel) continue;
            Icons.draw(ctx, Icon.CHEVRON_RIGHT, itemX + itemW - 16.0f, iy + (itemH - 8.0f) / 2.0f, 8.0f, ColorUtil.withAlpha(0xFFFFFF, 0.8f));
        }
        GuiStyle.end(ctx, ui);
        this.renderConfigSwitcher(ui);
    }

    public static void drawLogo(GuiGraphics ctx, Theme t, float x, float y, float k) {
        ArtManager.drawLogo(ctx, x, y, 176.0f * k, 1.0f, t.accent(), t.accent2());
        float tw = AnimationUtils.pulse(2.4f);
        Render2D.sparkle(ctx, x + 158.0f * k, y + 12.0f * k, (3.0f + 2.2f * tw) * k, tw * 45.0f, ColorUtil.withAlpha(-1, 0.6f + 0.4f * tw));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderConfigSwitcher(UiContext ui) {
        float rx = 12.0f;
        float rw = 172.0f;
        float rh = 82.0f;
        float ry = 600.0f - rh - 14.0f;
        this.configRect = new float[]{rx, ry, rw, rh};
        if (!GuiStyle.begin(ui.ctx, ui, "config", rx, ry, rw, rh)) {
            return;
        }
        try {
            this.renderConfigSwitcherInner(ui);
        }
        finally {
            GuiStyle.end(ui.ctx, ui);
        }
    }

    private void renderConfigSwitcherInner(UiContext ui) {
        Theme t = GuiStyle.theme("config", ui.theme);
        GuiGraphics ctx = ui.ctx;
        float x = 12.0f;
        float w = 172.0f;
        float h = 82.0f;
        float y = 600.0f - h - 14.0f;
        UiDraw.card(ctx, t, x, y, w, h, 9.0f, 0.0f, false);
        Fonts.draw(ctx, "\u041a\u043e\u043d\u0444\u0438\u0433", x + 10.0f, y + 9.0f, 8.0f, t.text(), true);
        float bx = x + 10.0f;
        float by = y + 24.0f;
        float bw = w - 20.0f;
        float bh = 18.0f;
        boolean open = this.popup instanceof DropdownPopup && this.configPopup == this.popup;
        float hv = this.configHover[0].update(ui.hover(bx, by, bw, bh) ? 1.0f : 0.0f, ui.dt);
        UiDraw.dropdownBox(ctx, t, bx, by, bw, bh, ConfigManager.activeProfile(), hv, open);
        Icon[] icons = new Icon[]{Icon.FOLDER, Icon.SAVE, Icon.PLUS, Icon.TRASH};
        float size = 22.0f;
        float gap = (bw - size * 4.0f) / 3.0f;
        for (int i = 0; i < 4; ++i) {
            float ix = bx + (float)i * (size + gap);
            float iy = by + bh + 8.0f;
            float ih = this.configHover[i + 1].update(ui.hover(ix, iy, size, size) ? 1.0f : 0.0f, ui.dt);
            boolean armed = i == 3 && System.currentTimeMillis() - this.deleteArmedAt < 3000L;
            UiDraw.iconButton(ctx, t, ix, iy, size, icons[i], armed ? 1.0f : ih);
            if (!armed) continue;
            Render2D.outline(ctx, ix, iy, size, size, 5.0f, 1.2f, -770210);
        }
    }

    private boolean configSwitcherClick(double mx, double my) {
        float x = 12.0f;
        float w = 172.0f;
        float h = 82.0f;
        float y = 600.0f - h - 14.0f;
        float bx = x + 10.0f;
        float by = y + 24.0f;
        float bw = w - 20.0f;
        float bh = 18.0f;
        if (mx >= (double)bx && mx < (double)(bx + bw) && my >= (double)by && my < (double)(by + bh)) {
            this.configPopup = new DropdownPopup(bx, by + bh + 2.0f, bw, ConfigManager.listProfiles(), ConfigManager::activeProfile, name -> {
                ConfigManager.loadProfile(name);
                this.toast("\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name + "\u00bb \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d");
            }, this::closePopup);
            this.openPopup(this.configPopup);
            return true;
        }
        float size = 22.0f;
        float gap = (bw - size * 4.0f) / 3.0f;
        for (int i = 0; i < 4; ++i) {
            float ix = bx + (float)i * (size + gap);
            float iy = by + bh + 8.0f;
            if (!(mx >= (double)ix) || !(mx < (double)(ix + size)) || !(my >= (double)iy) || !(my < (double)(iy + size))) continue;
            switch (i) {
                case 0: {
                    Util.getPlatform().openFile(ConfigManager.profilesDir().toFile());
                    break;
                }
                case 1: {
                    ConfigManager.save();
                    this.toast("\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + ConfigManager.activeProfile() + "\u00bb \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d");
                    break;
                }
                case 2: {
                    String name2 = ConfigManager.uniqueName("\u041f\u0440\u043e\u0444\u0438\u043b\u044c");
                    if (!ConfigManager.createProfile(name2)) break;
                    this.toast("\u0421\u043e\u0437\u0434\u0430\u043d \u043f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name2 + "\u00bb");
                    break;
                }
                case 3: {
                    if (System.currentTimeMillis() - this.deleteArmedAt < 3000L) {
                        String name3 = ConfigManager.activeProfile();
                        this.deleteArmedAt = 0L;
                        if (ConfigManager.deleteProfile(name3)) {
                            this.toast("\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name3 + "\u00bb \u0443\u0434\u0430\u043b\u0451\u043d");
                            break;
                        }
                        this.toast("\u041d\u0435\u043b\u044c\u0437\u044f \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0435\u0434\u0438\u043d\u0441\u0442\u0432\u0435\u043d\u043d\u044b\u0439 \u043f\u0440\u043e\u0444\u0438\u043b\u044c");
                        break;
                    }
                    this.deleteArmedAt = System.currentTimeMillis();
                    this.toast("\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0435\u0449\u0451 \u0440\u0430\u0437, \u0447\u0442\u043e\u0431\u044b \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043f\u0440\u043e\u0444\u0438\u043b\u044c");
                    break;
                }
            }
            return true;
        }
        return mx >= (double)x && mx < (double)(x + w) && my >= (double)y && my < (double)(y + h);
    }

    private void renderToast(UiContext ui) {
        if (this.toast == null) {
            return;
        }
        long age = System.currentTimeMillis() - this.toastAt;
        if (age > 2600L) {
            this.toast = null;
            return;
        }
        float a = Math.min(1.0f, (float)age / 150.0f) * Math.min(1.0f, (float)(2600L - age) / 300.0f);
        Theme t = ui.theme;
        float size = 7.6f;
        float tw = Fonts.width(this.toast, size, true) + 30.0f;
        float x = (this.designW + 196.0f) / 2.0f - tw / 2.0f;
        float y = 560.0f + (1.0f - a) * 8.0f;
        float prev = Render2D.alpha;
        Render2D.alpha *= a;
        Render2D.shadow(ui.ctx, x, y, tw, 20.0f, 10.0f, 8.0f, 0x60000000);
        Render2D.roundRect(ui.ctx, x, y, tw, 20.0f, 10.0f, Paint.horizontal(x, x + tw, t.bg2(), ColorUtil.lerp(t.bg2(), t.accent(), 0.25f)));
        Render2D.outline(ui.ctx, x, y, tw, 20.0f, 10.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.8f));
        Render2D.sparkle(ui.ctx, x + 11.0f, y + 10.0f, 4.0f, 0.0f, t.accent2());
        Fonts.draw(ui.ctx, this.toast, x + 20.0f, y + (20.0f - size) / 2.0f + 0.4f, size, t.text(), true);
        Render2D.alpha = prev;
    }

    @Override
    public void openPopup(Popup p) {
        this.popup = p;
        if (p != null) {
            p.fit(this.designW, 600.0f);
        }
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

    public float designWidth() {
        return this.designW;
    }

    private void updateMouse(double mx, double my) {
        this.ui.mx = (float)this.toDesignX(mx);
        this.ui.my = (float)this.toDesignY(my);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.closing) {
            return false;
        }
        this.updateMouse(mouseX, mouseY);
        double mx = this.ui.mx;
        double my = this.ui.my;
        this.ui.blocked = false;
        if (this.popup != null) {
            if (this.popup.mouseClicked(this.ui, mx, my, button)) {
                return true;
            }
            this.closePopup();
            return true;
        }
        if (this.focus != null && !this.focus.contains(mx, my)) {
            this.setFocus(null);
        }
        float cx = 196.0f;
        float cy = 12.0f;
        float cw = this.designW - 196.0f - 12.0f;
        float closeX = cx + cw - 26.0f;
        float closeY = cy + 10.0f;
        float[] cp = ClickGuiScreen.unmap("close", this.closeRect, mx, my);
        if (!GuiStyle.hidden("close") && cp[0] >= closeX - 3.0f && cp[0] < closeX + 17.0f && cp[1] >= closeY - 3.0f && cp[1] < closeY + 17.0f) {
            this.onClose();
            return true;
        }
        float itemX = 12.0f;
        float itemW = 172.0f;
        float itemH = 29.0f;
        float gap = 3.5f;
        float y0 = 92.0f;
        Category[] cats = Category.values();
        float[] np = ClickGuiScreen.unmap("nav", this.navRect, mx, my);
        for (int i = 0; i < cats.length && !GuiStyle.hidden("nav"); ++i) {
            float iy = y0 + (float)i * (itemH + gap);
            if (!(np[0] >= itemX) || !(np[0] < itemX + itemW) || !(np[1] >= iy) || !(np[1] < iy + itemH)) continue;
            this.setCategory(cats[i]);
            return true;
        }
        float[] kp = ClickGuiScreen.unmap("config", this.configRect, mx, my);
        if (!GuiStyle.hidden("config") && this.configSwitcherClick(kp[0], kp[1])) {
            return true;
        }
        if (GuiStyle.hidden("content")) {
            return false;
        }
        float[] pp = ClickGuiScreen.unmap("content", this.contentRect, mx, my);
        return this.page(this.category).mouseClicked(this.ui, pp[0], pp[1], button);
    }

    public float[] devDesignToGui(float dx, float dy) {
        this.computeLayout();
        float s = this.scale * this.zoom;
        return new float[]{this.originX + this.designW * this.scale / 2.0f + (dx - this.designW / 2.0f) * s, this.originY + 600.0f * this.scale / 2.0f + (dy - 300.0f) * s};
    }

    private static float[] unmap(String id, float[] r, double mx, double my) {
        return GuiStyle.unmap(id, r[0], r[1], r[2], r[3], mx, my);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.updateMouse(mouseX, mouseY);
        if (this.popup != null && this.popup.mouseDragged(this.ui, this.ui.mx, this.ui.my, button)) {
            return true;
        }
        float[] pp = ClickGuiScreen.unmap("content", this.contentRect, this.ui.mx, this.ui.my);
        return this.page(this.category).mouseDragged(this.ui, pp[0], pp[1], button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.updateMouse(mouseX, mouseY);
        if (this.popup != null) {
            this.popup.mouseReleased(this.ui, this.ui.mx, this.ui.my, button);
        }
        float[] pp = ClickGuiScreen.unmap("content", this.contentRect, this.ui.mx, this.ui.my);
        this.page(this.category).mouseReleased(this.ui, pp[0], pp[1], button);
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        this.updateMouse(mouseX, mouseY);
        if (this.popup != null && this.popup.mouseScrolled(this.ui.mx, this.ui.my, vertical)) {
            return true;
        }
        float[] pp = ClickGuiScreen.unmap("content", this.contentRect, this.ui.mx, this.ui.my);
        return this.page(this.category).mouseScrolled(pp[0], pp[1], vertical);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focus != null && this.focus.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.page(this.category).keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256 && this.popup != null) {
            this.closePopup();
            return true;
        }
        if (AstraClient.openGuiKey != null && AstraClient.openGuiKey.matches(new KeyEvent(keyCode, scanCode, modifiers))) {
            this.onClose();
            return true;
        }
        if (this.popup == null && (keyCode == 258 || keyCode == 264 || keyCode == 265) && (modifiers & 2) != 0) {
            int dir = keyCode == 265 || (modifiers & 1) != 0 ? -1 : 1;
            Category[] cats = Category.values();
            this.setCategory(cats[Math.floorMod(this.category.ordinal() + dir, cats.length)]);
            return true;
        }
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.focus != null && this.focus.charTyped(chr, modifiers)) {
            return true;
        }
        return this.page(this.category).charTyped(chr, modifiers);
    }

    public void onClose() {
        if (this.closing) {
            return;
        }
        this.closePopup();
        this.setFocus(null);
        this.closing = true;
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.onMenuClose();
        }
        if (AnimationUtils.speedMultiplier() <= 0.0f) {
            this.finishClose();
        }
    }

    public void removed() {
        ModuleCard.collapseAll();
        this.closePopup();
        this.setFocus(null);
    }

    private void finishClose() {
        ConfigManager.save();
        this.minecraft.setScreen(this.parent);
    }

    public void openHudEditor() {
        this.minecraft.setScreen((Screen)new HudEditorScreen(this));
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
            handled |= this.charTyped(ch, input.modifiers());
        }
        return handled;
    }
}

