/*
 * Decompiled with CFR 0.152.
 */
package astra.media;

import astra.media.LyricFx;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

public final class WorldLyricMotion {
    public static final String FADE = "\u0420\u0430\u0441\u0442\u0432\u043e\u0440\u0435\u043d\u0438\u0435";
    public static final String RISE = "\u0423\u043b\u0435\u0442\u0435\u0442\u044c \u0432\u0432\u0435\u0440\u0445";
    public static final String DUST = "\u0420\u0430\u0441\u0441\u044b\u043f\u0430\u0442\u044c\u0441\u044f";
    public static final String SHRINK = "\u0421\u0445\u043b\u043e\u043f\u043d\u0443\u0442\u044c\u0441\u044f";
    public static final String SIDE = "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435";
    public static final String FALL = "\u041e\u043f\u0430\u0441\u0442\u044c";
    public static final String NONE = "\u041c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e";

    private WorldLyricMotion() {
    }

    public static float scale(float percent, float referenceDistance, float actualDistance, boolean screenSize) {
        float reference = Math.max(0.5f, Math.min(10.0f, referenceDistance));
        float factor = screenSize ? Math.max(0.4f, Math.min(actualDistance, reference * 2.0f)) : reference;
        return 0.0042f * Math.max(0.4f, Math.min(3.0f, percent / 100.0f)) * factor;
    }

    public static float distanceAlpha(float distance, float maximum) {
        float near = WorldLyricMotion.clamp((distance - 0.45f) / 0.65f);
        float far = WorldLyricMotion.clamp((maximum - distance) / Math.max(1.0f, maximum * 0.2f));
        return near * far;
    }

    public static void tracking(List<LyricFx.Glyph> glyphs, float spacing) {
        for (LyricFx.Glyph g : glyphs) {
            g.x *= spacing;
        }
    }

    public static void animate(List<LyricFx.Glyph> glyphs, String effect, String exit, float seconds, boolean exiting, float fill, float entryDuration, float exitDuration, float intensity, LyricFx.Colors colors, float time, boolean sway, String idle, float motion, float speed) {
        int n = glyphs.size();
        float span = switch (effect) {
            case "\u0412\u043e\u043b\u043d\u0430" -> Math.max(0.42f, (float)(n - 1) * 0.028f + 0.42f);
            case "\u041a\u0430\u0441\u043a\u0430\u0434 \u0431\u0443\u043a\u0432" -> Math.max(0.3f, (float)(n - 1) * 0.04f + 0.3f);
            case "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430" -> Math.max(0.1f, (float)n / 32.0f);
            case "\u041f\u043b\u0430\u0432\u043d\u043e", "\u0421\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445" -> 0.45f;
            case "\u041f\u0440\u0443\u0436\u0438\u043d\u0430", "\u0421\u0431\u043e\u0440\u043a\u0430 \u0438\u0437 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432", "\u041f\u0440\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435" -> 0.75f;
            case "\u0413\u043b\u0438\u0442\u0447" -> 0.55f;
            case "\u041d\u0435\u043e\u043d-\u0432\u0441\u043f\u044b\u0448\u043a\u0430", "\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438" -> 0.8f;
            case "\u041e\u0440\u0431\u0438\u0442\u0430 \u0431\u0443\u043a\u0432" -> 0.83f;
            case "\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430" -> 0.7f;
            case "\u0420\u0430\u0437\u0432\u043e\u0440\u043e\u0442" -> 0.68f;
            case "\u0421\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u043f\u0440\u043e\u0445\u043e\u0434" -> 0.65f;
            case "\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434", "\u0421\u0435\u0440\u0434\u0446\u0435\u0431\u0438\u0435\u043d\u0438\u0435" -> 0.85f;
            default -> 0.9f;
        };
        float age = exiting ? Math.max(12.0f, (float)n / 20.0f) : Math.max(0.0f, seconds) / Math.max(0.15f, entryDuration) * span;
        LyricFx.animate(glyphs, effect, age, -1.0f, exiting ? -1.0f : fill, colors, time, false);
        float e = WorldLyricMotion.clamp(seconds / Math.max(0.1f, exitDuration));
        float eased = 1.0f - (1.0f - e) * (1.0f - e) * (1.0f - e);
        block59: for (int i = 0; i < glyphs.size(); ++i) {
            LyricFx.Glyph g = glyphs.get(i);
            g.dx *= intensity;
            g.dy *= intensity;
            g.rot *= intensity;
            g.scale = Math.max(0.05f, 1.0f + (g.scale - 1.0f) * intensity);
            float phase = time * 2.0f * speed;
            float a = sway ? motion : 0.0f;
            switch (idle) {
                case "\u041f\u0430\u0440\u0435\u043d\u0438\u0435": {
                    g.dy = (float)((double)g.dy + Math.sin(phase) * 0.25 * (double)a);
                    break;
                }
                case "\u0412\u043e\u043b\u043d\u0430": {
                    g.dy = (float)((double)g.dy + Math.sin(phase + (float)i * 0.42f) * (double)0.32f * (double)a);
                    break;
                }
                case "\u0414\u044b\u0445\u0430\u043d\u0438\u0435": {
                    g.scale = (float)((double)g.scale * (1.0 + Math.sin(phase) * (double)0.07f * (double)a));
                    break;
                }
                case "\u041e\u0440\u0431\u0438\u0442\u0430": {
                    g.dx = (float)((double)g.dx + Math.cos(phase + (float)i * 0.3f) * (double)0.12f * (double)a);
                    g.dy = (float)((double)g.dy + Math.sin(phase + (float)i * 0.3f) * (double)0.22f * (double)a);
                    break;
                }
            }
            if (!exiting) continue;
            if (NONE.equals(exit)) {
                g.alpha = 0.0f;
                continue;
            }
            g.alpha *= 1.0f - e;
            switch (exit) {
                case "\u0423\u043b\u0435\u0442\u0435\u0442\u044c \u0432\u0432\u0435\u0440\u0445": {
                    g.dy -= eased * 0.9f * intensity;
                    continue block59;
                }
                case "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435": {
                    g.dx += eased * 2.0f * intensity;
                    continue block59;
                }
                case "\u0421\u0445\u043b\u043e\u043f\u043d\u0443\u0442\u044c\u0441\u044f": {
                    g.scale *= Math.max(0.04f, 1.0f - eased);
                    continue block59;
                }
                case "\u0420\u0430\u0441\u0441\u044b\u043f\u0430\u0442\u044c\u0441\u044f": {
                    g.dx += (WorldLyricMotion.hash(i, 4) - 0.5f) * eased * 4.0f * intensity;
                    g.dy -= (0.5f + WorldLyricMotion.hash(i, 7)) * eased * 1.8f * intensity;
                    g.rot += (WorldLyricMotion.hash(i, 8) - 0.5f) * eased * 2.0f * intensity;
                    continue block59;
                }
                case "\u041e\u043f\u0430\u0441\u0442\u044c": {
                    g.dy += e * e * (1.2f + WorldLyricMotion.hash(i, 3)) * intensity;
                    g.rot += (WorldLyricMotion.hash(i, 6) - 0.5f) * eased * 1.5f * intensity;
                    continue block59;
                }
            }
        }
    }

    public static List<String> wrap(String text, float width, int rows, ToDoubleFunction<String> measure) {
        ArrayList<String> lines = new ArrayList<String>();
        String rest = text.replace('\n', ' ').strip();
        width = Math.max(12.0f, width);
        while (!rest.isEmpty() && lines.size() < rows) {
            int next;
            if (measure.applyAsDouble(rest) <= (double)width) {
                lines.add(rest);
                rest = "";
                break;
            }
            int end = 0;
            int space = -1;
            int i = 0;
            while (!(i >= rest.length() || measure.applyAsDouble(rest.substring(0, next = i + Character.charCount(rest.codePointAt(i)))) > (double)width && end > 0)) {
                end = next;
                if (Character.isWhitespace(rest.codePointAt(i))) {
                    space = i;
                }
                i = next;
            }
            if (space > 0) {
                end = space;
            }
            if (end <= 0) {
                end = Character.charCount(rest.codePointAt(0));
            }
            Object row = rest.substring(0, end).strip();
            rest = rest.substring(end).strip();
            if (lines.size() == rows - 1 && !rest.isEmpty()) {
                while (!((String)row).isEmpty() && measure.applyAsDouble((String)row + "\u2026") > (double)width) {
                    row = ((String)row).substring(0, ((String)row).offsetByCodePoints(((String)row).length(), -1));
                }
                row = (String)row + "\u2026";
            }
            lines.add((String)row);
        }
        return lines;
    }

    private static float hash(int a, int b) {
        int h = a * 374761393 + b * 668265263;
        h = (h ^ h >>> 13) * 1274126177;
        return (float)((h ^ h >>> 16) & 0xFFFF) / 65535.0f;
    }

    public static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}

