/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package astra.module.modules.visual;

import astra.animation.AnimationUtils;
import astra.media.AudioSpectrum;
import astra.media.CoverArt;
import astra.media.NowPlaying;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.hud.StatusBars;
import astra.module.modules.visual.MusicModule;
import astra.module.modules.visual.NowPlayingModule;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import astra.utils.FrameStats;
import net.minecraft.client.gui.GuiGraphics;

public class VisualizerModule
extends Module {
    private static final String HOTBAR = "\u041d\u0430\u0434 \u0445\u043e\u0442\u0431\u0430\u0440\u043e\u043c";
    private static final String EDGES = "\u041f\u043e \u043a\u0440\u0430\u044f\u043c \u044d\u043a\u0440\u0430\u043d\u0430";
    private static final String RING = "\u041a\u043e\u043b\u044c\u0446\u043e \u0443 \u043f\u0440\u0438\u0446\u0435\u043b\u0430";
    private final ModeSetting style = this.add(new ModeSetting("\u0412\u0438\u0434", "\u041d\u0430\u0434 \u0445\u043e\u0442\u0431\u0430\u0440\u043e\u043c", "\u041d\u0430\u0434 \u0445\u043e\u0442\u0431\u0430\u0440\u043e\u043c", "\u041f\u043e \u043a\u0440\u0430\u044f\u043c \u044d\u043a\u0440\u0430\u043d\u0430", "\u041a\u043e\u043b\u044c\u0446\u043e \u0443 \u043f\u0440\u0438\u0446\u0435\u043b\u0430"));
    private final ModeSetting colors = this.add(new ModeSetting("\u0426\u0432\u0435\u0442\u0430", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u0426\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438", "\u0420\u0430\u0434\u0443\u0433\u0430"));
    private final NumberSetting bars = this.add(new NumberSetting("\u041f\u043e\u043b\u043e\u0441\u043e\u043a", 32.0, 12.0, 64.0, 4.0));
    private final NumberSetting height = this.add(new NumberSetting("\u0412\u044b\u0441\u043e\u0442\u0430", 100.0, 30.0, 250.0, 10.0).unit("%"));
    private final NumberSetting opacity = this.add(new NumberSetting("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 70.0, 10.0, 100.0, 5.0).unit("%"));
    private final ModeSetting source = this.add((ModeSetting)new ModeSetting("\u0417\u0432\u0443\u043a", "\u0410\u0432\u0442\u043e", "\u0410\u0432\u0442\u043e", "\u0422\u043e\u043b\u044c\u043a\u043e \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f").desc("\u0410\u0432\u0442\u043e \u2014 \u043d\u0430\u0441\u0442\u043e\u044f\u0449\u0438\u0439 \u0437\u0432\u0443\u043a \u041f\u041a \u0447\u0435\u0440\u0435\u0437 \u00ab\u0421\u0442\u0435\u0440\u0435\u043e \u043c\u0438\u043a\u0448\u0435\u0440\u00bb Windows, \u0435\u0441\u043b\u0438 \u043e\u043d \u0432\u043a\u043b\u044e\u0447\u0451\u043d"));
    private final float[] shown = new float[64];

    public VisualizerModule() {
        super("visualizer", "\u0412\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440 \u043c\u0443\u0437\u044b\u043a\u0438", "\u042d\u043a\u0432\u0430\u043b\u0430\u0439\u0437\u0435\u0440 \u0432 \u0442\u0430\u043a\u0442 \u043c\u0443\u0437\u044b\u043a\u0435: \u043d\u0430\u0434 \u0445\u043e\u0442\u0431\u0430\u0440\u043e\u043c, \u043f\u043e \u043a\u0440\u0430\u044f\u043c \u0438\u043b\u0438 \u0432\u043e\u043a\u0440\u0443\u0433 \u043f\u0440\u0438\u0446\u0435\u043b\u0430.", Category.VISUAL, Icon.CHART);
    }

    public static VisualizerModule get() {
        return ModuleManager.get(VisualizerModule.class);
    }

    @Override
    protected void onEnable() {
        if (this.source.is("\u0410\u0432\u0442\u043e")) {
            AudioSpectrum.start();
        }
    }

    @Override
    protected void onDisable() {
        AudioSpectrum.stop();
    }

    @Override
    public void onTick() {
        if (this.source.is("\u0410\u0432\u0442\u043e")) {
            AudioSpectrum.start();
        } else {
            AudioSpectrum.stop();
        }
    }

    private static boolean musicPlaying() {
        NowPlayingModule np = NowPlayingModule.get();
        if (np != null && np.isEnabled() && np.track().hasTrack() && np.track().playing()) {
            return true;
        }
        MusicModule m = ModuleManager.get(MusicModule.class);
        return m != null && m.isEnabled() && m.nowPlayingTitle() != null;
    }

    private float[] levels(int n, float dt) {
        boolean real;
        float[] raw = new float[n];
        boolean bl = real = this.source.is("\u0410\u0432\u0442\u043e") && AudioSpectrum.available();
        if (real) {
            float[] s = AudioSpectrum.levels();
            for (int i = 0; i < n; ++i) {
                raw[i] = s[Math.min(s.length - 1, i * s.length / n)];
            }
        } else if (VisualizerModule.musicPlaying()) {
            float t = AnimationUtils.time();
            float beat = (float)Math.pow(Math.abs(Math.sin((double)t * Math.PI * 1.9)), 8.0);
            for (int i = 0; i < n; ++i) {
                float k = (float)i / (float)n;
                float bass = (1.0f - k) * (0.35f + 0.55f * beat);
                float wave = 0.25f + 0.25f * (float)Math.sin(t * (3.1f + k * 7.0f) + (float)i * 1.7f) * (float)Math.cos(t * 1.3f + (float)i);
                raw[i] = Math.max(0.05f, Math.min(1.0f, bass + wave * (0.6f + 0.4f * k)));
            }
        }
        for (int i = 0; i < n; ++i) {
            float target = raw[i];
            int n2 = i;
            this.shown[n2] = this.shown[n2] + (target - this.shown[i]) * Math.min(1.0f, dt * (float)(target > this.shown[i] ? 30 : 8));
        }
        return this.shown;
    }

    private int color(Theme t, int i, int n) {
        float k = (float)i / (float)Math.max(1, n - 1);
        return switch ((String)this.colors.get()) {
            case "\u0420\u0430\u0434\u0443\u0433\u0430" -> ColorUtil.rainbow(k * 0.8f, 1.0f, 0.65f, 1.0f);
            case "\u0426\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438" -> {
                CoverArt.Cover c;
                NowPlayingModule np = NowPlayingModule.get();
                NowPlaying tr = np != null ? np.track() : NowPlaying.NONE;
                CoverArt.Cover v1 = c = tr.hasTrack() ? CoverArt.get(tr.cover()) : null;
                if (c != null) {
                    yield ColorUtil.lerp(c.color1(), c.color2(), k);
                }
                yield ColorUtil.lerp(t.accent(), t.accent2(), k);
            }
            default -> ColorUtil.lerp(t.accent(), t.accent2(), k);
        };
    }

    public void renderOverlay(GuiGraphics ctx) {
        int n = Math.min(64, this.bars.getInt());
        float[] lv = this.levels(n, FrameStats.delta());
        float max = 0.0f;
        for (int i = 0; i < n; ++i) {
            max = Math.max(max, lv[i]);
        }
        if (max < 0.02f) {
            return;
        }
        Theme t = ThemeManager.get();
        float sw = ctx.guiWidth();
        float sh = ctx.guiHeight();
        float hMul = this.height.getFloat() / 100.0f;
        float op = this.opacity.getFloat() / 100.0f;
        float prev = Render2D.alpha;
        Render2D.alpha *= op;
        switch ((String)this.style.get()) {
            case "\u041f\u043e \u043a\u0440\u0430\u044f\u043c \u044d\u043a\u0440\u0430\u043d\u0430": {
                float bh = (sh - 40.0f) / (float)n;
                float maxW = 26.0f * hMul;
                for (int i = 0; i < n; ++i) {
                    float len = Math.max(1.5f, lv[i] * maxW);
                    float y = 20.0f + (float)i * bh;
                    int c = this.color(t, i, n);
                    Render2D.roundRect(ctx, 0.0f, y, len, bh * 0.6f, bh * 0.3f, ColorUtil.withAlpha(c, 0.85f));
                    Render2D.roundRect(ctx, sw - len, y, len, bh * 0.6f, bh * 0.3f, ColorUtil.withAlpha(c, 0.85f));
                }
                break;
            }
            case "\u041a\u043e\u043b\u044c\u0446\u043e \u0443 \u043f\u0440\u0438\u0446\u0435\u043b\u0430": {
                float cx = sw / 2.0f;
                float cy = sh / 2.0f;
                float r0 = 14.0f;
                float maxL = 12.0f * hMul;
                for (int i = 0; i < n; ++i) {
                    double a = Math.PI * 2 * (double)i / (double)n - 1.5707963267948966;
                    float len = Math.max(0.8f, lv[i] * maxL);
                    float x0 = cx + (float)Math.cos(a) * r0;
                    float y0 = cy + (float)Math.sin(a) * r0;
                    float x1 = cx + (float)Math.cos(a) * (r0 + len);
                    float y1 = cy + (float)Math.sin(a) * (r0 + len);
                    Render2D.line(ctx, x0, y0, x1, y1, 1.6f, ColorUtil.withAlpha(this.color(t, i, n), 0.8f));
                }
                break;
            }
            default: {
                float total = Math.min(sw * 0.5f, 260.0f);
                float bw = total / (float)n;
                float x0 = (sw - total) / 2.0f;
                float base = sh - 38.0f;
                StatusBars bars = StatusBars.get();
                if (StatusBars.replacing() && bars != null) {
                    base = bars.getY(sh) - 3.0f;
                }
                float glowH = Math.max(3.0f, max * 26.0f * hMul);
                Render2D.glow(ctx, x0, base - glowH * 0.7f, total, glowH * 0.7f, 4.0f, 6.0f, ColorUtil.withAlpha(ColorUtil.lerp(this.color(t, 0, n), this.color(t, n - 1, n), 0.5f), 0.16f));
                for (int i = 0; i < n; ++i) {
                    int band = Math.abs(i - n / 2) * 2;
                    float v = lv[Math.min(n - 1, band)];
                    float bh = Math.max(1.5f, v * 26.0f * hMul);
                    int c = this.color(t, i, n);
                    float x = x0 + (float)i * bw;
                    Render2D.roundRect(ctx, x, base - bh, bw * 0.7f, bh, bw * 0.35f, ColorUtil.withAlpha(c, 0.9f));
                }
            }
        }
        Render2D.alpha = prev;
    }
}

