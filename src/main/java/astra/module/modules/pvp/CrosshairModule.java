/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.phys.HitResult$Type
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.pvp;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.Module;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.utils.FrameStats;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.HitResult;
import org.joml.Matrix3x2fStack;

public class CrosshairModule
extends Module {
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "Astra Cross", "Astra Cross", "\u041a\u0440\u0435\u0441\u0442", "\u041a\u0440\u0435\u0441\u0442 + \u0442\u043e\u0447\u043a\u0430", "\u0422\u043e\u0447\u043a\u0430", "\u041a\u0440\u0443\u0433", "\u0421\u0430\u043a\u0443\u0440\u0430"));
    private final NumberSetting size = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440", 80.0, 30.0, 200.0, 5.0).unit("%"));
    private final NumberSetting thickness = this.add(new NumberSetting("\u0422\u043e\u043b\u0449\u0438\u043d\u0430", 2.0, 0.5, 4.0, 0.1));
    private final NumberSetting gap = this.add(new NumberSetting("\u0417\u0430\u0437\u043e\u0440", 2.0, 0.0, 8.0, 0.5));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442", -45608));
    private final BooleanSetting outline = this.add(new BooleanSetting("\u041e\u0431\u0432\u043e\u0434\u043a\u0430", true));
    private final BooleanSetting dynamic = this.add((BooleanSetting)new BooleanSetting("\u0414\u0438\u043d\u0430\u043c\u0438\u0447\u0435\u0441\u043a\u0438\u0439", true).desc("\u0420\u0430\u0441\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438 \u0438 \u0443\u0434\u0430\u0440\u0435"));
    private final BooleanSetting spin = this.add(new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0432\u0440\u0430\u0449\u0435\u043d\u0438\u044f", false));
    private final BooleanSetting cooldown = this.add(new BooleanSetting("\u0418\u043d\u0434\u0438\u043a\u0430\u0442\u043e\u0440 \u0430\u0442\u0430\u043a\u0438", true));
    private final BooleanSetting targetColor = this.add((BooleanSetting)new BooleanSetting("\u0426\u0432\u0435\u0442 \u043d\u0430 \u0446\u0435\u043b\u0438", true).desc("\u041c\u0435\u043d\u044f\u0435\u0442 \u0446\u0432\u0435\u0442, \u043a\u043e\u0433\u0434\u0430 \u043f\u043e\u0434 \u043f\u0440\u0438\u0446\u0435\u043b\u043e\u043c \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u044c"));
    private final BooleanSetting thirdPerson = this.add(new BooleanSetting("\u0412 \u0442\u0440\u0435\u0442\u044c\u0435\u043c \u043b\u0438\u0446\u0435", false));
    private final Animation spread = new Animation(0.0f, 10.0f);
    private final Animation onTarget = new Animation(0.0f, 12.0f);
    private float rotation;

    public CrosshairModule() {
        super("crosshair", "\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u043f\u0440\u0438\u0446\u0435\u043b", "\u0424\u043e\u0440\u043c\u044b, \u0446\u0432\u0435\u0442, \u0442\u043e\u043b\u0449\u0438\u043d\u0430 \u0438 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u043f\u0440\u0438\u0446\u0435\u043b\u0430.", Category.PVP, Icon.CROSSHAIR);
        this.setDefaultEnabled(true);
    }

    public void renderCrosshair(GuiGraphics ctx, DeltaTracker tick) {
        if (CrosshairModule.mc.player == null || CrosshairModule.mc.options.hideGui) {
            return;
        }
        if (!CrosshairModule.mc.options.getCameraType().isFirstPerson() && !((Boolean)this.thirdPerson.get()).booleanValue()) {
            return;
        }
        if (CrosshairModule.mc.player.isSpectator() && CrosshairModule.mc.crosshairPickEntity == null) {
            return;
        }
        float dt = FrameStats.delta();
        float target = 0.0f;
        if (((Boolean)this.dynamic.get()).booleanValue()) {
            double speed = CrosshairModule.mc.player.getDeltaMovement().horizontalDistance();
            target = (float)Math.min(1.0, speed * 6.0);
            if (CrosshairModule.mc.player.swinging) {
                target = Math.max(target, 0.8f);
            }
        }
        float sp = this.spread.update(target, dt);
        boolean entity = CrosshairModule.mc.hitResult != null && CrosshairModule.mc.hitResult.getType() == HitResult.Type.ENTITY;
        float tg = this.onTarget.update(entity && (Boolean)this.targetColor.get() != false ? 1.0f : 0.0f, dt);
        if (((Boolean)this.spin.get()).booleanValue()) {
            this.rotation = (this.rotation + dt * 90.0f) % 360.0f;
        }
        float cx = (float)ctx.guiWidth() / 2.0f;
        float cy = (float)ctx.guiHeight() / 2.0f;
        int col = ColorUtil.lerp(this.color.getColor(), -770210, tg);
        float cd = (Boolean)this.cooldown.get() != false ? CrosshairModule.mc.player.getAttackStrengthScale(0.0f) : 1.0f;
        this.draw(ctx, cx, cy, 1.0f, sp, col, (Boolean)this.spin.get() != false ? this.rotation : 0.0f, cd);
    }

    private void draw(GuiGraphics ctx, float cx, float cy, float k, float sp, int col, float rot, float cooldownProgress) {
        float s = this.size.getFloat() / 100.0f * k;
        float th = this.thickness.getFloat() * k;
        float g = (this.gap.getFloat() + sp * 3.0f) * k;
        float len = 5.0f * s;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(cx, cy);
        if (rot != 0.0f) {
            m.rotate((float)Math.toRadians(rot));
        }
        float px = Render2D.pixel(ctx);
        Mesh mesh = Render2D.begin();
        int out = -1342177280;
        String st = (String)this.style.get();
        boolean ol = (Boolean)this.outline.get();
        switch (st) {
            case "\u041a\u0440\u0435\u0441\u0442": 
            case "\u041a\u0440\u0435\u0441\u0442 + \u0442\u043e\u0447\u043a\u0430": 
            case "Astra Cross": {
                float[][] arms;
                for (float[] a : arms = new float[][]{{0.0f, -1.0f}, {0.0f, 1.0f}, {-1.0f, 0.0f}, {1.0f, 0.0f}}) {
                    float x1 = a[0] * g;
                    float y1 = a[1] * g;
                    float x2 = a[0] * (g + len);
                    float y2 = a[1] * (g + len);
                    if (ol) {
                        Render2D.addLine(mesh, x1 - a[0] * 0.6f, y1 - a[1] * 0.6f, x2 + a[0] * 0.6f, y2 + a[1] * 0.6f, th + 1.2f * k, out, false, px);
                    }
                    Render2D.addLine(mesh, x1, y1, x2, y2, th, col, st.equals("Astra Cross"), px);
                }
                if (st.equals("\u041a\u0440\u0435\u0441\u0442 + \u0442\u043e\u0447\u043a\u0430")) {
                    if (ol) {
                        Render2D.addCircle(mesh, 0.0f, 0.0f, th * 0.8f + 0.6f * k, Paint.solid(out), px);
                    }
                    Render2D.addCircle(mesh, 0.0f, 0.0f, th * 0.8f, Paint.solid(col), px);
                }
                if (!st.equals("Astra Cross")) break;
                Render2D.addSparkle(mesh, 0.0f, 0.0f, 2.2f * s + th * 0.3f, 45.0f, ColorUtil.withAlpha(-1, 0.95f), px);
                break;
            }
            case "\u0422\u043e\u0447\u043a\u0430": {
                if (ol) {
                    Render2D.addCircle(mesh, 0.0f, 0.0f, th + 0.7f * k, Paint.solid(out), px);
                }
                Render2D.addCircle(mesh, 0.0f, 0.0f, th, Paint.solid(col), px);
                break;
            }
            case "\u041a\u0440\u0443\u0433": {
                float r = 3.5f * s + g;
                if (ol) {
                    Render2D.addArc(mesh, 0.0f, 0.0f, r, th + 1.2f * k, 0.0f, 360.0f, out, px);
                }
                Render2D.addArc(mesh, 0.0f, 0.0f, r, th, 0.0f, 360.0f, col, px);
                Render2D.addCircle(mesh, 0.0f, 0.0f, th * 0.6f, Paint.solid(col), px);
                break;
            }
            default: {
                for (int i = 0; i < 4; ++i) {
                    float a = (float)Math.toRadians(i * 90);
                    float d = g + 2.5f * s;
                    Render2D.addPetal(mesh, (float)Math.sin(a) * d, -((float)Math.cos(a)) * d, 2.6f * s, i * 90 + 180, Paint.solid(col), px);
                }
                Render2D.addCircle(mesh, 0.0f, 0.0f, th * 0.55f, Paint.solid(-1), px);
            }
        }
        if (cooldownProgress < 1.0f) {
            float r = g + len + 3.5f * k;
            Render2D.addArc(mesh, 0.0f, 0.0f, r, 1.4f * k, -90.0f, 360.0f, ColorUtil.withAlpha(-16777216, 0.45f), px);
            Render2D.addArc(mesh, 0.0f, 0.0f, r, 1.4f * k, -90.0f, 360.0f * cooldownProgress, col, px);
        }
        Render2D.end(ctx);
        m.popMatrix();
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 56.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, Paint.vertical(y, y + h, ColorUtil.lerp(ui.theme.bg(), ui.theme.accent(), 0.18f), ColorUtil.lerp(ui.theme.bg(), -14724816, 0.35f)));
                Render2D.outline(ui.ctx, x, y, w, h, 5.0f, 0.7f, ColorUtil.withAlpha(ui.theme.accent(), 0.4f));
                if (((Boolean)CrosshairModule.this.spin.get()).booleanValue()) {
                    CrosshairModule.this.rotation = (CrosshairModule.this.rotation + ui.dt * 90.0f) % 360.0f;
                }
                float cd = (float)(System.currentTimeMillis() % 1600L) / 1600.0f;
                CrosshairModule.this.draw(ui.ctx, x + w / 2.0f, y + h / 2.0f, 2.2f, 0.0f, CrosshairModule.this.color.getColor(), (Boolean)CrosshairModule.this.spin.get() != false ? CrosshairModule.this.rotation : 0.0f, (Boolean)CrosshairModule.this.cooldown.get() != false ? cd : 1.0f);
            }
        };
    }
}

