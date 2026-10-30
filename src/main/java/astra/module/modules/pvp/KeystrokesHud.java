/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.pvp;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.gui.components.UiDraw;
import astra.hud.HudModule;
import astra.module.Category;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.theme.Theme;
import astra.utils.ClickTracker;
import astra.utils.FrameStats;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public class KeystrokesHud
extends HudModule {
    private static final float KEY = 18.0f;
    private static final float GAP = 2.0f;
    private final BooleanSetting mouse = this.add(new BooleanSetting("\u041a\u043d\u043e\u043f\u043a\u0438 \u043c\u044b\u0448\u0438", true));
    private final BooleanSetting showCps = this.add((BooleanSetting)new BooleanSetting("CPS \u043d\u0430 \u043a\u043d\u043e\u043f\u043a\u0430\u0445", true).visibleWhen(() -> (Boolean)this.mouse.get()));
    private final BooleanSetting shift = this.add(new BooleanSetting("Shift", true));
    private final BooleanSetting space = this.add(new BooleanSetting("\u041f\u0440\u043e\u0431\u0435\u043b", false));
    private final Animation[] anim = new Animation[8];

    public KeystrokesHud() {
        super("keystrokes", "\u041a\u0435\u0439\u0441\u0442\u0440\u043e\u043a\u0438", "\u041d\u0430\u0436\u0430\u0442\u0438\u044f W, A, S, D, Shift, \u043f\u0440\u043e\u0431\u0435\u043b\u0430 \u0438 \u043a\u043d\u043e\u043f\u043e\u043a \u043c\u044b\u0448\u0438.", Category.PVP, Icon.KEYBOARD, 0.9f, 0.82f);
        for (int i = 0; i < this.anim.length; ++i) {
            this.anim[i] = new Animation(0.0f, 18.0f);
        }
        this.setDefaultEnabled(true);
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    public float width() {
        return 58.0f;
    }

    @Override
    public float height() {
        float h = 38.0f;
        if (((Boolean)this.mouse.get()).booleanValue()) {
            h += 20.0f;
        }
        if (((Boolean)this.shift.get()).booleanValue()) {
            h += 14.0f;
        }
        if (((Boolean)this.space.get()).booleanValue()) {
            h += 11.0f;
        }
        return h;
    }

    private boolean pressed(KeyMapping key) {
        return KeystrokesHud.mc.field_1724 != null && key.method_1434();
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        float dt = FrameStats.delta();
        this.draw(ctx, KeystrokesHud.theme(), w, dt, new boolean[]{this.pressed(KeystrokesHud.mc.field_1690.field_1894), this.pressed(KeystrokesHud.mc.field_1690.field_1913), this.pressed(KeystrokesHud.mc.field_1690.field_1881), this.pressed(KeystrokesHud.mc.field_1690.field_1849), this.pressed(KeystrokesHud.mc.field_1690.field_1886), this.pressed(KeystrokesHud.mc.field_1690.field_1904), this.pressed(KeystrokesHud.mc.field_1690.field_1832), this.pressed(KeystrokesHud.mc.field_1690.field_1903)});
    }

    private void draw(GuiGraphics ctx, Theme t, float w, float dt, boolean[] state) {
        float[] p = new float[8];
        for (int i = 0; i < 8; ++i) {
            p[i] = this.anim[i].update(state[i] ? 1.0f : 0.0f, dt);
        }
        float y = 0.0f;
        UiDraw.keyCap(ctx, t, 20.0f, y, 18.0f, 18.0f, "W", p[0], 8.0f);
        UiDraw.keyCap(ctx, t, 0.0f, y += 20.0f, 18.0f, 18.0f, "A", p[1], 8.0f);
        UiDraw.keyCap(ctx, t, 20.0f, y, 18.0f, 18.0f, "S", p[2], 8.0f);
        UiDraw.keyCap(ctx, t, 40.0f, y, 18.0f, 18.0f, "D", p[3], 8.0f);
        y += 20.0f;
        if (((Boolean)this.mouse.get()).booleanValue()) {
            float mw = (w - 2.0f) / 2.0f;
            Object l = (Boolean)this.showCps.get() != false ? ClickTracker.leftCps() + " CPS" : "LMB";
            Object r = (Boolean)this.showCps.get() != false ? ClickTracker.rightCps() + " CPS" : "RMB";
            UiDraw.keyCap(ctx, t, 0.0f, y, mw, 18.0f, (String)l, p[4], (Boolean)this.showCps.get() != false ? 6.0f : 7.0f);
            UiDraw.keyCap(ctx, t, mw + 2.0f, y, mw, 18.0f, (String)r, p[5], (Boolean)this.showCps.get() != false ? 6.0f : 7.0f);
            y += 20.0f;
        }
        if (((Boolean)this.shift.get()).booleanValue()) {
            UiDraw.keyCap(ctx, t, 0.0f, y, w, 12.0f, "SHIFT", p[6], 6.5f);
            y += 14.0f;
        }
        if (((Boolean)this.space.get()).booleanValue()) {
            UiDraw.keyCap(ctx, t, 0.0f, y, w, 9.0f, "\u2014", p[7], 6.0f);
        }
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return KeystrokesHud.this.height() * 1.05f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                float s = 1.05f;
                Matrix3x2fStack m = ui.ctx.method_51448();
                m.pushMatrix();
                m.translate(x + (w - KeystrokesHud.this.width() * s) / 2.0f, y);
                m.scale(s, s);
                boolean live = mc.field_1724 != null;
                KeystrokesHud.this.draw(ui.ctx, ui.theme, KeystrokesHud.this.width(), ui.dt, new boolean[]{live && mc.field_1690.field_1894.method_1434(), live && mc.field_1690.field_1913.method_1434(), live && mc.field_1690.field_1881.method_1434(), live && mc.field_1690.field_1849.method_1434(), System.currentTimeMillis() - ClickTracker.lastClick(0) < 90L, System.currentTimeMillis() - ClickTracker.lastClick(1) < 90L, false, false});
                m.popMatrix();
            }
        };
    }
}

