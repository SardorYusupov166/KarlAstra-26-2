/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.particle.AstraParticles;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.NumberSetting;

public class AuraCosmetic
extends PlayerCosmetic {
    private final BooleanSetting circle = this.add(new BooleanSetting("\u041c\u0430\u0433\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043a\u0440\u0443\u0433", true));
    private final BooleanSetting particles = this.add(new BooleanSetting("\u0427\u0430\u0441\u0442\u0438\u0446\u044b \u0432\u043e\u043a\u0440\u0443\u0433", true));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442", -4160260));
    private final NumberSetting size = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 100.0, 50.0, 200.0, 10.0).unit("%"));
    private float angle;

    public AuraCosmetic() {
        super("aura", "\u0410\u0443\u0440\u044b", "\u041c\u0430\u0433\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043a\u0440\u0443\u0433 \u0438 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0432\u043e\u043a\u0440\u0443\u0433 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430.", Category.COSMETICS, Icon.SPARKLE);
    }

    public boolean drawCircle() {
        return (Boolean)this.circle.get();
    }

    public int color() {
        return this.color.getColor();
    }

    public float size() {
        return this.size.getFloat() / 100.0f;
    }

    @Override
    public void onTick() {
        if (!((Boolean)this.particles.get()).booleanValue() || this.player() == null || AuraCosmetic.mc.level == null || mc.isPaused()) {
            return;
        }
        this.angle += 0.3f;
        float r = 0.75f * this.size();
        for (int i = 0; i < 2; ++i) {
            double a = (double)this.angle + (double)i * Math.PI;
            double x = this.player().getX() + Math.cos(a) * (double)r;
            double z = this.player().getZ() + Math.sin(a) * (double)r;
            double y = this.player().getY() + 0.15 + (Math.sin((double)this.angle * 0.5 + (double)i) + 1.0) * 0.55;
            AstraParticles.orb(x, y, z, 0.0, 0.01, 0.0, this.color.getColor(), 0.07f, 22);
        }
        if (AstraParticles.random().nextInt(3) == 0) {
            double a = AstraParticles.random().nextDouble() * Math.PI * 2.0;
            AstraParticles.sparkle(this.player().getX() + Math.cos(a) * (double)r * 1.2, this.player().getY() + 0.1, this.player().getZ() + Math.sin(a) * (double)r * 1.2, -1, 0.06f, 30);
        }
    }
}

