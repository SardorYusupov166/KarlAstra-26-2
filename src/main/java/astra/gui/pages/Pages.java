/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.pages;

import astra.gui.pages.CosmeticsPage;
import astra.gui.pages.HudPage;
import astra.gui.pages.ModulesPage;
import astra.gui.pages.Page;
import astra.gui.pages.ProfilesPage;
import astra.gui.pages.ReplaysPage;
import astra.gui.pages.SoundsPage;
import astra.gui.pages.ThemesPage;
import astra.gui.screens.ClickGuiScreen;
import astra.module.Category;

public final class Pages {
    private Pages() {
    }

    public static Page create(ClickGuiScreen screen, Category category) {
        return switch (category) {
            case Category.HUD -> new HudPage(screen);
            case Category.COSMETICS -> new CosmeticsPage(screen);
            case Category.REPLAYS -> new ReplaysPage(screen);
            case Category.PROFILES -> new ProfilesPage(screen);
            case Category.THEMES -> new ThemesPage(screen);
            case Category.SOUNDS -> new SoundsPage(screen);
            default -> new ModulesPage(screen, category);
        };
    }
}

