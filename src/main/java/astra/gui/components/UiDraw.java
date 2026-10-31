/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.gui.components;

import astra.gui.style.GuiStyle;
import astra.module.ClientSettings;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class UiDraw {
    public static final float TOGGLE_W = 22.0f;
    public static final float TOGGLE_H = 12.0f;

    private UiDraw() {
    }

    public static float glowStrength() {
        return ClientSettings.glowFactor();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void card(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, float h, float r, float hover, boolean active) {
        GuiStyle.record(ctx, "style.card", x, y, w, h);
        t = GuiStyle.theme("style.card", t);
        r = GuiStyle.radius("style.card", r);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.card");
        try {
            float glow = UiDraw.glowStrength();
            if (glow > 0.0f) {
                Render2D.glow(ctx, x, y, w, h, r, 10.0f, ColorUtil.withAlpha(ColorUtil.lerp(t.accent(), t.accent2(), hover), (0.07f + 0.2f * hover + (active ? 0.05f : 0.0f)) * glow));
            }
            int fillTop = ColorUtil.lerp(t.card(), t.cardHover(), hover);
            int fillBottom = ColorUtil.lerp(ColorUtil.darken(t.card(), 0.18f), t.card(), hover * 0.6f);
            Render2D.roundRect(ctx, x, y, w, h, r, Paint.vertical(y, y + h, fillTop, fillBottom));
            float a = 0.34f + 0.4f * hover;
            Render2D.outline(ctx, x, y, w, h, r, 0.9f, Paint.linear(x, y, x + w, y + h, ColorUtil.withAlpha(t.accent(), a + 0.1f), ColorUtil.withAlpha(t.accent2(), a * 0.8f)));
            Render2D.rect(ctx, x + r, y + 0.6f, w - r * 2.0f, 0.6f, Paint.horizontal(x + r, x + w - r, 0xFFFFFF, ColorUtil.withAlpha(0xFFFFFF, 0.08f + 0.06f * hover)));
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void toggle(GuiGraphicsExtractor ctx, Theme t, float x, float y, float progress, float hover) {
        GuiStyle.record(ctx, "style.toggle", x, y, 22.0f, 12.0f);
        t = GuiStyle.theme("style.toggle", t);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.toggle");
        try {
            float w = 22.0f;
            float h = 12.0f;
            float r = h / 2.0f;
            int off = ColorUtil.lerp(t.track(), ColorUtil.brighten(t.track(), 0.12f), hover);
            if (progress > 0.01f && UiDraw.glowStrength() > 0.0f) {
                Render2D.glow(ctx, x, y, w, h, r, 6.0f, ColorUtil.withAlpha(t.accent2(), 0.45f * progress * UiDraw.glowStrength()));
            }
            Render2D.roundRect(ctx, x, y, w, h, r, off);
            if (progress > 0.01f) {
                Render2D.roundRect(ctx, x, y, w, h, r, Paint.horizontal(x, x + w, ColorUtil.mulAlpha(t.accent(), progress), ColorUtil.mulAlpha(t.accent2(), progress)));
            }
            float kx = x + r + (w - h) * progress;
            Render2D.shadow(ctx, kx - 3.6f, y + r - 3.6f, 7.2f, 7.2f, 3.6f, 1.6f, 0x50000000);
            Render2D.circle(ctx, kx, y + r, 3.9f, ColorUtil.lerp(-2502422, -1, progress));
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void sliderTrack(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, float pct, float hover) {
        GuiStyle.record(ctx, "style.slider", x, y - 5.0f, w, 10.0f);
        t = GuiStyle.theme("style.slider", t);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.slider");
        try {
            float th = 3.0f;
            float cy = y;
            Render2D.roundRect(ctx, x, cy - th / 2.0f, w, th, th / 2.0f, t.track());
            float fw = Math.max(th, w * pct);
            Render2D.roundRect(ctx, x, cy - th / 2.0f, fw, th, th / 2.0f, Paint.horizontal(x, x + w, t.accent(), t.accent2()));
            float kx = x + w * pct;
            float kr = 4.0f + hover * 0.8f;
            if (UiDraw.glowStrength() > 0.0f) {
                Render2D.glow(ctx, kx - kr, cy - kr, kr * 2.0f, kr * 2.0f, kr, 5.0f, ColorUtil.withAlpha(t.accent(), 0.45f * UiDraw.glowStrength()));
            }
            Render2D.circle(ctx, kx, cy, kr, -1);
            Render2D.circle(ctx, kx, cy, kr - 1.6f, ColorUtil.lerp(t.accent(), -1, 0.25f));
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void dropdownBox(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, float h, String value, float hover, boolean open) {
        GuiStyle.record(ctx, "style.dropdown", x, y, w, h);
        t = GuiStyle.theme("style.dropdown", t);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.dropdown");
        try {
            float dr = GuiStyle.radius("style.dropdown", 4.0f);
            Render2D.roundRect(ctx, x, y, w, h, dr, ColorUtil.lerp(ColorUtil.withAlpha(t.bg(), 0.75f), ColorUtil.withAlpha(t.bg2(), 0.95f), hover));
            Render2D.outline(ctx, x, y, w, h, dr, 0.7f, ColorUtil.withAlpha(t.accent(), open ? 0.8f : 0.22f + 0.3f * hover));
            float size = 7.0f;
            Fonts.draw(ctx, Fonts.trim(value, w - 20.0f, size, false), x + 6.0f, y + (h - size) / 2.0f + 0.4f, size, t.text(), false);
            Icons.draw(ctx, Icon.CHEVRON_DOWN, x + w - 12.0f, y + h / 2.0f - 3.5f, 7.0f, open ? t.accent() : t.textDim());
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void button(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, float h, String label, Icon icon, float hover, boolean primary) {
        GuiStyle.record(ctx, "style.button", x, y, w, h);
        t = GuiStyle.theme("style.button", t);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.button");
        try {
            float tw;
            float r = GuiStyle.radius("style.button", 5.0f);
            if (primary) {
                if (UiDraw.glowStrength() > 0.0f) {
                    Render2D.glow(ctx, x, y, w, h, r, 6.0f, ColorUtil.withAlpha(t.accent(), (0.25f + 0.25f * hover) * UiDraw.glowStrength()));
                }
                Render2D.roundRect(ctx, x, y, w, h, r, Paint.horizontal(x, x + w, ColorUtil.lerp(t.accent(), -1, hover * 0.12f), ColorUtil.lerp(t.accent2(), -1, hover * 0.12f)));
            } else {
                Render2D.roundRect(ctx, x, y, w, h, r, ColorUtil.lerp(ColorUtil.withAlpha(t.bg2(), 0.85f), ColorUtil.withAlpha(t.cardHover(), 1.0f), hover));
                Render2D.outline(ctx, x, y, w, h, r, 0.8f, ColorUtil.withAlpha(t.accent(), 0.3f + 0.4f * hover));
            }
            float size = 7.2f;
            float iconSize = icon != null ? 9.0f : 0.0f;
            float avail = w - 10.0f - (icon != null ? iconSize + 4.0f : 0.0f);
            if (!label.isEmpty() && Fonts.width(label, size, true) > avail) {
                label = Fonts.trim(label, avail, size, true);
            }
            float f = tw = label.isEmpty() ? 0.0f : Fonts.width(label, size, true);
            float total = tw + (icon != null ? iconSize + (float)(label.isEmpty() ? 0 : 4) : 0.0f);
            float sx = x + (w - total) / 2.0f;
            if (icon != null) {
                Icons.draw(ctx, icon, sx, y + (h - iconSize) / 2.0f, iconSize, t.text());
                sx += iconSize + 4.0f;
            }
            if (!label.isEmpty()) {
                Fonts.draw(ctx, label, sx, y + (h - size) / 2.0f + 0.4f, size, t.text(), true);
            }
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void iconButton(GuiGraphicsExtractor ctx, Theme t, float x, float y, float size, Icon icon, float hover) {
        GuiStyle.record(ctx, "style.button", x, y, size, size);
        t = GuiStyle.theme("style.button", t);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.button");
        try {
            Render2D.roundRect(ctx, x, y, size, size, 5.0f, ColorUtil.lerp(ColorUtil.withAlpha(t.bg2(), 0.85f), t.cardHover(), hover));
            Render2D.outline(ctx, x, y, size, size, 5.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.28f + 0.45f * hover));
            float is = size * 0.5f;
            Icons.draw(ctx, icon, x + (size - is) / 2.0f, y + (size - is) / 2.0f, is, ColorUtil.lerp(t.textDim(), t.text(), hover));
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void iconBadge(GuiGraphicsExtractor ctx, Theme t, float x, float y, float size, Icon icon, boolean active) {
        GuiStyle.record(ctx, "style.badge", x, y, size, size);
        t = GuiStyle.theme("style.badge", t);
        float styleAlphaPrev = GuiStyle.styleAlpha("style.badge");
        try {
            if (active && UiDraw.glowStrength() > 0.0f) {
                Render2D.glow(ctx, x, y, size, size, size * 0.28f, size * 0.35f, ColorUtil.withAlpha(t.accent(), 0.3f * UiDraw.glowStrength()));
            }
            Render2D.roundRect(ctx, x, y, size, size, size * 0.28f, Paint.linear(x, y, x + size, y + size, ColorUtil.withAlpha(t.accent(), 0.45f), ColorUtil.withAlpha(t.accent2(), 0.25f)));
            Render2D.outline(ctx, x, y, size, size, size * 0.28f, 1.0f, Paint.linear(x, y, x + size, y + size, t.accent(), t.accent2()));
            float is = size * 0.6f;
            Icons.drawNeon(ctx, icon, x + (size - is) / 2.0f, y + (size - is) / 2.0f, is, -1, t.accent2(), 1.0f);
        }
        finally {
            Render2D.alpha = styleAlphaPrev;
        }
    }

    public static void keyCap(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, float h, String label, float pressed, float textSize) {
        if (pressed > 0.01f && UiDraw.glowStrength() > 0.0f) {
            Render2D.glow(ctx, x, y, w, h, 3.5f, 6.0f, ColorUtil.withAlpha(t.accent(), 0.5f * pressed * UiDraw.glowStrength()));
        }
        Render2D.roundRect(ctx, x, y, w, h, 3.5f, ColorUtil.lerp(ColorUtil.withAlpha(t.bg(), 0.7f), ColorUtil.withAlpha(t.accent(), 0.55f), pressed));
        Render2D.outline(ctx, x, y, w, h, 3.5f, 0.9f, ColorUtil.lerp(ColorUtil.withAlpha(t.accent(), 0.55f), t.accent2(), pressed));
        Fonts.drawCentered(ctx, label, x + w / 2.0f, y + (h - textSize) / 2.0f + 0.4f, textSize, -1, true);
    }
}

