/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuSampler
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.gui.render.TextureSetup
 *  net.minecraft.client.gui.render.state.GuiElementRenderState
 *  net.minecraft.client.gui.render.state.GuiRenderState
 *  net.minecraft.client.renderer.RenderPipelines
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fStack
 *  org.joml.Matrix3x2fc
 *  org.joml.Vector2f
 */
package astra.render;

import astra.render.ColorUtil;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.ShapeRenderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

public final class Render2D {
    public static float alpha = 1.0f;
    private static final Mesh MESH = new Mesh(false);
    private static final Mesh TEX_MESH = new Mesh(true);
    private static float[] P = new float[1024];
    private static float[] N = new float[1024];
    private static final Matrix3x2f BATCH_POSE = new Matrix3x2f();
    private static final Vector2f TMP = new Vector2f();
    private static GuiRenderState batchState;
    private static ScreenRectangle batchScissor;
    private static float[] arenaPos;
    private static int[] arenaCol;
    private static int arenaUsed;
    private static int batchStart;
    private static int batchCount;
    private static int bx0;
    private static int by0;
    private static int bx1;
    private static int by1;
    public static boolean flushing;
    private static final int[] TMP_BOUNDS;
    private static final int MAX_SEGS = 28;
    private static final float[][] TRIG_COS;
    private static final float[][] TRIG_SIN;
    private static int segCap;
    private static final float[] RADII;
    private static final float[] CXS;
    private static final float[] CYS;
    private static final int[] SEGS;

    private Render2D() {
    }

    public static float pixel(GuiGraphics ctx) {
        double gui;
        Matrix3x2fStack m = ctx.method_51448();
        float scale = (float)Math.sqrt(m.m00 * m.m00 + m.m01 * m.m01);
        float s = (float)((double)scale * (gui = (double)Minecraft.method_1551().method_22683().method_4495()));
        return s <= 1.0E-4f ? 1.0f : 1.0f / s;
    }

    public static Mesh begin() {
        MESH.reset();
        return MESH;
    }

    public static void end(GuiGraphics ctx) {
        Render2D.submit(ctx, MESH, RenderPipelines.field_56879, TextureSetup.method_70899());
    }

    private static boolean screenBounds(Matrix3x2f pose, Mesh m, ScreenRectangle scissor) {
        float x0 = Float.MAX_VALUE;
        float y0 = Float.MAX_VALUE;
        float x1 = -3.4028235E38f;
        float y1 = -3.4028235E38f;
        for (int i = 0; i < 4; ++i) {
            pose.transformPosition((i & 1) == 0 ? m.minX : m.maxX, i < 2 ? m.minY : m.maxY, TMP);
            x0 = Math.min(x0, Render2D.TMP.x);
            y0 = Math.min(y0, Render2D.TMP.y);
            x1 = Math.max(x1, Render2D.TMP.x);
            y1 = Math.max(y1, Render2D.TMP.y);
        }
        int ix0 = (int)Math.floor(x0);
        int iy0 = (int)Math.floor(y0);
        int ix1 = (int)Math.ceil(x1);
        int iy1 = (int)Math.ceil(y1);
        if (ix1 <= ix0) {
            ix1 = ix0 + 1;
        }
        if (iy1 <= iy0) {
            iy1 = iy0 + 1;
        }
        if (scissor != null) {
            ix0 = Math.max(ix0, scissor.method_49620());
            iy0 = Math.max(iy0, scissor.method_49618());
            ix1 = Math.min(ix1, scissor.method_49621());
            iy1 = Math.min(iy1, scissor.method_49619());
            if (ix1 <= ix0 || iy1 <= iy0) {
                return false;
            }
        }
        Render2D.TMP_BOUNDS[0] = ix0;
        Render2D.TMP_BOUNDS[1] = iy0;
        Render2D.TMP_BOUNDS[2] = ix1;
        Render2D.TMP_BOUNDS[3] = iy1;
        return true;
    }

    private static boolean samePose(Matrix3x2f a, Matrix3x2f b) {
        return a.m00 == b.m00 && a.m01 == b.m01 && a.m10 == b.m10 && a.m11 == b.m11 && a.m20 == b.m20 && a.m21 == b.m21;
    }

    public static void submit(GuiGraphics ctx, Mesh m, RenderPipeline pipeline, TextureSetup textures) {
        ScreenRectangle scissor;
        if (m.count == 0) {
            return;
        }
        Matrix3x2fStack cur = ctx.method_51448();
        if (!Render2D.screenBounds((Matrix3x2f)cur, m, scissor = ctx.field_44659.method_70863())) {
            return;
        }
        if (m.uv == null && pipeline == RenderPipelines.field_56879) {
            if (!(batchCount <= 0 || batchState == ctx.field_59826 && Render2D.samePose(BATCH_POSE, (Matrix3x2f)cur) && Objects.equals(batchScissor, scissor))) {
                Render2D.flushBatch();
            }
            if (batchCount == 0) {
                batchStart = arenaUsed;
                batchState = ctx.field_59826;
                BATCH_POSE.set((Matrix3x2fc)cur);
                batchScissor = scissor;
                bx0 = TMP_BOUNDS[0];
                by0 = TMP_BOUNDS[1];
                bx1 = TMP_BOUNDS[2];
                by1 = TMP_BOUNDS[3];
            } else {
                bx0 = Math.min(bx0, TMP_BOUNDS[0]);
                by0 = Math.min(by0, TMP_BOUNDS[1]);
                bx1 = Math.max(bx1, TMP_BOUNDS[2]);
                by1 = Math.max(by1, TMP_BOUNDS[3]);
            }
            if ((arenaUsed + m.count) * 2 > arenaPos.length) {
                int size = Math.max(arenaPos.length * 2, (batchCount + m.count) * 4);
                float[] np = new float[size];
                int[] nc = new int[size / 2];
                System.arraycopy(arenaPos, batchStart * 2, np, 0, batchCount * 2);
                System.arraycopy(arenaCol, batchStart, nc, 0, batchCount);
                arenaPos = np;
                arenaCol = nc;
                batchStart = 0;
                arenaUsed = batchCount;
            }
            System.arraycopy(m.pos, 0, arenaPos, arenaUsed * 2, m.count * 2);
            System.arraycopy(m.col, 0, arenaCol, arenaUsed, m.count);
            arenaUsed += m.count;
            batchCount += m.count;
            return;
        }
        Render2D.flushBatch();
        ScreenRectangle bounds = new ScreenRectangle(TMP_BOUNDS[0], TMP_BOUNDS[1], TMP_BOUNDS[2] - TMP_BOUNDS[0], TMP_BOUNDS[3] - TMP_BOUNDS[1]);
        ctx.field_59826.method_70919((GuiElementRenderState)new ShapeRenderState(pipeline, textures, new Matrix3x2f((Matrix3x2fc)cur), Arrays.copyOf(m.pos, m.count * 2), m.uv != null ? Arrays.copyOf(m.uv, m.count * 2) : null, Arrays.copyOf(m.col, m.count), 0, m.count, scissor, bounds));
    }

    public static void dropBatch() {
        batchCount = 0;
        batchState = null;
        arenaUsed = 0;
    }

    public static void flushBatch() {
        if (batchCount == 0 || flushing) {
            return;
        }
        flushing = true;
        try {
            ScreenRectangle bounds = new ScreenRectangle(bx0, by0, Math.max(1, bx1 - bx0), Math.max(1, by1 - by0));
            batchState.method_70919((GuiElementRenderState)new ShapeRenderState(RenderPipelines.field_56879, TextureSetup.method_70899(), new Matrix3x2f((Matrix3x2fc)BATCH_POSE), arenaPos, null, arenaCol, batchStart, batchCount, batchScissor, bounds));
        }
        finally {
            batchCount = 0;
            batchState = null;
            flushing = false;
        }
    }

    private static int clear(int c) {
        return c & 0xFFFFFF;
    }

    private static void ensurePerimeter(int points) {
        if (points * 2 > P.length) {
            P = new float[points * 4];
            N = new float[points * 4];
        }
    }

    private static int segmentsFor(float radius, float px) {
        float rp = radius / Math.max(1.0E-4f, px);
        return Math.max(3, Math.min(segCap, (int)Math.ceil(Math.sqrt(rp) * 2.0)));
    }

    private static int perimeter(float x, float y, float w, float h, float rtl, float rtr, float rbr, float rbl, float px, float extraRadius) {
        float maxR = Math.min(w, h) / 2.0f;
        float[] radii = RADII;
        float[] cxs = CXS;
        float[] cys = CYS;
        int[] segs = SEGS;
        radii[0] = Math.min(rtl, maxR);
        radii[1] = Math.min(rtr, maxR);
        radii[2] = Math.min(rbr, maxR);
        radii[3] = Math.min(rbl, maxR);
        cxs[0] = x + radii[0];
        cxs[1] = x + w - radii[1];
        cxs[2] = x + w - radii[2];
        cxs[3] = x + radii[3];
        cys[0] = y + radii[0];
        cys[1] = y + radii[1];
        cys[2] = y + h - radii[2];
        cys[3] = y + h - radii[3];
        int total = 0;
        for (int c = 0; c < 4; ++c) {
            segs[c] = radii[c] < px * 0.5f && extraRadius <= 0.0f ? 0 : Render2D.segmentsFor(radii[c] + extraRadius, px);
            total += segs[c] + 1;
        }
        Render2D.ensurePerimeter(total + 2);
        int n = 0;
        for (int c = 0; c < 4; ++c) {
            float r = radii[c];
            if (segs[c] == 0) {
                Render2D.P[n * 2] = c == 0 || c == 3 ? x : x + w;
                Render2D.P[n * 2 + 1] = c <= 1 ? y : y + h;
                Render2D.N[n * 2] = c == 0 || c == 3 ? -1.0f : 1.0f;
                Render2D.N[n * 2 + 1] = c <= 1 ? -1.0f : 1.0f;
                ++n;
                continue;
            }
            float[] tc = TRIG_COS[segs[c]];
            float[] ts = TRIG_SIN[segs[c]];
            for (int i = 0; i <= segs[c]; ++i) {
                float cos;
                float ct = tc[i];
                float st = ts[i];
                float sin = switch (c) {
                    case 0 -> {
                        cos = -ct;
                        yield -st;
                    }
                    case 1 -> {
                        cos = st;
                        yield -ct;
                    }
                    case 2 -> {
                        cos = ct;
                        yield st;
                    }
                    default -> {
                        cos = -st;
                        yield ct;
                    }
                };
                Render2D.P[n * 2] = cxs[c] + cos * r;
                Render2D.P[n * 2 + 1] = cys[c] + sin * r;
                Render2D.N[n * 2] = cos;
                Render2D.N[n * 2 + 1] = sin;
                ++n;
            }
        }
        return n;
    }

    public static void addRoundRect(Mesh m, float x, float y, float w, float h, float rtl, float rtr, float rbr, float rbl, Paint paint, float px) {
        if (w <= 0.0f || h <= 0.0f) {
            return;
        }
        int n = Render2D.perimeter(x, y, w, h, rtl, rtr, rbr, rbl, px, 0.0f);
        float hf = px * 0.5f;
        float cx = x + w / 2.0f;
        float cy = y + h / 2.0f;
        int cc = paint.at(cx, cy);
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            float ix = P[i * 2] - N[i * 2] * hf;
            float iy = P[i * 2 + 1] - N[i * 2 + 1] * hf;
            float jx = P[j * 2] - N[j * 2] * hf;
            float jy = P[j * 2 + 1] - N[j * 2 + 1] * hf;
            float ox = P[i * 2] + N[i * 2] * hf;
            float oy = P[i * 2 + 1] + N[i * 2 + 1] * hf;
            float qx = P[j * 2] + N[j * 2] * hf;
            float qy = P[j * 2 + 1] + N[j * 2 + 1] * hf;
            int ci = paint.at(ix, iy);
            int cj = paint.at(jx, jy);
            m.tri(cx, cy, cc, ix, iy, ci, jx, jy, cj);
            m.quad(ix, iy, ci, ox, oy, Render2D.clear(ci), qx, qy, Render2D.clear(cj), jx, jy, cj);
        }
    }

    public static void addOutline(Mesh m, float x, float y, float w, float h, float rtl, float rtr, float rbr, float rbl, float thickness, Paint paint, float px) {
        if (w <= 0.0f || h <= 0.0f || thickness <= 0.0f) {
            return;
        }
        int n = Render2D.perimeter(x, y, w, h, rtl, rtr, rbr, rbl, px, 0.0f);
        float hf = px * 0.5f;
        float core = Math.max(0.0f, thickness - px);
        float fade = thickness < px ? thickness / px : 1.0f;
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            float pix = P[i * 2];
            float piy = P[i * 2 + 1];
            float nix = N[i * 2];
            float niy = N[i * 2 + 1];
            float pjx = P[j * 2];
            float pjy = P[j * 2 + 1];
            float njx = N[j * 2];
            float njy = N[j * 2 + 1];
            int ci = ColorUtil.mulAlpha(paint.at(pix, piy), fade);
            int cj = ColorUtil.mulAlpha(paint.at(pjx, pjy), fade);
            float a0x = pix + nix * hf;
            float a0y = piy + niy * hf;
            float a1x = pjx + njx * hf;
            float a1y = pjy + njy * hf;
            float b0x = pix - nix * hf;
            float b0y = piy - niy * hf;
            float b1x = pjx - njx * hf;
            float b1y = pjy - njy * hf;
            m.quad(a0x, a0y, Render2D.clear(ci), b0x, b0y, ci, b1x, b1y, cj, a1x, a1y, Render2D.clear(cj));
            float c0x = b0x - nix * core;
            float c0y = b0y - niy * core;
            float c1x = b1x - njx * core;
            float c1y = b1y - njy * core;
            if (core > 0.0f) {
                m.quad(b0x, b0y, ci, c0x, c0y, ci, c1x, c1y, cj, b1x, b1y, cj);
            }
            float d0x = c0x - nix * px;
            float d0y = c0y - niy * px;
            float d1x = c1x - njx * px;
            float d1y = c1y - njy * px;
            m.quad(c0x, c0y, ci, d0x, d0y, Render2D.clear(ci), d1x, d1y, Render2D.clear(cj), c1x, c1y, cj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addGlow(Mesh m, float x, float y, float w, float h, float r, float spread, int color, boolean fillInside, float px) {
        int n;
        if (spread <= 0.0f || ColorUtil.alpha(color) == 0) {
            return;
        }
        segCap = 8;
        try {
            n = Render2D.perimeter(x, y, w, h, r, r, r, r, px, spread * 0.6f);
        }
        finally {
            segCap = 28;
        }
        int c1 = ColorUtil.mulAlpha(color, 0.42f);
        int c2 = ColorUtil.mulAlpha(color, 0.12f);
        float s1 = spread * 0.3f;
        float s2 = spread * 0.62f;
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            float pix = P[i * 2];
            float piy = P[i * 2 + 1];
            float nix = N[i * 2];
            float niy = N[i * 2 + 1];
            float pjx = P[j * 2];
            float pjy = P[j * 2 + 1];
            float njx = N[j * 2];
            float njy = N[j * 2 + 1];
            m.quad(pix, piy, color, pix + nix * s1, piy + niy * s1, c1, pjx + njx * s1, pjy + njy * s1, c1, pjx, pjy, color);
            m.quad(pix + nix * s1, piy + niy * s1, c1, pix + nix * s2, piy + niy * s2, c2, pjx + njx * s2, pjy + njy * s2, c2, pjx + njx * s1, pjy + njy * s1, c1);
            m.quad(pix + nix * s2, piy + niy * s2, c2, pix + nix * spread, piy + niy * spread, Render2D.clear(color), pjx + njx * spread, pjy + njy * spread, Render2D.clear(color), pjx + njx * s2, pjy + njy * s2, c2);
        }
        if (fillInside) {
            float cx = x + w / 2.0f;
            float cy = y + h / 2.0f;
            for (int i = 0; i < n; ++i) {
                int j = (i + 1) % n;
                m.tri(cx, cy, color, P[i * 2], P[i * 2 + 1], color, P[j * 2], P[j * 2 + 1], color);
            }
        }
    }

    public static void addArc(Mesh m, float cx, float cy, float radius, float width, float startDeg, float sweepDeg, int color, float px) {
        if (width <= 0.0f || sweepDeg == 0.0f) {
            return;
        }
        int segs = Math.max(4, (int)Math.ceil((double)(Math.abs(sweepDeg) / 360.0f) * Math.sqrt(radius / px) * 10.0));
        float hw = width / 2.0f;
        float hf = px * 0.5f;
        float core = Math.max(0.0f, hw - hf);
        int c = width < px ? ColorUtil.mulAlpha(color, width / px) : color;
        float[] radii = new float[]{radius + core + px, radius + core, radius - core, radius - core - px};
        int[] cols = new int[]{Render2D.clear(c), c, c, Render2D.clear(c)};
        for (int i = 0; i < segs; ++i) {
            double a0 = Math.toRadians(startDeg + sweepDeg * (float)i / (float)segs);
            double a1 = Math.toRadians(startDeg + sweepDeg * (float)(i + 1) / (float)segs);
            float c0 = (float)Math.cos(a0);
            float s0 = (float)Math.sin(a0);
            float c1 = (float)Math.cos(a1);
            float s1 = (float)Math.sin(a1);
            for (int b = 0; b < 3; ++b) {
                float ra = Math.max(0.0f, radii[b]);
                float rb = Math.max(0.0f, radii[b + 1]);
                m.quad(cx + c0 * ra, cy + s0 * ra, cols[b], cx + c0 * rb, cy + s0 * rb, cols[b + 1], cx + c1 * rb, cy + s1 * rb, cols[b + 1], cx + c1 * ra, cy + s1 * ra, cols[b]);
            }
        }
    }

    public static void addCircle(Mesh m, float cx, float cy, float r, Paint paint, float px) {
        Render2D.addRoundRect(m, cx - r, cy - r, r * 2.0f, r * 2.0f, r, r, r, r, paint, px);
    }

    public static void addLine(Mesh m, float x1, float y1, float x2, float y2, float width, int color, boolean roundCaps, float px) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float)Math.sqrt(dx * dx + dy * dy);
        if (len < 1.0E-4f) {
            return;
        }
        float nx = -dy / len;
        float ny = dx / len;
        float hw = width / 2.0f;
        float hf = px * 0.5f;
        float core = Math.max(0.0f, hw - hf);
        int c = width < px ? ColorUtil.mulAlpha(color, width / px) : color;
        float[] off = new float[]{core + px, core, -core, -core - px};
        int[] cols = new int[]{Render2D.clear(c), c, c, Render2D.clear(c)};
        for (int b = 0; b < 3; ++b) {
            float oa = off[b];
            float ob = off[b + 1];
            m.quad(x1 + nx * oa, y1 + ny * oa, cols[b], x1 + nx * ob, y1 + ny * ob, cols[b + 1], x2 + nx * ob, y2 + ny * ob, cols[b + 1], x2 + nx * oa, y2 + ny * oa, cols[b]);
        }
        if (roundCaps && width > px * 1.5f) {
            Render2D.addArc(m, x1, y1, hw * 0.5f, hw, (float)Math.toDegrees(Math.atan2(ny, nx)), 180.0f, color, px);
            Render2D.addArc(m, x2, y2, hw * 0.5f, hw, (float)Math.toDegrees(Math.atan2(-ny, -nx)), 180.0f, color, px);
        }
    }

    public static void addPolygon(Mesh m, float[] pts, int n, float cx, float cy, Paint paint, float px) {
        if (n < 3) {
            return;
        }
        float area = 0.0f;
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            area += pts[i * 2] * pts[j * 2 + 1] - pts[j * 2] * pts[i * 2 + 1];
        }
        float sign = area >= 0.0f ? 1.0f : -1.0f;
        Render2D.ensurePerimeter(n + 2);
        for (int i = 0; i < n; ++i) {
            int p = (i - 1 + n) % n;
            int q = (i + 1) % n;
            float e1x = pts[i * 2] - pts[p * 2];
            float e1y = pts[i * 2 + 1] - pts[p * 2 + 1];
            float e2x = pts[q * 2] - pts[i * 2];
            float e2y = pts[q * 2 + 1] - pts[i * 2 + 1];
            float l1 = (float)Math.max(1.0E-5, Math.hypot(e1x, e1y));
            float l2 = (float)Math.max(1.0E-5, Math.hypot(e2x, e2y));
            float n1x = e1y / l1 * sign;
            float n1y = -e1x / l1 * sign;
            float n2x = e2y / l2 * sign;
            float n2y = -e2x / l2 * sign;
            float nx = n1x + n2x;
            float ny = n1y + n2y;
            float nl = (float)Math.max(1.0E-5, Math.hypot(nx, ny));
            float dot = Math.max(0.35f, (nx /= nl) * n1x + (ny /= nl) * n1y);
            Render2D.N[i * 2] = nx / dot;
            Render2D.N[i * 2 + 1] = ny / dot;
        }
        float hf = px * 0.5f;
        int cc = paint.at(cx, cy);
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            float ix = pts[i * 2] - N[i * 2] * hf;
            float iy = pts[i * 2 + 1] - N[i * 2 + 1] * hf;
            float jx = pts[j * 2] - N[j * 2] * hf;
            float jy = pts[j * 2 + 1] - N[j * 2 + 1] * hf;
            float ox = pts[i * 2] + N[i * 2] * hf;
            float oy = pts[i * 2 + 1] + N[i * 2 + 1] * hf;
            float qx = pts[j * 2] + N[j * 2] * hf;
            float qy = pts[j * 2 + 1] + N[j * 2 + 1] * hf;
            int ci = paint.at(ix, iy);
            int cj = paint.at(jx, jy);
            m.tri(cx, cy, cc, ix, iy, ci, jx, jy, cj);
            m.quad(ix, iy, ci, ox, oy, Render2D.clear(ci), qx, qy, Render2D.clear(cj), jx, jy, cj);
        }
    }

    public static void addPetal(Mesh m, float cx, float cy, float size, float rotDeg, Paint paint, float px) {
        int n = 18;
        float[] pts = new float[n * 2];
        double rot = Math.toRadians(rotDeg);
        float cos = (float)Math.cos(rot);
        float sin = (float)Math.sin(rot);
        for (int i = 0; i < n; ++i) {
            double t = Math.PI * 2 * (double)i / (double)n;
            float lx = (float)Math.sin(t) * size * 0.55f;
            float ly = (float)(-Math.cos(t)) * size;
            if (ly > 0.0f) {
                lx *= 1.0f - 0.55f * (ly / size);
            }
            if (Math.abs(lx) < size * 0.18f && ly < -size * 0.75f) {
                ly = -size * 0.72f;
            }
            pts[i * 2] = cx + lx * cos - ly * sin;
            pts[i * 2 + 1] = cy + lx * sin + ly * cos;
        }
        Render2D.addPolygon(m, pts, n, cx, cy, paint, px);
    }

    public static void addSparkle(Mesh m, float cx, float cy, float size, float rotDeg, int color, float px) {
        int n = 8;
        float[] pts = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            double a = Math.toRadians(rotDeg - 90.0f + (float)(i * 45));
            float r = i % 2 == 0 ? size : size * 0.28f;
            pts[i * 2] = cx + (float)Math.cos(a) * r;
            pts[i * 2 + 1] = cy + (float)Math.sin(a) * r;
        }
        Render2D.addPolygon(m, pts, n, cx, cy, Paint.solid(color), px);
    }

    public static void addStar(Mesh m, float cx, float cy, float size, float rotDeg, int color, float px) {
        int n = 10;
        float[] pts = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            double a = Math.toRadians(rotDeg - 90.0f + (float)(i * 36));
            float r = i % 2 == 0 ? size : size * 0.45f;
            pts[i * 2] = cx + (float)Math.cos(a) * r;
            pts[i * 2 + 1] = cy + (float)Math.sin(a) * r;
        }
        Render2D.addPolygon(m, pts, n, cx, cy, Paint.solid(color), px);
    }

    public static void addHeart(Mesh m, float cx, float cy, float size, int color, float px) {
        int n = 32;
        float[] pts = new float[n * 2];
        float k = size / 17.0f;
        for (int i = 0; i < n; ++i) {
            double t = Math.PI * 2 * (double)i / (double)n;
            float hx = (float)(16.0 * Math.pow(Math.sin(t), 3.0));
            float hy = (float)(-(13.0 * Math.cos(t) - 5.0 * Math.cos(2.0 * t) - 2.0 * Math.cos(3.0 * t) - Math.cos(4.0 * t)));
            pts[i * 2] = cx + hx * k;
            pts[i * 2 + 1] = cy + hy * k;
        }
        Render2D.addPolygon(m, pts, n, cx, cy + size * 0.1f, Paint.solid(color), px);
    }

    public static void rect(GuiGraphics ctx, float x, float y, float w, float h, int color) {
        Mesh m = Render2D.begin();
        m.rect(x, y, w, h, color);
        Render2D.end(ctx);
    }

    public static void rect(GuiGraphics ctx, float x, float y, float w, float h, Paint paint) {
        Mesh m = Render2D.begin();
        m.rect(x, y, w, h, paint);
        Render2D.end(ctx);
    }

    public static void roundRect(GuiGraphics ctx, float x, float y, float w, float h, float r, int color) {
        Render2D.roundRect(ctx, x, y, w, h, r, Paint.solid(color));
    }

    public static void roundRect(GuiGraphics ctx, float x, float y, float w, float h, float r, Paint paint) {
        Mesh m = Render2D.begin();
        Render2D.addRoundRect(m, x, y, w, h, r, r, r, r, paint, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void roundRect(GuiGraphics ctx, float x, float y, float w, float h, float rtl, float rtr, float rbr, float rbl, Paint paint) {
        Mesh m = Render2D.begin();
        Render2D.addRoundRect(m, x, y, w, h, rtl, rtr, rbr, rbl, paint, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void outline(GuiGraphics ctx, float x, float y, float w, float h, float r, float thickness, int color) {
        Render2D.outline(ctx, x, y, w, h, r, thickness, Paint.solid(color));
    }

    public static void outline(GuiGraphics ctx, float x, float y, float w, float h, float r, float thickness, Paint paint) {
        Mesh m = Render2D.begin();
        Render2D.addOutline(m, x, y, w, h, r, r, r, r, thickness, paint, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void glow(GuiGraphics ctx, float x, float y, float w, float h, float r, float spread, int color) {
        Mesh m = Render2D.begin();
        Render2D.addGlow(m, x, y, w, h, r, spread, color, false, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void shadow(GuiGraphics ctx, float x, float y, float w, float h, float r, float spread, int color) {
        Mesh m = Render2D.begin();
        Render2D.addGlow(m, x, y, w, h, r, spread, color, true, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void circle(GuiGraphics ctx, float cx, float cy, float r, int color) {
        Mesh m = Render2D.begin();
        Render2D.addCircle(m, cx, cy, r, Paint.solid(color), Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void circle(GuiGraphics ctx, float cx, float cy, float r, Paint paint) {
        Mesh m = Render2D.begin();
        Render2D.addCircle(m, cx, cy, r, paint, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void ring(GuiGraphics ctx, float cx, float cy, float r, float width, int color) {
        Render2D.arc(ctx, cx, cy, r, width, 0.0f, 360.0f, color);
    }

    public static void arc(GuiGraphics ctx, float cx, float cy, float r, float width, float start, float sweep, int color) {
        Mesh m = Render2D.begin();
        Render2D.addArc(m, cx, cy, r, width, start, sweep, color, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void line(GuiGraphics ctx, float x1, float y1, float x2, float y2, float width, int color) {
        Mesh m = Render2D.begin();
        Render2D.addLine(m, x1, y1, x2, y2, width, color, false, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void dashedLine(GuiGraphics ctx, float x1, float y1, float x2, float y2, float width, float dash, float gap, int color) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float)Math.sqrt(dx * dx + dy * dy);
        if (len < 0.001f) {
            return;
        }
        float ux = dx / len;
        float uy = dy / len;
        Mesh m = Render2D.begin();
        float px = Render2D.pixel(ctx);
        for (float t = 0.0f; t < len; t += dash + gap) {
            float e = Math.min(len, t + dash);
            Render2D.addLine(m, x1 + ux * t, y1 + uy * t, x1 + ux * e, y1 + uy * e, width, color, false, px);
        }
        Render2D.end(ctx);
    }

    public static void textureRoundRect(GuiGraphics ctx, Identifier id, float texAspect, float x, float y, float w, float h, float r, int color) {
        if (w <= 0.0f || h <= 0.0f) {
            return;
        }
        float px = Render2D.pixel(ctx);
        int n = Render2D.perimeter(x, y, w, h, r, r, r, r, px, 0.0f);
        float rectAspect = w / h;
        float uSpan = 1.0f;
        float vSpan = 1.0f;
        if (rectAspect > texAspect) {
            vSpan = texAspect / rectAspect;
        } else {
            uSpan = rectAspect / texAspect;
        }
        float u0 = (1.0f - uSpan) / 2.0f;
        float v0 = (1.0f - vSpan) / 2.0f;
        TEX_MESH.reset();
        float cx = x + w / 2.0f;
        float cy = y + h / 2.0f;
        float cu = u0 + uSpan / 2.0f;
        float cv = v0 + vSpan / 2.0f;
        for (int i = 0; i < n; ++i) {
            int j = (i + 1) % n;
            float ax = P[i * 2];
            float ay = P[i * 2 + 1];
            float bx = P[j * 2];
            float by = P[j * 2 + 1];
            TEX_MESH.triUv(cx, cy, cu, cv, color, ax, ay, u0 + (ax - x) / w * uSpan, v0 + (ay - y) / h * vSpan, color, bx, by, u0 + (bx - x) / w * uSpan, v0 + (by - y) / h * vSpan, color);
        }
        Render2D.submit(ctx, TEX_MESH, RenderPipelines.field_56883, TextureSetup.method_70900((GpuTextureView)Render2D.textureView(id), (GpuSampler)Render2D.linearSampler()));
    }

    public static void petal(GuiGraphics ctx, float cx, float cy, float size, float rot, Paint paint) {
        Mesh m = Render2D.begin();
        Render2D.addPetal(m, cx, cy, size, rot, paint, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void sparkle(GuiGraphics ctx, float cx, float cy, float size, float rot, int color) {
        Mesh m = Render2D.begin();
        Render2D.addSparkle(m, cx, cy, size, rot, color, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void star(GuiGraphics ctx, float cx, float cy, float size, float rot, int color) {
        Mesh m = Render2D.begin();
        Render2D.addStar(m, cx, cy, size, rot, color, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void triangle(GuiGraphics ctx, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        Mesh m = Render2D.begin();
        Render2D.addPolygon(m, new float[]{x1, y1, x2, y2, x3, y3}, 3, (x1 + x2 + x3) / 3.0f, (y1 + y2 + y3) / 3.0f, Paint.solid(color), Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    public static void heart(GuiGraphics ctx, float cx, float cy, float size, int color) {
        Mesh m = Render2D.begin();
        Render2D.addHeart(m, cx, cy, size, color, Render2D.pixel(ctx));
        Render2D.end(ctx);
    }

    private static GpuSampler linearSampler() {
        return RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR);
    }

    public static GpuTextureView textureView(Identifier id) {
        AbstractTexture tex = Minecraft.method_1551().method_1531().method_4619(id);
        return tex.method_71659();
    }

    public static void texture(GuiGraphics ctx, GpuTextureView view, float x, float y, float w, float h, float u0, float v0, float u1, float v1, int color) {
        TEX_MESH.reset();
        TEX_MESH.quadUv(x, y, u0, v0, color, x, y + h, u0, v1, color, x + w, y + h, u1, v1, color, x + w, y, u1, v0, color);
        Render2D.submit(ctx, TEX_MESH, RenderPipelines.field_56883, TextureSetup.method_70900((GpuTextureView)view, (GpuSampler)Render2D.linearSampler()));
    }

    public static void textureGradient(GuiGraphics ctx, Identifier id, float x, float y, float w, float h, int left, int right) {
        TEX_MESH.reset();
        TEX_MESH.quadUv(x, y, 0.0f, 0.0f, left, x, y + h, 0.0f, 1.0f, left, x + w, y + h, 1.0f, 1.0f, right, x + w, y, 1.0f, 0.0f, right);
        Render2D.submit(ctx, TEX_MESH, RenderPipelines.field_56883, TextureSetup.method_70900((GpuTextureView)Render2D.textureView(id), (GpuSampler)Render2D.linearSampler()));
    }

    public static void texture(GuiGraphics ctx, Identifier id, float x, float y, float w, float h, float u0, float v0, float u1, float v1, int color) {
        Render2D.texture(ctx, Render2D.textureView(id), x, y, w, h, u0, v0, u1, v1, color);
    }

    public static void texturedCircle(GuiGraphics ctx, GpuTextureView view, float cx, float cy, float r, float u, float v, float uvRadius, float rotationDeg, int color) {
        TEX_MESH.reset();
        int segs = 64;
        double rot = Math.toRadians(rotationDeg);
        for (int i = 0; i < segs; ++i) {
            double a0 = Math.PI * 2 * (double)i / (double)segs;
            double a1 = Math.PI * 2 * (double)(i + 1) / (double)segs;
            float x0 = cx + (float)Math.cos(a0) * r;
            float y0 = cy + (float)Math.sin(a0) * r;
            float x1 = cx + (float)Math.cos(a1) * r;
            float y1 = cy + (float)Math.sin(a1) * r;
            float u0 = u + (float)Math.cos(a0 + rot) * uvRadius;
            float v0 = v + (float)Math.sin(a0 + rot) * uvRadius;
            float u1 = u + (float)Math.cos(a1 + rot) * uvRadius;
            float v1 = v + (float)Math.sin(a1 + rot) * uvRadius;
            TEX_MESH.triUv(cx, cy, u, v, color, x0, y0, u0, v0, color, x1, y1, u1, v1, color);
        }
        Render2D.submit(ctx, TEX_MESH, RenderPipelines.field_56883, TextureSetup.method_70900((GpuTextureView)view, (GpuSampler)Render2D.linearSampler()));
    }

    public static void texturedSquare(GuiGraphics ctx, GpuTextureView view, float cx, float cy, float half, float u, float v, float uvHalf, float rotationDeg, int color) {
        TEX_MESH.reset();
        double rot = Math.toRadians(rotationDeg);
        float cos = (float)Math.cos(rot);
        float sin = (float)Math.sin(rot);
        float[][] c = new float[][]{{-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}};
        float[] q = new float[16];
        for (int i = 0; i < 4; ++i) {
            q[i * 4] = cx + c[i][0] * half;
            q[i * 4 + 1] = cy + c[i][1] * half;
            q[i * 4 + 2] = u + (c[i][0] * cos - c[i][1] * sin) * uvHalf;
            q[i * 4 + 3] = v + (c[i][0] * sin + c[i][1] * cos) * uvHalf;
        }
        TEX_MESH.quadUv(q[0], q[1], q[2], q[3], color, q[4], q[5], q[6], q[7], color, q[8], q[9], q[10], q[11], color, q[12], q[13], q[14], q[15], color);
        Render2D.submit(ctx, TEX_MESH, RenderPipelines.field_56883, TextureSetup.method_70900((GpuTextureView)view, (GpuSampler)Render2D.linearSampler()));
    }

    static {
        arenaPos = new float[131072];
        arenaCol = new int[65536];
        TMP_BOUNDS = new int[4];
        TRIG_COS = new float[29][];
        TRIG_SIN = new float[29][];
        for (int s = 1; s <= 28; ++s) {
            Render2D.TRIG_COS[s] = new float[s + 1];
            Render2D.TRIG_SIN[s] = new float[s + 1];
            for (int i = 0; i <= s; ++i) {
                double a = 1.5707963267948966 * (double)i / (double)s;
                Render2D.TRIG_COS[s][i] = (float)Math.cos(a);
                Render2D.TRIG_SIN[s][i] = (float)Math.sin(a);
            }
        }
        segCap = 28;
        RADII = new float[4];
        CXS = new float[4];
        CYS = new float[4];
        SEGS = new int[4];
    }
}

