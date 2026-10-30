/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ServerData
 */
package astra.waypoint;

import astra.AstraClient;
import astra.config.ConfigManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public final class Waypoints {
    private static final Map<String, List<Waypoint>> BY_WORLD = new HashMap<String, List<Waypoint>>();
    private static boolean loaded;

    private Waypoints() {
    }

    private static Path file() {
        return ConfigManager.root().resolve("waypoints.json");
    }

    public static String worldKey() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        if (mc.getSingleplayerServer() != null) {
            return "local:" + mc.getSingleplayerServer().getWorldData().getLevelName();
        }
        ServerData server = mc.getCurrentServer();
        return server != null ? "server:" + server.ip.toLowerCase(Locale.ROOT) : "unknown";
    }

    public static String dimension() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null ? mc.level.dimension().identifier().toString() : "";
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        JsonObject root = ConfigManager.readJson(Waypoints.file());
        if (root == null) {
            return;
        }
        for (Map.Entry e : root.entrySet()) {
            if (!((JsonElement)e.getValue()).isJsonArray()) continue;
            ArrayList<Waypoint> list = new ArrayList<Waypoint>();
            for (JsonElement el : ((JsonElement)e.getValue()).getAsJsonArray()) {
                try {
                    JsonObject o = el.getAsJsonObject();
                    list.add(new Waypoint(o.get("name").getAsString(), o.get("x").getAsInt(), o.get("y").getAsInt(), o.get("z").getAsInt(), o.get("dim").getAsString(), o.get("color").getAsInt(), o.has("death") && o.get("death").getAsBoolean()));
                }
                catch (RuntimeException ex) {
                    AstraClient.LOGGER.warn("\u041f\u0440\u043e\u043f\u0443\u0449\u0435\u043d\u0430 \u043f\u043e\u0432\u0440\u0435\u0436\u0434\u0451\u043d\u043d\u0430\u044f \u0442\u043e\u0447\u043a\u0430 \u043c\u0430\u0440\u0448\u0440\u0443\u0442\u0430: {}", (Object)el);
                }
            }
            BY_WORLD.put((String)e.getKey(), list);
        }
    }

    private static void save() {
        JsonObject root = new JsonObject();
        for (Map.Entry<String, List<Waypoint>> e : BY_WORLD.entrySet()) {
            if (e.getValue().isEmpty()) continue;
            JsonArray arr = new JsonArray();
            for (Waypoint w : e.getValue()) {
                JsonObject o = new JsonObject();
                o.addProperty("name", w.name);
                o.addProperty("x", (Number)w.x);
                o.addProperty("y", (Number)w.y);
                o.addProperty("z", (Number)w.z);
                o.addProperty("dim", w.dimension);
                o.addProperty("color", (Number)w.color);
                if (w.death) {
                    o.addProperty("death", Boolean.valueOf(true));
                }
                arr.add((JsonElement)o);
            }
            root.add(e.getKey(), (JsonElement)arr);
        }
        ConfigManager.writeJson(Waypoints.file(), root);
    }

    public static List<Waypoint> all() {
        Waypoints.ensureLoaded();
        String key = Waypoints.worldKey();
        if (key == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(BY_WORLD.getOrDefault(key, Collections.emptyList()));
    }

    public static List<Waypoint> here() {
        String dim = Waypoints.dimension();
        ArrayList<Waypoint> out = new ArrayList<Waypoint>();
        for (Waypoint w : Waypoints.all()) {
            if (!w.dimension.equals(dim)) continue;
            out.add(w);
        }
        return out;
    }

    public static Waypoint find(String name) {
        for (Waypoint w : Waypoints.all()) {
            if (!w.name.equalsIgnoreCase(name)) continue;
            return w;
        }
        return null;
    }

    public static Waypoint add(String name, int x, int y, int z, int color, boolean death) {
        Waypoints.ensureLoaded();
        String key = Waypoints.worldKey();
        if (key == null) {
            return null;
        }
        List<Waypoint> list = BY_WORLD.computeIfAbsent(key, k -> new ArrayList());
        list.removeIf(w -> w.name.equalsIgnoreCase(name) || death && w.death);
        Waypoint w2 = new Waypoint(name, x, y, z, Waypoints.dimension(), color, death);
        list.add(w2);
        Waypoints.save();
        return w2;
    }

    public static boolean remove(String name) {
        Waypoints.ensureLoaded();
        List<Waypoint> list = BY_WORLD.get(Waypoints.worldKey());
        if (list == null || !list.removeIf(w -> w.name.equalsIgnoreCase(name))) {
            return false;
        }
        Waypoints.save();
        return true;
    }

    public static int clear() {
        Waypoints.ensureLoaded();
        List<Waypoint> list = BY_WORLD.remove(Waypoints.worldKey());
        Waypoints.save();
        return list == null ? 0 : list.size();
    }

    public static String nextName() {
        int n = 1;
        String name;
        while (Waypoints.find(name = "\u0422\u043e\u0447\u043a\u0430 " + n) != null) {
            ++n;
        }
        return name;
    }

    public static final class Waypoint {
        public String name;
        public int x;
        public int y;
        public int z;
        public String dimension;
        public int color;
        public boolean death;

        public Waypoint(String name, int x, int y, int z, String dimension, int color, boolean death) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.color = color;
            this.death = death;
        }
    }
}

