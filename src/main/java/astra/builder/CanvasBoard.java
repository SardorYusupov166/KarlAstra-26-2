/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.Identifier
 */
package astra.builder;

import astra.builder.ServerIdentity;
import astra.config.ConfigManager;
import astra.online.AstraOnline;
import astra.waypoint.Waypoints;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

public final class CanvasBoard {
    public static final Gson GSON = new Gson();
    private static final Map<String, LinkedHashMap<String, Canvas>> boards = new HashMap<String, LinkedHashMap<String, Canvas>>();
    private static final Map<String, Identifier> textures = new HashMap<String, Identifier>();
    public static final Set<String> hidden = new HashSet<String>();
    private static String scope = "";
    private static String requested = "";
    private static String connection = "";
    private static boolean loaded;
    public static String status;

    public static String scope() {
        return scope;
    }

    public static void onlineReset() {
        requested = "";
    }

    private static boolean shared() {
        return AstraOnline.connected() && scope.startsWith("server:");
    }

    public static void toggleHidden(String id) {
        if (!hidden.add(id)) {
            hidden.remove(id);
        }
        JsonObject o = new JsonObject();
        for (String h : hidden) {
            o.addProperty(h, Boolean.valueOf(true));
        }
        ConfigManager.writeJson(ConfigManager.root().resolve("builder-canvas-hidden.json"), o);
    }

    public static void tick() {
        String conn;
        LinkedHashMap<String, Canvas> legacy;
        String legacyWorld;
        String world;
        String next;
        if (!loaded) {
            JsonObject data;
            loaded = true;
            JsonObject prefs = ConfigManager.readJson(ConfigManager.root().resolve("builder-canvas-hidden.json"));
            if (prefs != null) {
                hidden.addAll(prefs.keySet());
            }
            if ((data = ConfigManager.readJson(ConfigManager.root().resolve("builder-canvases.json"))) != null) {
                for (Map.Entry e : data.entrySet()) {
                    try {
                        LinkedHashMap<String, Canvas> map = new LinkedHashMap<String, Canvas>();
                        for (JsonElement el : ((JsonElement)e.getValue()).getAsJsonArray()) {
                            Canvas c = (Canvas)GSON.fromJson(el, Canvas.class);
                            if (!c.valid() || map.size() >= 32) continue;
                            map.put(c.id, c);
                        }
                        boards.put((String)e.getKey(), map);
                    }
                    catch (Exception map) {}
                }
            }
        }
        String string = next = (world = CanvasBoard.worldIdentity(legacyWorld = Waypoints.worldKey())) == null ? "" : world + "|" + Waypoints.dimension();
        if (world != null && legacyWorld != null && !world.equals(legacyWorld) && (legacy = boards.get("local|" + legacyWorld + "|" + Waypoints.dimension())) != null && !legacy.isEmpty()) {
            LinkedHashMap dest = boards.computeIfAbsent("local|" + next, k -> new LinkedHashMap());
            legacy.forEach(dest::putIfAbsent);
            boards.remove("local|" + legacyWorld + "|" + Waypoints.dimension());
            CanvasBoard.save();
        }
        String string2 = conn = AstraOnline.connected() && next.startsWith("server:") ? "shared:" + AstraOnline.canvasConnection() : "local";
        if (!next.equals(scope) || !conn.equals(connection)) {
            scope = next;
            connection = conn;
            requested = "";
            CanvasBoard.releaseTextures();
        }
        if (CanvasBoard.shared() && !scope.isEmpty() && !requested.equals(scope)) {
            requested = scope;
            JsonObject m = new JsonObject();
            m.addProperty("t", "canvas_get");
            m.addProperty("scope", scope);
            AstraOnline.sendCanvas(m);
        }
    }

    private static String worldIdentity(String legacy) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        if (mc.getSingleplayerServer() != null) {
            return legacy;
        }
        ClientPacketListener handler = mc.getConnection();
        if (handler == null) {
            return null;
        }
        return ServerIdentity.peer(handler.getConnection().getRemoteAddress());
    }

    private static String key() {
        return connection + "|" + scope;
    }

    public static List<Canvas> all() {
        return new ArrayList<Canvas>(boards.computeIfAbsent(CanvasBoard.key(), k -> new LinkedHashMap()).values());
    }

    private static void save() {
        JsonObject root = new JsonObject();
        boards.forEach((k, v) -> root.add(k, GSON.toJsonTree(v.values())));
        ConfigManager.writeJson(ConfigManager.root().resolve("builder-canvases.json"), root);
    }

    public static void put(Canvas c) {
        if (!c.valid() || scope.isEmpty()) {
            status = "\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0435 \u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u044b \u0445\u043e\u043b\u0441\u0442\u0430";
            return;
        }
        if (CanvasBoard.shared()) {
            JsonObject m = new JsonObject();
            m.addProperty("t", "canvas_put");
            m.addProperty("scope", scope);
            m.add("canvas", GSON.toJsonTree((Object)c));
            AstraOnline.sendCanvas(m);
            status = "\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435\u2026";
        } else {
            if (CanvasBoard.all().size() >= 32 && !boards.get(CanvasBoard.key()).containsKey(c.id)) {
                status = "\u041c\u0430\u043a\u0441\u0438\u043c\u0443\u043c 32 \u0445\u043e\u043b\u0441\u0442\u0430";
                return;
            }
            boards.computeIfAbsent(CanvasBoard.key(), k -> new LinkedHashMap()).put(c.id, c.copy());
            CanvasBoard.save();
            status = "\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u043e \u043b\u043e\u043a\u0430\u043b\u044c\u043d\u043e \u2014 \u0434\u043b\u044f \u043e\u0431\u0449\u0438\u0445 \u0445\u043e\u043b\u0441\u0442\u043e\u0432 \u0437\u0430\u0439\u0434\u0438\u0442\u0435 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0441 Astra Online";
        }
    }

    public static void delete(Canvas c) {
        if (CanvasBoard.shared()) {
            JsonObject m = new JsonObject();
            m.addProperty("t", "canvas_delete");
            m.addProperty("scope", scope);
            m.addProperty("id", c.id);
            m.addProperty("revision", (Number)c.revision);
            AstraOnline.sendCanvas(m);
        } else {
            boards.computeIfAbsent(CanvasBoard.key(), k -> new LinkedHashMap()).remove(c.id);
            CanvasBoard.save();
        }
        CanvasBoard.releaseTextures();
    }

    public static void handle(JsonObject m) {
        CanvasBoard.tick();
        try {
            if (!m.has("scope") || !m.get("scope").getAsString().equals(scope)) {
                return;
            }
            String t = m.get("t").getAsString();
            LinkedHashMap map = boards.computeIfAbsent(CanvasBoard.key(), k -> new LinkedHashMap());
            if (t.equals("canvas_snapshot")) {
                map.clear();
                for (JsonElement el : m.getAsJsonArray("canvases")) {
                    Canvas c = (Canvas)GSON.fromJson(el, Canvas.class);
                    if (!c.valid() || map.size() >= 32) continue;
                    map.put(c.id, c);
                }
                status = "\u041e\u0431\u0449\u0438\u0435 \u0445\u043e\u043b\u0441\u0442\u044b \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u044b";
            } else if (t.equals("canvas_put")) {
                Canvas c = (Canvas)GSON.fromJson(m.get("canvas"), Canvas.class);
                if (c.valid() && (map.containsKey(c.id) || map.size() < 32)) {
                    map.put(c.id, c);
                }
                status = "\u0425\u043e\u043b\u0441\u0442 \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d";
            } else if (t.equals("canvas_delete")) {
                map.remove(m.get("id").getAsString());
                status = "\u0425\u043e\u043b\u0441\u0442 \u0443\u0434\u0430\u043b\u0451\u043d";
            } else if (t.equals("canvas_error")) {
                status = m.get("msg").getAsString();
                return;
            }
            CanvasBoard.releaseTextures();
            CanvasBoard.save();
        }
        catch (Exception ex) {
            status = "\u041f\u0440\u043e\u043f\u0443\u0449\u0435\u043d\u044b \u043d\u0435\u0432\u0435\u0440\u043d\u044b\u0435 \u0434\u0430\u043d\u043d\u044b\u0435 \u0445\u043e\u043b\u0441\u0442\u0430";
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Canvas importImage(Path path) throws Exception {
        BufferedImage src;
        if (Files.size(path) > 0x800000L) {
            throw new IOException("\u0424\u0430\u0439\u043b \u0431\u043e\u043b\u044c\u0448\u0435 8 \u041c\u0411");
        }
        try (ImageInputStream in = ImageIO.createImageInputStream(path.toFile());){
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (!readers.hasNext()) {
                throw new IOException("\u041d\u0443\u0436\u043d\u0430 PNG/JPG/GIF/BMP \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0430");
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(in);
                if ((long)reader.getWidth(0) * (long)reader.getHeight(0) > 16000000L) {
                    throw new IOException("\u0411\u043e\u043b\u044c\u0448\u0435 16 \u043c\u0438\u043b\u043b\u0438\u043e\u043d\u043e\u0432 \u043f\u0438\u043a\u0441\u0435\u043b\u0435\u0439");
                }
                src = reader.read(0);
            }
            finally {
                reader.dispose();
            }
        }
        double scale = Math.min(1.0, 512.0 / (double)Math.max(src.getWidth(), src.getHeight()));
        int w = Math.max(1, (int)((double)src.getWidth() * scale));
        int h = Math.max(1, (int)((double)src.getHeight() * scale));
        BufferedImage img = new BufferedImage(w, h, 2);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)img, "png", out);
        Canvas c = new Canvas();
        c.name = path.getFileName().toString();
        if (c.name.length() > 80) {
            c.name = c.name.substring(0, 80);
        }
        c.png = Base64.getEncoder().encodeToString(out.toByteArray());
        c.height = c.width * (double)h / (double)w;
        if (c.height > 128.0) {
            c.width *= 128.0 / c.height;
            c.height = 128.0;
        }
        if (c.height < 0.1) {
            c.width *= 0.1 / c.height;
            c.height = 0.1;
        }
        return c;
    }

    public static Identifier texture(Canvas c) {
        if (textures.containsKey(c.id)) {
            return textures.get(c.id);
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(c.png);
            if (bytes.length < 24 || bytes[0] != -119 || bytes[1] != 80 || bytes[2] != 78 || bytes[3] != 71) {
                throw new IOException();
            }
            ByteBuffer b = ByteBuffer.wrap(bytes);
            int w = b.getInt(16);
            int h = b.getInt(20);
            if (w < 1 || h < 1 || w > 512 || h > 512) {
                throw new IOException();
            }
            NativeImage image = NativeImage.read((InputStream)new ByteArrayInputStream(bytes));
            Identifier id = Identifier.fromNamespaceAndPath((String)"astra", (String)("canvas/" + c.id));
            Minecraft.getInstance().getTextureManager().register(id, (AbstractTexture)new DynamicTexture(() -> "astra_canvas", image));
            textures.put(c.id, id);
            return id;
        }
        catch (Exception e) {
            textures.put(c.id, null);
            return null;
        }
    }

    private static void releaseTextures() {
        TextureManager tm = Minecraft.getInstance().getTextureManager();
        for (Identifier id : textures.values()) {
            if (id == null) continue;
            tm.release(id);
        }
        textures.clear();
    }

    static {
        status = "";
    }

    public static final class Canvas {
        public String id = UUID.randomUUID().toString();
        public String name = "\u0425\u043e\u043b\u0441\u0442";
        public String png = "";
        public double x;
        public double y;
        public double z;
        public double width = 4.0;
        public double height = 3.0;
        public double yaw;
        public double pitch;
        public double opacity = 1.0;
        public long revision;

        public Canvas copy() {
            return (Canvas)GSON.fromJson(GSON.toJson((Object)this), Canvas.class);
        }

        public boolean valid() {
            return this.id != null && this.id.matches("[0-9a-f-]{36}") && this.name != null && this.name.length() <= 80 && this.png != null && this.png.length() <= 1500000 && Double.isFinite(this.x) && Double.isFinite(this.y) && Double.isFinite(this.z) && Math.abs(this.x) <= 3.0E7 && Math.abs(this.z) <= 3.0E7 && Math.abs(this.y) <= 100000.0 && Double.isFinite(this.width) && this.width >= 0.1 && this.width <= 128.0 && Double.isFinite(this.height) && this.height >= 0.1 && this.height <= 128.0 && Double.isFinite(this.yaw) && Math.abs(this.yaw) <= 360.0 && Double.isFinite(this.pitch) && Math.abs(this.pitch) <= 180.0 && Double.isFinite(this.opacity) && this.opacity >= 0.0 && this.opacity <= 1.0;
        }
    }
}

