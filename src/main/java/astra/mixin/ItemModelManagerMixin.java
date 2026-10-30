/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.item.ItemModelResolver
 *  net.minecraft.client.renderer.item.ItemStackRenderState
 *  net.minecraft.world.entity.ItemOwner
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.module.modules.pvp.ShieldStatus;
import astra.render.TintedItemState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemModelResolver.class})
public abstract class ItemModelManagerMixin {
    @Inject(method={"appendItemLayers"}, at=@At(value="TAIL"))
    private void astra$shieldTint(ItemStackRenderState state, ItemStack stack, ItemDisplayContext context, Level world, ItemOwner holder, int seed, CallbackInfo ci) {
        int tint = ShieldStatus.tint(holder != null ? holder.asLivingEntity() : null, stack);
        ((TintedItemState)state).astra$setTint(tint);
        if (tint != 0) {
            state.appendModelIdentityElement((Object)tint);
        }
    }
}

