/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.Fonts;
import astra.render.Icon;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphics;

public abstract class StatHud
extends HudModule {
    protected final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430", "\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430", "\u0421\u0442\u0440\u043e\u043a\u0430"));

    protected StatHud(String id, String name, String description, Category category, Icon icon, float x, float y) {
        super(id, name, description, category, icon, x, y);
    }

    protected abstract String label();

    protected abstract String value();

    protected int valueColor() {
        return StatHud.theme().accent3();
    }

    private boolean card() {
        return this.style.is("\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430");
    }

    @Override
    public float width() {
        if (this.card()) {
            return Math.max(40.0f, Math.max(Fonts.width(this.value(), 13.0f, true), Fonts.width(this.label(), 6.0f, true)) + 14.0f);
        }
        return Fonts.width(this.value() + " " + this.label(), 8.0f, true) + 12.0f;
    }

    @Override
    public float height() {
        return this.card() ? 30.0f : 15.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        Theme t = StatHud.theme();
        if (this.card()) {
            Fonts.drawCentered(ctx, this.label(), w / 2.0f, 4.5f, 6.0f, t.textDim(), true);
            Fonts.drawCentered(ctx, this.value(), w / 2.0f, 12.5f, 13.0f, this.valueColor(), true);
        } else {
            String v = this.value();
            float vw = Fonts.width(v + " ", 8.0f, true);
            float x = (w - Fonts.width(v + " " + this.label(), 8.0f, true)) / 2.0f;
            Fonts.draw(ctx, v, x, 3.8f, 8.0f, this.valueColor(), true);
            Fonts.draw(ctx, this.label(), x + vw, 3.8f, 8.0f, t.textDim(), true);
        }
    }
}

