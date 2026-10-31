/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
 *  net.fabricmc.fabric.api.client.screen.v1.Screens
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.StringWidget
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.network.chat.Component
 */
package astra.gui.vanilla;

import astra.gui.screens.AstraModsScreen;
import astra.gui.style.NativeScreenLayouts;
import astra.gui.vanilla.AstraWidgets;
import astra.module.ClientSettings;
import astra.render.ColorUtil;
import astra.render.Decorations;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

public final class MenuStyle {
    private static final Decorations PETALS = new Decorations(30);
    private static long lastFrame;

    private MenuStyle() {
    }

    public static boolean customMods() {
        ClientSettings cs = ClientSettings.get();
        return cs == null || (Boolean)cs.customModsScreen.get() != false;
    }

    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen || screen instanceof PauseScreen) {
                MenuStyle.replaceModsButton(screen);
            }
            if (screen instanceof PauseScreen && AstraWidgets.enabled()) {
                Screens.getWidgets((Screen)screen).removeIf(b -> b instanceof StringWidget && b.getMessage().getString().equals(screen.getTitle().getString()));
                ScreenEvents.afterExtract((Screen)screen).register((s, ctx, mx, my, delta) -> MenuStyle.drawPauseLogo(s, ctx));
            }
        });
    }

    public static void drawMenuBackground(GuiGraphicsExtractor ctx, int w, int h) {
        if (!AstraWidgets.enabled()) {
            return;
        }
        Theme t = ThemeManager.get();
        ctx.fillGradient(0, 0, w, h, ColorUtil.withAlpha(t.bg(), 0.28f), ColorUtil.withAlpha(ColorUtil.lerp(t.bg(), t.accent(), 0.25f), 0.55f));
        Render2D.shadow(ctx, -60.0f, h - 160, 300.0f, 200.0f, 100.0f, 120.0f, ColorUtil.withAlpha(t.accent(), 0.16f));
        Render2D.shadow(ctx, w - 240, -40.0f, 300.0f, 160.0f, 80.0f, 120.0f, ColorUtil.withAlpha(t.accent2(), 0.14f));
        NativeScreenLayouts.backdrop(NativeScreenLayouts.current(), ctx);
        float artW = Math.min((float)w * 0.26f, (float)h * 0.5f);
        NativeScreenLayouts.art(NativeScreenLayouts.current(), ctx, t, (float)w - artW - 6.0f, h, artW, (float)h * 0.82f, 1.0f);
        long now = System.nanoTime();
        float dt = lastFrame == 0L ? 0.016f : Math.min(0.1f, (float)(now - lastFrame) / 1.0E9f);
        lastFrame = now;
        PETALS.update(dt, w, h);
        PETALS.render(ctx, t, 0.9f);
    }

    public static void renderPausePreview(Screen screen, GuiGraphicsExtractor ctx) {
        MenuStyle.drawPauseLogo(screen, ctx);
    }

    private static void drawPauseLogo(Screen screen, GuiGraphicsExtractor ctx) {
        int minY = Integer.MAX_VALUE;
        for (AbstractWidget b : Screens.getWidgets((Screen)screen)) {
            if (!(b instanceof Button)) continue;
            minY = Math.min(minY, b.getY());
        }
        List<NativeScreenLayouts.Entry> originals = NativeScreenLayouts.entries(screen);
        if (!originals.isEmpty()) {
            minY = originals.stream().mapToInt(e -> e.base().y()).min().orElse(minY);
        }
        if (minY == Integer.MAX_VALUE) {
            return;
        }
        float lw = 150.0f;
        float lh = lw * 280.0f / 720.0f;
        float y = Math.max(2.0f, (float)minY - lh - 6.0f);
        Theme t = ThemeManager.get();
        NativeScreenLayouts.logo(screen, ctx, ((float)screen.width - lw) / 2.0f, y, lw, 1.0f);
    }

    private static void replaceModsButton(Screen screen) {
        if (!MenuStyle.customMods()) {
            return;
        }
        List<AbstractWidget> buttons = Screens.getWidgets((Screen)screen);
        String modmenu = Component.translatable((String)"modmenu.title").getString();
        ArrayList<AbstractWidget> found = new ArrayList<AbstractWidget>();
        for (AbstractWidget b : buttons) {
            String m = b.getMessage().getString();
            if (!(b instanceof Button) || !m.equals("\u041c\u043e\u0434\u044b") && !m.equals("Mods") && !m.equals(modmenu) && !m.startsWith("\u041c\u043e\u0434\u044b (") && !m.startsWith("Mods (")) continue;
            found.add(b);
        }
        for (AbstractWidget old : found) {
            Button mine = Button.builder((Component)Component.literal((String)"\u041c\u043e\u0434\u044b"), btn -> Minecraft.getInstance().gui.setScreen((Screen)new AstraModsScreen(screen))).bounds(old.getX(), old.getY(), old.getWidth(), old.getHeight()).build();
            int i = buttons.indexOf(old);
            buttons.remove(old);
            buttons.add(Math.min(i, buttons.size()), mine);
        }
        if (found.isEmpty() && screen instanceof TitleScreen) {
            buttons.add(Button.builder((Component)Component.literal((String)"\u041c\u043e\u0434\u044b"), btn -> Minecraft.getInstance().gui.setScreen((Screen)new AstraModsScreen(screen))).bounds(4, 4, 60, 18).build());
        }
    }
}

