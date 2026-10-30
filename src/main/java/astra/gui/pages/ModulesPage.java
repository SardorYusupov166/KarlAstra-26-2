/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.pages;

import astra.gui.UiContext;
import astra.gui.components.ModuleCard;
import astra.gui.components.Widget;
import astra.gui.pages.Page;
import astra.gui.screens.ClickGuiScreen;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import java.util.ArrayList;
import java.util.List;

public class ModulesPage
extends Page {
    protected static final float GAP = 8.0f;
    protected static final float MIN_CARD_W = 205.0f;
    protected final List<Widget> header = new ArrayList<Widget>();
    protected final List<ModuleCard> cards = new ArrayList<ModuleCard>();

    public ModulesPage(ClickGuiScreen screen, Category category) {
        super(screen);
        for (Module m : ModuleManager.byCategory(category)) {
            if (m.isHidden()) continue;
            this.cards.add(new ModuleCard(m, screen));
        }
    }

    protected ModulesPage(ClickGuiScreen screen, List<Module> modules) {
        super(screen);
        for (Module m : modules) {
            this.cards.add(new ModuleCard(m, screen));
        }
    }

    public List<ModuleCard> cards() {
        return this.cards;
    }

    protected int columns(float width) {
        return Math.max(1, (int)((width + 8.0f) / 213.0f));
    }

    @Override
    protected float layout(float top) {
        float cy = top;
        for (Widget wdg : this.header) {
            cy += wdg.layout(this.x, cy, this.w) + 8.0f;
        }
        cy = this.layoutCards(this.cards, this.x, cy, this.w);
        return cy - top;
    }

    protected float layoutCards(List<ModuleCard> list, float left, float top, float width) {
        if (list.isEmpty()) {
            return top;
        }
        int cols = this.columns(width);
        float colW = (width - 8.0f * (float)(cols - 1)) / (float)cols;
        float[] heights = new float[cols];
        for (int i = 0; i < cols; ++i) {
            heights[i] = top;
        }
        for (ModuleCard card : list) {
            int best = 0;
            for (int i = 1; i < cols; ++i) {
                if (!(heights[i] < heights[best] - 0.5f)) continue;
                best = i;
            }
            float ch = card.layout(left + (float)best * (colW + 8.0f), heights[best], colW);
            int n = best;
            heights[n] = heights[n] + (ch + 8.0f);
        }
        float max = top;
        for (float hh : heights) {
            max = Math.max(max, hh);
        }
        return max - 8.0f;
    }

    @Override
    protected void renderContent(UiContext ui) {
        for (Widget wdg : this.header) {
            if (!(wdg.y + wdg.h >= this.y) || !(wdg.y <= this.y + this.h)) continue;
            wdg.render(ui);
        }
        for (ModuleCard card : this.cards) {
            if (!(card.y + card.h >= this.y) || !(card.y <= this.y + this.h)) continue;
            card.render(ui);
        }
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        if (!this.inView(mx, my)) {
            return false;
        }
        for (Widget wdg : this.header) {
            if (!wdg.contains(mx, my) || !wdg.mouseClicked(ui, mx, my, button)) continue;
            return true;
        }
        for (ModuleCard card : this.cards) {
            if (!card.contains(mx, my) || !card.mouseClicked(ui, mx, my, button)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        for (Widget wdg : this.header) {
            if (!wdg.mouseDragged(ui, mx, my, button)) continue;
            return true;
        }
        for (ModuleCard card : this.cards) {
            if (!card.mouseDragged(ui, mx, my, button)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(UiContext ui, double mx, double my, int button) {
        for (Widget wdg : this.header) {
            wdg.mouseReleased(ui, mx, my, button);
        }
        for (ModuleCard card : this.cards) {
            card.mouseReleased(ui, mx, my, button);
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        for (Widget wdg : this.header) {
            if (!wdg.keyPressed(key, scanCode, modifiers)) continue;
            return true;
        }
        for (ModuleCard card : this.cards) {
            if (!card.keyPressed(key, scanCode, modifiers)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Widget wdg : this.header) {
            if (!wdg.charTyped(chr, modifiers)) continue;
            return true;
        }
        return false;
    }
}

