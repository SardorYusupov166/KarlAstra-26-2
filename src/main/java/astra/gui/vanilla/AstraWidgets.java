/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  org.joml.Matrix3x2fStack
 */
package astra.gui.vanilla;

import astra.gui.style.GuiStyle;
import astra.gui.style.NativeScreenLayouts;
import astra.module.ClientSettings;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.joml.Matrix3x2fStack;

public final class AstraWidgets {
    private static final Map<Object, float[]> HOVER = new WeakHashMap<Object, float[]>();
    private static long lastFrame;
    private static float dt;

    private AstraWidgets() {
    }

    public static boolean enabled() {
        ClientSettings cs = ClientSettings.get();
        return cs == null || (Boolean)cs.gameMenuStyle.get() != false;
    }

    private static float hover(Object widget, boolean hovered) {
        long now = System.nanoTime();
        if (now - lastFrame > 1000000L) {
            dt = lastFrame == 0L ? 0.016f : Math.min(0.1f, (float)(now - lastFrame) / 1.0E9f);
            lastFrame = now;
        }
        float[] h = HOVER.computeIfAbsent(widget, k -> new float[1]);
        h[0] = h[0] + ((float)(hovered ? 1 : 0) - h[0]) * Math.min(1.0f, dt * 14.0f);
        return h[0];
    }

    private static Component bold(Component t) {
        return t.copy().withStyle(Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Fonts.BOLD)));
    }

    private static void label(GuiGraphicsExtractor ctx, Component msg, float x, float y, float w, float h, int color) {
        Font tr = Minecraft.getInstance().font;
        Component t = AstraWidgets.bold(msg);
        float tw = tr.width((FormattedText)t);
        float s = tw > w - 8.0f ? Math.max(0.6f, (w - 8.0f) / tw) : 1.0f;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(x + w / 2.0f, y + h / 2.0f);
        m.scale(s, s);
        ctx.text(tr, t, (int)(-tw / 2.0f), -4, color, true);
        m.popMatrix();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void button(GuiGraphicsExtractor ctx, Object widget, int x, int y, int w, int h, Component message, boolean active, boolean hovered, float alpha) {
        GuiStyle.record(ctx, "style.button", x, y, w, h);
        Theme t = NativeScreenLayouts.theme(widget, GuiStyle.theme("style.button", ThemeManager.get()));
        float prev = Render2D.alpha;
        Render2D.alpha = prev * (alpha *= NativeScreenLayouts.opacity(widget)) * (GuiStyle.get("style.button") != null ? GuiStyle.get((String)"style.button").opacity / 100.0f : 1.0f);
        try {
            float hv = active ? AstraWidgets.hover(widget, hovered) : 0.0f;
            float r = NativeScreenLayouts.radius(widget, GuiStyle.radius("style.button", Math.min(6.0f, (float)h / 3.2f)));
            if (active && hv > 0.01f) {
                Render2D.glow(ctx, x, y, w, h, r, 7.0f, ColorUtil.withAlpha(t.accent(), 0.4f * hv));
            }
            int top = active ? ColorUtil.lerp(ColorUtil.withAlpha(t.bg2(), 0.88f), ColorUtil.withAlpha(ColorUtil.lerp(t.bg2(), t.accent(), 0.45f), 0.95f), hv) : ColorUtil.withAlpha(t.bg(), 0.7f);
            int bottom = active ? ColorUtil.lerp(ColorUtil.withAlpha(t.bg(), 0.9f), ColorUtil.withAlpha(ColorUtil.lerp(t.bg(), t.accent2(), 0.35f), 0.95f), hv) : ColorUtil.withAlpha(t.bg(), 0.7f);
            Render2D.roundRect(ctx, (float)x, (float)y, (float)w, (float)h, r, Paint.vertical(y, y + h, top, bottom));
            Render2D.outline(ctx, (float)x, (float)y, (float)w, (float)h, r, 0.9f, active ? Paint.horizontal(x, x + w, ColorUtil.withAlpha(t.accent(), 0.45f + 0.5f * hv), ColorUtil.withAlpha(t.accent2(), 0.35f + 0.5f * hv)) : Paint.solid(ColorUtil.withAlpha(t.textMuted(), 0.3f)));
            Render2D.rect(ctx, (float)x + r, (float)y + 0.8f, (float)w - r * 2.0f, 0.6f, Paint.horizontal((float)x + r, (float)(x + w) - r, 0xFFFFFF, ColorUtil.withAlpha(0xFFFFFF, 0.12f + 0.1f * hv)));
            int a = Math.round(255.0f * Math.min(1.0f, Render2D.alpha)) << 24;
            AstraWidgets.label(ctx, message, x, y, w, h, (active ? ColorUtil.lerp(0xE6E6E6, 0xFFFFFF, hv) & 0xFFFFFF : 9077401) | a);
        }
        finally {
            Render2D.alpha = prev;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void slider(GuiGraphicsExtractor ctx, Object widget, int x, int y, int w, int h, Component message, double value, boolean active, boolean hovered, float alpha) {
        GuiStyle.record(ctx, "style.slider", x, y, w, h);
        Theme t = GuiStyle.theme("style.slider", ThemeManager.get());
        float prev = Render2D.alpha;
        Render2D.alpha = prev * alpha;
        try {
            float hv = active ? AstraWidgets.hover(widget, hovered) : 0.0f;
            float r = Math.min(6.0f, (float)h / 3.2f);
            Render2D.roundRect(ctx, (float)x, (float)y, (float)w, (float)h, r, Paint.vertical(y, y + h, ColorUtil.withAlpha(t.bg2(), 0.88f), ColorUtil.withAlpha(t.bg(), 0.9f)));
            float fw = Math.max(r * 2.0f, (float)((double)w * Math.max(0.0, Math.min(1.0, value))));
            Render2D.roundRect(ctx, (float)x, (float)y, fw, (float)h, r, Paint.horizontal(x, x + w, ColorUtil.withAlpha(t.accent(), 0.55f + 0.2f * hv), ColorUtil.withAlpha(t.accent2(), 0.55f + 0.2f * hv)));
            Render2D.outline(ctx, (float)x, (float)y, (float)w, (float)h, r, 0.9f, ColorUtil.withAlpha(t.accent(), 0.4f + 0.45f * hv));
            float kx = (float)x + fw;
            Render2D.glow(ctx, kx - 3.0f, y + 2, 6.0f, h - 4, 3.0f, 4.0f, ColorUtil.withAlpha(t.accent2(), 0.5f));
            Render2D.roundRect(ctx, kx - 2.0f, (float)(y + 2), 4.0f, (float)(h - 4), 2.0f, ColorUtil.withAlpha(ColorUtil.brighten(t.accent2(), 0.35f), 0.85f));
            int a = Math.round(255.0f * Math.min(1.0f, Render2D.alpha)) << 24;
            AstraWidgets.label(ctx, message, x, y, w, h, (active ? 0xFFFFFF : 9077401) | a);
        }
        finally {
            Render2D.alpha = prev;
        }
    }

    static {
        dt = 0.016f;
    }
}

