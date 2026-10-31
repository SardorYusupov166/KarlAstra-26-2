/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.core.ClientAsset$ResourceTexture
 *  net.minecraft.core.ClientAsset$Texture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.player.PlayerSkin
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.AstraClient;
import astra.module.modules.cosmetic.CapeCosmetic;
import astra.online.RemoteCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractClientPlayer.class})
public abstract class AbstractClientPlayerEntityMixin {
    @Inject(method={"getSkin"}, at=@At(value="RETURN"), cancellable=true)
    private void astra$cape(CallbackInfoReturnable<PlayerSkin> cir) {
        Identifier cape;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        try {
            CapeCosmetic remote;
            cape = (Object)this == mc.player ? CapeCosmetic.capeTexture() : ((remote = (CapeCosmetic)RemoteCosmetics.get((Player)((AbstractClientPlayer)(Object)this), "cape")) != null ? remote.texture() : null);
        }
        catch (RuntimeException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u043e\u0434\u0433\u043e\u0442\u043e\u0432\u0438\u0442\u044c \u043f\u043b\u0430\u0449 Astra", (Throwable)e);
            return;
        }
        if (cape == null) {
            return;
        }
        PlayerSkin s = (PlayerSkin)cir.getReturnValue();
        cir.setReturnValue(new PlayerSkin(s.body(), (ClientAsset.Texture)new ClientAsset.ResourceTexture(cape, cape), s.elytra(), s.model(), s.secure()));
    }
}

