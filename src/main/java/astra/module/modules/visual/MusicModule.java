/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.SoundInstance
 */
package astra.module.modules.visual;

import astra.AstraClient;
import astra.animation.AnimationUtils;
import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.visual.NowPlayingModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.sound.MusicTrackInstance;
import astra.sound.user.SoundPacks;
import astra.theme.Theme;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.resources.sounds.SoundInstance;

public class MusicModule
extends Module {
    public static final List<Track> TRACKS = List.of(new Track("music_lofi_loop", "Lofi Hip Hop Loop", "omfgdude"), new Track("music_lofi_again", "Lofi again", "omfgdude"), new Track("music_freame", "freame", "James Gargette"), new Track("music_yoiyami_core", "Yoiyami Core Theme", "Yoiyami"), new Track("music_first_light", "First Light Particles", "Yoiyami"));
    private static final String ALL = "\u0412\u0441\u0435 \u043f\u043e \u043f\u043e\u0440\u044f\u0434\u043a\u0443";
    private static final String SHUFFLE = "\u041f\u0435\u0440\u0435\u043c\u0435\u0448\u0430\u0442\u044c";
    private final NumberSetting volume = this.add(new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u043c\u0443\u0437\u044b\u043a\u0438", 60.0, 0.0, 100.0, 5.0).unit("%"));
    private static final String OWN_MIX = "\u0412\u043c\u0435\u0441\u0442\u0435 \u0441\u043e \u0432\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u043c\u0438";
    private static final String OWN_ONLY = "\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0432\u043e\u0438";
    private static final String OWN_OFF = "\u041d\u0435 \u0438\u0433\u0440\u0430\u0442\u044c";
    private final ModeSetting own = this.add((ModeSetting)new ModeSetting("\u0421\u0432\u043e\u0438 \u043f\u0435\u0441\u043d\u0438", "\u0412\u043c\u0435\u0441\u0442\u0435 \u0441\u043e \u0432\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u043c\u0438", "\u0412\u043c\u0435\u0441\u0442\u0435 \u0441\u043e \u0432\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u043c\u0438", "\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0432\u043e\u0438", "\u041d\u0435 \u0438\u0433\u0440\u0430\u0442\u044c").desc("\u041f\u0435\u0441\u043d\u0438 \u0438\u0437 config/astra/music \u0438 \u0438\u0437 \u043f\u0430\u043f\u043e\u043a \u00ab\u041c\u0443\u0437\u044b\u043a\u0430\u00bb \u0437\u0432\u0443\u043a\u043e\u0432\u044b\u0445 \u043f\u0430\u043a\u043e\u0432 (OGG, MP3, WAV)"));
    private final ModeSetting playlist = this.add(new ModeSetting("\u041f\u043b\u0435\u0439\u043b\u0438\u0441\u0442", "\u041f\u0435\u0440\u0435\u043c\u0435\u0448\u0430\u0442\u044c", "\u041f\u0435\u0440\u0435\u043c\u0435\u0448\u0430\u0442\u044c", "\u0412\u0441\u0435 \u043f\u043e \u043f\u043e\u0440\u044f\u0434\u043a\u0443").dynamic(this::modes));
    private final NumberSetting pause = this.add(new NumberSetting("\u041f\u0430\u0443\u0437\u0430 \u043c\u0435\u0436\u0434\u0443 \u0442\u0440\u0435\u043a\u0430\u043c\u0438", 8.0, 0.0, 60.0, 1.0).unit(" \u0441"));
    private final BooleanSetting inMenu = this.add(new BooleanSetting("\u0418\u0433\u0440\u0430\u0442\u044c \u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043c\u0435\u043d\u044e", true));
    private final ButtonSetting next = this.add(new ButtonSetting("\u0422\u0440\u0435\u043a", "\u23ed \u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0438\u0439 \u0442\u0440\u0435\u043a", this::skip));
    private final ButtonSetting openMusic = this.add(new ButtonSetting("\u041f\u0430\u043f\u043a\u0430 \u043f\u0435\u0441\u0435\u043d", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u0441\u0432\u043e\u0438\u0445 \u043f\u0435\u0441\u0435\u043d", () -> SoundPacks.open(SoundPacks.musicDir())).label(() -> "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u0441\u0432\u043e\u0438\u0445 \u043f\u0435\u0441\u0435\u043d (" + SoundPacks.songs().size() + ")"));
    private final Random random = new Random();
    private final List<Integer> order = new ArrayList<Integer>();
    private MusicTrackInstance current;
    private int currentIndex = -1;
    private Track currentTrack;
    private List<Track> list = TRACKS;
    private String listKey = "";
    private int orderPos;
    private int waitTicks;
    private String lastPlaylist = "";

    public MusicModule() {
        super("music", "\u041c\u0443\u0437\u044b\u043a\u0430 Astra", "Lo-fi \u0438 \u0430\u043d\u0438\u043c\u0435-\u043f\u0438\u0430\u043d\u0438\u043d\u043e \u0432\u043c\u0435\u0441\u0442\u043e \u043c\u0443\u0437\u044b\u043a\u0438 Minecraft.", Category.VISUAL, Icon.FILM);
        this.setDefaultEnabled(true);
    }

    private List<String> modes() {
        ArrayList<String> modes = new ArrayList<String>();
        modes.add(SHUFFLE);
        modes.add(ALL);
        for (Track t : this.tracks()) {
            modes.add((String)(t.own() ? "\u266a " + t.title() : t.title()));
        }
        return modes;
    }

    public List<Track> tracks() {
        ArrayList<Track> ownTracks = new ArrayList<Track>();
        for (SoundPacks.Song s : SoundPacks.songs()) {
            ownTracks.add(new Track("user", s.title(), s.artist(), s.file()));
        }
        if (this.own.is(OWN_OFF) || ownTracks.isEmpty()) {
            return TRACKS;
        }
        if (this.own.is(OWN_ONLY)) {
            return ownTracks;
        }
        ArrayList<Track> all = new ArrayList<Track>(TRACKS);
        all.addAll(ownTracks);
        return all;
    }

    private void refreshList() {
        List<Track> t = this.tracks();
        StringBuilder key = new StringBuilder();
        for (Track tr : t) {
            key.append(tr.title()).append(tr.file()).append('|');
        }
        String k = Integer.toHexString(key.toString().hashCode()) + t.size();
        if (k.equals(this.listKey)) {
            return;
        }
        this.listKey = k;
        this.list = t;
        this.currentIndex = this.currentTrack == null ? -1 : this.list.indexOf(this.currentTrack);
        this.rebuildOrder();
    }

    public static boolean replacesVanilla() {
        MusicModule m = ModuleManager.get(MusicModule.class);
        return m != null && m.isEnabled();
    }

    private float targetVolume() {
        return this.volume.getFloat() / 100.0f;
    }

    private boolean playing() {
        return this.current != null && mc.method_1483().method_4877((SoundInstance)this.current);
    }

    private void rebuildOrder() {
        this.order.clear();
        for (int i = 0; i < this.list.size(); ++i) {
            this.order.add(i);
        }
        if (this.playlist.is(SHUFFLE)) {
            Collections.shuffle(this.order, this.random);
        }
        this.orderPos = 0;
    }

    private int nextIndex() {
        int idx;
        String mode = (String)this.playlist.get();
        for (int i = 0; i < this.list.size(); ++i) {
            Track t = this.list.get(i);
            if (!t.title().equals(mode) && !("\u266a " + t.title()).equals(mode)) continue;
            return i;
        }
        if (this.order.isEmpty() || this.orderPos >= this.order.size()) {
            this.rebuildOrder();
        }
        if ((idx = this.order.get(this.orderPos++).intValue()) == this.currentIndex && this.list.size() > 1 && this.orderPos < this.order.size()) {
            idx = this.order.get(this.orderPos++);
        }
        return idx;
    }

    private void start(int index) {
        this.stopCurrent();
        this.currentIndex = index;
        this.currentTrack = this.list.get(index);
        this.current = this.currentTrack.own() ? new MusicTrackInstance(this.currentTrack.file(), this::targetVolume) : new MusicTrackInstance(this.currentTrack.id(), this::targetVolume);
        mc.method_1483().method_4873((SoundInstance)this.current);
        AstraClient.LOGGER.info("\u041c\u0443\u0437\u044b\u043a\u0430 Astra: {} \u2014 {}", (Object)this.currentTrack.title(), (Object)this.currentTrack.author());
    }

    public String nowPlayingTitle() {
        return this.playing() && this.currentTrack != null ? this.currentTrack.title() : null;
    }

    public void skipTrack() {
        this.skip();
    }

    private void stopCurrent() {
        if (this.current != null) {
            mc.method_1483().method_4870((SoundInstance)this.current);
            this.current = null;
        }
    }

    private void skip() {
        if (!this.isEnabled()) {
            return;
        }
        this.waitTicks = 0;
        this.start(this.nextIndex());
    }

    @Override
    protected void onEnable() {
        if (mc.method_1538() != null) {
            mc.method_1538().method_4859();
        }
        this.waitTicks = 40;
    }

    @Override
    protected void onDisable() {
        this.stopCurrent();
    }

    @Override
    public void onTick() {
        boolean allowed;
        if (mc.method_1483() == null) {
            return;
        }
        this.refreshList();
        if (!((String)this.playlist.get()).equals(this.lastPlaylist)) {
            boolean changed = !this.lastPlaylist.isEmpty();
            this.lastPlaylist = (String)this.playlist.get();
            this.rebuildOrder();
            if (changed) {
                this.start(this.nextIndex());
                return;
            }
        }
        boolean bl = allowed = MusicModule.mc.field_1687 != null || (Boolean)this.inMenu.get() != false;
        if (!allowed) {
            if (this.current != null) {
                this.current.fadeOut();
            }
            return;
        }
        if (NowPlayingModule.pcMusicPlaying()) {
            if (this.current != null) {
                this.current.fadeOut();
            }
            if (!this.playing()) {
                this.current = null;
            }
            this.waitTicks = Math.max(this.waitTicks, 60);
            return;
        }
        if (this.playing()) {
            return;
        }
        this.current = null;
        if (this.waitTicks > 0) {
            --this.waitTicks;
            return;
        }
        this.start(this.nextIndex());
        this.waitTicks = this.pause.getInt() * 20;
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 34.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Theme t = ui.theme;
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, ColorUtil.withAlpha(t.bg(), 0.55f));
                boolean on = MusicModule.this.isEnabled() && MusicModule.this.playing();
                for (int i = 0; i < 4; ++i) {
                    float bh = on ? 4.0f + 10.0f * AnimationUtils.pulse(0.45f + (float)i * 0.13f) : 3.0f;
                    float bx = x + 8.0f + (float)i * 4.5f;
                    Render2D.roundRect(ui.ctx, bx, y + h / 2.0f + 7.0f - bh, 3.0f, bh, 1.5f, Paint.vertical(y, y + h, t.accent2(), t.accent()));
                }
                Track track = on ? MusicModule.this.currentTrack : null;
                Fonts.draw(ui.ctx, track != null ? track.title() : (MusicModule.this.isEnabled() ? "\u041f\u0430\u0443\u0437\u0430 \u043c\u0435\u0436\u0434\u0443 \u0442\u0440\u0435\u043a\u0430\u043c\u0438\u2026" : "\u0412\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u043e"), x + 30.0f, y + 7.0f, 7.4f, t.text(), true);
                Fonts.draw(ui.ctx, (String)(track != null ? track.author() + (track.own() ? " \u00b7 \u0441\u0432\u043e\u044f \u043f\u0435\u0441\u043d\u044f" : " \u00b7 CC0") : "\u0412\u0430\u043d\u0438\u043b\u044c\u043d\u0430\u044f \u043c\u0443\u0437\u044b\u043a\u0430 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0430, \u043f\u043e\u043a\u0430 \u043c\u043e\u0434\u0443\u043b\u044c \u0432\u043a\u043b\u044e\u0447\u0451\u043d"), x + 30.0f, y + 19.0f, 6.0f, t.textMuted(), false);
            }
        };
    }

    public record Track(String id, String title, String author, Path file) {
        public Track(String id, String title, String author) {
            this(id, title, author, null);
        }

        public boolean own() {
            return this.file != null;
        }
    }
}

