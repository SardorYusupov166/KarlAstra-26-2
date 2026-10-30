/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix3x2fStack
 */
package astra.render;

import astra.render.ColorUtil;
import astra.render.Render2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public final class Fonts {
    public static final Identifier REGULAR = Identifier.method_60655((String)"astra", (String)"regular");
    public static final Identifier BOLD = Identifier.method_60655((String)"astra", (String)"bold");
    private static final Style REGULAR_STYLE = Style.field_24360.method_27704((FontDescription)new FontDescription.Resource(REGULAR));
    private static final Style BOLD_STYLE = Style.field_24360.method_27704((FontDescription)new FontDescription.Resource(BOLD));
    private static final float BASE = 9.0f;
    private static final int CACHE_LIMIT = 4096;
    private static final HashMap<String, Component> TEXT_REGULAR = new HashMap();
    private static final HashMap<String, Component> TEXT_BOLD = new HashMap();
    private static final HashMap<String, Integer> WIDTH_REGULAR = new HashMap();
    private static final HashMap<String, Integer> WIDTH_BOLD = new HashMap();

    private Fonts() {
    }

    private static Font tr() {
        return Minecraft.method_1551().field_1772;
    }

    public static void clearCache() {
        TEXT_REGULAR.clear();
        TEXT_BOLD.clear();
        WIDTH_REGULAR.clear();
        WIDTH_BOLD.clear();
    }

    public static Component text(String s, boolean bold) {
        HashMap<String, Component> cache = bold ? TEXT_BOLD : TEXT_REGULAR;
        Component t = cache.get(s);
        if (t == null) {
            if (cache.size() >= 4096) {
                cache.clear();
            }
            MutableComponent m = Component.method_43470((String)s);
            t = m.method_10862(bold ? BOLD_STYLE : REGULAR_STYLE);
            cache.put(s, t);
        }
        return t;
    }

    public static float width(String s, float size, boolean bold) {
        HashMap<String, Integer> cache = bold ? WIDTH_BOLD : WIDTH_REGULAR;
        Integer w = cache.get(s);
        if (w == null) {
            if (cache.size() >= 4096) {
                cache.clear();
            }
            w = Fonts.tr().method_27525((FormattedText)Fonts.text(s, bold));
            cache.put(s, w);
        }
        return (float)w.intValue() * size / 9.0f;
    }

    public static float width(String s, float size) {
        return Fonts.width(s, size, false);
    }

    public static void draw(GuiGraphics ctx, String s, float x, float y, float size, int color, boolean bold) {
        Fonts.drawText(ctx, Fonts.text(s, bold), x, y, size, color, false);
    }

    public static void draw(GuiGraphics ctx, String s, float x, float y, float size, int color) {
        Fonts.draw(ctx, s, x, y, size, color, false);
    }

    public static void drawShadow(GuiGraphics ctx, String s, float x, float y, float size, int color, boolean bold) {
        Fonts.drawText(ctx, Fonts.text(s, bold), x, y, size, color, true);
    }

    public static void drawCentered(GuiGraphics ctx, String s, float cx, float y, float size, int color, boolean bold) {
        Fonts.draw(ctx, s, cx - Fonts.width(s, size, bold) / 2.0f, y, size, color, bold);
    }

    public static void drawRight(GuiGraphics ctx, String s, float right, float y, float size, int color, boolean bold) {
        Fonts.draw(ctx, s, right - Fonts.width(s, size, bold), y, size, color, bold);
    }

    public static void drawText(GuiGraphics ctx, Component text, float x, float y, float size, int color, boolean shadow) {
        int c = ColorUtil.mulAlpha(color, Render2D.alpha);
        if (ColorUtil.alpha(c) < 5) {
            return;
        }
        Matrix3x2fStack m = ctx.method_51448();
        m.pushMatrix();
        m.translate(x, y);
        float s = size / 9.0f;
        m.scale(s, s);
        ctx.method_51439(Fonts.tr(), text, 0, 0, c, shadow);
        m.popMatrix();
    }

    public static void drawGradient(GuiGraphics ctx, String s, float x, float y, float size, int from, int to, boolean bold) {
        int cp;
        float total = Math.max(1.0f, Fonts.width(s, size, bold));
        float cx = x;
        for (int i = 0; i < s.length(); i += Character.charCount(cp)) {
            cp = s.codePointAt(i);
            String ch = new String(Character.toChars(cp));
            float w = Fonts.width(ch, size, bold);
            int color = ColorUtil.lerp(from, to, (cx - x + w / 2.0f) / total);
            Fonts.draw(ctx, ch, cx, y, size, color, bold);
            cx += w;
        }
    }

    public static String trim(String s, float maxWidth, float size, boolean bold) {
        if (Fonts.width(s, size, bold) <= maxWidth) {
            return s;
        }
        String dots = "\u2026";
        float dw = Fonts.width(dots, size, bold);
        int lo = 0;
        int hi = s.length();
        while (lo < hi) {
            int mid = lo + hi + 1 >>> 1;
            Font font = Fonts.tr();
            MutableComponent mutableComponent = Component.method_43470((String)s.substring(0, mid));
            Style style = bold ? BOLD_STYLE : REGULAR_STYLE;
            if ((float)font.method_27525((FormattedText)mutableComponent.method_10862(style)) * size / 9.0f + dw <= maxWidth) {
                lo = mid;
                continue;
            }
            hi = mid - 1;
        }
        return s.substring(0, lo) + dots;
    }

    public static List<String> wrap(String s, float maxWidth, float size, boolean bold) {
        ArrayList<String> lines = new ArrayList<String>();
        StringBuilder line = new StringBuilder();
        for (String word : s.split(" ")) {
            String probe;
            String string = probe = line.isEmpty() ? word : String.valueOf(line) + " " + word;
            if (Fonts.width(probe, size, bold) > maxWidth && !line.isEmpty()) {
                lines.add(line.toString());
                line = new StringBuilder(word);
                continue;
            }
            line = new StringBuilder(probe);
        }
        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        return lines;
    }
}

