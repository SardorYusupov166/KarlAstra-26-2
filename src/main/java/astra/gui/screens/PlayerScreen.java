/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.client.input.MouseButtonInfo
 *  net.minecraft.network.chat.Component
 */
package astra.gui.screens;

import astra.animation.AnimationUtils;
import astra.media.LyricFx;
import astra.media.Lyrics;
import astra.media.MediaBridge;
import astra.media.NowPlaying;
import astra.module.modules.visual.NowPlayingModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import astra.utils.FrameStats;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

public class PlayerScreen
extends Screen {
    private final Screen parent;
    private final long openedAt = System.currentTimeMillis();
    private float lyricScroll = -1.0f;
    private float plainScroll;
    private boolean dragging;
    private float dragProgress;
    private float[] prevBtn = new float[3];
    private float[] playBtn = new float[3];
    private float[] nextBtn = new float[3];
    private float[] bar = new float[4];
    private final List<float[]> lineHits = new ArrayList<float[]>();

    public PlayerScreen(Screen parent) {
        super((Component)Component.method_43470((String)"\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442"));
        this.parent = parent;
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25419() {
        this.field_22787.method_1507(this.parent);
    }

    private static NowPlayingModule module() {
        return NowPlayingModule.get();
    }

    public void method_25420(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (this.field_22787.field_1687 == null) {
            this.method_57728(ctx, delta);
        }
        if (this.field_22787.field_1690.method_57703() >= 1) {
            ctx.method_71278();
        }
        Theme t = ThemeManager.get();
        float e = this.open();
        ctx.method_25296(0, 0, this.field_22789, this.field_22790, ColorUtil.withAlpha(-16448759, 0.93f * e), ColorUtil.withAlpha(ColorUtil.lerp(t.bg(), -16777216, 0.4f), 0.97f * e));
    }

    private float open() {
        return AnimationUtils.easeOutCubic(Math.min(1.0f, (float)(System.currentTimeMillis() - this.openedAt) / 260.0f));
    }

    public void method_25394(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        int[] nArray;
        super.method_25394(ctx, mouseX, mouseY, delta);
        NowPlayingModule mod = PlayerScreen.module();
        if (mod == null) {
            return;
        }
        Theme t = ThemeManager.get();
        NowPlaying np = mod.track();
        float prevAlpha = Render2D.alpha;
        Render2D.alpha = this.open();
        float W = this.field_22789;
        float H = this.field_22790;
        boolean has = np.hasTrack();
        if (has) {
            nArray = mod.colors(np);
        } else {
            int[] nArray2 = new int[2];
            nArray2[0] = t.accent();
            nArray = nArray2;
            nArray2[1] = t.accent2();
        }
        int[] c = nArray;
        float time = AnimationUtils.time();
        float pad = Math.max(14.0f, W * 0.03f);
        float histW = W >= 560.0f ? Math.min(170.0f, W * 0.2f) : 0.0f;
        float lyrW = W >= 460.0f ? Math.min(300.0f, W * 0.32f) : 0.0f;
        float centerL = pad + histW + (histW > 0.0f ? pad : 0.0f);
        float centerR = W - pad - lyrW - (lyrW > 0.0f ? pad : 0.0f);
        float cx = (centerL + centerR) / 2.0f;
        float coverS = Math.min(150.0f, Math.min(H * 0.34f, (centerR - centerL) * 0.6f));
        float coverY = H * 0.13f;
        float blobY = coverY + coverS / 2.0f;
        this.drawBlob(ctx, cx, blobY, coverS, c, np.playing(), time);
        if (has) {
            float coverX = cx - coverS / 2.0f;
            float lift = np.playing() ? (float)Math.sin(time * 1.6f) * 2.0f : 0.0f;
            NowPlayingModule.drawCover(ctx, np, c, coverX, coverY + lift, coverS, coverS * 0.1f);
            float titleSize = Math.min(26.0f, coverS * 0.17f);
            float maxW = centerR - centerL - 10.0f;
            List<LyricFx.Glyph> title = LyricFx.split(np.title(), ch -> Fonts.width(ch, 1.0f, true));
            float tw = LyricFx.totalWidth(title) * titleSize;
            if (tw > maxW) {
                titleSize *= maxW / tw;
                tw = maxW;
            }
            float ty = coverY + coverS + 16.0f;
            LyricFx.animate(title, "\u0412\u043e\u043b\u043d\u0430", (float)(System.currentTimeMillis() - this.openedAt) / 1000.0f, -1.0f, -1.0f, new LyricFx.Colors(-1, ColorUtil.lerp(-1, c[0], 0.45f), 0, false), time, false);
            NowPlayingModule.drawGlyphs(ctx, title, cx - tw / 2.0f, ty, titleSize, true, true, c[0], 1.0f);
            float ay = ty + titleSize + 4.0f;
            String artist = np.artist().isEmpty() ? "\u2014" : np.artist();
            Fonts.drawCentered(ctx, Fonts.trim(artist, maxW, 9.0f, false), cx, ay, 9.0f, ColorUtil.lerp(t.textDim(), c[1], 0.3f), false);
            String app = np.appName();
            float aw = Fonts.width(app, 6.0f, true) + 12.0f;
            float chipY = ay + 14.0f;
            Render2D.roundRect(ctx, cx - aw / 2.0f, chipY, aw, 11.0f, 5.5f, ColorUtil.withAlpha(c[0], 0.18f));
            Render2D.outline(ctx, cx - aw / 2.0f, chipY, aw, 11.0f, 5.5f, 0.6f, ColorUtil.withAlpha(c[0], 0.6f));
            Fonts.drawCentered(ctx, app, cx, chipY + 2.6f, 6.0f, ColorUtil.brighten(c[0], 0.3f), true);
            float bw = Math.min(260.0f, centerR - centerL - 20.0f);
            float by = chipY + 24.0f;
            float p = this.dragging ? this.dragProgress : np.progress();
            this.bar = new float[]{cx - bw / 2.0f, by - 5.0f, bw, 13.0f};
            boolean barHover = np.canSeek() && PlayerScreen.inside(mouseX, mouseY, this.bar);
            NowPlayingModule.drawProgress(ctx, cx - bw / 2.0f, by, bw, barHover || this.dragging ? 4.0f : 3.0f, np.durMs() > 0L ? p : 0.0f, c);
            long shownPos = this.dragging ? (long)(this.dragProgress * (float)np.durMs()) : np.position();
            Fonts.draw(ctx, NowPlaying.time(shownPos), cx - bw / 2.0f, by + 8.0f, 6.4f, t.textMuted(), false);
            if (np.durMs() > 0L) {
                Fonts.drawRight(ctx, NowPlaying.time(np.durMs()), cx + bw / 2.0f, by + 8.0f, 6.4f, t.textMuted(), false);
            }
            float btnY = by + 32.0f;
            this.playBtn = new float[]{cx, btnY, 17.0f};
            this.prevBtn = new float[]{cx - 44.0f, btnY, 12.0f};
            this.nextBtn = new float[]{cx + 44.0f, btnY, 12.0f};
            this.drawRoundButton(ctx, this.prevBtn, mouseX, mouseY, false, c, true);
            this.drawRoundButton(ctx, this.playBtn, mouseX, mouseY, true, c, true);
            this.drawRoundButton(ctx, this.nextBtn, mouseX, mouseY, false, c, true);
            this.drawPrevNext(ctx, this.prevBtn, false);
            this.drawPrevNext(ctx, this.nextBtn, true);
            if (np.playing()) {
                float s = 4.2f;
                Render2D.roundRect(ctx, cx - 1.6f - s, btnY - s * 1.4f, s, s * 2.8f, 1.2f, -15464417);
                Render2D.roundRect(ctx, cx + 1.6f, btnY - s * 1.4f, s, s * 2.8f, 1.2f, -15464417);
            } else {
                float s = 6.5f;
                Render2D.triangle(ctx, cx - s * 0.55f, btnY - s, cx - s * 0.55f, btnY + s, cx + s * 0.9f, btnY, -15464417);
            }
        } else {
            float y = coverY + coverS / 2.0f - 14.0f;
            Fonts.drawCentered(ctx, "\u0421\u0435\u0439\u0447\u0430\u0441 \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u0438\u0433\u0440\u0430\u0435\u0442", cx, y, 14.0f, t.text(), true);
            String hint = !MediaBridge.error().isEmpty() ? MediaBridge.error() : "\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u0435 \u043c\u0443\u0437\u044b\u043a\u0443 \u0432 Spotify, \u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0435, \u0431\u0440\u0430\u0443\u0437\u0435\u0440\u0435 \u0438\u043b\u0438 \u043f\u043b\u0435\u0435\u0440\u0435 \u2014 \u043e\u043d\u0430 \u043f\u043e\u044f\u0432\u0438\u0442\u0441\u044f \u0437\u0434\u0435\u0441\u044c";
            for (String line : Fonts.wrap(hint, centerR - centerL - 20.0f, 7.5f, false)) {
                Fonts.drawCentered(ctx, line, cx, (y += 12.0f) + 10.0f, 7.5f, t.textMuted(), false);
            }
        }
        if (histW > 0.0f) {
            this.drawHistory(ctx, mod, pad, pad + 6.0f, histW, H - pad * 2.0f - 20.0f, np, c, t);
        }
        if (lyrW > 0.0f) {
            this.drawLyrics(ctx, mod, np, W - pad - lyrW, pad + 6.0f, lyrW, H - pad * 2.0f - 20.0f, c, t, mouseX, mouseY);
        }
        Fonts.drawCentered(ctx, "\u041f\u0440\u043e\u0431\u0435\u043b \u2014 \u043f\u0430\u0443\u0437\u0430   \u00b7   \u2190 \u2192 \u2014 \u043f\u0435\u0440\u0435\u043c\u043e\u0442\u043a\u0430 10 \u0441   \u00b7   Shift + \u2190 \u2192 \u2014 \u0442\u0440\u0435\u043a   \u00b7   Esc \u2014 \u0437\u0430\u043a\u0440\u044b\u0442\u044c", W / 2.0f, H - 14.0f, 6.2f, ColorUtil.withAlpha(t.textMuted(), 0.8f), false);
        Render2D.alpha = prevAlpha;
    }

    private void drawBlob(GuiGraphics ctx, float cx, float cy, float size, int[] c, boolean playing, float time) {
        float a;
        int i;
        float beat = playing ? (float)Math.pow(Math.abs(Math.sin((double)time * Math.PI * 1.05)), 6.0) : 0.0f;
        float base = size * (0.95f + beat * 0.06f);
        for (int i2 = 0; i2 < 6; ++i2) {
            float a2 = time * (0.25f + (float)i2 * 0.07f) + (float)i2 * 1.1f;
            float ox = (float)Math.cos(a2) * size * 0.18f;
            float oy = (float)Math.sin(a2 * 1.3f) * size * 0.14f;
            float r = base * (0.55f + 0.08f * (float)i2);
            int col = i2 % 2 == 0 ? c[0] : c[1];
            Render2D.glow(ctx, cx + ox - r / 2.0f, cy + oy - r / 2.0f, r, r, r / 2.0f, size * 0.55f, ColorUtil.withAlpha(col, (0.16f + beat * 0.06f) * (1.0f - (float)i2 * 0.08f)));
        }
        int rays = 28;
        for (i = 0; i < rays; ++i) {
            a = (float)((double)i * Math.PI * 2.0 / (double)rays) + time * 0.08f;
            float len = size * (0.75f + 0.35f * (float)Math.abs(Math.sin((float)i * 2.3f + time * 0.9f)) + beat * 0.15f);
            float x2 = cx + (float)Math.cos(a) * len;
            float y2 = cy + (float)Math.sin(a) * len;
            Render2D.line(ctx, cx, cy, x2, y2, 1.4f, ColorUtil.withAlpha(i % 2 == 0 ? c[0] : c[1], 0.06f));
        }
        for (i = 0; i < 7; ++i) {
            a = time * 0.3f + (float)i * 0.9f;
            float rr = size * (0.85f + 0.2f * (float)Math.sin(a * 1.7f));
            float px = cx + (float)Math.cos(a) * rr;
            float py = cy + (float)Math.sin(a) * rr * 0.7f;
            Render2D.petal(ctx, px, py, 3.5f + (float)(i % 3), a * 60.0f, Paint.solid(ColorUtil.withAlpha(-18456, 0.5f)));
        }
    }

    private void drawRoundButton(GuiGraphics ctx, float[] b, int mx, int my, boolean primary, int[] c, boolean enabled) {
        boolean hover = Math.hypot((float)mx - b[0], (float)my - b[1]) <= (double)b[2];
        float r = b[2] * (hover ? 1.08f : 1.0f);
        if (primary) {
            Render2D.glow(ctx, b[0] - r, b[1] - r, r * 2.0f, r * 2.0f, r, 10.0f, ColorUtil.withAlpha(c[0], hover ? 0.6f : 0.4f));
            Render2D.circle(ctx, b[0], b[1], r, Paint.vertical(b[1] - r, b[1] + r, ColorUtil.brighten(c[0], 0.2f), c[1]));
        } else {
            Render2D.circle(ctx, b[0], b[1], r, ColorUtil.withAlpha(-1, hover ? 0.16f : 0.07f));
            Render2D.ring(ctx, b[0], b[1], r, 0.7f, ColorUtil.withAlpha(c[0], hover ? 0.8f : 0.35f));
        }
    }

    private void drawPrevNext(GuiGraphics ctx, float[] b, boolean next) {
        float s = 4.2f;
        float x = b[0];
        float y = b[1];
        int col = -1;
        float dir = next ? 1.0f : -1.0f;
        Render2D.triangle(ctx, x - dir * s, y - s, x - dir * s, y + s, x + dir * s * 0.6f, y, col);
        Render2D.roundRect(ctx, next ? x + s * 0.6f : x - s * 0.6f - 1.4f, y - s, 1.4f, s * 2.0f, 0.5f, col);
    }

    private void drawHistory(GuiGraphics ctx, NowPlayingModule mod, float x, float y, float w, float h, NowPlaying np, int[] c, Theme t) {
        Fonts.draw(ctx, "\u0418\u0441\u0442\u043e\u0440\u0438\u044f", x, y, 9.0f, t.text(), true);
        Render2D.roundRect(ctx, x, y + 13.0f, 18.0f, 2.0f, 1.0f, Paint.horizontal(x, x + 18.0f, c[0], c[1]));
        float iy = y + 24.0f;
        List<NowPlayingModule.HistoryEntry> list = mod.history();
        if (list.isEmpty()) {
            Fonts.draw(ctx, "\u0417\u0434\u0435\u0441\u044c \u043f\u043e\u044f\u0432\u044f\u0442\u0441\u044f \u0442\u0440\u0435\u043a\u0438,", x, iy, 6.6f, t.textMuted(), false);
            Fonts.draw(ctx, "\u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u0432\u044b \u0441\u043b\u0443\u0448\u0430\u043b\u0438", x, iy + 9.0f, 6.6f, t.textMuted(), false);
            return;
        }
        for (int i = 0; i < list.size() && iy + 30.0f < y + h; ++i) {
            float slide;
            NowPlayingModule.HistoryEntry e = list.get(i);
            boolean current = i == 0 && np.hasTrack() && e.title().equals(np.title());
            NowPlaying fake = new NowPlaying(true, e.app(), e.title(), e.artist(), "", false, 0L, 0L, 0L, e.cover(), false, false, false);
            int[] ec = current ? c : mod.colors(fake);
            float f = slide = current ? 6.0f : 0.0f;
            if (current) {
                Render2D.roundRect(ctx, x - 4.0f, iy - 3.0f, w + 4.0f, 32.0f, 8.0f, ColorUtil.withAlpha(ec[0], 0.1f));
                Render2D.outline(ctx, x - 4.0f, iy - 3.0f, w + 4.0f, 32.0f, 8.0f, 0.6f, ColorUtil.withAlpha(ec[0], 0.45f));
            }
            NowPlayingModule.drawCover(ctx, fake, ec, x + slide, iy, 26.0f, 6.0f);
            float tx = x + slide + 32.0f;
            Fonts.draw(ctx, Fonts.trim(e.title(), w - 36.0f - slide, 7.2f, true), tx, iy + 5.0f, 7.2f, current ? ColorUtil.brighten(ec[0], 0.25f) : t.text(), true);
            Fonts.draw(ctx, Fonts.trim(e.artist().isEmpty() ? e.app() : e.artist(), w - 36.0f - slide, 6.0f, false), tx, iy + 15.0f, 6.0f, t.textMuted(), false);
            iy += 36.0f;
        }
    }

    private void drawLyrics(GuiGraphics ctx, NowPlayingModule mod, NowPlaying np, float x, float y, float w, float h, int[] c, Theme t, int mx, int my) {
        float targetScroll;
        String msg;
        this.lineHits.clear();
        Fonts.draw(ctx, "\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438", x, y, 9.0f, t.text(), true);
        Render2D.roundRect(ctx, x, y + 13.0f, 18.0f, 2.0f, 1.0f, Paint.horizontal(x, x + 18.0f, c[0], c[1]));
        float top = y + 22.0f;
        float bottom = y + h;
        if (MediaBridge.estimated() && Lyrics.status() == Lyrics.Status.SYNCED) {
            Fonts.draw(ctx, "\u041f\u043e\u0437\u0438\u0446\u0438\u044f \u043f\u0440\u0438\u043c\u0435\u0440\u043d\u0430\u044f \u2014 \u043d\u0430\u0436\u043c\u0438\u0442\u0435 \u0441\u0442\u0440\u043e\u043a\u0443, \u043a\u043e\u0442\u043e\u0440\u0430\u044f \u0437\u0432\u0443\u0447\u0438\u0442 \u0441\u0435\u0439\u0447\u0430\u0441.", x, top - 2.0f, 5.8f, ColorUtil.withAlpha(c[0], 0.85f), false);
            Fonts.draw(ctx, "\u0422\u043e\u0447\u043d\u0430\u044f \u0441\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0430\u0446\u0438\u044f: \u043f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u0435 Spotify / \u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0430 \u0438\u043b\u0438 Chrome / Edge", x, top + 6.0f, 5.8f, ColorUtil.withAlpha(t.textMuted(), 0.9f), false);
            top += 18.0f;
        }
        Lyrics.Status st = Lyrics.status();
        switch (st) {
            case LOADING: {
                String string = "\u0418\u0449\u0435\u043c \u0442\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438\u2026";
                break;
            }
            case NOT_FOUND: {
                String string = "\u0422\u0435\u043a\u0441\u0442 \u0434\u043b\u044f \u044d\u0442\u043e\u0439 \u043f\u0435\u0441\u043d\u0438 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d";
                break;
            }
            case ERROR: {
                String string = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0442\u0435\u043a\u0441\u0442 (\u043d\u0435\u0442 \u0438\u043d\u0442\u0435\u0440\u043d\u0435\u0442\u0430?)";
                break;
            }
            case NONE: {
                String string;
                if (np.hasTrack()) {
                    string = "\u0417\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u0442\u0435\u043a\u0441\u0442\u043e\u0432 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430 \u0432 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430\u0445";
                    break;
                }
                string = "\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u0435 \u043c\u0443\u0437\u044b\u043a\u0443";
                break;
            }
            default: {
                String string = msg = null;
            }
        }
        if (msg != null) {
            Fonts.draw(ctx, msg, x, top + 6.0f, 7.0f, t.textMuted(), false);
            return;
        }
        List<Lyrics.Line> lines = Lyrics.lines();
        float lineH = 15.0f;
        ctx.method_44379((int)x - 6, (int)top, (int)(x + w + 6.0f), (int)bottom);
        if (st == Lyrics.Status.PLAIN) {
            float yy = top + 4.0f - this.plainScroll;
            for (Lyrics.Line l : lines) {
                for (String part : Fonts.wrap(l.text(), w, 7.5f, false)) {
                    if (yy > top - 12.0f && yy < bottom) {
                        Fonts.draw(ctx, part, x, yy, 7.5f, t.textDim(), false);
                    }
                    yy += 11.0f;
                }
            }
            ctx.method_44380();
            return;
        }
        long pos = np.position() + mod.offsetMs();
        int cur = Lyrics.indexAt(pos);
        float[] ys = new float[lines.size()];
        ArrayList<List<String>> wrapped = new ArrayList<List<String>>();
        float acc = 0.0f;
        for (int i = 0; i < lines.size(); ++i) {
            float size = i == cur ? 10.5f : 8.0f;
            List<String> parts = Fonts.wrap(lines.get(i).text().isBlank() ? "\u266a" : lines.get(i).text(), w, size, i == cur);
            wrapped.add(parts);
            ys[i] = acc;
            acc += (float)parts.size() * (size + 3.0f) + 6.0f;
        }
        float f = targetScroll = cur >= 0 ? ys[cur] - (bottom - top) * 0.35f : -(bottom - top) * 0.2f;
        if (this.lyricScroll < -9000.0f || this.lyricScroll == -1.0f) {
            this.lyricScroll = targetScroll;
        }
        this.lyricScroll += (targetScroll - this.lyricScroll) * (1.0f - (float)Math.exp(-FrameStats.delta() * 6.0f));
        for (int i = 0; i < lines.size(); ++i) {
            boolean clickable;
            float ly = top + ys[i] - this.lyricScroll;
            float size = i == cur ? 10.5f : 8.0f;
            float lh = (float)((List)wrapped.get(i)).size() * (size + 3.0f);
            if (ly + lh < top - 4.0f || ly > bottom + 4.0f) continue;
            float dist = Math.abs(i - cur);
            boolean hover = (float)mx >= x - 4.0f && (float)mx <= x + w && (float)my >= ly - 2.0f && (float)my <= ly + lh;
            boolean bl = clickable = np.canSeek() || MediaBridge.estimated();
            if (clickable) {
                this.lineHits.add(new float[]{x - 4.0f, ly - 2.0f, w + 4.0f, lh + 2.0f, lines.get(i).time()});
            }
            if (hover && clickable) {
                Render2D.roundRect(ctx, x - 5.0f, ly - 3.0f, w + 6.0f, lh + 2.0f, 5.0f, ColorUtil.withAlpha(-1, 0.06f));
            }
            float yy = ly;
            for (String part : (List)wrapped.get(i)) {
                if (i == cur) {
                    Fonts.drawGradient(ctx, part, x, yy, size, ColorUtil.brighten(c[0], 0.2f), ColorUtil.brighten(c[1], 0.2f), true);
                } else {
                    float a = i < cur ? 0.35f : Math.max(0.3f, 0.75f - dist * 0.08f);
                    Fonts.draw(ctx, part, x, yy, size, ColorUtil.withAlpha(t.text(), a), false);
                }
                yy += size + 3.0f;
            }
        }
        ctx.method_44380();
        Render2D.rect(ctx, x - 6.0f, top, w + 12.0f, 14.0f, Paint.vertical(top, top + 14.0f, ColorUtil.withAlpha(-16317427, 0.9f), 0));
        Render2D.rect(ctx, x - 6.0f, bottom - 18.0f, w + 12.0f, 18.0f, Paint.vertical(bottom - 18.0f, bottom, 0, ColorUtil.withAlpha(-16317427, 0.9f)));
    }

    private static boolean inside(double mx, double my, float[] r) {
        return mx >= (double)r[0] && mx <= (double)(r[0] + r[2]) && my >= (double)r[1] && my <= (double)(r[1] + r[3]);
    }

    private static boolean inCircle(double mx, double my, float[] b) {
        return b[2] > 0.0f && Math.hypot(mx - (double)b[0], my - (double)b[1]) <= (double)(b[2] + 2.0f);
    }

    public boolean mouseClicked(double mx, double my, int button) {
        NowPlaying np;
        if (button != 0) {
            return super.method_25402(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), false);
        }
        NowPlaying nowPlaying = np = PlayerScreen.module() != null ? PlayerScreen.module().track() : NowPlaying.NONE;
        if (!np.hasTrack()) {
            return super.method_25402(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), false);
        }
        if (PlayerScreen.inCircle(mx, my, this.playBtn)) {
            MediaBridge.togglePlay();
            return true;
        }
        if (PlayerScreen.inCircle(mx, my, this.prevBtn)) {
            MediaBridge.previous();
            return true;
        }
        if (PlayerScreen.inCircle(mx, my, this.nextBtn)) {
            MediaBridge.next();
            return true;
        }
        if (np.canSeek() && np.durMs() > 0L && PlayerScreen.inside(mx, my, this.bar)) {
            this.dragging = true;
            this.dragProgress = (float)Math.max(0.0, Math.min(1.0, (mx - (double)this.bar[0]) / (double)this.bar[2]));
            return true;
        }
        for (float[] hit : this.lineHits) {
            if (!PlayerScreen.inside(mx, my, hit)) continue;
            MediaBridge.seek((long)hit[4] - PlayerScreen.module().offsetMs() + 30L);
            return true;
        }
        return super.method_25402(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), false);
    }

    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (this.dragging) {
            this.dragProgress = (float)Math.max(0.0, Math.min(1.0, (mx - (double)this.bar[0]) / (double)this.bar[2]));
            return true;
        }
        return super.method_25403(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), dx, dy);
    }

    public boolean mouseReleased(double mx, double my, int button) {
        if (this.dragging) {
            this.dragging = false;
            NowPlaying np = PlayerScreen.module().track();
            MediaBridge.seek((long)(this.dragProgress * (float)np.durMs()));
            return true;
        }
        return super.method_25406(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)));
    }

    public boolean method_25401(double mx, double my, double h, double v) {
        if (Lyrics.status() == Lyrics.Status.PLAIN) {
            this.plainScroll = Math.max(0.0f, this.plainScroll - (float)v * 22.0f);
            return true;
        }
        return super.method_25401(mx, my, h, v);
    }

    public boolean keyPressed(int key, int scan, int mods) {
        NowPlaying np = PlayerScreen.module() != null ? PlayerScreen.module().track() : NowPlaying.NONE;
        boolean shift = (mods & 1) != 0;
        switch (key) {
            case 32: {
                MediaBridge.togglePlay();
                return true;
            }
            case 263: {
                if (shift) {
                    MediaBridge.previous();
                } else {
                    MediaBridge.seek(np.position() - 10000L);
                }
                return true;
            }
            case 262: {
                if (shift) {
                    MediaBridge.next();
                } else {
                    MediaBridge.seek(np.position() + 10000L);
                }
                return true;
            }
            case 74: {
                this.method_25419();
                return true;
            }
        }
        return super.method_25404(new KeyEvent(key, scan, mods));
    }

    public boolean method_25402(MouseButtonEvent click, boolean doubled) {
        return this.mouseClicked(click.comp_4798(), click.comp_4799(), click.method_74245());
    }

    public boolean method_25406(MouseButtonEvent click) {
        return this.mouseReleased(click.comp_4798(), click.comp_4799(), click.method_74245());
    }

    public boolean method_25403(MouseButtonEvent click, double dx, double dy) {
        return this.mouseDragged(click.comp_4798(), click.comp_4799(), click.method_74245(), dx, dy);
    }

    public boolean method_25404(KeyEvent input) {
        return this.keyPressed(input.comp_4795(), input.comp_4796(), input.comp_4797());
    }
}

