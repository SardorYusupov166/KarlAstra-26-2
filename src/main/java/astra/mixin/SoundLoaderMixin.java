/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.SoundBuffer
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.client.sounds.SoundBufferLibrary
 *  net.minecraft.resources.Identifier
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package astra.mixin;

import astra.sound.user.UserAudio;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SoundBufferLibrary.class})
public abstract class SoundLoaderMixin {
    @Inject(method={"getCompleteBuffer(Lnet/minecraft/class_2960;)Ljava/util/concurrent/CompletableFuture;"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$userStatic(Identifier id, CallbackInfoReturnable<CompletableFuture<SoundBuffer>> cir) {
        if ("astra_user".equals(id.getNamespace())) {
            cir.setReturnValue(UserAudio.loadStatic(id));
        }
    }

    @Inject(method={"getStream"}, at=@At(value="HEAD"), cancellable=true)
    private void astra$userStreamed(Identifier id, boolean repeat, CallbackInfoReturnable<CompletableFuture<AudioStream>> cir) {
        if ("astra_user".equals(id.getNamespace())) {
            cir.setReturnValue(UserAudio.loadStreamed(id));
        }
    }
}

