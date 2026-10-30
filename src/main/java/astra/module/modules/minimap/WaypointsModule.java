/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.blockentity.BeaconRenderer
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 *  org.joml.Quaternionfc
 */
package astra.module.modules.minimap;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.ThemeManager;
import astra.waypoint.Waypoints;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

public class WaypointsModule
extends Module {
    private static final int[] PALETTE = new int[]{-45608, -11939841, -11870592, -340971, -5745153, -30147, -770210};
    private final BooleanSetting beam = this.add(new BooleanSetting("\u041b\u0443\u0447", true));
    private final BooleanSetting label = this.add(new BooleanSetting("\u041f\u043e\u0434\u043f\u0438\u0441\u044c", true));
    private final BooleanSetting distance = this.add(new BooleanSetting("\u0420\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435", true));
    private final NumberSetting labelSize = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u043f\u043e\u0434\u043f\u0438\u0441\u0438", 100.0, 50.0, 200.0, 10.0).unit("%"));
    private final NumberSetting maxDistance = this.add(new NumberSetting("\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u043f\u043e\u043a\u0430\u0437\u0430", 5000.0, 100.0, 30000.0, 100.0).unit(" \u0431\u043b."));
    private final BooleanSetting onMinimap = this.add(new BooleanSetting("\u041d\u0430 \u043c\u0438\u043d\u0438\u043a\u0430\u0440\u0442\u0435", true));
    private final BooleanSetting deathPoint = this.add(new BooleanSetting("\u0422\u043e\u0447\u043a\u0430 \u0441\u043c\u0435\u0440\u0442\u0438", true));
    private final ModeSetting colorMode = this.add(new ModeSetting("\u0426\u0432\u0435\u0442 \u043d\u043e\u0432\u044b\u0445 \u0442\u043e\u0447\u0435\u043a", "\u041f\u043e \u043e\u0447\u0435\u0440\u0435\u0434\u0438", "\u041f\u043e \u043e\u0447\u0435\u0440\u0435\u0434\u0438", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b"));
    private final KeybindSetting addKey = this.add(new KeybindSetting("\u041f\u043e\u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0442\u043e\u0447\u043a\u0443", 78));
    private final KeybindSetting removeKey = this.add(new KeybindSetting("\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0431\u043b\u0438\u0436\u0430\u0439\u0448\u0443\u044e", -1));
    private boolean addWasDown;
    private boolean removeWasDown;
    private boolean wasDead;

    public WaypointsModule() {
        super("waypoints", "\u0422\u043e\u0447\u043a\u0438 \u043c\u0430\u0440\u0448\u0440\u0443\u0442\u0430", "\u041c\u0435\u0442\u043a\u0438 \u0432 \u043c\u0438\u0440\u0435 \u0438 \u043d\u0430 \u043c\u0438\u043d\u0438\u043a\u0430\u0440\u0442\u0435, \u0442\u043e\u0447\u043a\u0430 \u0441\u043c\u0435\u0440\u0442\u0438, \u043a\u043e\u043c\u0430\u043d\u0434\u044b /wp.", Category.MINIMAP, Icon.PIN);
        this.setDefaultEnabled(true);
    }

    public static WaypointsModule get() {
        return ModuleManager.get(WaypointsModule.class);
    }

    public boolean showOnMinimap() {
        return this.isEnabled() && (Boolean)this.onMinimap.get() != false;
    }

    private int nextColor() {
        if (this.colorMode.is("\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b")) {
            return ThemeManager.get().accent();
        }
        return PALETTE[Waypoints.all().size() % PALETTE.length];
    }

    private static void say(String text) {
        if (WaypointsModule.mc.field_1705 != null) {
            WaypointsModule.mc.field_1705.method_1743().method_1812((Component)Component.method_43470((String)"\u2726 ").method_54663(16731608).method_10852((Component)Component.method_43470((String)text).method_54663(15261170)));
        }
    }

    public Waypoints.Waypoint addHere(String name) {
        if (WaypointsModule.mc.field_1724 == null) {
            return null;
        }
        Waypoints.Waypoint w = Waypoints.add(name, WaypointsModule.mc.field_1724.method_31477(), WaypointsModule.mc.field_1724.method_31478(), WaypointsModule.mc.field_1724.method_31479(), this.nextColor(), false);
        if (w != null) {
            WaypointsModule.say("\u0422\u043e\u0447\u043a\u0430 \u00ab" + w.name + "\u00bb \u043f\u043e\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0430: " + w.x + " " + w.y + " " + w.z);
        }
        return w;
    }

    private Waypoints.Waypoint nearest() {
        if (WaypointsModule.mc.field_1724 == null) {
            return null;
        }
        Waypoints.Waypoint best = null;
        double bestD = Double.MAX_VALUE;
        for (Waypoints.Waypoint w : Waypoints.here()) {
            double d = WaypointsModule.mc.field_1724.method_5649((double)w.x + 0.5, (double)w.y, (double)w.z + 0.5);
            if (!(d < bestD)) continue;
            bestD = d;
            best = w;
        }
        return best;
    }

    @Override
    public void onTick() {
        Waypoints.Waypoint w;
        boolean rem;
        boolean add;
        if (WaypointsModule.mc.field_1724 == null) {
            return;
        }
        Window window = mc.method_22683();
        boolean canUse = WaypointsModule.mc.field_1755 == null;
        boolean bl = add = canUse && this.addKey.isBound() && InputConstants.method_15987((Window)window, (int)this.addKey.getKey());
        if (add && !this.addWasDown) {
            this.addHere(Waypoints.nextName());
        }
        this.addWasDown = add;
        boolean bl2 = rem = canUse && this.removeKey.isBound() && InputConstants.method_15987((Window)window, (int)this.removeKey.getKey());
        if (rem && !this.removeWasDown && (w = this.nearest()) != null && Waypoints.remove(w.name)) {
            WaypointsModule.say("\u0422\u043e\u0447\u043a\u0430 \u00ab" + w.name + "\u00bb \u0443\u0434\u0430\u043b\u0435\u043d\u0430");
        }
        this.removeWasDown = rem;
        boolean dead = WaypointsModule.mc.field_1724.method_29504();
        if (dead && !this.wasDead && ((Boolean)this.deathPoint.get()).booleanValue()) {
            Waypoints.add("\u0421\u043c\u0435\u0440\u0442\u044c", WaypointsModule.mc.field_1724.method_31477(), WaypointsModule.mc.field_1724.method_31478(), WaypointsModule.mc.field_1724.method_31479(), -770210, true);
            WaypointsModule.say("\u041c\u0435\u0441\u0442\u043e \u0441\u043c\u0435\u0440\u0442\u0438 \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u043e: " + WaypointsModule.mc.field_1724.method_31477() + " " + WaypointsModule.mc.field_1724.method_31478() + " " + WaypointsModule.mc.field_1724.method_31479());
        }
        this.wasDead = dead;
    }

    public void renderWorld(WorldRenderContext ctx) {
        if (WaypointsModule.mc.field_1724 == null || WaypointsModule.mc.field_1687 == null || WaypointsModule.mc.field_1690.field_1842) {
            return;
        }
        List<Waypoints.Waypoint> list = Waypoints.here();
        if (list.isEmpty()) {
            return;
        }
        Camera camera = Minecraft.method_1551().field_1773.method_19418();
        Vec3 cam = camera.method_71156();
        MultiBufferSource.BufferSource imm = mc.method_22940().method_23000();
        float td = Minecraft.method_1551().method_61966().method_60637(false);
        long time = WaypointsModule.mc.field_1687.method_75260();
        double maxD = (Double)this.maxDistance.get();
        int beamLimit = WaypointsModule.mc.field_1690.method_38521() * 16;
        for (Waypoints.Waypoint w : list) {
            double dx = (double)w.x + 0.5 - cam.field_1352;
            double dz = (double)w.z + 0.5 - cam.field_1350;
            double dy = (double)w.y + 0.5 - cam.field_1351;
            double horiz = Math.sqrt(dx * dx + dz * dz);
            double dist = Math.sqrt(horiz * horiz + dy * dy);
            if (dist > maxD || !((Boolean)this.beam.get()).booleanValue() || !(horiz < (double)beamLimit) || !(horiz > 1.5)) continue;
            WaypointsModule.drawBeam((MultiBufferSource)imm, (float)((double)w.x + 0.5 - cam.field_1352), (float)((double)WaypointsModule.mc.field_1687.method_31607() - cam.field_1351), (float)((double)w.z + 0.5 - cam.field_1350), WaypointsModule.mc.field_1687.method_31605(), w.color, (float)time + td);
        }
        imm.method_22993();
        if (!((Boolean)this.label.get()).booleanValue()) {
            return;
        }
        Font tr = WaypointsModule.mc.field_1772;
        for (Waypoints.Waypoint w : list) {
            double dx = (double)w.x + 0.5 - cam.field_1352;
            double dy = (double)w.y + 1.2 - cam.field_1351;
            double dz = (double)w.z + 0.5 - cam.field_1350;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > maxD || dist < 0.5) continue;
            double place = Math.min(dist, 12.0);
            double k = place / dist;
            float scale = (float)(0.025 * (double)this.labelSize.getFloat() / 100.0 * Math.max(place, 3.0) / 6.0);
            Matrix4f mat = new Matrix4f().translate((float)(dx * k), (float)(dy * k), (float)(dz * k)).rotate((Quaternionfc)camera.method_23767()).scale(scale, -scale, scale);
            String title = w.name;
            String sub = (Boolean)this.distance.get() != false ? WaypointsModule.formatDistance(dist) : null;
            int bg = ColorUtil.withAlpha(-15464420, 0.55f);
            float tw = tr.method_1727(title);
            tr.method_27521(title, -tw / 2.0f, sub != null ? -10.0f : -4.0f, w.color | 0xFF000000, false, mat, (MultiBufferSource)imm, Font.DisplayMode.field_33994, bg, 0xF000F0);
            if (sub == null) continue;
            float sw = tr.method_1727(sub);
            tr.method_27521(sub, -sw / 2.0f, 1.0f, -1, false, mat, (MultiBufferSource)imm, Font.DisplayMode.field_33994, bg, 0xF000F0);
        }
        imm.method_22993();
    }

    private static void drawBeam(MultiBufferSource consumers, float x, float y, float z, int height, int color, float time) {
        VertexConsumer vc = consumers.method_73477(RenderTypes.method_75988((Identifier)BeaconRenderer.field_4338, (boolean)true));
        float scroll = -(time * 0.025f) % 1.0f;
        float v1 = (float)height * 0.5f + scroll;
        WaypointsModule.beamPrism(vc, x, y, z, height, 0.12f, ColorUtil.withAlpha(color, 0.85f), scroll, v1, time * 2.25f);
        WaypointsModule.beamPrism(vc, x, y, z, height, 0.18f, ColorUtil.withAlpha(color, 0.25f), scroll, v1, -time * 1.5f);
    }

    private static void beamPrism(VertexConsumer vc, float x, float y, float z, int height, float r, int color, float v0, float v1, float spin) {
        double a = Math.toRadians(spin);
        float[] px = new float[4];
        float[] pz = new float[4];
        for (int i = 0; i < 4; ++i) {
            px[i] = x + (float)Math.cos(a + (double)i * Math.PI / 2.0) * r;
            pz[i] = z + (float)Math.sin(a + (double)i * Math.PI / 2.0) * r;
        }
        float top = y + (float)height;
        int light = 0xF000F0;
        for (int i = 0; i < 4; ++i) {
            int j = (i + 1) % 4;
            vc.method_22912(px[i], top, pz[i]).method_39415(color).method_22913(1.0f, v1).method_22922(OverlayTexture.field_21444).method_60803(light).method_22914(0.0f, 1.0f, 0.0f);
            vc.method_22912(px[i], y, pz[i]).method_39415(color).method_22913(1.0f, v0).method_22922(OverlayTexture.field_21444).method_60803(light).method_22914(0.0f, 1.0f, 0.0f);
            vc.method_22912(px[j], y, pz[j]).method_39415(color).method_22913(0.0f, v0).method_22922(OverlayTexture.field_21444).method_60803(light).method_22914(0.0f, 1.0f, 0.0f);
            vc.method_22912(px[j], top, pz[j]).method_39415(color).method_22913(0.0f, v1).method_22922(OverlayTexture.field_21444).method_60803(light).method_22914(0.0f, 1.0f, 0.0f);
        }
    }

    private static String formatDistance(double d) {
        return d >= 1000.0 ? String.format(Locale.ROOT, "%.1f \u043a\u043c", d / 1000.0) : Math.round(d) + " \u043c";
    }

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)ClientCommandManager.literal((String)"wp").executes(c -> WaypointsModule.list())).then(((LiteralArgumentBuilder)ClientCommandManager.literal((String)"add").executes(c -> WaypointsModule.add(Waypoints.nextName()))).then(ClientCommandManager.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).executes(c -> WaypointsModule.add(StringArgumentType.getString((CommandContext)c, (String)"name")))))).then(ClientCommandManager.literal((String)"remove").then(ClientCommandManager.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).suggests((c, b) -> {
            for (Waypoints.Waypoint w : Waypoints.all()) {
                b.suggest(w.name);
            }
            return b.buildFuture();
        }).executes(c -> {
            String name = StringArgumentType.getString((CommandContext)c, (String)"name");
            WaypointsModule.say(Waypoints.remove(name) ? "\u0422\u043e\u0447\u043a\u0430 \u00ab" + name + "\u00bb \u0443\u0434\u0430\u043b\u0435\u043d\u0430" : "\u0422\u043e\u0447\u043a\u0438 \u00ab" + name + "\u00bb \u043d\u0435\u0442");
            return 1;
        })))).then(ClientCommandManager.literal((String)"list").executes(c -> WaypointsModule.list()))).then(ClientCommandManager.literal((String)"clear").executes(c -> {
            WaypointsModule.say("\u0423\u0434\u0430\u043b\u0435\u043d\u043e \u0442\u043e\u0447\u0435\u043a: " + Waypoints.clear());
            return 1;
        }))).then(ClientCommandManager.literal((String)"share").then(ClientCommandManager.argument((String)"name", (ArgumentType)StringArgumentType.greedyString()).suggests((c, b) -> {
            for (Waypoints.Waypoint w : Waypoints.all()) {
                b.suggest(w.name);
            }
            return b.buildFuture();
        }).executes(c -> {
            Waypoints.Waypoint w = Waypoints.find(StringArgumentType.getString((CommandContext)c, (String)"name"));
            if (w == null) {
                WaypointsModule.say("\u0422\u0430\u043a\u043e\u0439 \u0442\u043e\u0447\u043a\u0438 \u043d\u0435\u0442");
            } else if (mc.method_1562() != null) {
                mc.method_1562().method_45729(w.name + ": " + w.x + " " + w.y + " " + w.z);
            }
            return 1;
        }))));
    }

    private static int add(String name) {
        WaypointsModule m = WaypointsModule.get();
        if (m != null) {
            m.addHere(name);
        }
        return 1;
    }

    private static int list() {
        List<Waypoints.Waypoint> all = Waypoints.all();
        if (all.isEmpty()) {
            WaypointsModule.say("\u0422\u043e\u0447\u0435\u043a \u043f\u043e\u043a\u0430 \u043d\u0435\u0442. \u041f\u043e\u0441\u0442\u0430\u0432\u044c: \u043a\u043b\u0430\u0432\u0438\u0448\u0430 N \u0438\u043b\u0438 /wp add <\u0438\u043c\u044f>");
            return 1;
        }
        String dim = Waypoints.dimension();
        for (Waypoints.Waypoint w : all) {
            String where = w.dimension.equals(dim) ? "" : "  (" + w.dimension.replace("minecraft:", "") + ")";
            Object dist = "";
            if (WaypointsModule.mc.field_1724 != null && w.dimension.equals(dim)) {
                dist = " \u00b7 " + WaypointsModule.formatDistance(Math.sqrt(WaypointsModule.mc.field_1724.method_5649((double)w.x + 0.5, (double)w.y, (double)w.z + 0.5)));
            }
            WaypointsModule.say(w.name + ": " + w.x + " " + w.y + " " + w.z + (String)dist + where);
        }
        return 1;
    }
}

