/*
 * Decompiled with CFR 0.152.
 */
package astra.media;

public record NowPlaying(boolean active, String app, String title, String artist, String album, boolean playing, long anchorPos, long anchorAt, long durMs, String cover, boolean canNext, boolean canPrev, boolean canSeek) {
    public static final NowPlaying NONE = new NowPlaying(false, "", "", "", "", false, 0L, 0L, 0L, "", false, false, false);

    public long position() {
        if (!this.playing) {
            return this.anchorPos;
        }
        long p = this.anchorPos + (System.currentTimeMillis() - this.anchorAt);
        return this.durMs > 0L ? Math.min(this.durMs, p) : p;
    }

    public float progress() {
        return this.durMs > 0L ? Math.min(1.0f, (float)this.position() / (float)this.durMs) : 0.0f;
    }

    public boolean hasTrack() {
        return this.active && !this.title.isBlank();
    }

    public String key() {
        return this.app + "|" + this.title + "|" + this.artist;
    }

    public NowPlaying withPlaying(boolean value) {
        long pos = this.position();
        return new NowPlaying(this.active, this.app, this.title, this.artist, this.album, value, pos, System.currentTimeMillis(), this.durMs, this.cover, this.canNext, this.canPrev, this.canSeek);
    }

    public NowPlaying withPosition(long pos) {
        return new NowPlaying(this.active, this.app, this.title, this.artist, this.album, this.playing, pos, System.currentTimeMillis(), this.durMs, this.cover, this.canNext, this.canPrev, this.canSeek);
    }

    public String appName() {
        int slash;
        String a = this.app.toLowerCase();
        if (a.contains("spotify")) {
            return "Spotify";
        }
        if (a.contains("yandex") && a.contains("music")) {
            return "\u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0430";
        }
        if (a.contains("zunemusic") || a.contains("mediaplayer")) {
            return "\u041c\u0435\u0434\u0438\u0430\u043f\u043b\u0435\u0435\u0440";
        }
        if (a.contains("applemusic") || a.contains("itunes")) {
            return "Apple Music";
        }
        if (a.contains("aimp")) {
            return "AIMP";
        }
        if (a.contains("foobar")) {
            return "foobar2000";
        }
        if (a.contains("vlc")) {
            return "VLC";
        }
        if (a.contains("telegram")) {
            return "Telegram";
        }
        if (a.contains("discord")) {
            return "Discord";
        }
        if (a.contains("chrome")) {
            return "Chrome";
        }
        if (a.contains("msedge")) {
            return "Edge";
        }
        if (a.contains("firefox")) {
            return "Firefox";
        }
        if (a.contains("opera")) {
            return "Opera";
        }
        if (a.contains("yandex") || a.contains("browser")) {
            return "\u042f\u043d\u0434\u0435\u043a\u0441 \u0411\u0440\u0430\u0443\u0437\u0435\u0440";
        }
        if (a.isEmpty()) {
            return "\u041f\u041a";
        }
        String s = this.app;
        int bang = s.lastIndexOf(33);
        if (bang >= 0) {
            s = s.substring(bang + 1);
        }
        if ((slash = Math.max(s.lastIndexOf(92), s.lastIndexOf(47))) >= 0) {
            s = s.substring(slash + 1);
        }
        if (s.toLowerCase().endsWith(".exe")) {
            s = s.substring(0, s.length() - 4);
        }
        return s;
    }

    public boolean isBrowser() {
        String a = this.app.toLowerCase();
        return a.contains("chrome") || a.contains("msedge") || a.contains("firefox") || a.contains("opera") || a.contains("browser") || a.contains("brave") || a.contains("vivaldi");
    }

    public static String time(long ms) {
        long s = Math.max(0L, ms / 1000L);
        return s / 60L + ":" + String.format("%02d", s % 60L);
    }
}

