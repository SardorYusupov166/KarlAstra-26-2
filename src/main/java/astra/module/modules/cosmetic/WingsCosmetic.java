/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.module.modules.visual.ShadersModule;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;

public class WingsCosmetic
extends PlayerCosmetic {
    private final ModeSetting design = this.add(new ModeSetting("\u041c\u043e\u0434\u0435\u043b\u044c", "Astra", "Astra", "\u0410\u043d\u0433\u0435\u043b\u044c\u0441\u043a\u0438\u0435", "\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0435", "\u0422\u0451\u043c\u043d\u044b\u0435"));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442", -1));
    private final NumberSetting size = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 100.0, 50.0, 150.0, 5.0).unit("%"));
    private final NumberSetting flap = this.add(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0437\u043c\u0430\u0445\u043e\u0432", 1.0, 0.0, 3.0, 0.1));
    private final NumberSetting flapAmount = this.add(new NumberSetting("\u0421\u0438\u043b\u0430 \u0432\u0437\u043c\u0430\u0445\u043e\u0432", 14.0, 0.0, 40.0, 1.0).unit("\u00b0"));
    private final BooleanSetting glow = this.add(new BooleanSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435", true));
    private final NumberSetting opacity = this.add((NumberSetting)new NumberSetting("\u041d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 100.0, 10.0, 100.0, 5.0).unit("%").desc("100% \u2014 \u043f\u043e\u043b\u043d\u043e\u0441\u0442\u044c\u044e \u043f\u043b\u043e\u0442\u043d\u044b\u0435 \u043a\u0440\u044b\u043b\u044c\u044f"));
    private final ModeSetting density = this.add((ModeSetting)new ModeSetting("\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", "\u0410\u0432\u0442\u043e", "\u0410\u0432\u0442\u043e", "\u041f\u043b\u043e\u0442\u043d\u044b\u0435", "\u041f\u043e\u043b\u0443\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u044b\u0435").desc("\u0410\u0432\u0442\u043e: \u043f\u043b\u043e\u0442\u043d\u044b\u0435 \u043f\u0440\u0438 \u0432\u043a\u043b\u044e\u0447\u0451\u043d\u043d\u044b\u0445 \u0448\u0435\u0439\u0434\u0435\u0440\u0430\u0445 Iris"));
    private final NumberSetting height = this.add((NumberSetting)new NumberSetting("\u0412\u044b\u0441\u043e\u0442\u0430", 0.0, -10.0, 10.0, 0.5).desc("\u0412\u044b\u0448\u0435 / \u043d\u0438\u0436\u0435 \u043d\u0430 \u0441\u043f\u0438\u043d\u0435"));
    private final NumberSetting distance = this.add((NumberSetting)new NumberSetting("\u041e\u0442\u0441\u0442\u0443\u043f \u043e\u0442 \u0441\u043f\u0438\u043d\u044b", 0.0, -4.0, 8.0, 0.5).desc("\u0411\u043b\u0438\u0436\u0435 \u043a \u0441\u043f\u0438\u043d\u0435 / \u0434\u0430\u043b\u044c\u0448\u0435 \u043e\u0442 \u043d\u0435\u0451"));
    private final NumberSetting spread = this.add((NumberSetting)new NumberSetting("\u0420\u0430\u0437\u0432\u0435\u0434\u0435\u043d\u0438\u0435", 0.0, -4.0, 8.0, 0.5).desc("\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u043c\u0435\u0436\u0434\u0443 \u043e\u0441\u043d\u043e\u0432\u0430\u043d\u0438\u044f\u043c\u0438 \u043a\u0440\u044b\u043b\u044c\u0435\u0432"));
    private final NumberSetting sweep = this.add((NumberSetting)new NumberSetting("\u041e\u0442\u0432\u043e\u0434 \u043d\u0430\u0437\u0430\u0434", 40.0, 0.0, 85.0, 1.0).unit("\u00b0").desc("\u041d\u0430\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u044b\u043b\u044c\u044f \u043e\u0442\u0432\u0435\u0434\u0435\u043d\u044b \u043d\u0430\u0437\u0430\u0434"));
    private final NumberSetting lift = this.add((NumberSetting)new NumberSetting("\u041f\u043e\u0434\u044a\u0451\u043c", 12.0, -40.0, 60.0, 1.0).unit("\u00b0").desc("\u041a\u043e\u043d\u0447\u0438\u043a\u0438 \u0432\u044b\u0448\u0435 / \u043d\u0438\u0436\u0435"));
    private final NumberSetting pitch = this.add((NumberSetting)new NumberSetting("\u041d\u0430\u043a\u043b\u043e\u043d", 0.0, -45.0, 45.0, 1.0).unit("\u00b0").desc("\u041d\u0430\u043a\u043b\u043e\u043d \u043a\u0440\u044b\u043b\u044c\u0435\u0432 \u0432\u043f\u0435\u0440\u0451\u0434 / \u043d\u0430\u0437\u0430\u0434"));
    private final ButtonSetting reset = this.add(new ButtonSetting("\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", this::resetPosition));

    public WingsCosmetic() {
        super("wings", "\u041a\u0440\u044b\u043b\u044c\u044f", "\u0421\u0432\u0435\u0442\u044f\u0449\u0438\u0435\u0441\u044f \u0430\u043d\u0438\u043c\u0435-\u043a\u0440\u044b\u043b\u044c\u044f \u0441 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0435\u0439 \u0438 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u043e\u0439 \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u044f.", Category.COSMETICS, Icon.WINGS);
    }

    private void resetPosition() {
        this.height.reset();
        this.distance.reset();
        this.spread.reset();
        this.sweep.reset();
        this.lift.reset();
        this.pitch.reset();
        this.flapAmount.reset();
    }

    public String design() {
        return (String)this.design.get();
    }

    public int color() {
        int c = this.color.getColor();
        int a = Math.round((float)(c >>> 24 & 0xFF) * this.opacity.getFloat() / 100.0f);
        return a << 24 | c & 0xFFFFFF;
    }

    public float scale() {
        return this.size.getFloat() / 100.0f;
    }

    public float flapSpeed() {
        return this.flap.getFloat();
    }

    public float flapAmount() {
        return this.flapAmount.getFloat();
    }

    public boolean solid() {
        if ((Double)this.opacity.get() < 100.0) {
            return false;
        }
        return switch ((String)this.density.get()) {
            case "\u041f\u043b\u043e\u0442\u043d\u044b\u0435" -> true;
            case "\u041f\u043e\u043b\u0443\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u044b\u0435" -> false;
            default -> ShadersModule.shadersInUse();
        };
    }

    public boolean glow() {
        return (Boolean)this.glow.get();
    }

    public float heightOffset() {
        return this.height.getFloat() / 16.0f;
    }

    public float distanceOffset() {
        return this.distance.getFloat() / 16.0f;
    }

    public float spreadOffset() {
        return this.spread.getFloat() / 16.0f;
    }

    public float sweepDegrees() {
        return this.sweep.getFloat();
    }

    public float liftDegrees() {
        return this.lift.getFloat();
    }

    public float pitchDegrees() {
        return this.pitch.getFloat();
    }
}

