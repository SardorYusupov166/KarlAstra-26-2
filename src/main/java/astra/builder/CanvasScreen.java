/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package astra.builder;

import astra.builder.CanvasBoard;
import astra.gui.style.AstraInspectorScreen;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.online.AstraOnline;
import astra.render.ColorUtil;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.settings.StringSetting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.lang.invoke.CallSite;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class CanvasScreen
extends AstraInspectorScreen {
    private final Screen parent;
    private String selected = "";
    private String path = "";
    private String wanted = "";
    private String fingerprint = "";
    private CanvasBoard.Canvas draft;
    private CanvasBoard.Canvas deleted;
    private boolean dirty;
    private boolean pending;
    private boolean busy;
    private long expected;

    public CanvasScreen(Screen parent) {
        super("\u0425\u043e\u043b\u0441\u0442\u044b \u0432 \u043c\u0438\u0440\u0435");
        this.parent = parent;
    }

    private void select(CanvasBoard.Canvas c) {
        this.selected = c.id;
        this.draft = c.copy();
        this.dirty = false;
        this.pending = false;
        this.rebuildRows();
    }

    private Setting<?> number(String title, double min, double max, double step, DoubleSupplier get, DoubleConsumer put) {
        return new NumberSetting(title, get.getAsDouble(), min, max, step).bind(() -> get.getAsDouble(), v -> {
            put.accept((double)v);
            this.dirty = true;
        });
    }

    @Override
    protected List<Setting<?>> properties() {
        ArrayList rows = new ArrayList();
        List<CanvasBoard.Canvas> all = CanvasBoard.all();
        if (this.draft == null && !all.isEmpty()) {
            this.draft = all.get(0).copy();
            this.selected = this.draft.id;
        }
        LinkedHashMap<CallSite, String> names = new LinkedHashMap<CallSite, String>();
        for (int i = 0; i < all.size(); ++i) {
            names.put((CallSite)((Object)(i + 1 + ". " + all.get((int)i).name)), all.get((int)i).id);
        }
        if (!names.isEmpty()) {
            String chosen = names.entrySet().stream().filter(e -> ((String)e.getValue()).equals(this.selected)).map(Map.Entry::getKey).findFirst().orElse((String)names.keySet().iterator().next());
            rows.add((Setting<?>)new ModeSetting("\u0425\u043e\u043b\u0441\u0442", chosen, (String[])names.keySet().toArray(String[]::new)).bind(() -> chosen, v -> {
                String id = (String)names.get(v);
                for (CanvasBoard.Canvas c : CanvasBoard.all()) {
                    if (!c.id.equals(id)) continue;
                    this.select(c);
                    break;
                }
            }));
        }
        if (this.draft != null) {
            rows.add((Setting<?>)new StringSetting("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435", this.draft.name, "\u0418\u043c\u044f \u0445\u043e\u043b\u0441\u0442\u0430", 80).bind(() -> this.draft.name, v -> {
                this.draft.name = v;
                this.dirty = true;
            }));
            rows.add(this.number("\u041f\u043e\u0437\u0438\u0446\u0438\u044f X", this.draft.x - 128.0, this.draft.x + 128.0, 0.05, () -> this.draft.x, v -> {
                this.draft.x = v;
            }));
            rows.add(this.number("\u041f\u043e\u0437\u0438\u0446\u0438\u044f Y", this.draft.y - 128.0, this.draft.y + 128.0, 0.05, () -> this.draft.y, v -> {
                this.draft.y = v;
            }));
            rows.add(this.number("\u041f\u043e\u0437\u0438\u0446\u0438\u044f Z", this.draft.z - 128.0, this.draft.z + 128.0, 0.05, () -> this.draft.z, v -> {
                this.draft.z = v;
            }));
            rows.add(this.number("\u0428\u0438\u0440\u0438\u043d\u0430", 0.1, 128.0, 0.1, () -> this.draft.width, v -> {
                double ratio = this.draft.height / this.draft.width;
                this.draft.width = v;
                this.draft.height = Math.min(128.0, Math.max(0.1, v * ratio));
            }));
            rows.add(this.number("\u0412\u044b\u0441\u043e\u0442\u0430", 0.1, 128.0, 0.1, () -> this.draft.height, v -> {
                double ratio = this.draft.width / this.draft.height;
                this.draft.height = v;
                this.draft.width = Math.min(128.0, Math.max(0.1, v * ratio));
            }));
            rows.add(this.number("\u041f\u043e\u0432\u043e\u0440\u043e\u0442", -180.0, 180.0, 1.0, () -> this.draft.yaw, v -> {
                this.draft.yaw = v;
            }));
            rows.add(this.number("\u041d\u0430\u043a\u043b\u043e\u043d", -90.0, 90.0, 1.0, () -> this.draft.pitch, v -> {
                this.draft.pitch = v;
            }));
            rows.add(this.number("\u041d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 0.0, 100.0, 1.0, () -> this.draft.opacity * 100.0, v -> {
                this.draft.opacity = v / 100.0;
            }));
            rows.add((Setting<?>)new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0443 \u043c\u0435\u043d\u044f", true).bind(() -> !CanvasBoard.hidden.contains(this.selected), v -> {
                if (v.booleanValue() == CanvasBoard.hidden.contains(this.selected)) {
                    CanvasBoard.toggleHidden(this.selected);
                }
            }));
            rows.add(new ButtonSetting("\u041f\u043b\u043e\u0442\u043d\u043e\u0441\u0442\u044c", "\u0421\u0434\u0435\u043b\u0430\u0442\u044c \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u044b\u043c", () -> {
                this.draft.opacity = 1.0;
                this.dirty = true;
                Module m = ModuleManager.byId("builder_canvas");
                if (m != null) {
                    for (Setting<?> st : m.getSettings()) {
                        if (!st.getName().equals("\u041e\u0431\u0449\u0430\u044f \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c")) continue;
                        ((NumberSetting)st).set(100.0);
                    }
                }
                this.commit();
            }));
            rows.add(new ButtonSetting("\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u041f\u0435\u0440\u0435\u043d\u0435\u0441\u0442\u0438 \u043a \u043f\u0440\u0438\u0446\u0435\u043b\u0443", () -> {
                this.place(this.draft);
                this.dirty = true;
                this.commit();
            }));
            rows.add(new ButtonSetting("\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c", "\u041e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f", this::commit));
            rows.add(new ButtonSetting("\u0423\u0434\u0430\u043b\u0438\u0442\u044c", "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043e\u0431\u0449\u0438\u0439 \u0445\u043e\u043b\u0441\u0442", this::deleteAction));
        }
        rows.add(new ButtonSetting("\u041d\u043e\u0432\u0430\u044f \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", "\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u0444\u0430\u0439\u043b\u2026", this::choose));
        rows.add((Setting<?>)new StringSetting("\u041f\u0443\u0442\u044c \u043a \u0444\u0430\u0439\u043b\u0443", this.path, "PNG / JPG", 1024).bind(() -> this.path, v -> {
            this.path = v;
        }));
        rows.add(new ButtonSetting("\u0418\u043c\u043f\u043e\u0440\u0442", "\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043f\u043e \u043f\u0443\u0442\u0438", () -> this.load(this.path)));
        rows.add(new ButtonSetting("\u0412\u0435\u0440\u043d\u0443\u0442\u044c \u0443\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0439", "\u0412\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0439 \u0445\u043e\u043b\u0441\u0442", this::undoAction));
        return rows;
    }

    private void commit() {
        if (!this.dirty || this.draft == null || this.pending) {
            return;
        }
        CanvasBoard.Canvas now = CanvasBoard.all().stream().filter(c -> c.id.equals(this.selected)).findFirst().orElse(null);
        if (now == null) {
            this.status = "\u0425\u043e\u043b\u0441\u0442 \u0443\u0434\u0430\u043b\u0451\u043d \u0434\u0440\u0443\u0433\u0438\u043c \u0438\u0433\u0440\u043e\u043a\u043e\u043c";
            return;
        }
        if (now.revision != this.draft.revision) {
            this.select(now);
            this.status = "\u0425\u043e\u043b\u0441\u0442 \u0438\u0437\u043c\u0435\u043d\u0451\u043d \u0434\u0440\u0443\u0433\u0438\u043c \u0438\u0433\u0440\u043e\u043a\u043e\u043c \u2014 \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u0430 \u043d\u043e\u0432\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f";
            return;
        }
        this.pending = true;
        this.expected = this.draft.revision + (long)(AstraOnline.connected() && CanvasBoard.scope().startsWith("server:") ? 1 : 0);
        this.dirty = false;
        CanvasBoard.put(this.draft.copy());
    }

    public void method_25393() {
        List<CanvasBoard.Canvas> all = CanvasBoard.all();
        String ids = all.stream().map(c -> c.id).reduce("", (a, b) -> a + b);
        if (!this.wanted.isEmpty()) {
            for (CanvasBoard.Canvas c2 : all) {
                if (!c2.id.equals(this.wanted)) continue;
                this.wanted = "";
                this.select(c2);
                break;
            }
        }
        if (this.pending) {
            for (CanvasBoard.Canvas c2 : all) {
                if (!c2.id.equals(this.selected) || c2.revision < this.expected) continue;
                this.draft.revision = c2.revision;
                this.pending = false;
                if (!this.dirty) break;
                this.commit();
                break;
            }
        }
        if (!ids.equals(this.fingerprint)) {
            this.fingerprint = ids;
            if (all.stream().noneMatch(c -> c.id.equals(this.selected))) {
                this.draft = null;
                this.selected = "";
            }
            this.rebuildRows();
        }
        if (!CanvasBoard.status.isEmpty()) {
            this.status = CanvasBoard.status;
        }
    }

    private void choose() {
        CompletableFuture.supplyAsync(() -> TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Astra image", (CharSequence)"", (PointerBuffer)null, null, (boolean)false)).thenAccept(f -> this.field_22787.execute(() -> {
            if (f != null) {
                this.path = f;
                this.load((String)f);
            }
        }));
    }

    private void load(String file) {
        if (this.busy || this.field_22787.field_1724 == null) {
            return;
        }
        if ((file = file.trim()).startsWith("\"") && file.endsWith("\"")) {
            file = file.substring(1, file.length() - 1);
        }
        String chosen = file;
        String scope = CanvasBoard.scope();
        this.busy = true;
        CompletableFuture.supplyAsync(() -> {
            try {
                return CanvasBoard.importImage(Path.of(chosen, new String[0]));
            }
            catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenComplete((c, error) -> this.field_22787.execute(() -> {
            this.busy = false;
            if (error != null) {
                this.status = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0443";
                return;
            }
            if (!scope.equals(CanvasBoard.scope()) || this.field_22787.field_1724 == null) {
                this.status = "\u041c\u0438\u0440 \u0438\u0437\u043c\u0435\u043d\u0438\u043b\u0441\u044f \u2014 \u043f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u0435 \u0438\u043c\u043f\u043e\u0440\u0442";
                return;
            }
            this.place((CanvasBoard.Canvas)c);
            this.wanted = c.id;
            CanvasBoard.put(c);
        }));
    }

    private void place(CanvasBoard.Canvas c) {
        HitResult hit = this.field_22787.field_1765;
        Vec3 p = hit != null && hit.method_17783() != HitResult.Type.field_1333 ? hit.method_17784() : this.field_22787.field_1724.method_33571().method_1019(this.field_22787.field_1724.method_5828(1.0f).method_1021(4.0));
        c.x = p.field_1352;
        c.y = p.field_1351;
        c.z = p.field_1350;
        c.yaw = Mth.method_15393((float)(180.0f - this.field_22787.field_1724.method_36454()));
        c.pitch = 0.0;
        if (hit instanceof BlockHitResult) {
            BlockHitResult b = (BlockHitResult)hit;
            Direction side = b.method_17780();
            Vec3 n = side.method_62676();
            c.x += n.field_1352 * 0.015;
            c.y += n.field_1351 * 0.015;
            c.z += n.field_1350 * 0.015;
            switch (side) {
                case field_11036: {
                    c.pitch = -90.0;
                    break;
                }
                case field_11033: {
                    c.pitch = 90.0;
                    break;
                }
                case field_11043: {
                    c.yaw = 180.0;
                    break;
                }
                case field_11035: {
                    c.yaw = 0.0;
                    break;
                }
                case field_11034: {
                    c.yaw = 90.0;
                    break;
                }
                case field_11039: {
                    c.yaw = -90.0;
                }
            }
        }
    }

    @Override
    protected void workspace(GuiGraphics ctx, int mx, int my, float delta) {
        if (this.draft == null) {
            return;
        }
        Identifier tex = CanvasBoard.texture(this.draft);
        if (tex == null) {
            return;
        }
        float availW = (float)this.field_22789 - this.workspaceLeft() - 40.0f * this.s;
        float availH = (float)this.field_22790 - 110.0f * this.s;
        float aspect = (float)(this.draft.width / this.draft.height);
        float w = Math.min(availW, availH * aspect);
        float h = w / aspect;
        float x = this.workspaceLeft() + ((float)this.field_22789 - this.workspaceLeft() - w) / 2.0f;
        float y = 65.0f * this.s + (availH - h) / 2.0f;
        Theme t = ThemeManager.get();
        Render2D.roundRect(ctx, x - 5.0f * this.s, y - 5.0f * this.s, w + 10.0f * this.s, h + 10.0f * this.s, 7.0f * this.s, ColorUtil.withAlpha(t.bg2(), 0.9f));
        Render2D.texture(ctx, tex, x, y, w, h, 0.0f, 0.0f, 1.0f, 1.0f, ColorUtil.withAlpha(-1, (float)this.draft.opacity));
        Render2D.outline(ctx, x - 5.0f * this.s, y - 5.0f * this.s, w + 10.0f * this.s, h + 10.0f * this.s, 7.0f * this.s, this.s, t.accent());
    }

    @Override
    protected boolean workspaceClick(double x, double y, int b) {
        return false;
    }

    @Override
    protected boolean workspaceDrag(double x, double y, int b) {
        return false;
    }

    @Override
    protected boolean workspaceRelease() {
        return false;
    }

    @Override
    protected void finishChange() {
        this.commit();
    }

    @Override
    protected void deleteAction() {
        if (this.draft != null) {
            this.deleted = this.draft.copy();
            CanvasBoard.delete(this.draft);
            this.draft = null;
            this.selected = "";
            this.dirty = false;
            this.pending = false;
            this.rebuildRows();
        }
    }

    @Override
    protected void undoAction() {
        if (this.deleted != null) {
            CanvasBoard.Canvas c = this.deleted.copy();
            c.id = UUID.randomUUID().toString();
            c.revision = 0L;
            this.wanted = c.id;
            CanvasBoard.put(c);
            this.deleted = null;
        }
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25419() {
        this.commit();
        this.field_22787.method_1507(this.parent);
    }
}

