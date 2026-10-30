/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.entity.player.Player
 */
package astra.online;

import astra.AstraClient;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.AuraCosmetic;
import astra.module.modules.cosmetic.CapeCosmetic;
import astra.module.modules.cosmetic.EmoteCosmetic;
import astra.module.modules.cosmetic.HaloCosmetic;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.module.modules.cosmetic.TrailCosmetic;
import astra.module.modules.cosmetic.WingsCosmetic;
import astra.settings.ButtonSetting;
import astra.settings.KeybindSetting;
import astra.settings.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class RemoteCosmetics {
    public static final List<String> SHARED = List.of("wings", "halo", "aura", "cape", "trail", "emotes");
    private static final Map<String, Remote> BY_NAME = new HashMap<String, Remote>();
    private static final Map<String, String> NAME_BY_UUID = new HashMap<String, String>();
    public static boolean showOthers = true;
    public static boolean showEmotes = true;

    private RemoteCosmetics() {
    }

    private static PlayerCosmetic create(String id) {
        return switch (id) {
            case "wings" -> new WingsCosmetic();
            case "halo" -> new HaloCosmetic();
            case "aura" -> new AuraCosmetic();
            case "cape" -> new CapeCosmetic();
            case "trail" -> new TrailCosmetic();
            case "emotes" -> new EmoteCosmetic();
            default -> null;
        };
    }

    private static boolean shareable(Setting<?> s) {
        return s.isPersistent() && !(s instanceof KeybindSetting) && !(s instanceof ButtonSetting);
    }

    public static JsonObject localProfile() {
        JsonObject out = new JsonObject();
        for (String id : SHARED) {
            Module m = ModuleManager.byId(id);
            if (m == null) continue;
            JsonObject o = new JsonObject();
            o.addProperty("on", Boolean.valueOf(m.isEnabled()));
            JsonObject s = new JsonObject();
            for (Setting<?> st : m.getSettings()) {
                if (!RemoteCosmetics.shareable(st)) continue;
                s.add(st.getName(), st.toJson());
            }
            o.add("s", (JsonElement)s);
            out.add(id, (JsonElement)o);
        }
        return out;
    }

    static void apply(String uuid, String name, JsonObject profile) {
        if (name == null || profile == null) {
            return;
        }
        if (uuid != null) {
            NAME_BY_UUID.put(uuid, name);
        }
        String key = name.toLowerCase(Locale.ROOT);
        Remote r = BY_NAME.computeIfAbsent(key, k -> new Remote());
        r.name = name;
        for (String id : SHARED) {
            JsonObject o = profile.has(id) && profile.get(id).isJsonObject() ? profile.getAsJsonObject(id) : null;
            PlayerCosmetic mod = r.mods.get(id);
            if (mod == null) {
                if (o == null) continue;
                try {
                    mod = RemoteCosmetics.create(id);
                }
                catch (RuntimeException e) {
                    AstraClient.LOGGER.warn("Astra Online: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0437\u0434\u0430\u0442\u044c {} \u0434\u043b\u044f {}", new Object[]{id, name, e});
                    continue;
                }
                if (mod == null) continue;
                r.mods.put(id, mod);
            }
            boolean on = o != null && o.has("on") && o.get("on").getAsBoolean();
            mod.setEnabledQuiet(on);
            if (o == null || !o.has("s") || !o.get("s").isJsonObject()) continue;
            JsonObject s = o.getAsJsonObject("s");
            for (Setting<?> st : mod.getSettings()) {
                if (!RemoteCosmetics.shareable(st) || !s.has(st.getName())) continue;
                JsonElement el = s.get(st.getName());
                try {
                    st.fromJson(el);
                }
                catch (RuntimeException runtimeException) {}
            }
        }
    }

    static void remove(String uuid) {
        String name = NAME_BY_UUID.remove(uuid);
        if (name != null) {
            BY_NAME.remove(name.toLowerCase(Locale.ROOT));
        }
    }

    static void clear() {
        BY_NAME.clear();
        NAME_BY_UUID.clear();
    }

    static int count() {
        return BY_NAME.size();
    }

    public static <T extends PlayerCosmetic> T get(Player player, String id) {
        if (!showOthers || player == null) {
            return null;
        }
        Remote r = BY_NAME.get(player.getGameProfile().name().toLowerCase(Locale.ROOT));
        if (r == null) {
            return null;
        }
        NAME_BY_UUID.put(player.getUUID().toString(), r.name);
        PlayerCosmetic m = r.mods.get(id);
        if (m == null || !m.isEnabled()) {
            return null;
        }
        m.owner = player;
        return (T)m;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (!showOthers || mc.level == null || mc.player == null) {
            return;
        }
        for (Player p : mc.level.players()) {
            if (p == mc.player || p.isInvisible()) continue;
            for (String id : new String[]{"trail", "aura", "emotes"}) {
                Object m = RemoteCosmetics.get(p, id);
                if (m == null) continue;
                try {
                    ((Module)m).onTick();
                }
                catch (RuntimeException e) {
                    AstraClient.LOGGER.debug("Astra Online: \u043e\u0448\u0438\u0431\u043a\u0430 {} \u0443 {}", new Object[]{id, p.getName().getString(), e});
                }
            }
        }
    }

    static void emote(String uuid, String emote) {
        if (!showEmotes) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        for (Player p : mc.level.players()) {
            EmoteCosmetic e;
            String name = NAME_BY_UUID.get(uuid);
            boolean match = p.getUUID().toString().equals(uuid) || name != null && p.getGameProfile().name().equalsIgnoreCase(name);
            if (!match) continue;
            Remote r = BY_NAME.get(p.getGameProfile().name().toLowerCase(Locale.ROOT));
            if (r == null) {
                return;
            }
            PlayerCosmetic playerCosmetic = r.mods.get("emotes");
            if (playerCosmetic instanceof EmoteCosmetic && (e = (EmoteCosmetic)playerCosmetic).isEnabled()) {
                e.owner = p;
                e.remoteBurst(emote);
                AstraClient.LOGGER.info("Astra Online: \u044d\u043c\u043e\u0446\u0438\u044f \u00ab{}\u00bb \u043e\u0442 {}", (Object)emote, (Object)p.getGameProfile().name());
            }
            return;
        }
    }

    private static final class Remote {
        String name;
        final Map<String, PlayerCosmetic> mods = new LinkedHashMap<String, PlayerCosmetic>();

        private Remote() {
        }
    }
}

