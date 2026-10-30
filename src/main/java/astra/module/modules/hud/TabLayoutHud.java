/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public class TabLayoutHud
extends HudModule {
    private final NumberSetting bgOpacity = this.add(new NumberSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0444\u043e\u043d\u0430", 88.0, 0.0, 100.0, 5.0).unit("%"));
    private final ModeSetting bgColor = this.add(new ModeSetting("\u0426\u0432\u0435\u0442 \u0444\u043e\u043d\u0430", "\u0422\u0435\u043c\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439"));
    private final ColorSetting customBg = this.add((ColorSetting)new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0444\u043e\u043d\u0430", -15463388).visibleWhen(() -> this.bgColor.is("\u0421\u0432\u043e\u0439")));
    private final BooleanSetting outline = this.add(new BooleanSetting("\u041d\u0435\u043e\u043d\u043e\u0432\u0430\u044f \u043e\u0431\u0432\u043e\u0434\u043a\u0430", true));
    private final NumberSetting radius = this.add(new NumberSetting("\u0421\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u0435", 4.0, 0.0, 10.0, 1.0));
    private final NumberSetting entryOpacity = this.add(new NumberSetting("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430 \u0441\u0442\u0440\u043e\u043a", 14.0, 0.0, 60.0, 2.0).unit("%"));

    public TabLayoutHud() {
        super("tab_layout", "\u0422\u0430\u0431-\u043b\u0438\u0441\u0442", "\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435, \u043c\u0430\u0441\u0448\u0442\u0430\u0431 \u0438 \u0446\u0432\u0435\u0442\u0430 \u0441\u043f\u0438\u0441\u043a\u0430 \u0438\u0433\u0440\u043e\u043a\u043e\u0432 (Tab).", Category.HUD, Icon.USER, 0.5f, 0.2f);
        this.setDefaultEnabled(true);
    }

    public static TabLayoutHud get() {
        return ModuleManager.get(TabLayoutHud.class);
    }

    @Override
    public float width() {
        return 240.0f;
    }

    @Override
    public float height() {
        return 120.0f;
    }

    private boolean untouched() {
        return this.isAtDefault() && this.scale.getFloat() == 100.0f;
    }

    @Override
    public float getX(float screenW) {
        return this.untouched() ? (screenW - this.scaledWidth()) / 2.0f : super.getX(screenW);
    }

    @Override
    public float getY(float screenH) {
        return this.untouched() ? 10.0f : super.getY(screenH);
    }

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    public static boolean begin(GuiGraphics ctx) {
        TabLayoutHud m = TabLayoutHud.get();
        if (m == null || !m.isEnabled() || m.untouched()) {
            return false;
        }
        float sw = ctx.method_51421();
        float sh = ctx.method_51443();
        float s = m.getScale();
        float ax = m.getX(sw) + m.scaledWidth() / 2.0f;
        float ay = m.getY(sh);
        Matrix3x2fStack mat = ctx.method_51448();
        mat.pushMatrix();
        mat.translate(ax, ay);
        mat.scale(s, s);
        mat.translate(-sw / 2.0f, -10.0f);
        return true;
    }

    public int background(Theme t) {
        return this.bgColor.is("\u0421\u0432\u043e\u0439") ? this.customBg.getColor() : t.bg2();
    }

    public float bgAlpha() {
        return this.bgOpacity.getFloat() / 100.0f;
    }

    public boolean outline() {
        return (Boolean)this.outline.get();
    }

    public float radius() {
        return this.radius.getFloat();
    }

    public float entryAlpha() {
        return this.entryOpacity.getFloat() / 100.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        if (!editor) {
            return;
        }
        Theme t = TabLayoutHud.theme();
        int bg = this.background(t);
        float r = this.radius();
        Render2D.roundRect(ctx, 0.0f, 0.0f, w, h, r, Paint.vertical(0.0f, h, ColorUtil.withAlpha(bg, this.bgAlpha()), ColorUtil.withAlpha(ColorUtil.darken(bg, 0.2f), this.bgAlpha())));
        if (this.outline()) {
            Render2D.outline(ctx, 0.0f, 0.0f, w, h, r, 0.7f, ColorUtil.withAlpha(t.accent(), 0.6f));
        }
        Fonts.drawCentered(ctx, "\u0422\u0430\u0431-\u043b\u0438\u0441\u0442 \u00b7 MigosMC", w / 2.0f, 5.0f, 7.5f, t.accent2(), true);
        for (int i = 0; i < 8; ++i) {
            float x = 8.0f + (float)(i % 2) * (w / 2.0f - 4.0f);
            float y = 20 + i / 2 * 13;
            Render2D.roundRect(ctx, x, y, w / 2.0f - 12.0f, 11.0f, 2.0f, ColorUtil.withAlpha(t.accent(), this.entryAlpha()));
            Fonts.draw(ctx, "\u0418\u0433\u0440\u043e\u043a_" + (i + 1), x + 3.0f, y + 2.5f, 6.5f, t.text(), false);
            Fonts.drawRight(ctx, "" + (40 + i * 23), x + w / 2.0f - 15.0f, y + 2.5f, 6.0f, -8585294, false);
        }
    }
}

