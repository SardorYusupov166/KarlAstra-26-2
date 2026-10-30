/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.loader.api.FabricLoader
 */
package astra.config;

import astra.AstraClient;
import astra.hud.HudModule;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.settings.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Pattern NAME = Pattern.compile("[\\p{L}\\p{N} _.\\-]{1,32}");
    public static final String DEFAULT_PROFILE = "Astra.purple";
    private static Path root;
    private static Path profilesDir;
    private static Path hudPresetsDir;
    private static String activeProfile;
    private static boolean dirty;
    private static long dirtySince;
    private static boolean loading;

    private ConfigManager() {
    }

    public static void init() {
        root = FabricLoader.getInstance().getConfigDir().resolve("astra");
        profilesDir = root.resolve("profiles");
        hudPresetsDir = root.resolve("hud_presets");
        try {
            Files.createDirectories(profilesDir, new FileAttribute[0]);
            Files.createDirectories(hudPresetsDir, new FileAttribute[0]);
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0437\u0434\u0430\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u0438", (Throwable)e);
        }
        JsonObject global = ConfigManager.readJson(root.resolve("astra.json"));
        if (global != null && global.has("profile")) {
            activeProfile = global.get("profile").getAsString();
        }
        if (!Files.exists(ConfigManager.profileFile(activeProfile), new LinkOption[0])) {
            List<String> existing = ConfigManager.listProfiles();
            activeProfile = existing.isEmpty() ? DEFAULT_PROFILE : existing.get(0);
        }
        ConfigManager.loadProfile(activeProfile);
    }

    public static Path root() {
        return root;
    }

    public static Path hudPresetsDir() {
        return hudPresetsDir;
    }

    public static Path profilesDir() {
        return profilesDir;
    }

    public static void markDirty() {
        if (loading) {
            return;
        }
        dirty = true;
        dirtySince = System.currentTimeMillis();
    }

    public static void tick() {
        if (dirty && System.currentTimeMillis() - dirtySince > 1500L) {
            ConfigManager.save();
        }
    }

    public static String activeProfile() {
        return activeProfile;
    }

    public static boolean isValidName(String name) {
        return name != null && NAME.matcher(name.trim()).matches();
    }

    private static Path profileFile(String name) {
        return profilesDir.resolve(name + ".json");
    }

    public static List<String> listProfiles() {
        ArrayList<String> names = new ArrayList<String>();
        if (profilesDir == null || !Files.isDirectory(profilesDir, new LinkOption[0])) {
            return names;
        }
        try (Stream<Path> files = Files.list(profilesDir);){
            files.filter(p -> p.getFileName().toString().endsWith(".json")).map(p -> p.getFileName().toString().replaceFirst("\\.json$", "")).sorted(Comparator.comparing(s -> s.toLowerCase(Locale.ROOT))).forEach(names::add);
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u043f\u0440\u043e\u0444\u0438\u043b\u0435\u0439", (Throwable)e);
        }
        if (!names.contains(activeProfile)) {
            names.add(0, activeProfile);
        }
        return names;
    }

    public static void save() {
        dirty = false;
        ConfigManager.writeJson(ConfigManager.profileFile(activeProfile), ConfigManager.serializeModules(false));
        JsonObject global = new JsonObject();
        global.addProperty("profile", activeProfile);
        ConfigManager.writeJson(root.resolve("astra.json"), global);
    }

    public static JsonObject serializeModules(boolean onlyHud) {
        JsonObject modules = new JsonObject();
        for (Module module : ModuleManager.all()) {
            if (onlyHud && !(module instanceof HudModule)) continue;
            JsonObject obj = new JsonObject();
            obj.addProperty("enabled", Boolean.valueOf(module.isEnabled()));
            JsonObject settings = new JsonObject();
            for (Setting<?> s : module.getSettings()) {
                if (!s.isPersistent()) continue;
                settings.add(s.getName(), s.toJson());
            }
            obj.add("settings", (JsonElement)settings);
            if (module instanceof HudModule) {
                HudModule hud = (HudModule)module;
                JsonObject pos = new JsonObject();
                pos.addProperty("x", (Number)Float.valueOf(hud.getPosX()));
                pos.addProperty("y", (Number)Float.valueOf(hud.getPosY()));
                obj.add("hud", (JsonElement)pos);
            }
            modules.add(module.getId(), (JsonElement)obj);
        }
        JsonObject root = new JsonObject();
        root.addProperty("version", (Number)1);
        root.add("modules", (JsonElement)modules);
        return root;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void applyModules(JsonObject json, boolean onlyHud, boolean resetMissing) {
        loading = true;
        try {
            JsonObject modules = json != null && json.has("modules") ? json.getAsJsonObject("modules") : new JsonObject();
            for (Module module : ModuleManager.all()) {
                if (onlyHud && !(module instanceof HudModule)) continue;
                JsonElement el = modules.get(module.getId());
                if (el == null || !el.isJsonObject()) {
                    if (!resetMissing) continue;
                    ConfigManager.resetModule(module);
                    continue;
                }
                JsonObject obj = el.getAsJsonObject();
                JsonObject settings = obj.has("settings") ? obj.getAsJsonObject("settings") : new JsonObject();
                for (Setting<?> s : module.getSettings()) {
                    if (!s.isPersistent()) continue;
                    if (settings.has(s.getName())) {
                        s.fromJson(settings.get(s.getName()));
                        continue;
                    }
                    if (!resetMissing) continue;
                    s.reset();
                }
                if (module instanceof HudModule) {
                    HudModule hud = (HudModule)module;
                    if (obj.has("hud")) {
                        JsonObject pos = obj.getAsJsonObject("hud");
                        hud.setPos(pos.get("x").getAsFloat(), pos.get("y").getAsFloat());
                    }
                }
                boolean enabled = obj.has("enabled") ? obj.get("enabled").getAsBoolean() : module.isDefaultEnabled();
                module.applyLoadedState(enabled);
                for (Setting<?> s : module.getSettings()) {
                    s.fireChange();
                }
            }
        }
        catch (RuntimeException e) {
            AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438\u043c\u0435\u043d\u0435\u043d\u0438\u044f \u043a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u0438", (Throwable)e);
        }
        finally {
            loading = false;
        }
    }

    private static void resetModule(Module module) {
        for (Setting<?> s : module.getSettings()) {
            if (!s.isPersistent()) continue;
            s.reset();
        }
        if (module instanceof HudModule) {
            HudModule hud = (HudModule)module;
            hud.resetPosition();
        }
        module.applyLoadedState(module.isDefaultEnabled());
    }

    public static void loadProfile(String name) {
        if (dirty && !name.equals(activeProfile)) {
            ConfigManager.save();
        }
        activeProfile = name;
        JsonObject json = ConfigManager.readJson(ConfigManager.profileFile(name));
        ConfigManager.applyModules(json, false, true);
        dirty = false;
        if (json == null) {
            ConfigManager.save();
        } else {
            JsonObject global = new JsonObject();
            global.addProperty("profile", activeProfile);
            ConfigManager.writeJson(root.resolve("astra.json"), global);
        }
    }

    public static boolean createProfile(String name) {
        if (!ConfigManager.isValidName(name = name.trim()) || Files.exists(ConfigManager.profileFile(name), new LinkOption[0])) {
            return false;
        }
        ConfigManager.save();
        activeProfile = name;
        ConfigManager.save();
        return true;
    }

    public static boolean createDefaultProfile(String name) {
        if (!ConfigManager.isValidName(name = name.trim()) || Files.exists(ConfigManager.profileFile(name), new LinkOption[0])) {
            return false;
        }
        ConfigManager.save();
        activeProfile = name;
        ConfigManager.applyModules(new JsonObject(), false, true);
        ConfigManager.save();
        return true;
    }

    public static boolean duplicateProfile(String source, String target) {
        if (!ConfigManager.isValidName(target = target.trim()) || Files.exists(ConfigManager.profileFile(target), new LinkOption[0])) {
            return false;
        }
        if (source.equals(activeProfile)) {
            ConfigManager.save();
        }
        try {
            Files.copy(ConfigManager.profileFile(source), ConfigManager.profileFile(target), new CopyOption[0]);
            return true;
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u0440\u043e\u0444\u0438\u043b\u044c", (Throwable)e);
            return false;
        }
    }

    public static boolean renameProfile(String oldName, String newName) {
        if (!ConfigManager.isValidName(newName = newName.trim()) || Files.exists(ConfigManager.profileFile(newName), new LinkOption[0])) {
            return false;
        }
        if (oldName.equals(activeProfile)) {
            ConfigManager.save();
        }
        try {
            Files.move(ConfigManager.profileFile(oldName), ConfigManager.profileFile(newName), new CopyOption[0]);
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c \u043f\u0440\u043e\u0444\u0438\u043b\u044c", (Throwable)e);
            return false;
        }
        if (oldName.equals(activeProfile)) {
            activeProfile = newName;
            ConfigManager.save();
        }
        return true;
    }

    public static boolean deleteProfile(String name) {
        List<String> profiles = ConfigManager.listProfiles();
        if (profiles.size() <= 1) {
            return false;
        }
        try {
            Files.deleteIfExists(ConfigManager.profileFile(name));
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043f\u0440\u043e\u0444\u0438\u043b\u044c", (Throwable)e);
            return false;
        }
        if (name.equals(activeProfile)) {
            profiles.remove(name);
            dirty = false;
            ConfigManager.loadProfile(profiles.get(0));
        }
        return true;
    }

    public static String uniqueName(String base) {
        String name = base;
        int i = 2;
        while (Files.exists(ConfigManager.profileFile((String)name), new LinkOption[0])) {
            name = base + " " + i++;
        }
        return name;
    }

    public static JsonObject readJson(Path path) {
        if (path == null || !Files.isRegularFile(path)) return null;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (Exception e) {
            AstraClient.LOGGER.error("Unable to read configuration {}", path, e);
            return null;
        }
    }

    public static void writeJson(Path path, JsonObject json) {
        if (path == null) {
            return;
        }
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Path tmp = path.resolveSibling(String.valueOf(path.getFileName()) + ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson((JsonElement)json, (Appendable)writer);
            }
            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException e) {
            AstraClient.LOGGER.error("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c {}", (Object)path, (Object)e);
        }
    }

    static {
        activeProfile = DEFAULT_PROFILE;
    }
}

