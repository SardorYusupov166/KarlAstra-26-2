/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.module.modules.pvp;

import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import astra.utils.ClickTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class CpsHud
extends HudModule {
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "\u0420\u0430\u0437\u0434\u0435\u043b\u044c\u043d\u043e", "\u0420\u0430\u0437\u0434\u0435\u043b\u044c\u043d\u043e", "\u0421\u0442\u0440\u043e\u043a\u0430", "\u0422\u043e\u043b\u044c\u043a\u043e \u041b\u041a\u041c"));

    public CpsHud() {
        super("cps", "CPS-\u0438\u043d\u0434\u0438\u043a\u0430\u0442\u043e\u0440", "\u041a\u043b\u0438\u043a\u0438 \u0432 \u0441\u0435\u043a\u0443\u043d\u0434\u0443: \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u043e \u041b\u041a\u041c \u0438 \u041f\u041a\u041c.", Category.PVP, Icon.MOUSE, 0.635f, 0.11f);
        this.setDefaultEnabled(true);
    }

    @Override
    public float width() {
        return switch ((String)this.style.get()) {
            case "\u0420\u0430\u0437\u0434\u0435\u043b\u044c\u043d\u043e" -> 82.0f;
            case "\u0421\u0442\u0440\u043e\u043a\u0430" -> Fonts.width(ClickTracker.leftCps() + " | " + ClickTracker.rightCps() + " CPS", 8.0f, true) + 14.0f;
            default -> 40.0f;
        };
    }

    @Override
    public float height() {
        return this.style.is("\u0421\u0442\u0440\u043e\u043a\u0430") ? 15.0f : 30.0f;
    }

    @Override
    protected boolean drawsPanel() {
        return !this.style.is("\u0420\u0430\u0437\u0434\u0435\u043b\u044c\u043d\u043e");
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = CpsHud.theme();
        switch ((String)this.style.get()) {
            case "\u0420\u0430\u0437\u0434\u0435\u043b\u044c\u043d\u043e": {
                float bw = (w - 4.0f) / 2.0f;
                this.box(ctx, t, 0.0f, 0.0f, bw, h, "LMB", ClickTracker.leftCps());
                this.box(ctx, t, bw + 4.0f, 0.0f, bw, h, "RMB", ClickTracker.rightCps());
                break;
            }
            case "\u0421\u0442\u0440\u043e\u043a\u0430": {
                Fonts.drawCentered(ctx, ClickTracker.leftCps() + " | " + ClickTracker.rightCps() + " CPS", w / 2.0f, 3.8f, 8.0f, t.text(), true);
                break;
            }
            default: {
                Fonts.drawCentered(ctx, "CPS", w / 2.0f, 4.5f, 6.0f, t.textDim(), true);
                Fonts.drawCentered(ctx, String.valueOf(ClickTracker.leftCps()), w / 2.0f, 12.5f, 13.0f, t.accent2(), true);
            }
        }
    }

    private void box(GuiGraphicsExtractor ctx, Theme t, float x, float y, float w, float h, String label, int cps) {
        this.drawPanel(ctx, x, y, w, h, 5.0f);
        Fonts.drawCentered(ctx, label, x + w / 2.0f, y + 5.0f, 7.0f, this.accentColor(), true);
        Fonts.drawCentered(ctx, cps + " CPS", x + w / 2.0f, y + 16.0f, 7.0f, t.text(), true);
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
                Theme t = ui.theme;
                float bw = Math.min(70.0f, (w - 8.0f) / 2.0f);
                float sx = x + (w - bw * 2.0f - 8.0f) / 2.0f;
                String[] labels = new String[]{"LMB", "RMB"};
                int[] values = new int[]{ClickTracker.leftCps(), ClickTracker.rightCps()};
                for (int i = 0; i < 2; ++i) {
                    float bx = sx + (float)i * (bw + 8.0f);
                    Render2D.roundRect(ui.ctx, bx, y, bw, h, 5.0f, ColorUtil.withAlpha(t.bg(), 0.6f));
                    Render2D.outline(ui.ctx, bx, y, bw, h, 5.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.6f));
                    Fonts.drawCentered(ui.ctx, labels[i], bx + bw / 2.0f, y + 6.0f, 8.0f, t.accent(), true);
                    Fonts.drawCentered(ui.ctx, values[i] + " CPS", bx + bw / 2.0f, y + 18.0f, 7.0f, t.text(), true);
                }
            }
        };
    }
}

