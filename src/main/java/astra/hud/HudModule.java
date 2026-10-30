/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Matrix3x2fStack
 */
package astra.hud;

import astra.config.ConfigManager;
import astra.module.Category;
import astra.module.Module;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public abstract class HudModule
extends Module {
    public static final String FRAME_GLOW = "Astra Glow";
    public static final String FRAME_THIN = "\u0422\u043e\u043d\u043a\u0430\u044f";
    public static final String FRAME_NONE = "\u0411\u0435\u0437 \u0440\u0430\u043c\u043a\u0438";
    private final float defaultX;
    private final float defaultY;
    private float posX;
    private float posY;
    public final NumberSetting scale = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 100.0, 50.0, 250.0, 5.0).unit("%"));
    public final NumberSetting opacity = this.add((NumberSetting)new NumberSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 85.0, 0.0, 100.0, 1.0).unit("%").editorOnly());
    public final BooleanSetting background = this.add((BooleanSetting)new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0444\u043e\u043d", true).editorOnly());
    public final ModeSetting frame = this.add((ModeSetting)new ModeSetting("\u0421\u0442\u0438\u043b\u044c \u0440\u0430\u043c\u043a\u0438", "Astra Glow", "Astra Glow", "\u0422\u043e\u043d\u043a\u0430\u044f", "\u0411\u0435\u0437 \u0440\u0430\u043c\u043a\u0438").editorOnly());
    public final BooleanSetting themeAccent = this.add((BooleanSetting)new BooleanSetting("\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", true).editorOnly());
    public final ColorSetting accent = this.add((ColorSetting)((Setting)new ColorSetting("\u0426\u0432\u0435\u0442 \u0430\u043a\u0446\u0435\u043d\u0442\u0430", -5745153).editorOnly()).visibleWhen(() -> (Boolean)this.themeAccent.get() == false));

    protected HudModule(String id, String name, String description, Category category, Icon icon, float defaultX, float defaultY) {
        super(id, name, description, category, icon);
        this.defaultX = defaultX;
        this.defaultY = defaultY;
        this.posX = defaultX;
        this.posY = defaultY;
    }

    public abstract float width();

    public abstract float height();

    protected abstract void renderContent(GuiGraphics var1, float var2, float var3, boolean var4);

    public boolean shouldRender() {
        return true;
    }

    protected boolean drawsPanel() {
        return true;
    }

    public float getScale() {
        return this.scale.getFloat() / 100.0f;
    }

    public float scaledWidth() {
        return this.width() * this.getScale();
    }

    public float scaledHeight() {
        return this.height() * this.getScale();
    }

    public float getX(float screenW) {
        float w = this.scaledWidth();
        return HudModule.clamp(this.posX * screenW - w / 2.0f, 0.0f, Math.max(0.0f, screenW - w));
    }

    public float getY(float screenH) {
        float h = this.scaledHeight();
        return HudModule.clamp(this.posY * screenH - h / 2.0f, 0.0f, Math.max(0.0f, screenH - h));
    }

    public void setTopLeft(float x, float y, float screenW, float screenH) {
        this.posX = (x + this.scaledWidth() / 2.0f) / Math.max(1.0f, screenW);
        this.posY = (y + this.scaledHeight() / 2.0f) / Math.max(1.0f, screenH);
        ConfigManager.markDirty();
    }

    public boolean isAtDefault() {
        return this.posX == this.defaultX && this.posY == this.defaultY;
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public void setPos(float x, float y) {
        this.posX = HudModule.clamp(x, 0.0f, 1.0f);
        this.posY = HudModule.clamp(y, 0.0f, 1.0f);
    }

    public void resetPosition() {
        this.posX = this.defaultX;
        this.posY = this.defaultY;
        ConfigManager.markDirty();
    }

    private static float clamp(float v, float min, float max) {
        return v < min ? min : Math.min(max, v);
    }

    protected static Theme theme() {
        return ThemeManager.get();
    }

    public int accentColor() {
        return (Boolean)this.themeAccent.get() != false ? ThemeManager.get().accent() : this.accent.getColor();
    }

    public final void render(GuiGraphics ctx, float screenW, float screenH, boolean editor) {
        float x = this.getX(screenW);
        float y = this.getY(screenH);
        float s = this.getScale();
        Matrix3x2fStack m = ctx.method_51448();
        m.pushMatrix();
        m.translate(x, y);
        m.scale(s, s);
        float w = this.width();
        float h = this.height();
        if (this.drawsPanel() && ((Boolean)this.background.get()).booleanValue()) {
            this.drawPanel(ctx, w, h);
        }
        this.renderContent(ctx, w, h, editor);
        m.popMatrix();
    }

    protected void drawPanel(GuiGraphics ctx, float w, float h) {
        this.drawPanel(ctx, 0.0f, 0.0f, w, h, 5.0f);
    }

    protected void drawPanel(GuiGraphics ctx, float x, float y, float w, float h, float r) {
        Theme t = ThemeManager.get();
        float op = this.opacity.getFloat() / 100.0f;
        int acc = this.accentColor();
        String style = (String)this.frame.get();
        if (FRAME_GLOW.equals(style)) {
            Render2D.glow(ctx, x, y, w, h, r, 6.0f, ColorUtil.withAlpha(acc, 0.22f * op));
        }
        int top = ColorUtil.withAlpha(t.bg2(), 0.82f * op);
        int bottom = ColorUtil.withAlpha(t.bg(), 0.88f * op);
        Render2D.roundRect(ctx, x, y, w, h, r, Paint.vertical(y, y + h, top, bottom));
        if (FRAME_GLOW.equals(style)) {
            Render2D.outline(ctx, x, y, w, h, r, 1.0f, Paint.horizontal(x, x + w, ColorUtil.withAlpha(acc, 0.85f * Math.max(op, 0.4f)), ColorUtil.withAlpha(t.accent2(), 0.55f * Math.max(op, 0.4f))));
        } else if (FRAME_THIN.equals(style)) {
            Render2D.outline(ctx, x, y, w, h, r, 0.75f, ColorUtil.withAlpha(acc, 0.45f * Math.max(op, 0.4f)));
        }
    }
}

