/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerCosmetic
extends Module {
    public Player owner;

    protected PlayerCosmetic(String id, String name, String description, Category category, Icon icon) {
        super(id, name, description, category, icon);
    }

    protected Player player() {
        return this.owner != null ? this.owner : PlayerCosmetic.mc.field_1724;
    }

    public boolean isRemote() {
        return this.owner != null;
    }
}

