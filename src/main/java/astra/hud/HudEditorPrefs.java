/*
 * Decompiled with CFR 0.152.
 */
package astra.hud;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.NumberSetting;

public class HudEditorPrefs
extends Module {
    public final BooleanSetting showGrid = this.add(new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0441\u0435\u0442\u043a\u0443", true));
    public final BooleanSetting snapGrid = this.add(new BooleanSetting("\u041f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u043a \u0441\u0435\u0442\u043a\u0435", false));
    public final BooleanSetting snapCenter = this.add(new BooleanSetting("\u041f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u043a \u0446\u0435\u043d\u0442\u0440\u0443", true));
    public final BooleanSetting snapElements = this.add(new BooleanSetting("\u041f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u043a \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430\u043c", true));
    public final NumberSetting gridSize = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u0441\u0435\u0442\u043a\u0438", 20.0, 5.0, 60.0, 5.0));

    public HudEditorPrefs() {
        super("hud_editor", "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 HUD", "\u0421\u0435\u0442\u043a\u0430 \u0438 \u043f\u0440\u0438\u0432\u044f\u0437\u043a\u0438 \u0440\u0435\u0434\u0430\u043a\u0442\u043e\u0440\u0430.", Category.HUD, Icon.GRID);
        this.alwaysOn();
        this.hide();
    }

    public static HudEditorPrefs get() {
        return ModuleManager.get(HudEditorPrefs.class);
    }
}

