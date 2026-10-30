/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.Module;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.Icon;
import astra.settings.ModeSetting;
import net.minecraft.world.entity.LivingEntity;

public class VictoryEffect
extends Module {
    private final ModeSetting type = this.add(new ModeSetting("\u042d\u0444\u0444\u0435\u043a\u0442", "\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a", "\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434", "\u0421\u0435\u0440\u0434\u0446\u0430"));

    public VictoryEffect() {
        super("victory", "\u041f\u043e\u0431\u0435\u0434\u043d\u044b\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b", "\u042d\u0444\u0444\u0435\u043a\u0442 \u043d\u0430 \u043c\u0435\u0441\u0442\u0435 \u043f\u043e\u0432\u0435\u0440\u0436\u0435\u043d\u043d\u043e\u0433\u043e \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0430.", Category.COSMETICS, Icon.TROPHY);
        this.setDefaultEnabled(true);
    }

    public void trigger(LivingEntity target) {
        if (VictoryEffect.mc.field_1687 == null || VictoryEffect.mc.field_1724 == null) {
            return;
        }
        double x = target.method_23317();
        double y = target.method_23318() + (double)target.method_17682() * 0.6;
        double z = target.method_23321();
        switch ((String)this.type.get()) {
            case "\u0421\u0430\u043a\u0443\u0440\u0430": {
                AstraParticles.burst(ParticleSprite.PETAL, x, y, z, 45, 0.25f, 0.13f, -22308, 60, 0.004f);
                break;
            }
            case "\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434": {
                AstraParticles.burst(ParticleSprite.STAR, x, y + 1.0, z, 30, 0.2f, 0.12f, -8090, 50, 0.01f);
                AstraParticles.burst(ParticleSprite.SPARKLE, x, y + 1.0, z, 20, 0.25f, 0.1f, -1, 30, 0.004f);
                break;
            }
            case "\u0421\u0435\u0440\u0434\u0446\u0430": {
                AstraParticles.burst(ParticleSprite.HEART, x, y, z, 30, 0.18f, 0.14f, -41048, 45, -0.002f);
                break;
            }
            default: {
                AstraParticles.burst(ParticleSprite.ORB, x, y + 0.5, z, 30, 0.35f, 0.12f, -45608, 30, 0.006f);
                AstraParticles.burst(ParticleSprite.ORB, x, y + 0.5, z, 30, 0.3f, 0.12f, -5745153, 30, 0.006f);
                AstraParticles.burst(ParticleSprite.SPARKLE, x, y + 0.5, z, 20, 0.4f, 0.1f, -1, 24, 0.004f);
            }
        }
    }
}

