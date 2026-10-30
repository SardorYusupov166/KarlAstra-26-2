/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.module.modules.pvp;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.utils.CombatTracker;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DamageIndicatorHud
extends HudModule {
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "\u0422\u0435\u043a\u0441\u0442 + \u0447\u0430\u0441\u0442\u0438\u0446\u044b", "\u0422\u0435\u043a\u0441\u0442 + \u0447\u0430\u0441\u0442\u0438\u0446\u044b", "\u0422\u043e\u043b\u044c\u043a\u043e \u0442\u0435\u043a\u0441\u0442"));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u0443\u0440\u043e\u043d\u0430", -45715));
    private final ColorSetting critColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u043a\u0440\u0438\u0442. \u0443\u0434\u0430\u0440\u0430", -1541639));

    public DamageIndicatorHud() {
        super("damage_indicator", "\u0418\u043d\u0434\u0438\u043a\u0430\u0442\u043e\u0440 \u0443\u0440\u043e\u043d\u0430", "\u0412\u0441\u043f\u043b\u044b\u0432\u0430\u044e\u0449\u0438\u0435 \u0447\u0438\u0441\u043b\u0430 \u043d\u0430\u043d\u0435\u0441\u0451\u043d\u043d\u043e\u0433\u043e \u0443\u0440\u043e\u043d\u0430.", Category.PVP, Icon.BOLT, 0.58f, 0.45f);
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    public boolean shouldRender() {
        return !CombatTracker.damageNumbers().isEmpty();
    }

    @Override
    public float width() {
        return 64.0f;
    }

    @Override
    public float height() {
        return 34.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        long now = System.currentTimeMillis();
        if (editor && CombatTracker.damageNumbers().isEmpty()) {
            if (((Boolean)this.background.get()).booleanValue()) {
                this.drawPanel(ctx, 0.0f, 0.0f, w, h, 5.0f);
            }
            this.draw(ctx, 2.4f, 0.3f, false, w, h);
            return;
        }
        int i = 0;
        for (CombatTracker.DamageNumber d : CombatTracker.damageNumbers()) {
            float age = (float)(now - d.time()) / 1500.0f;
            this.draw(ctx, d.amount(), age, d.crit(), w, h - (float)(i * 2));
            ++i;
        }
    }

    private void draw(GuiGraphicsExtractor ctx, float amount, float age, boolean crit, float w, float h) {
        float a = age < 0.15f ? age / 0.15f : 1.0f - Math.max(0.0f, (age - 0.6f) / 0.4f);
        float prev = Render2D.alpha;
        Render2D.alpha *= Math.max(0.0f, a);
        float rise = age * 14.0f;
        String text = String.format(Locale.ROOT, "-%.1f", Float.valueOf(amount));
        int col = crit ? this.critColor.getColor() : this.color.getColor();
        float size = crit ? 15.0f : 13.0f;
        float tx = w / 2.0f + 6.0f;
        float ty = h / 2.0f - size / 2.0f - rise + 4.0f;
        if (this.style.is("\u0422\u0435\u043a\u0441\u0442 + \u0447\u0430\u0441\u0442\u0438\u0446\u044b")) {
            float cx = w / 2.0f - Fonts.width(text, size, true) / 2.0f - 4.0f;
            float cy = h / 2.0f - rise + 3.0f;
            float spin = age * 120.0f;
            Render2D.line(ctx, cx - 3.0f, cy - 3.0f, cx + 3.0f, cy + 3.0f, 1.6f, col);
            Render2D.line(ctx, cx - 3.0f, cy + 3.0f, cx + 3.0f, cy - 3.0f, 1.6f, col);
            Render2D.sparkle(ctx, cx + 3.0f, cy - 7.0f, 2.2f, spin, ColorUtil.withAlpha(col, 0.8f));
        }
        Render2D.shadow(ctx, tx - Fonts.width(text, size, true) / 2.0f + 2.0f, ty + 4.0f, Fonts.width(text, size, true) - 4.0f, size - 8.0f, 3.0f, 8.0f, ColorUtil.withAlpha(col, 0.18f));
        Fonts.drawCentered(ctx, text, tx, ty, size, col, true);
        Render2D.alpha = prev;
    }
}

