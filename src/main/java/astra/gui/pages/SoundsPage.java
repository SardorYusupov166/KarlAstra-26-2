/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.pages;

import astra.gui.UiContext;
import astra.gui.components.ModuleCard;
import astra.gui.components.Widget;
import astra.gui.pages.ModulesPage;
import astra.gui.screens.ClickGuiScreen;
import astra.module.modules.cosmetic.SoundsModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SoundsPage
extends ModulesPage {
    private final Map<Heading, List<ModuleCard>> groups = new LinkedHashMap<Heading, List<ModuleCard>>();

    public SoundsPage(ClickGuiScreen screen) {
        super(screen, List.of());
        SoundsModule sounds = SoundsModule.get();
        if (sounds == null) {
            return;
        }
        LinkedHashMap<String, Heading> byName = new LinkedHashMap<String, Heading>();
        for (SoundsModule.Section sec : sounds.sections()) {
            Heading heading = byName.computeIfAbsent(sec.group(), g -> {
                Heading hd = new Heading((String)g, SoundsPage.iconFor(g));
                this.header.add(hd);
                return hd;
            });
            ModuleCard card = sec.moduleSwitch() ? ModuleCard.moduleSection(sounds, screen, sec.title(), sec.description(), sec.icon(), sec.settings()) : new ModuleCard(sounds, screen, sec.title(), sec.description(), sec.icon(), sec.toggle(), sec.settings());
            this.cards.add(card);
            this.groups.computeIfAbsent(heading, h -> new ArrayList()).add(card);
        }
    }

    private static Icon iconFor(String group) {
        return switch (group) {
            case "\u0411\u043e\u0439" -> Icon.SWORD;
            case "\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0438 \u0441\u043c\u0435\u0440\u0442\u044c" -> Icon.HEART;
            case "\u0421\u0435\u0440\u0432\u0435\u0440 \u0438 \u043c\u0435\u043d\u044e" -> Icon.HOME;
            case "\u041f\u0440\u0435\u0434\u043c\u0435\u0442\u044b" -> Icon.SHIELD;
            case "\u0420\u0430\u0437\u043d\u043e\u0435" -> Icon.SPARKLE;
            default -> Icon.WAVE;
        };
    }

    @Override
    protected float layout(float top) {
        float cy = top;
        boolean first = true;
        for (Map.Entry<Heading, List<ModuleCard>> e : this.groups.entrySet()) {
            if (!first) {
                cy += 6.0f;
            }
            first = false;
            cy += e.getKey().layout(this.x, cy, this.w) + 4.0f;
            cy = this.layoutCards(e.getValue(), this.x, cy, this.w) + 8.0f;
        }
        return cy - top;
    }

    private final class Heading
    extends Widget {
        private final String text;
        private final Icon icon;

        Heading(String text, Icon icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        protected float measure(float width) {
            return 16.0f;
        }

        @Override
        public void render(UiContext ui) {
            float lx;
            String count;
            Theme t = ui.theme;
            Icons.draw(ui.ctx, this.icon, this.x, this.y + 3.0f, 10.0f, t.accent());
            Fonts.draw(ui.ctx, this.text, this.x + 14.0f, this.y + 3.6f, 8.2f, t.text(), true);
            float tw = Fonts.width(this.text, 8.2f, true);
            List<ModuleCard> list = SoundsPage.this.groups.get(this);
            String string = list == null ? "" : (count = list.size() + (list.size() == 1 ? " \u0441\u043e\u0431\u044b\u0442\u0438\u0435" : (list.size() < 5 ? " \u0441\u043e\u0431\u044b\u0442\u0438\u044f" : " \u0441\u043e\u0431\u044b\u0442\u0438\u0439")));
            if (!count.isEmpty()) {
                Fonts.draw(ui.ctx, count, this.x + 20.0f + tw, this.y + 5.0f, 6.0f, t.textMuted(), false);
            }
            if ((lx = this.x + 26.0f + tw + Fonts.width(count, 6.0f, false)) < this.x + this.w) {
                Render2D.rect(ui.ctx, lx, this.y + 8.0f, this.x + this.w - lx, 0.7f, Paint.horizontal(lx, this.x + this.w, ColorUtil.withAlpha(t.accent(), 0.45f), ColorUtil.withAlpha(t.accent(), 0.0f)));
            }
        }
    }
}

