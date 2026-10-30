/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package astra.module.modules.pvp;

import astra.animation.AnimationUtils;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.particle.AstraParticle;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.render.WorldFx;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.NumberSetting;
import astra.utils.CombatTracker;
import astra.utils.FrameStats;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.BiFunction;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class TargetEffects
extends Module {
    private static final Identifier GLOW = Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/fx/glow_line.png");
    private static final Identifier RING = Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/fx/target_ring.png");
    private static final Identifier RETICLE = Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/fx/lock_reticle.png");
    private final BooleanSetting mobs = this.add(new BooleanSetting("\u041c\u043e\u0431\u044b \u0442\u043e\u0436\u0435", false));
    private final ColorSetting color1 = this.add(new ColorSetting("\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u0446\u0432\u0435\u0442", -45608));
    private final ColorSetting color2 = this.add(new ColorSetting("\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442", -5745153));
    private final BooleanSetting rig = this.add((BooleanSetting)new BooleanSetting("\u0420\u0438\u0433 (\u0441\u043a\u0435\u043b\u0435\u0442)", true).desc("\u0421\u0432\u0435\u0442\u044f\u0449\u0438\u0439\u0441\u044f \u0430\u043d\u0438\u043c\u0435-\u0441\u043a\u0435\u043b\u0435\u0442 \u0446\u0435\u043b\u0438"));
    private final NumberSetting rigWidth = this.add((NumberSetting)new NumberSetting("\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u0440\u0438\u0433\u0430", 1.2, 0.4, 3.0, 0.1).visibleWhen(() -> (Boolean)this.rig.get()));
    private final BooleanSetting frame = this.add(new BooleanSetting("\u0420\u0430\u043c\u043a\u0430 \u0437\u0430\u0445\u0432\u0430\u0442\u0430", true));
    private final BooleanSetting ring = this.add(new BooleanSetting("\u041a\u043e\u043b\u044c\u0446\u043e \u043f\u043e\u0434 \u0446\u0435\u043b\u044c\u044e", true));
    private final NumberSetting ringSize = this.add((NumberSetting)new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u043a\u043e\u043b\u044c\u0446\u0430", 100.0, 50.0, 200.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.ring.get()));
    private final NumberSetting ringLift = this.add((NumberSetting)new NumberSetting("\u041f\u043e\u0434\u044a\u0451\u043c \u043a\u043e\u043b\u044c\u0446\u0430", 60.0, 0.0, 100.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.ring.get()));
    private final NumberSetting ringSpeed = this.add((NumberSetting)new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043a\u043e\u043b\u044c\u0446\u0430", 1.0, 0.2, 3.0, 0.1).visibleWhen(() -> (Boolean)this.ring.get()));
    private final BooleanSetting petals = this.add(new BooleanSetting("\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0441\u0430\u043a\u0443\u0440\u044b", true));
    private final NumberSetting petalAmount = this.add((NumberSetting)new NumberSetting("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u043e\u0432", 3.0, 1.0, 10.0, 1.0).visibleWhen(() -> (Boolean)this.petals.get()));
    private final BooleanSetting lock = this.add(new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0437\u0430\u0445\u0432\u0430\u0442\u0430", true));
    private final BooleanSetting lockText = this.add((BooleanSetting)new BooleanSetting("\u041d\u0430\u0434\u043f\u0438\u0441\u044c \u00ab\u0426\u0435\u043b\u044c \u0437\u0430\u0445\u0432\u0430\u0447\u0435\u043d\u0430\u00bb", true).visibleWhen(() -> (Boolean)this.lock.get()));
    private final BooleanSetting hideVanilla = this.add((BooleanSetting)new BooleanSetting("\u0423\u0431\u0440\u0430\u0442\u044c \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0443\u0434\u0430\u0440\u0430", true).desc("\u041a\u0440\u0438\u0442\u044b, \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u044f, \u0432\u0437\u043c\u0430\u0445 \u0438 \u0441\u0435\u0440\u0434\u0435\u0447\u043a\u0438 \u0443\u0440\u043e\u043d\u0430"));
    private LivingEntity target;
    private long lockedAt;
    private float fight;
    private float ringPhase;
    private long lastHitSeen;
    private final Vector3f[] lastJoints = new Vector3f[12];
    private float surface;

    public TargetEffects() {
        super("target_effects", "\u042d\u0444\u0444\u0435\u043a\u0442\u044b \u0446\u0435\u043b\u0438", "\u0420\u0438\u0433, \u0440\u0430\u043c\u043a\u0430, \u043a\u043e\u043b\u044c\u0446\u043e, \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0438 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0437\u0430\u0445\u0432\u0430\u0442\u0430 \u0446\u0435\u043b\u0438.", Category.PVP, Icon.TARGET);
        this.setDefaultEnabled(true);
    }

    public static TargetEffects get() {
        TargetEffects m = ModuleManager.get(TargetEffects.class);
        return m != null && m.isEnabled() ? m : null;
    }

    public static boolean hidesVanillaHitParticles() {
        TargetEffects m = TargetEffects.get();
        return m != null && (Boolean)m.hideVanilla.get() != false;
    }

    private boolean visible(LivingEntity e) {
        return e != null && e.isAlive() && TargetEffects.mc.player != null && TargetEffects.mc.player.hasLineOfSight((Entity)e);
    }

    @Override
    public void onTick() {
        if (TargetEffects.mc.player == null || TargetEffects.mc.level == null) {
            this.target = null;
            return;
        }
        LivingEntity t = CombatTracker.currentTarget((Boolean)this.mobs.get());
        if (t != this.target && t != null) {
            this.lockedAt = System.currentTimeMillis();
        }
        this.target = this.visible(t) ? t : null;
        boolean fighting = this.target != null && System.currentTimeMillis() - CombatTracker.lastAttack() < 3000L;
        this.fight += ((fighting ? 1.0f : 0.0f) - this.fight) * 0.12f;
        if (this.target == null || mc.isPaused()) {
            return;
        }
        if (((Boolean)this.petals.get()).booleanValue() && fighting) {
            int n = this.petalAmount.getInt();
            for (int i = 0; i < n; ++i) {
                int c;
                double vz;
                double vx;
                if (AstraParticles.random().nextFloat() > 0.5f) continue;
                double a = AstraParticles.random().nextDouble() * Math.PI * 2.0;
                double r = (double)this.target.getBbWidth() * 0.9 + 0.3;
                double x = this.target.getX() + Math.cos(a) * r;
                double z = this.target.getZ() + Math.sin(a) * r;
                double y = this.target.getY() + AstraParticles.random().nextDouble() * (double)this.target.getBbHeight();
                AstraParticle p = AstraParticles.spawn(ParticleSprite.PETAL, x, y, z, vx = -Math.sin(a) * 0.06, 0.01, vz = Math.cos(a) * 0.06, 0.09f, c = ColorUtil.lerp(-18466, this.color1.getColor(), AstraParticles.random().nextFloat() * 0.5f), 40);
                if (p == null) continue;
                p.gravity(8.0E-4f).spin(8.0f).drag(0.94f);
            }
        }
        if (CombatTracker.lastComboHit() != this.lastHitSeen) {
            this.lastHitSeen = CombatTracker.lastComboHit();
            AABB box = this.target.getBoundingBox();
            AstraParticles.burst(ParticleSprite.SPARKLE, (box.minX + box.maxX) / 2.0, box.minY + box.getYsize() * 0.6, (box.minZ + box.maxZ) / 2.0, 5, 0.18f, 0.09f, -1, 10, 0.0f);
        }
    }

    public void renderWorld(WorldRenderContext ctx) {
        VertexConsumer vc;
        if (this.target == null || TargetEffects.mc.player == null) {
            return;
        }
        if (!this.visible(this.target)) {
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cam = camera.position();
        Quaternionf camRot = camera.rotation();
        float td = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        MultiBufferSource.BufferSource imm = mc.renderBuffers().bufferSource();
        float time = AnimationUtils.time();
        float hitPulse = 1.0f - Math.min(1.0f, (float)(System.currentTimeMillis() - CombatTracker.lastComboHit()) / 250.0f);
        int c1 = this.color1.getColor();
        int c2 = this.color2.getColor();
        Vec3 pos = this.target.getPosition(td);
        Vector3f base = new Vector3f((float)(pos.x - cam.x), (float)(pos.y - cam.y), (float)(pos.z - cam.z));
        if (((Boolean)this.ring.get()).booleanValue()) {
            RenderType layer = RenderTypes.entityTranslucentEmissive((Identifier)RING);
            vc = imm.getBuffer(layer);
            this.ringPhase += FrameStats.delta() * this.ringSpeed.getFloat() * (0.6f + this.fight);
            float lift = (0.5f - 0.5f * (float)Math.cos(this.ringPhase * 2.2f)) * this.target.getBbHeight() * this.ringLift.getFloat() / 100.0f * this.fight;
            float r = (this.target.getBbWidth() * 1.25f + 0.35f) * this.ringSize.getFloat() / 100.0f * (1.0f + hitPulse * 0.12f);
            float a = 0.55f + 0.45f * this.fight;
            WorldFx.flat(vc, new Vector3f((Vector3fc)base).add(0.0f, 0.03f + lift, 0.0f), r, this.ringPhase * 1.4f, WorldFx.alpha(c1, a));
            WorldFx.flat(vc, new Vector3f((Vector3fc)base).add(0.0f, 0.035f + lift * 0.6f, 0.0f), r * 0.62f, -this.ringPhase * 2.1f, WorldFx.alpha(c2, a * 0.8f));
            imm.endBatch(layer);
        }
        this.surface = this.target.getBbWidth() * 0.55f + 0.12f;
        RenderType glow = RenderTypes.entityTranslucentEmissive((Identifier)GLOW);
        vc = imm.getBuffer(glow);
        if (((Boolean)this.rig.get()).booleanValue()) {
            this.drawRig(vc, base, td, camRot, c1, c2, time, hitPulse);
        }
        if (((Boolean)this.frame.get()).booleanValue()) {
            this.drawFrame(vc, base, camRot, c1, c2, time, hitPulse);
        }
        imm.endBatch(glow);
        if (((Boolean)this.lock.get()).booleanValue()) {
            RenderType rl = RenderTypes.entityTranslucentEmissive((Identifier)RETICLE);
            VertexConsumer rv = imm.getBuffer(rl);
            float t = Math.min(1.0f, (float)(System.currentTimeMillis() - this.lockedAt) / 550.0f);
            float e = AnimationUtils.easeOutBack(t);
            float size = this.target.getBbHeight() * (0.32f + 0.55f * (1.0f - e)) * (1.0f + hitPulse * 0.15f);
            float roll = (1.0f - e) * (float)Math.PI + time * 0.6f;
            Vector3f chest = TargetEffects.toCamera(new Vector3f((Vector3fc)base).add(0.0f, this.target.getBbHeight() * 0.62f, 0.0f), this.surface + 0.05f);
            WorldFx.billboard(rv, chest, size, roll, camRot, 0.0f, 0.0f, 1.0f, 1.0f, WorldFx.alpha(c1, 0.35f + 0.65f * t));
            imm.endBatch(rl);
        }
        if (((Boolean)this.rig.get()).booleanValue()) {
            RenderType jl = RenderTypes.entityTranslucentEmissive((Identifier)AstraParticles.SHEET);
            VertexConsumer jv = imm.getBuffer(jl);
            ParticleSprite orb = ParticleSprite.ORB;
            for (Vector3f j : this.lastJoints) {
                if (j == null) continue;
                WorldFx.billboard(jv, j, 0.07f * this.rigWidth.getFloat() * (1.0f + hitPulse * 0.5f), 0.0f, camRot, orb.u0, orb.v0, orb.u1, orb.v1, WorldFx.alpha(-1, 0.9f));
            }
            imm.endBatch(jl);
        }
    }

    private static Vector3f toCamera(Vector3f p, float amount) {
        float len = p.length();
        if (len < 0.001f) {
            return p;
        }
        float k = Math.min(amount, len * 0.5f) / len;
        return p.sub(p.x * k, p.y * k, p.z * k);
    }

    private void drawRig(VertexConsumer vc, Vector3f base, float td, Quaternionf camRot, int c1, int c2, float time, float hit) {
        float[][][] bones;
        LivingEntity e = this.target;
        float s = e.getBbHeight() / 1.8f;
        float yaw = (float)Math.toRadians(Mth.rotLerp((float)td, (float)e.yBodyRotO, (float)e.yBodyRot));
        Vector3f fwd = new Vector3f((float)(-Math.sin(yaw)), 0.0f, (float)Math.cos(yaw));
        Vector3f right = new Vector3f((float)(-Math.cos(yaw)), 0.0f, (float)(-Math.sin(yaw)));
        float limbPos = e.walkAnimation.position(td);
        float limbAmp = Math.min(1.0f, e.walkAnimation.speed(td));
        float leg = (float)Math.cos(limbPos * 0.6662f) * 1.2f * limbAmp;
        float arm = (float)Math.cos((double)(limbPos * 0.6662f) + Math.PI) * 0.9f * limbAmp;
        float swing = (float)Math.sin(Math.sqrt(e.getAttackAnim(td)) * Math.PI) * 1.4f;
        boolean sneak = e.isCrouching();
        float lean = sneak ? 0.22f : 0.0f;
        BiFunction<float[], Float, Vector3f> at = (p, unused) -> TargetEffects.toCamera(new Vector3f((Vector3fc)base).add((Vector3fc)new Vector3f((Vector3fc)right).mul(p[0] * s)).add(0.0f, p[1] * s, 0.0f).add((Vector3fc)new Vector3f((Vector3fc)fwd).mul(p[2] * s)), this.surface);
        float[] pelvis = new float[]{0.0f, 0.78f, 0.0f};
        float[] neck = new float[]{0.0f, 1.42f, lean};
        float[] head = new float[]{0.0f, 1.66f, lean};
        float[] shR = new float[]{0.33f, 1.38f, lean};
        float[] shL = new float[]{-0.33f, 1.38f, lean};
        float[] hipR = new float[]{0.12f, 0.78f, 0.0f};
        float[] hipL = new float[]{-0.12f, 0.78f, 0.0f};
        float[] handR = TargetEffects.limb(shR, -0.66f, arm + swing);
        float[] handL = TargetEffects.limb(shL, -0.66f, -arm);
        float[] elR = TargetEffects.mid(shR, handR, 0.06f);
        float[] elL = TargetEffects.mid(shL, handL, 0.06f);
        float[] footR = TargetEffects.limb(hipR, -0.76f, leg);
        float[] footL = TargetEffects.limb(hipL, -0.76f, -leg);
        float[] kneeR = TargetEffects.mid(hipR, footR, -0.05f);
        float[] kneeL = TargetEffects.mid(hipL, footL, -0.05f);
        float w = 0.05f * this.rigWidth.getFloat() * (1.0f + hit * 0.4f);
        float pulse = 0.75f + 0.25f * (float)Math.sin(time * 4.0f);
        int a = WorldFx.alpha(c1, pulse);
        int b = WorldFx.alpha(c2, pulse);
        for (float[][] bone : bones = new float[][][]{new float[][]{neck, pelvis}, new float[][]{shL, shR}, new float[][]{hipL, hipR}, new float[][]{shR, elR}, new float[][]{elR, handR}, new float[][]{shL, elL}, new float[][]{elL, handL}, new float[][]{hipR, kneeR}, new float[][]{kneeR, footR}, new float[][]{hipL, kneeL}, new float[][]{kneeL, footL}, new float[][]{neck, head}}) {
            WorldFx.beam(vc, at.apply(bone[0], Float.valueOf(0.0f)), at.apply(bone[1], Float.valueOf(0.0f)), w, a, b);
        }
        Vector3f hc = at.apply(head, Float.valueOf(0.0f));
        Vector3f camR = new Vector3f(1.0f, 0.0f, 0.0f).rotate((Quaternionfc)camRot);
        Vector3f camU = new Vector3f(0.0f, 1.0f, 0.0f).rotate((Quaternionfc)camRot);
        float hr = 0.21f * s;
        int segs = 18;
        for (int i = 0; i < segs; ++i) {
            double t0 = Math.PI * 2 * (double)i / (double)segs;
            double t1 = Math.PI * 2 * (double)(i + 1) / (double)segs;
            Vector3f p0 = new Vector3f((Vector3fc)hc).add((Vector3fc)new Vector3f((Vector3fc)camR).mul((float)Math.cos(t0) * hr)).add((Vector3fc)new Vector3f((Vector3fc)camU).mul((float)Math.sin(t0) * hr));
            Vector3f p1 = new Vector3f((Vector3fc)hc).add((Vector3fc)new Vector3f((Vector3fc)camR).mul((float)Math.cos(t1) * hr)).add((Vector3fc)new Vector3f((Vector3fc)camU).mul((float)Math.sin(t1) * hr));
            WorldFx.beam(vc, p0, p1, w * 0.9f, a, a);
        }
        float[][] joints = new float[][]{neck, pelvis, shR, shL, hipR, hipL, elR, elL, handR, handL, kneeR, kneeL};
        for (int i = 0; i < joints.length; ++i) {
            this.lastJoints[i] = at.apply(joints[i], Float.valueOf(0.0f));
        }
    }

    private static float[] limb(float[] from, float len, float ang) {
        return new float[]{from[0], from[1] + len * (float)Math.cos(ang), from[2] - len * (float)Math.sin(ang)};
    }

    private static float[] mid(float[] a, float[] b, float bend) {
        return new float[]{(a[0] + b[0]) / 2.0f, (a[1] + b[1]) / 2.0f, (a[2] + b[2]) / 2.0f + bend};
    }

    private void drawFrame(VertexConsumer vc, Vector3f base, Quaternionf camRot, int c1, int c2, float time, float hit) {
        int sx;
        Vector3f camR = new Vector3f(1.0f, 0.0f, 0.0f).rotate((Quaternionfc)camRot);
        Vector3f camU = new Vector3f(0.0f, 1.0f, 0.0f).rotate((Quaternionfc)camRot);
        float h = this.target.getBbHeight();
        Vector3f center = TargetEffects.toCamera(new Vector3f((Vector3fc)base).add(0.0f, h / 2.0f, 0.0f), this.surface);
        float breathe = 1.0f + 0.03f * (float)Math.sin(time * 3.0f);
        float hw = (this.target.getBbWidth() * 0.9f + 0.25f) * breathe * (1.0f + hit * 0.18f);
        float hh = (h * 0.58f + 0.1f) * breathe * (1.0f + hit * 0.18f);
        float len = Math.min(hw, hh) * 0.45f;
        float w = 0.045f;
        for (sx = -1; sx <= 1; sx += 2) {
            for (int sy = -1; sy <= 1; sy += 2) {
                Vector3f corner = new Vector3f((Vector3fc)center).add((Vector3fc)new Vector3f((Vector3fc)camR).mul((float)sx * hw)).add((Vector3fc)new Vector3f((Vector3fc)camU).mul((float)sy * hh));
                Vector3f hx = new Vector3f((Vector3fc)corner).sub((Vector3fc)new Vector3f((Vector3fc)camR).mul((float)sx * len));
                Vector3f vy = new Vector3f((Vector3fc)corner).sub((Vector3fc)new Vector3f((Vector3fc)camU).mul((float)sy * len));
                WorldFx.beam(vc, corner, hx, w, c1, WorldFx.alpha(c2, 0.3f));
                WorldFx.beam(vc, corner, vy, w, c1, WorldFx.alpha(c2, 0.3f));
            }
        }
        for (sx = -1; sx <= 1; sx += 2) {
            Vector3f top = new Vector3f((Vector3fc)center).add((Vector3fc)new Vector3f((Vector3fc)camR).mul((float)sx * hw * 1.12f)).add((Vector3fc)new Vector3f((Vector3fc)camU).mul(hh * 0.5f));
            Vector3f bottom = new Vector3f((Vector3fc)center).add((Vector3fc)new Vector3f((Vector3fc)camR).mul((float)sx * hw * 1.12f)).sub((Vector3fc)new Vector3f((Vector3fc)camU).mul(hh * 0.5f));
            WorldFx.beam(vc, top, bottom, w * 0.5f, WorldFx.alpha(c2, 0.15f), WorldFx.alpha(c2, 0.7f));
        }
    }

    public void renderHud(GuiGraphics ctx) {
        if (!((Boolean)this.lock.get()).booleanValue() || !((Boolean)this.lockText.get()).booleanValue() || this.target == null) {
            return;
        }
        long age = System.currentTimeMillis() - this.lockedAt;
        if (age > 1800L) {
            return;
        }
        float a = Math.min(1.0f, (float)age / 200.0f) * Math.min(1.0f, (float)(1800L - age) / 400.0f);
        float slide = (1.0f - AnimationUtils.easeOutCubic(Math.min(1.0f, (float)age / 350.0f))) * 12.0f;
        float cx = (float)ctx.guiWidth() / 2.0f + 26.0f + slide;
        float cy = (float)ctx.guiHeight() / 2.0f - 8.0f;
        String text = "\u0426\u0435\u043b\u044c \u0437\u0430\u0445\u0432\u0430\u0447\u0435\u043d\u0430";
        float tw = Fonts.width(text, 8.0f, true) + 26.0f;
        float prev = Render2D.alpha;
        Render2D.alpha *= a;
        int c1 = this.color1.getColor();
        Render2D.roundRect(ctx, cx, cy, tw, 16.0f, 8.0f, Paint.horizontal(cx, cx + tw, ColorUtil.withAlpha(-16054504, 0.75f), ColorUtil.withAlpha(ColorUtil.lerp(-16054504, c1, 0.3f), 0.75f)));
        Render2D.outline(ctx, cx, cy, tw, 16.0f, 8.0f, 0.8f, ColorUtil.withAlpha(c1, 0.9f));
        Render2D.sparkle(ctx, cx + 9.0f, cy + 8.0f, 4.0f, (float)age * 0.3f, c1);
        Fonts.draw(ctx, text, cx + 18.0f, cy + 4.0f, 8.0f, -1, true);
        Render2D.alpha = prev;
    }
}

