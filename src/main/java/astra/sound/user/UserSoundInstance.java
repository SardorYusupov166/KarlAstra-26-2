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
        super(id, category, SoundInstance.method_43221());
        this.stream = stream;
        this.field_5442 = volume;
        this.field_5441 = 1.0f;
        this.field_18936 = true;
        this.field_5440 = SoundInstance.Attenuation.field_5478;
    }

    public static WeighedSoundEvents soundSet(Identifier id, boolean stream) {
        WeighedSoundEvents set = new WeighedSoundEvents(id, null);
        set.method_4885((Weighted)UserSoundInstance.sound(id, stream));
        return set;
    }

    static Sound sound(Identifier id, boolean stream) {
        return new Sound(id, r -> 1.0f, r -> 1.0f, 1, Sound.Type.field_5474, stream, false, 16);
    }

    public WeighedSoundEvents method_4783(SoundManager manager) {
        WeighedSoundEvents set = UserSoundInstance.soundSet(this.field_5448, this.stream);
        this.field_5444 = set.method_4887(RandomSource.method_43047());
        return set;
    }

    public boolean method_4785() {
        return true;
    }
}

