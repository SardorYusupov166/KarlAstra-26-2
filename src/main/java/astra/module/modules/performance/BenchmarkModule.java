/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.performance;

import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.Module;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.ButtonSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import astra.utils.FrameStats;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BenchmarkModule
extends Module {
    private final NumberSetting duration = this.add(new NumberSetting("\u0414\u043b\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", 10.0, 5.0, 30.0, 1.0).unit(" \u0441"));
    private final ButtonSetting start = this.add(new ButtonSetting("\u0417\u0430\u043c\u0435\u0440", "\u041d\u0430\u0447\u0430\u0442\u044c \u0437\u0430\u043c\u0435\u0440", this::toggleRun).label(() -> this.running ? "\u041e\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0437\u0430\u043c\u0435\u0440" : "\u041d\u0430\u0447\u0430\u0442\u044c \u0437\u0430\u043c\u0435\u0440"));
    private final List<Result> results = new ArrayList<Result>();
    private boolean running;
    private long startedAt;
    private int frames;
    private final List<Float> frameTimes = new ArrayList<Float>();
    private long lastFrame;

    public BenchmarkModule() {
        super("benchmark", "\u0421\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u0435 \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438", "\u0417\u0430\u043c\u0435\u0440 \u0441\u0440\u0435\u0434\u043d\u0435\u0433\u043e FPS \u0438 1% low \u0434\u043e/\u043f\u043e\u0441\u043b\u0435 \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439.", Category.PERFORMANCE, Icon.CHART);
        this.alwaysOn();
    }

    private void toggleRun() {
        if (this.running) {
            this.running = false;
            return;
        }
        this.running = true;
        this.startedAt = System.currentTimeMillis();
        this.frames = 0;
        this.frameTimes.clear();
        this.lastFrame = 0L;
    }

    public void onFrame() {
        if (!this.running) {
            return;
        }
        long now = System.nanoTime();
        if (this.lastFrame != 0L) {
            this.frameTimes.add(Float.valueOf((float)(now - this.lastFrame) / 1000000.0f));
        }
        this.lastFrame = now;
        ++this.frames;
        long elapsed = System.currentTimeMillis() - this.startedAt;
        if ((double)elapsed >= (Double)this.duration.get() * 1000.0) {
            this.running = false;
            float avg = (float)this.frames * 1000.0f / (float)elapsed;
            this.frameTimes.sort(null);
            float p99 = this.frameTimes.isEmpty() ? 0.0f : this.frameTimes.get(Math.min(this.frameTimes.size() - 1, (int)((float)this.frameTimes.size() * 0.99f))).floatValue();
            float low = p99 > 0.0f ? 1000.0f / p99 : 0.0f;
            String label = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            this.results.add(new Result(label, avg, low));
            if (this.results.size() > 2) {
                this.results.remove(0);
            }
        }
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 62.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Theme t = ui.theme;
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, ColorUtil.withAlpha(t.bg(), 0.5f));
                if (BenchmarkModule.this.running) {
                    float p = Math.min(1.0f, (float)(System.currentTimeMillis() - BenchmarkModule.this.startedAt) / (float)((Double)BenchmarkModule.this.duration.get() * 1000.0));
                    Fonts.draw(ui.ctx, "\u0418\u0434\u0451\u0442 \u0437\u0430\u043c\u0435\u0440\u2026 " + Math.round(p * 100.0f) + "%", x + 7.0f, y + 8.0f, 7.0f, t.text(), true);
                    Render2D.roundRect(ui.ctx, x + 7.0f, y + 22.0f, w - 14.0f, 4.0f, 2.0f, t.track());
                    Render2D.roundRect(ui.ctx, x + 7.0f, y + 22.0f, Math.max(4.0f, (w - 14.0f) * p), 4.0f, 2.0f, Paint.horizontal(x, x + w, t.accent(), t.accent2()));
                    Fonts.draw(ui.ctx, "\u0421\u0435\u0439\u0447\u0430\u0441: " + FrameStats.fps() + " FPS", x + 7.0f, y + 34.0f, 6.5f, t.textDim(), false);
                    return;
                }
                if (BenchmarkModule.this.results.isEmpty()) {
                    Fonts.draw(ui.ctx, "\u041d\u0435\u0442 \u0437\u0430\u043c\u0435\u0440\u043e\u0432", x + 7.0f, y + 8.0f, 7.0f, t.text(), true);
                    Fonts.draw(ui.ctx, "\u0421\u0434\u0435\u043b\u0430\u0439\u0442\u0435 \u0437\u0430\u043c\u0435\u0440, \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", x + 7.0f, y + 22.0f, 6.0f, t.textMuted(), false);
                    Fonts.draw(ui.ctx, "\u0438 \u043f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u0435 \u0434\u043b\u044f \u0441\u0440\u0430\u0432\u043d\u0435\u043d\u0438\u044f.", x + 7.0f, y + 31.0f, 6.0f, t.textMuted(), false);
                    return;
                }
                float colW = (w - 14.0f) / 2.0f;
                float max = 1.0f;
                for (Result r : BenchmarkModule.this.results) {
                    max = Math.max(max, r.avgFps);
                }
                for (int i = 0; i < BenchmarkModule.this.results.size(); ++i) {
                    Result r;
                    r = BenchmarkModule.this.results.get(i);
                    float cx = x + 7.0f + (float)i * colW;
                    String title = BenchmarkModule.this.results.size() == 2 ? (i == 0 ? "\u0414\u043e" : "\u041f\u043e\u0441\u043b\u0435") : "\u0417\u0430\u043c\u0435\u0440";
                    Fonts.draw(ui.ctx, title + " \u00b7 " + r.label, cx, y + 6.0f, 6.0f, t.textDim(), false);
                    int col = BenchmarkModule.this.results.size() == 2 && i == 1 ? (r.avgFps >= BenchmarkModule.this.results.get((int)0).avgFps ? -11870592 : -770210) : t.accent3();
                    Fonts.draw(ui.ctx, String.format(Locale.ROOT, "%.0f FPS", Float.valueOf(r.avgFps)), cx, y + 16.0f, 10.0f, col, true);
                    Fonts.draw(ui.ctx, String.format(Locale.ROOT, "1%% low: %.0f", Float.valueOf(r.lowFps)), cx, y + 30.0f, 6.0f, t.textMuted(), false);
                    float bw = colW - 10.0f;
                    Render2D.roundRect(ui.ctx, cx, y + 44.0f, bw, 4.0f, 2.0f, t.track());
                    Render2D.roundRect(ui.ctx, cx, y + 44.0f, Math.max(4.0f, bw * r.avgFps / max), 4.0f, 2.0f, Paint.horizontal(cx, cx + bw, t.accent(), col));
                }
            }
        };
    }

    public record Result(String label, float avgFps, float lowFps) {
    }
}

