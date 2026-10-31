/*
 * Decompiled with CFR 0.152.
 */
package astra.sound;

import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.sound.AstraSounds;
import astra.sound.user.SoundPacks;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SoundSlot {
    public static final String RANDOM = "\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439";
    public static Predicate<String> allowed = id -> true;
    public static Supplier<List<SoundPacks.Pack>> activePacks = List::of;
    public static BooleanSupplier replaceBuiltin = () -> true;
    public static final String CUSTOM_PREFIX = "\u0421\u0432\u043e\u0439: ";
    private final String channel;
    private final String[] ids;
    private final String key;
    public final BooleanSetting enabled;
    public final NumberSetting volume;
    public final ModeSetting variant;
    public final ButtonSetting test;

    public SoundSlot(String title, String channel, String[] ids, int defaultVolume, Supplier<Float> master) {
        this(title, channel, ids.length > 0 ? ids[0].replaceAll("_\\d+$", "") : title, ids, defaultVolume, master);
    }

    public SoundSlot(String title, String channel, String eventKey, String[] ids, int defaultVolume, Supplier<Float> master) {
        this.channel = channel;
        this.ids = ids;
        this.key = eventKey;
        SoundPacks.registerEvent(this.key, title);
        String[] modes = new String[ids.length + 1];
        modes[0] = RANDOM;
        for (int i = 0; i < ids.length; ++i) {
            modes[i + 1] = "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 " + (i + 1);
        }
        boolean single = title.startsWith("\u0417\u0432\u0443\u043a");
        String suffix = single ? "" : ": " + title.toLowerCase();
        this.enabled = new BooleanSetting(title, true);
        this.volume = (NumberSetting)new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c" + suffix, defaultVolume, 0.0, 100.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.enabled.get());
        List<String> builtin = List.of(modes);
        this.variant = (ModeSetting)new ModeSetting((String)(single ? "\u0412\u0430\u0440\u0438\u0430\u043d\u0442 \u0437\u0432\u0443\u043a\u0430" : "\u0417\u0432\u0443\u043a" + suffix), RANDOM, modes).dynamic(() -> {
            ArrayList<String> list = new ArrayList<String>(builtin);
            list.addAll(this.customFiles().keySet());
            return list;
        }).visibleWhen(() -> (Boolean)this.enabled.get());
        this.test = (ButtonSetting)new ButtonSetting("\u041f\u0440\u043e\u0441\u043b\u0443\u0448\u0430\u0442\u044c" + suffix, "\u25b6 \u041f\u0440\u043e\u0441\u043b\u0443\u0448\u0430\u0442\u044c" + suffix, () -> this.play(master)).visibleWhen(() -> (Boolean)this.enabled.get());
    }

    public List<Setting<?>> settings() {
        ArrayList list = new ArrayList();
        list.add(this.enabled);
        list.add(this.volume);
        list.add(this.variant);
        list.add(this.test);
        return list;
    }

    public String key() {
        return this.key;
    }

    private Map<String, Path> customFiles() {
        LinkedHashMap<String, Path> map = new LinkedHashMap<String, Path>();
        for (SoundPacks.Pack pack : SoundPacks.packs()) {
            for (Path f : pack.events().getOrDefault(this.key, List.of())) {
                map.put(SoundSlot.label(pack, f), f);
            }
        }
        for (SoundPacks.Pack pack : SoundPacks.packs()) {
            for (Path f : pack.loose()) {
                map.putIfAbsent(SoundSlot.label(pack, f), f);
            }
        }
        return map;
    }

    private static String label(SoundPacks.Pack pack, Path f) {
        return CUSTOM_PREFIX + pack.name() + " / " + String.valueOf(f.getFileName());
    }

    private Object pick() {
        String chosen = (String)this.variant.get();
        if (chosen.startsWith(CUSTOM_PREFIX)) {
            Path f = this.customFiles().get(chosen);
            if (f != null) {
                return f;
            }
        } else {
            int idx = this.variant.index();
            if (idx > 0 && idx <= this.ids.length) {
                return allowed.test(this.ids[idx - 1]) ? this.ids[idx - 1] : null;
            }
        }
        ArrayList<Object> pool = new ArrayList<>();
        for (SoundPacks.Pack pack : activePacks.get()) {
            pool.addAll(pack.events().getOrDefault(this.key, List.of()));
        }
        if (pool.isEmpty() || !replaceBuiltin.getAsBoolean()) {
            for (String id : this.ids) {
                if (!allowed.test(id)) continue;
                pool.add(id);
            }
        }
        return pool.isEmpty() ? null : pool.get(AstraSounds.random().nextInt(pool.size()));
    }

    public String play(Supplier<Float> master) {
        if (!((Boolean)this.enabled.get()).booleanValue()) {
            return null;
        }
        float m = master != null ? master.get().floatValue() : 1.0f;
        Object choice = this.pick();
        if (choice == null) {
            return null;
        }
        float v = this.volume.getFloat() / 100.0f * m;
        if (choice instanceof Path) {
            Path f = (Path)choice;
            AstraSounds.playFile(this.channel, f, v);
            return "user:" + String.valueOf(f.getFileName());
        }
        AstraSounds.play(this.channel, (String)choice, v);
        return (String)choice;
    }
}

