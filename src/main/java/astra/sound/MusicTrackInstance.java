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
        this(SoundEvent.createVariableRangeEvent((Identifier)Identifier.fromNamespaceAndPath((String)"astra", (String)id)), null, targetVolume);
    }

    public MusicTrackInstance(Path file, Supplier<Float> targetVolume) {
        this(SoundEvent.createVariableRangeEvent((Identifier)UserAudio.id(file)), file, targetVolume);
    }

    private MusicTrackInstance(SoundEvent event, Path file, Supplier<Float> targetVolume) {
        super(event, SoundSource.MUSIC, RandomSource.create());
        this.file = file;
        this.targetVolume = targetVolume;
        this.looping = false;
        this.relative = true;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.volume = 1.0E-4f;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public WeighedSoundEvents resolve(SoundManager manager) {
        if (this.file == null) {
            return super.resolve(manager);
        }
        WeighedSoundEvents set = UserSoundInstance.soundSet(this.getIdentifier(), true);
        this.sound = set.getSound(RandomSource.create());
        return set;
    }

    public void fadeOut() {
        this.fadingOut = true;
    }

    public void tick() {
        if (this.fadingOut) {
            this.fade -= 0.05f;
            if (this.fade <= 0.0f) {
                this.stop();
                return;
            }
        } else if (this.fade < 1.0f) {
            this.fade = Math.min(1.0f, this.fade + 0.025f);
        }
        this.volume = Math.max(1.0E-4f, this.targetVolume.get().floatValue() * this.fade);
    }

    public boolean canStartSilent() {
        return true;
    }
}

