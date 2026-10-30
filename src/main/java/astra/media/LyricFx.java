/*
 * Decompiled with CFR 0.152.
 */
package astra.media;

import astra.render.ColorUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public final class LyricFx {
    public static final String SMOOTH = "\u041f\u043b\u0430\u0432\u043d\u043e";
    public static final String RISE = "\u0421\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445";
    public static final String TYPEWRITER = "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430";
    public static final String WAVE = "\u0412\u043e\u043b\u043d\u0430";
    public static final String SPRING = "\u041f\u0440\u0443\u0436\u0438\u043d\u0430";
    public static final String GLITCH = "\u0413\u043b\u0438\u0442\u0447";
    public static final String CASCADE = "\u041a\u0430\u0441\u043a\u0430\u0434 \u0431\u0443\u043a\u0432";
    public static final String NEON = "\u041d\u0435\u043e\u043d-\u0432\u0441\u043f\u044b\u0448\u043a\u0430";
    public static final String SCATTER = "\u0421\u0431\u043e\u0440\u043a\u0430 \u0438\u0437 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432";
    public static final String BLUR = "\u041f\u0440\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435";
    public static final String LIGHTNING = "\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438";
    public static final String ORBIT = "\u041e\u0440\u0431\u0438\u0442\u0430 \u0431\u0443\u043a\u0432";
    public static final String HOLOGRAM = "\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430";
    public static final String FLIP = "\u0420\u0430\u0437\u0432\u043e\u0440\u043e\u0442";
    public static final String SWEEP = "\u0421\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u043f\u0440\u043e\u0445\u043e\u0434";
    public static final String FALL = "\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434";
    public static final String HEARTBEAT = "\u0421\u0435\u0440\u0434\u0446\u0435\u0431\u0438\u0435\u043d\u0438\u0435";
    public static final String NONE = "\u0411\u0435\u0437 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438";
    public static final String[] EFFECTS = new String[]{"\u0412\u043e\u043b\u043d\u0430", "\u041f\u043b\u0430\u0432\u043d\u043e", "\u0421\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445", "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430", "\u041f\u0440\u0443\u0436\u0438\u043d\u0430", "\u0413\u043b\u0438\u0442\u0447", "\u041a\u0430\u0441\u043a\u0430\u0434 \u0431\u0443\u043a\u0432", "\u041d\u0435\u043e\u043d-\u0432\u0441\u043f\u044b\u0448\u043a\u0430", "\u0421\u0431\u043e\u0440\u043a\u0430 \u0438\u0437 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432", "\u041f\u0440\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435", "\u041e\u0440\u0431\u0438\u0442\u0430 \u0431\u0443\u043a\u0432", "\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430", "\u0420\u0430\u0437\u0432\u043e\u0440\u043e\u0442", "\u0421\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u043f\u0440\u043e\u0445\u043e\u0434", "\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434", "\u0421\u0435\u0440\u0434\u0446\u0435\u0431\u0438\u0435\u043d\u0438\u0435", "\u0411\u0435\u0437 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438"};
    public static final String[] WORLD_EFFECTS = (String[])Stream.concat(Arrays.stream(EFFECTS), Stream.of("\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438")).toArray(String[]::new);
    private static final String GLITCH_CHARS = "#%&$@!?*<>/\\=+~\u0414\u0416\u042f\u042e\u266a";

    private LyricFx() {
    }

    public static List<Glyph> split(String text, ToDoubleFunction<String> unitWidth) {
        int cp;
        ArrayList<Glyph> out = new ArrayList<Glyph>();
        float x = 0.0f;
        for (int i = 0; i < text.length(); i += Character.charCount(cp)) {
            cp = text.codePointAt(i);
            Glyph g = new Glyph();
            g.ch = new String(Character.toChars(cp));
            g.x = x;
            g.w = (float)unitWidth.applyAsDouble(g.ch);
            x += g.w;
            out.add(g);
        }
        return out;
    }

    public static float totalWidth(List<Glyph> glyphs) {
        if (glyphs.isEmpty()) {
            return 0.0f;
        }
        Glyph last = glyphs.get(glyphs.size() - 1);
        return last.x + last.w;
    }

    public static void animate(List<Glyph> glyphs, String effect, float t, float exit, float fill, Colors colors, float time, boolean sway) {
        int n = glyphs.size();
        float total = Math.max(0.001f, LyricFx.totalWidth(glyphs));
        for (int i = 0; i < n; ++i) {
            int bright;
            float hit;
            float e;
            Glyph g = glyphs.get(i);
            g.dx = 0.0f;
            g.dy = 0.0f;
            g.scale = 1.0f;
            g.alpha = 1.0f;
            g.rot = 0.0f;
            g.glow = 1.0f;
            float k = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
            switch (effect) {
                case "\u041f\u043b\u0430\u0432\u043d\u043e": {
                    g.alpha = LyricFx.clamp(t / 0.45f);
                    break;
                }
                case "\u0421\u043d\u0438\u0437\u0443 \u0432\u0432\u0435\u0440\u0445": {
                    e = LyricFx.easeOutCubic(LyricFx.clamp(t / 0.45f));
                    g.dy = (1.0f - e) * 1.2f;
                    g.alpha = e;
                    break;
                }
                case "\u041f\u0435\u0447\u0430\u0442\u043d\u0430\u044f \u043c\u0430\u0448\u0438\u043d\u043a\u0430": {
                    float shown = t * 32.0f;
                    g.alpha = LyricFx.clamp(shown - (float)i);
                    g.dy = (1.0f - LyricFx.easeOutCubic(LyricFx.clamp(shown - (float)i))) * 0.25f;
                    break;
                }
                case "\u0412\u043e\u043b\u043d\u0430": {
                    float local = LyricFx.clamp((t - (float)i * 0.028f) / 0.42f);
                    float e2 = LyricFx.easeOutBack(local);
                    g.dy = (1.0f - e2) * 1.1f;
                    g.alpha = LyricFx.clamp(local * 2.0f);
                    break;
                }
                case "\u041f\u0440\u0443\u0436\u0438\u043d\u0430": {
                    e = LyricFx.easeOutElastic(LyricFx.clamp(t / 0.75f));
                    g.scale = Math.max(0.01f, e);
                    g.alpha = LyricFx.clamp(t / 0.15f);
                    float centre = total / 2.0f - (g.x + g.w / 2.0f);
                    g.dx = centre * (1.0f - e);
                    break;
                }
                case "\u0413\u043b\u0438\u0442\u0447": {
                    float p = LyricFx.clamp(t / 0.55f);
                    int frame = (int)(time * 30.0f);
                    float r = LyricFx.hash(i, frame);
                    if (r > p) {
                        g.ch = String.valueOf(GLITCH_CHARS.charAt(Math.min(GLITCH_CHARS.length() - 1, (int)(LyricFx.hash(i + 7, frame) * (float)GLITCH_CHARS.length()))));
                        g.dx = (LyricFx.hash(i, frame + 3) - 0.5f) * 0.35f;
                        g.alpha = 0.4f + LyricFx.hash(i, frame + 5) * 0.6f;
                    }
                    if (!(p < 1.0f) || !(LyricFx.hash(frame, 11) > 0.8f)) break;
                    g.dx += 0.15f;
                    break;
                }
                case "\u041a\u0430\u0441\u043a\u0430\u0434 \u0431\u0443\u043a\u0432": {
                    float local = LyricFx.clamp((t - (float)i * 0.04f) / 0.3f);
                    float e3 = LyricFx.easeOutCubic(local);
                    g.scale = 2.4f - 1.4f * e3;
                    g.alpha = local;
                    g.dy = -(1.0f - e3) * 0.5f;
                    break;
                }
                case "\u041d\u0435\u043e\u043d-\u0432\u0441\u043f\u044b\u0448\u043a\u0430": {
                    float on = t > 0.55f ? 1.0f : (LyricFx.flick(t + (float)i * 0.013f) ? 1.0f : 0.12f);
                    g.alpha = Math.max(0.12f, on) * LyricFx.clamp(t / 0.05f);
                    g.glow = t < 0.8f ? 1.6f : 1.0f;
                    break;
                }
                case "\u0421\u0431\u043e\u0440\u043a\u0430 \u0438\u0437 \u043e\u0441\u043a\u043e\u043b\u043a\u043e\u0432": {
                    float local = LyricFx.clamp((t - k * 0.15f) / 0.6f);
                    float e4 = LyricFx.easeOutCubic(local);
                    float ang = LyricFx.hash(i, 3) * 6.283f;
                    float dist = 2.5f + LyricFx.hash(i, 9) * 2.5f;
                    g.dx = (float)Math.cos(ang) * dist * (1.0f - e4);
                    g.dy = (float)Math.sin(ang) * dist * (1.0f - e4);
                    g.rot = (LyricFx.hash(i, 5) - 0.5f) * 2.5f * (1.0f - e4);
                    g.alpha = LyricFx.clamp(local * 1.5f);
                    break;
                }
                case "\u041f\u0440\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435": {
                    float local = LyricFx.clamp((t - k * 0.25f) / 0.5f);
                    g.alpha = local * local;
                    g.scale = 1.15f - 0.15f * LyricFx.easeOutCubic(local);
                    break;
                }
                case "\u041e\u0440\u0431\u0438\u0442\u0430 \u0431\u0443\u043a\u0432": {
                    float p = LyricFx.clamp((t - k * 0.18f) / 0.65f);
                    float e5 = LyricFx.easeOutCubic(p);
                    float angle = (1.0f - e5) * 6.283f + k * 6.283f;
                    g.dx = (float)Math.cos(angle) * (1.0f - e5) * 2.2f;
                    g.dy = (float)Math.sin(angle) * (1.0f - e5) * 1.6f;
                    g.rot = (1.0f - e5) * (k - 0.5f) * 2.0f;
                    g.alpha = LyricFx.clamp(p * 2.0f);
                    break;
                }
                case "\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430": {
                    float scan;
                    float p = LyricFx.clamp(t / 0.7f);
                    g.alpha = scan = LyricFx.clamp((p - k * 0.55f) / 0.45f);
                    g.dx = (1.0f - scan) * (float)Math.sin(time * 32.0f + (float)(i * 4)) * 0.4f;
                    g.scale = 0.8f + scan * 0.2f;
                    g.glow = 1.0f + (1.0f - scan) * 1.3f;
                    break;
                }
                case "\u0420\u0430\u0437\u0432\u043e\u0440\u043e\u0442": {
                    float p = LyricFx.clamp((t - k * 0.18f) / 0.5f);
                    float e6 = LyricFx.easeOutCubic(p);
                    g.rot = (1.0f - e6) * -1.57f;
                    g.scale = 0.12f + e6 * 0.88f;
                    g.dy = (1.0f - e6) * 0.6f;
                    g.alpha = LyricFx.clamp(p * 2.0f);
                    break;
                }
                case "\u0421\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u043f\u0440\u043e\u0445\u043e\u0434": {
                    float p;
                    g.alpha = p = LyricFx.clamp((t - k * 0.4f) / 0.25f);
                    g.dx = -(1.0f - LyricFx.easeOutCubic(p)) * 0.9f;
                    g.glow = 1.0f + (float)Math.sin((double)p * Math.PI) * 1.7f;
                    break;
                }
                case "\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434": {
                    float p = LyricFx.clamp((t - LyricFx.hash(i, 16) * 0.25f) / 0.6f);
                    float e7 = LyricFx.easeOutBack(p);
                    g.dy = -(1.0f - e7) * (2.0f + LyricFx.hash(i, 18) * 2.0f);
                    g.dx = (1.0f - e7) * (LyricFx.hash(i, 12) - 0.5f);
                    g.rot = (1.0f - e7) * (LyricFx.hash(i, 20) - 0.5f);
                    g.alpha = LyricFx.clamp(p * 2.0f);
                    break;
                }
                case "\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438": {
                    float reveal;
                    hit = t - k * 0.72f;
                    g.alpha = reveal = LyricFx.clamp(hit / 0.08f);
                    g.scale = 0.9f + 0.1f * LyricFx.easeOutBack(reveal);
                    g.dy = (1.0f - reveal) * 0.15f;
                    g.glow = hit >= 0.0f ? 1.0f + (float)Math.exp(-hit * 16.0f) * 2.0f : 1.0f;
                    break;
                }
                case "\u0421\u0435\u0440\u0434\u0446\u0435\u0431\u0438\u0435\u043d\u0438\u0435": {
                    float p = LyricFx.clamp(t / 0.85f);
                    g.alpha = LyricFx.clamp(t / 0.15f);
                    g.scale = 1.0f + (float)Math.sin((double)p * Math.PI * 4.0) * (1.0f - p) * 0.25f;
                    g.glow = 1.0f + (1.0f - p) * 0.7f;
                    break;
                }
            }
            if (sway) {
                g.dy += (float)Math.sin(time * 2.6f + (float)i * 0.45f) * 0.045f;
            }
            float pos = (g.x + g.w / 2.0f) / total;
            int n2 = bright = colors.rainbow() ? ColorUtil.rainbow(pos * 0.6f + time * 0.15f, 1.0f, 0.6f, 1.0f) : ColorUtil.lerp(colors.c1(), colors.c2(), pos);
            if (fill < 0.0f) {
                g.color = bright;
            } else {
                float edge = LyricFx.clamp((fill - pos) / 0.06f + 0.5f);
                g.color = ColorUtil.lerp(colors.dim(), bright, edge);
                g.glow *= 0.35f + 0.65f * edge;
            }
            if (LIGHTNING.equals(effect) && (hit = t - k * 0.72f) >= 0.0f) {
                g.color = ColorUtil.lerp(g.color, -1, (float)Math.exp(-hit * 20.0f) * 0.8f);
            }
            if (!(exit >= 0.0f)) continue;
            e = LyricFx.clamp(exit / 0.35f);
            g.alpha *= 1.0f - e;
            g.dy -= LyricFx.easeOutCubic(e) * 0.9f;
            g.scale *= 1.0f - e * 0.15f;
        }
    }

    private static boolean flick(float t) {
        return t > 0.05f && t < 0.1f || t > 0.18f && t < 0.24f || t > 0.33f && t < 0.37f || t > 0.45f;
    }

    private static float hash(int a, int b) {
        int h = a * 374761393 + b * 668265263;
        h = (h ^ h >>> 13) * 1274126177;
        return (float)((h ^ h >>> 16) & 0xFFFF) / 65535.0f;
    }

    static float clamp(float v) {
        return v < 0.0f ? 0.0f : Math.min(1.0f, v);
    }

    static float easeOutCubic(float t) {
        float f = 1.0f - t;
        return 1.0f - f * f * f;
    }

    static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float f = t - 1.0f;
        return 1.0f + c3 * f * f * f + c1 * f * f;
    }

    static float easeOutElastic(float t) {
        if (t <= 0.0f) {
            return 0.0f;
        }
        if (t >= 1.0f) {
            return 1.0f;
        }
        return (float)(Math.pow(2.0, -10.0f * t) * Math.sin(((double)(t * 10.0f) - 0.75) * 2.0943951023931953) + 1.0);
    }

    public static final class Glyph {
        public String ch;
        public float x;
        public float w;
        public float dx;
        public float dy;
        public float scale = 1.0f;
        public float alpha = 1.0f;
        public float rot;
        public int color;
        public float glow = 1.0f;
    }

    public record Colors(int c1, int c2, int dim, boolean rainbow) {
    }
}

