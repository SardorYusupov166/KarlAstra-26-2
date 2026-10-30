/*
 * Decompiled with CFR 0.152.
 */
package astra.render;

import astra.render.ColorUtil;

@FunctionalInterface
public interface Paint {
    public int at(float var1, float var2);

    public static Paint solid(int color) {
        return (x, y) -> color;
    }

    public static Paint vertical(float y0, float y1, int top, int bottom) {
        float span = Math.max(1.0E-4f, y1 - y0);
        return (x, y) -> ColorUtil.lerp(top, bottom, (y - y0) / span);
    }

    public static Paint horizontal(float x0, float x1, int left, int right) {
        float span = Math.max(1.0E-4f, x1 - x0);
        return (x, y) -> ColorUtil.lerp(left, right, (x - x0) / span);
    }

    public static Paint linear(float x0, float y0, float x1, float y1, int from, int to) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float len2 = Math.max(1.0E-4f, dx * dx + dy * dy);
        return (x, y) -> ColorUtil.lerp(from, to, ((x - x0) * dx + (y - y0) * dy) / len2);
    }
}

