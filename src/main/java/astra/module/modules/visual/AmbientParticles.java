/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.Vec3
 */
package astra.module.modules.visual;

import astra.module.Category;
import astra.module.Module;
import astra.particle.AstraParticle;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.NumberSetting;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class AmbientParticles
extends Module {
    private static final int[] SAKURA = new int[]{-18466, -25902, -13848, -33080};
    private final NumberSetting intensity = this.add(new NumberSetting("\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c", 60.0, 5.0, 200.0, 5.0).unit("%"));
    private final BooleanSetting sakura = this.add(new BooleanSetting("\u0421\u0430\u043a\u0443\u0440\u0430 (\u043b\u0435\u043f\u0435\u0441\u0442\u043a\u0438)", true));
    private final BooleanSetting customPetals = this.add((BooleanSetting)new BooleanSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u043e\u0432", false).visibleWhen(() -> (Boolean)this.sakura.get()));
    private final ColorSetting petalColor = this.add((ColorSetting)new ColorSetting("\u0426\u0432\u0435\u0442 \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u043e\u0432", -28720).visibleWhen(() -> (Boolean)this.sakura.get() != false && (Boolean)this.customPetals.get() != false));
    private final BooleanSetting stars = this.add(new BooleanSetting("\u0421\u0432\u0435\u0442\u044f\u0449\u0438\u0435\u0441\u044f \u0437\u0432\u0451\u0437\u0434\u043e\u0447\u043a\u0438", true));
    private final BooleanSetting hearts = this.add(new BooleanSetting("\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", false));
    private final BooleanSetting sparks = this.add(new BooleanSetting("\u0418\u0441\u043a\u0440\u044b \u0438 \u0441\u0432\u0435\u0442\u043e\u0432\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b", true));
    private final ColorSetting sparkColor = this.add((ColorSetting)new ColorSetting("\u0426\u0432\u0435\u0442 \u0438\u0441\u043a\u0440", -1061124868).withAlpha().visibleWhen(() -> (Boolean)this.sparks.get()));
    private final NumberSetting radius = this.add(new NumberSetting("\u0420\u0430\u0434\u0438\u0443\u0441", 12.0, 4.0, 32.0, 1.0).unit(" \u0431\u043b."));
    private final NumberSetting speed = this.add(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 100.0, 20.0, 300.0, 10.0).unit("%"));
    private float accumulator;

    public AmbientParticles() {
        super("ambient_particles", "\u0427\u0430\u0441\u0442\u0438\u0446\u044b", "\u0410\u043d\u0438\u043c\u0435-\u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0438 \u0430\u0442\u043c\u043e\u0441\u0444\u0435\u0440\u043d\u044b\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b.", Category.VISUAL, Icon.STAR);
    }

    @Override
    public void onTick() {
        if (AmbientParticles.mc.field_1687 == null || AmbientParticles.mc.field_1724 == null || mc.method_1493()) {
            return;
        }
        ArrayList<Runnable> kinds = new ArrayList<Runnable>();
        Random r = AstraParticles.random();
        float rad = this.radius.getFloat();
        float sp = this.speed.getFloat() / 100.0f;
        Vec3 c = AmbientParticles.mc.field_1724.method_73189();
        if (((Boolean)this.sakura.get()).booleanValue()) {
            kinds.add(() -> {
                double[] p = this.point(c, rad, 5.0 + r.nextDouble() * 7.0);
                if (p != null) {
                    int col = (Boolean)this.customPetals.get() != false ? this.petalColor.getColor() : SAKURA[r.nextInt(SAKURA.length)];
                    AstraParticles.petal(p[0], p[1], p[2], col, sp);
                }
            });
        }
        if (((Boolean)this.stars.get()).booleanValue()) {
            kinds.add(() -> {
                double[] p = this.point(c, rad, 1.0 + r.nextDouble() * 6.0);
                if (p != null) {
                    AstraParticles.sparkle(p[0], p[1], p[2], ColorUtil.lerp(-1, -15632, r.nextFloat()), 0.06f + r.nextFloat() * 0.07f, 60 + r.nextInt(60));
                }
            });
        }
        if (((Boolean)this.hearts.get()).booleanValue() && r.nextFloat() < 0.3f) {
            kinds.add(() -> {
                double[] p = this.point(c, rad * 0.6f, 1.0 + r.nextDouble() * 2.0);
                if (p == null) {
                    return;
                }
                AstraParticle h = AstraParticles.spawn(ParticleSprite.HEART, p[0], p[1], p[2], 0.0, 0.012 * (double)sp, 0.0, 0.1f + r.nextFloat() * 0.06f, -41048, 70 + r.nextInt(30));
                if (h != null) {
                    h.sway(0.0012f).drag(0.99f);
                }
            });
        }
        if (((Boolean)this.sparks.get()).booleanValue()) {
            kinds.add(() -> {
                double[] p = this.point(c, rad, 0.3 + r.nextDouble() * 4.0);
                if (p != null) {
                    AstraParticles.orb(p[0], p[1], p[2], 0.0, 0.006 * (double)sp, 0.0, this.sparkColor.getColor(), 0.05f + r.nextFloat() * 0.06f, 50 + r.nextInt(50));
                }
            });
        }
        if (kinds.isEmpty()) {
            return;
        }
        this.accumulator += 3.0f * this.intensity.getFloat() / 100.0f;
        int count = (int)this.accumulator;
        this.accumulator -= (float)count;
        for (int i = 0; i < count; ++i) {
            ((Runnable)kinds.get(r.nextInt(kinds.size()))).run();
        }
    }

    private double[] point(Vec3 c, float rad, double dy) {
        double[] dArray;
        double z;
        double y;
        Random r = AstraParticles.random();
        double x = c.field_1352 + (r.nextDouble() * 2.0 - 1.0) * (double)rad;
        if (AmbientParticles.mc.field_1687.method_22347(BlockPos.method_49637((double)x, (double)(y = c.field_1351 + dy), (double)(z = c.field_1350 + (r.nextDouble() * 2.0 - 1.0) * (double)rad)))) {
            double[] dArray2 = new double[3];
            dArray2[0] = x;
            dArray2[1] = y;
            dArray = dArray2;
            dArray2[2] = z;
        } else {
            dArray = null;
        }
        return dArray;
    }
}

