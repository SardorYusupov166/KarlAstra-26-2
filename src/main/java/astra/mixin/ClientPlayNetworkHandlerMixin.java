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
    @Inject(method={"method_11146"}, at={@At(value="TAIL")})
    private void astra$shieldBreak(ClientboundSoundPacket packet, CallbackInfo ci) {
        if (packet.method_11894().comp_349() == SoundEvents.field_15239.comp_349()) {
            ShieldStatus.onShieldBreakSound(packet.method_11890(), packet.method_11889(), packet.method_11893());
        }
    }

    @Inject(method={"method_11148"}, at={@At(value="TAIL")})
    private void astra$totemPop(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (packet.method_11470() != 35) {
            return;
        }
        Minecraft mc = Minecraft.method_1551();
        if (mc.field_1687 == null) {
            return;
        }
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.onTotemPop(packet.method_11469((Level)mc.field_1687));
        }
        Entity popped = packet.method_11469((Level)mc.field_1687);
        PvpTweaks.onTotemPop(popped);
        if (popped != null && mc.field_1724 != null && (popped == mc.field_1724 || popped.method_5858((Entity)mc.field_1724) < 1024.0)) {
            CompanionHud.onTotem(popped == mc.field_1724);
        }
    }
}

