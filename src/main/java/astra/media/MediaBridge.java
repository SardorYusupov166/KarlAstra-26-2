/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.util.Util
 *  net.minecraft.util.Util$OS
 */
package astra.media;

import astra.AstraClient;
import astra.config.ConfigManager;
import astra.media.NowPlaying;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import net.minecraft.util.Util;

public final class MediaBridge {
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Astra media commands");
        t.setDaemon(true);
        return t;
    });
    private static volatile NowPlaying state = NowPlaying.NONE;
    private static volatile NowPlaying devOverride;
    private static volatile String error;
    private static volatile boolean estimated;
    private static Process process;
    private static BufferedWriter stdin;
    private static long nextStart;
    private static boolean hookInstalled;
    private static boolean loggedParseError;

    private MediaBridge() {
    }

    public static NowPlaying state() {
        NowPlaying o = devOverride;
        return o != null ? o : state;
    }

    public static void devOverride(NowPlaying np) {
        devOverride = np;
    }

    public static String error() {
        return error;
    }

    public static boolean estimated() {
        return devOverride == null && estimated && state.active();
    }

    public static boolean supported() {
        return Util.method_668() == Util.OS.field_1133;
    }

    public static boolean running() {
        return process != null && process.isAlive();
    }

    public static void tick(boolean wanted) {
        if (!wanted) {
            if (process != null) {
                MediaBridge.stop();
            }
            return;
        }
        if (!MediaBridge.supported()) {
            error = "\u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 Windows 10/11";
            return;
        }
        if (!MediaBridge.running() && System.currentTimeMillis() >= nextStart) {
            MediaBridge.start();
        }
    }

    private static Path dir() {
        return ConfigManager.root().resolve("media");
    }

    public static Path coversDir() {
        return MediaBridge.dir().resolve("covers");
    }

    private static synchronized void start() {
        nextStart = System.currentTimeMillis() + 8000L;
        try {
            Process p;
            Files.createDirectories(MediaBridge.coversDir(), new FileAttribute[0]);
            MediaBridge.cleanupCovers();
            Path script = MediaBridge.dir().resolve("media_helper.ps1");
            try (InputStream in = MediaBridge.class.getResourceAsStream("/assets/astra/media/media_helper.ps1");){
                if (in == null) {
                    throw new IllegalStateException("media_helper.ps1 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0432 \u043c\u043e\u0434\u0435");
                }
                Files.copy(in, script, StandardCopyOption.REPLACE_EXISTING);
            }
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-File", script.toString(), MediaBridge.coversDir().toString());
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            process = p = pb.start();
            stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8));
            Thread reader = new Thread(() -> MediaBridge.readLoop(p), "Astra media reader");
            reader.setDaemon(true);
            reader.start();
            if (!hookInstalled) {
                hookInstalled = true;
                Runtime.getRuntime().addShutdownHook(new Thread(MediaBridge::stop));
            }
            error = "";
            AstraClient.LOGGER.info("\u00ab\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442\u00bb: \u043f\u043e\u043c\u043e\u0449\u043d\u0438\u043a \u043c\u0435\u0434\u0438\u0430 \u0437\u0430\u043f\u0443\u0449\u0435\u043d");
        }
        catch (Exception e) {
            error = "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u043f\u043e\u043c\u043e\u0449\u043d\u0438\u043a\u0430: " + e.getMessage();
            AstraClient.LOGGER.warn("\u00ab\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442\u00bb: {}", (Object)error);
        }
    }

    private static void readLoop(Process p) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));){
            String line;
            while ((line = r.readLine()) != null) {
                if (!(line = line.replace("\ufeff", "").trim()).startsWith("{")) continue;
                try {
                    MediaBridge.accept(JsonParser.parseString((String)line).getAsJsonObject());
                }
                catch (RuntimeException e) {
                    if (loggedParseError) continue;
                    loggedParseError = true;
                    AstraClient.LOGGER.warn("\u00ab\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442\u00bb: \u043d\u0435 \u0440\u0430\u0437\u043e\u0431\u0440\u0430\u043d\u0430 \u0441\u0442\u0440\u043e\u043a\u0430 \u043f\u043e\u043c\u043e\u0449\u043d\u0438\u043a\u0430: {}", (Object)line, (Object)e);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (process == p) {
            process = null;
            state = NowPlaying.NONE;
        }
    }

    private static void accept(JsonObject o) {
        if (o.has("error") && !o.get("active").getAsBoolean()) {
            error = o.get("error").getAsString();
        }
        if (!o.get("active").getAsBoolean()) {
            state = NowPlaying.NONE;
            return;
        }
        error = "";
        estimated = "window".equals(MediaBridge.str(o, "source"));
        long now = System.currentTimeMillis();
        boolean playing = "Playing".equals(MediaBridge.str(o, "status"));
        long pos = o.get("pos").getAsLong();
        long age = Math.max(0L, o.get("age").getAsLong());
        long reported = playing ? pos + age : pos;
        NowPlaying old = state;
        String title = MediaBridge.str(o, "title");
        String artist = MediaBridge.str(o, "artist");
        long dur = o.get("dur").getAsLong();
        if (dur > 0L) {
            reported = Math.min(reported, dur);
        }
        long anchorPos = reported;
        long anchorAt = now;
        if (old.active() && old.playing() == playing && old.title().equals(title) && old.artist().equals(artist) && Math.abs(old.position() - reported) < 1500L) {
            anchorPos = old.anchorPos();
            anchorAt = old.anchorAt();
        }
        state = new NowPlaying(true, MediaBridge.str(o, "app"), title, artist, MediaBridge.str(o, "album"), playing, anchorPos, anchorAt, dur, MediaBridge.str(o, "cover"), MediaBridge.bool(o, "canNext"), MediaBridge.bool(o, "canPrev"), MediaBridge.bool(o, "canSeek"));
    }

    private static String str(JsonObject o, String key) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : "";
    }

    private static boolean bool(JsonObject o, String key) {
        return o.has(key) && o.get(key).getAsBoolean();
    }

    private static void send(String cmd) {
        BufferedWriter w = stdin;
        if (w == null || !MediaBridge.running()) {
            return;
        }
        WRITER.execute(() -> {
            try {
                w.write(cmd);
                w.newLine();
                w.flush();
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    public static void togglePlay() {
        if (devOverride != null) {
            devOverride = devOverride.withPlaying(!devOverride.playing());
            return;
        }
        NowPlaying s = state;
        if (s.active()) {
            state = s.withPlaying(!s.playing());
        }
        MediaBridge.send("toggle");
    }

    public static void next() {
        MediaBridge.send("next");
    }

    public static void previous() {
        MediaBridge.send("prev");
    }

    public static void seek(long ms) {
        if (devOverride != null) {
            devOverride = devOverride.withPosition(Math.max(0L, ms));
            return;
        }
        NowPlaying s = state;
        if (estimated && s.active()) {
            long p = Math.max(0L, ms);
            state = s.withPosition(p);
            MediaBridge.send("setpos " + p);
            return;
        }
        if (!s.active() || !s.canSeek()) {
            return;
        }
        long clamped = Math.max(0L, s.durMs() > 0L ? Math.min(s.durMs() - 500L, ms) : ms);
        state = s.withPosition(clamped);
        MediaBridge.send("seek " + clamped);
    }

    public static synchronized void stop() {
        Process p = process;
        process = null;
        state = NowPlaying.NONE;
        if (p == null) {
            return;
        }
        try {
            if (stdin != null) {
                stdin.write("quit");
                stdin.newLine();
                stdin.flush();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        p.destroy();
        stdin = null;
    }

    private static void cleanupCovers() {
        long limit = System.currentTimeMillis() - 604800000L;
        try (Stream<Path> files = Files.list(MediaBridge.coversDir());){
            files.forEach(f -> {
                try {
                    if (Files.getLastModifiedTime(f, new LinkOption[0]).toMillis() < limit) {
                        Files.deleteIfExists(f);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            });
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static {
        error = "";
    }
}

