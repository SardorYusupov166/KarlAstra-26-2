/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderPipelines
 *  net.minecraft.client.renderer.rendertype.RenderSetup
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package astra.render;

import astra.media.LyricFx;
import astra.render.ColorUtil;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class LyricLightning {
    private static final RenderType DEPTH = LyricLightning.layer(false);
    private static final RenderType OVERLAY = LyricLightning.layer(true);

    private LyricLightning() {
    }

    public static void init() {
    }

    private static RenderType layer(boolean overlay) {
        String name = overlay ? "lyric_lightning_overlay" : "lyric_lightning_depth";
        RenderPipeline pipeline = RenderPipelines.register((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RenderPipelines.DEBUG_FILLED_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"astra", (String)("pipeline/" + name))).withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS).withBlend(BlendFunction.TRANSLUCENT).withCull(false).withDepthWrite(false).withDepthTestFunction(overlay ? DepthTestFunction.NO_DEPTH_TEST : DepthTestFunction.LEQUAL_DEPTH_TEST).build());
        return RenderType.create((String)("astra_" + name), (RenderSetup)RenderSetup.builder((RenderPipeline)pipeline).bufferSize(4096).sortOnUpload().createRenderSetup());
    }

    public static float strikeAt(int index, int count, float duration) {
        return count <= 1 ? 0.0f : (float)index / (float)(count - 1) * duration * 0.9f;
    }

    public static Point[] path(int seed, float x, float y, float height) {
        Point[] points = new Point[9];
        float lean = (LyricLightning.noise(seed, 2) - 0.5f) * height * 0.5f;
        points[0] = new Point(x + lean, y - height);
        for (int i = 1; i < 8; ++i) {
            float p = (float)i / 8.0f;
            float zig = (float)(i % 2 == 0 ? -1 : 1) * (1.8f + LyricLightning.noise(seed + i, 4) * 3.8f);
            points[i] = new Point(x + lean * (1.0f - p) + zig * (1.0f - p * 0.55f), y - height * (1.0f - p));
        }
        points[8] = new Point(x, y);
        return points;
    }

    public static void draw(MultiBufferSource.BufferSource provider, Matrix4f base, List<LyricFx.Glyph> glyphs, float y, float age, float entryDuration, float duration, float height, float width, int branches, int color, float opacity, boolean overlay) {
        RenderType layer = overlay ? OVERLAY : DEPTH;
        VertexConsumer vertices = null;
        float total = LyricFx.totalWidth(glyphs) * 9.0f;
        int active = 0;
        for (int i = glyphs.size() - 1; i >= 0 && active < 8; --i) {
            float since;
            LyricFx.Glyph glyph = glyphs.get(i);
            if (glyph.ch.isBlank() || (since = age - LyricLightning.strikeAt(i, glyphs.size(), entryDuration)) < 0.0f || since > duration) continue;
            if (vertices == null) {
                vertices = provider.getBuffer(layer);
            }
            ++active;
            float fade = 1.0f - since / duration;
            float flicker = (int)(since * 45.0f) % 3 == 1 ? 0.68f : 1.0f;
            float alpha = Math.min(1.0f, opacity * fade * flicker);
            float x = (glyph.x + glyph.w / 2.0f + glyph.dx) * 9.0f - total / 2.0f;
            float hitY = y + glyph.dy * 9.0f - 3.0f;
            Point[] spine = LyricLightning.path(i * 37 + (int)(since * 30.0f), x, hitY, height);
            for (int pass = 0; pass < 3; ++pass) {
                float thickness = width * (pass == 0 ? 4.5f : (pass == 1 ? 1.8f : 0.65f));
                int tint = ColorUtil.withAlpha(pass == 2 ? -1 : color, alpha * (pass == 0 ? 0.12f : (pass == 1 ? 0.72f : 0.95f)));
                for (int j = 1; j < spine.length; ++j) {
                    LyricLightning.segment(vertices, base, spine[j - 1], spine[j], thickness, tint);
                }
                for (int fork = 0; fork < branches; ++fork) {
                    Point start = spine[3 + fork];
                    float side = fork % 2 == 0 ? -1 : 1;
                    Point bend = new Point(start.x + side * (4.0f + LyricLightning.noise(i, fork + 7) * 4.0f), start.y + 3.0f);
                    Point end = new Point(bend.x + side * 3.0f, bend.y + 5.0f + LyricLightning.noise(i, fork) * 3.0f);
                    LyricLightning.segment(vertices, base, start, bend, thickness * 0.6f, tint);
                    LyricLightning.segment(vertices, base, bend, end, thickness * 0.35f, ColorUtil.mulAlpha(tint, 0.6f));
                }
            }
            float radius = 1.5f + (1.0f - fade) * 4.0f;
            int spark = ColorUtil.withAlpha(ColorUtil.brighten(color, 0.65f), alpha * 0.7f);
            for (int ray = 0; ray < 4; ++ray) {
                double angle = (double)ray * Math.PI / 2.0 + 0.7853981633974483;
                Point start = new Point(x + (float)Math.cos(angle) * radius * 0.5f, hitY + (float)Math.sin(angle) * radius * 0.5f);
                Point end = new Point(x + (float)Math.cos(angle) * radius, hitY + (float)Math.sin(angle) * radius);
                LyricLightning.segment(vertices, base, start, end, width * 0.5f, spark);
            }
        }
        if (vertices != null) {
            provider.endBatch(layer);
        }
    }

    private static void segment(VertexConsumer vertices, Matrix4f matrix, Point a, Point b, float width, int color) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float length = (float)Math.sqrt(dx * dx + dy * dy);
        if (length < 0.001f) {
            return;
        }
        float nx = -dy / length * width / 2.0f;
        float ny = dx / length * width / 2.0f;
        vertices.addVertex((Matrix4fc)matrix, a.x - nx, a.y - ny, -0.05f).setColor(color);
        vertices.addVertex((Matrix4fc)matrix, a.x + nx, a.y + ny, -0.05f).setColor(color);
        vertices.addVertex((Matrix4fc)matrix, b.x + nx, b.y + ny, -0.05f).setColor(color);
        vertices.addVertex((Matrix4fc)matrix, b.x - nx, b.y - ny, -0.05f).setColor(color);
    }

    private static float noise(int a, int b) {
        int h = a * 374761393 + b * 668265263;
        h = (h ^ h >>> 13) * 1274126177;
        return (float)((h ^ h >>> 16) & 0xFFFF) / 65535.0f;
    }

    public record Point(float x, float y) {
    }
}

