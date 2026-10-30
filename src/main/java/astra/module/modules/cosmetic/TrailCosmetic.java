/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.particle.AstraParticle;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.Icon;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import java.util.Random;

public class TrailCosmetic
extends PlayerCosmetic {
    private final ModeSetting type = this.add(new ModeSetting("\u042d\u0444\u0444\u0435\u043a\u0442", "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438", "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438", "\u0417\u0432\u0451\u0437\u0434\u044b", "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", "\u041d\u043e\u0442\u044b", "\u041e\u0433\u043e\u043d\u044c\u043a\u0438"));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442", -28720));
    private final NumberSetting density = this.add(new NumberSetting("\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", 2.0, 1.0, 6.0, 1.0));

    public TrailCosmetic() {
        super("trail", "\u0421\u043b\u0435\u0434 \u0447\u0430\u0441\u0442\u0438\u0446", "\u0427\u0430\u0441\u0442\u0438\u0446\u044b \u0437\u0430 \u0432\u0430\u043c\u0438 \u043f\u0440\u0438 \u0445\u043e\u0434\u044c\u0431\u0435.", Category.COSMETICS, Icon.SPARKLE);
    }

    @Override
    public void onTick() {
        if (this.player() == null || TrailCosmetic.mc.field_1687 == null || mc.method_1493()) {
            return;
        }
        if (this.player().method_18798().method_37268() < 0.002) {
            return;
        }
        Random r = AstraParticles.random();
        int n = this.density.getInt();
        block11: for (int i = 0; i < n; ++i) {
            double x = this.player().method_23317() + (r.nextDouble() - 0.5) * 0.4;
            double z = this.player().method_23321() + (r.nextDouble() - 0.5) * 0.4;
            double y = this.player().method_23318() + 0.08 + r.nextDouble() * 0.2;
            int c = this.color.getColor();
            switch ((String)this.type.get()) {
                case "\u0417\u0432\u0451\u0437\u0434\u044b": {
                    AstraParticles.sparkle(x, y, z, c, 0.08f, 30);
                    continue block11;
                }
                case "\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438": 
                case "\u041d\u043e\u0442\u044b": {
                    ParticleSprite s;
                    AstraParticle p;
                    if (r.nextInt(3) != 0 || (p = AstraParticles.spawn(s = this.type.is("\u041d\u043e\u0442\u044b") ? ParticleSprite.NOTE : ParticleSprite.HEART, x, y + 0.2, z, 0.0, 0.02, 0.0, 0.1f, c, 30)) == null) continue block11;
                    p.drag(0.95f).sway(0.002f);
                    continue block11;
                }
                case "\u041e\u0433\u043e\u043d\u044c\u043a\u0438": {
                    AstraParticles.orb(x, y, z, 0.0, 0.01, 0.0, c, 0.07f, 26);
                    continue block11;
                }
                default: {
                    AstraParticle p = AstraParticles.spawn(ParticleSprite.PETAL, x, y, z, 0.0, 0.008, 0.0, 0.08f, c, 40);
                    if (p == null) continue block11;
                    p.gravity(8.0E-4f).spin(5.0f).sway(0.001f).drag(0.96f);
                }
            }
        }
    }
}

