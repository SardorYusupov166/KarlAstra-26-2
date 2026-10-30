/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.client.sounds.WeighedSoundEvents
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 */
package astra.sound;

import astra.sound.user.UserAudio;
import astra.sound.user.UserSoundInstance;
import java.nio.file.Path;
import java.util.function.Supplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public final class MusicTrackInstance
extends AbstractTickableSoundInstance {
    private static final float FADE_TICKS = 40.0f;
    private final Supplier<Float> targetVolume;
    private final Path file;
    private float fade;
    private boolean fadingOut;

    public MusicTrackInstance(String id, Supplier<Float> targetVolume) {
        this(SoundEvent.method_47908((Identifier)Identifier.method_60655((String)"astra", (String)id)), null, targetVolume);
    }

    public MusicTrackInstance(Path file, Supplier<Float> targetVolume) {
        this(SoundEvent.method_47908((Identifier)UserAudio.id(file)), file, targetVolume);
    }

    private MusicTrackInstance(SoundEvent event, Path file, Supplier<Float> targetVolume) {
        super(event, SoundSource.field_15253, RandomSource.method_43047());
        this.file = file;
        this.targetVolume = targetVolume;
        this.field_5446 = false;
        this.field_18936 = true;
        this.field_5440 = SoundInstance.Attenuation.field_5478;
        this.field_5442 = 1.0E-4f;
        this.field_5439 = 0.0;
        this.field_5450 = 0.0;
        this.field_5449 = 0.0;
    }

    public WeighedSoundEvents method_4783(SoundManager manager) {
        if (this.file == null) {
            return super.method_4783(manager);
        }
        WeighedSoundEvents set = UserSoundInstance.soundSet(this.method_4775(), true);
        this.field_5444 = set.method_4887(RandomSource.method_43047());
        return set;
    }

    public void fadeOut() {
        this.fadingOut = true;
    }

    public void method_16896() {
        if (this.fadingOut) {
            this.fade -= 0.05f;
            if (this.fade <= 0.0f) {
                this.method_24876();
                return;
            }
        } else if (this.fade < 1.0f) {
            this.fade = Math.min(1.0f, this.fade + 0.025f);
        }
        this.field_5442 = Math.max(1.0E-4f, this.targetVolume.get().floatValue() * this.fade);
    }

    public boolean method_4785() {
        return true;
    }
}

