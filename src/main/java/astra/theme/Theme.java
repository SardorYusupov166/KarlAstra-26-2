/*
 * Decompiled with CFR 0.152.
 */
package astra.theme;

import astra.render.ColorUtil;

public record Theme(String name, int bg, int bg2, int card, int cardHover, int accent, int accent2, int accent3, int text, int textDim, int textMuted, int track) {
    public static Theme lerp(Theme a, Theme b, float t) {
        if (t >= 1.0f) {
            return b;
        }
        if (t <= 0.0f) {
            return a;
        }
        return new Theme(b.name, ColorUtil.lerp(a.bg, b.bg, t), ColorUtil.lerp(a.bg2, b.bg2, t), ColorUtil.lerp(a.card, b.card, t), ColorUtil.lerp(a.cardHover, b.cardHover, t), ColorUtil.lerp(a.accent, b.accent, t), ColorUtil.lerp(a.accent2, b.accent2, t), ColorUtil.lerp(a.accent3, b.accent3, t), ColorUtil.lerp(a.text, b.text, t), ColorUtil.lerp(a.textDim, b.textDim, t), ColorUtil.lerp(a.textMuted, b.textMuted, t), ColorUtil.lerp(a.track, b.track, t));
    }

    public Theme withAccent(int accent) {
        return new Theme(this.name, this.bg, this.bg2, this.card, this.cardHover, accent, this.accent2, this.accent3, this.text, this.textDim, this.textMuted, this.track);
    }

    public int stroke() {
        return ColorUtil.withAlpha(this.accent, 0.22f);
    }
}

