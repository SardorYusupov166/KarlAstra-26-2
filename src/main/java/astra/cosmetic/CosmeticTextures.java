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
package astra.cosmetic;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

public final class CosmeticTextures {
    private static final Map<String, Identifier> CACHE = new HashMap<String, Identifier>();
    public static final Identifier WING_ART = Identifier.method_60655((String)"astra", (String)"textures/cosmetic/wing_astra.png");

    private CosmeticTextures() {
    }

    private static Identifier register(String key, NativeImage image) {
        String safe = key.matches("[a-z0-9_]+") ? key : "c" + Integer.toHexString(key.hashCode());
        Identifier id = Identifier.method_60655((String)"astra", (String)("cosmetic/" + safe));
        Minecraft.method_1551().method_1531().method_4616(id, (AbstractTexture)new DynamicTexture(() -> "astra_" + key, image));
        return id;
    }

    private static int argb(int a, int r, int g, int b) {
        return Math.max(0, Math.min(255, a)) << 24 | Math.max(0, Math.min(255, r)) << 16 | Math.max(0, Math.min(255, g)) << 8 | Math.max(0, Math.min(255, b));
    }

    private static int mix(int c1, int c2, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        int a = (int)((float)(c1 >>> 24 & 0xFF) + (float)((c2 >>> 24 & 0xFF) - (c1 >>> 24 & 0xFF)) * t);
        int r = (int)((float)(c1 >> 16 & 0xFF) + (float)((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * t);
        int g = (int)((float)(c1 >> 8 & 0xFF) + (float)((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * t);
        int b = (int)((float)(c1 & 0xFF) + (float)((c2 & 0xFF) - (c1 & 0xFF)) * t);
        return CosmeticTextures.argb(a, r, g, b);
    }

    public static Identifier cape(String design) {
        return CACHE.computeIfAbsent("cape_" + design, k -> CosmeticTextures.register(k, CosmeticTextures.makeCape(design)));
    }

    private static NativeImage makeCape(String design) {
        int bottom;
        int top;
        NativeImage img = new NativeImage(64, 32, true);
        int accent = switch (design) {
            case "\u0421\u0430\u043a\u0443\u0440\u0430" -> {
                top = -19494;
                bottom = -5230470;
                yield -1;
            }
            case "\u041d\u043e\u0447\u044c" -> {
                top = -14996885;
                bottom = -16315874;
                yield -3152;
            }
            case "\u041d\u0435\u043e\u043d" -> {
                top = -14494738;
                bottom = -8635667;
                yield -1;
            }
            default -> {
                top = -4160260;
                bottom = -14020772;
                yield -45608;
            }
        };
        Random random = new Random(design.hashCode());
        for (int y = 0; y < 32; ++y) {
            for (int x = 0; x < 64; ++x) {
                float t = (float)(y % 17) / 16.0f;
                int c = CosmeticTextures.mix(top, bottom, t);
                if (random.nextFloat() < 0.04f) {
                    c = CosmeticTextures.mix(c, accent, 0.6f);
                }
                img.method_61941(x, y, c);
            }
        }
        if (design.equals("\u0421\u0430\u043a\u0443\u0440\u0430")) {
            int[][] petals;
            for (int[] p : petals = new int[][]{{5, 6}, {3, 8}, {7, 8}, {4, 10}, {6, 10}}) {
                img.method_61941(p[0], p[1], -1);
            }
            img.method_61941(5, 8, -8090);
        } else if (design.equals("\u041d\u043e\u0447\u044c")) {
            for (int i = 0; i < 10; ++i) {
                img.method_61941(1 + random.nextInt(10), 1 + random.nextInt(16), -2090);
            }
        } else {
            int cx = 5;
            int cy = 7;
            for (int d = -3; d <= 3; ++d) {
                img.method_61941(cx + d, cy, accent);
                img.method_61941(cx, cy + d, accent);
            }
            img.method_61941(cx - 1, cy - 1, accent);
            img.method_61941(cx + 1, cy + 1, accent);
            img.method_61941(cx + 1, cy - 1, accent);
            img.method_61941(cx - 1, cy + 1, accent);
            img.method_61941(cx, cy, -1);
        }
        return img;
    }

    public static Identifier wing(String design) {
        return CACHE.computeIfAbsent("wing_" + design, k -> CosmeticTextures.register(k, CosmeticTextures.makeWing(design)));
    }

    private static NativeImage makeWing(String design) {
        NativeImage img = new NativeImage(64, 64, true);
        boolean crystal = design.equals("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u044c\u043d\u044b\u0435");
        boolean dark = design.equals("\u0422\u0451\u043c\u043d\u044b\u0435");
        for (int y = 0; y < 64; ++y) {
            for (int x = 0; x < 64; ++x) {
                int base;
                float u = (float)x / 63.0f;
                float v = (float)y / 63.0f;
                float topEdge = 0.4f - 0.34f * (float)Math.sin((double)u * Math.PI / 2.0);
                float thickness = 0.52f * (1.0f - (float)Math.pow(u, 1.6)) + 0.1f;
                float feathers = 0.09f * (float)Math.abs(Math.sin((double)u * Math.PI * 6.5)) * (1.0f - u * 0.5f);
                float bottomEdge = Math.min(0.98f, topEdge + thickness - feathers);
                if (v < topEdge || v > bottomEdge || u < 0.02f) {
                    img.method_61941(x, y, 0);
                    continue;
                }
                float inside = (v - topEdge) / Math.max(0.01f, bottomEdge - topEdge);
                int n = dark ? CosmeticTextures.mix(-13956289, -16186609, inside) : (base = crystal ? CosmeticTextures.mix(-2034946, -8530948, inside) : CosmeticTextures.mix(-1, -25899, inside * 0.9f));
                if ((int)(u * 22.0f + inside * 4.0f) % 3 == 0) {
                    base = CosmeticTextures.mix(base, dark ? -5745153 : -1452545, 0.35f);
                }
                float edgeGlow = Math.max(0.0f, 1.0f - Math.min(v - topEdge, bottomEdge - v) * 18.0f);
                base = CosmeticTextures.mix(base, dark ? -45608 : -1, edgeGlow * 0.5f);
                int alpha = crystal ? 200 : 235;
                img.method_61941(x, y, alpha << 24 | base & 0xFFFFFF);
            }
        }
        return img;
    }

    public static Identifier white() {
        return CACHE.computeIfAbsent("white", k -> {
            NativeImage img = new NativeImage(4, 4, true);
            img.method_4326(0, 0, 4, 4, -1);
            return CosmeticTextures.register(k, img);
        });
    }

    public static Identifier auraCircle() {
        return CACHE.computeIfAbsent("aura", k -> {
            NativeImage img = new NativeImage(64, 64, true);
            for (int y = 0; y < 64; ++y) {
                for (int x = 0; x < 64; ++x) {
                    float dx = ((float)x - 31.5f) / 31.5f;
                    float dy = ((float)y - 31.5f) / 31.5f;
                    float r = (float)Math.sqrt(dx * dx + dy * dy);
                    float a = 0.0f;
                    a += CosmeticTextures.ring(r, 0.95f, 0.035f);
                    a += CosmeticTextures.ring(r, 0.8f, 0.02f) * 0.8f;
                    a += CosmeticTextures.ring(r, 0.45f, 0.02f) * 0.6f;
                    double ang = Math.atan2(dy, dx);
                    if (r > 0.8f && r < 0.95f) {
                        a += (float)Math.max(0.0, Math.cos(ang * 12.0)) * 0.5f * CosmeticTextures.ring(r, 0.875f, 0.06f);
                    }
                    for (int i = 0; i < 5; ++i) {
                        float y2;
                        float x2;
                        float y1;
                        double a1 = Math.toRadians(-90 + i * 144);
                        double a2 = Math.toRadians(-90 + (i + 1) * 144);
                        float x1 = (float)Math.cos(a1) * 0.8f;
                        a += CosmeticTextures.lineDist(dx, dy, x1, y1 = (float)Math.sin(a1) * 0.8f, x2 = (float)Math.cos(a2) * 0.8f, y2 = (float)Math.sin(a2) * 0.8f) < 0.02f ? 0.7f : 0.0f;
                    }
                    int alpha = Math.min(255, Math.round((a += Math.max(0.0f, 0.25f - r) * 0.6f) * 255.0f));
                    img.method_61941(x, y, alpha << 24 | 0xFFFFFF);
                }
            }
            return CosmeticTextures.register(k, img);
        });
    }

    private static float ring(float r, float center, float width) {
        float d = Math.abs(r - center);
        return d > width ? 0.0f : 1.0f - d / width;
    }

    private static float lineDist(float px, float py, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float t = Math.max(0.0f, Math.min(1.0f, ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy)));
        float cx = x1 + t * dx - px;
        float cy = y1 + t * dy - py;
        return (float)Math.sqrt(cx * cx + cy * cy);
    }
}

