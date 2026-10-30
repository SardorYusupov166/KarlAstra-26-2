/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.fabricmc.loader.api.entrypoint.EntrypointContainer
 *  net.fabricmc.loader.api.metadata.ContactInformation
 *  net.fabricmc.loader.api.metadata.ModEnvironment
 *  net.fabricmc.loader.api.metadata.ModMetadata
 *  net.fabricmc.loader.api.metadata.Person
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.client.input.MouseButtonInfo
 *  net.minecraft.client.renderer.texture.AbstractTexture
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  net.minecraft.util.Util
 */
package astra.gui.screens;

import astra.AstraClient;
import astra.animation.Animation;
import astra.animation.AnimationUtils;
import astra.gui.components.UiDraw;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class AstraModsScreen
extends Screen {
    private static final Map<String, Identifier> ICONS = new HashMap<String, Identifier>();
    private static final Map<String, Boolean> NO_ICON = new HashMap<String, Boolean>();
    private static Map<String, Function<Screen, Screen>> configScreens;
    private final Screen parent;
    private final List<ModContainer> all = new ArrayList<ModContainer>();
    private final List<ModContainer> shown = new ArrayList<ModContainer>();
    private final Map<String, Animation> rowHover = new HashMap<String, Animation>();
    private final Animation[] btnHover = new Animation[]{new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f)};
    private final long openedAt = System.currentTimeMillis();
    private ModContainer selected;
    private String query = "";
    private float scroll;
    private float scrollTarget;
    private boolean libraries;
    private long lastFrame;
    private float dt = 0.016f;
    private final List<float[]> hits = new ArrayList<float[]>();
    private final List<String[]> links = new ArrayList<String[]>();
    private float lastChipEnd;

    public AstraModsScreen(Screen parent) {
        super((Component)Component.literal((String)"\u041c\u043e\u0434\u044b"));
        this.parent = parent;
        for (ModContainer c2 : FabricLoader.getInstance().getAllMods()) {
            if (c2.getContainingMod().isPresent()) continue;
            this.all.add(c2);
        }
        this.all.sort(Comparator.comparing(c -> c.getMetadata().getName().toLowerCase(Locale.ROOT)));
        this.filter();
        if (!this.shown.isEmpty()) {
            this.selected = this.shown.stream().filter(c -> c.getMetadata().getId().equals("astra")).findFirst().orElse(this.shown.get(0));
        }
    }

    private static boolean isLibrary(ModContainer c) {
        String id = c.getMetadata().getId();
        return id.equals("java") || id.equals("fabricloader") || id.startsWith("fabric-") && !id.equals("fabric-api") || id.equals("mixinextras") || c.getMetadata().getType().equals("builtin") && !id.equals("minecraft");
    }

    private void filter() {
        this.shown.clear();
        String q = this.query.toLowerCase(Locale.ROOT).trim();
        for (ModContainer c : this.all) {
            if (!this.libraries && AstraModsScreen.isLibrary(c)) continue;
            ModMetadata m = c.getMetadata();
            if (!q.isEmpty() && !m.getName().toLowerCase(Locale.ROOT).contains(q) && !m.getId().contains(q) && !m.getDescription().toLowerCase(Locale.ROOT).contains(q)) continue;
            this.shown.add(c);
        }
        this.scrollTarget = 0.0f;
    }

    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    public boolean isPauseScreen() {
        return true;
    }

    private static Identifier icon(ModContainer c) {
        String id;
        block10: {
            Identifier identifier;
            block11: {
                id = c.getMetadata().getId();
                if (ICONS.containsKey(id)) {
                    return ICONS.get(id);
                }
                if (NO_ICON.containsKey(id)) {
                    return null;
                }
                Optional path = c.getMetadata().getIconPath(64);
                Optional file = path.flatMap(arg_0 -> ((ModContainer)c).findPath(arg_0));
                if (!file.isPresent()) break block10;
                InputStream in = Files.newInputStream((Path)file.get(), new OpenOption[0]);
                try {
                    NativeImage img = NativeImage.read((InputStream)in);
                    Identifier tex = Identifier.fromNamespaceAndPath((String)"astra", (String)("modicon/" + id.replaceAll("[^a-z0-9_.-]", "_")));
                    Minecraft.getInstance().getTextureManager().register(tex, (AbstractTexture)new DynamicTexture(() -> "astra_modicon", img));
                    ICONS.put(id, tex);
                    identifier = tex;
                    if (in == null) break block11;
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
                        AstraClient.LOGGER.debug("\u0418\u043a\u043e\u043d\u043a\u0430 \u043c\u043e\u0434\u0430 {} \u043d\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u043b\u0430\u0441\u044c", (Object)id, (Object)e);
                    }
                }
                in.close();
            }
            return identifier;
        }
        NO_ICON.put(id, true);
        return null;
    }

    private static Map<String, Function<Screen, Screen>> configScreens() {
        if (configScreens != null) {
            return configScreens;
        }
        configScreens = new HashMap<String, Function<Screen, Screen>>();
        try {
            for (EntrypointContainer ec : FabricLoader.getInstance().getEntrypointContainers("modmenu", Object.class)) {
                try {
                    Object provided;
                    Object api = ec.getEntrypoint();
                    String modId = ec.getProvider().getMetadata().getId();
                    Object factory = api.getClass().getMethod("getModConfigScreenFactory", new Class[0]).invoke(api, new Object[0]);
                    Function<Screen, Screen> f = AstraModsScreen.wrap(factory);
                    if (f != null) {
                        configScreens.put(modId, f);
                    }
                    if (!((provided = api.getClass().getMethod("getProvidedConfigScreenFactories", new Class[0]).invoke(api, new Object[0])) instanceof Map)) continue;
                    Map map = (Map)provided;
                    for (Map.Entry e : map.entrySet()) {
                        Function<Screen, Screen> pf = AstraModsScreen.wrap(e.getValue());
                        if (pf == null) continue;
                        configScreens.putIfAbsent(String.valueOf(e.getKey()), pf);
                    }
                }
                catch (Throwable t) {
                    AstraClient.LOGGER.debug("Mod Menu API: \u043f\u0440\u043e\u043f\u0443\u0441\u043a {}", (Object)ec.getProvider().getMetadata().getId(), (Object)t);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return configScreens;
    }

    private static Function<Screen, Screen> wrap(Object factory) {
        if (factory == null) {
            return null;
        }
        Method create = null;
        for (Class<?> i : factory.getClass().getInterfaces()) {
            for (Method m : i.getMethods()) {
                if (!m.getName().equals("create") || m.getParameterCount() != 1) continue;
                create = m;
            }
        }
        if (create == null) {
            return null;
        }
        Method mm = create;
        return parent -> {
            try {
                Screen sc;
                Object s = mm.invoke(factory, parent);
                return s instanceof Screen ? (sc = (Screen)s) : null;
            }
            catch (Exception e) {
                AstraClient.LOGGER.warn("\u042d\u043a\u0440\u0430\u043d \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a \u043c\u043e\u0434\u0430 \u043d\u0435 \u043e\u0442\u043a\u0440\u044b\u043b\u0441\u044f", (Throwable)e);
                return null;
            }
        };
    }

    private static String authors(ModMetadata m) {
        ArrayList<String> names = new ArrayList<String>();
        for (Person p : m.getAuthors()) {
            names.add(p.getName());
        }
        return String.join((CharSequence)", ", names);
    }

    public void renderBackground(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            this.renderPanorama(ctx, delta);
        } else {
            if (this.minecraft.options.getMenuBackgroundBlurriness() >= 1) {
                ctx.blurBeforeThisStratum();
            }
            Theme t = ThemeManager.get();
            ctx.fillGradient(0, 0, this.width, this.height, ColorUtil.withAlpha(t.bg(), 0.55f), ColorUtil.withAlpha(t.bg(), 0.75f));
        }
    }

    public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        boolean ch;
        super.render(ctx, mouseX, mouseY, delta);
        long now = System.nanoTime();
        this.dt = this.lastFrame == 0L ? 0.016f : Math.min(0.1f, (float)(now - this.lastFrame) / 1.0E9f);
        this.lastFrame = now;
        this.hits.clear();
        this.links.clear();
        Theme t = ThemeManager.get();
        float open = AnimationUtils.easeOutCubic(Math.min(1.0f, (float)(System.currentTimeMillis() - this.openedAt) / 260.0f));
        float prevA = Render2D.alpha;
        Render2D.alpha = open;
        float W = Math.min(this.width - 24, 720);
        float H = this.height - 24;
        float X = ((float)this.width - W) / 2.0f;
        float Y = 12.0f + (1.0f - open) * 12.0f;
        Render2D.glow(ctx, X, Y, W, H, 14.0f, 24.0f, ColorUtil.withAlpha(t.accent(), 0.25f));
        Render2D.roundRect(ctx, X, Y, W, H, 14.0f, Paint.linear(X, Y, X + W * 0.4f, Y + H, ColorUtil.withAlpha(ColorUtil.lerp(t.bg2(), t.accent(), 0.12f), 0.9f), ColorUtil.withAlpha(t.bg(), 0.94f)));
        Render2D.outline(ctx, X, Y, W, H, 14.0f, 1.3f, Paint.linear(X, Y, X + W, Y + H, ColorUtil.brighten(t.accent(), 0.15f), t.accent2()));
        float lw = 92.0f;
        ArtManager.drawLogo(ctx, X + 10.0f, Y + 4.0f, lw, 1.0f, t.accent(), t.accent2());
        Fonts.draw(ctx, "\u041c\u043e\u0434\u044b", X + lw + 20.0f, Y + 10.0f, 14.0f, t.text(), true);
        long libs = this.all.stream().filter(AstraModsScreen::isLibrary).count();
        Fonts.draw(ctx, "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u043e: " + ((long)this.all.size() - libs) + (String)(this.libraries ? "  \u00b7  \u0431\u0438\u0431\u043b\u0438\u043e\u0442\u0435\u043a: " + libs : ""), X + lw + 20.0f, Y + 26.0f, 6.6f, t.textMuted(), false);
        float closeX = X + W - 24.0f;
        float closeY = Y + 10.0f;
        boolean bl = ch = (float)mouseX >= closeX - 3.0f && (float)mouseX < closeX + 17.0f && (float)mouseY >= closeY - 3.0f && (float)mouseY < closeY + 17.0f;
        if (ch) {
            Render2D.circle(ctx, closeX + 7.0f, closeY + 7.0f, 10.0f, ColorUtil.withAlpha(t.accent(), 0.25f));
        }
        Icons.draw(ctx, Icon.CLOSE, closeX, closeY, 14.0f, ch ? t.text() : t.textDim());
        this.hits.add(new float[]{closeX - 3.0f, closeY - 3.0f, 20.0f, 20.0f, 9.0f});
        float top = Y + 42.0f;
        float bottom = Y + H - 34.0f;
        Render2D.rect(ctx, X + 12.0f, top - 4.0f, W - 24.0f, 0.8f, Paint.horizontal(X + 12.0f, X + W - 12.0f, t.accent2(), ColorUtil.withAlpha(t.accent(), 0.0f)));
        float lx = X + 12.0f;
        float lwid = Math.min(260.0f, W * 0.42f);
        float sy = top;
        boolean focus = true;
        Render2D.roundRect(ctx, lx, sy, lwid, 18.0f, 6.0f, ColorUtil.withAlpha(t.bg(), 0.8f));
        Render2D.outline(ctx, lx, sy, lwid, 18.0f, 6.0f, 0.8f, ColorUtil.withAlpha(t.accent(), focus ? 0.6f : 0.3f));
        Icons.draw(ctx, Icon.ZOOM, lx + 6.0f, sy + 4.5f, 9.0f, t.textDim());
        String q = this.query.isEmpty() ? "\u041f\u043e\u0438\u0441\u043a \u043c\u043e\u0434\u043e\u0432\u2026" : this.query + (System.currentTimeMillis() / 500L % 2L == 0L ? "|" : "");
        Fonts.draw(ctx, Fonts.trim(q, lwid - 30.0f, 7.2f, false), lx + 20.0f, sy + 5.5f, 7.2f, this.query.isEmpty() ? t.textMuted() : t.text(), false);
        float listTop = sy + 24.0f;
        float listBottom = bottom;
        float rowH = 32.0f;
        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, this.dt * 14.0f);
        float maxScroll = Math.max(0.0f, (float)this.shown.size() * (rowH + 3.0f) - (listBottom - listTop));
        this.scrollTarget = Math.max(0.0f, Math.min(this.scrollTarget, maxScroll));
        ctx.enableScissor((int)lx - 2, (int)listTop, (int)(lx + lwid + 2.0f), (int)listBottom);
        float ry = listTop - this.scroll;
        for (ModContainer c : this.shown) {
            if (ry + rowH >= listTop && ry <= listBottom) {
                this.drawRow(ctx, t, c, lx, ry, lwid, rowH, mouseX, mouseY, listTop, listBottom);
            }
            ry += rowH + 3.0f;
        }
        ctx.disableScissor();
        if (this.shown.isEmpty()) {
            Fonts.draw(ctx, "\u041d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u043e", lx + 6.0f, listTop + 6.0f, 7.4f, t.textMuted(), false);
        }
        if (maxScroll > 0.0f) {
            float trackH = listBottom - listTop;
            float thumbH = Math.max(20.0f, trackH * trackH / (trackH + maxScroll));
            float thumbY = listTop + (trackH - thumbH) * (this.scroll / maxScroll);
            Render2D.roundRect(ctx, lx + lwid + 3.0f, thumbY, 2.5f, thumbH, 1.2f, ColorUtil.withAlpha(t.accent(), 0.7f));
        }
        float dx = lx + lwid + 14.0f;
        float dw = X + W - 12.0f - dx;
        UiDraw.card(ctx, t, dx, top, dw, bottom - top, 10.0f, 0.0f, false);
        if (this.selected != null) {
            this.drawDetails(ctx, t, this.selected, dx + 12.0f, top + 12.0f, dw - 24.0f, bottom - top - 24.0f, mouseX, mouseY);
        }
        float by = Y + H - 26.0f;
        this.button(ctx, t, 0, X + 12.0f, by, 120.0f, 18.0f, this.libraries ? "\u0421\u043a\u0440\u044b\u0442\u044c \u0431\u0438\u0431\u043b\u0438\u043e\u0442\u0435\u043a\u0438" : "\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u0431\u0438\u0431\u043b\u0438\u043e\u0442\u0435\u043a\u0438", Icon.LAYERS, false, mouseX, mouseY);
        this.button(ctx, t, 1, X + W - 12.0f - 230.0f, by, 110.0f, 18.0f, "\u041f\u0430\u043f\u043a\u0430 \u0441 \u043c\u043e\u0434\u0430\u043c\u0438", Icon.FOLDER, false, mouseX, mouseY);
        this.button(ctx, t, 2, X + W - 12.0f - 112.0f, by, 112.0f, 18.0f, "\u0413\u043e\u0442\u043e\u0432\u043e", Icon.CHECK, true, mouseX, mouseY);
        Render2D.alpha = prevA;
    }

    private void drawRow(GuiGraphics ctx, Theme t, ModContainer c, float x, float y, float w, float h, int mx, int my, float clipTop, float clipBottom) {
        ModMetadata m = c.getMetadata();
        boolean sel = c == this.selected;
        boolean hov = (float)mx >= x && (float)mx < x + w && (float)my >= y && (float)my < y + h && (float)my >= clipTop && (float)my < clipBottom;
        float hv = this.rowHover.computeIfAbsent(m.getId(), k -> new Animation(0.0f, 16.0f)).update(hov ? 1.0f : 0.0f, this.dt);
        if (sel) {
            Render2D.glow(ctx, x, y, w, h, 7.0f, 6.0f, ColorUtil.withAlpha(t.accent(), 0.35f));
            Render2D.roundRect(ctx, x, y, w, h, 7.0f, Paint.horizontal(x, x + w, ColorUtil.withAlpha(t.accent(), 0.4f), ColorUtil.withAlpha(t.accent2(), 0.2f)));
            Render2D.outline(ctx, x, y, w, h, 7.0f, 0.9f, ColorUtil.withAlpha(t.accent2(), 0.8f));
        } else {
            Render2D.roundRect(ctx, x, y, w, h, 7.0f, ColorUtil.withAlpha(ColorUtil.lerp(t.card(), t.cardHover(), hv), 0.75f));
        }
        this.drawIcon(ctx, t, c, x + 4.0f, y + 4.0f, h - 8.0f);
        float tx = x + h + 2.0f;
        String name = m.getName();
        Fonts.draw(ctx, Fonts.trim(name, w - h - 60.0f, 7.6f, true), tx, y + 6.0f, 7.6f, t.text(), true);
        Fonts.draw(ctx, Fonts.trim(m.getDescription().replace('\n', ' '), w - h - 10.0f, 6.0f, false), tx, y + 18.0f, 6.0f, t.textMuted(), false);
        String ver = m.getVersion().getFriendlyString();
        Fonts.drawRight(ctx, Fonts.trim(ver, 50.0f, 5.8f, false), x + w - 6.0f, y + 6.5f, 5.8f, ColorUtil.withAlpha(t.accent2(), 0.9f), false);
        this.hits.add(new float[]{x, Math.max(y, clipTop), w, Math.min(y + h, clipBottom) - Math.max(y, clipTop), 100 + this.shown.indexOf(c)});
    }

    private void drawIcon(GuiGraphics ctx, Theme t, ModContainer c, float x, float y, float s) {
        Identifier icon = AstraModsScreen.icon(c);
        if (icon != null) {
            Render2D.textureRoundRect(ctx, icon, 1.0f, x, y, s, s, s * 0.22f, -1);
        } else {
            Render2D.roundRect(ctx, x, y, s, s, s * 0.22f, Paint.linear(x, y, x + s, y + s, t.accent(), t.accent2()));
            String letter = c.getMetadata().getName().isEmpty() ? "?" : c.getMetadata().getName().substring(0, 1).toUpperCase(Locale.ROOT);
            Fonts.drawCentered(ctx, letter, x + s / 2.0f, y + s * 0.22f, s * 0.55f, -1, true);
        }
    }

    private void drawDetails(GuiGraphics ctx, Theme t, ModContainer c, float x, float y, float w, float h, int mx, int my) {
        String[][] kinds;
        ModMetadata m = c.getMetadata();
        float s = 54.0f;
        Render2D.glow(ctx, x, y, s, s, 12.0f, 8.0f, ColorUtil.withAlpha(t.accent(), 0.35f));
        this.drawIcon(ctx, t, c, x, y, s);
        float tx = x + s + 12.0f;
        String name = m.getName();
        float ns = Math.min(15.0f, 15.0f * (w - s - 14.0f) / Math.max(1.0f, Fonts.width(name, 15.0f, true)));
        Fonts.drawGradient(ctx, name, tx, y + 2.0f, ns, ColorUtil.brighten(t.accent(), 0.35f), ColorUtil.brighten(t.accent2(), 0.3f), true);
        float cy = y + 22.0f;
        cy = this.chip(ctx, t, tx, cy, "v" + m.getVersion().getFriendlyString(), t.accent());
        float chipX = this.lastChipEnd + 4.0f;
        if (m.getEnvironment() == ModEnvironment.CLIENT) {
            this.chip(ctx, t, chipX, y + 22.0f, "\u041a\u043b\u0438\u0435\u043d\u0442", -13058568);
        } else if (m.getEnvironment() == ModEnvironment.SERVER) {
            this.chip(ctx, t, chipX, y + 22.0f, "\u0421\u0435\u0440\u0432\u0435\u0440", -680437);
        }
        String au = AstraModsScreen.authors(m);
        if (!au.isEmpty()) {
            Fonts.draw(ctx, Fonts.trim("\u0410\u0432\u0442\u043e\u0440\u044b: " + au, w - s - 14.0f, 6.8f, false), tx, y + 40.0f, 6.8f, t.textDim(), false);
        }
        float yy = y + s + 14.0f;
        Fonts.draw(ctx, "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435", x, yy, 7.4f, t.text(), true);
        yy += 12.0f;
        String desc = m.getDescription().isBlank() ? "\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435 \u043d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d\u043e." : m.getDescription();
        block0: for (String para : desc.split("\\R")) {
            for (String line : Fonts.wrap(para, w, 7.0f, false)) {
                if (yy > y + h - 70.0f) continue block0;
                Fonts.draw(ctx, line, x, yy, 7.0f, t.textDim(), false);
                yy += 10.0f;
            }
        }
        yy += 6.0f;
        String license = String.join((CharSequence)", ", m.getLicense());
        if (!license.isEmpty()) {
            Fonts.draw(ctx, "\u041b\u0438\u0446\u0435\u043d\u0437\u0438\u044f: " + license, x, yy, 6.6f, t.textMuted(), false);
            yy += 10.0f;
        }
        Fonts.draw(ctx, "ID: " + m.getId(), x, yy, 6.6f, t.textMuted(), false);
        float bx = x;
        float by = Math.max(yy += 14.0f, y + h - 22.0f);
        Function<Screen, Screen> cfg = AstraModsScreen.configScreens().get(m.getId());
        if (cfg != null) {
            this.button(ctx, t, 3, bx, by, 120.0f, 18.0f, "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043c\u043e\u0434\u0430", Icon.GEAR, true, mx, my);
            bx += 126.0f;
        }
        ContactInformation contact = m.getContact();
        for (String[] k : kinds = new String[][]{{"homepage", "\u0421\u0430\u0439\u0442"}, {"sources", "\u041a\u043e\u0434"}, {"issues", "\u041f\u0440\u043e\u0431\u043b\u0435\u043c\u044b"}}) {
            Optional url = contact.get(k[0]);
            if (url.isEmpty() || bx + 70.0f > x + w) continue;
            float bw = Fonts.width(k[1], 7.2f, true) + 30.0f;
            this.links.add(new String[]{(String)url.get()});
            this.button(ctx, t, -1, bx, by, bw, 18.0f, k[1], Icon.ARROW_UP, false, mx, my);
            this.hits.add(new float[]{bx, by, bw, 18.0f, 1000 + this.links.size() - 1});
            bx += bw + 6.0f;
        }
    }

    private float chip(GuiGraphics ctx, Theme t, float x, float y, String text, int color) {
        float w = Fonts.width(text, 6.2f, true) + 10.0f;
        Render2D.roundRect(ctx, x, y, w, 11.0f, 5.5f, ColorUtil.withAlpha(color, 0.18f));
        Render2D.outline(ctx, x, y, w, 11.0f, 5.5f, 0.7f, ColorUtil.withAlpha(color, 0.7f));
        Fonts.draw(ctx, text, x + 5.0f, y + 2.6f, 6.2f, ColorUtil.brighten(color, 0.35f), true);
        this.lastChipEnd = x + w;
        return y + 14.0f;
    }

    private void button(GuiGraphics ctx, Theme t, int id, float x, float y, float w, float h, String label, Icon icon, boolean primary, int mx, int my) {
        boolean hov;
        boolean bl = hov = (float)mx >= x && (float)mx < x + w && (float)my >= y && (float)my < y + h;
        float hv = id >= 0 && id < this.btnHover.length ? this.btnHover[id].update(hov ? 1.0f : 0.0f, this.dt) : (float)(hov ? 1 : 0);
        UiDraw.button(ctx, t, x, y, w, h, label, icon, hv, primary);
        if (id >= 0) {
            this.hits.add(new float[]{x, y, w, h, id});
        }
    }

    public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) {
            return super.mouseClicked(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), false);
        }
        for (int i = this.hits.size() - 1; i >= 0; --i) {
            float[] r = this.hits.get(i);
            if (mx < (double)r[0] || mx >= (double)(r[0] + r[2]) || my < (double)r[1] || my >= (double)(r[1] + r[3])) continue;
            int id = (int)r[4];
            if (id >= 1000) {
                this.openLink(this.links.get(id - 1000)[0]);
            } else if (id >= 100) {
                if (id - 100 < this.shown.size()) {
                    this.selected = this.shown.get(id - 100);
                }
            } else {
                switch (id) {
                    case 0: {
                        this.libraries = !this.libraries;
                        this.filter();
                        break;
                    }
                    case 1: {
                        Util.getPlatform().openFile(FabricLoader.getInstance().getGameDir().resolve("mods").toFile());
                        break;
                    }
                    case 2: 
                    case 9: {
                        this.onClose();
                        break;
                    }
                    case 3: {
                        Screen s;
                        Function<Screen, Screen> f = this.selected != null ? AstraModsScreen.configScreens().get(this.selected.getMetadata().getId()) : null;
                        Screen screen = s = f != null ? f.apply(this) : null;
                        if (s == null) break;
                        this.minecraft.setScreen(s);
                        break;
                    }
                }
            }
            return true;
        }
        return super.mouseClicked(new MouseButtonEvent(mx, my, new MouseButtonInfo(button, 0)), false);
    }

    private void openLink(String url) {
        try {
            Util.getPlatform().openUri(new URI(url));
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u0441\u0441\u044b\u043b\u043a\u0443 {}", (Object)url);
        }
    }

    public boolean mouseScrolled(double mx, double my, double h, double v) {
        this.scrollTarget -= (float)v * 30.0f;
        return true;
    }

    public boolean keyPressed(int key, int scan, int mods) {
        if (key == 259 && !this.query.isEmpty()) {
            this.query = (mods & 2) != 0 ? "" : this.query.substring(0, this.query.length() - 1);
            this.filter();
            return true;
        }
        if (key == 264 || key == 265) {
            int i = Math.max(0, this.shown.indexOf(this.selected) + (key == 264 ? 1 : -1));
            if (i < this.shown.size()) {
                this.selected = this.shown.get(i);
            }
            return true;
        }
        if (key == 86 && (mods & 2) != 0) {
            this.query = this.query + this.minecraft.keyboardHandler.getClipboard().replaceAll("\\R", " ");
            this.filter();
            return true;
        }
        return super.keyPressed(new KeyEvent(key, scan, mods));
    }

    public boolean charTyped(char chr, int modifiers) {
        if (chr >= ' ' && this.query.length() < 40) {
            this.query = this.query + chr;
            this.filter();
            if (!this.shown.isEmpty() && !this.shown.contains(this.selected)) {
                this.selected = this.shown.get(0);
            }
            return true;
        }
        return false;
    }

    private static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        return this.mouseClicked(click.x(), click.y(), click.button());
    }

    public boolean keyPressed(KeyEvent input) {
        return this.keyPressed(input.key(), input.scancode(), input.modifiers());
    }

    public boolean charTyped(CharacterEvent input) {
        boolean handled = false;
        for (char ch : input.codepointAsString().toCharArray()) {
            handled |= this.charTyped(ch, input.modifiers());
        }
        return handled;
    }
}

