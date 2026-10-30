/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package astra.particle;

import astra.AstraClient;
import astra.module.modules.performance.PerformanceModule;
import astra.particle.AstraParticle;
import astra.particle.ParticleSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public final class AstraParticles {
    public static final Identifier SHEET = Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/particle/astra_particles.png");
    private static final int MAX = 4000;
    private static final List<AstraParticle> PARTICLES = new ArrayList<AstraParticle>();
    private static final Random RANDOM = new Random();
    private static ClientLevel world;

    private AstraParticles() {
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(AstraParticles::tick);
        WorldRenderEvents.AFTER_ENTITIES.register(AstraParticles::render);
    }

    public static Random random() {
        return RANDOM;
    }

    public static AstraParticle spawn(ParticleSprite sprite, double x, double y, double z, double vx, double vy, double vz, float size, int color, int life) {
        int cap = Math.max(0, PerformanceModule.limitParticles(4000));
        if (PARTICLES.size() >= cap) {
            return null;
        }
        AstraParticle p = new AstraParticle(sprite, x, y, z, vx, vy, vz, size, color, life);
        PARTICLES.add(p);
        return p;
    }

    public static int count() {
        return PARTICLES.size();
    }

    public static void petal(double x, double y, double z, int color, float speed) {
        AstraParticle p = AstraParticles.spawn(ParticleSprite.PETAL, x, y, z, (RANDOM.nextDouble() - 0.5) * 0.03 * (double)speed, -0.01 * (double)speed, (RANDOM.nextDouble() - 0.5) * 0.03 * (double)speed, 0.09f + RANDOM.nextFloat() * 0.06f, color, 140 + RANDOM.nextInt(80));
        if (p != null) {
            p.gravity(6.0E-4f * speed).drag(0.97f).sway(0.0025f * speed).spin(2.0f + RANDOM.nextFloat() * 4.0f);
        }
    }

    public static void sparkle(double x, double y, double z, int color, float size, int life) {
        AstraParticle p = AstraParticles.spawn(ParticleSprite.SPARKLE, x, y, z, (RANDOM.nextDouble() - 0.5) * 0.004, 0.003 + RANDOM.nextDouble() * 0.004, (RANDOM.nextDouble() - 0.5) * 0.004, size, color, life);
        if (p != null) {
            p.twinkle().drag(0.99f);
        }
    }

    public static void orb(double x, double y, double z, double vx, double vy, double vz, int color, float size, int life) {
        AstraParticle p = AstraParticles.spawn(ParticleSprite.ORB, x, y, z, vx, vy, vz, size, color, life);
        if (p != null) {
            p.drag(0.96f).shrink(0.4f);
        }
    }

    public static void burst(ParticleSprite sprite, double x, double y, double z, int count, float speed, float size, int color, int life, float gravity) {
        for (int i = 0; i < count; ++i) {
            double theta = RANDOM.nextDouble() * Math.PI * 2.0;
            double phi = Math.acos(2.0 * RANDOM.nextDouble() - 1.0);
            double s = (double)speed * (0.5 + RANDOM.nextDouble() * 0.5);
            AstraParticle p = AstraParticles.spawn(sprite, x, y, z, Math.sin(phi) * Math.cos(theta) * s, Math.abs(Math.cos(phi)) * s * 0.8 + 0.02, Math.sin(phi) * Math.sin(theta) * s, size * (0.7f + RANDOM.nextFloat() * 0.6f), color, life + RANDOM.nextInt(Math.max(1, life / 3)));
            if (p == null) continue;
            p.gravity(gravity).drag(0.9f).shrink(0.5f);
            if (sprite == ParticleSprite.PETAL || sprite == ParticleSprite.STAR) {
                p.spin(RANDOM.nextFloat() * 16.0f - 8.0f);
            }
            if (sprite != ParticleSprite.SPARKLE) continue;
            p.twinkle();
        }
    }

    private static void tick(Minecraft mc) {
        if (mc.level != world) {
            PARTICLES.clear();
            world = mc.level;
        }
        if (mc.level == null || mc.isPaused()) {
            return;
        }
        PARTICLES.removeIf(p -> {
            p.tick();
            return p.dead();
        });
    }

    private static void render(WorldRenderContext ctx) {
        if (PARTICLES.isEmpty()) {
            return;
        }
        try {
            AstraParticles.draw(ctx);
        }
        catch (RuntimeException e) {
            AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u0447\u0430\u0441\u0442\u0438\u0446 Astra", (Throwable)e);
            PARTICLES.clear();
        }
    }

    private static void draw(WorldRenderContext ctx) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cam = camera.position();
        Quaternionf rotation = camera.rotation();
        float td = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        PARTICLES.sort(Comparator.comparingDouble(p -> -AstraParticles.sqDist(p, cam)));
        MultiBufferSource.BufferSource immediate = mc.renderBuffers().bufferSource();
        RenderType layer = RenderTypes.entityTranslucentEmissive((Identifier)SHEET);
        VertexConsumer vc = immediate.getBuffer(layer);
        PoseStack.Pose entry = new PoseStack().last();
        int light = 0xF000F0;
        Vector3f v = new Vector3f();
        float[][] corners = new float[][]{{-1.0f, -1.0f}, {-1.0f, 1.0f}, {1.0f, 1.0f}, {1.0f, -1.0f}};
        for (AstraParticle p2 : PARTICLES) {
            float a = p2.alpha(td);
            if (a <= 0.01f) continue;
            float x = (float)(AstraParticles.lerp(td, p2.prevX, p2.x) - cam.x);
            float y = (float)(AstraParticles.lerp(td, p2.prevY, p2.y) - cam.y);
            float z = (float)(AstraParticles.lerp(td, p2.prevZ, p2.z) - cam.z);
            float size = p2.currentSize(td);
            float roll = (float)Math.toRadians(p2.prevRoll + (p2.roll - p2.prevRoll) * td);
            int baseAlpha = p2.color >>> 24 & 0xFF;
            int color = Math.round((float)baseAlpha * a) << 24 | p2.color & 0xFFFFFF;
            ParticleSprite s = p2.sprite;
            float[] us = new float[]{s.u0, s.u0, s.u1, s.u1};
            float[] vs = new float[]{s.v1, s.v0, s.v0, s.v1};
            for (int i = 0; i < 4; ++i) {
                v.set(corners[i][0], corners[i][1], 0.0f).rotateZ(roll).mul(size).rotate((Quaternionfc)rotation).add(x, y, z);
                vc.addVertex(entry, v.x, v.y, v.z).setColor(color).setUv(us[i], vs[i]).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, 0.0f, 1.0f, 0.0f);
            }
        }
        immediate.endBatch(layer);
    }

    private static double sqDist(AstraParticle p, Vec3 cam) {
        double dx = p.x - cam.x;
        double dy = p.y - cam.y;
        double dz = p.z - cam.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static double lerp(float t, double a, double b) {
        return a + (b - a) * (double)t;
    }
}

