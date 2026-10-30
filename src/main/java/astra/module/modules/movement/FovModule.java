/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.movement;

import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import astra.settings.NumberSetting;
import astra.settings.Setting;

public class FovModule
extends Module {
    private final NumberSetting fov = this.add((NumberSetting)new NumberSetting("FOV", 90.0, 30.0, 110.0, 1.0).unit("\u00b0").bind(() -> (double)((Integer)FovModule.mc.options.fov().get()), v -> {
        FovModule.mc.options.fov().set((Object)((int)Math.round(v)));
        FovModule.mc.options.save();
    }));
    private final NumberSetting effects = this.add((NumberSetting)((Setting)new NumberSetting("\u042d\u0444\u0444\u0435\u043a\u0442\u044b FOV", 100.0, 0.0, 100.0, 5.0).unit("%").desc("\u0418\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 FOV \u043e\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438 \u0438 \u0437\u0435\u043b\u0438\u0439")).bind(() -> (Double)FovModule.mc.options.fovEffectScale().get() * 100.0, v -> {
        FovModule.mc.options.fovEffectScale().set((Object)(v / 100.0));
        FovModule.mc.options.save();
    }));

    public FovModule() {
        super("fov", "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 FOV", "\u0418\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u043f\u043e\u043b\u044f \u0437\u0440\u0435\u043d\u0438\u044f \u043f\u043e\u0434 \u0441\u0435\u0431\u044f.", Category.MOVEMENT, Icon.EYE);
        this.alwaysOn();
    }
}

