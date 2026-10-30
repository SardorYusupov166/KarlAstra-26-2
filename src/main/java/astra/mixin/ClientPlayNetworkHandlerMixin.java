/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.network.protocol.game.ClientboundEntityEventPacket
 *  net.minecraft.network.protocol.game.ClientboundSoundPacket
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.hud.CompanionHud;
import astra.module.modules.pvp.PvpTweaks;
import astra.module.modules.pvp.ShieldStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPacketListener.class})
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method={"handleSoundEvent"}, at={@At(value="TAIL")})
    private void astra$shieldBreak(ClientboundSoundPacket packet, CallbackInfo ci) {
        if (packet.getSound().value() == SoundEvents.SHIELD_BREAK.value()) {
            ShieldStatus.onShieldBreakSound(packet.getX(), packet.getY(), packet.getZ());
        }
    }

    @Inject(method={"handleEntityEvent"}, at={@At(value="TAIL")})
    private void astra$totemPop(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (packet.getEventId() != 35) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.onTotemPop(packet.getEntity((Level)mc.level));
        }
        Entity popped = packet.getEntity((Level)mc.level);
        PvpTweaks.onTotemPop(popped);
        if (popped != null && mc.player != null && (popped == mc.player || popped.distanceToSqr((Entity)mc.player) < 1024.0)) {
            CompanionHud.onTotem(popped == mc.player);
        }
    }
}

