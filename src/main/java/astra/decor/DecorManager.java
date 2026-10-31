/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 *  org.joml.Matrix3x2fStack
 */
package astra.decor;

import astra.AstraClient;
import astra.animation.AnimationUtils;
import astra.config.ConfigManager;
import astra.decor.DecorLayer;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.platform.NativeImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public final class DecorManager {
    public static final String BUILTIN_PREFIX = "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: ";
    private static final Map<String, Identifier> BUILTIN = new LinkedHashMap<String, Identifier>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Map<String, Tex> TEXTURES = new HashMap<String, Tex>();
    private static final Map<String, Long> STAMPS = new HashMap<String, Long>();
    private static final Map<String, Long> FAILED = new HashMap<String, Long>();
    private static List<DecorLayer> layers = new ArrayList<DecorLayer>();
    public static boolean editing;
    private static boolean loaded;
    private static int counter;
    private static long lastStampCheck;

    private DecorManager() {
    }

    private static Path file() {
        return ConfigManager.root().resolve("decor.json");
    }

    public static List<DecorLayer> layers() {
        if (!loaded) {
            DecorManager.load();
        }
        return layers;
    }

    public static void load() {
        loaded = true;
        try {
            List list;
            if (Files.isRegularFile(DecorManager.file(), new LinkOption[0]) && (list = (List)GSON.fromJson(Files.readString(DecorManager.file(), StandardCharsets.UTF_8), new TypeToken<List<DecorLayer>>(){}.getType())) != null) {
                layers = new ArrayList<DecorLayer>(list);
            }
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c decor.json", (Throwable)e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(DecorManager.file().getParent(), new FileAttribute[0]);
            Files.writeString(DecorManager.file(), (CharSequence)GSON.toJson(layers), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c decor.json", (Throwable)e);
        }
    }

    public static String toJson() {
        return GSON.toJson(DecorManager.layers());
    }

    public static void fromJson(String json) {
        List list = (List)GSON.fromJson(json, new TypeToken<List<DecorLayer>>(){}.getType());
        layers = list != null ? new ArrayList<DecorLayer>(list) : new ArrayList();
    }

    public static List<String> images() {
        ArrayList<String> list = new ArrayList<String>();
        for (Path p : ArtManager.customFiles()) {
            list.add(p.getFileName().toString());
        }
        list.addAll(BUILTIN.keySet());
        return list;
    }

    public static Tex texture(String image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        Identifier builtin = BUILTIN.get(image);
        if (builtin != null) {
            int[] s = ArtManager.size(builtin);
            return s == null ? null : new Tex(builtin, s[0], s[1]);
        }
        DecorManager.checkStamps();
        Tex t = TEXTURES.get(image);
        if (t != null) {
            return t;
        }
        Long failed = FAILED.get(image);
        if (failed != null && System.currentTimeMillis() - failed < 3000L) {
            return null;
        }
        Path f = ArtManager.artDir().resolve(image).normalize();
        try {
            if (!f.getParent().equals(ArtManager.artDir().normalize())) {
                throw new IllegalArgumentException("Изображение вне папки art");
            }
            if (!Files.isRegularFile(f, new LinkOption[0])) {
                throw new IllegalStateException("\u043d\u0435\u0442 \u0444\u0430\u0439\u043b\u0430");
            }
            NativeImage img = ArtManager.readImage(f);
            Identifier id = Identifier.fromNamespaceAndPath((String)"astra", (String)("decor/" + counter++));
            Minecraft.getInstance().getTextureManager().register(id, (AbstractTexture)new DynamicTexture(() -> "astra_decor", img));
            t = new Tex(id, img.getWidth(), img.getHeight());
            TEXTURES.put(image, t);
            STAMPS.put(image, Files.getLastModifiedTime(f, new LinkOption[0]).toMillis());
            FAILED.remove(image);
            return t;
        }
        catch (Exception e) {
            FAILED.put(image, System.currentTimeMillis());
            return null;
        }
    }

    private static void checkStamps() {
        long now = System.currentTimeMillis();
        if (now - lastStampCheck < 2000L) {
            return;
        }
        lastStampCheck = now;
        for (String name : new ArrayList<String>(TEXTURES.keySet())) {
            try {
                Path f = ArtManager.artDir().resolve(name);
                long m = Files.isRegularFile(f, new LinkOption[0]) ? Files.getLastModifiedTime(f, new LinkOption[0]).toMillis() : -1L;
                if (m == STAMPS.getOrDefault(name, 0L)) continue;
                Minecraft.getInstance().getTextureManager().release(TEXTURES.remove(name).id());
                STAMPS.remove(name);
            }
            catch (Exception exception) {}
        }
    }

    public static void refreshImages() {
        lastStampCheck = 0L;
        DecorManager.checkStamps();
        FAILED.clear();
    }

    public static void renderMenu(GuiGraphicsExtractor ctx, String depth, float designW) {
        for (DecorLayer l : DecorManager.layers()) {
            if (!l.visible || !l.isMenu() || !depth.equals(l.depth)) continue;
            DecorManager.draw(ctx, l, l.x * designW, l.y, l.w, l.h, 1.0f);
        }
    }

    public static void renderScreen(GuiGraphicsExtractor ctx, String depth, float sw, float sh) {
        DecorManager.renderScreen(ctx, depth, sw, sh, false);
    }

    public static void renderScreen(GuiGraphicsExtractor ctx, String depth, float sw, float sh, boolean fromEditor) {
        if (editing && !fromEditor) {
            return;
        }
        for (DecorLayer l : DecorManager.layers()) {
            if (!l.visible || l.isMenu() || !depth.equals(l.depth)) continue;
            DecorManager.draw(ctx, l, l.x * sw, l.y * sh, l.w * sh, l.h * sh, 1.0f);
        }
    }

    public static void draw(GuiGraphicsExtractor ctx, DecorLayer l, float cx, float cy, float w, float h, float alphaMul) {
        boolean neon;
        Tex tex = DecorManager.texture(l.image);
        float t = AnimationUtils.time() * l.animSpeed / 100.0f + l.phase;
        float rot = l.rotation;
        float scale = 1.0f;
        float alpha = l.opacity / 100.0f * alphaMul;
        float dy = 0.0f;
        float glowMul = 1.0f;
        switch (l.animation) {
            case "\u041f\u0430\u0440\u0435\u043d\u0438\u0435": {
                dy = (float)Math.sin(t * 1.6f) * Math.max(2.0f, h * 0.035f);
                break;
            }
            case "\u041f\u0443\u043b\u044c\u0441": {
                scale = 1.0f + 0.04f * (float)Math.sin(t * 2.6f);
                break;
            }
            case "\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435": {
                rot += (float)Math.sin(t * 1.3f) * 5.0f;
                break;
            }
            case "\u0412\u0440\u0430\u0449\u0435\u043d\u0438\u0435": {
                rot += t * 40.0f % 360.0f;
                break;
            }
            case "\u041c\u0435\u0440\u0446\u0430\u043d\u0438\u0435": {
                alpha *= 0.6f + 0.4f * (0.5f + 0.5f * (float)Math.sin(t * 3.2f));
                break;
            }
            case "\u041d\u0435\u043e\u043d\u043e\u0432\u043e\u0435 \u0434\u044b\u0445\u0430\u043d\u0438\u0435": {
                glowMul = 0.45f + 0.75f * (0.5f + 0.5f * (float)Math.sin(t * 2.2f));
                break;
            }
        }
        if (alpha <= 0.01f) {
            return;
        }
        Theme theme = ThemeManager.get();
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(cx, cy + dy);
        if (rot != 0.0f) {
            m.rotate((float)Math.toRadians(rot));
        }
        m.scale(scale * (float)(l.flipX ? -1 : 1), scale * (float)(l.flipY ? -1 : 1));
        float x0 = -w / 2.0f;
        float y0 = -h / 2.0f;
        float r = Math.min(l.radius, Math.min(w, h) / 2.0f);
        float prev = Render2D.alpha;
        Render2D.alpha = prev * alpha;
        boolean bl = neon = l.glow || "\u041d\u0435\u043e\u043d\u043e\u0432\u043e\u0435 \u0434\u044b\u0445\u0430\u043d\u0438\u0435".equals(l.animation);
        if (neon) {
            Render2D.glow(ctx, x0, y0, w, h, Math.max(r, 4.0f), Math.max(6.0f, Math.min(w, h) * 0.12f), ColorUtil.withAlpha(theme.accent(), 0.45f * glowMul));
        }
        if (tex == null) {
            Render2D.roundRect(ctx, x0, y0, w, h, r, ColorUtil.withAlpha(theme.bg2(), 0.5f));
            Render2D.outline(ctx, x0, y0, w, h, r, 1.0f, ColorUtil.withAlpha(theme.accent(), 0.7f));
        } else {
            float aspect;
            int color = ColorUtil.lerp(-1, ColorUtil.brighten(theme.accent(), 0.2f), l.tint / 100.0f);
            float f = aspect = "\u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u044c (\u043e\u0431\u0440\u0435\u0437\u0430\u0442\u044c)".equals(l.fit) ? tex.aspect() : w / Math.max(0.001f, h);
            if (r > 0.5f || "\u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u044c (\u043e\u0431\u0440\u0435\u0437\u0430\u0442\u044c)".equals(l.fit)) {
                Render2D.textureRoundRect(ctx, tex.id(), aspect, x0, y0, w, h, r, color);
            } else {
                Render2D.texture(ctx, tex.id(), x0, y0, w, h, 0.0f, 0.0f, 1.0f, 1.0f, color);
            }
            if (l.fadeTop > 0.0f) {
                float fh = h * Math.min(100.0f, l.fadeTop) / 100.0f;
                Render2D.rect(ctx, x0, y0, w, fh, Paint.vertical(y0, y0 + fh, ColorUtil.withAlpha(theme.bg(), 0.95f), ColorUtil.withAlpha(theme.bg(), 0.0f)));
            }
            if (neon) {
                Render2D.outline(ctx, x0, y0, w, h, r, 1.0f, ColorUtil.withAlpha(theme.accent2(), 0.55f * glowMul));
            }
        }
        Render2D.alpha = prev;
        m.popMatrix();
    }

    static {
        BUILTIN.put("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0430\u043d\u0438\u043c\u0435-\u0434\u0435\u0432\u0443\u0448\u043a\u0430", Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/gui/sidebar_girl.png"));
        BUILTIN.put("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0444\u043e\u043d \u0441 \u0441\u0430\u043a\u0443\u0440\u043e\u0439", Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/gui/background.png"));
        BUILTIN.put("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u043b\u043e\u0433\u043e\u0442\u0438\u043f", Identifier.fromNamespaceAndPath((String)"astra", (String)"textures/gui/logo.png"));
        for (int i = 1; i <= 4; ++i) {
            BUILTIN.put("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0430\u0432\u0430\u0442\u0430\u0440 " + i, Identifier.fromNamespaceAndPath((String)"astra", (String)("textures/gui/avatar_" + i + ".png")));
        }
    }

    public record Tex(Identifier id, int w, int h) {
        public float aspect() {
            return this.h == 0 ? 1.0f : (float)this.w / (float)this.h;
        }
    }
}

