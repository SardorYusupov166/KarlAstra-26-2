/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package astra.config;

import astra.AstraClient;
import astra.config.ConfigManager;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class HudPresetManager {
    public static final String DEFAULT = "\u041f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e";

    private HudPresetManager() {
    }

    public static List<String> list() {
        ArrayList<String> names = new ArrayList<String>();
        names.add(DEFAULT);
        Path dir = ConfigManager.hudPresetsDir();
        if (dir == null || !Files.isDirectory(dir, new LinkOption[0])) {
            return names;
        }
        try (Stream<Path> files = Files.list(dir);){
            files.map(p -> p.getFileName().toString()).filter(n -> n.endsWith(".json")).map(n -> n.substring(0, n.length() - 5)).sorted().forEach(names::add);
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u043f\u0440\u0435\u0441\u0435\u0442\u044b HUD", (Throwable)e);
        }
        return names;
    }

    public static boolean save(String name) {
        if (!ConfigManager.isValidName(name) || DEFAULT.equals(name)) {
            return false;
        }
        ConfigManager.writeJson(ConfigManager.hudPresetsDir().resolve(name.trim() + ".json"), ConfigManager.serializeModules(true));
        return true;
    }

    public static boolean load(String name) {
        if (DEFAULT.equals(name)) {
            ConfigManager.applyModules(new JsonObject(), true, true);
            ConfigManager.markDirty();
            return true;
        }
        JsonObject json = ConfigManager.readJson(ConfigManager.hudPresetsDir().resolve(name + ".json"));
        if (json == null) {
            return false;
        }
        ConfigManager.applyModules(json, true, false);
        ConfigManager.markDirty();
        return true;
    }

    public static boolean delete(String name) {
        if (DEFAULT.equals(name)) {
            return false;
        }
        try {
            return Files.deleteIfExists(ConfigManager.hudPresetsDir().resolve(name + ".json"));
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043f\u0440\u0435\u0441\u0435\u0442 HUD", (Throwable)e);
            return false;
        }
    }
}

