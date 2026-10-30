/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fStack
 *  org.joml.Matrix3x2fc
 *  org.joml.Vector2f
 */
package astra.gui.style;

import astra.AstraClient;
import astra.config.ConfigManager;
import astra.gui.UiContext;
import astra.render.Render2D;
import astra.theme.Theme;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;

public final class GuiStyle {
    public static final Map<String, Element> ELEMENTS = new LinkedHashMap<String, Element>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static Map<String, Override> overrides = new LinkedHashMap<String, Override>();
    private static boolean loaded;
    public static boolean recording;
    public static final List<Hit> HITS;
    private static final Deque<Frame> STACK;

    private GuiStyle() {
    }

    private static void block(String id, String label, boolean image) {
        ELEMENTS.put(id, new Element(id, label, true, image));
    }

    private static void style(String id, String label) {
        ELEMENTS.put(id, new Element(id, label, false, false));
    }

    private static Path file() {
        return ConfigManager.root().resolve("gui_style.json");
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            Map m;
            if (Files.isRegularFile(GuiStyle.file(), new LinkOption[0]) && (m = (Map)GSON.fromJson(Files.readString(GuiStyle.file(), StandardCharsets.UTF_8), new TypeToken<LinkedHashMap<String, Override>>(){}.getType())) != null) {
                overrides = m;
            }
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c gui_style.json", (Throwable)e);
        }
    }

    public static void save() {
        GuiStyle.ensureLoaded();
        overrides.values().removeIf(Override::isDefault);
        try {
            Files.createDirectories(GuiStyle.file().getParent(), new FileAttribute[0]);
            Files.writeString(GuiStyle.file(), (CharSequence)GSON.toJson(overrides), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c gui_style.json", (Throwable)e);
        }
    }

    public static String toJson() {
        GuiStyle.ensureLoaded();
        return GSON.toJson(overrides);
    }

    public static void fromJson(String json) {
        Map m = (Map)GSON.fromJson(json, new TypeToken<LinkedHashMap<String, Override>>(){}.getType());
        overrides = m != null ? m : new LinkedHashMap();
        loaded = true;
    }

    public static Override get(String id) {
        GuiStyle.ensureLoaded();
        return overrides.get(id);
    }

    public static Override edit(String id) {
        GuiStyle.ensureLoaded();
        return overrides.computeIfAbsent(id, k -> new Override());
    }

    public static void reset(String id) {
        GuiStyle.ensureLoaded();
        overrides.remove(id);
    }

    public static Theme theme(String id, Theme t) {
        Override o = GuiStyle.get(id);
        if (o == null || !o.customColor) {
            return t;
        }
        return new Theme(t.name(), t.bg(), t.bg2(), t.card(), t.cardHover(), o.color, o.color2, t.accent3(), t.text(), t.textDim(), t.textMuted(), t.track());
    }

    public static float radius(String id, float r) {
        Override o = GuiStyle.get(id);
        return o != null && o.radius >= 0.0f ? o.radius : r;
    }

    public static String image(String id) {
        Override o = GuiStyle.get(id);
        return o == null || o.image.isEmpty() ? null : o.image;
    }

    public static boolean begin(GuiGraphics ctx, UiContext ui, String id, float x, float y, float w, float h) {
        Override o = GuiStyle.get(id);
        if (o != null && o.hidden && !recording) {
            return false;
        }
        boolean move = o != null && (o.dx != 0.0f || o.dy != 0.0f || o.scale != 100.0f);
        Matrix3x2fStack m = ctx.method_51448();
        if (move) {
            float cx = x + w / 2.0f;
            float cy = y + h / 2.0f;
            float s = o.scale / 100.0f;
            m.pushMatrix();
            m.translate(cx + o.dx, cy + o.dy);
            m.scale(s, s);
            m.translate(-cx, -cy);
        }
        GuiStyle.record(ctx, id, x, y, w, h);
        STACK.push(new Frame(Render2D.alpha, ui != null ? ui.mx : 0.0f, ui != null ? ui.my : 0.0f, move));
        if (o != null) {
            float a = o.opacity / 100.0f;
            if (o.hidden) {
                a *= 0.25f;
            }
            Render2D.alpha *= a;
            if (ui != null && move) {
                float[] p = GuiStyle.unmap(id, x, y, w, h, ui.mx, ui.my);
                ui.mx = p[0];
                ui.my = p[1];
            }
        }
        return true;
    }

    public static void end(GuiGraphics ctx, UiContext ui) {
        Frame f = STACK.pop();
        Render2D.alpha = f.alpha();
        if (ui != null) {
            ui.mx = f.mx();
            ui.my = f.my();
        }
        if (f.pushed()) {
            ctx.method_51448().popMatrix();
        }
    }

    public static float[] unmap(String id, float x, float y, float w, float h, double mx, double my) {
        Override o = GuiStyle.get(id);
        if (o == null || o.dx == 0.0f && o.dy == 0.0f && o.scale == 100.0f) {
            return new float[]{(float)mx, (float)my};
        }
        float cx = x + w / 2.0f;
        float cy = y + h / 2.0f;
        float s = o.scale / 100.0f;
        return new float[]{(float)((mx - (double)cx - (double)o.dx) / (double)s + (double)cx), (float)((my - (double)cy - (double)o.dy) / (double)s + (double)cy)};
    }

    public static boolean hidden(String id) {
        Override o = GuiStyle.get(id);
        return o != null && o.hidden && !recording;
    }

    public static void record(GuiGraphics ctx, String id, float x, float y, float w, float h) {
        if (!recording) {
            return;
        }
        Matrix3x2f m = new Matrix3x2f((Matrix3x2fc)ctx.method_51448());
        Vector2f a = m.transformPosition(x, y, new Vector2f());
        Vector2f b = m.transformPosition(x + w, y + h, new Vector2f());
        HITS.add(new Hit(id, Math.min(a.x, b.x), Math.min(a.y, b.y), Math.max(a.x, b.x), Math.max(a.y, b.y)));
    }

    public static float styleAlpha(String id) {
        float prev = Render2D.alpha;
        Override o = GuiStyle.get(id);
        if (o != null) {
            Render2D.alpha *= o.opacity / 100.0f;
        }
        return prev;
    }

    static {
        HITS = new ArrayList<Hit>();
        STACK = new ArrayDeque<Frame>();
        GuiStyle.block("logo", "\u041b\u043e\u0433\u043e\u0442\u0438\u043f", true);
        GuiStyle.block("nav", "\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438 (\u0431\u043e\u043a\u043e\u0432\u043e\u0435 \u043c\u0435\u043d\u044e)", false);
        GuiStyle.block("config", "\u0411\u043b\u043e\u043a \u00ab\u041a\u043e\u043d\u0444\u0438\u0433\u00bb", false);
        GuiStyle.block("header", "\u0417\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u044b", false);
        GuiStyle.block("close", "\u041a\u043d\u043e\u043f\u043a\u0430 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u044f", false);
        GuiStyle.block("content", "\u0421\u043e\u0434\u0435\u0440\u0436\u0438\u043c\u043e\u0435 \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u044b", false);
        GuiStyle.block("window.bg", "\u0424\u043e\u043d \u043e\u043a\u043d\u0430", true);
        GuiStyle.block("window.frame", "\u0420\u0430\u043c\u043a\u0430 \u0438 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043e\u043a\u043d\u0430", false);
        GuiStyle.style("style.button", "\u0412\u0441\u0435 \u043a\u043d\u043e\u043f\u043a\u0438");
        GuiStyle.style("style.card", "\u0412\u0441\u0435 \u043a\u0430\u0440\u0442\u043e\u0447\u043a\u0438");
        GuiStyle.style("style.toggle", "\u0412\u0441\u0435 \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0430\u0442\u0435\u043b\u0438");
        GuiStyle.style("style.slider", "\u0412\u0441\u0435 \u043f\u043e\u043b\u0437\u0443\u043d\u043a\u0438");
        GuiStyle.style("style.dropdown", "\u0412\u0441\u0435 \u0432\u044b\u043f\u0430\u0434\u0430\u044e\u0449\u0438\u0435 \u0441\u043f\u0438\u0441\u043a\u0438");
        GuiStyle.style("style.badge", "\u0412\u0441\u0435 \u0437\u043d\u0430\u0447\u043a\u0438 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0439");
    }

    public record Element(String id, String label, boolean movable, boolean image) {
    }

    public static final class Override {
        public float dx;
        public float dy;
        public float scale = 100.0f;
        public float opacity = 100.0f;
        public boolean hidden;
        public boolean customColor;
        public int color = -5745153;
        public int color2 = -45608;
        public float radius = -1.0f;
        public String image = "";

        public boolean isDefault() {
            return this.dx == 0.0f && this.dy == 0.0f && this.scale == 100.0f && this.opacity == 100.0f && !this.hidden && !this.customColor && this.radius < 0.0f && this.image.isEmpty();
        }
    }

    private record Frame(float alpha, float mx, float my, boolean pushed) {
    }

    public record Hit(String id, float x0, float y0, float x1, float y1) {
        public boolean contains(double x, double y) {
            return x >= (double)this.x0 && x <= (double)this.x1 && y >= (double)this.y0 && y <= (double)this.y1;
        }

        public float area() {
            return (this.x1 - this.x0) * (this.y1 - this.y0);
        }
    }
}

