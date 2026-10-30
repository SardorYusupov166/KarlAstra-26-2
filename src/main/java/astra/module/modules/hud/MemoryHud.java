/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MemoryHud
extends HudModule {
    public MemoryHud() {
        super("memory", "\u041f\u0430\u043c\u044f\u0442\u044c", "\u0417\u0430\u043d\u044f\u0442\u0430\u044f \u0438 \u0432\u044b\u0434\u0435\u043b\u0435\u043d\u043d\u0430\u044f \u043f\u0430\u043c\u044f\u0442\u044c Java.", Category.PERFORMANCE, Icon.CHIP, 0.12f, 0.72f);
    }

    public static long usedBytes() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static long maxBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    public static String gb(long bytes) {
        return String.format(Locale.ROOT, "%.1f GB", (double)bytes / 1024.0 / 1024.0 / 1024.0);
    }

    @Override
    public float width() {
        return 110.0f;
    }

    @Override
    public float height() {
        return 28.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        MemoryHud.drawBar(ctx, 6.0f, 5.0f, w - 12.0f, this.accentColor(), MemoryHud.theme(), 6.5f);
    }

    public static void drawBar(GuiGraphicsExtractor ctx, float x, float y, float w, int accent, Theme t, float textSize) {
        long used = MemoryHud.usedBytes();
        long max = MemoryHud.maxBytes();
        float pct = max > 0L ? (float)used / (float)max : 0.0f;
        Fonts.draw(ctx, MemoryHud.gb(used) + " / " + MemoryHud.gb(max), x, y, textSize, t.accent3(), true);
        Fonts.drawRight(ctx, Math.round(pct * 100.0f) + "%", x + w, y, textSize, t.text(), true);
        float by = y + textSize + 4.0f;
        Render2D.roundRect(ctx, x, by, w, 5.0f, 2.5f, ColorUtil.withAlpha(t.track(), 0.9f));
        int warn = pct > 0.85f ? -770210 : t.accent2();
        Render2D.roundRect(ctx, x, by, Math.max(5.0f, w * pct), 5.0f, 2.5f, Paint.horizontal(x, x + w, accent, warn));
    }
}

