/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.Minecraft
 */
package astra.gui.pages;

import astra.animation.Animation;
import astra.config.HudPresetManager;
import astra.gui.UiContext;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.gui.pages.ModulesPage;
import astra.gui.screens.ClickGuiScreen;
import astra.hud.HudModule;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import com.mojang.blaze3d.platform.Window;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;

public class HudPage
extends ModulesPage {
    public HudPage(ClickGuiScreen screen) {
        super(screen, Category.HUD);
        this.header.add(new EditorBanner());
        this.header.add(new ElementChips(this));
    }

    private final class EditorBanner
    extends Widget {
        private final Animation openHover = new Animation(0.0f, 16.0f);
        private final Animation resetHover = new Animation(0.0f, 16.0f);

        private EditorBanner() {
        }

        @Override
        protected float measure(float width) {
            return 112.0f;
        }

        private float[] openBtn() {
            return new float[]{this.x + 14.0f, this.y + this.h - 32.0f, 130.0f, 20.0f};
        }

        private float[] resetBtn() {
            return new float[]{this.x + 150.0f, this.y + this.h - 32.0f, 110.0f, 20.0f};
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            UiDraw.card(ui.ctx, t, this.x, this.y, this.w, this.h, 9.0f, 0.0f, true);
            Render2D.roundRect(ui.ctx, this.x, this.y, this.w, this.h, 9.0f, Paint.horizontal(this.x, this.x + this.w, ColorUtil.withAlpha(t.accent(), 0.16f), 0));
            UiDraw.iconBadge(ui.ctx, t, this.x + 14.0f, this.y + 14.0f, 26.0f, Icon.MOVE, true);
            Fonts.draw(ui.ctx, "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 HUD", this.x + 48.0f, this.y + 16.0f, 11.0f, t.text(), true);
            Fonts.draw(ui.ctx, "\u041f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u0439\u0442\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u044b, \u043c\u0435\u043d\u044f\u0439\u0442\u0435 \u0440\u0430\u0437\u043c\u0435\u0440, \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0438 \u0441\u0442\u0438\u043b\u044c.", this.x + 48.0f, this.y + 31.0f, 6.6f, t.textDim(), false);
            Fonts.draw(ui.ctx, "\u0421\u0435\u0442\u043a\u0430, \u043d\u0430\u043f\u0440\u0430\u0432\u043b\u044f\u044e\u0449\u0438\u0435, \u043f\u0440\u0438\u0432\u044f\u0437\u043a\u0430 \u043a \u0446\u0435\u043d\u0442\u0440\u0443 \u0438 \u043f\u0440\u0435\u0441\u0435\u0442\u044b \u0440\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0438.", this.x + 48.0f, this.y + 41.0f, 6.6f, t.textMuted(), false);
            float[] o = this.openBtn();
            float[] r = this.resetBtn();
            UiDraw.button(ui.ctx, t, o[0], o[1], o[2], o[3], "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u0440\u0435\u0434\u0430\u043a\u0442\u043e\u0440", Icon.EDIT, this.openHover.update(ui.hover(o[0], o[1], o[2], o[3]) ? 1.0f : 0.0f, ui.dt), true);
            UiDraw.button(ui.ctx, t, r[0], r[1], r[2], r[3], "\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c", Icon.RESET, this.resetHover.update(ui.hover(r[0], r[1], r[2], r[3]) ? 1.0f : 0.0f, ui.dt), false);
            float sw = Math.min(150.0f, this.w * 0.32f);
            float sh = sw * 9.0f / 16.0f;
            float sx = this.x + this.w - sw - 16.0f;
            float sy = this.y + (this.h - sh) / 2.0f;
            Render2D.roundRect(ui.ctx, sx, sy, sw, sh, 5.0f, Paint.vertical(sy, sy + sh, ColorUtil.lerp(t.bg(), -13951138, 0.6f), ColorUtil.lerp(t.bg(), -10868140, 0.5f)));
            Render2D.outline(ui.ctx, sx, sy, sw, sh, 5.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.6f));
            Render2D.dashedLine(ui.ctx, sx + sw / 2.0f, sy + 3.0f, sx + sw / 2.0f, sy + sh - 3.0f, 0.5f, 2.0f, 2.0f, ColorUtil.withAlpha(t.accent3(), 0.5f));
            Render2D.dashedLine(ui.ctx, sx + 3.0f, sy + sh / 2.0f, sx + sw - 3.0f, sy + sh / 2.0f, 0.5f, 2.0f, 2.0f, ColorUtil.withAlpha(t.accent3(), 0.5f));
            Window window = Minecraft.getInstance().getWindow();
            float gw = window.getGuiScaledWidth();
            float gh = window.getGuiScaledHeight();
            for (HudModule hud : ModuleManager.hudModules()) {
                if (!hud.isEnabled()) continue;
                float ex = sx + hud.getX(gw) / gw * sw;
                float ey = sy + hud.getY(gh) / gh * sh;
                float ew = Math.max(2.0f, hud.scaledWidth() / gw * sw);
                float eh = Math.max(2.0f, hud.scaledHeight() / gh * sh);
                Render2D.roundRect(ui.ctx, ex, ey, ew, eh, 1.0f, ColorUtil.withAlpha(t.accent(), 0.55f));
                Render2D.outline(ui.ctx, ex, ey, ew, eh, 1.0f, 0.5f, ColorUtil.withAlpha(0xFFFFFF, 0.6f));
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            float[] o = this.openBtn();
            float[] r = this.resetBtn();
            if (mx >= (double)o[0] && mx < (double)(o[0] + o[2]) && my >= (double)o[1] && my < (double)(o[1] + o[3])) {
                HudPage.this.screen.openHudEditor();
                return true;
            }
            if (mx >= (double)r[0] && mx < (double)(r[0] + r[2]) && my >= (double)r[1] && my < (double)(r[1] + r[3])) {
                HudPresetManager.load("\u041f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e");
                HudPage.this.screen.toast("\u0420\u0430\u0441\u043a\u043b\u0430\u0434\u043a\u0430 HUD \u0441\u0431\u0440\u043e\u0448\u0435\u043d\u0430");
                return true;
            }
            return this.contains(mx, my);
        }
    }

    private final class ElementChips
    extends Widget {
        private final Map<HudModule, Animation> anims = new HashMap<HudModule, Animation>();
        private static final float CHIP_H = 20.0f;
        private static final float GAP = 6.0f;

        private ElementChips(HudPage hudPage) {
        }

        private List<HudModule> list() {
            return ModuleManager.hudModules();
        }

        private int cols() {
            return Math.max(2, (int)((this.w - 20.0f + 6.0f) / 126.0f));
        }

        @Override
        protected float measure(float width) {
            this.w = width;
            int rows = (int)Math.ceil((double)this.list().size() / (double)this.cols());
            return 30.0f + (float)rows * 26.0f + 6.0f;
        }

        private float[] chip(int i) {
            int c = this.cols();
            float cw = (this.w - 20.0f - 6.0f * (float)(c - 1)) / (float)c;
            return new float[]{this.x + 10.0f + (float)(i % c) * (cw + 6.0f), this.y + 28.0f + (float)(i / c) * 26.0f, cw, 20.0f};
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            UiDraw.card(ui.ctx, t, this.x, this.y, this.w, this.h, 9.0f, 0.0f, false);
            Fonts.draw(ui.ctx, "\u042d\u043b\u0435\u043c\u0435\u043d\u0442\u044b HUD", this.x + 12.0f, this.y + 10.0f, 8.6f, t.text(), true);
            Fonts.drawRight(ui.ctx, "\u041a\u043b\u0438\u043a \u2014 \u0432\u043a\u043b/\u0432\u044b\u043a\u043b", this.x + this.w - 12.0f, this.y + 11.0f, 6.2f, t.textMuted(), false);
            List<HudModule> mods = this.list();
            for (int i = 0; i < mods.size(); ++i) {
                HudModule m = mods.get(i);
                float[] c = this.chip(i);
                Animation a = this.anims.computeIfAbsent(m, k -> new Animation(k.isEnabled() ? 1.0f : 0.0f, 14.0f));
                float on = a.update(m.isEnabled() ? 1.0f : 0.0f, ui.dt);
                boolean hov = ui.hover(c[0], c[1], c[2], c[3]);
                Render2D.roundRect(ui.ctx, c[0], c[1], c[2], c[3], 5.0f, ColorUtil.lerp(ColorUtil.withAlpha(t.bg(), 0.6f), ColorUtil.withAlpha(t.accent(), 0.32f), on));
                Render2D.outline(ui.ctx, c[0], c[1], c[2], c[3], 5.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.2f + 0.5f * on + (hov ? 0.2f : 0.0f)));
                Icons.draw(ui.ctx, m.getIcon(), c[0] + 6.0f, c[1] + 5.0f, 10.0f, ColorUtil.lerp(t.textMuted(), -1, on));
                Fonts.draw(ui.ctx, Fonts.trim(m.getName(), c[2] - 26.0f, 6.8f, false), c[0] + 20.0f, c[1] + 6.8f, 6.8f, ColorUtil.lerp(t.textDim(), t.text(), on), on > 0.5f);
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            List<HudModule> mods = this.list();
            for (int i = 0; i < mods.size(); ++i) {
                float[] c = this.chip(i);
                if (!(mx >= (double)c[0]) || !(mx < (double)(c[0] + c[2])) || !(my >= (double)c[1]) || !(my < (double)(c[1] + c[3]))) continue;
                mods.get(i).toggle();
                return true;
            }
            return this.contains(mx, my);
        }
    }
}

