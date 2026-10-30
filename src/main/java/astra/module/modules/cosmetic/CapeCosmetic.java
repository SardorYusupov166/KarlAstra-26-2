/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.Identifier
 */
package astra.module.modules.cosmetic;

import astra.cosmetic.CosmeticTextures;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.render.Icon;
import astra.settings.ModeSetting;
import net.minecraft.resources.Identifier;

public class CapeCosmetic
extends PlayerCosmetic {
    private final ModeSetting design = this.add(new ModeSetting("\u041c\u043e\u0434\u0435\u043b\u044c", "Astra", "Astra", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u041d\u043e\u0447\u044c", "\u041d\u0435\u043e\u043d"));

    public CapeCosmetic() {
        super("cape", "\u041f\u043b\u0430\u0449\u0438", "\u041f\u043b\u0430\u0449\u0438 Astra \u043d\u0430 \u0432\u0430\u0448\u0435\u043c \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0435.", Category.COSMETICS, Icon.CAPE);
    }

    public Identifier texture() {
        return this.isEnabled() ? CosmeticTextures.cape((String)this.design.get()) : null;
    }

    public static Identifier capeTexture() {
        CapeCosmetic m = ModuleManager.get(CapeCosmetic.class);
        if (m == null || !m.isEnabled()) {
            return null;
        }
        return CosmeticTextures.cape((String)m.design.get());
    }
}

