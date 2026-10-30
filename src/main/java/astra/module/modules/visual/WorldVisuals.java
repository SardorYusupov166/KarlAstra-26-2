/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.visual;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;

public class WorldVisuals
extends Module {
    private final ModeSetting time = this.add(new ModeSetting("\u0412\u0440\u0435\u043c\u044f \u0441\u0443\u0442\u043e\u043a", "\u0421\u0430\u043a\u0443\u0440\u0430-\u043d\u043e\u0447\u044c", "\u041a\u0430\u043a \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", "\u0423\u0442\u0440\u043e", "\u0414\u0435\u043d\u044c", "\u0417\u0430\u043a\u0430\u0442", "\u0421\u0430\u043a\u0443\u0440\u0430-\u043d\u043e\u0447\u044c", "\u041f\u043e\u043b\u043d\u043e\u0447\u044c", "\u0421\u0432\u043e\u0451"));
    private final NumberSetting customTime = this.add((NumberSetting)new NumberSetting("\u0412\u0440\u0435\u043c\u044f (\u0442\u0438\u043a\u0438)", 18000.0, 0.0, 23999.0, 100.0).visibleWhen(() -> this.time.is("\u0421\u0432\u043e\u0451")));
    private final BooleanSetting clearWeather = this.add(new BooleanSetting("\u0411\u0435\u0437 \u0434\u043e\u0436\u0434\u044f (\u0432\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u043e)", true));

    public WorldVisuals() {
        super("world_visuals", "\u041d\u0435\u0431\u043e \u0438 \u0430\u0442\u043c\u043e\u0441\u0444\u0435\u0440\u0430", "\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u043e\u0435 \u0432\u0440\u0435\u043c\u044f \u0441\u0443\u0442\u043e\u043a \u0438 \u043f\u043e\u0433\u043e\u0434\u0430 \u0431\u0435\u0437 \u0432\u043b\u0438\u044f\u043d\u0438\u044f \u043d\u0430 \u0438\u0433\u0440\u0443.", Category.VISUAL, Icon.CLOUD);
    }

    public static long overrideTime() {
        WorldVisuals m = ModuleManager.get(WorldVisuals.class);
        if (m == null || !m.isEnabled()) {
            return -1L;
        }
        return switch ((String)m.time.get()) {
            case "\u0423\u0442\u0440\u043e" -> 0L;
            case "\u0414\u0435\u043d\u044c" -> 6000L;
            case "\u0417\u0430\u043a\u0430\u0442" -> 12500L;
            case "\u0421\u0430\u043a\u0443\u0440\u0430-\u043d\u043e\u0447\u044c" -> 13600L;
            case "\u041f\u043e\u043b\u043d\u043e\u0447\u044c" -> 18000L;
            case "\u0421\u0432\u043e\u0451" -> m.customTime.getInt();
            default -> -1L;
        };
    }

    public static boolean clearWeather() {
        WorldVisuals m = ModuleManager.get(WorldVisuals.class);
        return m != null && m.isEnabled() && (Boolean)m.clearWeather.get() != false;
    }
}

