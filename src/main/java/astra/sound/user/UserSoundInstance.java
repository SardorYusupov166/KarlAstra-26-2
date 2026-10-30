/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.AbstractSoundInstance
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.Sound$Type
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.client.sounds.Weighted
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 */
package astra.sound.user;

import astra.sound.user.UserAudio;
import java.nio.file.Path;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class UserSoundInstance
extends AbstractSoundInstance {
    private final boolean stream;

    public UserSoundInstance(Path file, SoundSource category, float volume, boolean stream) {
        this(UserAudio.id(file), category, volume, stream);
    }

    protected UserSoundInstance(Identifier id, SoundSource category, float volume, boolean stream) {
        super(id, category, SoundInstance.createUnseededRandom());
        this.stream = stream;
        this.volume = volume;
        this.pitch = 1.0f;
        this.relative = true;
        this.attenuation = SoundInstance.Attenuation.NONE;
    }

    public static WeighedSoundEvents soundSet(Identifier id, boolean stream) {
        WeighedSoundEvents set = new WeighedSoundEvents(id, null);
        set.addSound((Weighted)UserSoundInstance.sound(id, stream));
        return set;
    }

    static Sound sound(Identifier id, boolean stream) {
        return new Sound(id, r -> 1.0f, r -> 1.0f, 1, Sound.Type.FILE, stream, false, 16);
    }

    public WeighedSoundEvents resolve(SoundManager manager) {
        WeighedSoundEvents set = UserSoundInstance.soundSet(this.identifier, this.stream);
        this.sound = set.getSound(RandomSource.create());
        return set;
    }

    public boolean canStartSilent() {
        return true;
    }
}

