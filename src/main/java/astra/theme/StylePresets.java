/*
 * Decompiled with CFR 0.152.
 */
package astra.theme;

import astra.module.ClientSettings;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.settings.Setting;
import java.util.List;

public final class StylePresets {
    public static final List<Preset> ALL = List.of(new Preset("\u0421\u0430\u043a\u0443\u0440\u0430", "\u0440\u043e\u0437\u043e\u0432\u044b\u0435 \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0438 \u0430\u043d\u0433\u0435\u043b\u044c\u0441\u043a\u0438\u0435 \u043a\u0440\u044b\u043b\u044c\u044f", -28720, -11800, () -> {
        StylePresets.theme("Sakura Pink");
        StylePresets.wings(true, "\u0410\u043d\u0433\u0435\u043b\u044c\u0441\u043a\u0438\u0435", -11800);
        StylePresets.halo(true, -11787);
        StylePresets.aura(false, -28720);
        StylePresets.trail(true, "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438", -28720);
        StylePresets.cape(true, "\u0421\u0430\u043a\u0443\u0440\u0430");
    }), new Preset("\u041a\u0438\u0431\u0435\u0440\u043f\u0430\u043d\u043a", "\u043d\u0435\u043e\u043d\u043e\u0432\u044b\u0439 \u0433\u043e\u043b\u0443\u0431\u043e\u0439 \u0438 \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b", -14494738, -5745153, () -> {
        StylePresets.theme("Cyber Cyan");
        StylePresets.wings(true, "\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0435", -13058568);
        StylePresets.halo(false, -14494738);
        StylePresets.aura(true, -14494738);
        StylePresets.trail(true, "\u041e\u0433\u043e\u043d\u044c\u043a\u0438", -14494738);
        StylePresets.cape(true, "\u041d\u0435\u043e\u043d");
    }), new Preset("\u041d\u043e\u0447\u043d\u0430\u044f \u0432\u0435\u0434\u044c\u043c\u043e\u0447\u043a\u0430", "\u0442\u0451\u043c\u043d\u044b\u0435 \u043a\u0440\u044b\u043b\u044c\u044f \u0438 \u0437\u0432\u0451\u0437\u0434\u044b", -7643914, -14804149, () -> {
        StylePresets.theme("Dark Anime");
        StylePresets.wings(true, "\u0422\u0451\u043c\u043d\u044b\u0435", -5011201);
        StylePresets.halo(false, -3885571);
        StylePresets.aura(true, -7643914);
        StylePresets.trail(true, "\u0417\u0432\u0451\u0437\u0434\u044b", -3885571);
        StylePresets.cape(true, "\u041d\u043e\u0447\u044c");
    }), new Preset("\u0410\u043d\u0433\u0435\u043b", "\u0431\u0435\u043b\u044b\u0435 \u043a\u0440\u044b\u043b\u044c\u044f \u0438 \u0437\u043e\u043b\u043e\u0442\u043e\u0439 \u043d\u0438\u043c\u0431", -2878, -1, () -> {
        StylePresets.theme("Astra Purple");
        StylePresets.wings(true, "\u0410\u043d\u0433\u0435\u043b\u044c\u0441\u043a\u0438\u0435", -1);
        StylePresets.halo(true, -5720);
        StylePresets.aura(true, -5720);
        StylePresets.trail(true, "\u0417\u0432\u0451\u0437\u0434\u044b", -1);
        StylePresets.cape(true, "Astra");
    }), new Preset("\u0414\u0435\u043c\u043e\u043d", "\u0430\u043b\u044b\u0435 \u043a\u0440\u044b\u043b\u044c\u044f \u0438 \u043e\u0433\u043e\u043d\u044c\u043a\u0438", -50340, -8446691, () -> {
        StylePresets.theme("Dark Anime");
        StylePresets.wings(true, "\u0422\u0451\u043c\u043d\u044b\u0435", -50340);
        StylePresets.halo(false, -50340);
        StylePresets.aura(true, -50340);
        StylePresets.trail(true, "\u041e\u0433\u043e\u043d\u044c\u043a\u0438", -41121);
        StylePresets.cape(true, "\u041d\u043e\u0447\u044c");
    }));

    private StylePresets() {
    }

    private static void theme(String name) {
        ClientSettings cs = ClientSettings.get();
        if (cs != null) {
            cs.theme.set(name);
        }
    }

    private static void set(Module m, String setting, Object value) {
        for (Setting<?> s : m.getSettings()) {
            if (!s.getName().equals(setting)) continue;
            ((Setting)s).set(value);
            return;
        }
    }

    private static Module module(String id, boolean on) {
        Module m = ModuleManager.byId(id);
        if (m != null) {
            m.setEnabled(on);
        }
        return m;
    }

    private static void wings(boolean on, String model, int color) {
        Module m = StylePresets.module("wings", on);
        if (m == null) {
            return;
        }
        StylePresets.set(m, "\u041c\u043e\u0434\u0435\u043b\u044c", model);
        StylePresets.set(m, "\u0426\u0432\u0435\u0442", color);
    }

    private static void halo(boolean on, int color) {
        Module m = StylePresets.module("halo", on);
        if (m != null) {
            StylePresets.set(m, "\u0426\u0432\u0435\u0442", color);
        }
    }

    private static void aura(boolean on, int color) {
        Module m = StylePresets.module("aura", on);
        if (m != null) {
            StylePresets.set(m, "\u0426\u0432\u0435\u0442", color);
        }
    }

    private static void trail(boolean on, String type, int color) {
        Module m = StylePresets.module("trail", on);
        if (m == null) {
            return;
        }
        StylePresets.set(m, "\u042d\u0444\u0444\u0435\u043a\u0442", type);
        StylePresets.set(m, "\u0426\u0432\u0435\u0442", color);
    }

    private static void cape(boolean on, String model) {
        Module m = StylePresets.module("cape", on);
        if (m != null) {
            StylePresets.set(m, "\u041c\u043e\u0434\u0435\u043b\u044c", model);
        }
    }

    public record Preset(String name, String hint, int color1, int color2, Runnable apply) {
    }
}

