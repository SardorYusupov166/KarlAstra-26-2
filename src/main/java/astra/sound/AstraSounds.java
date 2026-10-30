/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.resources.Identifier
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 */
package astra.sound;

import astra.AstraClient;
import astra.sound.user.UserSoundInstance;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public final class AstraSounds {
    private static final Map<String, SoundEvent> EVENTS = new HashMap<String, SoundEvent>();
    private static final Map<String, SoundInstance> PLAYING = new HashMap<String, SoundInstance>();
    public static boolean singleVoice = true;
    private static SoundInstance voice;
    private static int voicePriority;
    private static final Map<String, Integer> PRIORITY;
    private static final Random RANDOM;

    private static boolean claim(Minecraft mc, String channel) {
        if (!singleVoice) {
            SoundInstance previous = PLAYING.get(channel);
            if (previous != null && mc.getSoundManager().isActive(previous)) {
                mc.getSoundManager().stop(previous);
            }
            return true;
        }
        int pr = PRIORITY.getOrDefault(channel, 2);
        if (voice != null && mc.getSoundManager().isActive(voice)) {
            if (pr < voicePriority) {
                return false;
            }
            mc.getSoundManager().stop(voice);
        }
        voicePriority = pr;
        return true;
    }

    private static void started(String channel, SoundInstance instance) {
        PLAYING.put(channel, instance);
        voice = instance;
    }

    private AstraSounds() {
    }

    public static Random random() {
        return RANDOM;
    }

    public static void play(String channel, String id, float volume) {
        if (volume <= 0.001f) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) {
            return;
        }
        try {
            SoundEvent event = EVENTS.computeIfAbsent(id, k -> SoundEvent.createVariableRangeEvent((Identifier)Identifier.fromNamespaceAndPath((String)"astra", (String)k)));
            if (!AstraSounds.claim(mc, channel)) {
                return;
            }
            SimpleSoundInstance instance = SimpleSoundInstance.forUI((SoundEvent)event, (float)1.0f, (float)Math.min(1.0f, volume));
            mc.getSoundManager().play((SoundInstance)instance);
            AstraSounds.started(channel, (SoundInstance)instance);
        }
        catch (RuntimeException e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0438\u0433\u0440\u0430\u0442\u044c \u0437\u0432\u0443\u043a {}", (Object)id, (Object)e);
        }
    }

    public static void playFile(String channel, Path file, float volume) {
        if (volume <= 0.001f) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) {
            return;
        }
        try {
            if (!AstraSounds.claim(mc, channel)) {
                return;
            }
            UserSoundInstance instance = new UserSoundInstance(file, SoundSource.MASTER, Math.min(1.0f, volume), false);
            mc.getSoundManager().play((SoundInstance)instance);
            AstraSounds.started(channel, (SoundInstance)instance);
        }
        catch (RuntimeException e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0438\u0433\u0440\u0430\u0442\u044c \u0441\u0432\u043e\u0439 \u0437\u0432\u0443\u043a {}", (Object)file.getFileName(), (Object)e);
        }
    }

    static {
        PRIORITY = Map.ofEntries(Map.entry("death", 5), Map.entry("kill", 4), Map.entry("totem", 4), Map.entry("join", 4), Map.entry("break", 3), Map.entry("lowhp", 3), Map.entry("board", 3), Map.entry("hurt", 2), Map.entry("fight", 2), Map.entry("target", 2), Map.entry("hit", 1), Map.entry("zoom", 1), Map.entry("menu", 1), Map.entry("toggle", 1), Map.entry("idle", 1));
        RANDOM = new Random();
    }
}

