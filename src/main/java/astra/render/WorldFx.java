/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package astra.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class WorldFx {
    private static final PoseStack.Pose IDENTITY = new PoseStack().last();
    private static final int LIGHT = 0xF000F0;

    private WorldFx() {
    }

    public static void vertex(VertexConsumer vc, float x, float y, float z, float u, float v, int color) {
        vc.addVertex(IDENTITY, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(IDENTITY, 0.0f, 1.0f, 0.0f);
    }

    public static void beam(VertexConsumer vc, Vector3f a, Vector3f b, float width, int colorA, int colorB) {
        Vector3f dir = new Vector3f((Vector3fc)b).sub((Vector3fc)a);
        if (dir.lengthSquared() < 1.0E-8f) {
            return;
        }
        Vector3f mid = new Vector3f((Vector3fc)a).add((Vector3fc)b).mul(0.5f);
        Vector3f side = new Vector3f((Vector3fc)dir).cross((Vector3fc)mid);
        if (side.lengthSquared() < 1.0E-8f) {
            return;
        }
        side.normalize().mul(width / 2.0f);
        WorldFx.vertex(vc, a.x - side.x, a.y - side.y, a.z - side.z, 0.0f, 0.0f, colorA);
        WorldFx.vertex(vc, a.x + side.x, a.y + side.y, a.z + side.z, 1.0f, 0.0f, colorA);
        WorldFx.vertex(vc, b.x + side.x, b.y + side.y, b.z + side.z, 1.0f, 1.0f, colorB);
        WorldFx.vertex(vc, b.x - side.x, b.y - side.y, b.z - side.z, 0.0f, 1.0f, colorB);
    }

    public static void billboard(VertexConsumer vc, Vector3f c, float size, float roll, Quaternionf camRot, float u0, float v0, float u1, float v1, int color) {
        float[][] corners = new float[][]{{-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}};
        float[] us = new float[]{u0, u0, u1, u1};
        float[] vs = new float[]{v1, v0, v0, v1};
        Vector3f p = new Vector3f();
        for (int i = 0; i < 4; ++i) {
            p.set(corners[i][0], corners[i][1], 0.0f).rotateZ(roll).mul(size).rotate((Quaternionfc)camRot).add((Vector3fc)c);
            WorldFx.vertex(vc, p.x, p.y, p.z, us[i], vs[i], color);
        }
    }

    public static void flat(VertexConsumer vc, Vector3f c, float radius, float angle, int color) {
        int i;
        float cos = (float)Math.cos(angle) * radius;
        float sin = (float)Math.sin(angle) * radius;
        float[][] p = new float[][]{{-cos + sin, -sin - cos}, {-cos - sin, -sin + cos}, {cos - sin, sin + cos}, {cos + sin, sin - cos}};
        float[][] uv = new float[][]{{0.0f, 0.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, 0.0f}};
        for (i = 0; i < 4; ++i) {
            WorldFx.vertex(vc, c.x + p[i][0], c.y, c.z + p[i][1], uv[i][0], uv[i][1], color);
        }
        for (i = 3; i >= 0; --i) {
            WorldFx.vertex(vc, c.x + p[i][0], c.y, c.z + p[i][1], uv[i][0], uv[i][1], color);
        }
    }

    public static int alpha(int color, float a) {
        int base = color >>> 24 & 0xFF;
        return Math.round((float)base * Math.max(0.0f, Math.min(1.0f, a))) << 24 | color & 0xFFFFFF;
    }
}

