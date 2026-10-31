/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.util.Util
 */
package astra.gui.pages;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.gui.pages.ModulesPage;
import astra.gui.screens.ClickGuiScreen;
import astra.gui.screens.DecorEditorScreen;
import astra.module.Category;
import astra.module.ClientSettings;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.StylePresets;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;

public class ThemesPage
extends ModulesPage {
    public ThemesPage(ClickGuiScreen screen) {
        super(screen, Category.THEMES);
        this.header.add(new ThemeGrid());
        this.header.add(new ArtCard());
        this.header.add(new PresetsCard());
    }

    private final class ThemeGrid
    extends Widget {
        private final List<Animation> hovers = new ArrayList<Animation>();
        private static final float GAP = 8.0f;

        ThemeGrid() {
            for (int i = 0; i < ThemeManager.all().size(); ++i) {
                this.hovers.add(new Animation(0.0f, 14.0f));
            }
        }

        private int cols() {
            return Math.max(2, Math.min(5, (int)((this.w + 8.0f) / 138.0f)));
        }

        private float cardH() {
            return 92.0f;
        }

        @Override
        protected float measure(float width) {
            this.w = width;
            int rows = (int)Math.ceil((double)ThemeManager.all().size() / (double)this.cols());
            return (float)rows * (this.cardH() + 8.0f) - 8.0f;
        }

        private float[] cell(int i) {
            int c = this.cols();
            float cw = (this.w - 8.0f * (float)(c - 1)) / (float)c;
            return new float[]{this.x + (float)(i % c) * (cw + 8.0f), this.y + (float)(i / c) * (this.cardH() + 8.0f), cw, this.cardH()};
        }

        @Override
        public void render(UiContext ui) {
            List<Theme> themes = ThemeManager.all();
            String current = ThemeManager.currentName();
            for (int i = 0; i < themes.size(); ++i) {
                Theme th = themes.get(i);
                float[] c = this.cell(i);
                boolean sel = th.name().equals(current);
                float hv = this.hovers.get(i).update(ui.hover(c[0], c[1], c[2], c[3]) ? 1.0f : 0.0f, ui.dt);
                if (sel || hv > 0.01f) {
                    Render2D.glow(ui.ctx, c[0], c[1], c[2], c[3], 8.0f, 9.0f, ColorUtil.withAlpha(th.accent(), (sel ? 0.45f : 0.25f * hv) * UiDraw.glowStrength()));
                }
                Render2D.roundRect(ui.ctx, c[0], c[1], c[2], c[3], 8.0f, Paint.linear(c[0], c[1], c[0] + c[2], c[1] + c[3], th.bg2(), th.bg()));
                float px = c[0] + 8.0f;
                float py = c[1] + 8.0f;
                float pw = c[2] - 16.0f;
                float ph = 46.0f;
                Render2D.roundRect(ui.ctx, px, py, pw, ph, 5.0f, ColorUtil.withAlpha(th.bg(), 0.9f));
                Render2D.roundRect(ui.ctx, px + 4.0f, py + 5.0f, pw * 0.3f, 7.0f, 3.0f, Paint.horizontal(px, px + pw * 0.3f, th.accent(), th.accent2()));
                for (int k = 0; k < 3; ++k) {
                    Render2D.roundRect(ui.ctx, px + 4.0f, py + 16.0f + (float)(k * 9), pw * 0.3f, 5.0f, 2.0f, ColorUtil.withAlpha(th.textDim(), 0.25f));
                }
                Render2D.roundRect(ui.ctx, px + pw * 0.38f, py + 5.0f, pw * 0.58f, ph - 10.0f, 4.0f, th.card());
                Render2D.roundRect(ui.ctx, px + pw * 0.44f, py + 12.0f, pw * 0.3f, 4.0f, 2.0f, ColorUtil.withAlpha(th.text(), 0.7f));
                Render2D.roundRect(ui.ctx, px + pw * 0.44f, py + 24.0f, pw * 0.45f, 3.0f, 1.5f, Paint.horizontal(px, px + pw, th.accent(), th.accent2()));
                Render2D.roundRect(ui.ctx, px + pw * 0.79f, py + 31.0f, 14.0f, 7.0f, 3.5f, th.accent());
                Fonts.draw(ui.ctx, th.name(), c[0] + 9.0f, c[1] + 60.0f, 8.0f, th.text(), true);
                int[] sw = new int[]{th.accent(), th.accent2(), th.accent3(), th.textDim()};
                for (int k = 0; k < sw.length; ++k) {
                    Render2D.circle(ui.ctx, c[0] + 13.0f + (float)(k * 11), c[1] + 79.0f, 3.8f, sw[k]);
                }
                Render2D.outline(ui.ctx, c[0], c[1], c[2], c[3], 8.0f, sel ? 1.4f : 0.8f, ColorUtil.withAlpha(th.accent(), sel ? 1.0f : 0.3f + 0.4f * hv));
                if (!sel) continue;
                Render2D.circle(ui.ctx, c[0] + c[2] - 13.0f, c[1] + 77.0f, 7.0f, th.accent());
                Icons.draw(ui.ctx, Icon.CHECK, c[0] + c[2] - 18.0f, c[1] + 72.0f, 10.0f, -1);
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            List<Theme> themes = ThemeManager.all();
            for (int i = 0; i < themes.size(); ++i) {
                float[] c = this.cell(i);
                if (!(mx >= (double)c[0]) || !(mx < (double)(c[0] + c[2])) || !(my >= (double)c[1]) || !(my < (double)(c[1] + c[3]))) continue;
                ClientSettings cs = ClientSettings.get();
                if (cs != null) {
                    cs.theme.set(themes.get(i).name());
                }
                ThemesPage.this.screen.toast("\u0422\u0435\u043c\u0430 \u00ab" + themes.get(i).name() + "\u00bb \u043f\u0440\u0438\u043c\u0435\u043d\u0435\u043d\u0430");
                return true;
            }
            return false;
        }
    }

    private final class ArtCard
    extends Widget {
        private final Animation h1 = new Animation(0.0f, 16.0f);
        private final Animation h2 = new Animation(0.0f, 16.0f);
        private final Animation h3 = new Animation(0.0f, 16.0f);
        private final Animation h4 = new Animation(0.0f, 16.0f);

        private ArtCard() {
        }

        @Override
        protected float measure(float width) {
            return 80.0f;
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            UiDraw.card(ui.ctx, t, this.x, this.y, this.w, this.h, 9.0f, 0.0f, false);
            Icons.draw(ui.ctx, Icon.IMAGE, this.x + 12.0f, this.y + 11.0f, 16.0f, t.accent());
            Fonts.draw(ui.ctx, "\u0410\u043d\u0438\u043c\u0435-\u0430\u0440\u0442 \u0432 \u043c\u0435\u043d\u044e", this.x + 34.0f, this.y + 11.0f, 8.6f, t.text(), true);
            Fonts.draw(ui.ctx, "\u0417\u0430\u043a\u0438\u043d\u044c\u0442\u0435 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438 (PNG, JPG, BMP, GIF) \u0432 \u043f\u0430\u043f\u043a\u0443 config/astra/art \u0438 \u0432\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u043d\u0443\u0436\u043d\u0443\u044e \u043d\u0438\u0436\u0435.", this.x + 34.0f, this.y + 23.0f, 6.3f, t.textMuted(), false);
            float bw = 120.0f;
            UiDraw.button(ui.ctx, t, this.x + 34.0f, this.y + 34.0f, bw, 17.0f, "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443", Icon.FOLDER, this.h1.update(ui.hover(this.x + 34.0f, this.y + 34.0f, bw, 17.0f) ? 1.0f : 0.0f, ui.dt), false);
            UiDraw.button(ui.ctx, t, this.x + 40.0f + bw, this.y + 34.0f, 100.0f, 17.0f, "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c", Icon.RESET, this.h2.update(ui.hover(this.x + 40.0f + bw, this.y + 34.0f, 100.0f, 17.0f) ? 1.0f : 0.0f, ui.dt), false);
            UiDraw.button(ui.ctx, t, this.x + 266.0f, this.y + 34.0f, 170.0f, 17.0f, "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f", Icon.EDIT, this.h4.update(ui.hover(this.x + 266.0f, this.y + 34.0f, 170.0f, 17.0f) ? 1.0f : 0.0f, ui.dt), true);
            Fonts.draw(ui.ctx, "\u0434\u0432\u0438\u0433\u0430\u0439\u0442\u0435 \u0438 \u0440\u0430\u0441\u0442\u044f\u0433\u0438\u0432\u0430\u0439\u0442\u0435 \u0441\u0432\u043e\u0438 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438 \u0438 \u0430\u0440\u0442 \u043f\u0430\u043d\u0435\u043b\u0438, \u0432 \u043c\u0435\u043d\u044e \u0438 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", this.x + 442.0f, this.y + 39.0f, 6.2f, t.textMuted(), false);
            ClientSettings cs = ClientSettings.get();
            if (cs == null) {
                return;
            }
            Fonts.draw(ui.ctx, "\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", this.x + 34.0f, this.y + 61.0f, 7.0f, t.textDim(), false);
            float sx = this.x + 80.0f;
            float sw = 220.0f;
            boolean hov = ui.hover(sx, this.y + 56.0f, sw, 17.0f);
            float hv = this.h3.update(hov ? 1.0f : 0.0f, ui.dt);
            Render2D.roundRect(ui.ctx, sx, this.y + 56.0f, sw, 17.0f, 5.0f, ColorUtil.withAlpha(t.card(), 0.9f + 0.1f * hv));
            Render2D.outline(ui.ctx, sx, this.y + 56.0f, sw, 17.0f, 5.0f, 0.75f, ColorUtil.withAlpha(t.accent(), 0.35f + 0.4f * hv));
            int total = cs.sidebarArt.getModes().size();
            String name = (String)cs.sidebarArt.get() + "  (" + (cs.sidebarArt.index() + 1) + "/" + total + ")";
            Fonts.drawCentered(ui.ctx, Fonts.trim(name, sw - 34.0f, 7.0f, true), sx + sw / 2.0f, this.y + 61.0f, 7.0f, t.text(), true);
            Fonts.draw(ui.ctx, "\u2039", sx + 7.0f, this.y + 60.0f, 8.5f, t.accent(), true);
            Fonts.drawRight(ui.ctx, "\u203a", sx + sw - 7.0f, this.y + 60.0f, 8.5f, t.accent(), true);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            ClientSettings cs;
            if (my >= (double)(this.y + 34.0f) && my < (double)(this.y + 51.0f)) {
                if (mx >= (double)(this.x + 34.0f) && mx < (double)(this.x + 154.0f)) {
                    ArtManager.artDir().toFile().mkdirs();
                    Util.getPlatform().openFile(ArtManager.artDir().toFile());
                    return true;
                }
                if (mx >= (double)(this.x + 266.0f) && mx < (double)(this.x + 436.0f)) {
                    Minecraft mc = Minecraft.getInstance();
                    mc.gui.setScreen((Screen)new DecorEditorScreen(mc.gui.screen()));
                    return true;
                }
                if (mx >= (double)(this.x + 160.0f) && mx < (double)(this.x + 260.0f)) {
                    ArtManager.reload();
                    ThemesPage.this.screen.toast("\u0410\u0440\u0442 \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d: \u043a\u0430\u0440\u0442\u0438\u043d\u043e\u043a \u0432 \u043f\u0430\u043f\u043a\u0435 \u2014 " + ArtManager.customFiles().size());
                    return true;
                }
            }
            if ((cs = ClientSettings.get()) != null && my >= (double)(this.y + 56.0f) && my < (double)(this.y + 73.0f) && mx >= (double)(this.x + 80.0f) && mx < (double)(this.x + 300.0f)) {
                cs.sidebarArt.cycle(mx < (double)(this.x + 190.0f) ? -1 : 1);
                ArtManager.reload();
                ThemesPage.this.screen.toast("\u0410\u0440\u0442 \u0432 \u043c\u0435\u043d\u044e: " + (String)cs.sidebarArt.get());
                return true;
            }
            return this.contains(mx, my);
        }
    }

    private final class PresetsCard
    extends Widget {
        private final List<Animation> hovers = new ArrayList<Animation>();

        PresetsCard() {
            for (int i = 0; i < StylePresets.ALL.size(); ++i) {
                this.hovers.add(new Animation(0.0f, 14.0f));
            }
        }

        @Override
        protected float measure(float width) {
            return 76.0f;
        }

        private float[] cell(int i) {
            int n = StylePresets.ALL.size();
            float gap = 6.0f;
            float cw = (this.w - 46.0f - gap * (float)(n - 1)) / (float)n;
            return new float[]{this.x + 34.0f + (float)i * (cw + gap), this.y + 26.0f, cw, 40.0f};
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            UiDraw.card(ui.ctx, t, this.x, this.y, this.w, this.h, 9.0f, 0.0f, false);
            Icons.draw(ui.ctx, Icon.SPARKLE, this.x + 12.0f, this.y + 9.0f, 16.0f, t.accent());
            Fonts.draw(ui.ctx, "\u0413\u043e\u0442\u043e\u0432\u044b\u0435 \u043e\u0431\u0440\u0430\u0437\u044b", this.x + 34.0f, this.y + 9.0f, 8.6f, t.text(), true);
            Fonts.draw(ui.ctx, "\u043e\u0434\u043d\u0438\u043c \u043a\u043b\u0438\u043a\u043e\u043c: \u0442\u0435\u043c\u0430, \u043a\u0440\u044b\u043b\u044c\u044f, \u043d\u0438\u043c\u0431, \u0430\u0443\u0440\u0430, \u0441\u043b\u0435\u0434 \u0438 \u043f\u043b\u0430\u0449 \u2014 \u0434\u0430\u043b\u044c\u0448\u0435 \u0432\u0441\u0451 \u043c\u043e\u0436\u043d\u043e \u043f\u043e\u0434\u043a\u0440\u0443\u0442\u0438\u0442\u044c", this.x + 130.0f, this.y + 11.0f, 6.3f, t.textMuted(), false);
            for (int i = 0; i < StylePresets.ALL.size(); ++i) {
                float[] c;
                StylePresets.Preset p = StylePresets.ALL.get(i);
                float hv = this.hovers.get(i).update(ui.hover((c = this.cell(i))[0], c[1], c[2], c[3]) ? 1.0f : 0.0f, ui.dt);
                if (hv > 0.01f) {
                    Render2D.glow(ui.ctx, c[0], c[1], c[2], c[3], 7.0f, 8.0f, ColorUtil.withAlpha(p.color1(), 0.45f * hv));
                }
                Render2D.roundRect(ui.ctx, c[0], c[1], c[2], c[3], 7.0f, Paint.linear(c[0], c[1], c[0] + c[2], c[1] + c[3], ColorUtil.withAlpha(p.color1(), 0.35f + 0.2f * hv), ColorUtil.withAlpha(p.color2(), 0.18f + 0.1f * hv)));
                Render2D.outline(ui.ctx, c[0], c[1], c[2], c[3], 7.0f, 0.9f, ColorUtil.withAlpha(p.color1(), 0.6f + 0.4f * hv));
                Fonts.draw(ui.ctx, Fonts.trim(p.name(), c[2] - 12.0f, 7.6f, true), c[0] + 7.0f, c[1] + 8.0f, 7.6f, t.text(), true);
                Fonts.draw(ui.ctx, Fonts.trim(p.hint(), c[2] - 12.0f, 5.8f, false), c[0] + 7.0f, c[1] + 22.0f, 5.8f, t.textDim(), false);
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            for (int i = 0; i < StylePresets.ALL.size(); ++i) {
                float[] c = this.cell(i);
                if (!(mx >= (double)c[0]) || !(mx < (double)(c[0] + c[2])) || !(my >= (double)c[1]) || !(my < (double)(c[1] + c[3]))) continue;
                StylePresets.Preset p = StylePresets.ALL.get(i);
                p.apply().run();
                ThemesPage.this.screen.toast("\u041e\u0431\u0440\u0430\u0437 \u00ab" + p.name() + "\u00bb \u043f\u0440\u0438\u043c\u0435\u043d\u0451\u043d");
                return true;
            }
            return this.contains(mx, my);
        }
    }
}

