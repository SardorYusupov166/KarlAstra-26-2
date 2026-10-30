/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
 *  net.fabricmc.fabric.api.client.screen.v1.Screens
 *  net.fabricmc.fabric.api.event.Event
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.client.input.MouseButtonInfo
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.ComponentContents
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.contents.TranslatableContents
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix3x2fStack
 */
package astra.gui.style;

import astra.builder.CanvasBoard;
import astra.config.ConfigManager;
import astra.gui.style.NativeLayoutEditor;
import astra.mixin.ClickableWidgetAccessor;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public final class NativeScreenLayouts {
    private static final Gson GSON = new Gson();
    private static Map<String, Layout> layouts = new LinkedHashMap<String, Layout>();
    private static final Map<Screen, List<Entry>> entries = new WeakHashMap<Screen, List<Entry>>();
    private static final Map<Object, Screen> owners = new WeakHashMap<Object, Screen>();
    private static final Map<Screen, Object> clickHooks = new WeakHashMap<Screen, Object>();
    private static final Map<Screen, float[]> logos = new WeakHashMap<Screen, float[]>();
    private static boolean initialized;
    private static final String EDITOR_LABEL = "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f";
    public static Screen preview;
    private static final Map<Object, int[]> expected;
    public static final Map<Screen, Map<String, float[]>> extras;

    public static String key(Screen s) {
        return s instanceof TitleScreen ? "title" : (s instanceof PauseScreen ? "pause" : (s instanceof JoinMultiplayerScreen ? "multiplayer" : ""));
    }

    public static String title(Screen s) {
        return switch (NativeScreenLayouts.key(s)) {
            case "title" -> "\u0413\u043b\u0430\u0432\u043d\u043e\u0435 \u043c\u0435\u043d\u044e";
            case "pause" -> "\u041c\u0435\u043d\u044e \u043f\u0430\u0443\u0437\u044b";
            default -> "\u0421\u043f\u0438\u0441\u043e\u043a \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u0432";
        };
    }

    public static Layout layout(Screen s) {
        return layouts.computeIfAbsent(NativeScreenLayouts.key(s), k -> new Layout());
    }

    public static Item item(Screen s, String id) {
        return NativeScreenLayouts.layout((Screen)s).items.computeIfAbsent(id, k -> new Item());
    }

    public static List<Entry> entries(Screen s) {
        return entries.getOrDefault(s, List.of());
    }

    public static void save() {
        JsonObject o = GSON.toJsonTree(layouts).getAsJsonObject();
        ConfigManager.writeJson(ConfigManager.root().resolve("native-layouts.json"), o);
    }

    public static String snapshot(Screen s) {
        return GSON.toJson((Object)NativeScreenLayouts.layout(s));
    }

    public static void restore(Screen s, String json) {
        layouts.put(NativeScreenLayouts.key(s), (Layout)GSON.fromJson(json, Layout.class));
        NativeScreenLayouts.apply(s);
        NativeScreenLayouts.save();
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        JsonObject stored = ConfigManager.readJson(ConfigManager.root().resolve("native-layouts.json"));
        if (stored != null) {
            for (Map.Entry e : stored.entrySet()) {
                try {
                    layouts.put((String)e.getKey(), (Layout)GSON.fromJson((JsonElement)e.getValue(), Layout.class));
                }
                catch (Exception exception) {}
            }
        }
        for (Layout layout : layouts.values()) {
            if (layout == null || layout.items == null) continue;
            layout.items.keySet().removeIf(id -> id.startsWith("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f#"));
            if (layout.schemaVersion < 2 && layout.backgroundOpacity == 60.0f) {
                layout.backgroundOpacity = 100.0f;
            }
            layout.schemaVersion = 2;
        }
        Identifier late = Identifier.fromNamespaceAndPath((String)"astra", (String)"native_layouts");
        ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, late);
        ScreenEvents.AFTER_INIT.register(late, (mc, s, w, h) -> {
            if (NativeScreenLayouts.key(s).isEmpty()) {
                return;
            }
            Screens.getButtons((Screen)s).removeIf(widget -> EDITOR_LABEL.equals(widget.getMessage().getString()));
            IdentityHashMap<AbstractWidget, Base> previous = new IdentityHashMap<AbstractWidget, Base>();
            for (Entry entry : NativeScreenLayouts.entries(s)) {
                previous.put(entry.widget(), entry.base());
            }
            owners.entrySet().removeIf(e -> e.getValue() == s);
            ArrayList<Entry> list = new ArrayList<Entry>();
            HashMap<String, Integer> counts = new HashMap<String, Integer>();
            for (AbstractWidget widget2 : Screens.getButtons((Screen)s)) {
                String name;
                if (!(widget2 instanceof AbstractButton)) continue;
                ComponentContents componentContents = widget2.getMessage().getContents();
                if (componentContents instanceof TranslatableContents) {
                    TranslatableContents t = (TranslatableContents)componentContents;
                    v0 = t.getKey();
                } else {
                    v0 = name = widget2.getMessage().getString();
                }
                if (name.isBlank()) {
                    name = widget2.getClass().getName();
                }
                String id = name + "#" + String.valueOf(counts.merge(name, 1, Integer::sum));
                list.add(new Entry(id, widget2, previous.getOrDefault(widget2, new Base(widget2.getX(), widget2.getY(), widget2.getWidth(), widget2.getHeight(), widget2.visible))));
                owners.put(widget2, s);
            }
            entries.keySet().removeIf(old -> old != s && NativeScreenLayouts.key(old).equals(NativeScreenLayouts.key(s)));
            owners.entrySet().removeIf(e -> e.getValue() != s && NativeScreenLayouts.key((Screen)e.getValue()).equals(NativeScreenLayouts.key(s)));
            entries.put(s, list);
            NativeScreenLayouts.apply(s);
            int[] frames = new int[]{0};
            ScreenEvents.beforeRender((Screen)s).register((screen, ctx, mx, my, delta) -> {
                int n = frames[0];
                frames[0] = n + 1;
                if (n < 3) {
                    NativeScreenLayouts.adoptDrift(screen);
                }
            });
            Event clickEvent = ScreenMouseEvents.allowMouseClick((Screen)s);
            if (clickHooks.get(s) != clickEvent) {
                clickHooks.put(s, clickEvent);
                clickEvent.register((screen, click) -> !NativeScreenLayouts.routeMovedClick(screen, click.x(), click.y(), click.button()));
            }
            Screens.getButtons((Screen)s).add(Button.builder((Component)Component.literal((String)EDITOR_LABEL), b -> mc.setScreen((Screen)new NativeLayoutEditor(s))).bounds(s instanceof JoinMultiplayerScreen ? Math.max(6, w - 118) : 6, s instanceof TitleScreen ? h - 42 : (s instanceof JoinMultiplayerScreen ? 6 : h - 24), s instanceof JoinMultiplayerScreen ? 112 : 128, 18).build());
        });
    }

    private static void adoptDrift(Screen s) {
        if (preview == s) {
            return;
        }
        List<Entry> list = NativeScreenLayouts.entries(s);
        boolean moved = false;
        for (int i = 0; i < list.size(); ++i) {
            Entry e = list.get(i);
            AbstractWidget w = e.widget();
            int[] ex = expected.get(w);
            if (ex == null) continue;
            int ddx = w.getX() - ex[0];
            int ddy = w.getY() - ex[1];
            if (ddx == 0 && ddy == 0) continue;
            Base b = e.base();
            list.set(i, new Entry(e.id(), w, new Base(b.x() + ddx, b.y() + ddy, b.w(), b.h(), b.visible())));
            moved = true;
        }
        if (moved) {
            NativeScreenLayouts.apply(s);
        }
    }

    public static void apply(Screen s) {
        for (Entry e : NativeScreenLayouts.entries(s)) {
            Item o = NativeScreenLayouts.item(s, e.id());
            Base b = e.base();
            AbstractWidget w = e.widget();
            o.scale = NativeScreenLayouts.clamp(o.scale, 25.0f, 250.0f);
            o.opacity = NativeScreenLayouts.clamp(o.opacity, 0.0f, 100.0f);
            o.dx = NativeScreenLayouts.clamp(o.dx, -1.0f, 1.0f);
            o.dy = NativeScreenLayouts.clamp(o.dy, -1.0f, 1.0f);
            w.setSize(Math.max(8, Math.round((float)b.w() * o.scale / 100.0f)), Math.max(8, Math.round((float)b.h() * o.scale / 100.0f)));
            w.setSize(Math.min(s.width, w.getWidth()), Math.min(s.height, w.getHeight()));
            w.setX(Math.max(0, Math.min(s.width - w.getWidth(), Math.round((float)b.x() + o.dx * (float)s.width))));
            w.setY(Math.max(0, Math.min(s.height - w.getHeight(), Math.round((float)b.y() + o.dy * (float)s.height))));
            o.dx = (float)(w.getX() - b.x()) / (float)Math.max(1, s.width);
            o.dy = (float)(w.getY() - b.y()) / (float)Math.max(1, s.height);
            w.visible = b.visible() && !o.hidden;
            expected.put(w, new int[]{w.getX(), w.getY()});
        }
    }

    public static boolean routeMovedClick(Screen s, double x, double y, int button) {
        if (button != 0 || Minecraft.getInstance().screen != s) {
            return false;
        }
        List<Entry> list = NativeScreenLayouts.entries(s);
        for (int i = list.size() - 1; i >= 0; --i) {
            Entry e = list.get(i);
            Item o = NativeScreenLayouts.item(s, e.id());
            AbstractWidget widget = e.widget();
            if (o.dx == 0.0f && o.dy == 0.0f && o.scale == 100.0f || !widget.active || !widget.visible || !widget.isMouseOver(x, y)) continue;
            s.setFocused((GuiEventListener)widget);
            return widget.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0)), false);
        }
        return false;
    }

    public static float clamp(float n, float min, float max) {
        return Float.isFinite(n) ? Math.max(min, Math.min(max, n)) : min;
    }

    private static Item widgetItem(Object w) {
        Screen s = owners.get(w);
        if (s == null) {
            return null;
        }
        for (Entry e : NativeScreenLayouts.entries(s)) {
            if (e.widget() != w) continue;
            return NativeScreenLayouts.item(s, e.id());
        }
        return null;
    }

    public static Theme theme(Object w, Theme t) {
        Item o = NativeScreenLayouts.widgetItem(w);
        if (o == null || o.accent.isBlank()) {
            return t;
        }
        try {
            return t.withAccent(0xFF000000 | Integer.parseInt(o.accent.replace("#", ""), 16));
        }
        catch (Exception ex) {
            return t;
        }
    }

    public static float opacity(Object w) {
        Item o = NativeScreenLayouts.widgetItem(w);
        return o == null ? 1.0f : NativeScreenLayouts.clamp(o.opacity, 0.0f, 100.0f) / 100.0f;
    }

    public static float radius(Object w, float fallback) {
        Item o = NativeScreenLayouts.widgetItem(w);
        return o != null && o.radius >= 0.0f ? o.radius : fallback;
    }

    public static void logo(Screen s, GuiGraphics ctx, float x, float y, float w, float alpha) {
        if (s == null || NativeScreenLayouts.key(s).isEmpty()) {
            return;
        }
        Item o = NativeScreenLayouts.item(s, "@logo");
        float scale = NativeScreenLayouts.clamp(o.scale, 25.0f, 250.0f) / 100.0f;
        CanvasBoard.Canvas custom = NativeScreenLayouts.layout((Screen)s).logo;
        float h = custom == null ? w * 280.0f / 720.0f : (float)((double)(w *= scale) * custom.height / custom.width);
        logos.put(s, new float[]{x += o.dx * (float)s.width, y += o.dy * (float)s.height, w, h});
        if (o.hidden) {
            return;
        }
        if (custom != null) {
            Identifier tex = CanvasBoard.texture(custom);
            if (tex != null) {
                Render2D.texture(ctx, tex, x, y, w, h, 0.0f, 0.0f, 1.0f, 1.0f, ColorUtil.withAlpha(-1, alpha * o.opacity / 100.0f));
            }
            return;
        }
        Theme t = ThemeManager.get();
        try {
            if (!o.accent.isBlank()) {
                t = t.withAccent(0xFF000000 | Integer.parseInt(o.accent.replace("#", ""), 16));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        ArtManager.drawLogo(ctx, x, y, w, alpha * NativeScreenLayouts.clamp(o.opacity, 0.0f, 100.0f) / 100.0f, t.accent(), t.accent2());
    }

    public static float[] logoRect(Screen s) {
        return logos.get(s);
    }

    public static Screen current() {
        return preview != null ? preview : Minecraft.getInstance().screen;
    }

    private static void rect(Screen screen, String id, float x, float y, float w, float h) {
        extras.computeIfAbsent(screen, k -> new LinkedHashMap()).put(id, new float[]{x, y, w, h});
    }

    public static Map<String, float[]> rects(Screen screen) {
        return extras.getOrDefault(screen, Map.of());
    }

    public static String copyrightId(Screen screen) {
        for (Entry e : NativeScreenLayouts.entries(screen)) {
            if (!e.id().startsWith("title.credits#")) continue;
            return e.id();
        }
        return "";
    }

    public static String label(Screen screen, String id) {
        return switch (id) {
            case "@logo" -> "\u041b\u043e\u0433\u043e\u0442\u0438\u043f";
            case "@splash" -> "\u0416\u0451\u043b\u0442\u0430\u044f \u043d\u0430\u0434\u043f\u0438\u0441\u044c";
            case "@version" -> "\u0421\u0442\u0440\u043e\u043a\u0430 \u0432\u0435\u0440\u0441\u0438\u0438";
            case "@art" -> "\u0410\u043d\u0438\u043c\u0435-\u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0430";
            default -> {
                if (id.startsWith("title.credits#")) {
                    yield "\u041a\u043e\u043f\u0438\u0440\u0430\u0439\u0442";
                }
                for (Entry e : NativeScreenLayouts.entries(screen)) {
                    if (!e.id().equals(id)) continue;
                    String n = e.widget().getMessage().getString();
                    if (n.isBlank()) {
                        yield "\u041a\u043d\u043e\u043f\u043a\u0430-\u0438\u043a\u043e\u043d\u043a\u0430";
                    }
                    yield n;
                }
                yield id;
            }
        };
    }

    public static void splash(Screen screen, GuiGraphics ctx, Font tr, Component text, float alpha) {
        Item o = NativeScreenLayouts.item(screen, "@splash");
        text = Component.literal((String)(o.text.isBlank() ? text.getString() : o.text));
        float scale = (1.8f - (float)Math.abs(Math.sin((double)(System.currentTimeMillis() % 1000L) / 1000.0 * Math.PI * 2.0)) * 0.1f) * 100.0f / (float)(tr.width((FormattedText)text) + 32) * NativeScreenLayouts.clamp(o.scale, 25.0f, 250.0f) / 100.0f;
        float cx = (float)screen.width / 2.0f + 123.0f + o.dx * (float)screen.width;
        float cy = 69.0f + o.dy * (float)screen.height;
        float tw = (float)tr.width((FormattedText)text) * scale;
        NativeScreenLayouts.rect(screen, "@splash", cx - tw / 2.0f - 4.0f, cy - tw * 0.18f - 12.0f, tw + 8.0f, tw * 0.36f + 24.0f);
        if (o.hidden || (double)(alpha * o.opacity) < 0.1) {
            return;
        }
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(cx, cy);
        m.rotate(-0.34906587f);
        m.scale(scale, scale);
        int rgb = 0xFFFF00;
        try {
            if (!o.accent.isBlank()) {
                rgb = Integer.parseInt(o.accent, 16);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        ctx.drawString(tr, text, -tr.width((FormattedText)text) / 2, -8, (int)(255.0f * alpha * o.opacity / 100.0f) << 24 | rgb);
        m.popMatrix();
    }

    public static void footer(Screen screen, GuiGraphics ctx, Font tr, String text, int x, int y, int color) {
        Item o = NativeScreenLayouts.item(screen, "@version");
        if (!o.text.isBlank()) {
            text = o.text;
        }
        float scale = NativeScreenLayouts.clamp(o.scale, 25.0f, 250.0f) / 100.0f;
        float px = (float)x + o.dx * (float)screen.width;
        float py = (float)y + o.dy * (float)screen.height;
        NativeScreenLayouts.rect(screen, "@version", px, py, (float)tr.width(text) * scale, 10.0f * scale);
        if (o.hidden || o.opacity <= 0.0f || (float)(color >>> 24) * o.opacity < 1.0f) {
            return;
        }
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(px, py);
        m.scale(scale, scale);
        int rgb = color & 0xFFFFFF;
        try {
            if (!o.accent.isBlank()) {
                rgb = Integer.parseInt(o.accent, 16);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        ctx.drawString(tr, text, 0, 0, (int)((float)(color >>> 24) * o.opacity / 100.0f) << 24 | rgb);
        m.popMatrix();
    }

    public static boolean textWidget(Object object, GuiGraphics ctx) {
        if (!(object instanceof AbstractWidget)) {
            return false;
        }
        AbstractWidget widget = (AbstractWidget)object;
        Screen screen = owners.get(widget);
        if (screen == null) {
            return false;
        }
        for (Entry e : NativeScreenLayouts.entries(screen)) {
            if (e.widget() != widget) continue;
            Item o = NativeScreenLayouts.item(screen, e.id());
            if (o.hidden || o.opacity <= 0.0f) {
                return true;
            }
            float scale = NativeScreenLayouts.clamp(o.scale, 25.0f, 250.0f) / 100.0f;
            Matrix3x2fStack m = ctx.pose();
            m.pushMatrix();
            m.translate((float)widget.getX(), (float)widget.getY());
            m.scale(scale, scale);
            int rgb = 0xFFFFFF;
            try {
                if (!o.accent.isBlank()) {
                    rgb = Integer.parseInt(o.accent, 16);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            float alpha = ((ClickableWidgetAccessor)widget).astra$getAlpha() * o.opacity / 100.0f;
            if ((double)alpha > 0.003) {
                ctx.drawString(Minecraft.getInstance().font, widget.getMessage(), 0, 0, (int)(alpha * 255.0f) << 24 | rgb);
            }
            m.popMatrix();
            return true;
        }
        return false;
    }

    public static void art(Screen screen, GuiGraphics ctx, Theme theme, float x, float bottom, float w, float maxH, float alpha) {
        Identifier tex;
        if (screen == null || NativeScreenLayouts.key(screen).isEmpty()) {
            ArtManager.drawSidebarArt(ctx, theme, x, bottom, w, maxH, alpha);
            return;
        }
        Item o = NativeScreenLayouts.item(screen, "@art");
        CanvasBoard.Canvas custom = NativeScreenLayouts.layout((Screen)screen).art;
        if (screen instanceof PauseScreen && custom == null) {
            return;
        }
        float scale = NativeScreenLayouts.clamp(o.scale, 25.0f, 250.0f) / 100.0f;
        float aspect = Math.max(0.01f, custom == null ? ArtManager.sidebarAspect() : (float)(custom.width / custom.height));
        float bw = w;
        float bh = w / aspect;
        if (bh > maxH) {
            bh = maxH;
            bw = maxH * aspect;
        }
        float aw = bw * scale * NativeScreenLayouts.clamp(o.sw, 25.0f, 300.0f) / 100.0f;
        float ah = bh * scale * NativeScreenLayouts.clamp(o.sh, 25.0f, 300.0f) / 100.0f;
        float px = x + w - aw + o.dx * (float)screen.width;
        float py = bottom + o.dy * (float)screen.height - ah;
        NativeScreenLayouts.rect(screen, "@art", px, py, aw, ah);
        if (o.hidden || o.opacity <= 0.0f) {
            return;
        }
        Identifier identifier = tex = custom == null ? ArtManager.sidebarTexture() : CanvasBoard.texture(custom);
        if (tex == null) {
            return;
        }
        int col = ColorUtil.withAlpha(-1, alpha * NativeScreenLayouts.clamp(o.opacity, 0.0f, 100.0f) / 100.0f);
        float r = Math.min(Math.max(0.0f, o.radius), Math.min(aw, ah) / 2.0f);
        if (r < 0.5f) {
            Render2D.texture(ctx, tex, px, py, aw, ah, 0.0f, 0.0f, 1.0f, 1.0f, col);
        } else {
            Render2D.textureRoundRect(ctx, tex, aw / ah, px, py, aw, ah, r, col);
        }
    }

    public static void backdrop(Screen s, GuiGraphics ctx) {
        if (s == null || NativeScreenLayouts.key(s).isEmpty()) {
            return;
        }
        Layout l = NativeScreenLayouts.layout(s);
        if (l.background == null) {
            return;
        }
        Identifier tex = CanvasBoard.texture(l.background);
        if (tex == null) {
            return;
        }
        Render2D.texture(ctx, tex, 0.0f, 0.0f, (float)s.width, (float)s.height, 0.0f, 0.0f, 1.0f, 1.0f, ColorUtil.withAlpha(-1, NativeScreenLayouts.clamp(l.backgroundOpacity, 0.0f, 100.0f) / 100.0f));
    }

    static {
        expected = new WeakHashMap<Object, int[]>();
        extras = new WeakHashMap<Screen, Map<String, float[]>>();
    }

    public static final class Layout {
        public Map<String, Item> items = new LinkedHashMap<String, Item>();
        public CanvasBoard.Canvas background;
        public CanvasBoard.Canvas art;
        public CanvasBoard.Canvas logo;
        public int schemaVersion;
        public float backgroundOpacity = 100.0f;
    }

    public static final class Item {
        public float dx;
        public float dy;
        public float scale = 100.0f;
        public float opacity = 100.0f;
        public float radius = -1.0f;
        public float sw = 100.0f;
        public float sh = 100.0f;
        public boolean hidden;
        public String accent = "";
        public String text = "";
    }

    public record Entry(String id, AbstractWidget widget, Base base) {
    }

    public record Base(int x, int y, int w, int h, boolean visible) {
    }
}

