/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.Util
 */
package astra.gui.pages;

import astra.AstraClient;
import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.gui.pages.ModulesPage;
import astra.gui.screens.ClickGuiScreen;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.replay.ScreenshotModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.theme.Theme;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class ReplaysPage
extends ModulesPage {
    public ReplaysPage(ClickGuiScreen screen) {
        super(screen, Category.REPLAYS);
        this.header.add(new Actions());
        this.header.add(new Gallery(this));
    }

    private final class Actions
    extends Widget {
        private final Animation[] hv = new Animation[]{new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f)};

        private Actions() {
        }

        @Override
        protected float measure(float width) {
            return 20.0f;
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            UiDraw.button(ui.ctx, t, this.x, this.y, 160.0f, 20.0f, "\u0421\u0434\u0435\u043b\u0430\u0442\u044c \u0441\u043a\u0440\u0438\u043d\u0448\u043e\u0442", Icon.CAMERA, this.hv[0].update(ui.hover(this.x, this.y, 160.0f, 20.0f) ? 1.0f : 0.0f, ui.dt), true);
            UiDraw.button(ui.ctx, t, this.x + 168.0f, this.y, 140.0f, 20.0f, "\u041f\u0430\u043f\u043a\u0430 \u0441\u043a\u0440\u0438\u043d\u0448\u043e\u0442\u043e\u0432", Icon.FOLDER, this.hv[1].update(ui.hover(this.x + 168.0f, this.y, 140.0f, 20.0f) ? 1.0f : 0.0f, ui.dt), false);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (my < (double)this.y || my >= (double)(this.y + 20.0f)) {
                return false;
            }
            if (mx >= (double)this.x && mx < (double)(this.x + 160.0f)) {
                ScreenshotModule sm = ModuleManager.get(ScreenshotModule.class);
                if (Minecraft.method_1551().field_1687 == null) {
                    ReplaysPage.this.screen.toast("\u0421\u043a\u0440\u0438\u043d\u0448\u043e\u0442 \u043c\u043e\u0436\u043d\u043e \u0441\u0434\u0435\u043b\u0430\u0442\u044c \u0432 \u043c\u0438\u0440\u0435");
                } else if (sm != null) {
                    sm.requestShot();
                    ReplaysPage.this.screen.method_25419();
                }
                return true;
            }
            if (mx >= (double)(this.x + 168.0f) && mx < (double)(this.x + 308.0f)) {
                ScreenshotModule.openFolder();
                return true;
            }
            return false;
        }
    }

    private final class Gallery
    extends Widget {
        private static final int MAX = 12;
        private static final float GAP = 8.0f;
        private final Map<String, Identifier> thumbs = new HashMap<String, Identifier>();
        private final Map<String, Animation> hovers = new HashMap<String, Animation>();
        private List<File> files = new ArrayList<File>();
        private long scannedAt;
        private int counter;

        private Gallery(ReplaysPage replaysPage) {
        }

        private void scan() {
            if (System.currentTimeMillis() - this.scannedAt < 3000L) {
                return;
            }
            this.scannedAt = System.currentTimeMillis();
            File[] list = ScreenshotModule.screenshotsDir().listFiles((d, n) -> n.toLowerCase().endsWith(".png"));
            if (list == null) {
                this.files = new ArrayList<File>();
                return;
            }
            this.files = new ArrayList<File>(Arrays.asList(list));
            this.files.sort(Comparator.comparingLong(File::lastModified).reversed());
            if (this.files.size() > 12) {
                this.files = new ArrayList<File>(this.files.subList(0, 12));
            }
        }

        private int cols() {
            return Math.max(2, (int)((this.w + 8.0f) / 158.0f));
        }

        private float cellW() {
            int c = this.cols();
            return (this.w - 8.0f * (float)(c - 1)) / (float)c;
        }

        private float cellH() {
            return this.cellW() * 9.0f / 16.0f + 16.0f;
        }

        @Override
        protected float measure(float width) {
            this.w = width;
            this.scan();
            int rows = Math.max(1, (int)Math.ceil((double)this.files.size() / (double)this.cols()));
            return 26.0f + (float)rows * (this.cellH() + 8.0f);
        }

        private Identifier thumbnail(File f) {
            Identifier identifier;
            block13: {
                String key = f.getName() + ":" + f.lastModified();
                Identifier id = this.thumbs.get(key);
                if (id != null || this.thumbs.containsKey(key)) {
                    return id;
                }
                if (this.counter++ % 2 != 0) {
                    return null;
                }
                if (System.currentTimeMillis() - f.lastModified() < 3000L) {
                    return null;
                }
                InputStream in = Files.newInputStream(f.toPath(), new OpenOption[0]);
                try {
                    NativeImage full = NativeImage.method_4309((InputStream)in);
                    int tw = 192;
                    int th = 108;
                    NativeImage small = new NativeImage(tw, th, false);
                    for (int y = 0; y < th; ++y) {
                        for (int x = 0; x < tw; ++x) {
                            small.method_61941(x, y, full.method_61940(x * full.method_4307() / tw, y * full.method_4323() / th));
                        }
                    }
                    full.close();
                    Identifier tid = Identifier.method_60655((String)"astra", (String)("gallery/" + Integer.toHexString(key.hashCode())));
                    Minecraft.method_1551().method_1531().method_4616(tid, (AbstractTexture)new DynamicTexture(() -> "astra_thumb", small));
                    this.thumbs.put(key, tid);
                    identifier = tid;
                    if (in == null) break block13;
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
                        AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043c\u0438\u043d\u0438\u0430\u0442\u044e\u0440\u0443 {}", (Object)f, (Object)e);
                        this.thumbs.put(key, null);
                        return null;
                    }
                }
                in.close();
            }
            return identifier;
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            Fonts.draw(ui.ctx, "\u0413\u0430\u043b\u0435\u0440\u0435\u044f", this.x + 2.0f, this.y + 4.0f, 9.0f, t.text(), true);
            Fonts.drawRight(ui.ctx, this.files.isEmpty() ? "" : "\u041a\u043b\u0438\u043a \u2014 \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u0441\u043d\u0438\u043c\u043e\u043a", this.x + this.w - 2.0f, this.y + 6.0f, 6.2f, t.textMuted(), false);
            if (this.files.isEmpty()) {
                float cy = this.y + 26.0f;
                UiDraw.card(ui.ctx, t, this.x, cy, this.w, this.cellH(), 8.0f, 0.0f, false);
                Fonts.drawCentered(ui.ctx, "\u0421\u043a\u0440\u0438\u043d\u0448\u043e\u0442\u043e\u0432 \u043f\u043e\u043a\u0430 \u043d\u0435\u0442 \u2014 \u043d\u0430\u0436\u043c\u0438\u0442\u0435 F2 \u0432 \u0438\u0433\u0440\u0435", this.x + this.w / 2.0f, cy + this.cellH() / 2.0f - 4.0f, 7.5f, t.textDim(), false);
                return;
            }
            int c = this.cols();
            float cw = this.cellW();
            float ch = this.cellH();
            for (int i = 0; i < this.files.size(); ++i) {
                File f = this.files.get(i);
                float cx = this.x + (float)(i % c) * (cw + 8.0f);
                float cy = this.y + 26.0f + (float)(i / c) * (ch + 8.0f);
                if (cy > 600.0f || cy + ch < 0.0f) continue;
                float hv = this.hovers.computeIfAbsent(f.getName(), k -> new Animation(0.0f, 14.0f)).update(ui.hover(cx, cy, cw, ch) ? 1.0f : 0.0f, ui.dt);
                UiDraw.card(ui.ctx, t, cx, cy, cw, ch, 7.0f, hv, false);
                float iw = cw - 8.0f;
                float ih = iw * 9.0f / 16.0f;
                Identifier tex = this.thumbnail(f);
                if (tex != null) {
                    Render2D.texture(ui.ctx, tex, cx + 4.0f, cy + 4.0f, iw, ih, 0.0f, 0.0f, 1.0f, 1.0f, -1);
                } else {
                    Render2D.roundRect(ui.ctx, cx + 4.0f, cy + 4.0f, iw, ih, 4.0f, ColorUtil.withAlpha(t.bg(), 0.7f));
                }
                Render2D.outline(ui.ctx, cx + 4.0f, cy + 4.0f, iw, ih, 3.0f, 0.7f, ColorUtil.withAlpha(t.accent(), 0.3f + 0.5f * hv));
                Fonts.draw(ui.ctx, Fonts.trim(f.getName().replace(".png", ""), iw, 5.8f, false), cx + 5.0f, cy + ih + 7.0f, 5.8f, t.textDim(), false);
            }
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            int c = this.cols();
            float cw = this.cellW();
            float ch = this.cellH();
            for (int i = 0; i < this.files.size(); ++i) {
                float cx = this.x + (float)(i % c) * (cw + 8.0f);
                float cy = this.y + 26.0f + (float)(i / c) * (ch + 8.0f);
                if (!(mx >= (double)cx) || !(mx < (double)(cx + cw)) || !(my >= (double)cy) || !(my < (double)(cy + ch))) continue;
                Util.method_668().method_672(this.files.get(i));
                return true;
            }
            return false;
        }
    }
}

