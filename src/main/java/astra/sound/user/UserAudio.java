/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.audio.SoundBuffer
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.client.sounds.FloatSampleSource
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.Util
 */
package astra.sound.user;

import astra.AstraClient;
import astra.sound.user.Decoders;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.FloatSampleSource;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public final class UserAudio {
    public static final String NAMESPACE = "astra_user";
    private static final Map<String, Path> FILES = new ConcurrentHashMap<String, Path>();
    private static final Map<String, CompletableFuture<SoundBuffer>> STATIC = new ConcurrentHashMap<String, CompletableFuture<SoundBuffer>>();

    private UserAudio() {
    }

    public static Identifier id(Path file) {
        String key;
        try {
            key = "f" + Integer.toHexString((file.toAbsolutePath().normalize().toString() + "|" + Files.getLastModifiedTime(file, new LinkOption[0]).toMillis() + "|" + Files.size(file)).hashCode() & Integer.MAX_VALUE);
        }
        catch (Exception e) {
            key = "f" + Integer.toHexString(file.toAbsolutePath().normalize().toString().hashCode() & Integer.MAX_VALUE);
        }
        FILES.put(key, file);
        return Identifier.fromNamespaceAndPath((String)NAMESPACE, (String)key);
    }

    private static String key(Identifier location) {
        String p = location.getPath();
        if (p.startsWith("sounds/")) {
            p = p.substring(7);
        }
        if (p.endsWith(".ogg")) {
            p = p.substring(0, p.length() - 4);
        }
        return p;
    }

    private static Path file(Identifier location) {
        Path f = FILES.get(UserAudio.key(location));
        if (f == null) {
            throw new CompletionException(new IllegalStateException("\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0441\u0432\u043e\u0439 \u0437\u0432\u0443\u043a " + String.valueOf(location)));
        }
        return f;
    }

    public static CompletableFuture<SoundBuffer> loadStatic(Identifier location) {
        return STATIC.computeIfAbsent(key(location), k -> CompletableFuture.supplyAsync(() -> {
            Path file = file(location);
            try (FloatSampleSource source = Decoders.open(file)) {
                ByteBuffer data = source.readAll();
                normalize(data);
                return new SoundBuffer(data, source.getFormat());
            } catch (Exception e) {
                AstraClient.LOGGER.warn("Unable to load custom sound {}", file, e);
                throw new CompletionException(e);
            }
        }, Util.nonCriticalIoPool()));
    }

    private static void normalize(ByteBuffer data) {
        ShortBuffer pcm = data.duplicate().order(ByteOrder.nativeOrder()).asShortBuffer();
        int peak = 0;
        for (int i = 0; i < pcm.limit(); ++i) {
            peak = Math.max(peak, Math.abs(pcm.get(i)));
        }
        if (peak < 64) {
            return;
        }
        float gain = Math.min(60.0f, 29000.0f / (float)peak);
        if (gain < 1.15f) {
            return;
        }
        for (int i = 0; i < pcm.limit(); ++i) {
            pcm.put(i, (short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, Math.round((float)pcm.get(i) * gain))));
        }
    }

    public static CompletableFuture<AudioStream> loadStreamed(Identifier location) {
        return CompletableFuture.supplyAsync(() -> {
            Path f = UserAudio.file(location);
            try {
                return Decoders.open(f);
            }
            catch (Exception e) {
                AstraClient.LOGGER.warn("\u0421\u0432\u043e\u0439 \u0442\u0440\u0435\u043a \u043d\u0435 \u043e\u0442\u043a\u0440\u044b\u043b\u0441\u044f: {} ({})", (Object)f.getFileName(), (Object)e.getMessage());
                throw new CompletionException(e);
            }
        }, (Executor)Util.nonCriticalIoPool());
    }

    public static void forgetMissing() {
        STATIC.keySet().removeIf(k -> {
            Path f = FILES.get(k);
            return f == null || !Files.isRegularFile(f, new LinkOption[0]);
        });
    }
}

