/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.utils.FrameStats;
import net.minecraft.client.gui.GuiGraphics;

public class FpsGraphHud
extends HudModule {
    public FpsGraphHud() {
        super("fps_graph", "\u0413\u0440\u0430\u0444\u0438\u043a FPS", "\u0418\u0441\u0442\u043e\u0440\u0438\u044f FPS \u0437\u0430 \u043c\u0438\u043d\u0443\u0442\u0443: \u043c\u0438\u043d., \u0441\u0440\u0435\u0434\u043d., \u043c\u0430\u043a\u0441.", Category.PERFORMANCE, Icon.CHART, 0.12f, 0.6f);
    }

    @Override
    public float width() {
        return 130.0f;
    }

    @Override
    public float height() {
        return 58.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        FpsGraphHud.drawGraph(ctx, 6.0f, 6.0f, 84.0f, h - 12.0f, this.accentColor(), FpsGraphHud.theme());
        Theme t = FpsGraphHud.theme();
        float x = 96.0f;
        Fonts.draw(ctx, "FPS", x, 6.0f, 6.0f, t.textDim(), true);
        Fonts.draw(ctx, String.valueOf(FrameStats.fps()), x, 13.0f, 11.0f, t.accent3(), true);
        Fonts.draw(ctx, "\u041c\u0438\u043d: " + Math.round(FrameStats.min()), x, 28.0f, 5.5f, t.textDim(), false);
        Fonts.draw(ctx, "\u0421\u0440\u0435\u0434: " + Math.round(FrameStats.avg()), x, 36.0f, 5.5f, t.textDim(), false);
        Fonts.draw(ctx, "\u041c\u0430\u043a\u0441: " + Math.round(FrameStats.max()), x, 44.0f, 5.5f, t.textDim(), false);
    }

    public static void drawGraph(GuiGraphics ctx, float x, float y, float w, float h, int accent, Theme t) {
        float[] hist = FrameStats.history();
        int n = FrameStats.historySize();
        float max = Math.max(60.0f, FrameStats.max() * 1.1f);
        Render2D.roundRect(ctx, x, y, w, h, 3.0f, ColorUtil.withAlpha(t.bg(), 0.5f));
        Mesh m = Render2D.begin();
        int bars = 30;
        float bw = w / (float)bars;
        for (int i = 0; i < bars; ++i) {
            int idx = 60 - bars + i;
            if (idx < 60 - n) continue;
            float v = hist[idx];
            float bh = Math.max(1.0f, (h - 4.0f) * Math.min(1.0f, v / max));
            float bx = x + (float)i * bw + bw * 0.15f;
            m.rect(bx, y + h - 2.0f - bh, bw * 0.7f, bh, Paint.vertical(y + h - 2.0f - bh, y + h - 2.0f, ColorUtil.lerp(accent, t.accent3(), 0.4f), ColorUtil.withAlpha(accent, 0.55f)));
        }
        Render2D.end(ctx);
        if (n == 0) {
            Fonts.drawCentered(ctx, "\u0441\u0431\u043e\u0440 \u0434\u0430\u043d\u043d\u044b\u0445\u2026", x + w / 2.0f, y + h / 2.0f - 3.0f, 5.5f, t.textMuted(), false);
        }
    }
}

