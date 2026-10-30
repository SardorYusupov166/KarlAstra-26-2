/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.AABB
 */
package astra.module.modules.pvp;

import astra.module.Category;
import astra.module.Module;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class HitParticles
extends Module {
    private final ModeSetting type = this.add(new ModeSetting("\u0427\u0430\u0441\u0442\u0438\u0446\u044b", "\u0417\u0432\u0451\u0437\u0434\u044b", "\u0417\u0432\u0451\u0437\u0434\u044b", "\u0418\u0441\u043a\u0440\u044b", "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438", "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u0421\u0438\u044f\u043d\u0438\u0435"));
    private final NumberSetting amount = this.add(new NumberSetting("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e", 8.0, 1.0, 30.0, 1.0));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442", -45608));
    private final BooleanSetting critBoost = this.add(new BooleanSetting("\u0411\u043e\u043b\u044c\u0448\u0435 \u043f\u0440\u0438 \u043a\u0440\u0438\u0442\u0435", true));

    public HitParticles() {
        super("hit_particles", "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0443\u0434\u0430\u0440\u0430", "\u0417\u0432\u0451\u0437\u0434\u044b, \u0438\u0441\u043a\u0440\u044b \u0438\u043b\u0438 \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u043f\u0440\u0438 \u043f\u043e\u043f\u0430\u0434\u0430\u043d\u0438\u0438.", Category.PVP, Icon.SPARKLE);
    }

    public void onHit(LivingEntity target, boolean crit) {
        if (HitParticles.mc.level == null) {
            return;
        }
        int count = this.amount.getInt();
        if (crit && ((Boolean)this.critBoost.get()).booleanValue()) {
            count = Math.round((float)count * 1.8f);
        }
        AABB box = target.getBoundingBox();
        double x = (box.minX + box.maxX) / 2.0;
        double y = box.minY + box.getYsize() * 0.6;
        double z = (box.minZ + box.maxZ) / 2.0;
        int c = this.color.getColor();
        switch ((String)this.type.get()) {
            case "\u0418\u0441\u043a\u0440\u044b": {
                AstraParticles.burst(ParticleSprite.SPARKLE, x, y, z, count, 0.22f, 0.12f, c, 14, 0.01f);
                break;
            }
            case "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438": {
                AstraParticles.burst(ParticleSprite.PETAL, x, y, z, count, 0.14f, 0.12f, c, 26, 0.006f);
                break;
            }
            case "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438": {
                AstraParticles.burst(ParticleSprite.HEART, x, y, z, count, 0.1f, 0.13f, c, 22, -0.002f);
                break;
            }
            case "\u0421\u0438\u044f\u043d\u0438\u0435": {
                AstraParticles.burst(ParticleSprite.ORB, x, y, z, count, 0.12f, 0.16f, c, 18, 0.0f);
                break;
            }
            default: {
                AstraParticles.burst(ParticleSprite.STAR, x, y, z, count, 0.16f, 0.11f, c, 20, 0.008f);
                AstraParticles.burst(ParticleSprite.SPARKLE, x, y, z, Math.max(1, count / 2), 0.2f, 0.1f, -1, 12, 0.0f);
            }
        }
    }
}

