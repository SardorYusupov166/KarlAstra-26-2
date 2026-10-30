/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.pvp;

import astra.animation.AnimationUtils;
import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.utils.CombatTracker;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2fStack;

public class ComboHud
extends HudModule {
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "Astra Anime", "Astra Anime", "\u041c\u0438\u043d\u0438\u043c\u0430\u043b"));
    private final ColorSetting numberColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 (\u0447\u0438\u0441\u043b\u043e)", -45608));
    private final ColorSetting textColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 (\u0442\u0435\u043a\u0441\u0442)", -1595137));
    private final BooleanSetting showZero = this.add(new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c 0", false));
    private int lastCombo;
    private long popAt;

    public ComboHud() {
        super("combo", "Combo Counter", "\u041f\u043e\u0441\u043b\u0435\u0434\u043e\u0432\u0430\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0443\u0434\u0430\u0440\u044b \u043f\u043e \u0446\u0435\u043b\u0438 \u0441 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0435\u0439 \u0447\u0438\u0441\u043b\u0430.", Category.PVP, Icon.FLAME, 0.5f, 0.6f);
    }

    @Override
    protected boolean drawsPanel() {
        return !this.style.is("Astra Anime");
    }

    @Override
    public boolean shouldRender() {
        return (Boolean)this.showZero.get() != false || CombatTracker.combo() > 0;
    }

    @Override
    public float width() {
        return 74.0f;
    }

    @Override
    public float height() {
        return 26.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        int combo = CombatTracker.combo();
        if (editor && combo == 0) {
            combo = 12;
        }
        this.drawCombo(ctx, w, h, combo);
    }

    private void drawCombo(GuiGraphics ctx, float w, float h, int combo) {
        if (combo != this.lastCombo) {
            if (combo > this.lastCombo) {
                this.popAt = System.currentTimeMillis();
            }
            this.lastCombo = combo;
        }
        float t = Math.min(1.0f, (float)(System.currentTimeMillis() - this.popAt) / 220.0f);
        float pop = 1.0f + 0.35f * (1.0f - AnimationUtils.easeOutCubic(t));
        String num = String.valueOf(combo);
        float numSize = 17.0f;
        float nw = Fonts.width(num, numSize, true);
        float cw = Fonts.width("Combo", 9.0f, true);
        float total = nw + 4.0f + cw;
        float x0 = (w - total) / 2.0f;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(x0 + nw / 2.0f, h / 2.0f);
        m.scale(pop, pop);
        if (this.style.is("Astra Anime")) {
            Render2D.shadow(ctx, -nw / 2.0f + 2.0f, -numSize / 2.0f + 4.0f, nw - 4.0f, numSize - 8.0f, 4.0f, 9.0f, ColorUtil.withAlpha(this.numberColor.getColor(), 0.18f));
        }
        Fonts.draw(ctx, num, -nw / 2.0f, -numSize / 2.0f + 1.0f, numSize, this.numberColor.getColor(), true);
        m.popMatrix();
        Fonts.draw(ctx, "Combo", x0 + nw + 4.0f, h / 2.0f - 3.0f, 9.0f, this.textColor.getColor(), true);
        if (this.style.is("Astra Anime")) {
            float ux = x0 + nw + 2.0f;
            float uy = h / 2.0f + 7.5f;
            Render2D.roundRect(ctx, ux, uy, cw + 4.0f, 1.6f, 0.8f, Paint.horizontal(ux, ux + cw + 4.0f, this.numberColor.getColor(), 0xFFFFFF));
        }
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 32.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, ColorUtil.withAlpha(ui.theme.bg(), 0.6f));
                Render2D.outline(ui.ctx, x, y, w, h, 5.0f, 0.7f, ColorUtil.withAlpha(ui.theme.accent(), 0.4f));
                Matrix3x2fStack m = ui.ctx.pose();
                m.pushMatrix();
                m.translate(x + (w - 74.0f) / 2.0f, y + 3.0f);
                int combo = CombatTracker.combo() > 0 ? CombatTracker.combo() : 12;
                ComboHud.this.drawCombo(ui.ctx, 74.0f, 26.0f, combo);
                m.popMatrix();
            }
        };
    }
}

