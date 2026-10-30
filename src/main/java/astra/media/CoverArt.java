/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 */
package astra.media;

import astra.AstraClient;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Color;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class CoverArt {
    private static final int KEEP = 12;
    private static final Map<String, Cover> LOADED = new LinkedHashMap<String, Cover>(16, 0.75f, true);
    private static final Map<String, Long> FAILED = new LinkedHashMap<String, Long>();
    private static int counter;

    private CoverArt() {
    }

    public static Cover get(String path) {
        Cover cover;
        block12: {
            if (path == null || path.isEmpty()) {
                return null;
            }
            Cover c = LOADED.get(path);
            if (c != null) {
                return c;
            }
            Long failedAt = FAILED.get(path);
            if (failedAt != null && System.currentTimeMillis() - failedAt < 3000L) {
                return null;
            }
            Path file = Path.of(path, new String[0]);
            if (!Files.isRegularFile(file, new LinkOption[0])) {
                FAILED.put(path, System.currentTimeMillis());
                return null;
            }
            InputStream in = Files.newInputStream(file, new OpenOption[0]);
            try {
                NativeImage img = NativeImage.read((InputStream)in);
                int[] palette = CoverArt.palette(img);
                Identifier id = Identifier.fromNamespaceAndPath((String)"astra", (String)("media/cover_" + counter++));
                Minecraft.getInstance().getTextureManager().register(id, (AbstractTexture)new DynamicTexture(() -> "astra_cover", img));
                c = new Cover(id, palette[0], palette[1]);
                LOADED.put(path, c);
                FAILED.remove(path);
                CoverArt.trim();
                cover = c;
                if (in == null) break block12;
            }
            catch (Throwable throwable) {
                try {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception e) {
                    FAILED.put(path, System.currentTimeMillis());
                    AstraClient.LOGGER.debug("\u041e\u0431\u043b\u043e\u0436\u043a\u0430 \u043d\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u043b\u0430\u0441\u044c: {}", (Object)path, (Object)e);
                    return null;
                }
            }
            in.close();
        }
        return cover;
    }

    private static void trim() {
        Iterator<Map.Entry<String, Cover>> it = LOADED.entrySet().iterator();
        while (LOADED.size() > 12 && it.hasNext()) {
            Cover old = it.next().getValue();
            Minecraft.getInstance().getTextureManager().release(old.id());
            it.remove();
        }
    }

    private static int[] palette(NativeImage img) {
        int bins = 18;
        float[] weight = new float[bins];
        float[] r = new float[bins];
        float[] g = new float[bins];
        float[] b = new float[bins];
        int w = img.getWidth();
        int h = img.getHeight();
        int step = Math.max(1, Math.min(w, h) / 48);
        float[] hsv = new float[3];
        for (int y = 0; y < h; y += step) {
            for (int x = 0; x < w; x += step) {
                int bin;
                int argb = img.getPixel(x, y);
                int cr = argb >> 16 & 0xFF;
                int cg = argb >> 8 & 0xFF;
                int cb = argb & 0xFF;
                Color.RGBtoHSB(cr, cg, cb, hsv);
                float wt = hsv[1] * hsv[1] * hsv[2];
                if (wt < 0.04f) continue;
                int n = bin = Math.min(bins - 1, (int)(hsv[0] * (float)bins));
                weight[n] = weight[n] + wt;
                int n2 = bin;
                r[n2] = r[n2] + (float)cr * wt;
                int n3 = bin;
                g[n3] = g[n3] + (float)cg * wt;
                int n4 = bin;
                b[n4] = b[n4] + (float)cb * wt;
            }
        }
        int first = -1;
        int second = -1;
        for (int i = 0; i < bins; ++i) {
            if (weight[i] <= 0.0f) continue;
            if (first < 0 || weight[i] > weight[first]) {
                second = first;
                first = i;
                continue;
            }
            if (second >= 0 && !(weight[i] > weight[second])) continue;
            second = i;
        }
        if (second >= 0 && Math.min(Math.abs(first - second), bins - Math.abs(first - second)) < 2) {
            second = -1;
        }
        int c1 = first < 0 ? -45608 : CoverArt.vivid(r[first] / weight[first], g[first] / weight[first], b[first] / weight[first]);
        int c2 = second < 0 ? CoverArt.shift(c1) : CoverArt.vivid(r[second] / weight[second], g[second] / weight[second], b[second] / weight[second]);
        return new int[]{c1, c2};
    }

    private static int vivid(float r, float g, float b) {
        float[] hsv = Color.RGBtoHSB(Math.round(r), Math.round(g), Math.round(b), null);
        int rgb = Color.HSBtoRGB(hsv[0], Math.max(0.55f, Math.min(0.95f, hsv[1] * 1.25f)), Math.max(0.85f, hsv[2]));
        return 0xFF000000 | rgb & 0xFFFFFF;
    }

    private static int shift(int c) {
        float[] hsv = Color.RGBtoHSB(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, null);
        int rgb = Color.HSBtoRGB((hsv[0] + 0.12f) % 1.0f, hsv[1], hsv[2]);
        return 0xFF000000 | rgb & 0xFFFFFF;
    }

    public record Cover(Identifier id, int color1, int color2) {
    }
}

