/*
 * Decompiled with CFR 0.152.
 */
package astra.render;

import astra.render.ColorUtil;
import astra.render.Paint;
import astra.render.Render2D;
import java.util.Arrays;

public final class Mesh {
    float[] pos = new float[512];
    float[] uv;
    int[] col = new int[256];
    int count;
    final boolean textured;
    float minX;
    float minY;
    float maxX;
    float maxY;

    public Mesh(boolean textured) {
        this.textured = textured;
        if (textured) {
            this.uv = new float[512];
        }
        this.reset();
    }

    public void reset() {
        this.count = 0;
        this.minY = Float.MAX_VALUE;
        this.minX = Float.MAX_VALUE;
        this.maxY = -3.4028235E38f;
        this.maxX = -3.4028235E38f;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    private void ensure(int extra) {
        if ((this.count + extra) * 2 > this.pos.length) {
            int n = Math.max(this.pos.length * 2, (this.count + extra) * 2);
            this.pos = Arrays.copyOf(this.pos, n);
            if (this.uv != null) {
                this.uv = Arrays.copyOf(this.uv, n);
            }
            this.col = Arrays.copyOf(this.col, n / 2);
        }
    }

    private void put(float x, float y, float u, float v, int c) {
        int i = this.count++;
        this.pos[i * 2] = x;
        this.pos[i * 2 + 1] = y;
        if (this.uv != null) {
            this.uv[i * 2] = u;
            this.uv[i * 2 + 1] = v;
        }
        int n = this.col[i] = Render2D.alpha >= 1.0f ? c : ColorUtil.mulAlpha(c, Render2D.alpha);
        if (x < this.minX) {
            this.minX = x;
        }
        if (y < this.minY) {
            this.minY = y;
        }
        if (x > this.maxX) {
            this.maxX = x;
        }
        if (y > this.maxY) {
            this.maxY = y;
        }
    }

    public void quad(float x1, float y1, int c1, float x2, float y2, int c2, float x3, float y3, int c3, float x4, float y4, int c4) {
        this.quadUv(x1, y1, 0.0f, 0.0f, c1, x2, y2, 0.0f, 0.0f, c2, x3, y3, 0.0f, 0.0f, c3, x4, y4, 0.0f, 0.0f, c4);
    }

    public void quadUv(float x1, float y1, float u1, float v1, int c1, float x2, float y2, float u2, float v2, int c2, float x3, float y3, float u3, float v3, int c3, float x4, float y4, float u4, float v4, int c4) {
        if ((c1 | c2 | c3 | c4) >>> 24 == 0) {
            return;
        }
        this.ensure(4);
        float area = x1 * y2 - x2 * y1 + (x2 * y3 - x3 * y2) + (x3 * y4 - x4 * y3) + (x4 * y1 - x1 * y4);
        if (area > 0.0f) {
            this.put(x1, y1, u1, v1, c1);
            this.put(x4, y4, u4, v4, c4);
            this.put(x3, y3, u3, v3, c3);
            this.put(x2, y2, u2, v2, c2);
        } else {
            this.put(x1, y1, u1, v1, c1);
            this.put(x2, y2, u2, v2, c2);
            this.put(x3, y3, u3, v3, c3);
            this.put(x4, y4, u4, v4, c4);
        }
    }

    public void tri(float x1, float y1, int c1, float x2, float y2, int c2, float x3, float y3, int c3) {
        this.quad(x1, y1, c1, x2, y2, c2, x3, y3, c3, x3, y3, c3);
    }

    public void triUv(float x1, float y1, float u1, float v1, int c1, float x2, float y2, float u2, float v2, int c2, float x3, float y3, float u3, float v3, int c3) {
        this.quadUv(x1, y1, u1, v1, c1, x2, y2, u2, v2, c2, x3, y3, u3, v3, c3, x3, y3, u3, v3, c3);
    }

    public void rect(float x, float y, float w, float h, int color) {
        this.quad(x, y, color, x, y + h, color, x + w, y + h, color, x + w, y, color);
    }

    public void rect(float x, float y, float w, float h, Paint paint) {
        this.quad(x, y, paint.at(x, y), x, y + h, paint.at(x, y + h), x + w, y + h, paint.at(x + w, y + h), x + w, y, paint.at(x + w, y));
    }
}

