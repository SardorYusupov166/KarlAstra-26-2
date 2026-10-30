/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.movement;

import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import astra.settings.BooleanSetting;

public class AutoJump
extends Module {
    private final BooleanSetting autoJump = this.add((BooleanSetting)new BooleanSetting("\u0410\u0432\u0442\u043e\u043f\u0440\u044b\u0436\u043e\u043a", false).bind(() -> (Boolean)AutoJump.mc.field_1690.method_42423().method_41753(), v -> {
        AutoJump.mc.field_1690.method_42423().method_41748(v);
        AutoJump.mc.field_1690.method_1640();
    }));

    public AutoJump() {
        super("auto_jump", "\u0410\u0432\u0442\u043e\u043f\u0440\u044b\u0436\u043e\u043a", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439 \u043f\u0440\u044b\u0436\u043e\u043a \u043d\u0430 \u0431\u043b\u043e\u043a \u043f\u0440\u0438 \u0431\u0435\u0433\u0435 (\u043e\u043f\u0446\u0438\u044f Minecraft).", Category.MOVEMENT, Icon.ARROW_UP);
        this.alwaysOn();
    }
}

