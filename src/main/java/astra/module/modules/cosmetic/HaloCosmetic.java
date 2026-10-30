/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.render.Icon;
import astra.settings.ColorSetting;

public class HaloCosmetic
extends PlayerCosmetic {
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442", -11787));

    public HaloCosmetic() {
        super("halo", "\u041d\u0438\u043c\u0431", "\u041f\u0430\u0440\u044f\u0449\u0438\u0439 \u0441\u0432\u0435\u0442\u044f\u0449\u0438\u0439\u0441\u044f \u043d\u0438\u043c\u0431.", Category.COSMETICS, Icon.HALO);
    }

    public int color() {
        return this.color.getColor();
    }
}

