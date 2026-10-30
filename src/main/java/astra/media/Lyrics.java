/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package astra.media;

import astra.AstraClient;
import astra.media.MediaBridge;
import astra.media.NowPlaying;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Lyrics {
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build();
    private static final Pattern TIME = Pattern.compile("\\[(\\d+):(\\d+(?:[.:]\\d+)?)]");
    private static final String UA = "AstraAnimeClient/0.1.0 (Minecraft mod)";
    private static String key = "";
    private static volatile Status status = Status.NONE;
    private static volatile List<Line> lines = List.of();
    private static int generation;

    private Lyrics() {
    }

    public static Status status() {
        return status;
    }

    public static List<Line> lines() {
        return lines;
    }

    public static void update(NowPlaying np, boolean enabled) {
        String k;
        String string = k = enabled && np.hasTrack() ? np.key() : "";
        if (k.equals(key)) {
            return;
        }
        key = k;
        int gen = ++generation;
        lines = List.of();
        if (k.isEmpty()) {
            status = Status.NONE;
            return;
        }
        status = Status.LOADING;
        String[] ta = Lyrics.clean(np.title(), np.artist(), np.isBrowser());
        String album = np.album();
        long durSec = np.durMs() / 1000L;
        Thread t = new Thread(() -> Lyrics.load(gen, ta[0], ta[1], album, durSec), "Astra lyrics");
        t.setDaemon(true);
        t.start();
    }

    public static int indexAt(long pos) {
        List<Line> l = lines;
        int lo = 0;
        int hi = l.size() - 1;
        int ans = -1;
        while (lo <= hi) {
            int mid = lo + hi >>> 1;
            if (l.get(mid).time() <= pos) {
                ans = mid;
                lo = mid + 1;
                continue;
            }
            hi = mid - 1;
        }
        return ans;
    }

    static String[] clean(String title, String artist, boolean browser) {
        String t = title.replaceAll("(?i)[(\\[][^)\\]]*(official|video|lyric|audio|\u043a\u043b\u0438\u043f|\u043f\u0440\u0435\u043c\u044c\u0435\u0440\u0430|mv|hd|4k|visualizer|\u0442\u0435\u043a\u0441\u0442)[^)\\]]*[)\\]]", "").replaceAll("\\s*\\|.*$", "").trim();
        String a = artist.replaceAll("(?i)\\s*-\\s*topic$", "").replaceAll("(?i)vevo$", "").trim();
        int dash = t.indexOf(" - ");
        if (dash < 0) {
            dash = t.indexOf(" \u2014 ");
        }
        if (dash > 0 && (a.isEmpty() || browser || t.toLowerCase().startsWith(a.toLowerCase()))) {
            a = t.substring(0, dash).trim();
            t = t.substring(dash + 3).trim();
        }
        t = t.replaceAll("(?i)\\s+-\\s+[^-]*(remaster|version|edit|mix|live|mono|stereo)[^-]*$", "");
        t = t.replaceAll("(?i)\\s*[(\\[]?\\s*(feat\\.?|ft\\.?)\\s+[^)\\]]*[)\\]]?", "").trim();
        t = t.replaceAll("(?i)\\s*[(\\[]\\s*with\\s+[^)\\]]*[)\\]]", "").trim();
        return new String[]{t, a};
    }

    private static Path cacheFile(String title, String artist) {
        String name = Integer.toHexString((artist.toLowerCase() + "|" + title.toLowerCase()).hashCode());
        return MediaBridge.coversDir().getParent().resolve("lyrics").resolve(name + ".lrc");
    }

    private static void load(int gen, String title, String artist, String album, long durSec) {
        Path cache = Lyrics.cacheFile(title, artist);
        try {
            String text;
            if (Files.isRegularFile(cache, new LinkOption[0])) {
                boolean stale;
                text = Files.readString(cache, StandardCharsets.UTF_8);
                boolean bl = stale = text.startsWith("#none") && System.currentTimeMillis() - Files.getLastModifiedTime(cache, new LinkOption[0]).toMillis() > 86400000L;
                if (!stale) {
                    Lyrics.publish(gen, text);
                    return;
                }
            }
            text = Lyrics.fetch(title, artist, album, durSec);
            Files.createDirectories(cache.getParent(), new FileAttribute[0]);
            Files.writeString(cache, (CharSequence)text, StandardCharsets.UTF_8, new OpenOption[0]);
            Lyrics.publish(gen, text);
        }
        catch (Exception e) {
            if (gen == generation) {
                status = Status.ERROR;
            }
            AstraClient.LOGGER.debug("\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438 \u043d\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u043b\u0441\u044f: {} \u2014 {}", new Object[]{artist, title, e});
        }
    }

    private static String fetch(String title, String artist, String album, long durSec) throws Exception {
        if (!artist.isEmpty()) {
            String found;
            HttpResponse<String> r;
            StringBuilder q = new StringBuilder("https://lrclib.net/api/get?track_name=").append(Lyrics.enc(title)).append("&artist_name=").append(Lyrics.enc(artist));
            if (!album.isBlank()) {
                q.append("&album_name=").append(Lyrics.enc(album));
            }
            if (durSec > 0L) {
                q.append("&duration=").append(durSec);
            }
            if ((r = Lyrics.get(q.toString())).statusCode() == 200 && (found = Lyrics.pick(JsonParser.parseString((String)r.body()).getAsJsonObject())) != null) {
                return found;
            }
        }
        String main = artist.split("\\s*(,|&|;| x | feat\\.?| ft\\.?)\\s*")[0].trim();
        String url = "https://lrclib.net/api/search?track_name=" + Lyrics.enc(title) + (String)(main.isEmpty() ? "" : "&artist_name=" + Lyrics.enc(main));
        HttpResponse<String> r = Lyrics.get(url);
        if (r.statusCode() != 200) {
            return "#none";
        }
        JsonArray arr = JsonParser.parseString((String)r.body()).getAsJsonArray();
        Object plain = null;
        for (JsonElement el : arr) {
            JsonObject o = el.getAsJsonObject();
            double d = o.has("duration") && !o.get("duration").isJsonNull() ? o.get("duration").getAsDouble() : 0.0;
            boolean durOk = durSec <= 0L || d <= 0.0 || Math.abs(d - (double)durSec) <= 4.0;
            if (!durOk) continue;
            if (o.has("syncedLyrics") && !o.get("syncedLyrics").isJsonNull()) {
                return o.get("syncedLyrics").getAsString();
            }
            if (plain != null || !o.has("plainLyrics") || o.get("plainLyrics").isJsonNull()) continue;
            plain = "#plain\n" + o.get("plainLyrics").getAsString();
        }
        return plain != null ? plain : "#none";
    }

    private static String pick(JsonObject o) {
        if (o.has("instrumental") && o.get("instrumental").getAsBoolean()) {
            return "#none";
        }
        if (o.has("syncedLyrics") && !o.get("syncedLyrics").isJsonNull()) {
            return o.get("syncedLyrics").getAsString();
        }
        if (o.has("plainLyrics") && !o.get("plainLyrics").isJsonNull()) {
            return "#plain\n" + o.get("plainLyrics").getAsString();
        }
        return null;
    }

    private static HttpResponse<String> get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10L)).header("User-Agent", UA).GET().build();
        return HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static void publish(int gen, String text) {
        if (gen != generation) {
            return;
        }
        if (text.startsWith("#none")) {
            lines = List.of();
            status = Status.NOT_FOUND;
            return;
        }
        if (text.startsWith("#plain")) {
            ArrayList<Line> out = new ArrayList<Line>();
            for (String l : text.substring(6).split("\\R")) {
                if (l.isBlank()) continue;
                out.add(new Line(-1L, l.trim()));
            }
            lines = Collections.unmodifiableList(out);
            status = Status.PLAIN;
            return;
        }
        ArrayList<Line> out = new ArrayList<Line>();
        for (String l : text.split("\\R")) {
            Matcher m = TIME.matcher(l);
            ArrayList<Long> times = new ArrayList<Long>();
            int end = 0;
            while (m.find() && m.start() == end) {
                long min = Long.parseLong(m.group(1));
                double sec = Double.parseDouble(m.group(2).replace(':', '.'));
                times.add(min * 60000L + Math.round(sec * 1000.0));
                end = m.end();
            }
            if (times.isEmpty()) continue;
            String lyric = l.substring(end).trim();
            Iterator iterator = times.iterator();
            while (iterator.hasNext()) {
                long t = (Long)iterator.next();
                out.add(new Line(t, lyric));
            }
        }
        out.sort((a, b) -> Long.compare(a.time(), b.time()));
        lines = Collections.unmodifiableList(out);
        status = out.isEmpty() ? Status.NOT_FOUND : Status.SYNCED;
    }

    public static enum Status {
        NONE,
        LOADING,
        SYNCED,
        PLAIN,
        NOT_FOUND,
        ERROR;

    }

    public record Line(long time, String text) {
    }
}

