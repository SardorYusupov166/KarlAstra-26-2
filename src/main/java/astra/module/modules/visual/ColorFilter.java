/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.Identifier
 */
package astra.module.modules.visual;

import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import net.minecraft.resources.Identifier;

public class ColorFilter
extends Module {
    private final ModeSetting filter = this.add(new ModeSetting("\u0424\u0438\u043b\u044c\u0442\u0440", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0411\u0435\u0437 \u0444\u0438\u043b\u044c\u0442\u0440\u0430", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u041d\u043e\u0447\u044c", "\u0422\u0451\u043f\u043b\u044b\u0439", "\u0425\u043e\u043b\u043e\u0434\u043d\u044b\u0439", "\u041c\u0435\u0447\u0442\u0430"));
    private final NumberSetting saturation = this.add(new NumberSetting("\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c", 125.0, 0.0, 200.0, 25.0).unit("%"));
    private final NumberSetting brightness = this.add((NumberSetting)new NumberSetting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c", 50.0, 0.0, 100.0, 5.0).unit("%").bind(() -> (Double)ColorFilter.mc.field_1690.method_42473().method_41753() * 100.0, v -> ColorFilter.mc.field_1690.method_42473().method_41748((Object)(v / 100.0))));

    public ColorFilter() {
        super("color_filter", "\u0426\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u0444\u0438\u043b\u044c\u0442\u0440", "\u041d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u044c, \u044f\u0440\u043a\u043e\u0441\u0442\u044c \u0438 \u0446\u0432\u0435\u0442\u043e\u0432\u044b\u0435 \u0444\u0438\u043b\u044c\u0442\u0440\u044b \u043c\u0438\u0440\u0430.", Category.VISUAL, Icon.DROPLET);
    }

    private Identifier effectId() {
        String key = switch ((String)this.filter.get()) {
            case "\u0421\u0430\u043a\u0443\u0440\u0430" -> "sakura";
            case "\u041d\u043e\u0447\u044c" -> "night";
            case "\u0422\u0451\u043f\u043b\u044b\u0439" -> "warm";
            case "\u0425\u043e\u043b\u043e\u0434\u043d\u044b\u0439" -> "cold";
            case "\u041c\u0435\u0447\u0442\u0430" -> "dream";
            default -> "none";
        };
        return Identifier.method_60655((String)"astra", (String)("grade_" + key + "_" + this.saturation.getInt()));
    }

    @Override
    public void onTick() {
        if (ColorFilter.mc.field_1773 == null || ColorFilter.mc.field_1687 == null) {
            return;
        }
        Identifier current = ColorFilter.mc.field_1773.method_62906();
        if (current != null && !"astra".equals(current.method_12836())) {
            return;
        }
        Identifier wanted = this.effectId();
        if (!wanted.equals((Object)current)) {
            ColorFilter.mc.field_1773.method_62904(wanted);
        }
    }

    @Override
    protected void onDisable() {
        if (ColorFilter.mc.field_1773 == null) {
            return;
        }
        Identifier current = ColorFilter.mc.field_1773.method_62906();
        if (current != null && "astra".equals(current.method_12836())) {
            ColorFilter.mc.field_1773.method_62905();
        }
        ColorFilter.mc.field_1690.method_1640();
    }
}

