/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.util.Mth
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public class DirectionHud
extends HudModule {
    private static final String[] NAMES = new String[]{"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "\u041a\u043e\u043c\u043f\u0430\u0441-\u043b\u0435\u043d\u0442\u0430", "\u041a\u043e\u043c\u043f\u0430\u0441-\u043b\u0435\u043d\u0442\u0430", "\u041a\u043e\u043c\u043f\u0430\u043a\u0442\u043d\u044b\u0439"));

    public DirectionHud() {
        super("direction", "\u041d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435", "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0435\u0442 \u0432\u0430\u0448\u0435 \u043d\u0430\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435.", Category.MOVEMENT, Icon.COMPASS, 0.5f, 0.03f);
    }

    private float yaw() {
        return DirectionHud.mc.player != null ? Mth.wrapDegrees((float)DirectionHud.mc.player.getYRot()) : -57.0f;
    }

    private float bearing() {
        return (this.yaw() + 180.0f + 360.0f) % 360.0f;
    }

    private String cardinal() {
        int i = Math.round((this.yaw() % 360.0f + 360.0f) % 360.0f / 45.0f) % 8;
        return NAMES[i];
    }

    private boolean tape() {
        return this.style.is("\u041a\u043e\u043c\u043f\u0430\u0441-\u043b\u0435\u043d\u0442\u0430");
    }

    @Override
    public float width() {
        return this.tape() ? 150.0f : Fonts.width(this.cardinal() + "  |  " + Math.round(this.bearing()) + "\u00b0", 8.0f, true) + 14.0f;
    }

    @Override
    public float height() {
        return this.tape() ? 24.0f : 15.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = DirectionHud.theme();
        if (!this.tape()) {
            String s = this.cardinal() + "  |  " + Math.round(this.bearing()) + "\u00b0";
            Fonts.drawCentered(ctx, s, w / 2.0f, 3.8f, 8.0f, t.text(), true);
            return;
        }
        float b = this.bearing();
        float pxPerDeg = 1.6f;
        ctx.enableScissor(2, 0, (int)w - 2, (int)h);
        for (int deg = -180; deg <= 540; deg += 15) {
            float x = w / 2.0f + ((float)deg - b) * pxPerDeg;
            if (x < -10.0f || x > w + 10.0f) continue;
            float edge = 1.0f - Math.min(1.0f, Math.abs(x - w / 2.0f) / (w / 2.0f));
            int norm = (deg % 360 + 360) % 360;
            if (norm % 45 == 0) {
                String label;
                switch (norm) {
                    case 0: {
                        String string = "N";
                        break;
                    }
                    case 45: {
                        String string = "NE";
                        break;
                    }
                    case 90: {
                        String string = "E";
                        break;
                    }
                    case 135: {
                        String string = "SE";
                        break;
                    }
                    case 180: {
                        String string = "S";
                        break;
                    }
                    case 225: {
                        String string = "SW";
                        break;
                    }
                    case 270: {
                        String string = "W";
                        break;
                    }
                    default: {
                        String string = label = "NW";
                    }
                }
                int col = norm % 90 == 0 ? (norm == 0 ? this.accentColor() : t.text()) : t.textDim();
                Fonts.drawCentered(ctx, label, x, 4.0f, norm % 90 == 0 ? 8.0f : 6.5f, ColorUtil.mulAlpha(col, edge), true);
                continue;
            }
            Render2D.rect(ctx, x - 0.4f, 6.0f, 0.8f, 4.0f, ColorUtil.withAlpha(t.textDim(), 0.6f * edge));
        }
        ctx.disableScissor();
        Render2D.roundRect(ctx, w / 2.0f - 0.75f, 13.5f, 1.5f, 5.0f, 0.75f, this.accentColor());
        String deg = Math.round(b) + "\u00b0";
        Fonts.drawCentered(ctx, deg, w / 2.0f, h - 7.5f, 5.5f, t.textDim(), true);
        Render2D.rect(ctx, 4.0f, h - 1.5f, w - 8.0f, 0.6f, Paint.horizontal(4.0f, w - 4.0f, 0xFFFFFF, ColorUtil.withAlpha(this.accentColor(), 0.6f)));
    }
}

