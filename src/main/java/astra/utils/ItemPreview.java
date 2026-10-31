package astra.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/** Item components arrive with the world registries in Minecraft 26.2. */
public final class ItemPreview {
    private ItemPreview() {}

    public static ItemStack stack(ItemLike item) {
        return stack(item, 1);
    }

    public static ItemStack stack(ItemLike item, int count) {
        var holder = item.asItem().builtInRegistryHolder();
        return holder.areComponentsBound() ? new ItemStack(holder, count) : ItemStack.EMPTY;
    }
}
