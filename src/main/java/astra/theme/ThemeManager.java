/*
 * Decompiled with CFR 0.152.
 */
package astra.theme;

import astra.theme.Theme;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ThemeManager {
    public static final Theme ASTRA_PURPLE = new Theme("Astra Purple", -16054504, -15265750, -803597776, -534570688, -5745153, -45608, -13058568, -1, -4675116, -8556898, -13885370);
    public static final Theme SAKURA_PINK = new Theme("Sakura Pink", -15398892, -14479328, -802811095, -533587658, -45608, -28744, -4160260, -1, -1985069, -6524784, -12968906);
    public static final Theme MIDNIGHT_BLUE = new Theme("Midnight Blue", -16381416, -15854032, -804185546, -535288762, -11567873, -13058568, -5745153, -1, -5654824, -9667936, -14931888);
    public static final Theme CYBER_CYAN = new Theme("Cyber Cyan", -16510956, -16114140, -804511191, -535744970, -14494738, -13773633, -5745153, -1, -6501935, -10515308, -15583168);
    public static final Theme DARK_ANIME = new Theme("Dark Anime", -16251126, -15462120, -803859685, -534898140, -770210, -5745153, -290244, -1, -4870208, -9146752, -14015184);
    private static final Map<String, Theme> THEMES = new LinkedHashMap<String, Theme>();
    private static Theme from;
    private static Theme to;
    private static long switchedAt;
    private static Integer accentOverride;
    private static final long DURATION_MS = 350L;

    private ThemeManager() {
    }

    public static List<Theme> all() {
        return List.copyOf(THEMES.values());
    }

    public static String[] names() {
        return THEMES.keySet().toArray(new String[0]);
    }

    public static Theme byName(String name) {
        return THEMES.getOrDefault(name, ASTRA_PURPLE);
    }

    public static void setTheme(String name) {
        Theme target = ThemeManager.byName(name);
        if (target.name().equals(to.name())) {
            return;
        }
        from = ThemeManager.get();
        to = target;
        switchedAt = System.currentTimeMillis();
    }

    public static void setAccentOverride(Integer accent) {
        accentOverride = accent;
    }

    public static Theme get() {
        float t = Math.min(1.0f, (float)(System.currentTimeMillis() - switchedAt) / 350.0f);
        t = 1.0f - (1.0f - t) * (1.0f - t);
        Theme theme = Theme.lerp(from, to, t);
        return accentOverride != null ? theme.withAccent(accentOverride) : theme;
    }

    public static String currentName() {
        return to.name();
    }

    static {
        for (Theme t : List.of(ASTRA_PURPLE, SAKURA_PINK, MIDNIGHT_BLUE, CYBER_CYAN, DARK_ANIME)) {
            THEMES.put(t.name(), t);
        }
        from = ASTRA_PURPLE;
        to = ASTRA_PURPLE;
    }
}

