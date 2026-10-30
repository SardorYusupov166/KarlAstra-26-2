/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.util.Util
 */
package astra.sound.user;

import astra.AstraClient;
import astra.config.ConfigManager;
import astra.sound.user.Decoders;
import astra.sound.user.UserAudio;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.minecraft.util.Util;

public final class SoundPacks {
    public static final String MUSIC_FOLDER = "\u041c\u0443\u0437\u044b\u043a\u0430";
    private static final Map<String, Event> EVENTS = new LinkedHashMap<String, Event>();
    private static volatile List<Pack> packs = List.of();
    private static volatile List<Song> songs = List.of();
    private static volatile long lastScan;
    private static volatile boolean scanning;
    private static String fingerprint;
    private static final String README_PACKS = "\u0417\u0432\u0443\u043a\u043e\u0432\u044b\u0435 \u043f\u0430\u043a\u0438 Astra Anime Client\n================================\n\n\u041a\u0430\u0436\u0434\u0430\u044f \u043f\u0430\u043f\u043a\u0430 \u0437\u0434\u0435\u0441\u044c \u2014 \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u044b\u0439 \u043f\u0430\u043a. \u041c\u043e\u0436\u043d\u043e \u043f\u043e\u043b\u043e\u0436\u0438\u0442\u044c \u0438 .zip-\u0430\u0440\u0445\u0438\u0432 \u043f\u0430\u043a\u0430.\n\u041f\u0440\u043e\u0449\u0435 \u0432\u0441\u0435\u0433\u043e: \u043c\u0435\u043d\u044e Astra (Right Shift) \u2192 \u041a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0430 \u2192 \u00ab\u0410\u043d\u0438\u043c\u0435-\u0437\u0432\u0443\u043a\u0438\u00bb \u2192 \u00ab\u041d\u043e\u0432\u044b\u0439 \u043f\u0430\u043a\u00bb.\n\n\u0412\u043d\u0443\u0442\u0440\u0438 \u043f\u0430\u043a\u0430:\n  \u2022 \u043f\u0430\u043f\u043a\u0438 \u0441\u043e\u0431\u044b\u0442\u0438\u0439 (\u00ab\u0423\u0434\u0430\u0440\u00bb, \u00ab\u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e\u00bb, \u00ab\u0412\u0445\u043e\u0434 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u00bb\u2026) \u2014 \u0444\u0430\u0439\u043b\u044b \u0432 \u043d\u0438\u0445 \u0437\u0432\u0443\u0447\u0430\u0442 \u043d\u0430 \u044d\u0442\u043e \u0441\u043e\u0431\u044b\u0442\u0438\u0435;\n  \u2022 \u043f\u0430\u043f\u043a\u0430 \u00ab\u041c\u0443\u0437\u044b\u043a\u0430\u00bb \u2014 \u043f\u0435\u0441\u043d\u0438 \u043f\u0430\u043a\u0430 \u043f\u043e\u043f\u0430\u0434\u0443\u0442 \u0432 \u00ab\u041c\u0443\u0437\u044b\u043a\u0443 Astra\u00bb;\n  \u2022 \u0444\u0430\u0439\u043b\u044b \u043f\u0440\u044f\u043c\u043e \u0432 \u043f\u0430\u043f\u043a\u0435 \u043f\u0430\u043a\u0430 \u2014 \u00ab\u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b\u0435\u00bb: \u0438\u0445 \u043c\u043e\u0436\u043d\u043e \u043d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043d\u0430 \u043b\u044e\u0431\u043e\u0435 \u0441\u043e\u0431\u044b\u0442\u0438\u0435\n    \u0432 \u043c\u0435\u043d\u044e (\u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u00ab\u0417\u0432\u0443\u043a: \u2026\u00bb \u2192 \u00ab\u0421\u0432\u043e\u0439: \u2026\u00bb).\n\n\u0424\u043e\u0440\u043c\u0430\u0442\u044b: OGG, MP3, WAV. \u041d\u043e\u0432\u044b\u0435 \u0444\u0430\u0439\u043b\u044b \u043f\u043e\u0434\u0445\u0432\u0430\u0442\u044b\u0432\u0430\u044e\u0442\u0441\u044f \u0441\u0430\u043c\u0438 \u0437\u0430 \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u043a\u0443\u043d\u0434.\n";
    private static final String README_PACK = "\u0417\u0430\u043a\u0438\u043d\u044c\u0442\u0435 \u0437\u0432\u0443\u043a\u0438 (OGG, MP3, WAV) \u0432 \u043f\u0430\u043f\u043a\u0438 \u0441\u043e\u0431\u044b\u0442\u0438\u0439 \u2014 \u043e\u043d\u0438 \u0431\u0443\u0434\u0443\u0442 \u0438\u0433\u0440\u0430\u0442\u044c \u043d\u0430 \u044d\u0442\u043e \u0441\u043e\u0431\u044b\u0442\u0438\u0435.\n\u041c\u043e\u0436\u043d\u043e \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0444\u0430\u0439\u043b\u043e\u0432 \u0432 \u043f\u0430\u043f\u043a\u0435: \u043a\u0430\u0436\u0434\u044b\u0439 \u0440\u0430\u0437 \u0431\u0443\u0434\u0435\u0442 \u0437\u0432\u0443\u0447\u0430\u0442\u044c \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439.\n\n\u0424\u0430\u0439\u043b\u044b \u043f\u0440\u044f\u043c\u043e \u0432 \u044d\u0442\u043e\u0439 \u043f\u0430\u043f\u043a\u0435 (\u043d\u0435 \u0432 \u043f\u043e\u0434\u043f\u0430\u043f\u043a\u0430\u0445) \u043c\u043e\u0436\u043d\u043e \u0432\u0440\u0443\u0447\u043d\u0443\u044e \u043d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043d\u0430 \u043b\u044e\u0431\u043e\u0435 \u0441\u043e\u0431\u044b\u0442\u0438\u0435\n\u0432 \u043c\u0435\u043d\u044e: \u00ab\u0410\u043d\u0438\u043c\u0435-\u0437\u0432\u0443\u043a\u0438\u00bb \u2192 \u00ab\u0417\u0432\u0443\u043a: \u2026\u00bb \u2192 \u00ab\u0421\u0432\u043e\u0439: \u2026\u00bb.\n\n\u041f\u0430\u043f\u043a\u0430 \u00ab\u041c\u0443\u0437\u044b\u043a\u0430\u00bb \u2014 \u043f\u0435\u0441\u043d\u0438 \u0434\u043b\u044f \u00ab\u041c\u0443\u0437\u044b\u043a\u0438 Astra\u00bb.\n\u0412 pack.json \u043c\u043e\u0436\u043d\u043e \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u0430\u0432\u0442\u043e\u0440\u0430 \u043f\u0430\u043a\u0430.\n";
    private static final String README_MUSIC = "\u0421\u0432\u043e\u0438 \u043f\u0435\u0441\u043d\u0438 \u0434\u043b\u044f \u00ab\u041c\u0443\u0437\u044b\u043a\u0438 Astra\u00bb\n=============================\n\n\u0417\u0430\u043a\u0438\u043d\u044c\u0442\u0435 \u0441\u044e\u0434\u0430 OGG, MP3 \u0438\u043b\u0438 WAV. \u0418\u043c\u044f \u0444\u0430\u0439\u043b\u0430 \u00ab\u0418\u0441\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c - \u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435.mp3\u00bb\n\u043f\u043e\u043a\u0430\u0436\u0435\u0442\u0441\u044f \u0432 \u043c\u0435\u043d\u044e \u043a\u0430\u043a \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0438 \u0430\u0432\u0442\u043e\u0440 \u0442\u0440\u0435\u043a\u0430.\n\n\u041c\u0435\u043d\u044e Astra \u2192 \u0412\u0438\u0437\u0443\u0430\u043b \u2192 \u00ab\u041c\u0443\u0437\u044b\u043a\u0430 Astra\u00bb \u2192 \u00ab\u0421\u0432\u043e\u0438 \u043f\u0435\u0441\u043d\u0438\u00bb.\n";

    private SoundPacks() {
    }

    public static Path root() {
        return ConfigManager.root().resolve("soundpacks");
    }

    public static Path musicDir() {
        return ConfigManager.root().resolve("music");
    }

    private static Path unpackedDir() {
        return SoundPacks.root().resolve(".unpacked");
    }

    public static Path exportDir() {
        return ConfigManager.root().resolve("export");
    }

    public static synchronized void registerEvent(String key, String title) {
        EVENTS.putIfAbsent(key, new Event(key, title));
    }

    public static List<Pack> packs() {
        return packs;
    }

    public static List<Song> songs() {
        return songs;
    }

    public static Pack pack(String name) {
        for (Pack p : packs) {
            if (!p.name().equals(name)) continue;
            return p;
        }
        return null;
    }

    public static String folderName(String title) {
        return title.replaceAll("[\\\\/:*?\"<>|]", "").trim();
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        if (scanning || now - lastScan < 4000L) {
            return;
        }
        lastScan = now;
        SoundPacks.rescanAsync(false);
    }

    public static void rescanAsync(boolean force) {
        if (scanning) {
            return;
        }
        scanning = true;
        Thread t = new Thread(() -> {
            try {
                SoundPacks.ensureFolders();
                String fp = SoundPacks.fingerprint();
                if (force || !fp.equals(fingerprint)) {
                    fingerprint = fp;
                    SoundPacks.scan();
                }
            }
            catch (Exception e) {
                AstraClient.LOGGER.warn("\u0421\u0432\u043e\u0438 \u0437\u0432\u0443\u043a\u0438: \u043e\u0448\u0438\u0431\u043a\u0430 \u0441\u043a\u0430\u043d\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f", (Throwable)e);
            }
            finally {
                scanning = false;
            }
        }, "Astra sound packs");
        t.setDaemon(true);
        t.start();
    }

    private static String fingerprint() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Path base : List.of(SoundPacks.root(), SoundPacks.musicDir())) {
            Stream<Path> s = Files.walk(base, 3, new FileVisitOption[0]);
            try {
                s.filter(p -> !p.startsWith(SoundPacks.unpackedDir())).sorted().forEach(p -> {
                    try {
                        sb.append(p).append(Files.getLastModifiedTime(p, new LinkOption[0]).toMillis()).append(';');
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                });
            }
            finally {
                if (s == null) continue;
                s.close();
            }
        }
        return Integer.toHexString(sb.toString().hashCode()) + sb.length();
    }

    private static void ensureFolders() throws IOException {
        Path mreadme;
        Files.createDirectories(SoundPacks.root(), new FileAttribute[0]);
        Files.createDirectories(SoundPacks.musicDir(), new FileAttribute[0]);
        Path readme = SoundPacks.root().resolve("\u041a\u0410\u041a \u0414\u041e\u0411\u0410\u0412\u0418\u0422\u042c \u0417\u0412\u0423\u041a\u0418.txt");
        if (!Files.exists(readme, new LinkOption[0])) {
            Files.writeString(readme, (CharSequence)README_PACKS, StandardCharsets.UTF_8, new OpenOption[0]);
        }
        if (!Files.exists(mreadme = SoundPacks.musicDir().resolve("\u041a\u0410\u041a \u0414\u041e\u0411\u0410\u0412\u0418\u0422\u042c \u041f\u0415\u0421\u041d\u0418.txt"), new LinkOption[0])) {
            Files.writeString(mreadme, (CharSequence)README_MUSIC, StandardCharsets.UTF_8, new OpenOption[0]);
        }
    }

    private static void scan() throws IOException {
        ArrayList<Pack> found = new ArrayList<Pack>();
        try (Stream<Path> s = Files.list(SoundPacks.root());){
            for (Path path : s.sorted().toList()) {
                Path dir;
                String n = path.getFileName().toString();
                if (n.startsWith(".")) continue;
                if (Files.isDirectory(path, new LinkOption[0])) {
                    found.add(SoundPacks.readPack(path, n, false));
                    continue;
                }
                if (!n.toLowerCase(Locale.ROOT).endsWith(".zip") || (dir = SoundPacks.unzip(path)) == null) continue;
                found.add(SoundPacks.readPack(dir, n.substring(0, n.length() - 4), true));
            }
        }
        ArrayList<Song> music = new ArrayList<Song>();
        for (Path path : SoundPacks.audioFiles(SoundPacks.musicDir(), 2)) {
            music.add(SoundPacks.song(path, "\u041c\u043e\u0438 \u043f\u0435\u0441\u043d\u0438"));
        }
        for (Pack pack : found) {
            for (Path f : pack.music()) {
                music.add(SoundPacks.song(f, pack.name()));
            }
        }
        packs = Collections.unmodifiableList(found);
        songs = Collections.unmodifiableList(music);
        UserAudio.forgetMissing();
        int sounds = found.stream().mapToInt(Pack::soundCount).sum();
        AstraClient.LOGGER.info("\u0421\u0432\u043e\u0438 \u0437\u0432\u0443\u043a\u0438: \u043f\u0430\u043a\u043e\u0432 {}, \u0437\u0432\u0443\u043a\u043e\u0432 {}, \u043f\u0435\u0441\u0435\u043d {}", new Object[]{found.size(), sounds, music.size()});
    }

    private static Pack readPack(Path dir, String fallbackName, boolean zip) throws IOException {
        String name = fallbackName;
        String author = "";
        Path meta = dir.resolve("pack.json");
        if (Files.isRegularFile(meta, new LinkOption[0])) {
            try {
                JsonObject o = JsonParser.parseString((String)Files.readString(meta, StandardCharsets.UTF_8)).getAsJsonObject();
                if (o.has("author")) {
                    author = o.get("author").getAsString();
                }
            }
            catch (Exception e) {
                AstraClient.LOGGER.warn("\u041f\u0430\u043a {}: \u0431\u0438\u0442\u044b\u0439 pack.json", (Object)fallbackName);
            }
        }
        LinkedHashMap<String, List<Path>> events = new LinkedHashMap<String, List<Path>>();
        ArrayList<Path> loose = new ArrayList<Path>();
        ArrayList<Path> music = new ArrayList<Path>();
        try (Stream<Path> s = Files.list(dir);){
            for (Path p : s.sorted().toList()) {
                if (Files.isDirectory(p, new LinkOption[0])) {
                    List<Path> files;
                    String folder = p.getFileName().toString();
                    if (folder.equalsIgnoreCase(MUSIC_FOLDER) || folder.equalsIgnoreCase("music")) {
                        music.addAll(SoundPacks.audioFiles(p, 2));
                        continue;
                    }
                    String key = SoundPacks.eventFor(folder);
                    if (key == null || (files = SoundPacks.audioFiles(p, 1)).isEmpty()) continue;
                    events.computeIfAbsent(key, k -> new ArrayList()).addAll(files);
                    continue;
                }
                if (!Decoders.supported(p)) continue;
                loose.add(p);
            }
        }
        return new Pack(name, author, dir, zip, events, loose, music);
    }

    private static synchronized String eventFor(String folder) {
        String f = folder.trim().toLowerCase(Locale.ROOT);
        for (Event e : EVENTS.values()) {
            if (!e.key().equalsIgnoreCase(f) && !SoundPacks.folderName(e.title()).toLowerCase(Locale.ROOT).equals(f)) continue;
            return e.key();
        }
        return null;
    }

    private static List<Path> audioFiles(Path dir, int depth) throws IOException {
        if (!Files.isDirectory(dir, new LinkOption[0])) {
            return List.of();
        }
        try (Stream<Path> s = Files.walk(dir, depth, new FileVisitOption[0]);){
            List<Path> list = s.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(Decoders::supported).sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ROOT))).toList();
            return list;
        }
    }

    private static Song song(Path f, String source) {
        String n = f.getFileName().toString();
        int dash = (n = n.substring(0, n.lastIndexOf(46))).indexOf(" - ");
        if (dash < 0) {
            dash = n.indexOf(" \u2014 ");
        }
        if (dash > 0) {
            return new Song(f, n.substring(dash + 3).trim(), n.substring(0, dash).trim(), source);
        }
        return new Song(f, n.replace('_', ' ').trim(), source, source);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static Path unzip(Path zip) {
        try {
            String n = zip.getFileName().toString();
            Path out = SoundPacks.unpackedDir().resolve(n.substring(0, n.length() - 4) + "-" + Files.size(zip));
            if (!Files.isDirectory(out, new LinkOption[0])) {
                Path tmp = SoundPacks.unpackedDir().resolve(".tmp-" + System.nanoTime());
                Files.createDirectories(tmp, new FileAttribute[0]);
                try (ZipInputStream in = new ZipInputStream(Files.newInputStream(zip, new OpenOption[0]), StandardCharsets.UTF_8);){
                    ZipEntry e;
                    while ((e = in.getNextEntry()) != null) {
                        Path target = tmp.resolve(e.getName()).normalize();
                        if (!target.startsWith(tmp)) continue;
                        if (e.isDirectory()) {
                            Files.createDirectories(target, new FileAttribute[0]);
                            continue;
                        }
                        Files.createDirectories(target.getParent(), new FileAttribute[0]);
                        Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                Files.move(tmp, out, new CopyOption[0]);
            }
            try (Stream<Path> s = Files.list(out);){
                List<Path> items = s.toList();
                if (items.size() != 1) return out;
                if (!Files.isDirectory(items.get(0), new LinkOption[0])) return out;
                Path path = items.get(0);
                return path;
            }
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041f\u0430\u043a {} \u043d\u0435 \u0440\u0430\u0441\u043f\u0430\u043a\u043e\u0432\u0430\u043b\u0441\u044f: {}", (Object)zip.getFileName(), (Object)e.getMessage());
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Path createPack() {
        try {
            SoundPacks.ensureFolders();
            Path dir = SoundPacks.root().resolve("\u041c\u043e\u0439 \u043f\u0430\u043a");
            int i = 2;
            while (Files.exists(dir, new LinkOption[0])) {
                dir = SoundPacks.root().resolve("\u041c\u043e\u0439 \u043f\u0430\u043a " + i);
                ++i;
            }
            Files.createDirectories(dir.resolve(MUSIC_FOLDER), new FileAttribute[0]);
            Class<SoundPacks> clazz = SoundPacks.class;
            synchronized (SoundPacks.class) {
                for (Event e : EVENTS.values()) {
                    Files.createDirectories(dir.resolve(SoundPacks.folderName(e.title())), new FileAttribute[0]);
                }
                // ** MonitorExit[var1_3] (shouldn't be in output)
                Files.writeString(dir.resolve("pack.json"), (CharSequence)("{\n  \"name\": \"" + String.valueOf(dir.getFileName()) + "\",\n  \"author\": \"\"\n}\n"), StandardCharsets.UTF_8, new OpenOption[0]);
                Files.writeString(dir.resolve("\u041f\u0420\u041e\u0427\u0422\u0418.txt"), (CharSequence)README_PACK, StandardCharsets.UTF_8, new OpenOption[0]);
                SoundPacks.rescanAsync(true);
                return dir;
            }
        }
        catch (IOException e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0437\u0434\u0430\u0442\u044c \u043f\u0430\u043a", (Throwable)e);
            return null;
        }
    }

    public static Path exportPack(Pack pack) {
        try {
            Files.createDirectories(SoundPacks.exportDir(), new FileAttribute[0]);
            Path zip = SoundPacks.exportDir().resolve(pack.name() + ".zip");
            try (OutputStream os = Files.newOutputStream(zip, new OpenOption[0]);
                 ZipOutputStream out = new ZipOutputStream(os, StandardCharsets.UTF_8);
                 Stream<Path> files = Files.walk(pack.dir(), new FileVisitOption[0]);){
                for (Path f : files.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).toList()) {
                    String rel = pack.name() + "/" + pack.dir().relativize(f).toString().replace('\\', '/');
                    out.putNextEntry(new ZipEntry(rel));
                    try (InputStream in = Files.newInputStream(f, new OpenOption[0]);){
                        in.transferTo(out);
                    }
                    out.closeEntry();
                }
            }
            return zip;
        }
        catch (IOException e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u044d\u043a\u0441\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u0430\u043a {}", (Object)pack.name(), (Object)e);
            return null;
        }
    }

    public static void open(Path dir) {
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
            Util.getPlatform().openFile(dir.toFile());
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 {}", (Object)dir, (Object)e);
        }
    }

    static {
        fingerprint = "";
    }

    private record Event(String key, String title) {
    }

    public record Pack(String name, String author, Path dir, boolean zip, Map<String, List<Path>> events, List<Path> loose, List<Path> music) {
        public int soundCount() {
            int n = this.loose.size();
            for (List<Path> l : this.events.values()) {
                n += l.size();
            }
            return n;
        }
    }

    public record Song(Path file, String title, String artist, String source) {
    }
}

