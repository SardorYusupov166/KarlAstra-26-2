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
 *  org.joml.Matrix3x2fStack
 */
package astra.gui.screens;

import astra.AstraClient;
import astra.animation.Animation;
import astra.animation.AnimationUtils;
import astra.config.ConfigManager;
import astra.config.HudPresetManager;
import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.Button;
import astra.gui.components.Popup;
import astra.gui.components.SettingRows;
import astra.gui.components.TextField;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.hud.HudEditorPrefs;
import astra.hud.HudModule;
import astra.module.ClientSettings;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.SoundsModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2fStack;

public class HudEditorScreen
extends Screen
implements PopupHost {
    private static final float INSPECTOR_W = 214.0f;
    private static final float TOPBAR_W = 452.0f;
    private static final float TOPBAR_H = 40.0f;
    private static final float SNAP = 4.0f;
    private static final int[] SWATCHES = new int[]{-5745153, -4160260, -13058568, -14494738, -11870592, -340971, -770210, -1};
    private final Screen parent;
    private final UiContext ui = new UiContext();
    private HudModule selected;
    private HudModule dragging;
    private float grabX;
    private float grabY;
    private int resizeCorner = -1;
    private float resizeFixedX;
    private float resizeFixedY;
    private float resizeStartScale;
    private float resizeStartDist;
    private final List<float[]> guides = new ArrayList<float[]>();
    private int tab;
    private boolean inspectorHidden;
    private final List<Widget> rows = new ArrayList<Widget>();
    private float inspectorScroll;
    private float inspectorContentH;
    private final Animation inspectorAnim = new Animation(1.0f, 12.0f);
    private final Animation dockAnim = new Animation(0.0f, 10.0f);
    private final Map<String, Animation> hovers = new HashMap<String, Animation>();
    private TextField presetField;
    private Popup popup;
    private Widget focus;
    private String toast;
    private long toastAt;
    private long lastFrame;
    private float openProgress;
    private float s = 1.0f;
    private float dw;
    private float dh;
    private final Animation topBarFade = new Animation(1.0f, 10.0f);

    public HudEditorScreen(Screen parent) {
        super((Component)Component.literal((String)"Astra HUD Editor"));
        this.parent = parent;
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.onEditorOpen();
        }
    }

    protected void init() {
        this.computeScale();
        this.presetField = new TextField(this, "\u0418\u043c\u044f \u043f\u0440\u0435\u0441\u0435\u0442\u0430\u2026", 32).onSubmit(this::savePreset);
        this.rebuildRows();
    }

    public boolean isPauseScreen() {
        return false;
    }

    private void computeScale() {
        double gui = this.minecraft.getWindow().getGuiScale();
        float sizeMul = ClientSettings.get() != null ? ClientSettings.get().uiScale.getFloat() / 100.0f : 1.0f;
        float pxPerUnit = (float)((double)this.height * gui) * 0.86f / 600.0f * sizeMul;
        this.s = (float)((double)pxPerUnit / gui);
        this.dw = (float)this.width / this.s;
        this.dh = (float)this.height / this.s;
    }

    private HudEditorPrefs prefs() {
        return HudEditorPrefs.get();
    }

    private void rebuildRows() {
        this.rows.clear();
        this.inspectorScroll = 0.0f;
        if (this.tab == 0) {
            Widget w;
            if (this.selected != null) {
                HudModule sel = this.selected;
                this.rows.add(SettingRows.create(new NumberSetting("\u041f\u043e\u0437\u0438\u0446\u0438\u044f X", 0.0, 0.0, Math.max(1.0f, (float)this.width - sel.scaledWidth()), 1.0).bind(() -> Math.round(sel.getX(this.width)), v -> sel.setTopLeft(v.floatValue(), sel.getY(this.height), this.width, this.height)), this));
                this.rows.add(SettingRows.create(new NumberSetting("\u041f\u043e\u0437\u0438\u0446\u0438\u044f Y", 0.0, 0.0, Math.max(1.0f, (float)this.height - sel.scaledHeight()), 1.0).bind(() -> Math.round(sel.getY(this.height)), v -> sel.setTopLeft(sel.getX(this.width), v.floatValue(), this.width, this.height)), this));
                for (Setting<?> setting : sel.getSettings()) {
                    w = SettingRows.create(setting, this);
                    if (w == null) continue;
                    this.rows.add(w);
                }
                this.rows.add(new SwatchRow(sel));
                this.rows.add(new Button("\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043f\u043e\u0437\u0438\u0446\u0438\u044e", Icon.RESET, false, 0.0f, () -> {
                    sel.resetPosition();
                    this.toast("\u041f\u043e\u0437\u0438\u0446\u0438\u044f \u00ab" + sel.getName() + "\u00bb \u0441\u0431\u0440\u043e\u0448\u0435\u043d\u0430");
                }));
            }
            this.rows.add(new SectionTitle("\u041f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u0438 \u0441\u0435\u0442\u043a\u0430", Icon.GRID));
            HudEditorPrefs p = this.prefs();
            for (Setting<?> setting : p.getSettings()) {
                w = SettingRows.create(setting, this);
                if (w == null) continue;
                this.rows.add(w);
            }
        } else if (this.tab == 1) {
            for (HudModule m : ModuleManager.hudModules()) {
                this.rows.add(new ElementRow(m));
            }
        } else {
            this.rows.add(this.presetField);
            this.rows.add(new Button("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043f\u0440\u0435\u0441\u0435\u0442", Icon.SAVE, true, 0.0f, () -> this.savePreset(this.presetField.getText().trim())));
            this.rows.add(new SectionTitle("\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0435 \u043f\u0440\u0435\u0441\u0435\u0442\u044b", Icon.LAYERS));
            for (String name : HudPresetManager.list()) {
                this.rows.add(new PresetRow(name));
            }
        }
    }

    private void savePreset(String name) {
        if (name.isEmpty()) {
            this.toast("\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0438\u043c\u044f \u043f\u0440\u0435\u0441\u0435\u0442\u0430");
            return;
        }
        if (HudPresetManager.save(name)) {
            this.toast("\u041f\u0440\u0435\u0441\u0435\u0442 \u00ab" + name + "\u00bb \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d");
            this.presetField.setText("");
            this.rebuildRows();
        } else {
            this.toast("\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u0438\u043c\u044f \u043f\u0440\u0435\u0441\u0435\u0442\u0430");
        }
    }

    public void select(HudModule m) {
        if (this.selected == m) {
            return;
        }
        this.selected = m;
        if (this.tab == 0) {
            this.rebuildRows();
        }
    }

    public void renderBackground(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            this.renderPanorama(ctx, delta);
        }
        Theme t = ThemeManager.get();
        ctx.fillGradient(0, 0, this.width, this.height, ColorUtil.withAlpha(t.bg(), 0.28f), ColorUtil.withAlpha(t.bg(), 0.42f));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        long now = System.nanoTime();
        float dt = this.lastFrame == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrame) / 1.0E9f);
        this.lastFrame = now;
        float sp = AnimationUtils.speedMultiplier();
        this.openProgress = sp <= 0.0f ? 1.0f : Math.min(1.0f, this.openProgress + dt * 4.0f * sp);
        this.computeScale();
        Theme t = ThemeManager.get();
        double gmx = this.minecraft.mouseHandler.xpos() * (double)this.width / (double)this.minecraft.getWindow().getScreenWidth();
        double gmy = this.minecraft.mouseHandler.ypos() * (double)this.height / (double)this.minecraft.getWindow().getScreenHeight();
        Render2D.alpha = AnimationUtils.easeOutCubic(this.openProgress);
        try {
            this.renderGrid(ctx, t);
            this.renderElements(ctx, t, (float)gmx, (float)gmy);
            this.renderGuides(ctx, t);
            this.ui.ctx = ctx;
            this.ui.dt = dt;
            this.ui.theme = t;
            this.ui.mx = (float)(gmx / (double)this.s);
            this.ui.my = (float)(gmy / (double)this.s);
            this.ui.blocked = this.popup != null && this.popup.contains(this.ui.mx, this.ui.my);
            Matrix3x2fStack m = ctx.pose();
            m.pushMatrix();
            m.scale(this.s, this.s);
            this.renderTopBar(this.ui);
            this.renderInspector(this.ui);
            if (this.popup != null) {
                this.ui.blocked = false;
                this.popup.render(this.ui);
            }
            this.renderToast(this.ui);
            m.popMatrix();
        }
        finally {
            Render2D.alpha = 1.0f;
        }
    }

    private void renderGrid(GuiGraphics ctx, Theme t) {
        HudEditorPrefs p = this.prefs();
        if (((Boolean)p.showGrid.get()).booleanValue()) {
            float g = p.gridSize.getFloat();
            Mesh mesh = Render2D.begin();
            float px = Render2D.pixel(ctx);
            int lineCol = ColorUtil.withAlpha(t.accent(), 0.1f);
            for (float x = 0.0f; x <= (float)this.width; x += g) {
                mesh.rect(x, 0.0f, px, (float)this.height, lineCol);
            }
            for (float y = 0.0f; y <= (float)this.height; y += g) {
                mesh.rect(0.0f, y, (float)this.width, px, lineCol);
            }
            Render2D.end(ctx);
        }
        int center = ColorUtil.withAlpha(t.accent3(), 0.45f);
        Render2D.dashedLine(ctx, (float)this.width / 2.0f, 0.0f, (float)this.width / 2.0f, this.height, 0.6f, 4.0f, 4.0f, center);
        Render2D.dashedLine(ctx, 0.0f, (float)this.height / 2.0f, this.width, (float)this.height / 2.0f, 0.6f, 4.0f, 4.0f, center);
    }

    private void renderElements(GuiGraphics ctx, Theme t, float mx, float my) {
        HudModule hovered = this.elementAt(mx, my);
        for (HudModule hud : ModuleManager.hudModules()) {
            if (!hud.isEnabled()) continue;
            try {
                hud.render(ctx, this.width, this.height, true);
            }
            catch (RuntimeException e) {
                AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430 {}", (Object)hud.getId(), (Object)e);
            }
            float x = hud.getX(this.width);
            float y = hud.getY(this.height);
            float w = hud.scaledWidth();
            float h = hud.scaledHeight();
            boolean sel = hud == this.selected;
            boolean hov = hud == hovered;
            float a = this.hovers.computeIfAbsent(hud.getId(), k -> new Animation(0.0f, 14.0f)).update(sel || hov ? 1.0f : 0.0f, 0.016666668f);
            if (sel) {
                Render2D.glow(ctx, x - 1.0f, y - 1.0f, w + 2.0f, h + 2.0f, 3.0f, 6.0f, ColorUtil.withAlpha(t.accent(), 0.45f));
                Render2D.outline(ctx, x - 1.5f, y - 1.5f, w + 3.0f, h + 3.0f, 3.0f, 1.0f, Paint.horizontal(x, x + w, t.accent(), t.accent2()));
                for (float[] c : this.corners(hud)) {
                    Render2D.roundRect(ctx, c[0] - 2.5f, c[1] - 2.5f, 5.0f, 5.0f, 1.2f, -1);
                    Render2D.outline(ctx, c[0] - 2.5f, c[1] - 2.5f, 5.0f, 5.0f, 1.2f, 0.8f, t.accent());
                }
            } else {
                int col = ColorUtil.withAlpha(t.accent(), 0.35f + 0.4f * a);
                Render2D.dashedLine(ctx, x - 1.0f, y - 1.0f, x + w + 1.0f, y - 1.0f, 0.6f, 3.0f, 2.0f, col);
                Render2D.dashedLine(ctx, x - 1.0f, y + h + 1.0f, x + w + 1.0f, y + h + 1.0f, 0.6f, 3.0f, 2.0f, col);
                Render2D.dashedLine(ctx, x - 1.0f, y - 1.0f, x - 1.0f, y + h + 1.0f, 0.6f, 3.0f, 2.0f, col);
                Render2D.dashedLine(ctx, x + w + 1.0f, y - 1.0f, x + w + 1.0f, y + h + 1.0f, 0.6f, 3.0f, 2.0f, col);
            }
            if (!sel && !hov && !(a > 0.05f)) continue;
            float size = 5.5f;
            float lw = Fonts.width(hud.getName(), size, true) + 8.0f;
            float ly = y - 10.0f >= 0.0f ? y - 10.0f : y + h + 2.0f;
            float prev = Render2D.alpha;
            Render2D.alpha *= Math.max(a, sel ? 1.0f : 0.0f);
            Render2D.roundRect(ctx, x, ly, lw, 8.5f, 2.5f, Paint.horizontal(x, x + lw, ColorUtil.withAlpha(t.accent(), 0.9f), ColorUtil.withAlpha(t.accent2(), 0.75f)));
            Fonts.draw(ctx, hud.getName(), x + 4.0f, ly + 1.6f, size, -1, true);
            Render2D.alpha = prev;
        }
    }

    private void renderGuides(GuiGraphics ctx, Theme t) {
        int col = ColorUtil.withAlpha(t.accent3(), 0.95f);
        for (float[] g : this.guides) {
            if (g[0] == 0.0f) {
                Render2D.dashedLine(ctx, g[1], 0.0f, g[1], this.height, 0.8f, 5.0f, 3.0f, col);
                continue;
            }
            Render2D.dashedLine(ctx, 0.0f, g[1], this.width, g[1], 0.8f, 5.0f, 3.0f, col);
        }
    }

    private float[][] corners(HudModule hud) {
        float x = hud.getX(this.width);
        float y = hud.getY(this.height);
        float w = hud.scaledWidth();
        float h = hud.scaledHeight();
        return new float[][]{{x - 1.5f, y - 1.5f}, {x + w + 1.5f, y - 1.5f}, {x + w + 1.5f, y + h + 1.5f}, {x - 1.5f, y + h + 1.5f}};
    }

    private float[] topBar() {
        float x = Math.max(8.0f, Math.min((this.dw - 452.0f) / 2.0f, this.dw - 214.0f - 452.0f - 20.0f));
        return new float[]{x, 8.0f, 452.0f, 40.0f};
    }

    private float[] topButton(int i) {
        float[] b = this.topBar();
        float[] widths = new float[]{78.0f, 82.0f, 86.0f};
        float bx = b[0] + 176.0f;
        for (int k = 0; k < i; ++k) {
            bx += widths[k] + 6.0f;
        }
        return new float[]{bx, b[1] + 10.0f, widths[i], 20.0f};
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderTopBar(UiContext ui) {
        Theme t = ui.theme;
        float[] b = this.topBar();
        boolean over = ui.hover(b[0], b[1], b[2], b[3]);
        float fade = this.topBarFade.update(this.dragging != null ? 0.25f : (over ? 1.0f : 0.82f), ui.dt);
        float prevAlpha = Render2D.alpha;
        Render2D.alpha *= fade;
        try {
            this.renderTopBarContent(ui, t, b);
        }
        finally {
            Render2D.alpha = prevAlpha;
        }
    }

    private void renderTopBarContent(UiContext ui, Theme t, float[] b) {
        float hv;
        UiDraw.card(ui.ctx, t, b[0], b[1], b[2], b[3], 9.0f, 0.0f, true);
        Render2D.roundRect(ui.ctx, b[0], b[1], b[2], b[3], 9.0f, ColorUtil.withAlpha(t.bg(), 0.55f));
        UiDraw.iconBadge(ui.ctx, t, b[0] + 8.0f, b[1] + 7.0f, 26.0f, Icon.HOME, true);
        Fonts.draw(ui.ctx, "HUD", b[0] + 40.0f, b[1] + 8.0f, 10.0f, t.text(), true);
        Fonts.draw(ui.ctx, "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u0438\u0433\u0440\u043e\u0432\u043e\u0433\u043e \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430", b[0] + 40.0f, b[1] + 22.0f, 6.2f, t.textDim(), false);
        String[] labels = new String[]{"\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c", "\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c", "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c"};
        Icon[] icons = new Icon[]{Icon.RESET, Icon.DOWNLOAD, Icon.SAVE};
        for (int i = 0; i < 3; ++i) {
            float[] r = this.topButton(i);
            hv = this.hover("top" + i, ui.hover(r[0], r[1], r[2], r[3]), ui.dt);
            UiDraw.button(ui.ctx, t, r[0], r[1], r[2], r[3], labels[i], icons[i], hv, i == 2);
        }
        float cx = b[0] + b[2] - 24.0f;
        float cy = b[1] + 12.0f;
        hv = this.hover("close", ui.hover(cx - 2.0f, cy - 2.0f, 20.0f, 20.0f), ui.dt);
        if (hv > 0.01f) {
            Render2D.circle(ui.ctx, cx + 8.0f, cy + 8.0f, 10.0f, ColorUtil.withAlpha(t.accent(), 0.2f * hv));
        }
        Icons.draw(ui.ctx, Icon.CLOSE, cx + 1.0f, cy + 1.0f, 14.0f, ColorUtil.lerp(t.textDim(), t.text(), hv));
    }

    private float hover(String key, boolean hovered, float dt) {
        return this.hovers.computeIfAbsent(key, k -> new Animation(0.0f, 16.0f)).update(hovered ? 1.0f : 0.0f, dt);
    }

    private boolean dockLeft() {
        return this.selected != null && this.selected.getX(this.width) + this.selected.scaledWidth() / 2.0f > (float)this.width * 0.55f;
    }

    private float[] inspectorRect() {
        float slide = this.inspectorAnim.get();
        float dock = this.dockAnim.get();
        float rightX = this.dw - 8.0f - 214.0f + (1.0f - slide) * 222.0f;
        float leftX = 8.0f - (1.0f - slide) * 222.0f;
        return new float[]{rightX + (leftX - rightX) * dock, Math.max(8.0f, 56.0f) * dock + 8.0f * (1.0f - dock), 214.0f, this.dh - 16.0f - Math.max(0.0f, 48.0f) * dock};
    }

    private float[] inspectorTab() {
        float[] r = this.inspectorRect();
        boolean left = this.dockAnim.get() > 0.5f;
        return new float[]{left ? r[0] + r[2] : r[0] - 16.0f, r[1] + 50.0f, 16.0f, 34.0f};
    }

    private void renderInspector(UiContext ui) {
        boolean leftDock;
        Theme t = ui.theme;
        this.inspectorAnim.update(this.inspectorHidden ? 0.0f : 1.0f, ui.dt);
        if (this.dragging == null) {
            this.dockAnim.update(this.dockLeft() ? 1.0f : 0.0f, ui.dt);
        }
        float[] r = this.inspectorRect();
        float x = r[0];
        float y = r[1];
        float w = r[2];
        float h = r[3];
        float[] tb = this.inspectorTab();
        float tabX = tb[0];
        float tabY = tb[1];
        boolean bl = leftDock = this.dockAnim.get() > 0.5f;
        if (leftDock) {
            Render2D.roundRect(ui.ctx, tabX, tabY, 16.0f, 34.0f, 0.0f, 5.0f, 5.0f, 0.0f, Paint.solid(ColorUtil.withAlpha(t.bg2(), 0.95f)));
        } else {
            Render2D.roundRect(ui.ctx, tabX, tabY, 16.0f, 34.0f, 5.0f, 0.0f, 0.0f, 5.0f, Paint.solid(ColorUtil.withAlpha(t.bg2(), 0.95f)));
        }
        Icons.draw(ui.ctx, this.inspectorHidden ? Icon.CHEVRON_DOWN : Icon.CHEVRON_RIGHT, tabX + 3.0f, tabY + 12.0f, 10.0f, ColorUtil.lerp(t.textDim(), t.text(), this.hover("itab", ui.hover(tabX, tabY, 16.0f, 34.0f), ui.dt)));
        if (this.inspectorAnim.get() < 0.02f) {
            return;
        }
        UiDraw.card(ui.ctx, t, x, y, w, h, 10.0f, 0.0f, true);
        Render2D.roundRect(ui.ctx, x, y, w, h, 10.0f, ColorUtil.withAlpha(t.bg(), 0.6f));
        String[] tabs = new String[]{"\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435", "\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b", "\u041f\u0440\u0435\u0441\u0435\u0442\u044b"};
        float tw = (w - 16.0f) / 3.0f;
        for (int i = 0; i < 3; ++i) {
            float bx = x + 8.0f + (float)i * tw;
            float by = y + 8.0f;
            boolean sel = this.tab == i;
            float hv = this.hover("tab" + i, ui.hover(bx, by, tw, 20.0f), ui.dt);
            if (sel) {
                Render2D.roundRect(ui.ctx, bx, by, tw - 3.0f, 20.0f, 6.0f, Paint.horizontal(bx, bx + tw, ColorUtil.withAlpha(t.accent(), 0.6f), ColorUtil.withAlpha(t.accent2(), 0.3f)));
                Render2D.outline(ui.ctx, bx, by, tw - 3.0f, 20.0f, 6.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.9f));
            } else if (hv > 0.01f) {
                Render2D.roundRect(ui.ctx, bx, by, tw - 3.0f, 20.0f, 6.0f, ColorUtil.withAlpha(t.accent(), 0.15f * hv));
            }
            Fonts.drawCentered(ui.ctx, tabs[i], bx + (tw - 3.0f) / 2.0f, by + 6.4f, 6.8f, sel ? t.text() : t.textDim(), sel);
        }
        float cy = y + 36.0f;
        if (this.tab == 0) {
            if (this.selected != null) {
                UiDraw.iconBadge(ui.ctx, t, x + 10.0f, cy, 22.0f, this.selected.getIcon(), true);
                Fonts.draw(ui.ctx, Fonts.trim(this.selected.getName(), w - 50.0f, 8.6f, true), x + 38.0f, cy + 2.0f, 8.6f, t.text(), true);
                Fonts.draw(ui.ctx, Math.round(this.selected.scaledWidth()) + " \u00d7 " + Math.round(this.selected.scaledHeight()), x + 38.0f, cy + 13.0f, 6.0f, t.textMuted(), false);
            } else {
                Fonts.draw(ui.ctx, "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", x + 10.0f, cy + 3.0f, 7.6f, t.text(), true);
                Fonts.draw(ui.ctx, "\u0422\u044f\u043d\u0438\u0442\u0435 \u2014 \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u0435, \u0443\u0433\u043b\u044b \u2014 \u0440\u0430\u0437\u043c\u0435\u0440", x + 10.0f, cy + 14.0f, 6.2f, t.textMuted(), false);
            }
            cy += 30.0f;
        }
        float viewTop = cy;
        float viewH = y + h - 10.0f - viewTop;
        float maxScroll = Math.max(0.0f, this.inspectorContentH - viewH);
        this.inspectorScroll = Math.max(0.0f, Math.min(maxScroll, this.inspectorScroll));
        float ry = viewTop - this.inspectorScroll;
        for (Widget row : this.rows) {
            if (!row.isVisible()) continue;
            ry += row.layout(x + 10.0f, ry, w - 20.0f) + 4.0f;
        }
        this.inspectorContentH = ry + this.inspectorScroll - viewTop;
        ui.setClip(x, viewTop, w, viewH);
        ui.ctx.enableScissor((int)x, (int)viewTop, (int)(x + w), (int)(viewTop + viewH));
        for (Widget row : this.rows) {
            if (!row.isVisible() || !(row.y + row.h >= viewTop) || !(row.y <= viewTop + viewH)) continue;
            row.render(ui);
        }
        ui.ctx.disableScissor();
        ui.clearClip();
        if (maxScroll > 0.0f) {
            float barH = Math.max(16.0f, viewH * viewH / this.inspectorContentH);
            float barY = viewTop + (viewH - barH) * (this.inspectorScroll / maxScroll);
            Render2D.roundRect(ui.ctx, x + w - 4.0f, barY, 2.0f, barH, 1.0f, ColorUtil.withAlpha(t.accent(), 0.7f));
        }
    }

    private boolean inInspector(double mx, double my) {
        if (this.inspectorHidden) {
            return false;
        }
        float[] r = this.inspectorRect();
        return mx >= (double)r[0] && mx < (double)(r[0] + r[2]) && my >= (double)r[1] && my < (double)(r[1] + r[3]);
    }

    private void renderToast(UiContext ui) {
        if (this.toast == null) {
            return;
        }
        long age = System.currentTimeMillis() - this.toastAt;
        if (age > 2400L) {
            this.toast = null;
            return;
        }
        float a = Math.min(1.0f, (float)age / 150.0f) * Math.min(1.0f, (float)(2400L - age) / 300.0f);
        Theme t = ui.theme;
        float tw = Fonts.width(this.toast, 7.4f, true) + 30.0f;
        float x = (this.dw - tw) / 2.0f;
        float y = this.dh - 70.0f;
        float prev = Render2D.alpha;
        Render2D.alpha *= a;
        Render2D.roundRect(ui.ctx, x, y, tw, 20.0f, 10.0f, Paint.horizontal(x, x + tw, t.bg2(), ColorUtil.lerp(t.bg2(), t.accent(), 0.25f)));
        Render2D.outline(ui.ctx, x, y, tw, 20.0f, 10.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.8f));
        Render2D.sparkle(ui.ctx, x + 11.0f, y + 10.0f, 4.0f, 0.0f, t.accent2());
        Fonts.draw(ui.ctx, this.toast, x + 20.0f, y + 6.4f, 7.4f, t.text(), true);
        Render2D.alpha = prev;
    }

    private HudModule elementAt(float mx, float my) {
        List<HudModule> list = ModuleManager.hudModules();
        for (int i = list.size() - 1; i >= 0; --i) {
            HudModule h = list.get(i);
            if (!h.isEnabled()) continue;
            float x = h.getX(this.width);
            float y = h.getY(this.height);
            if (!(mx >= x - 2.0f) || !(mx < x + h.scaledWidth() + 2.0f) || !(my >= y - 2.0f) || !(my < y + h.scaledHeight() + 2.0f)) continue;
            return h;
        }
        return null;
    }

    private int cornerAt(HudModule hud, float mx, float my) {
        if (hud == null) {
            return -1;
        }
        float[][] c = this.corners(hud);
        for (int i = 0; i < 4; ++i) {
            if (!(Math.abs(mx - c[i][0]) <= 4.0f) || !(Math.abs(my - c[i][1]) <= 4.0f)) continue;
            return i;
        }
        return -1;
    }

    private void dragTo(float mx, float my) {
        HudModule hud = this.dragging;
        HudEditorPrefs p = this.prefs();
        float w = hud.scaledWidth();
        float h = hud.scaledHeight();
        float x = mx - this.grabX;
        float y = my - this.grabY;
        this.guides.clear();
        if (((Boolean)p.snapGrid.get()).booleanValue()) {
            float g = p.gridSize.getFloat();
            x = (float)Math.round(x / g) * g;
            y = (float)Math.round(y / g) * g;
        }
        boolean snappedX = false;
        boolean snappedY = false;
        if (((Boolean)p.snapCenter.get()).booleanValue()) {
            if (Math.abs(x + w / 2.0f - (float)this.width / 2.0f) < 4.0f) {
                x = (float)this.width / 2.0f - w / 2.0f;
                this.guides.add(new float[]{0.0f, (float)this.width / 2.0f});
                snappedX = true;
            }
            if (Math.abs(y + h / 2.0f - (float)this.height / 2.0f) < 4.0f) {
                y = (float)this.height / 2.0f - h / 2.0f;
                this.guides.add(new float[]{1.0f, (float)this.height / 2.0f});
                snappedY = true;
            }
        }
        if (((Boolean)p.snapElements.get()).booleanValue()) {
            block0: for (HudModule other : ModuleManager.hudModules()) {
                if (other == hud || !other.isEnabled()) continue;
                float ox = other.getX(this.width);
                float oy = other.getY(this.height);
                float ow = other.scaledWidth();
                float oh = other.scaledHeight();
                if (!snappedX) {
                    float[][] xs;
                    for (float[] pair : xs = new float[][]{{x, ox}, {x, ox + ow}, {x + w, ox + ow}, {x + w, ox}, {x + w / 2.0f, ox + ow / 2.0f}}) {
                        if (!(Math.abs(pair[0] - pair[1]) < 4.0f)) continue;
                        x += pair[1] - pair[0];
                        this.guides.add(new float[]{0.0f, pair[1]});
                        snappedX = true;
                        break;
                    }
                }
                if (snappedY) continue;
                float[][] ys = new float[][]{{y, oy}, {y, oy + oh}, {y + h, oy + oh}, {y + h, oy}, {y + h / 2.0f, oy + oh / 2.0f}};
                for (float[] pair : ys) {
                    if (!(Math.abs(pair[0] - pair[1]) < 4.0f)) continue;
                    y += pair[1] - pair[0];
                    this.guides.add(new float[]{1.0f, pair[1]});
                    snappedY = true;
                    continue block0;
                }
            }
        }
        if (Math.abs(x) < 4.0f) {
            x = 0.0f;
        }
        if (Math.abs(x + w - (float)this.width) < 4.0f) {
            x = (float)this.width - w;
        }
        if (Math.abs(y) < 4.0f) {
            y = 0.0f;
        }
        if (Math.abs(y + h - (float)this.height) < 4.0f) {
            y = (float)this.height - h;
        }
        hud.setTopLeft(Math.max(0.0f, Math.min((float)this.width - w, x)), Math.max(0.0f, Math.min((float)this.height - h, y)), this.width, this.height);
    }

    private void resizeTo(float mx, float my) {
        HudModule hud = this.dragging;
        float dist = Math.max(Math.abs(mx - this.resizeFixedX) / Math.max(1.0f, hud.width()), Math.abs(my - this.resizeFixedY) / Math.max(1.0f, hud.height()));
        float scale = this.resizeStartScale * dist / Math.max(0.01f, this.resizeStartDist);
        double pct = Math.max(hud.scale.getMin(), Math.min(hud.scale.getMax(), (double)(Math.round(scale * 100.0f / 5.0f) * 5)));
        hud.scale.set(pct);
        float w = hud.scaledWidth();
        float h = hud.scaledHeight();
        float x = this.resizeCorner == 1 || this.resizeCorner == 2 ? this.resizeFixedX : this.resizeFixedX - w;
        float y = this.resizeCorner >= 2 ? this.resizeFixedY : this.resizeFixedY - h;
        hud.setTopLeft(Math.max(0.0f, Math.min((float)this.width - w, x)), Math.max(0.0f, Math.min((float)this.height - h, y)), this.width, this.height);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float[] b;
        float mx = (float)mouseX;
        float my = (float)mouseY;
        this.ui.mx = mx / this.s;
        this.ui.my = my / this.s;
        double dx = this.ui.mx;
        double dy = this.ui.my;
        this.ui.blocked = false;
        if (this.popup != null) {
            if (this.popup.mouseClicked(this.ui, dx, dy, button)) {
                return true;
            }
            this.closePopup();
            return true;
        }
        if (this.focus != null && !this.focus.contains(dx, dy)) {
            this.setFocus(null);
        }
        if (dx >= (double)(b = this.topBar())[0] && dx < (double)(b[0] + b[2]) && dy >= (double)b[1] && dy < (double)(b[1] + b[3])) {
            for (int i = 0; i < 3; ++i) {
                float[] r = this.topButton(i);
                if (!(dx >= (double)r[0]) || !(dx < (double)(r[0] + r[2])) || !(dy >= (double)r[1]) || !(dy < (double)(r[1] + r[3]))) continue;
                switch (i) {
                    case 0: {
                        HudPresetManager.load("\u041f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e");
                        this.toast("\u0420\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0430 \u0441\u0431\u0440\u043e\u0448\u0435\u043d\u0430 \u043a \u0441\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u043e\u0439");
                        this.rebuildRows();
                        break;
                    }
                    case 1: {
                        this.tab = 2;
                        this.inspectorHidden = false;
                        this.rebuildRows();
                        break;
                    }
                    default: {
                        ConfigManager.save();
                        this.toast("\u0420\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0430 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430 \u0432 \u043f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + ConfigManager.activeProfile() + "\u00bb");
                    }
                }
                return true;
            }
            float cx = b[0] + b[2] - 24.0f;
            float cy = b[1] + 12.0f;
            if (dx >= (double)(cx - 2.0f) && dx < (double)(cx + 18.0f) && dy >= (double)(cy - 2.0f) && dy < (double)(cy + 18.0f)) {
                this.onClose();
                return true;
            }
        }
        float[] r = this.inspectorRect();
        float[] tb = this.inspectorTab();
        if (dx >= (double)tb[0] && dx < (double)(tb[0] + tb[2]) && dy >= (double)tb[1] && dy < (double)(tb[1] + tb[3])) {
            this.inspectorHidden = !this.inspectorHidden;
            return true;
        }
        if (this.inInspector(dx, dy)) {
            float tw = (r[2] - 16.0f) / 3.0f;
            if (dy >= (double)(r[1] + 8.0f) && dy < (double)(r[1] + 28.0f)) {
                int t = (int)((dx - (double)r[0] - 8.0) / (double)tw);
                if (t >= 0 && t < 3 && t != this.tab) {
                    this.tab = t;
                    this.rebuildRows();
                }
                return true;
            }
            for (Widget row : new ArrayList<Widget>(this.rows)) {
                if (!row.isVisible() || !this.ui.inClip(dx, dy) || !row.contains(dx, dy) || !row.mouseClicked(this.ui, dx, dy, button)) continue;
                return true;
            }
            for (Widget row : new ArrayList<Widget>(this.rows)) {
                if (!row.isVisible() || !row.contains(dx, dy) || !row.mouseClicked(this.ui, dx, dy, button)) continue;
                return true;
            }
            return true;
        }
        int corner = this.cornerAt(this.selected, mx, my);
        if (corner >= 0 && button == 0) {
            this.dragging = this.selected;
            this.resizeCorner = corner;
            float x = this.selected.getX(this.width);
            float y = this.selected.getY(this.height);
            float w = this.selected.scaledWidth();
            float h = this.selected.scaledHeight();
            this.resizeFixedX = corner == 1 || corner == 2 ? x : x + w;
            this.resizeFixedY = corner >= 2 ? y : y + h;
            this.resizeStartScale = this.selected.getScale();
            this.resizeStartDist = Math.max(w / Math.max(1.0f, this.selected.width()), h / Math.max(1.0f, this.selected.height()));
            return true;
        }
        HudModule hit = this.elementAt(mx, my);
        if (hit != null) {
            this.select(hit);
            if (button == 0) {
                this.dragging = hit;
                this.resizeCorner = -1;
                this.grabX = mx - hit.getX(this.width);
                this.grabY = my - hit.getY(this.height);
            }
            if (this.tab != 0) {
                this.tab = 0;
                this.rebuildRows();
            }
            return true;
        }
        this.select(null);
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        this.ui.mx = (float)mouseX / this.s;
        this.ui.my = (float)mouseY / this.s;
        if (this.popup != null && this.popup.mouseDragged(this.ui, this.ui.mx, this.ui.my, button)) {
            return true;
        }
        if (this.dragging != null) {
            if (this.resizeCorner >= 0) {
                this.resizeTo((float)mouseX, (float)mouseY);
            } else {
                this.dragTo((float)mouseX, (float)mouseY);
            }
            return true;
        }
        for (Widget row : this.rows) {
            if (!row.isVisible() || !row.mouseDragged(this.ui, this.ui.mx, this.ui.my, button)) continue;
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.ui.mx = (float)mouseX / this.s;
        this.ui.my = (float)mouseY / this.s;
        if (this.popup != null) {
            this.popup.mouseReleased(this.ui, this.ui.mx, this.ui.my, button);
        }
        if (this.dragging != null) {
            this.dragging = null;
            this.resizeCorner = -1;
            this.guides.clear();
            ConfigManager.markDirty();
            if (this.tab == 0) {
                this.rebuildRows();
            }
        }
        for (Widget row : this.rows) {
            row.mouseReleased(this.ui, this.ui.mx, this.ui.my, button);
        }
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        double dx = mouseX / (double)this.s;
        double dy = mouseY / (double)this.s;
        if (this.popup != null && this.popup.mouseScrolled(dx, dy, vertical)) {
            return true;
        }
        if (this.inInspector(dx, dy)) {
            this.inspectorScroll -= (float)vertical * 24.0f;
            return true;
        }
        HudModule hit = this.elementAt((float)mouseX, (float)mouseY);
        if (hit != null) {
            hit.scale.set((Double)hit.scale.get() + (double)(vertical > 0.0 ? 5 : -5));
            this.select(hit);
            return true;
        }
        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focus != null && this.focus.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256 && this.popup != null) {
            this.closePopup();
            return true;
        }
        if (this.selected != null) {
            float step = (modifiers & 1) != 0 ? 10.0f : 1.0f;
            float x = this.selected.getX(this.width);
            float y = this.selected.getY(this.height);
            switch (keyCode) {
                case 263: {
                    this.selected.setTopLeft(Math.max(0.0f, x - step), y, this.width, this.height);
                    return true;
                }
                case 262: {
                    this.selected.setTopLeft(Math.min((float)this.width - this.selected.scaledWidth(), x + step), y, this.width, this.height);
                    return true;
                }
                case 265: {
                    this.selected.setTopLeft(x, Math.max(0.0f, y - step), this.width, this.height);
                    return true;
                }
                case 264: {
                    this.selected.setTopLeft(x, Math.min((float)this.height - this.selected.scaledHeight(), y + step), this.width, this.height);
                    return true;
                }
                case 261: {
                    this.selected.setEnabled(false);
                    this.toast("\u00ab" + this.selected.getName() + "\u00bb \u0441\u043a\u0440\u044b\u0442 (\u0432\u043a\u043b\u044e\u0447\u0438\u0442\u0435 \u0432\u043e \u0432\u043a\u043b\u0430\u0434\u043a\u0435 \u00ab\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b\u00bb)");
                    this.select(null);
                    return true;
                }
            }
        }
        if (keyCode == 72) {
            this.inspectorHidden = !this.inspectorHidden;
            return true;
        }
        return super.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
    }

    public boolean charTyped(char chr, int modifiers) {
        return this.focus != null && this.focus.charTyped(chr, modifiers);
    }

    public void onClose() {
        ConfigManager.save();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void openPopup(Popup p) {
        this.popup = p;
        if (p != null) {
            p.fit(this.dw, this.dh);
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

    private final class SwatchRow
    extends Widget {
        private final HudModule hud;

        SwatchRow(HudModule hud) {
            this.hud = hud;
        }

        @Override
        protected float measure(float width) {
            return 18.0f;
        }

        @Override
        public void render(UiContext ui) {
            float step = this.w / (float)SWATCHES.length;
            for (int i = 0; i < SWATCHES.length; ++i) {
                float cx = this.x + step * (float)i + step / 2.0f;
                float cy = this.y + 9.0f;
                boolean sel = (Boolean)this.hud.themeAccent.get() == false && this.hud.accent.getColor() == SWATCHES[i];
                boolean hov = ui.hover(cx - 7.0f, cy - 7.0f, 14.0f, 14.0f);
                if (sel || hov) {
                    Render2D.ring(ui.ctx, cx, cy, 7.5f, 1.1f, sel ? -1 : -2130706433);
                }
                Render2D.circle(ui.ctx, cx, cy, 5.5f, SWATCHES[i]);
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (!this.contains(mx, my)) {
                return false;
            }
            int i = (int)((mx - (double)this.x) / (double)(this.w / (float)SWATCHES.length));
            if (i >= 0 && i < SWATCHES.length) {
                this.hud.themeAccent.set(false);
                this.hud.accent.set(SWATCHES[i]);
                HudEditorScreen.this.rebuildRows();
            }
            return true;
        }
    }

    private static final class SectionTitle
    extends Widget {
        private final String text;
        private final Icon icon;

        SectionTitle(String text, Icon icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        protected float measure(float width) {
            return 20.0f;
        }

        @Override
        public void render(UiContext ui) {
            Render2D.rect(ui.ctx, this.x, this.y + 3.0f, this.w, 0.7f, Paint.horizontal(this.x, this.x + this.w, ColorUtil.withAlpha(ui.theme.accent(), 0.5f), 0));
            Icons.draw(ui.ctx, this.icon, this.x, this.y + 8.0f, 9.0f, ui.theme.accent());
            Fonts.draw(ui.ctx, this.text, this.x + 13.0f, this.y + 9.0f, 7.4f, ui.theme.text(), true);
        }
    }

    private final class ElementRow
    extends Widget {
        private final HudModule hud;
        private final Animation on;

        ElementRow(HudModule hud) {
            this.hud = hud;
            this.on = new Animation(hud.isEnabled() ? 1.0f : 0.0f, 14.0f);
        }

        @Override
        protected float measure(float width) {
            return 22.0f;
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            boolean sel = this.hud == HudEditorScreen.this.selected;
            float hv = HudEditorScreen.this.hover("el" + this.hud.getId(), ui.hover(this.x, this.y, this.w, this.h), ui.dt);
            Render2D.roundRect(ui.ctx, this.x, this.y, this.w, this.h, 5.0f, sel ? ColorUtil.withAlpha(t.accent(), 0.3f) : ColorUtil.withAlpha(t.accent(), 0.08f + 0.1f * hv));
            Icons.draw(ui.ctx, this.hud.getIcon(), this.x + 5.0f, this.y + 5.0f, 12.0f, this.hud.isEnabled() ? t.accent() : t.textMuted());
            Fonts.draw(ui.ctx, Fonts.trim(this.hud.getName(), this.w - 60.0f, 7.0f, false), this.x + 22.0f, this.y + 7.5f, 7.0f, this.hud.isEnabled() ? t.text() : t.textDim(), sel);
            UiDraw.toggle(ui.ctx, t, this.x + this.w - 22.0f - 4.0f, this.y + 5.0f, this.on.update(this.hud.isEnabled() ? 1.0f : 0.0f, ui.dt), 0.0f);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (!this.contains(mx, my)) {
                return false;
            }
            if (mx >= (double)(this.x + this.w - 22.0f - 8.0f)) {
                this.hud.toggle();
            } else {
                if (!this.hud.isEnabled()) {
                    this.hud.setEnabled(true);
                }
                HudEditorScreen.this.selected = this.hud;
                HudEditorScreen.this.tab = 0;
                HudEditorScreen.this.rebuildRows();
            }
            return true;
        }
    }

    private final class PresetRow
    extends Widget {
        private final String name;

        PresetRow(String name) {
            this.name = name;
        }

        @Override
        protected float measure(float width) {
            return 22.0f;
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            float hv = HudEditorScreen.this.hover("pr" + this.name, ui.hover(this.x, this.y, this.w, this.h), ui.dt);
            Render2D.roundRect(ui.ctx, this.x, this.y, this.w, this.h, 5.0f, ColorUtil.withAlpha(t.accent(), 0.08f + 0.12f * hv));
            Icons.draw(ui.ctx, Icon.LAYERS, this.x + 5.0f, this.y + 5.0f, 12.0f, t.accent());
            Fonts.draw(ui.ctx, Fonts.trim(this.name, this.w - 70.0f, 7.0f, false), this.x + 22.0f, this.y + 7.5f, 7.0f, t.text(), false);
            UiDraw.iconButton(ui.ctx, t, this.x + this.w - 42.0f, this.y + 2.0f, 18.0f, Icon.DOWNLOAD, ui.hover(this.x + this.w - 42.0f, this.y + 2.0f, 18.0f, 18.0f) ? 1.0f : 0.0f);
            if (!"\u041f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e".equals(this.name)) {
                UiDraw.iconButton(ui.ctx, t, this.x + this.w - 20.0f, this.y + 2.0f, 18.0f, Icon.TRASH, ui.hover(this.x + this.w - 20.0f, this.y + 2.0f, 18.0f, 18.0f) ? 1.0f : 0.0f);
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (!this.contains(mx, my)) {
                return false;
            }
            if (mx >= (double)(this.x + this.w - 20.0f) && !"\u041f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e".equals(this.name)) {
                HudPresetManager.delete(this.name);
                HudEditorScreen.this.toast("\u041f\u0440\u0435\u0441\u0435\u0442 \u00ab" + this.name + "\u00bb \u0443\u0434\u0430\u043b\u0451\u043d");
                HudEditorScreen.this.rebuildRows();
            } else {
                HudPresetManager.load(this.name);
                HudEditorScreen.this.toast("\u041f\u0440\u0435\u0441\u0435\u0442 \u00ab" + this.name + "\u00bb \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d");
            }
            return true;
        }
    }
}

