/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.packs.resources.Resource
 */
package astra.render;

import astra.AstraClient;
import astra.config.ConfigManager;
import astra.module.ClientSettings;
import astra.render.ColorUtil;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public final class ArtManager {
    public static final Identifier LOGO = ArtManager.gui("logo");
    public static final Identifier LOGO_GLOW = ArtManager.gui("logo_glow");
    public static final Identifier LOGO_CORE = ArtManager.gui("logo_core");
    public static final Identifier SIDEBAR_GIRL = ArtManager.gui("sidebar_girl");
    public static final Identifier BACKGROUND = ArtManager.gui("background");
    public static final Identifier BACKGROUND_MONO = ArtManager.gui("background_mono");
    public static final Identifier AVATAR_FALLBACK = ArtManager.gui("avatar_girl");
    public static final int AVATAR_SLOTS = 4;
    private static final Identifier CUSTOM_ID = Identifier.fromNamespaceAndPath((String)"astra", (String)"art/sidebar_custom");
    private static final Map<Identifier, int[]> SIZES = new HashMap<Identifier, int[]>();
    public static final String AUTO = "\u0410\u0432\u0442\u043e";
    public static final String BUILTIN = "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439";
    public static final String NONE = "\u0411\u0435\u0437 \u0430\u0440\u0442\u0430";
    private static final String[] IMAGE_EXT = new String[]{".png", ".jpg", ".jpeg", ".bmp", ".gif"};
    private static boolean loaded;
    private static String loadedChoice;
    private static long loadedStamp;
    private static long lastCheck;
    private static boolean custom;
    private static int texW;
    private static int texH;

    private ArtManager() {
    }

    private static Identifier gui(String name) {
        return Identifier.fromNamespaceAndPath((String)"astra", (String)("textures/gui/" + name + ".png"));
    }

    public static Path artDir() {
        return ConfigManager.root().resolve("art");
    }

    public static int[] size(Identifier id) {
        return SIZES.computeIfAbsent(id, key -> {
            Optional res = Minecraft.getInstance().getResourceManager().getResource(key);
            if (res.isEmpty()) {
                return null;
            }
            try (InputStream in = ((Resource)res.get()).open();){
                int[] nArray;
                block15: {
                    NativeImage img = NativeImage.read((InputStream)in);
                    try {
                        nArray = new int[]{img.getWidth(), img.getHeight()};
                        if (img == null) break block15;
                    }
                    catch (Throwable throwable) {
                        if (img != null) {
                            try {
                                img.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    img.close();
                }
                return nArray;
            }
            catch (Exception e) {
                AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c {}", key, (Object)e);
                return null;
            }
        });
    }

    public static boolean exists(Identifier id) {
        return ArtManager.size(id) != null;
    }

    private static float aspect(Identifier id, float fallback) {
        int[] s = ArtManager.size(id);
        return s == null || s[1] == 0 ? fallback : (float)s[0] / (float)s[1];
    }

    public static List<Path> customFiles() {
        ArrayList<Path> out = new ArrayList<Path>();
        try {
            Files.createDirectories(ArtManager.artDir(), new FileAttribute[0]);
            try (Stream<Path> s = Files.list(ArtManager.artDir());){
                block7: for (Path p : s.sorted().toList()) {
                    String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
                    for (String e : IMAGE_EXT) {
                        if (!n.endsWith(e) || !Files.isRegularFile(p, new LinkOption[0])) continue;
                        out.add(p);
                        continue block7;
                    }
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return out;
    }

    public static List<String> artChoices() {
        ArrayList<String> list = new ArrayList<String>(List.of(AUTO, BUILTIN, NONE));
        for (Path p : ArtManager.customFiles()) {
            list.add(p.getFileName().toString());
        }
        return list;
    }

    public static String sidebarImageForDecor() {
        if (NONE.equals(ArtManager.choice())) {
            return null;
        }
        Path f = ArtManager.chosenFile();
        if (f != null) {
            return f.getFileName().toString();
        }
        return BUILTIN.equals(ArtManager.choice()) || ArtManager.customFiles().isEmpty() ? "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0430\u043d\u0438\u043c\u0435-\u0434\u0435\u0432\u0443\u0448\u043a\u0430" : null;
    }

    private static String choice() {
        ClientSettings cs = ClientSettings.get();
        return cs != null ? (String)cs.sidebarArt.get() : AUTO;
    }

    private static Path chosenFile() {
        String c = ArtManager.choice();
        if (BUILTIN.equals(c) || NONE.equals(c)) {
            return null;
        }
        List<Path> files = ArtManager.customFiles();
        if (!AUTO.equals(c)) {
            for (Path p : files) {
                if (!p.getFileName().toString().equals(c)) continue;
                return p;
            }
        }
        for (Path p : files) {
            if (!p.getFileName().toString().toLowerCase(Locale.ROOT).startsWith("sidebar.")) continue;
            return p;
        }
        return files.isEmpty() ? null : files.get(0);
    }

    private static long stamp(Path file) {
        try {
            return file == null ? 0L : Files.getLastModifiedTime(file, new LinkOption[0]).toMillis() ^ (long)file.hashCode();
        }
        catch (Exception e) {
            return 0L;
        }
    }

    private static void checkChanged() {
        long now = System.currentTimeMillis();
        if (now - lastCheck < 1000L) {
            return;
        }
        lastCheck = now;
        Path f = ArtManager.chosenFile();
        if (!ArtManager.choice().equals(loadedChoice) || ArtManager.stamp(f) != loadedStamp) {
            ArtManager.reload();
        }
    }

    public static NativeImage readImage(Path file) throws Exception {
        if (Files.size(file) > 32L * 1024 * 1024) {
            throw new IllegalArgumentException("Изображение слишком большое: " + file.getFileName());
        }
        BufferedImage src;
        try (ImageInputStream input = ImageIO.createImageInputStream(file.toFile())) {
            if (input == null) throw new IllegalArgumentException("Не удалось открыть изображение: " + file.getFileName());
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("Формат не поддерживается: " + file.getFileName());
            ImageReader reader = readers.next();
            try {
                reader.setInput(input);
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 16_000_000L) {
                    throw new IllegalArgumentException("Слишком высокое разрешение: " + file.getFileName());
                }
                src = reader.read(0);
            } finally {
                reader.dispose();
            }
        }
        if (src == null) {
            throw new IllegalArgumentException("\u0424\u043e\u0440\u043c\u0430\u0442 \u043d\u0435 \u043f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442\u0441\u044f: " + String.valueOf(file.getFileName()));
        }
        int w = src.getWidth();
        int h = src.getHeight();
        if (Math.max(w, h) > 1024) {
            float scale = 1024.0f / Math.max(w, h);
            w = Math.max(1, Math.round(w * scale));
            h = Math.max(1, Math.round(h * scale));
        }
        BufferedImage img = new BufferedImage(w, h, 2);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)img, "png", png);
        return NativeImage.read((InputStream)new ByteArrayInputStream(png.toByteArray()));
    }

    private static void loadCustom() {
        loaded = true;
        lastCheck = System.currentTimeMillis();
        Path file = ArtManager.chosenFile();
        loadedChoice = ArtManager.choice();
        loadedStamp = ArtManager.stamp(file);
        if (file == null) {
            return;
        }
        try {
            NativeImage image = ArtManager.readImage(file);
            texW = image.getWidth();
            texH = image.getHeight();
            Minecraft.getInstance().getTextureManager().register(CUSTOM_ID, (AbstractTexture)new DynamicTexture(() -> "astra_sidebar_art", image));
            custom = true;
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0441\u0432\u043e\u0439 \u0430\u0440\u0442 \u0431\u043e\u043a\u043e\u0432\u043e\u0439 \u043f\u0430\u043d\u0435\u043b\u0438", (Throwable)e);
        }
    }

    public static void reload() {
        if (custom) {
            Minecraft.getInstance().getTextureManager().release(CUSTOM_ID);
        }
        loaded = false;
        custom = false;
        SIZES.clear();
    }

    public static Identifier sidebarTexture() {
        if (!loaded) {
            ArtManager.loadCustom();
        }
        if (NONE.equals(ArtManager.choice())) {
            return null;
        }
        return custom ? CUSTOM_ID : (ArtManager.exists(SIDEBAR_GIRL) ? SIDEBAR_GIRL : null);
    }

    public static float sidebarAspect() {
        if (!loaded) {
            ArtManager.loadCustom();
        }
        return custom ? (texH > 0 ? (float)texW / (float)texH : 1.0f) : ArtManager.aspect(SIDEBAR_GIRL, 1.0f);
    }

    public static void drawSidebarArt(GuiGraphicsExtractor ctx, Theme t, float x, float bottom, float w, float maxH, float alpha) {
        float aspect;
        Identifier id;
        if (loaded) {
            ArtManager.checkChanged();
        }
        if (!loaded) {
            ArtManager.loadCustom();
        }
        if (NONE.equals(ArtManager.choice())) {
            return;
        }
        if (custom) {
            id = CUSTOM_ID;
            aspect = texH > 0 ? (float)texW / (float)texH : 1.0f;
        } else {
            if (!ArtManager.exists(SIDEBAR_GIRL)) {
                return;
            }
            id = SIDEBAR_GIRL;
            aspect = ArtManager.aspect(SIDEBAR_GIRL, 1.0f);
        }
        float h = w / aspect;
        float v0 = 0.0f;
        if (h > maxH) {
            v0 = 1.0f - maxH / h;
            h = maxH;
        }
        float y = bottom - h;
        Render2D.texture(ctx, id, x, y, w, h, 0.0f, v0, 1.0f, 1.0f, ColorUtil.withAlpha(-1, alpha));
        if (custom || v0 > 0.0f) {
            float fade = Math.min(h, Math.max(80.0f, h * 0.55f));
            Render2D.rect(ctx, x, y, w, fade, Paint.vertical(y, y + fade, ColorUtil.withAlpha(t.bg(), 0.95f), ColorUtil.withAlpha(t.bg(), 0.0f)));
        }
    }

    public static void drawBackground(GuiGraphicsExtractor ctx, float x, float y, float w, float h, float r, float alpha) {
        if (alpha <= 0.0f || !ArtManager.exists(BACKGROUND)) {
            return;
        }
        ClientSettings cs = ClientSettings.get();
        if ((cs == null || ((Boolean)cs.artBackgroundThemed.get()).booleanValue()) && ArtManager.exists(BACKGROUND_MONO)) {
            Theme t = ThemeManager.get();
            int tint = ColorUtil.brighten(ColorUtil.lerp(t.accent(), t.accent2(), 0.35f), 0.25f);
            Render2D.textureRoundRect(ctx, BACKGROUND_MONO, ArtManager.aspect(BACKGROUND_MONO, 1.5f), x, y, w, h, r, ColorUtil.withAlpha(tint, alpha));
            return;
        }
        Render2D.textureRoundRect(ctx, BACKGROUND, ArtManager.aspect(BACKGROUND, 1.5f), x, y, w, h, r, ColorUtil.withAlpha(-1, alpha));
    }

    public static void drawLogo(GuiGraphicsExtractor ctx, float x, float y, float width, float alpha, int from, int to) {
        if (!ArtManager.exists(LOGO_GLOW) || !ArtManager.exists(LOGO_CORE)) {
            ArtManager.drawLogo(ctx, x, y, width, alpha);
            return;
        }
        float h = width / ArtManager.aspect(LOGO_GLOW, 2.5714285f);
        Render2D.textureGradient(ctx, LOGO_GLOW, x, y, width, h, ColorUtil.withAlpha(ColorUtil.brighten(from, 0.15f), alpha), ColorUtil.withAlpha(ColorUtil.brighten(to, 0.15f), alpha));
        Render2D.texture(ctx, LOGO_CORE, x, y, width, h, 0.0f, 0.0f, 1.0f, 1.0f, ColorUtil.withAlpha(-1, alpha));
    }

    public static void drawLogo(GuiGraphicsExtractor ctx, float x, float y, float width, float alpha) {
        if (!ArtManager.exists(LOGO)) {
            return;
        }
        Render2D.texture(ctx, LOGO, x, y, width, width / ArtManager.aspect(LOGO, 2.5714285f), 0.0f, 0.0f, 1.0f, 1.0f, ColorUtil.withAlpha(-1, alpha));
    }

    public static List<Identifier> avatars() {
        ArrayList<Identifier> list = new ArrayList<Identifier>();
        for (int i = 1; i <= 4; ++i) {
            Identifier id = ArtManager.gui("avatar_" + i);
            if (!ArtManager.exists(id)) continue;
            list.add(id);
        }
        if (list.isEmpty() && ArtManager.exists(AVATAR_FALLBACK)) {
            list.add(AVATAR_FALLBACK);
        }
        return list;
    }

    public static void drawAvatar(GuiGraphicsExtractor ctx, int index, float cx, float cy, float r) {
        List<Identifier> list = ArtManager.avatars();
        if (list.isEmpty()) {
            Render2D.circle(ctx, cx, cy, r, -13885370);
            return;
        }
        Identifier id = list.get(Math.floorMod(index, list.size()));
        Render2D.texturedCircle(ctx, Render2D.textureView(id), cx, cy, r, 0.5f, 0.5f, 0.5f, 0.0f, -1);
    }

    static {
        loadedChoice = "";
    }
}

