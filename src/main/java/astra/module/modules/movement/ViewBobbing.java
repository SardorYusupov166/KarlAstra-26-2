/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.movement;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.NumberSetting;

public class ViewBobbing
extends Module {
    private final BooleanSetting vanilla = this.add((BooleanSetting)new BooleanSetting("\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435 \u043a\u0430\u043c\u0435\u0440\u044b", true).bind(() -> (Boolean)ViewBobbing.mc.field_1690.method_42448().method_41753(), v -> {
        ViewBobbing.mc.field_1690.method_42448().method_41748(v);
        ViewBobbing.mc.field_1690.method_1640();
    }));
    private final NumberSetting intensity = this.add(new NumberSetting("\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c", 60.0, 0.0, 200.0, 5.0).unit("%"));

    public ViewBobbing() {
        super("view_bobbing", "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u044f", "\u0420\u0435\u0433\u0443\u043b\u0438\u0440\u043e\u0432\u043a\u0430 \u043f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u044f \u043a\u0430\u043c\u0435\u0440\u044b \u043f\u0440\u0438 \u0445\u043e\u0434\u044c\u0431\u0435.", Category.MOVEMENT, Icon.WAVE);
        this.setDefaultEnabled(true);
    }

    public static float factor() {
        ViewBobbing m = ModuleManager.get(ViewBobbing.class);
        if (m == null || !m.isEnabled()) {
            return 1.0f;
        }
        return m.intensity.getFloat() / 100.0f;
    }
}

