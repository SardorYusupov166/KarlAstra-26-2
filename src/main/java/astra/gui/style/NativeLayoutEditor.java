/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
 *  org.joml.Matrix3x2fStack
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package astra.gui.style;

import astra.builder.CanvasBoard;
import astra.gui.style.AstraInspectorScreen;
import astra.gui.style.NativeScreenLayouts;
import astra.gui.vanilla.MenuStyle;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Mesh;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.settings.StringSetting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import org.joml.Matrix3x2fStack;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class NativeLayoutEditor
extends AstraInspectorScreen {
    private static final float SNAP = 4.0f;
    private static boolean showGrid = true;
    private static boolean snapGrid = true;
    private static boolean snapElements = true;
    private static float gridSize = 10.0f;
    private final Screen source;
    private String selected = "";
    private final Deque<String> history = new ArrayDeque<String>();
    private float ox;
    private float oy;
    private float zoom;
    private float startX;
    private float startY;
    private float startDx;
    private float startDy;
    private float startScale;
    private float startSw;
    private float startSh;
    private float[] startRect;
    private int corner;
    private boolean dragging;
    private final List<float[]> guides = new ArrayList<float[]>();

    public NativeLayoutEditor(Screen source) {
        super("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f");
        this.source = source;
    }

    private void remember() {
        String snap = NativeScreenLayouts.snapshot(this.source);
        if (snap.equals(this.history.peek())) {
            return;
        }
        this.history.push(snap);
        for (int bytes = this.history.stream().mapToInt(String::length).sum(); this.history.size() > 1 && (this.history.size() > 30 || bytes > 0x1000000); bytes -= this.history.removeLast().length()) {
        }
    }

    private void changed() {
        NativeScreenLayouts.apply(this.source);
    }

    private Setting<?> number(String label, float min, float max, float step, Supplier<Float> get, Consumer<Float> put) {
        return new NumberSetting(label, get.get().floatValue(), min, max, step).bind(() -> ((Float)get.get()).floatValue(), v -> {
            put.accept(Float.valueOf(v.floatValue()));
            this.changed();
        });
    }

    private Map<String, String> choices() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        int n = 1;
        for (NativeScreenLayouts.Entry e : NativeScreenLayouts.entries(this.source)) {
            map.put(n++ + ". " + NativeScreenLayouts.label(this.source, e.id()), e.id());
        }
        if (!(this.source instanceof JoinMultiplayerScreen)) {
            map.put("\u041b\u043e\u0433\u043e\u0442\u0438\u043f", "@logo");
        }
        if (this.source instanceof TitleScreen) {
            map.put("\u0416\u0451\u043b\u0442\u0430\u044f \u043d\u0430\u0434\u043f\u0438\u0441\u044c", "@splash");
            map.put("\u0421\u0442\u0440\u043e\u043a\u0430 \u0432\u0435\u0440\u0441\u0438\u0438", "@version");
        }
        map.put("\u0410\u043d\u0438\u043c\u0435-\u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", "@art");
        return map;
    }

    @Override
    protected List<Setting<?>> properties() {
        ArrayList list = new ArrayList();
        Map<String, String> choices = this.choices();
        if (!choices.containsValue(this.selected)) {
            this.selected = choices.isEmpty() ? "@art" : choices.values().iterator().next();
        }
        NativeScreenLayouts.Item o = NativeScreenLayouts.item(this.source, this.selected);
        String label = choices.entrySet().stream().filter(e -> ((String)e.getValue()).equals(this.selected)).map(Map.Entry::getKey).findFirst().orElse("\u042d\u043b\u0435\u043c\u0435\u043d\u0442");
        list.add((Setting<?>)new ModeSetting("\u042d\u043b\u0435\u043c\u0435\u043d\u0442", label, (String[])choices.keySet().toArray(String[]::new)).bind(() -> choices.entrySet().stream().filter(e -> ((String)e.getValue()).equals(this.selected)).map(Map.Entry::getKey).findFirst().orElse(label), v -> {
            this.selected = (String)choices.get(v);
            this.field_22787.execute(this::rebuildRows);
        }));
        list.add(this.number("\u041f\u043e\u0437\u0438\u0446\u0438\u044f X, %", -100.0f, 100.0f, 0.1f, () -> Float.valueOf(o.dx * 100.0f), v -> {
            o.dx = v.floatValue() / 100.0f;
        }));
        list.add(this.number("\u041f\u043e\u0437\u0438\u0446\u0438\u044f Y, %", -100.0f, 100.0f, 0.1f, () -> Float.valueOf(o.dy * 100.0f), v -> {
            o.dy = v.floatValue() / 100.0f;
        }));
        list.add(this.number("\u0420\u0430\u0437\u043c\u0435\u0440", 25.0f, 250.0f, 1.0f, () -> Float.valueOf(o.scale), v -> {
            o.scale = v.floatValue();
        }));
        boolean art = this.selected.equals("@art");
        if (art) {
            list.add(this.number("\u0428\u0438\u0440\u0438\u043d\u0430, %", 25.0f, 300.0f, 1.0f, () -> Float.valueOf(o.sw), v -> {
                o.sw = v.floatValue();
            }));
            list.add(this.number("\u0412\u044b\u0441\u043e\u0442\u0430, %", 25.0f, 300.0f, 1.0f, () -> Float.valueOf(o.sh), v -> {
                o.sh = v.floatValue();
            }));
            list.add(new ButtonSetting("\u041f\u0440\u043e\u043f\u043e\u0440\u0446\u0438\u0438", "\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u043f\u0440\u043e\u043f\u043e\u0440\u0446\u0438\u0438 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438", () -> {
                this.remember();
                o.sh = 100.0f;
                o.sw = 100.0f;
                this.changed();
                this.finishChange();
            }));
        }
        list.add(this.number("\u041d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 0.0f, 100.0f, 1.0f, () -> Float.valueOf(o.opacity), v -> {
            o.opacity = v.floatValue();
        }));
        list.add((Setting<?>)new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u044d\u043b\u0435\u043c\u0435\u043d\u0442", true).bind(() -> !o.hidden, v -> {
            o.hidden = v == false;
            this.changed();
        }));
        if (!this.selected.startsWith("@") || art) {
            list.add(this.number("\u0421\u043a\u0440\u0443\u0433\u043b\u0435\u043d\u0438\u0435", art ? 0.0f : -1.0f, art ? 40.0f : 30.0f, 1.0f, () -> Float.valueOf(art ? Math.max(0.0f, o.radius) : o.radius), v -> {
                o.radius = v.floatValue();
            }));
        }
        if (!art) {
            String[] colors = new String[]{"\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u0411\u0435\u043b\u044b\u0439", "\u0413\u043e\u043b\u0443\u0431\u043e\u0439", "\u0420\u043e\u0437\u043e\u0432\u044b\u0439", "\u0424\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u044b\u0439", "\u041a\u0440\u0430\u0441\u043d\u044b\u0439"};
            String[] hex = new String[]{"", "FFFFFF", "22D3EE", "FF4DD8", "A855FF", "FF3344"};
            list.add((Setting<?>)new ModeSetting("\u0426\u0432\u0435\u0442", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", colors).bind(() -> {
                for (int i = 0; i < hex.length; ++i) {
                    if (!hex[i].equalsIgnoreCase(o.accent)) continue;
                    return colors[i];
                }
                return "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b";
            }, v -> {
                for (int i = 0; i < colors.length; ++i) {
                    if (!colors[i].equals(v)) continue;
                    o.accent = hex[i];
                }
                this.changed();
            }));
            list.add((Setting<?>)new StringSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 HEX", o.accent, "22D3EE", 6).bind(() -> o.accent, v -> {
                o.accent = v;
                this.changed();
            }));
        }
        if (this.selected.equals("@splash") || this.selected.equals("@version")) {
            list.add((Setting<?>)new StringSetting("\u0421\u0432\u043e\u0439 \u0442\u0435\u043a\u0441\u0442", o.text, "\u041e\u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0438\u0441\u0445\u043e\u0434\u043d\u044b\u0439", 180).bind(() -> o.text, v -> {
                o.text = v;
            }));
        }
        if (art || this.selected.equals("@logo")) {
            list.add(new ButtonSetting("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", "\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c / \u0437\u0430\u043c\u0435\u043d\u0438\u0442\u044c", () -> this.picture(this.selected)));
        }
        list.add(new ButtonSetting("\u0423\u0434\u0430\u043b\u0438\u0442\u044c", "\u0423\u0431\u0440\u0430\u0442\u044c \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0439 \u044d\u043b\u0435\u043c\u0435\u043d\u0442", this::deleteAction));
        list.add(new ButtonSetting("\u0421\u0431\u0440\u043e\u0441", "\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u0438\u0441\u0445\u043e\u0434\u043d\u043e\u0435 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435", () -> {
            this.remember();
            NativeScreenLayouts.layout((Screen)this.source).items.remove(this.selected);
            this.changed();
            this.finishChange();
            this.rebuildRows();
        }));
        list.add(new ButtonSetting("\u041e\u0442\u043c\u0435\u043d\u0430", "\u041e\u0442\u043c\u0435\u043d\u0438\u0442\u044c \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0435\u0435 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435", this::undoAction));
        list.add((Setting<?>)new BooleanSetting("\u0421\u0435\u0442\u043a\u0430", true).bind(() -> showGrid, v -> {
            showGrid = v;
        }));
        list.add((Setting<?>)new BooleanSetting("\u041f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u043a \u0441\u0435\u0442\u043a\u0435", true).bind(() -> snapGrid, v -> {
            snapGrid = v;
        }));
        list.add((Setting<?>)((Setting)new BooleanSetting("\u041f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u043a \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430\u043c", true).bind(() -> snapElements, v -> {
            snapElements = v;
        })).desc("\u041a\u0440\u0430\u044f \u0438 \u0446\u0435\u043d\u0442\u0440\u044b \u0434\u0440\u0443\u0433\u0438\u0445 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u043e\u0432 \u0438 \u0446\u0435\u043d\u0442\u0440 \u044d\u043a\u0440\u0430\u043d\u0430"));
        list.add((Setting<?>)new NumberSetting("\u0428\u0430\u0433 \u0441\u0435\u0442\u043a\u0438", gridSize, 4.0, 40.0, 1.0).bind(() -> gridSize, v -> {
            gridSize = v.floatValue();
        }));
        list.add(new ButtonSetting("\u0424\u043e\u043d \u044d\u0442\u043e\u0433\u043e \u044d\u043a\u0440\u0430\u043d\u0430", "\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0443", () -> this.picture("@background")));
        NativeScreenLayouts.Layout layout = NativeScreenLayouts.layout(this.source);
        list.add(this.number("\u041d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0444\u043e\u043d\u0430", 0.0f, 100.0f, 1.0f, () -> Float.valueOf(layout.backgroundOpacity), v -> {
            layout.backgroundOpacity = v.floatValue();
        }));
        list.add(new ButtonSetting("\u0423\u0431\u0440\u0430\u0442\u044c \u0444\u043e\u043d", "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0432\u043e\u044e \u0444\u043e\u043d\u043e\u0432\u0443\u044e \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0443", () -> {
            this.remember();
            layout.background = null;
            this.finishChange();
        }));
        return list;
    }

    private void picture(String target) {
        CompletableFuture.supplyAsync(() -> {
            String f = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Astra image", (CharSequence)"", (PointerBuffer)null, null, (boolean)false);
            if (f == null) {
                return null;
            }
            try {
                return CanvasBoard.importImage(Path.of(f, new String[0]));
            }
            catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenComplete((image, error) -> this.field_22787.execute(() -> {
            if (error != null) {
                this.status = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0443";
                return;
            }
            if (image != null) {
                this.remember();
                NativeScreenLayouts.Layout l = NativeScreenLayouts.layout(this.source);
                if (target.equals("@art")) {
                    l.art = image;
                } else if (target.equals("@logo")) {
                    l.logo = image;
                } else {
                    l.background = image;
                    l.backgroundOpacity = 100.0f;
                }
                NativeScreenLayouts.item((Screen)this.source, (String)target).hidden = false;
                this.finishChange();
                this.status = "\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430";
            }
        }));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void workspace(GuiGraphics ctx, int mx, int my, float delta) {
        this.ox = this.workspaceLeft() + 10.0f * this.s;
        this.oy = 54.0f * this.s;
        this.zoom = Math.min(((float)this.field_22789 - this.ox - 10.0f * this.s) / (float)this.source.field_22789, ((float)this.field_22790 - this.oy - 20.0f * this.s) / (float)this.source.field_22790);
        ctx.method_44379((int)this.workspaceLeft(), (int)(40.0f * this.s), this.field_22789, this.field_22790);
        Matrix3x2fStack m = ctx.method_51448();
        m.pushMatrix();
        m.translate(this.ox, this.oy);
        m.scale(this.zoom, this.zoom);
        NativeScreenLayouts.preview = this.source;
        try {
            NativeScreenLayouts.backdrop(this.source, ctx);
            if (!(this.source instanceof TitleScreen)) {
                float aw = Math.min((float)this.source.field_22789 * 0.26f, (float)this.source.field_22790 * 0.5f);
                NativeScreenLayouts.art(this.source, ctx, ThemeManager.get(), (float)this.source.field_22789 - aw - 6.0f, this.source.field_22790, aw, (float)this.source.field_22790 * 0.82f, 1.0f);
            }
            this.source.method_25394(ctx, -10000, -10000, delta);
            if (this.source instanceof PauseScreen) {
                MenuStyle.renderPausePreview(this.source, ctx);
            }
            Theme t = ThemeManager.get();
            this.renderGrid(ctx, t);
            float[] r = this.selectionRect();
            if (r != null) {
                float hs = 3.0f * this.s / this.zoom;
                Render2D.outline(ctx, r[0] - 2.0f, r[1] - 2.0f, r[2] + 4.0f, r[3] + 4.0f, 2.0f, 1.1f * this.s / this.zoom, t.accent2());
                for (Object c : (Object)NativeLayoutEditor.corners(r)) {
                    Render2D.roundRect(ctx, (float)(c[0] - hs), (float)(c[1] - hs), hs * 2.0f, hs * 2.0f, 1.0f, -1);
                    Render2D.outline(ctx, (float)(c[0] - hs), (float)(c[1] - hs), hs * 2.0f, hs * 2.0f, 1.0f, 0.8f * this.s / this.zoom, t.accent2());
                }
                String size = Math.round(r[2]) + " \u00d7 " + Math.round(r[3]);
                float fs = 5.5f * this.s / this.zoom;
                float lw = Fonts.width(size, fs, true) + 6.0f * this.s / this.zoom;
                float ly = r[1] - 11.0f * this.s / this.zoom >= 0.0f ? r[1] - 11.0f * this.s / this.zoom : r[1] + r[3] + 3.0f * this.s / this.zoom;
                Render2D.roundRect(ctx, r[0], ly, lw, 8.5f * this.s / this.zoom, 2.5f * this.s / this.zoom, ColorUtil.withAlpha(t.accent(), 0.9f));
                Fonts.draw(ctx, size, r[0] + 3.0f * this.s / this.zoom, ly + 1.6f * this.s / this.zoom, fs, -1, true);
            }
            int col = ColorUtil.withAlpha(t.accent3(), 0.95f);
            for (float[] g : this.guides) {
                if (g[0] == 0.0f) {
                    Render2D.dashedLine(ctx, g[1], 0.0f, g[1], this.source.field_22790, 0.8f * this.s / this.zoom, 5.0f, 3.0f, col);
                    continue;
                }
                Render2D.dashedLine(ctx, 0.0f, g[1], this.source.field_22789, g[1], 0.8f * this.s / this.zoom, 5.0f, 3.0f, col);
            }
        }
        finally {
            NativeScreenLayouts.preview = null;
            m.popMatrix();
            ctx.method_44380();
        }
    }

    private void renderGrid(GuiGraphics ctx, Theme t) {
        float px = Render2D.pixel(ctx);
        if (showGrid) {
            Mesh mesh = Render2D.begin();
            int minor = ColorUtil.withAlpha(t.accent(), 0.08f);
            int major = ColorUtil.withAlpha(t.accent(), 0.16f);
            int i = 0;
            float x = 0.0f;
            while (x <= (float)this.source.field_22789) {
                mesh.rect(x, 0.0f, px, (float)this.source.field_22790, i % 5 == 0 ? major : minor);
                x += gridSize;
                ++i;
            }
            i = 0;
            float y = 0.0f;
            while (y <= (float)this.source.field_22790) {
                mesh.rect(0.0f, y, (float)this.source.field_22789, px, i % 5 == 0 ? major : minor);
                y += gridSize;
                ++i;
            }
            Render2D.end(ctx);
        }
        int center = ColorUtil.withAlpha(t.accent3(), 0.45f);
        Render2D.dashedLine(ctx, (float)this.source.field_22789 / 2.0f, 0.0f, (float)this.source.field_22789 / 2.0f, this.source.field_22790, 0.6f * this.s / this.zoom, 4.0f, 4.0f, center);
        Render2D.dashedLine(ctx, 0.0f, (float)this.source.field_22790 / 2.0f, this.source.field_22789, (float)this.source.field_22790 / 2.0f, 0.6f * this.s / this.zoom, 4.0f, 4.0f, center);
    }

    private static float[][] corners(float[] r) {
        return new float[][]{{r[0], r[1]}, {r[0] + r[2], r[1]}, {r[0] + r[2], r[1] + r[3]}, {r[0], r[1] + r[3]}};
    }

    private float[] point(double x, double y) {
        return new float[]{(float)((x - (double)this.ox) / (double)this.zoom), (float)((y - (double)this.oy) / (double)this.zoom)};
    }

    private float[] rectOf(String id) {
        if (id.equals("@logo")) {
            return NativeScreenLayouts.logoRect(this.source);
        }
        if (id.startsWith("@")) {
            return NativeScreenLayouts.rects(this.source).get(id);
        }
        for (NativeScreenLayouts.Entry e : NativeScreenLayouts.entries(this.source)) {
            if (!e.id().equals(id)) continue;
            AbstractWidget w = e.widget();
            return new float[]{w.method_46426(), w.method_46427(), w.method_25368(), w.method_25364()};
        }
        return null;
    }

    private float[] selectionRect() {
        return this.rectOf(this.selected);
    }

    @Override
    protected boolean workspaceClick(double x, double y, int button) {
        float[] fArray;
        String found;
        if (button != 0 || this.zoom <= 0.0f) {
            return false;
        }
        float[] p = this.point(x, y);
        float[] cur = this.selectionRect();
        int hit = 0;
        if (cur != null) {
            float zone = 7.0f * this.s / this.zoom;
            float[][] cs = NativeLayoutEditor.corners(cur);
            for (int i = 0; i < 4; ++i) {
                if (!(Math.abs(p[0] - cs[i][0]) <= zone) || !(Math.abs(p[1] - cs[i][1]) <= zone)) continue;
                hit = i + 1;
                break;
            }
        }
        String string = found = hit != 0 ? this.selected : null;
        if (found == null) {
            float[] logo = NativeScreenLayouts.logoRect(this.source);
            if (NativeLayoutEditor.inside(p, logo)) {
                found = "@logo";
            }
            for (Map.Entry<String, float[]> e : NativeScreenLayouts.rects(this.source).entrySet()) {
                if (!NativeLayoutEditor.inside(p, e.getValue())) continue;
                found = e.getKey();
            }
            List<NativeScreenLayouts.Entry> entries = NativeScreenLayouts.entries(this.source);
            for (int i = entries.size() - 1; i >= 0; --i) {
                NativeScreenLayouts.Entry e = entries.get(i);
                AbstractWidget w = e.widget();
                if (!w.field_22764 || !NativeLayoutEditor.inside(p, new float[]{w.method_46426(), w.method_46427(), w.method_25368(), w.method_25364()})) continue;
                found = e.id();
                break;
            }
        }
        if (found == null) {
            return false;
        }
        boolean changedSelection = !found.equals(this.selected);
        this.selected = found;
        this.remember();
        NativeScreenLayouts.Item o = NativeScreenLayouts.item(this.source, this.selected);
        this.startDx = o.dx;
        this.startDy = o.dy;
        this.startScale = o.scale;
        this.startSw = o.sw;
        this.startSh = o.sh;
        this.startX = p[0];
        this.startY = p[1];
        float[] r = this.selectionRect();
        if (r == null) {
            float[] fArray2 = new float[4];
            fArray2[0] = p[0];
            fArray2[1] = p[1];
            fArray2[2] = 100.0f;
            fArray = fArray2;
            fArray2[3] = 20.0f;
        } else {
            fArray = (float[])r.clone();
        }
        this.startRect = fArray;
        this.corner = hit;
        this.dragging = true;
        if (changedSelection) {
            this.rebuildRows();
        }
        return true;
    }

    private static boolean inside(float[] p, float[] r) {
        return r != null && p[0] >= r[0] - 3.0f && p[0] <= r[0] + r[2] + 3.0f && p[1] >= r[1] - 3.0f && p[1] <= r[1] + r[3] + 3.0f;
    }

    @Override
    protected boolean workspaceDrag(double x, double y, int button) {
        if (!this.dragging) {
            return false;
        }
        float[] p = this.point(x, y);
        NativeScreenLayouts.Item o = NativeScreenLayouts.item(this.source, this.selected);
        float ddx = p[0] - this.startX;
        float ddy = p[1] - this.startY;
        float W = Math.max(1, this.source.field_22789);
        float H = Math.max(1, this.source.field_22790);
        this.guides.clear();
        if (this.corner != 0) {
            this.resize(o, ddx, ddy, W, H);
        } else {
            float[] snapped = this.snap(this.startRect[0] + ddx, this.startRect[1] + ddy, this.startRect[2], this.startRect[3]);
            o.dx = NativeScreenLayouts.clamp(this.startDx + (snapped[0] - this.startRect[0]) / W, -1.0f, 1.0f);
            o.dy = NativeScreenLayouts.clamp(this.startDy + (snapped[1] - this.startRect[1]) / H, -1.0f, 1.0f);
        }
        this.changed();
        return true;
    }

    private void resize(NativeScreenLayouts.Item o, float ddx, float ddy, float W, float H) {
        boolean art;
        boolean left = this.corner == 1 || this.corner == 4;
        boolean top = this.corner == 1 || this.corner == 2;
        float sw0 = this.startRect[2];
        float sh0 = this.startRect[3];
        float newW = Math.max(8.0f, sw0 + (left ? -ddx : ddx));
        float newH = Math.max(8.0f, sh0 + (top ? -ddy : ddy));
        if (snapGrid && !this.hasShift()) {
            float edgeY;
            float sy;
            float edgeX = left ? this.startRect[0] + sw0 - newW : this.startRect[0] + newW;
            float sx = (float)Math.round(edgeX / gridSize) * gridSize;
            if (Math.abs(sx - edgeX) < 4.0f) {
                newW += left ? edgeX - sx : sx - edgeX;
            }
            if (Math.abs((sy = (float)Math.round((edgeY = top ? this.startRect[1] + sh0 - newH : this.startRect[1] + newH) / gridSize) * gridSize) - edgeY) < 4.0f) {
                newH += top ? edgeY - sy : sy - edgeY;
            }
        }
        if (!(art = this.selected.equals("@art")) || this.hasShift()) {
            float kx = newW / sw0;
            float ky = newH / sh0;
            float k = Math.abs(kx - 1.0f) >= Math.abs(ky - 1.0f) ? kx : ky;
            newW = sw0 * k;
            newH = sh0 * k;
        }
        if (art) {
            if (this.hasShift()) {
                o.scale = NativeScreenLayouts.clamp(this.startScale * newW / sw0, 25.0f, 250.0f);
            } else {
                o.sw = NativeScreenLayouts.clamp(this.startSw * newW / sw0, 25.0f, 300.0f);
                o.sh = NativeScreenLayouts.clamp(this.startSh * newH / sh0, 25.0f, 300.0f);
            }
            newW = sw0 * (o.scale / this.startScale) * (o.sw / this.startSw);
            newH = sh0 * (o.scale / this.startScale) * (o.sh / this.startSh);
            o.dx = NativeScreenLayouts.clamp(this.startDx + (left ? 0.0f : newW - sw0) / W, -1.0f, 1.0f);
            o.dy = NativeScreenLayouts.clamp(this.startDy + (top ? 0.0f : newH - sh0) / H, -1.0f, 1.0f);
        } else {
            o.scale = NativeScreenLayouts.clamp(this.startScale * newW / sw0, 25.0f, 250.0f);
            newW = sw0 * o.scale / this.startScale;
            newH = sh0 * o.scale / this.startScale;
            o.dx = NativeScreenLayouts.clamp(this.startDx + (left ? sw0 - newW : 0.0f) / W, -1.0f, 1.0f);
            o.dy = NativeScreenLayouts.clamp(this.startDy + (top ? sh0 - newH : 0.0f) / H, -1.0f, 1.0f);
        }
    }

    private boolean hasShift() {
        long win = this.field_22787.method_22683().method_4490();
        return GLFW.glfwGetKey((long)win, (int)340) == 1 || GLFW.glfwGetKey((long)win, (int)344) == 1;
    }

    private float[] snap(float x, float y, float w, float h) {
        if (this.hasShift()) {
            return new float[]{x, y};
        }
        boolean sx = false;
        boolean sy = false;
        float W = this.source.field_22789;
        float H = this.source.field_22790;
        if (snapElements) {
            if (Math.abs(x + w / 2.0f - W / 2.0f) < 4.0f) {
                x = W / 2.0f - w / 2.0f;
                this.guides.add(new float[]{0.0f, W / 2.0f});
                sx = true;
            }
            if (Math.abs(y + h / 2.0f - H / 2.0f) < 4.0f) {
                y = H / 2.0f - h / 2.0f;
                this.guides.add(new float[]{1.0f, H / 2.0f});
                sy = true;
            }
            ArrayList<float[]> others = new ArrayList<float[]>();
            for (String id : this.choices().values()) {
                float[] r;
                if (id.equals(this.selected) || (r = this.rectOf(id)) == null || !(r[2] > 0.0f) || !(r[3] > 0.0f)) continue;
                others.add(r);
            }
            block1: for (float[] r : others) {
                int i;
                float[] theirs;
                float[] mine;
                if (!sx) {
                    mine = new float[]{x, x + w / 2.0f, x + w};
                    theirs = new float[]{r[0], r[0] + r[2] / 2.0f, r[0] + r[2]};
                    block2: for (i = 0; i < 3; ++i) {
                        for (float t : theirs) {
                            if (!(Math.abs(mine[i] - t) < 4.0f)) continue;
                            x += t - mine[i];
                            this.guides.add(new float[]{0.0f, t});
                            sx = true;
                            break block2;
                        }
                    }
                }
                if (sy) continue;
                mine = new float[]{y, y + h / 2.0f, y + h};
                theirs = new float[]{r[1], r[1] + r[3] / 2.0f, r[1] + r[3]};
                for (i = 0; i < 3; ++i) {
                    for (float t : theirs) {
                        if (!(Math.abs(mine[i] - t) < 4.0f)) continue;
                        y += t - mine[i];
                        this.guides.add(new float[]{1.0f, t});
                        sy = true;
                        continue block1;
                    }
                }
            }
        }
        if (snapGrid) {
            float gy;
            float gx;
            if (!sx && Math.abs((gx = (float)Math.round(x / gridSize) * gridSize) - x) < 4.0f) {
                x = gx;
            }
            if (!sy && Math.abs((gy = (float)Math.round(y / gridSize) * gridSize) - y) < 4.0f) {
                y = gy;
            }
        }
        return new float[]{x, y};
    }

    @Override
    protected boolean workspaceRelease() {
        boolean used = this.dragging;
        this.dragging = false;
        this.corner = 0;
        this.guides.clear();
        if (used) {
            this.finishChange();
            this.rebuildRows();
        }
        return used;
    }

    @Override
    protected void beforeChange() {
        this.remember();
    }

    @Override
    protected void finishChange() {
        NativeScreenLayouts.save();
    }

    @Override
    protected void undoAction() {
        if (!this.history.isEmpty()) {
            NativeScreenLayouts.restore(this.source, this.history.pop());
            this.rebuildRows();
        }
    }

    @Override
    protected void deleteAction() {
        this.remember();
        NativeScreenLayouts.item((Screen)this.source, (String)this.selected).hidden = true;
        this.changed();
        this.finishChange();
        this.rebuildRows();
    }

    public boolean method_25421() {
        return this.source.method_25421();
    }

    public void method_25419() {
        this.finishChange();
        this.field_22787.method_1507(this.source);
    }

    public boolean editorClick(double x, double y, int b) {
        return this.inputClick(x, y, b);
    }

    public boolean editorDrag(double x, double y, int b) {
        return this.inputDrag(x, y, b);
    }

    public boolean editorRelease() {
        return this.workspaceRelease();
    }
}

