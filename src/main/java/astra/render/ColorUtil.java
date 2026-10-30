/*
 * Decompiled with CFR 0.152.
 */
package astra.render;

import java.awt.Color;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static int alpha(int c) {
        return c >>> 24 & 0xFF;
    }

    public static int red(int c) {
        return c >> 16 & 0xFF;
    }

    public static int green(int c) {
        return c >> 8 & 0xFF;
    }

    public static int blue(int c) {
        return c & 0xFF;
    }

    public static int argb(int a, int r, int g, int b) {
        return ColorUtil.clamp(a) << 24 | ColorUtil.clamp(r) << 16 | ColorUtil.clamp(g) << 8 | ColorUtil.clamp(b);
    }

    private static int clamp(int v) {
        return v < 0 ? 0 : Math.min(255, v);
    }

    public static int withAlpha(int c, float a) {
        return Math.round(Math.max(0.0f, Math.min(1.0f, a)) * 255.0f) << 24 | c & 0xFFFFFF;
    }

    public static int mulAlpha(int c, float m) {
        if (m >= 0.999f) {
            return c;
        }
        if (m <= 0.0f) {
            return c & 0xFFFFFF;
        }
        int a = (int)((float)(c >>> 24 & 0xFF) * m + 0.5f);
        return a << 24 | c & 0xFFFFFF;
    }

    public static int lerp(int a, int b, float t) {
        if (t <= 0.0f) {
            return a;
        }
        if (t >= 1.0f) {
            return b;
        }
        int k = (int)(t * 256.0f + 0.5f);
        int aa = a >>> 24 & 0xFF;
        int ar = a >> 16 & 0xFF;
        int ag = a >> 8 & 0xFF;
        int ab = a & 0xFF;
        int ba = b >>> 24 & 0xFF;
        int br = b >> 16 & 0xFF;
        int bg = b >> 8 & 0xFF;
        int bb = b & 0xFF;
        return aa + ((ba - aa) * k >> 8) << 24 | ar + ((br - ar) * k >> 8) << 16 | ag + ((bg - ag) * k >> 8) << 8 | ab + ((bb - ab) * k >> 8);
    }

    public static int brighten(int c, float amount) {
        return ColorUtil.lerp(c, c & 0xFF000000 | 0xFFFFFF, amount);
    }

    public static int darken(int c, float amount) {
        int dark = c & 0xFF000000;
        return ColorUtil.lerp(c, dark, amount);
    }

    public static float[] toHsv(int c) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(ColorUtil.red(c), ColorUtil.green(c), ColorUtil.blue(c), hsv);
        return hsv;
    }

    public static int fromHsv(float h, float s, float v, int alpha) {
        int rgb = Color.HSBtoRGB(h, s, v) & 0xFFFFFF;
        return alpha << 24 | rgb;
    }

    public static int rainbow(float offset, float speed, float s, float v) {
        float hue = (float)((double)(System.currentTimeMillis() % 100000L) / 1000.0 * (double)speed + (double)offset) % 1.0f;
        return ColorUtil.fromHsv(hue, s, v, 255);
    }
}

