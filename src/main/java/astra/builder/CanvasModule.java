/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommands
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
 *  net.minecraft.client.gui.screens.Screen
 *  astra.render.WorldBuffers$BufferSource
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package astra.builder;

import astra.builder.CanvasBoard;
import astra.builder.CanvasRender;
import astra.builder.CanvasScreen;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.NumberSetting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.gui.screens.Screen;
import astra.render.WorldBuffers;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public final class CanvasModule
extends Module {
    private final BooleanSetting show = this.add(new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0445\u043e\u043b\u0441\u0442\u044b", true));
    private final BooleanSetting through = this.add(new BooleanSetting("\u041f\u043e\u0432\u0435\u0440\u0445 \u0431\u043b\u043e\u043a\u043e\u0432", false));
    private final NumberSetting opacity = this.add(new NumberSetting("\u041e\u0431\u0449\u0430\u044f \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", 100.0, 0.0, 100.0, 5.0).unit("%"));
    private final NumberSetting distance = this.add(new NumberSetting("\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0445\u043e\u043b\u0441\u0442\u043e\u0432", 128.0, 16.0, 512.0, 16.0).unit(" \u0431\u043b."));

    public CanvasModule() {
        super("builder_canvas", "\u0425\u043e\u043b\u0441\u0442 Astra", "\u041e\u0431\u0449\u0438\u0435 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438-\u0440\u0435\u0444\u0435\u0440\u0435\u043d\u0441\u044b \u0432 \u043c\u0438\u0440\u0435. /canvas \u2014 \u0440\u0435\u0434\u0430\u043a\u0442\u043e\u0440.", Category.MINIMAP, Icon.PIN);
        this.add(new ButtonSetting("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u0445\u043e\u043b\u0441\u0442\u043e\u0432", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u0445\u043e\u043b\u0441\u0442\u044b", () -> mc.gui.setScreen((Screen)new CanvasScreen(CanvasModule.mc.gui.screen()))));
        this.add(new ButtonSetting("\u0421\u0435\u0440\u0432\u0435\u0440 \u0445\u043e\u043b\u0441\u0442\u043e\u0432", "\u0410\u0434\u0440\u0435\u0441 \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f", () -> {}).label(() -> CanvasBoard.scope().isEmpty() ? "\u041d\u0435\u0442 \u0438\u0433\u0440\u043e\u0432\u043e\u0433\u043e \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f" : CanvasBoard.scope()));
        this.setDefaultEnabled(true);
    }

    public static void init() {
        CanvasRender.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> CanvasBoard.tick());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register((LiteralArgumentBuilder)ClientCommands.literal((String)"canvas").executes(c -> {
            mc.execute(() -> mc.gui.setScreen((Screen)new CanvasScreen(null)));
            return 1;
        })));
        LevelRenderEvents.COLLECT_SUBMITS.register(ctx -> {
            CanvasModule m = ModuleManager.get(CanvasModule.class);
            if (m != null && m.isEnabled()) {
                m.draw(ctx);
            }
        });
    }

    private void draw(LevelRenderContext ctx) {
        if (CanvasModule.mc.level == null || CanvasModule.mc.player == null || !((Boolean)this.show.get()).booleanValue() || CanvasModule.mc.gui.hud.isHidden()) {
            return;
        }
        Vec3 cam = CanvasModule.mc.gameRenderer.mainCamera().position();
        WorldBuffers.BufferSource imm = WorldBuffers.forContext(ctx);
        for (CanvasBoard.Canvas c : CanvasBoard.all()) {
            float[] p;
            int j;
            Identifier tex;
            float alpha;
            if (CanvasBoard.hidden.contains(c.id) || cam.distanceToSqr(c.x, c.y, c.z) > (Double)this.distance.get() * (Double)this.distance.get() || (double)(alpha = (float)(c.opacity * (Double)this.opacity.get() / 100.0)) <= 0.003 || (tex = CanvasBoard.texture(c)) == null) continue;
            RenderType layer = CanvasRender.layer(tex, (Boolean)this.through.get() != false && alpha < 0.999f, alpha >= 0.999f);
            VertexConsumer vc = imm.getBuffer(layer);
            PoseStack ms = new PoseStack();
            ms.translate(c.x - cam.x, c.y - cam.y, c.z - cam.z);
            ms.mulPose((Quaternionfc)new Quaternionf().rotateY((float)Math.toRadians(c.yaw)).rotateX((float)Math.toRadians(c.pitch)));
            float w = (float)c.width / 2.0f;
            float h = (float)c.height / 2.0f;
            float[][] pts = new float[][]{{-w, h, 0.0f, 0.0f}, {-w, -h, 0.0f, 1.0f}, {w, -h, 1.0f, 1.0f}, {w, h, 1.0f, 0.0f}};
            int color = (int)(alpha * 255.0f) << 24 | 0xFFFFFF;
            for (j = 0; j < 4; ++j) {
                p = pts[j];
                vc.addVertex(ms.last(), p[0], p[1], 0.0f).setColor(color).setUv(p[2], p[3]);
            }
            imm.endBatch(layer);
            if (!((Boolean)this.through.get()).booleanValue() || !(alpha >= 0.999f)) continue;
            layer = CanvasRender.layer(tex, true, false);
            vc = imm.getBuffer(layer);
            for (j = 0; j < 4; ++j) {
                p = pts[j];
                vc.addVertex(ms.last(), p[0], p[1], 0.0f).setColor(color).setUv(p[2], p[3]);
            }
            imm.endBatch(layer);
        }
        imm.endBatch();
    }
}

