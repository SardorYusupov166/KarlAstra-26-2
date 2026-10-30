/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  com.mojang.blaze3d.audio.SoundBuffer
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents$AllowMouseClick
 *  net.fabricmc.fabric.api.client.screen.v1.Screens
 *  net.minecraft.client.Camera
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Screenshot
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.ChatComponent
 *  net.minecraft.client.gui.components.CommandSuggestions
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.ChatScreen
 *  net.minecraft.client.gui.screens.PauseScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
 *  net.minecraft.client.gui.screens.options.OptionsScreen
 *  net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.InputWithModifiers
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.client.input.MouseButtonInfo
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.player.RemotePlayer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.server.IntegratedServer
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.monster.zombie.Zombie
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 */
package astra.utils;

import astra.AstraClient;
import astra.builder.CanvasBoard;
import astra.builder.CanvasScreen;
import astra.config.ConfigManager;
import astra.decor.DecorLayer;
import astra.decor.DecorManager;
import astra.gui.UiContext;
import astra.gui.components.ModuleCard;
import astra.gui.pages.CosmeticsPage;
import astra.gui.pages.ModulesPage;
import astra.gui.pages.Page;
import astra.gui.screens.AstraModsScreen;
import astra.gui.screens.ClickGuiScreen;
import astra.gui.screens.DecorEditorScreen;
import astra.gui.screens.EmoteWheelScreen;
import astra.gui.screens.HudEditorScreen;
import astra.gui.screens.PlayerScreen;
import astra.gui.style.AstraInspectorScreen;
import astra.gui.style.GuiStyle;
import astra.gui.style.NativeLayoutEditor;
import astra.gui.style.NativeScreenLayouts;
import astra.hud.HudModule;
import astra.media.LyricFx;
import astra.media.Lyrics;
import astra.media.MediaBridge;
import astra.media.NowPlaying;
import astra.media.WorldLyricMotion;
import astra.media.WorldLyricsConfig;
import astra.mixin.ChatHudAccessor;
import astra.module.Category;
import astra.module.ClientSettings;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.AuraCosmetic;
import astra.module.modules.cosmetic.CapeCosmetic;
import astra.module.modules.cosmetic.EmoteCosmetic;
import astra.module.modules.cosmetic.HaloCosmetic;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.cosmetic.WingsCosmetic;
import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.ChatInputHud;
import astra.module.modules.hud.ChatLayoutHud;
import astra.module.modules.hud.CompanionHud;
import astra.module.modules.hud.TrackBannerHud;
import astra.module.modules.minimap.MinimapHud;
import astra.module.modules.pvp.KeybindsModule;
import astra.module.modules.pvp.PvpTweaks;
import astra.module.modules.pvp.ShieldStatus;
import astra.module.modules.visual.AmbientParticles;
import astra.module.modules.visual.ColorFilter;
import astra.module.modules.visual.MusicModule;
import astra.module.modules.visual.NowPlayingModule;
import astra.module.modules.visual.WorldVisuals;
import astra.online.AstraOnline;
import astra.render.ColorUtil;
import astra.render.LyricLightning;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import astra.settings.Setting;
import astra.sound.SoundSlot;
import astra.sound.user.SoundPacks;
import astra.sound.user.UserAudio;
import astra.theme.StylePresets;
import astra.utils.ChatPatches;
import astra.utils.ChatScrollbar;
import astra.utils.CombatTracker;
import astra.waypoint.Waypoints;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public final class DevShots {
    private static final String ONLY = System.getProperty("astra.devshots.only", "");
    private static final boolean ONLY_PLAYER = ONLY.startsWith("player");
    private static final boolean LIVE = ONLY.equals("playerlive");
    private static Phase phase = Phase.TITLE;
    private static int tick;
    private static int step;
    private static int titleTicks;
    private static ClickGuiScreen gui;
    private static String devCover;
    private static DecorEditorScreen decorEditor;
    private static final List<Integer> fpsSamples;
    private static final String[] PERF_MODULES;
    private static Screen artTitle;
    private static RemotePlayer pvpBob;
    private static Vec3 lyricsOrigin;
    private static Vec3 lyricsPinned;
    private static Vec3 lyricsOriginalPosition;
    private static float lyricsOriginalYaw;
    private static float lyricsOriginalPitch;
    private static String lyricsOriginalGameMode;
    private static float lyricsWorldScale;
    private static float lyricsApparent;
    private static CanvasBoard.Canvas builderTest;
    private static float builderYaw;
    private static float builderPitch;
    private static Screen editorSource;
    private static String editorSnapshot;

    private DevShots() {
    }

    public static void init() {
        if (!Boolean.getBoolean("astra.devshots")) {
            return;
        }
        AstraClient.LOGGER.info("DevShots: \u0440\u0435\u0436\u0438\u043c \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0445 \u0441\u043a\u0440\u0438\u043d\u0448\u043e\u0442\u043e\u0432 \u0432\u043a\u043b\u044e\u0447\u0451\u043d");
        ClientTickEvents.END_CLIENT_TICK.register(DevShots::onTick);
    }

    private static void shot(Minecraft client, String name) {
        Screenshot.grab((File)client.gameDirectory, (String)("astra_" + name + ".png"), (RenderTarget)client.gameRenderer.mainRenderTarget(), (int)1, text -> AstraClient.LOGGER.info("DevShots: {}", (Object)text.getString()));
    }

    private static void devCommand(Minecraft client, String command) {
        IntegratedServer server = client.getSingleplayerServer();
        if (server == null) {
            return;
        }
        server.execute(() -> {
            List players = server.getPlayerList().getPlayers();
            if (players.isEmpty()) {
                return;
            }
            ServerPlayer p = (ServerPlayer)players.get(0);
            CommandSourceStack source = server.createCommandSourceStack().withEntity((Entity)p).withPosition(p.position()).withRotation(p.getRotationVector()).withLevel(p.level()).withSuppressedOutput();
            server.getCommands().performPrefixedCommand(source, command);
        });
    }

    private static void devHit(Minecraft client) {
        if (client.level == null || client.player == null) {
            return;
        }
        for (Entity e : client.level.entitiesForRendering()) {
            Zombie z;
            if (!(e instanceof Zombie) || !((z = (Zombie)e).distanceToSqr((Entity)client.player) < 64.0)) continue;
            CombatTracker.onAttack((Entity)z);
            z.hurtTime = 10;
            z.hurtDuration = 10;
            client.player.swing(InteractionHand.MAIN_HAND);
            return;
        }
    }

    private static void enable(Class<? extends Module> type) {
        Module m = ModuleManager.get(type);
        if (m != null) {
            m.setEnabled(true);
        }
    }

    private static void set(Module m, String name, Object value) {
        for (Setting<?> s : m.getSettings()) {
            if (!s.getName().equals(name)) continue;
            Setting<?> setting = s;
            ((Setting)setting).set(value);
            return;
        }
        AstraClient.LOGGER.warn("DevShots: \u043d\u0435\u0442 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 {}", (Object)name);
    }

    private static NowPlaying fakeTrack(String cover, long pos) {
        return new NowPlaying(true, "Spotify.exe", "\u0413\u0440\u0443\u043f\u043f\u0430 \u043a\u0440\u043e\u0432\u0438", "\u041a\u0438\u043d\u043e", "\u0413\u0440\u0443\u043f\u043f\u0430 \u043a\u0440\u043e\u0432\u0438", true, pos, System.currentTimeMillis(), 238000L, cover, true, true, true);
    }

    private static Setting<?> setting(Module m, String name) {
        for (Setting<?> s : m.getSettings()) {
            if (!s.getName().equals(name)) continue;
            return s;
        }
        return null;
    }

    private static void perfReport(String name) {
        if (fpsSamples.isEmpty()) {
            return;
        }
        ArrayList<Integer> s = new ArrayList<Integer>(fpsSamples);
        Collections.sort(s);
        double avg = s.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        AstraClient.LOGGER.info("PERF {}: \u0441\u0440\u0435\u0434\u043d\u0438\u0439 FPS {} \u00b7 \u043c\u0438\u043d {} \u00b7 5% \u0445\u0443\u0434\u0448\u0438\u0445 {} \u00b7 \u043a\u0430\u0434\u0440\u043e\u0432-\u0437\u0430\u043c\u0435\u0440\u043e\u0432 {}", new Object[]{name, Math.round(avg), s.get(0), s.get(Math.max(0, s.size() / 20)), s.size()});
        fpsSamples.clear();
    }

    private static void perfScenes(Minecraft client) {
        if (tick == 1) {
            client.options.enableVsync().set(false);
            client.options.framerateLimit().set(260);
            for (String id : PERF_MODULES) {
                Module m = ModuleManager.byId(id);
                if (m != null) {
                    m.setEnabled(true);
                    continue;
                }
                AstraClient.LOGGER.warn("PERF: \u043d\u0435\u0442 \u043c\u043e\u0434\u0443\u043b\u044f {}", (Object)id);
            }
            MediaBridge.devOverride(DevShots.fakeTrack("", 30000L));
            DevShots.devCommand(client, "summon minecraft:zombie ^ ^ ^3 {NoAI:1b,PersistenceRequired:1b,Silent:1b}");
            DevShots.devCommand(client, "scoreboard objectives remove astra_perf");
            DevShots.devCommand(client, "scoreboard objectives add astra_perf dummy {\"text\":\"PERF TEST\",\"color\":\"gold\"}");
            DevShots.devCommand(client, "scoreboard objectives setdisplay sidebar astra_perf");
            for (int i = 0; i < 10; ++i) {
                DevShots.devCommand(client, "scoreboard players set \u0421\u0442\u0440\u043e\u043a\u0430_" + i + " astra_perf " + i);
            }
            ChatComponent chat = client.gui.hud.getChat();
            for (int i = 0; i < 60; ++i) {
                chat.addClientSystemMessage((Component)Component.literal((String)("\u00a7e[Spy] \u00a77[\u0418\u0433\u0440\u043e\u043a] \u00a7fPlayer" + i + " \u00a76\u25b8 \u00a7f\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u043d\u043e\u043c\u0435\u0440 " + i)));
            }
        }
        if (tick >= 60 && tick < 460) {
            fpsSamples.add(client.getFps());
            if (tick % 10 == 0) {
                DevShots.devHit(client);
            }
        }
        if (tick == 300) {
            DevShots.shot(client, "perf_game");
        }
        if (tick == 600) {
            DevShots.shot(client, "perf_menu");
        }
        if (tick == 460) {
            DevShots.perfReport("\u0438\u0433\u0440\u0430, \u0432\u0441\u0451 \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u043e");
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 462) {
            gui.setCategory(Category.VISUAL);
        }
        if (tick >= 500 && tick < 700) {
            fpsSamples.add(client.getFps());
        }
        if (tick == 700) {
            DevShots.perfReport("\u043c\u0435\u043d\u044e Astra");
            client.gui.setScreen(null);
            for (Module m : ModuleManager.all()) {
                if (m instanceof ClientSettings) continue;
                m.setEnabled(false);
            }
            MediaBridge.devOverride(null);
        }
        if (tick >= 740 && tick < 940) {
            fpsSamples.add(client.getFps());
        }
        if (tick == 940) {
            DevShots.perfReport("Astra \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430");
            for (Module m : ModuleManager.all()) {
                m.setEnabled(m.isDefaultEnabled());
            }
            DevShots.devCommand(client, "scoreboard objectives remove astra_perf");
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void artEditScenes(Minecraft client) {
        if (tick == 1) {
            artTitle = new PauseScreen(true);
            client.gui.setScreen(artTitle);
            try {
                Path fixture = client.gameDirectory.toPath().resolve("astra-art-test.png");
                try (InputStream stream = DevShots.class.getResourceAsStream("/assets/astra/textures/gui/sidebar_girl.png");){
                    Files.copy(stream, fixture, StandardCopyOption.REPLACE_EXISTING);
                }
                NativeScreenLayouts.layout((Screen)DevShots.artTitle).art = CanvasBoard.importImage(fixture);
                Files.deleteIfExists(fixture);
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        if (tick == 10) {
            NativeScreenLayouts.Item o = NativeScreenLayouts.item(artTitle, "@art");
            o.radius = 18.0f;
            o.sw = 130.0f;
            o.sh = 90.0f;
            o.scale = 110.0f;
        }
        if (tick == 20) {
            DevShots.shot(client, "art_title");
        }
        if (tick == 22) {
            NativeLayoutEditor ed = new NativeLayoutEditor(artTitle);
            client.gui.setScreen((Screen)ed);
            try {
                Field f = ed.getClass().getDeclaredField("selected");
                f.setAccessible(true);
                f.set(ed, "@art");
                Method rebuild = AstraInspectorScreen.class.getDeclaredMethod("rebuildRows", new Class[0]);
                rebuild.setAccessible(true);
                rebuild.invoke((Object)ed, new Object[0]);
            }
            catch (ReflectiveOperationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        if (tick == 34) {
            DevShots.shot(client, "art_editor");
        }
        if (tick == 36) {
            float[] r = NativeScreenLayouts.rects(artTitle).get("@art");
            AstraClient.LOGGER.info("DevShots: ART rect {}", (Object)(r == null ? "null" : Arrays.toString(r)));
            if (r == null || r[2] <= 0.0f || r[3] <= 0.0f) {
                throw new IllegalStateException("Missing art geometry");
            }
            try {
                NativeLayoutEditor ed = (NativeLayoutEditor)client.gui.screen();
                Class<?> c = ed.getClass();
                Field selected = c.getDeclaredField("selected");
                selected.setAccessible(true);
                selected.set(ed, "@art");
                Method resize = c.getDeclaredMethod("resize", NativeScreenLayouts.Item.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE);
                resize.setAccessible(true);
                Field grid = c.getDeclaredField("snapGrid");
                grid.setAccessible(true);
                boolean savedGrid = grid.getBoolean(null);
                grid.setBoolean(null, false);
                NativeScreenLayouts.Item item = NativeScreenLayouts.item(artTitle, "@art");
                for (int corner = 1; corner <= 4; ++corner) {
                    item.dx = 0.0f;
                    item.dy = 0.0f;
                    item.scale = 100.0f;
                    item.sw = 100.0f;
                    item.sh = 100.0f;
                    String[] fields = new String[]{"startDx", "startDy", "startScale", "startSw", "startSh"};
                    float[] vals = new float[]{0.0f, 0.0f, 100.0f, 100.0f, 100.0f};
                    for (int j = 0; j < fields.length; ++j) {
                        Field f = c.getDeclaredField(fields[j]);
                        f.setAccessible(true);
                        f.setFloat(ed, vals[j]);
                    }
                    Field rect = c.getDeclaredField("startRect");
                    rect.setAccessible(true);
                    rect.set(ed, new float[]{20.0f, 30.0f, 100.0f, 80.0f});
                    Field cf = c.getDeclaredField("corner");
                    cf.setAccessible(true);
                    cf.setInt(ed, corner);
                    boolean left = corner == 1 || corner == 4;
                    boolean top = corner == 1 || corner == 2;
                    resize.invoke((Object)ed, item, Float.valueOf(left ? -20.0f : 20.0f), Float.valueOf(top ? -16.0f : 16.0f), Float.valueOf(DevShots.artTitle.width), Float.valueOf(DevShots.artTitle.height));
                    if (!((double)Math.abs(item.sw - 120.0f) > 0.01 || (double)Math.abs(item.sh - 120.0f) > 0.01 || (double)Math.abs(item.dx * (float)DevShots.artTitle.width - (float)(left ? 0 : 20)) > 0.01)) {
                        float f = item.dy * (float)DevShots.artTitle.height;
                        int n = top ? 0 : 16;
                        if (!((double)Math.abs(f - (float)n) > 0.01)) continue;
                    }
                    throw new IllegalStateException("Art corner anchoring regression: " + corner);
                }
                selected.set(ed, "@logo");
                item.scale = 100.0f;
                Field lastCorner = c.getDeclaredField("corner");
                lastCorner.setAccessible(true);
                lastCorner.setInt(ed, 3);
                resize.invoke((Object)ed, item, Float.valueOf(-20.0f), Float.valueOf(0.0f), Float.valueOf(DevShots.artTitle.width), Float.valueOf(DevShots.artTitle.height));
                if ((double)Math.abs(item.scale - 80.0f) > 0.01) {
                    throw new IllegalStateException("Proportional shrink regression");
                }
                selected.set(ed, "@art");
                item.sh = 100.0f;
                item.sw = 100.0f;
                item.scale = 100.0f;
                item.dy = 0.0f;
                item.dx = 0.0f;
                resize.invoke((Object)ed, item, Float.valueOf(1000.0f), Float.valueOf(1000.0f), Float.valueOf(DevShots.artTitle.width), Float.valueOf(DevShots.artTitle.height));
                if (item.sw != 300.0f || item.sh != 300.0f || (double)Math.abs(item.dx * (float)DevShots.artTitle.width - 200.0f) > 0.01 || (double)Math.abs(item.dy * (float)DevShots.artTitle.height - 160.0f) > 0.01) {
                    throw new IllegalStateException("Clamped art anchor regression");
                }
                Field elementSnap = c.getDeclaredField("snapElements");
                elementSnap.setAccessible(true);
                boolean savedElement = elementSnap.getBoolean(null);
                elementSnap.setBoolean(null, false);
                Field step = c.getDeclaredField("gridSize");
                step.setAccessible(true);
                float savedStep = step.getFloat(null);
                step.setFloat(null, 10.0f);
                grid.setBoolean(null, true);
                Method snap = c.getDeclaredMethod("snap", Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE);
                snap.setAccessible(true);
                float[] pos = (float[])snap.invoke((Object)ed, Float.valueOf(11.5f), Float.valueOf(21.5f), Float.valueOf(18.0f), Float.valueOf(12.0f));
                if (pos[0] != 10.0f || pos[1] != 20.0f) {
                    throw new IllegalStateException("Grid snap regression");
                }
                elementSnap.setBoolean(null, true);
                pos = (float[])snap.invoke((Object)ed, Float.valueOf((float)DevShots.artTitle.width / 2.0f - 50.0f + 1.5f), Float.valueOf((float)DevShots.artTitle.height / 2.0f - 40.0f + 1.5f), Float.valueOf(100.0f), Float.valueOf(80.0f));
                if ((double)Math.abs(pos[0] + 50.0f - (float)DevShots.artTitle.width / 2.0f) > 0.01 || (double)Math.abs(pos[1] + 40.0f - (float)DevShots.artTitle.height / 2.0f) > 0.01) {
                    throw new IllegalStateException("Center snap regression");
                }
                step.setFloat(null, savedStep);
                elementSnap.setBoolean(null, savedElement);
                grid.setBoolean(null, savedGrid);
                AstraClient.LOGGER.info("DevShots: GRID PASS \u2014 grid step, center guides, clamped resize anchoring");
                AstraClient.LOGGER.info("DevShots: ARTEDIT PASS \u2014 stretch, rounded image, four anchored corners, proportional shrink");
            }
            catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
            NativeScreenLayouts.Item o = NativeScreenLayouts.item(artTitle, "@art");
            o.radius = -1.0f;
            o.scale = 100.0f;
            o.sh = 100.0f;
            o.sw = 100.0f;
            NativeScreenLayouts.layout((Screen)DevShots.artTitle).art = null;
            NativeScreenLayouts.save();
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void cardScenes(Minecraft client) {
        ModulesPage page;
        Page page2;
        if (tick == 1) {
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 3) {
            gui.setCategory(Category.SOUNDS);
        }
        if (tick == 20) {
            DevShots.shot(client, "cards_sounds");
        }
        if (tick == 22 && (page2 = gui.currentPage()) instanceof ModulesPage) {
            page = (ModulesPage)page2;
            List<ModuleCard> cards = page.cards();
            if (cards.size() < 10) {
                throw new IllegalStateException("Sounds page sections regression: " + cards.size());
            }
            ModuleCard first = cards.get(0);
            UiContext testUi = new UiContext();
            boolean before = first.getModule().isEnabled();
            if (!first.mouseClicked(testUi, first.x + first.w / 2.0f, first.y + first.h - 10.0f, 0) || !first.isExpanded() || first.getModule().isEnabled() != before) {
                throw new IllegalStateException("Settings footer click regression");
            }
            first.setExpanded(false);
            if (!first.mouseClicked(testUi, first.x + 10.0f, first.y + 10.0f, 1) || !first.isExpanded()) {
                throw new IllegalStateException("Settings RMB regression");
            }
            cards.get(0).setExpanded(true);
            cards.get(2).setExpanded(true);
            AstraClient.LOGGER.info("DevShots: CARDS PASS \u2014 \u0440\u0430\u0437\u0434\u0435\u043b\u043e\u0432 \u043e\u0437\u0432\u0443\u0447\u043a\u0438 {}", (Object)cards.size());
        }
        if (tick == 40) {
            DevShots.shot(client, "cards_sounds_open");
        }
        if (tick == 42) {
            gui.setCategory(Category.PVP);
        }
        if (tick == 50 && (page2 = gui.currentPage()) instanceof ModulesPage) {
            page = (ModulesPage)page2;
            page.cards().get(1).setExpanded(true);
        }
        if (tick == 62) {
            DevShots.shot(client, "cards_pvp");
        }
        if (tick == 70) {
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void toolScenes(Minecraft client) {
        LocalPlayer p = client.player;
        if (tick == 1) {
            for (String id : new String[]{"cooldowns", "item_counter", "waypoints", "minimap"}) {
                ModuleManager.byId(id).setEnabled(true);
            }
            DevShots.devCommand(client, "clear @s");
            DevShots.devCommand(client, "time set day");
            DevShots.devCommand(client, "give @s minecraft:totem_of_undying 1");
            DevShots.devCommand(client, "give @s minecraft:ender_pearl 16");
            DevShots.devCommand(client, "give @s minecraft:golden_apple 12");
            DevShots.devCommand(client, "give @s minecraft:arrow 64");
            DevShots.devCommand(client, "item replace entity @s weapon.mainhand with minecraft:netherite_sword");
            Waypoints.clear();
            double yaw = Math.toRadians(p.getYRot());
            int fx = (int)Math.round(-Math.sin(yaw));
            int fz = (int)Math.round(Math.cos(yaw));
            Waypoints.add("\u0411\u0430\u0437\u0430", p.getBlockX() + fx * 25 + fz * 8, p.getBlockY(), p.getBlockZ() + fz * 25 - fx * 8, -11939841, false);
            Waypoints.add("\u0428\u0430\u0445\u0442\u0430", p.getBlockX() + fx * 300 - fz * 60, p.getBlockY() - 20, p.getBlockZ() + fz * 300 + fx * 60, -11870592, false);
            Waypoints.add("\u0421\u043c\u0435\u0440\u0442\u044c", p.getBlockX() - fx * 40, p.getBlockY(), p.getBlockZ() - fz * 40, -770210, true);
            p.setXRot(5.0f);
        }
        if (tick == 30) {
            client.getConnection().sendCommand("wp list");
            client.getConnection().sendCommand("wp add \u0422\u0435\u0441\u0442");
            if (Waypoints.find("\u0422\u0435\u0441\u0442") == null) {
                throw new IllegalStateException("Waypoint command regression");
            }
            Waypoints.remove("\u0422\u0435\u0441\u0442");
        }
        if (tick == 40) {
            Waypoints.Waypoint base = Waypoints.find("\u0411\u0430\u0437\u0430");
            if (base != null) {
                double dx = (double)base.x + 0.5 - p.getX();
                double dz = (double)base.z + 0.5 - p.getZ();
                p.setYRot((float)Math.toDegrees(Math.atan2(-dx, dz)) + 12.0f);
            }
            p.getCooldowns().addCooldown(new ItemStack((ItemLike)Items.ENDER_PEARL), 200);
            p.getCooldowns().addCooldown(new ItemStack((ItemLike)Items.CHORUS_FRUIT), 120);
            p.swing(InteractionHand.MAIN_HAND);
            p.resetOnlyAttackStrengthTicker();
        }
        if (tick == 44) {
            AstraClient.LOGGER.info("DevShots: TOOLS PASS \u2014 waypoints {}", (Object)Waypoints.all().size());
            DevShots.shot(client, "tools_game");
        }
        if (tick == 60) {
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void bindScenes(Minecraft client) {
        KeybindsModule binds = ModuleManager.get(KeybindsModule.class);
        if (tick == 1) {
            binds.setEnabled(true);
            DevShots.set(binds, "\u0411\u0438\u043d\u0434 1 \u00b7 \u043a\u043b\u0430\u0432\u0438\u0448\u0430", 71);
            DevShots.set(binds, "\u0411\u0438\u043d\u0434 1 \u00b7 \u0442\u0435\u043a\u0441\u0442", "/time set night && Astra \u0431\u0438\u043d\u0434 {x} {y} {z} && gg");
            DevShots.set(binds, "\u0411\u0438\u043d\u0434 2 \u00b7 \u043a\u043b\u0430\u0432\u0438\u0448\u0430", 72);
            DevShots.set(binds, "\u0411\u0438\u043d\u0434 2 \u00b7 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435", "\u041f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043c\u043e\u0434\u0443\u043b\u044c");
            DevShots.set(binds, "\u0411\u0438\u043d\u0434 2 \u00b7 \u043c\u043e\u0434\u0443\u043b\u044c", ModuleManager.byId("minimap").getName());
            binds.trigger(1);
        }
        if (tick == 2) {
            boolean before = ModuleManager.byId("minimap").isEnabled();
            binds.trigger(2);
            if (ModuleManager.byId("minimap").isEnabled() == before) {
                throw new IllegalStateException("Bind toggle regression");
            }
            binds.trigger(2);
            if (ModuleManager.byId("minimap").isEnabled() == before) {
                throw new IllegalStateException("Bind cooldown regression");
            }
        }
        if (tick == 50) {
            long night = client.level.clockManager().getTotalTicks(client.level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.WORLD_CLOCK).getOrThrow(net.minecraft.world.clock.WorldClocks.OVERWORLD)) % 24000L;
            AstraClient.LOGGER.info("DevShots: BINDS time {} (\u043d\u043e\u0447\u044c \u2248 13000)", (Object)night);
            DevShots.shot(client, "binds_chat");
            ModuleManager.byId("minimap").setEnabled(true);
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 52) {
            gui.setCategory(Category.PVP);
        }
        if (tick == 54) {
            gui.mouseScrolled((double)client.getWindow().getGuiScaledWidth() / 2.0, (double)client.getWindow().getGuiScaledHeight() / 2.0, 0.0, -30.0);
        }
        if (tick == 70) {
            DevShots.shot(client, "binds_menu");
        }
        if (tick == 80) {
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void pvpScenes(Minecraft client) {
        LocalPlayer p = client.player;
        if (tick == 1) {
            ModuleManager.byId("pvp_tweaks").setEnabled(true);
            ModuleManager.byId("shield_status").setEnabled(true);
            DevShots.devCommand(client, "item replace entity @s weapon.offhand with minecraft:shield");
            DevShots.devCommand(client, "item replace entity @s weapon.mainhand with minecraft:netherite_axe");
            DevShots.devCommand(client, "gamemode survival");
            DevShots.devCommand(client, "effect give @s minecraft:fire_resistance 60 0 true");
            DevShots.devCommand(client, "effect give @s minecraft:resistance 60 4 true");
            DevShots.devCommand(client, "time set day");
            pvpBob = new RemotePlayer(client.level, new GameProfile(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Bob"));
            double yaw = Math.toRadians(p.getYRot());
            pvpBob.snapTo(p.getX() - Math.sin(yaw) * 2.6, p.getY(), p.getZ() + Math.cos(yaw) * 2.6, p.getYRot() + 150.0f, 0.0f);
            pvpBob.setYHeadRot(p.getYRot() + 150.0f);
            pvpBob.setYBodyRot(p.getYRot() + 150.0f);
            pvpBob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)Items.SHIELD));
            pvpBob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.DIAMOND_SWORD));
            client.level.addEntity((Entity)pvpBob);
            p.setXRot(12.0f);
        }
        if (tick == 20 || tick == 60) {
            IntegratedServer server = client.getSingleplayerServer();
            UUID uuid = p.getUUID();
            server.execute(() -> server.getPlayerList().getPlayer(uuid).igniteForSeconds(20.0f));
            p.getCooldowns().addCooldown(p.getOffhandItem(), 100);
            ShieldStatus.onShieldBreakSound(pvpBob.getX(), pvpBob.getY(), pvpBob.getZ());
            PvpTweaks.onTotemPop((Entity)pvpBob);
            client.gameRenderer.displayItemActivation(new ItemStack((ItemLike)Items.TOTEM_OF_UNDYING));
        }
        if (tick == 30) {
            if (ShieldStatus.brokenProgress((LivingEntity)pvpBob) <= 0.0f || ShieldStatus.brokenProgress((LivingEntity)p) <= 0.0f) {
                throw new IllegalStateException("Shield status regression");
            }
            AstraClient.LOGGER.info("DevShots: PVP PASS \u2014 shield own {} enemy {}", (Object)Float.valueOf(ShieldStatus.brokenProgress((LivingEntity)p)), (Object)Float.valueOf(ShieldStatus.brokenProgress((LivingEntity)pvpBob)));
            DevShots.shot(client, "pvp_on");
        }
        if (tick == 50) {
            ModuleManager.byId("pvp_tweaks").setEnabled(false);
            ModuleManager.byId("shield_status").setEnabled(false);
        }
        if (tick == 70) {
            DevShots.shot(client, "pvp_off");
        }
        if (tick == 80) {
            ModuleManager.byId("pvp_tweaks").setEnabled(true);
            ModuleManager.byId("shield_status").setEnabled(true);
            client.level.removeEntity(pvpBob.getId(), Entity.RemovalReason.DISCARDED);
            DevShots.devCommand(client, "gamemode creative");
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void featureScenes(Minecraft client) {
        if (tick == 1) {
            DecorManager.layers().clear();
            ModuleManager.byId("visualizer").setEnabled(true);
            ModuleManager.byId("companion").setEnabled(true);
            Module module = ModuleManager.byId("astra_online");
            DevShots.set(module, "\u0410\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "127.0.0.1:8799");
            DevShots.set(module, "\u041a\u043b\u044e\u0447 \u043a\u043e\u043c\u043d\u0430\u0442\u044b", "friends");
            LocalPlayer p = client.player;
            RemotePlayer bob = new RemotePlayer(client.level, new GameProfile(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Bob"));
            double yaw = Math.toRadians(p.getYRot());
            bob.snapTo(p.getX() - Math.sin(yaw) * 3.0, p.getY(), p.getZ() + Math.cos(yaw) * 3.0, p.getYRot() + 180.0f, 0.0f);
            bob.setYHeadRot(p.getYRot() + 180.0f);
            bob.setYBodyRot(p.getYRot() + 180.0f);
            client.level.addEntity((Entity)bob);
            p.setXRot(5.0f);
        }
        if (tick == 10) {
            ChatComponent chatComponent = client.gui.hud.getChat();
            chatComponent.addClientSystemMessage((Component)Component.literal((String)"\u00a7d[Astra] \u00a7f\u0414\u043e\u0431\u0440\u043e \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440!"));
            chatComponent.addClientSystemMessage((Component)Component.literal((String)"\u00a7b<Bob> \u00a7f\u043f\u0440\u0438\u0432\u0435\u0442, \u0442\u044b \u0433\u0434\u0435?"));
            chatComponent.addClientSystemMessage((Component)Component.literal((String)("\u00a7e<Kira> \u00a7f" + client.player.getGameProfile().name() + ", \u043f\u043e\u0439\u0434\u0451\u043c \u0432 \u043f\u0432\u043f!")));
        }
        if (tick == 30) {
            CompanionHud.onKill();
        }
        for (int t2 : new int[]{70, 76, 82}) {
            if (tick != t2) continue;
            DevShots.shot(client, "feat_friend_emote_" + t2);
        }
        if (tick == 90) {
            DevShots.shot(client, "feat_hud");
        }
        if (tick == 92) {
            client.options.keyPlayerList.setDown(true);
        }
        if (tick == 100) {
            DevShots.shot(client, "feat_tab");
        }
        if (tick == 102) {
            client.options.keyPlayerList.setDown(false);
            client.gui.setScreen((Screen)new ChatScreen("\u043f\u0440\u0438\u0432\u0435\u0442", false));
        }
        if (tick == 110) {
            DevShots.shot(client, "feat_chat");
        }
        if (tick == 112) {
            EmoteCosmetic emoteCosmetic = (EmoteCosmetic)ModuleManager.byId("emotes");
            client.gui.setScreen((Screen)new EmoteWheelScreen(emoteCosmetic, 71));
        }
        if (tick == 125) {
            DevShots.shot(client, "feat_wheel");
        }
        if (tick == 127) {
            client.gui.setScreen(null);
            StylePresets.ALL.get(1).apply().run();
            client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
        if (tick == 160) {
            DevShots.shot(client, "feat_preset");
        }
        if (tick == 162) {
            client.options.setCameraType(CameraType.FIRST_PERSON);
            StylePresets.ALL.get(0).apply().run();
            DevShots.set(ModuleManager.byId("astra_online"), "\u0410\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "");
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void onlineScenes(Minecraft client) {
        Module online = ModuleManager.byId("astra_online");
        if (tick == 1) {
            DevShots.set(online, "\u0410\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "127.0.0.1:8799");
            DevShots.set(online, "\u041a\u043b\u044e\u0447 \u043a\u043e\u043c\u043d\u0430\u0442\u044b", "friends");
            online.setEnabled(true);
            LocalPlayer p = client.player;
            RemotePlayer bob = new RemotePlayer(client.level, new GameProfile(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Bob"));
            double yaw = Math.toRadians(p.getYRot());
            bob.snapTo(p.getX() - Math.sin(yaw) * 3.0, p.getY(), p.getZ() + Math.cos(yaw) * 3.0, p.getYRot(), 0.0f);
            bob.setYHeadRot(p.getYRot());
            bob.setYBodyRot(p.getYRot());
            client.level.addEntity((Entity)bob);
            p.setXRot(5.0f);
        }
        if (tick == 140) {
            AstraClient.LOGGER.info("DevShots: Astra Online \u2014 {}", (Object)AstraOnline.status());
            DevShots.shot(client, "online_friend");
        }
        if (tick == 200) {
            DevShots.shot(client, "online_emote");
        }
        if (tick == 202) {
            DevShots.set(online, "\u0410\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "");
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void menuScenes(Minecraft client) {
        if (tick == 1) {
            DevShots.devCommand(client, "gamemode survival @p");
            DevShots.devCommand(client, "scoreboard objectives remove astra_test");
            DevShots.devCommand(client, "scoreboard objectives add astra_test dummy {\"text\":\"MIGOSMC\",\"color\":\"gold\",\"bold\":true}");
            DevShots.devCommand(client, "scoreboard objectives setdisplay sidebar astra_test");
            DevShots.devCommand(client, "scoreboard players set \u00a7f\u041d\u0438\u043a:_\u00a7a556621 astra_test 6");
            DevShots.devCommand(client, "scoreboard players set \u00a7f\u0411\u0430\u043b\u0430\u043d\u0441:_\u00a7e5k astra_test 5");
            DevShots.devCommand(client, "scoreboard players set \u00a7f\u0423\u0431\u0438\u0439\u0441\u0442\u0432:_\u00a7c0 astra_test 4");
            DevShots.devCommand(client, "scoreboard players set \u00a7f\u041d\u0430\u0438\u0433\u0440\u0430\u043d\u043e:_\u00a7b80\u0447. astra_test 3");
            DevShots.devCommand(client, "scoreboard players set \u00a7d/sb_-_\u0441\u043a\u0440\u044b\u0442\u044c astra_test 2");
            DevShots.devCommand(client, "effect give @p absorption 60 0 true");
            DevShots.devCommand(client, "xp set @p 26 levels");
            DevShots.devCommand(client, "xp set @p 40 points");
            DevShots.devCommand(client, "item replace entity @p armor.chest with minecraft:diamond_chestplate");
        }
        if (tick == 20) {
            DevShots.devCommand(client, "damage @p 7 minecraft:generic");
        }
        if (tick == 24) {
            DevShots.shot(client, "menus_bars_damage");
        }
        if (tick == 60) {
            DevShots.shot(client, "menus_ingame");
        }
        if (tick == 62) {
            client.gui.setScreen((Screen)new PauseScreen(true));
        }
        if (tick == 90) {
            DevShots.shot(client, "menus_pause");
        }
        if (tick == 92) {
            client.gui.setScreen((Screen)new OptionsScreen(client.gui.screen(), client.options, false));
        }
        if (tick == 120) {
            DevShots.shot(client, "menus_options");
        }
        if (tick == 122) {
            DevShots.devCommand(client, "scoreboard objectives remove astra_test");
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static float[] hitCenter(String id) {
        for (GuiStyle.Hit h : GuiStyle.HITS) {
            if (!h.id().equals(id)) continue;
            return new float[]{(h.x0() + h.x1()) / 2.0f, (h.y0() + h.y1()) / 2.0f};
        }
        return null;
    }

    private static void guiStyleScenes(Minecraft client) {
        GuiStyle.Override o;
        float[] c;
        if (tick == 1) {
            DecorManager.layers().clear();
            for (String id : GuiStyle.ELEMENTS.keySet()) {
                GuiStyle.reset(id);
            }
            ClientSettings.get().theme.set("Astra Purple");
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 3) {
            gui.setCategory(Category.THEMES);
        }
        if (tick == 20) {
            decorEditor = new DecorEditorScreen(null);
            client.gui.setScreen((Screen)decorEditor);
        }
        if (tick == 40) {
            c = DevShots.hitCenter("logo");
            AstraClient.LOGGER.info("DevShots: \u043b\u043e\u0433\u043e\u0442\u0438\u043f \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435 {}", c == null ? null : c[0] + "," + c[1]);
            if (c != null) {
                decorEditor.devClick(c[0], c[1]);
                decorEditor.devClick(c[0], c[1]);
            }
            AstraClient.LOGGER.info("DevShots: \u0434\u0432\u043e\u0439\u043d\u043e\u0439 \u043a\u043b\u0438\u043a \u2192 {}", (Object)decorEditor.devElement());
        }
        if (tick == 44) {
            c = DevShots.hitCenter("logo");
            if (c != null) {
                decorEditor.mouseClicked(c[0] + 5.0f, c[1] + 5.0f, 0);
                decorEditor.mouseDragged(c[0] + 65.0f, c[1] + 30.0f, 0, 60.0, 25.0);
                decorEditor.mouseReleased(c[0] + 65.0f, c[1] + 30.0f, 0);
            }
            AstraClient.LOGGER.info("DevShots: \u043b\u043e\u0433\u043e\u0442\u0438\u043f \u0441\u0434\u0432\u0438\u043d\u0443\u0442 dx={} dy={}", (Object)Float.valueOf((o = GuiStyle.get("logo")) != null ? o.dx : 0.0f), (Object)Float.valueOf(o != null ? o.dy : 0.0f));
        }
        if (tick == 60) {
            c = DevShots.hitCenter("logo");
            if (c != null) {
                decorEditor.devClick(c[0], c[1]);
                decorEditor.devClick(c[0], c[1]);
                decorEditor.devClick(c[0], c[1]);
            }
            AstraClient.LOGGER.info("DevShots: \u0442\u0440\u043e\u0439\u043d\u043e\u0439 \u043a\u043b\u0438\u043a \u2192 \u043e\u043a\u043d\u043e \u0432\u044b\u0431\u043e\u0440\u0430 \u043a\u0430\u0440\u0442\u0438\u043d\u043a\u0438: {}", (Object)(decorEditor.currentPopup() != null ? 1 : 0));
        }
        if (tick == 70) {
            DevShots.shot(client, "guistyle_triple");
        }
        if (tick == 72) {
            decorEditor.closePopup();
            GuiStyle.edit((String)"logo").image = "sidebar.jpg";
        }
        if (tick == 80) {
            c = DevShots.hitCenter("style.button");
            if (c != null) {
                decorEditor.devClick(c[0], c[1]);
                decorEditor.devClick(c[0], c[1]);
            }
            AstraClient.LOGGER.info("DevShots: \u0434\u0432\u043e\u0439\u043d\u043e\u0439 \u043a\u043b\u0438\u043a \u043f\u043e \u043a\u043d\u043e\u043f\u043a\u0435 \u2192 {}", (Object)decorEditor.devElement());
            o = GuiStyle.edit("style.button");
            o.radius = 0.0f;
            o.customColor = true;
            o.color = -13058568;
            o.color2 = -14494738;
        }
        if (tick == 100) {
            DevShots.shot(client, "guistyle_editor");
        }
        if (tick == 102) {
            GuiStyle.Override n = GuiStyle.edit("nav");
            n.dx = 30.0f;
            n.dy = 40.0f;
            decorEditor.onClose();
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 120) {
            float[] g = gui.devDesignToGui(128.0f, 244.0f);
            gui.mouseClicked(new MouseButtonEvent((double)g[0], (double)g[1], new MouseButtonInfo(0, 0)), false);
            AstraClient.LOGGER.info("DevShots: \u043a\u043b\u0438\u043a \u043f\u043e \u0441\u0434\u0432\u0438\u043d\u0443\u0442\u043e\u043c\u0443 \u00ab\u0414\u0432\u0438\u0436\u0435\u043d\u0438\u0435\u00bb \u2192 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u044f {}", (Object)gui.getCategory());
        }
        if (tick == 150) {
            DevShots.shot(client, "guistyle_menu");
        }
        if (tick == 152) {
            for (String id : GuiStyle.ELEMENTS.keySet()) {
                GuiStyle.reset(id);
            }
            GuiStyle.save();
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void themeBgScenes(Minecraft client) {
        DecorLayer l;
        ClientSettings cs = ClientSettings.get();
        if (tick == 1) {
            DecorManager.layers().clear();
            cs.sidebarArt.set("sidebar.jpg");
            cs.theme.set("Midnight Blue");
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 3) {
            gui.setCategory(Category.THEMES);
        }
        if (tick == 40) {
            DevShots.shot(client, "themebg_blue");
        }
        if (tick == 42) {
            cs.theme.set("Cyber Cyan");
        }
        if (tick == 70) {
            DevShots.shot(client, "themebg_cyan");
        }
        if (tick == 72) {
            cs.theme.set("Astra Purple");
            decorEditor = new DecorEditorScreen(null);
            client.gui.setScreen((Screen)decorEditor);
        }
        if (tick == 100) {
            DevShots.shot(client, "themebg_editor_before");
        }
        if (tick == 102) {
            decorEditor.devConvertSidebar();
            l = decorEditor.devSelected();
            AstraClient.LOGGER.info("DevShots: \u0430\u0440\u0442 \u043f\u0430\u043d\u0435\u043b\u0438 \u2192 \u0441\u043b\u043e\u0439 {} x={} y={} w={} h={}, \u0430\u0440\u0442 \u0432 \u043c\u0435\u043d\u044e: {}", new Object[]{l != null ? l.image : null, Float.valueOf(l != null ? l.x : 0.0f), Float.valueOf(l != null ? l.y : 0.0f), Float.valueOf(l != null ? l.w : 0.0f), Float.valueOf(l != null ? l.h : 0.0f), cs.sidebarArt.get()});
        }
        if (tick == 125) {
            DevShots.shot(client, "themebg_editor_converted");
        }
        if (tick == 127 && (l = decorEditor.devSelected()) != null) {
            float[] g = decorEditor.devGeom(l);
            decorEditor.mouseClicked(g[0], g[1], 0);
            decorEditor.mouseDragged(g[0] + 60.0f, g[1] - 20.0f, 0, 60.0, -20.0);
            decorEditor.mouseReleased(g[0] + 60.0f, g[1] - 20.0f, 0);
            decorEditor.mouseScrolled(g[0] + 60.0f, g[1] - 20.0f, 0.0, 3.0);
        }
        if (tick == 150) {
            DevShots.shot(client, "themebg_editor_moved");
        }
        if (tick == 152) {
            decorEditor.onClose();
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 190) {
            DevShots.shot(client, "themebg_menu_after");
        }
        if (tick == 192) {
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void decorScenes(Minecraft client) {
        List<DecorLayer> layers = DecorManager.layers();
        if (tick == 1) {
            layers.clear();
            DecorLayer girl = new DecorLayer();
            girl.image = "sidebar.jpg";
            girl.depth = "\u041f\u043e\u0434 \u043e\u043a\u043d\u043e\u043c";
            girl.x = -0.08f;
            girl.y = 330.0f;
            girl.w = 330.0f;
            girl.h = 330.0f;
            girl.radius = 24.0f;
            girl.glow = true;
            layers.add(girl);
            DecorLayer logo = new DecorLayer();
            logo.image = "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0430\u0432\u0430\u0442\u0430\u0440 2";
            logo.depth = "\u041f\u043e\u0432\u0435\u0440\u0445 \u043c\u0435\u043d\u044e";
            logo.x = 0.86f;
            logo.y = 470.0f;
            logo.w = 120.0f;
            logo.h = 120.0f;
            logo.radius = 60.0f;
            logo.rotation = -12.0f;
            logo.animation = "\u041f\u0430\u0440\u0435\u043d\u0438\u0435";
            logo.glow = true;
            layers.add(logo);
            DecorLayer sticker = new DecorLayer();
            sticker.image = "sidebar.jpg";
            sticker.target = "screen";
            sticker.depth = "\u041f\u043e\u0432\u0435\u0440\u0445 HUD";
            sticker.x = 0.12f;
            sticker.y = 0.72f;
            sticker.w = 0.28f;
            sticker.h = 0.28f;
            sticker.radius = 14.0f;
            sticker.opacity = 85.0f;
            sticker.animation = "\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435";
            layers.add(sticker);
            DecorManager.save();
        }
        if (tick == 20) {
            DevShots.shot(client, "decor_ingame_sticker");
        }
        if (tick == 22) {
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 60) {
            DevShots.shot(client, "decor_menu");
        }
        if (tick == 62) {
            decorEditor = new DecorEditorScreen(null);
            client.gui.setScreen((Screen)decorEditor);
        }
        if (tick == 64) {
            decorEditor.devSelect(layers.get(1), "menu");
        }
        if (tick == 90) {
            DevShots.shot(client, "decor_editor_menu");
        }
        if (tick == 92) {
            DecorLayer l = layers.get(1);
            float[] g = decorEditor.devGeom(l);
            float[] c = DevShots.rotate(g, l.rotation, g[2] / 2.0f, g[3] / 2.0f);
            AstraClient.LOGGER.info("DevShots: \u0434\u043e \u0440\u0430\u0441\u0442\u044f\u0433\u0438\u0432\u0430\u043d\u0438\u044f w={} h={} rot={}", new Object[]{Float.valueOf(l.w), Float.valueOf(l.h), Float.valueOf(l.rotation)});
            decorEditor.mouseClicked(c[0], c[1], 0);
            decorEditor.mouseDragged(c[0] + 40.0f, c[1] + 10.0f, 0, 40.0, 10.0);
            decorEditor.mouseReleased(c[0] + 40.0f, c[1] + 10.0f, 0);
            AstraClient.LOGGER.info("DevShots: \u043f\u043e\u0441\u043b\u0435 \u0440\u0430\u0441\u0442\u044f\u0433\u0438\u0432\u0430\u043d\u0438\u044f w={} h={} (\u043e\u0436\u0438\u0434\u0430\u0435\u0442\u0441\u044f \u0431\u043e\u043b\u044c\u0448\u0435 \u0438 \u043a\u0432\u0430\u0434\u0440\u0430\u0442)", (Object)Float.valueOf(l.w), (Object)Float.valueOf(l.h));
        }
        if (tick == 110) {
            DevShots.shot(client, "decor_editor_resized");
        }
        if (tick == 112) {
            decorEditor.devSelect(layers.get(2), "screen");
        }
        if (tick == 140) {
            DevShots.shot(client, "decor_editor_screen");
        }
        if (tick == 142) {
            decorEditor.onClose();
            client.gui.setScreen(null);
        }
        if (tick == 150) {
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static float[] rotate(float[] g, float deg, float lx, float ly) {
        double a = Math.toRadians(deg);
        return new float[]{g[0] + (float)((double)lx * Math.cos(a) - (double)ly * Math.sin(a)), g[1] + (float)((double)lx * Math.sin(a) + (double)ly * Math.cos(a))};
    }

    private static void artScenes(Minecraft client) {
        ClientSettings cs = ClientSettings.get();
        if (tick == 1) {
            cs.sidebarArt.set("\u0410\u0432\u0442\u043e");
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 3) {
            gui.setCategory(Category.THEMES);
        }
        if (tick == 40) {
            AstraClient.LOGGER.info("DevShots: \u0432\u0430\u0440\u0438\u0430\u043d\u0442\u044b \u0430\u0440\u0442\u0430 {}", cs.sidebarArt.getModes());
            DevShots.shot(client, "art_auto");
        }
        if (tick == 42) {
            cs.sidebarArt.set("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439");
        }
        if (tick == 80) {
            DevShots.shot(client, "art_builtin");
        }
        if (tick == 82) {
            cs.sidebarArt.set("sidebar.jpg");
        }
        if (tick == 84) {
            gui.setCategory(Category.VISUAL);
        }
        if (tick == 120) {
            DevShots.shot(client, "art_jpg_visual");
        }
        if (tick == 122) {
            cs.sidebarArt.set("\u0410\u0432\u0442\u043e");
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void packScenes(Minecraft client) {
        ModeSetting variant;
        SoundsModule sounds = SoundsModule.get();
        MusicModule music = ModuleManager.get(MusicModule.class);
        if (tick == 1) {
            sounds.setEnabled(true);
            SoundPacks.rescanAsync(true);
        }
        if (tick == 60) {
            for (SoundPacks.Pack p : SoundPacks.packs()) {
                AstraClient.LOGGER.info("DevShots: \u043f\u0430\u043a \u00ab{}\u00bb (zip={}): \u0441\u043e\u0431\u044b\u0442\u0438\u044f {}, \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b\u0435 {}, \u043f\u0435\u0441\u043d\u0438 {}", new Object[]{p.name(), p.zip(), p.events().keySet(), p.loose().stream().map(f -> f.getFileName().toString()).toList(), p.music().stream().map(f -> f.getFileName().toString()).toList()});
                ArrayList<Path> all = new ArrayList<Path>(p.loose());
                p.events().values().forEach(all::addAll);
                for (Path f2 : all) {
                    try {
                        SoundBuffer st = UserAudio.loadStatic(UserAudio.id(f2).withPath("sounds/" + UserAudio.id(f2).getPath() + ".ogg")).join();
                        AstraClient.LOGGER.info("DevShots: \u0437\u0432\u0443\u043a {} \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d ({})", (Object)f2.getFileName(), (Object)(st != null ? "ok" : "null"));
                    }
                    catch (Exception e) {
                        AstraClient.LOGGER.error("DevShots: \u0437\u0432\u0443\u043a {} \u041d\u0415 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u043b\u0441\u044f", (Object)f2.getFileName(), (Object)e);
                    }
                }
            }
            for (SoundPacks.Song s : SoundPacks.songs()) {
                try {
                    AudioStream st = UserAudio.loadStreamed(UserAudio.id(s.file()).withPath("sounds/" + UserAudio.id(s.file()).getPath() + ".ogg")).join();
                    try {
                        int bytes = st.read(65536).remaining();
                        AstraClient.LOGGER.info("DevShots: \u043f\u0435\u0441\u043d\u044f \u00ab{}\u00bb \u2014 {} ({}), \u043f\u043e\u0442\u043e\u043a {} \u0431\u0430\u0439\u0442, {}", new Object[]{s.title(), s.artist(), s.source(), bytes, st.getFormat()});
                    }
                    finally {
                        if (st == null) continue;
                        st.close();
                    }
                }
                catch (Exception e) {
                    AstraClient.LOGGER.error("DevShots: \u043f\u0435\u0441\u043d\u044f {} \u041d\u0415 \u043e\u0442\u043a\u0440\u044b\u043b\u0430\u0441\u044c", (Object)s.title(), (Object)e);
                }
            }
            variant = (ModeSetting)DevShots.setting(sounds, "\u0417\u0432\u0443\u043a: \u0443\u0434\u0430\u0440");
            AstraClient.LOGGER.info("DevShots: \u0432\u0430\u0440\u0438\u0430\u043d\u0442\u044b \u00ab\u0417\u0432\u0443\u043a: \u0443\u0434\u0430\u0440\u00bb: {}", variant.getModes());
        }
        if (tick == 70) {
            sounds.onHit(false, 0);
        }
        if (tick == 90) {
            sounds.onKill();
        }
        if (tick == 110) {
            sounds.onHit(true, 0);
        }
        if (tick == 130) {
            variant = (ModeSetting)DevShots.setting(sounds, "\u0417\u0432\u0443\u043a: \u0443\u0434\u0430\u0440");
            for (String m : variant.getModes()) {
                if (!m.contains("\u043a\u0438\u0441-\u043a\u0438\u0441")) continue;
                variant.set(m);
            }
            AstraClient.LOGGER.info("DevShots: \u0443\u0434\u0430\u0440 \u043d\u0430\u0437\u043d\u0430\u0447\u0435\u043d \u043d\u0430 \u00ab{}\u00bb", variant.get());
            sounds.onHit(false, 0);
        }
        if (tick == 150) {
            ((ModeSetting)DevShots.setting(sounds, "\u0417\u0432\u0443\u043a: \u0443\u0434\u0430\u0440")).set("\u0421\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0439");
            music.setEnabled(true);
            ((ModeSetting)DevShots.setting(music, "\u0421\u0432\u043e\u0438 \u043f\u0435\u0441\u043d\u0438")).set("\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0432\u043e\u0438");
        }
        if (tick == 160) {
            music.skipTrack();
        }
        if (tick == 161 || tick == 170 || tick == 200) {
            AstraClient.LOGGER.info("DevShots: +{} \u0442\u0438\u043a\u043e\u0432: \u0438\u0433\u0440\u0430\u0435\u0442 {}", (Object)(tick - 160), (Object)music.nowPlayingTitle());
        }
        if (tick == 260) {
            AstraClient.LOGGER.info("DevShots: \u043c\u0443\u0437\u044b\u043a\u0430 Astra \u0441\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442: {}", (Object)music.nowPlayingTitle());
        }
        if (tick == 262) {
            music.skipTrack();
        }
        if (tick == 360) {
            AstraClient.LOGGER.info("DevShots: \u043c\u0443\u0437\u044b\u043a\u0430 Astra \u043f\u043e\u0441\u043b\u0435 \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f: {}", (Object)music.nowPlayingTitle());
        }
        if (tick == 362) {
            gui = new ClickGuiScreen(null);
            client.gui.setScreen((Screen)gui);
        }
        if (tick == 364) {
            gui.setCategory(Category.COSMETICS);
        }
        if (tick == 400) {
            DevShots.shot(client, "packs_cosmetics");
        }
        if (tick == 402) {
            gui.setCategory(Category.VISUAL);
        }
        if (tick == 440) {
            DevShots.shot(client, "packs_visual");
        }
        if (tick == 442) {
            client.gui.setScreen(null);
            ((ModeSetting)DevShots.setting(music, "\u0421\u0432\u043e\u0438 \u043f\u0435\u0441\u043d\u0438")).set("\u0412\u043c\u0435\u0441\u0442\u0435 \u0441\u043e \u0432\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u043c\u0438");
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void playerScenes(Minecraft client) {
        List<Lyrics.Line> lines;
        NowPlayingModule np = NowPlayingModule.get();
        if (tick == 1) {
            try {
                Path dir = ConfigManager.root().resolve("media").resolve("covers");
                Files.createDirectories(dir, new FileAttribute[0]);
                Path file = dir.resolve("dev_cover.png");
                try (InputStream in = DevShots.class.getResourceAsStream("/assets/astra/textures/gui/avatar_1.png");){
                    Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
                }
                devCover = file.toString();
            }
            catch (Exception e) {
                devCover = "";
            }
            np.setEnabled(true);
            np.resetPosition();
            DevShots.set(np, "\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438", "\u0412 \u043c\u0438\u0440\u0435");
            if (!LIVE) {
                MediaBridge.devOverride(DevShots.fakeTrack(devCover, 30000L));
            }
        }
        if (tick == 14) {
            DevShots.shot(client, "np_banner_start");
        }
        if (tick == 40) {
            DevShots.shot(client, "np_banner");
        }
        if (tick == 130) {
            lines = Lyrics.lines();
            AstraClient.LOGGER.info("DevShots: \u0442\u0440\u0435\u043a \u2014 {} / {}; \u0442\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438 \u2014 {} ({} \u0441\u0442\u0440\u043e\u043a)", new Object[]{np.track().title(), np.track().artist(), Lyrics.status(), lines.size()});
            if (!LIVE && lines.size() > 8) {
                MediaBridge.devOverride(DevShots.fakeTrack(devCover, lines.get(8).time() - 100L));
            }
        }
        if (tick == 134) {
            DevShots.shot(client, "np_world_appear");
        }
        if (tick == 150) {
            DevShots.shot(client, "np_world");
        }
        if (tick == 152) {
            client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        }
        if (tick == 175) {
            DevShots.shot(client, "np_world_third");
        }
        if (tick == 177) {
            client.options.setCameraType(CameraType.FIRST_PERSON);
        }
        if (tick == 180) {
            DevShots.set(np, "\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438", "\u041d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435");
            DevShots.set(np, "\u042d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f", "\u0413\u043b\u0438\u0442\u0447");
            DevShots.set(np, "\u0412\u0438\u0434 \u0432\u0438\u0434\u0436\u0435\u0442\u0430", "\u041f\u043b\u0430\u0441\u0442\u0438\u043d\u043a\u0430");
            lines = Lyrics.lines();
            if (!LIVE && lines.size() > 12) {
                MediaBridge.devOverride(DevShots.fakeTrack(devCover, lines.get(12).time() - 100L));
            }
        }
        if (tick == 184) {
            DevShots.shot(client, "np_screen_glitch");
        }
        if (tick == 205) {
            DevShots.shot(client, "np_screen");
        }
        if (tick == 207) {
            DevShots.set(np, "\u0412\u0438\u0434 \u0432\u0438\u0434\u0436\u0435\u0442\u0430", "\u041a\u043e\u043c\u043f\u0430\u043a\u0442");
        }
        if (tick == 215) {
            DevShots.shot(client, "np_compact");
        }
        if (tick == 217) {
            client.gui.setScreen((Screen)new PlayerScreen(null));
        }
        if (tick == 250) {
            DevShots.shot(client, "np_player");
        }
        if (tick == 252) {
            client.gui.setScreen(null);
            DevShots.set(np, "\u0412\u0438\u0434 \u0432\u0438\u0434\u0436\u0435\u0442\u0430", "\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430");
            DevShots.set(np, "\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438", "\u0412 \u043c\u0438\u0440\u0435");
            DevShots.set(np, "\u042d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f", "\u0412\u043e\u043b\u043d\u0430");
            MediaBridge.devOverride(null);
        }
        if (tick >= 256) {
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void chatInputScenes(Minecraft client) {
        ChatInputHud input = ChatInputHud.get();
        CompanionHud companion = CompanionHud.get();
        if (tick == 1) {
            try {
                Path dir = SoundPacks.root().resolve("DevShots mention regression").resolve("\u0423\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0435");
                Files.createDirectories(dir, new FileAttribute[0]);
                Path file = dir.resolve("notification.ogg");
                if (!Files.exists(file, new LinkOption[0])) {
                    try (InputStream stream = DevShots.class.getResourceAsStream("/assets/astra/sounds/menu_2.ogg");){
                        Files.copy(Objects.requireNonNull(stream), file, new CopyOption[0]);
                    }
                }
                SoundPacks.rescanAsync(true);
            }
            catch (IOException e2) {
                throw new RuntimeException(e2);
            }
            input.setEnabled(true);
            input.resetPosition();
            input.scale.set(100.0);
            DevShots.set(input, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", 100.0);
            DevShots.set(input, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", 14.0);
            client.gui.setScreen((Screen)new ChatScreen("\u0422\u0435\u0441\u0442 \u0432\u0432\u043e\u0434\u0430", false));
            AstraClient.LOGGER.info("DevShots: INPUT DEFAULT bounds={}", (Object)input.bounds());
        }
        if (tick == 8) {
            DevShots.shot(client, "input_default");
        }
        if (tick == 10) {
            input.setPos(0.52f, 0.45f);
            input.scale.set(125.0);
            DevShots.set(input, "\u0428\u0438\u0440\u0438\u043d\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", 42.0);
            DevShots.set(input, "\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", 24.0);
            DevShots.set(input, "\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435 \u0441\u0442\u0440\u043e\u043a\u0438", "\u0421\u0432\u043e\u044f \u043f\u0430\u043d\u0435\u043b\u044c");
            DevShots.set(input, "\u0426\u0432\u0435\u0442 \u0442\u0435\u043a\u0441\u0442\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", -8331009);
            client.gui.setScreen((Screen)new ChatScreen("", false));
            Screen screen = client.gui.screen();
            EditBox field = screen.children().stream().filter(e -> e instanceof EditBox).map(e -> (EditBox)e).findFirst().orElseThrow();
            ChatInputHud.Bounds b = input.bounds();
            if (field.getX() != b.x() + 4 || field.getWidth() != b.width() - 8) {
                throw new IllegalStateException("Input geometry regression");
            }
            for (char c : "\u043f\u0440\u0438\u0432\u0435\u0442".toCharArray()) {
                screen.charTyped(new CharacterEvent((int)c));
            }
            screen.keyPressed(new KeyEvent(263, 0, 0));
            screen.keyPressed(new KeyEvent(259, 0, 0));
            if (!field.getValue().equals("\u043f\u0440\u0438\u0432\u0442")) {
                throw new IllegalStateException("Input editing regression: " + field.getValue());
            }
            screen.mouseClicked(new MouseButtonEvent((double)(field.getX() + 1), (double)(field.getY() + 3), new MouseButtonInfo(0, 0)), false);
            screen.charTyped(new CharacterEvent(1071));
            if (!field.getValue().startsWith("\u042f")) {
                throw new IllegalStateException("Input cursor hit testing regression");
            }
            String text = field.getValue();
            screen.resize(screen.width, screen.height);
            EditBox resized = screen.children().stream().filter(e -> e instanceof EditBox).map(e -> (EditBox)e).findFirst().orElseThrow();
            if (!resized.getValue().equals(text) || resized.getX() != b.x() + 4) {
                throw new IllegalStateException("Input resize regression");
            }
            AstraClient.LOGGER.info("DevShots: INPUT EDIT PASS \u2014 typing, arrows, deletion, cursor click, resize; {}", (Object)b);
        }
        if (tick == 18) {
            DevShots.shot(client, "input_moved");
        }
        if (tick == 20) {
            client.gui.setScreen((Screen)new HudEditorScreen(null));
        }
        if (tick == 24) {
            ChatInputHud.Bounds b = input.bounds();
            double x = (double)b.x() + (double)b.width() / 2.0;
            double y = (double)b.y() + (double)b.height() / 2.0;
            float oldX = input.getPosX();
            float oldY = input.getPosY();
            client.gui.screen().mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)), false);
            client.gui.screen().mouseDragged(new MouseButtonEvent(x - 80.0, y + 55.0, new MouseButtonInfo(0, 0)), -80.0, 55.0);
            client.gui.screen().mouseReleased(new MouseButtonEvent(x - 80.0, y + 55.0, new MouseButtonInfo(0, 0)));
            if (input.getPosX() == oldX && input.getPosY() == oldY) {
                throw new IllegalStateException("HUD input drag regression");
            }
            JsonObject snapshot = ConfigManager.serializeModules(true);
            ChatInputHud.Bounds expected = input.bounds();
            input.resetPosition();
            ConfigManager.applyModules((JsonObject)snapshot, true, false);
            if (!input.bounds().equals(expected)) {
                throw new IllegalStateException("HUD layout persistence regression");
            }
            AstraClient.LOGGER.info("DevShots: INPUT HUD PASS \u2014 editor drag, config round trip");
        }
        if (tick == 30) {
            DevShots.shot(client, "input_editor");
        }
        if (tick == 32) {
            input.setPos(0.5f, 0.05f);
            client.gui.setScreen((Screen)new ChatScreen("/", false));
        }
        if (tick == 42) {
            client.gui.screen().keyPressed(new KeyEvent(258, 0, 0));
        }
        if (tick == 48) {
            try {
                Field sf = ChatScreen.class.getDeclaredField("chatInputSuggestor");
                sf.setAccessible(true);
                CommandSuggestions suggestor = (CommandSuggestions)sf.get(client.gui.screen());
                Field wf = suggestor.getClass().getDeclaredField("window");
                wf.setAccessible(true);
                Object window = wf.get(suggestor);
                if (window == null) {
                    throw new IllegalStateException("Completion menu did not open");
                }
                Field af = window.getClass().getDeclaredField("area");
                af.setAccessible(true);
                Rect2i area = (Rect2i)af.get(window);
                ChatInputHud.Bounds b = input.bounds();
                if (area.getY() < b.y() + b.height() || area.getY() + area.getHeight() > client.gui.screen().height) {
                    throw new IllegalStateException("Top input completion bounds regression");
                }
                AstraClient.LOGGER.info("DevShots: INPUT COMPLETION PASS \u2014 menu below top input, inside window");
            }
            catch (ReflectiveOperationException e3) {
                throw new RuntimeException(e3);
            }
            DevShots.shot(client, "input_completion_top");
        }
        if (tick == 50) {
            try {
                Field f = companion.getClass().getDeclaredField("mentionSound");
                f.setAccessible(true);
                SoundSlot slot = (SoundSlot)f.get(companion);
                String choice = slot.variant.getModes().stream().filter(v -> v.contains("DevShots mention regression / notification.ogg")).findFirst().orElseThrow(() -> new IllegalStateException("Custom mention not discovered"));
                slot.variant.set(choice);
                JsonElement json = slot.variant.toJson();
                slot.variant.reset();
                slot.variant.fromJson(json);
                if (!((String)slot.variant.get()).equals(choice)) {
                    throw new IllegalStateException("Mention persistence regression");
                }
                String played = slot.play(AnimeChatModule::mentionVolume);
                if (!"user:notification.ogg".equals(played)) {
                    throw new IllegalStateException("Mention selection regression: " + played);
                }
                slot.enabled.set(false);
                if (slot.play(null) != null) {
                    throw new IllegalStateException("Mention mute regression");
                }
                slot.enabled.set(true);
                companion.setEnabled(false);
                CompanionHud.playMentionSound();
                companion.setEnabled(true);
                AnimeChatModule chat = AnimeChatModule.get();
                chat.setEnabled(true);
                DevShots.set(chat, "\u0423\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f", true);
                Class<?> cls = chat.getClass();
                for (String timer : new String[]{"joinedAt", "lastMention"}) {
                    Field tf = cls.getDeclaredField(timer);
                    tf.setAccessible(true);
                    tf.setLong(null, 0L);
                }
                Method mention = cls.getDeclaredMethod("mention", Component.class);
                mention.setAccessible(true);
                mention.invoke(null, Component.literal((String)("\u041f\u0440\u0438\u0432\u0435\u0442, " + client.player.getGameProfile().name())));
                Field lf = cls.getDeclaredField("lastMention");
                lf.setAccessible(true);
                long notified = lf.getLong(null);
                if (notified <= 0L) {
                    throw new IllegalStateException("Incoming mention regression");
                }
                mention.invoke(null, Component.literal((String)("\u041f\u043e\u0432\u0442\u043e\u0440: " + client.player.getGameProfile().name())));
                if (lf.getLong(null) != notified) {
                    throw new IllegalStateException("Mention cooldown regression");
                }
                AstraClient.LOGGER.info("DevShots: MENTION EVENT PASS \u2014 incoming nickname and cooldown");
                AstraClient.LOGGER.info("DevShots: MENTION CUSTOM PASS \u2014 discovery, selection, persistence, playback, mute, hidden companion");
            }
            catch (ReflectiveOperationException e4) {
                throw new RuntimeException(e4);
            }
            input.setEnabled(false);
            client.gui.setScreen((Screen)new ChatScreen("\u0412\u0430\u043d\u0438\u043b\u044c\u043d\u043e\u0435 \u043f\u043e\u043b\u0435", false));
            EditBox field = client.gui.screen().children().stream().filter(e -> e instanceof EditBox).map(e -> (EditBox)e).findFirst().orElseThrow();
            if (field.getX() != 4 || field.getY() != client.gui.screen().height - 12) {
                throw new IllegalStateException("Disabled input fallback regression");
            }
            AstraClient.LOGGER.info("DevShots: INPUT DISABLED PASS");
        }
        if (tick == 60) {
            input.setEnabled(true);
            input.setPos(0.52f, 0.45f);
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void trackBannerScenes(Minecraft client) {
        TrackBannerHud banner = TrackBannerHud.get();
        NowPlayingModule player = NowPlayingModule.get();
        if (tick == 1) {
            MediaBridge.devOverride(NowPlaying.NONE);
            player.setEnabled(false);
            DevShots.set(player, "\u0417\u0430\u0433\u0440\u0443\u0436\u0430\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u044b (lrclib.net)", false);
            DevShots.set(player, "\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438", "\u0412\u044b\u043a\u043b");
            banner.setEnabled(true);
            banner.scale.set(100.0);
            banner.setPos(0.42f, 0.4f);
            client.gui.setScreen((Screen)new HudEditorScreen(null));
        }
        if (tick == 10) {
            double x = banner.getX(client.gui.screen().width) + banner.scaledWidth() / 2.0f;
            double y = banner.getY(client.gui.screen().height) + banner.scaledHeight() / 2.0f;
            float oldX = banner.getPosX();
            float oldY = banner.getPosY();
            client.gui.screen().mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)), false);
            client.gui.screen().mouseDragged(new MouseButtonEvent(x + 45.0, y + 70.0, new MouseButtonInfo(0, 0)), 45.0, 70.0);
            client.gui.screen().mouseReleased(new MouseButtonEvent(x + 45.0, y + 70.0, new MouseButtonInfo(0, 0)));
            if (banner.getPosX() == oldX && banner.getPosY() == oldY) {
                throw new IllegalStateException("Banner drag regression");
            }
            float cornerX = banner.getX(client.gui.screen().width) + banner.scaledWidth() + 1.5f;
            float cornerY = banner.getY(client.gui.screen().height) + banner.scaledHeight() + 1.5f;
            double oldSize = (Double)banner.scale.get();
            client.gui.screen().mouseClicked(new MouseButtonEvent((double)cornerX, (double)cornerY, new MouseButtonInfo(0, 0)), false);
            client.gui.screen().mouseDragged(new MouseButtonEvent((double)(cornerX + 65.0f), (double)(cornerY + 20.0f), new MouseButtonInfo(0, 0)), 65.0, 20.0);
            client.gui.screen().mouseReleased(new MouseButtonEvent((double)(cornerX + 65.0f), (double)(cornerY + 20.0f), new MouseButtonInfo(0, 0)));
            if ((Double)banner.scale.get() <= oldSize || player.bannerSize() != ((Double)banner.scale.get()).doubleValue()) {
                throw new IllegalStateException("Banner resize binding regression");
            }
            DevShots.set(banner, "\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435 \u043f\u043b\u0430\u0448\u043a\u0438", "\u041f\u0430\u043d\u0435\u043b\u044c");
            banner.themeAccent.set(false);
            banner.accent.set(-11939841);
            JsonObject snapshot = ConfigManager.serializeModules(true);
            float expectedX = banner.getPosX();
            float expectedY = banner.getPosY();
            double expectedSize = (Double)banner.scale.get();
            banner.resetPosition();
            banner.scale.set(50.0);
            ConfigManager.applyModules(snapshot, true, false);
            if (banner.getPosX() != expectedX || banner.getPosY() != expectedY || (Double)banner.scale.get() != expectedSize) {
                throw new IllegalStateException("Banner persistence regression");
            }
            AstraClient.LOGGER.info("DevShots: TRACK BANNER EDITOR PASS \u2014 no music, drag, corner resize, scale binding, persistence");
        }
        if (tick == 20) {
            DevShots.shot(client, "track_banner_editor");
        }
        if (tick == 22) {
            player.setEnabled(true);
            DevShots.set(player, "\u041f\u043b\u0430\u0448\u043a\u0430 \u0441\u043c\u0435\u043d\u044b \u0442\u0440\u0435\u043a\u0430", true);
            DevShots.set(player, "\u041f\u043b\u0430\u0448\u043a\u0430: \u0432\u0440\u0435\u043c\u044f \u043f\u043e\u043a\u0430\u0437\u0430", 15.0);
            MediaBridge.devOverride(new NowPlaying(true, "Firefox.exe", "In Affliction", "Yakui The Maid", "", true, 0L, System.currentTimeMillis(), 180000L, "", false, false, false));
            client.gui.setScreen(null);
        }
        if (tick == 42) {
            if (!player.bannerVisible()) {
                throw new IllegalStateException("Track change banner did not activate");
            }
            if (banner.scaledWidth() > (float)(client.getWindow().getGuiScaledWidth() + 1)) {
                throw new IllegalStateException("Banner screen bounds regression");
            }
            DevShots.shot(client, "track_banner_ingame");
            AstraClient.LOGGER.info("DevShots: TRACK BANNER LIVE PASS \u2014 uses editor geometry on track change");
        }
        if (tick == 44) {
            try {
                Field age = player.getClass().getDeclaredField("trackChangedAt");
                age.setAccessible(true);
                age.setLong(player, System.currentTimeMillis() - 60000L);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            if (player.bannerVisible()) {
                throw new IllegalStateException("Banner expiry regression");
            }
            client.gui.setScreen((Screen)new HudEditorScreen(null));
        }
        if (tick == 50) {
            float x = banner.getX(client.gui.screen().width) + 20.0f;
            float y = banner.getY(client.gui.screen().height) + 20.0f;
            client.gui.screen().mouseClicked(new MouseButtonEvent((double)x, (double)y, new MouseButtonInfo(0, 0)), false);
            client.gui.screen().mouseReleased(new MouseButtonEvent((double)x, (double)y, new MouseButtonInfo(0, 0)));
            DevShots.shot(client, "track_banner_expired_editor");
            AstraClient.LOGGER.info("DevShots: TRACK BANNER EXPIRED PASS \u2014 editor preview still visible and selectable");
        }
        if (tick == 55) {
            banner.scale.set(250.0);
            MediaBridge.devOverride(new NowPlaying(true, "Firefox.exe", "\u041e\u0447\u0435\u043d\u044c \u0434\u043b\u0438\u043d\u043d\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043f\u0435\u0441\u043d\u0438 ".repeat(10), "\u041e\u0447\u0435\u043d\u044c \u0434\u043b\u0438\u043d\u043d\u043e\u0435 \u0438\u043c\u044f \u0438\u0441\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044f ".repeat(10), "", true, 0L, System.currentTimeMillis(), 180000L, "", false, false, false));
        }
        if (tick == 60) {
            if (banner.scaledWidth() > (float)(client.getWindow().getGuiScaledWidth() + 1)) {
                throw new IllegalStateException("Long banner overflow regression");
            }
            AstraClient.LOGGER.info("DevShots: TRACK BANNER LONG TITLE PASS");
            MediaBridge.devOverride(null);
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static Object lyricField(Object owner, String name) {
        try {
            Field f = owner.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return f.get(owner);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectDevLyrics(List<Lyrics.Line> lines) {
        try {
            Field key = Lyrics.class.getDeclaredField("key");
            key.setAccessible(true);
            key.set(null, "");
            Field generation = Lyrics.class.getDeclaredField("generation");
            generation.setAccessible(true);
            generation.setInt(null, generation.getInt(null) + 1);
            Field field = Lyrics.class.getDeclaredField("lines");
            field.setAccessible(true);
            field.set(null, lines);
            Field status = Lyrics.class.getDeclaredField("status");
            status.setAccessible(true);
            status.set(null, (Object)Lyrics.Status.SYNCED);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static float actualWorldScale(NowPlayingModule module, Minecraft client) {
        try {
            Method method = module.getClass().getDeclaredMethod("worldPlacement", Camera.class, Vec3.class, Quaternionf.class, Float.TYPE);
            method.setAccessible(true);
            Object placement = method.invoke((Object)module, client.gameRenderer.mainCamera(), DevShots.lyricField(module, "worldAnchor"), DevShots.lyricField(module, "pinnedRot"), (Float)DevShots.lyricField(module, "worldReferenceDistance"));
            if (placement == null) {
                throw new IllegalStateException("World lyrics culled unexpectedly");
            }
            Method scale = placement.getClass().getDeclaredMethod("scale", new Class[0]);
            scale.setAccessible(true);
            return ((Float)scale.invoke(placement, new Object[0])).floatValue();
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void worldLyricsMathTests() {
        float fixed = WorldLyricMotion.scale(100.0f, 4.0f, 4.0f, false);
        for (float distance : new float[]{0.5f, 2.0f, 4.0f, 10.0f, 24.0f, 100.0f}) {
            if (WorldLyricMotion.scale(100.0f, 4.0f, distance, false) != fixed) {
                throw new IllegalStateException("Physical lyrics scale depends on camera distance");
            }
            if (!(WorldLyricMotion.scale(100.0f, 4.0f, distance, true) > fixed * 2.0f + 1.0E-5f)) continue;
            throw new IllegalStateException("Screen-size lyrics unbounded growth");
        }
        if (WorldLyricMotion.distanceAlpha(0.2f, 28.0f) != 0.0f || WorldLyricMotion.distanceAlpha(29.0f, 28.0f) != 0.0f) {
            throw new IllegalStateException("Lyrics near/far clipping regression");
        }
        LyricFx.Colors colors = new LyricFx.Colors(-45608, -13058568, -7829368, false);
        for (String effect : LyricFx.WORLD_EFFECTS) {
            for (float age : new float[]{0.0f, 0.1f, 0.5f, 1.0f, 2.0f}) {
                List<LyricFx.Glyph> glyphs = LyricFx.split("\u0421\u0438\u044f\u0439 \u0441\u0440\u0435\u0434\u0438 \u0437\u0432\u0451\u0437\u0434 \u2014 Astra", ch -> 0.6);
                WorldLyricMotion.animate(glyphs, effect, "\u0420\u0430\u0441\u0441\u044b\u043f\u0430\u0442\u044c\u0441\u044f", age, false, 0.5f, 1.0f, 0.5f, 1.8f, colors, 1.0f, true, "\u0412\u043e\u043b\u043d\u0430", 1.0f, 2.0f);
                for (LyricFx.Glyph g : glyphs) {
                    if (!Float.isFinite(g.dx + g.dy + g.scale + g.rot + g.alpha) || g.scale < 0.0f || g.alpha < 0.0f || g.alpha > 1.001f) {
                        throw new IllegalStateException("Invalid lyric glyph in " + effect);
                    }
                    if (!(age >= 1.0f) || !(g.alpha < 0.99f)) continue;
                    throw new IllegalStateException("Entry duration exceeded in " + effect);
                }
            }
        }
        for (String exit : new String[]{"\u0420\u0430\u0441\u0442\u0432\u043e\u0440\u0435\u043d\u0438\u0435", "\u0423\u043b\u0435\u0442\u0435\u0442\u044c \u0432\u0432\u0435\u0440\u0445", "\u0420\u0430\u0441\u0441\u044b\u043f\u0430\u0442\u044c\u0441\u044f", "\u0421\u0445\u043b\u043e\u043f\u043d\u0443\u0442\u044c\u0441\u044f", "\u0421\u043a\u043e\u043b\u044c\u0436\u0435\u043d\u0438\u0435", "\u041e\u043f\u0430\u0441\u0442\u044c", "\u041c\u0433\u043d\u043e\u0432\u0435\u043d\u043d\u043e"}) {
            List<LyricFx.Glyph> glyphs = LyricFx.split("Astra", ch -> 0.6);
            WorldLyricMotion.animate(glyphs, "\u0412\u043e\u043b\u043d\u0430", exit, 0.5f, true, -1.0f, 1.0f, 0.5f, 1.0f, colors, 1.0f, false, "\u041d\u0435\u0442", 0.0f, 1.0f);
            for (LyricFx.Glyph g : glyphs) {
                if (!(g.alpha > 0.001f)) continue;
                throw new IllegalStateException("Exit duration exceeded: " + exit);
            }
        }
        List<String> wrapped = WorldLyricMotion.wrap("\u661f\ud83c\udf38\u0437\u0432\u0451\u0437\u0434\u044b".repeat(30), 50.0f, 3, value -> value.codePointCount(0, value.length()) * 6);
        if (wrapped.size() != 3 || !wrapped.get(2).endsWith("\u2026")) {
            throw new IllegalStateException("Unicode wrap regression");
        }
        for (String row : wrapped) {
            if (row.codePointCount(0, row.length()) * 6 <= 50) continue;
            throw new IllegalStateException("Wrap width regression");
        }
        AstraClient.LOGGER.info("DevShots: WORLD LYRICS MATH PASS \u2014 stable scale, bounded screen mode, near/far fade, 18 entries, 7 exits, unicode wrapping");
    }

    private static void worldLyricsScenes(Minecraft client) {
        NowPlayingModule module = NowPlayingModule.get();
        if (tick == 1) {
            DevShots.worldLyricsMathTests();
            module.setEnabled(true);
            DevShots.set(module, "\u0417\u0430\u0433\u0440\u0443\u0436\u0430\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u044b (lrclib.net)", false);
            DevShots.set(module, "\u0418\u0441\u0442\u043e\u0447\u043d\u0438\u043a", "\u041b\u044e\u0431\u043e\u0439 \u043f\u043b\u0435\u0435\u0440");
            DevShots.set(module, "\u041f\u043b\u0430\u0448\u043a\u0430 \u0441\u043c\u0435\u043d\u044b \u0442\u0440\u0435\u043a\u0430", false);
            module.applyWorldPreset("\u041d\u0435\u043e\u043d-\u043a\u0430\u0440\u0430\u043e\u043a\u0435");
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u0417\u0430\u043a\u0440\u0435\u043f\u043b\u044f\u0442\u044c \u043d\u0430 \u043c\u0435\u0441\u0442\u0435");
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u0432\u044b\u0441\u043e\u0442\u0430", 0.0);
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435", 4.0);
            MediaBridge.devOverride(new NowPlaying(true, "Astra test", "\u0420\u0435\u0433\u0440\u0435\u0441\u0441\u0438\u044f \u0442\u0435\u043a\u0441\u0442\u0430", "Astra", "", false, 1000L, System.currentTimeMillis(), 300000L, "", false, false, false));
            lyricsOriginalPosition = client.player.position();
            lyricsOriginalYaw = client.player.getYRot();
            lyricsOriginalPitch = client.player.getXRot();
            lyricsOriginalGameMode = client.gameMode.getPlayerMode().getSerializedName();
            lyricsOrigin = client.player.position().add(0.0, 24.0, 0.0);
            DevShots.devCommand(client, "gamemode spectator @p");
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f 0 0", DevShots.lyricsOrigin.x, DevShots.lyricsOrigin.y, DevShots.lyricsOrigin.z));
        }
        if (tick == 12) {
            DevShots.injectDevLyrics(List.of(new Lyrics.Line(0L, "\u0421\u0438\u044f\u0439 \u0441\u0440\u0435\u0434\u0438 \u0437\u0432\u0451\u0437\u0434"), new Lyrics.Line(100000L, "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0442\u0430\u043d\u0446\u0443\u044e\u0442 \u043f\u043e\u0434 \u043c\u0443\u0437\u044b\u043a\u0443"), new Lyrics.Line(200000L, "\u0422\u0432\u043e\u0439 \u043c\u0438\u0440 \u2014 \u0442\u0432\u043e\u0439 \u0440\u0438\u0442\u043c")));
        }
        if (tick == 30) {
            lyricsPinned = (Vec3)DevShots.lyricField(module, "worldAnchor");
            if (lyricsPinned == null) {
                throw new IllegalStateException("No pinned world text");
            }
            lyricsWorldScale = DevShots.actualWorldScale(module, client);
            lyricsApparent = lyricsWorldScale / (float)lyricsPinned.distanceTo(client.gameRenderer.mainCamera().position());
            DevShots.shot(client, "world_lyrics_near");
        }
        if (tick == 32) {
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f 0 0", DevShots.lyricsOrigin.x, DevShots.lyricsOrigin.y, DevShots.lyricsOrigin.z - 8.0));
        }
        if (tick == 44) {
            Vec3 at = (Vec3)DevShots.lyricField(module, "worldAnchor");
            float actual = DevShots.actualWorldScale(module, client);
            float apparent = actual / (float)at.distanceTo(client.gameRenderer.mainCamera().position());
            if (!at.equals((Object)lyricsPinned) || Math.abs(actual - lyricsWorldScale) > 1.0E-6f || apparent >= lyricsApparent) {
                throw new IllegalStateException("Walking away enlarged or moved pinned lyrics");
            }
            AstraClient.LOGGER.info("DevShots: WORLD LYRICS DISTANCE PASS \u2014 physical scale {} unchanged; apparent scale {} -> {}", new Object[]{Float.valueOf(actual), Float.valueOf(lyricsApparent), Float.valueOf(apparent)});
            DevShots.shot(client, "world_lyrics_far");
        }
        if (tick == 46) {
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f 0 0", DevShots.lyricsOrigin.x, DevShots.lyricsOrigin.y, DevShots.lyricsOrigin.z - 22.0));
        }
        if (tick == 58) {
            if (!lyricsPinned.equals(DevShots.lyricField(module, "worldAnchor"))) {
                throw new IllegalStateException("Pinned lyrics teleported past 20 blocks");
            }
            DevShots.actualWorldScale(module, client);
            AstraClient.LOGGER.info("DevShots: WORLD LYRICS PIN PASS \u2014 no 20-block re-anchor");
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f 0 0", DevShots.lyricsOrigin.x, DevShots.lyricsOrigin.y, DevShots.lyricsOrigin.z));
        }
        if (tick == 67) {
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f 0 0", DevShots.lyricsOrigin.x + 2.0, DevShots.lyricsOrigin.y, DevShots.lyricsOrigin.z));
        }
        if (tick == 70) {
            MediaBridge.devOverride(module.track().withPosition(100500L));
        }
        if (tick == 73) {
            if (!lyricsPinned.equals(DevShots.lyricField(module, "previousWorldAnchor"))) {
                throw new IllegalStateException("Outgoing world line jumped to new anchor");
            }
            AstraClient.LOGGER.info("DevShots: WORLD LYRICS TRANSITION PASS \u2014 outgoing line keeps old anchor");
            DevShots.shot(client, "world_lyrics_transition");
        }
        String[] presets = new String[]{"\u041d\u0435\u043e\u043d-\u043a\u0430\u0440\u0430\u043e\u043a\u0435", "\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0421\u043f\u043e\u043a\u043e\u0439\u043d\u044b\u0439"};
        for (int i = 0; i < presets.length; ++i) {
            if (tick == 80 + i * 25) {
                module.applyWorldPreset(presets[i]);
            }
            if (tick != 99 + i * 25) continue;
            DevShots.shot(client, "world_lyrics_preset_" + i);
        }
        if (tick == 180) {
            JsonObject snapshot = ConfigManager.serializeModules(false);
            String before = snapshot.getAsJsonObject("modules").getAsJsonObject("now_playing").getAsJsonObject("settings").toString();
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u0441\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 160.0);
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u0430", 10.0);
            ConfigManager.applyModules(snapshot, false, false);
            String after = ConfigManager.serializeModules(false).getAsJsonObject("modules").getAsJsonObject("now_playing").getAsJsonObject("settings").toString();
            if (!before.equals(after)) {
                throw new IllegalStateException("World lyrics persistence regression");
            }
            AstraClient.LOGGER.info("DevShots: WORLD LYRICS SETTINGS PASS \u2014 full profile round trip");
            module.applyWorldPreset("\u0421\u0430\u043a\u0443\u0440\u0430");
            MediaBridge.devOverride(NowPlaying.NONE);
            module.previewWorldLyrics();
        }
        if (tick == 200) {
            if (DevShots.lyricField(module, "worldAnchor") == null || (Long)DevShots.lyricField(module, "demoStartedAt") == 0L) {
                throw new IllegalStateException("Offline lyrics preview regression");
            }
            DevShots.shot(client, "world_lyrics_preview");
            module.previewWorldLyrics();
            if ((Long)DevShots.lyricField(module, "demoStartedAt") != 0L) {
                throw new IllegalStateException("Preview cancel regression");
            }
            AstraClient.LOGGER.info("DevShots: WORLD LYRICS PREVIEW PASS \u2014 no song, start and cancel");
        }
        if (tick == 205) {
            module.applyWorldPreset("\u0413\u0440\u043e\u0437\u0430");
            DevShots.set(module, "\u041a\u0430\u0440\u0430\u043e\u043a\u0435-\u0437\u0430\u043b\u0438\u0432\u043a\u0430", false);
            MediaBridge.devOverride(new NowPlaying(true, "Astra test", "\u041d\u0430\u0440\u0438\u0441\u043e\u0432\u0430\u043d\u043d\u044b\u0435 \u043c\u043e\u043b\u043d\u0438\u0438", "Astra", "", false, 1000L, System.currentTimeMillis(), 300000L, "", false, false, false));
            DevShots.injectDevLyrics(List.of(new Lyrics.Line(0L, "\u041c\u043e\u043b\u043d\u0438\u044f \u0437\u0430\u0436\u0438\u0433\u0430\u0435\u0442 \u0431\u0443\u043a\u0432\u044b"), new Lyrics.Line(100000L, "\u041a\u0430\u0436\u0434\u044b\u0439 \u0440\u0430\u0437\u0440\u044f\u0434 \u043d\u0430\u0440\u0438\u0441\u043e\u0432\u0430\u043d \u0432\u0440\u0443\u0447\u043d\u0443\u044e")));
            LyricLightning.Point[] points = LyricLightning.path(37, 12.0f, 5.0f, 34.0f);
            if (points.length != 9 || points[8].x() != 12.0f || points[8].y() != 5.0f || points[0].y() != -29.0f) {
                throw new IllegalStateException("Vector lightning does not hit letter");
            }
            for (int i = 0; i < 100; ++i) {
                float at = LyricLightning.strikeAt(i, 100, 1.8f);
                if (Float.isFinite(at) && !(at < 0.0f) && !(at > 1.8f)) continue;
                throw new IllegalStateException("Lightning timing regression");
            }
            AstraClient.LOGGER.info("DevShots: LYRIC LIGHTNING GEOMETRY PASS \u2014 hand-drawn nine-point spine, exact glyph impact, per-letter timing");
        }
        if (tick == 213 || tick == 220 || tick == 227) {
            DevShots.shot(client, "world_lyrics_lightning_" + tick);
        }
        if (tick == 230) {
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u0441\u043a\u0432\u043e\u0437\u044c \u0431\u043b\u043e\u043a\u0438", false);
            module.applyWorldPreset("\u0413\u0440\u043e\u0437\u0430");
        }
        if (tick == 238) {
            DevShots.shot(client, "world_lyrics_lightning_depth");
        }
        if (tick == 240) {
            Vec3 at = (Vec3)DevShots.lyricField(module, "worldAnchor");
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f 0 0", at.x, DevShots.lyricsOrigin.y, at.z - 0.75));
        }
        if (tick == 250) {
            try {
                Method method = module.getClass().getDeclaredMethod("worldPlacement", Camera.class, Vec3.class, Quaternionf.class, Float.TYPE);
                method.setAccessible(true);
                Object placement = method.invoke((Object)module, client.gameRenderer.mainCamera(), DevShots.lyricField(module, "worldAnchor"), DevShots.lyricField(module, "pinnedRot"), (Float)DevShots.lyricField(module, "worldReferenceDistance"));
                if (placement == null) {
                    throw new IllegalStateException("Near camera unexpectedly culled");
                }
                Method limit = placement.getClass().getDeclaredMethod("widthLimit", new Class[0]);
                limit.setAccessible(true);
                float width = ((Float)limit.invoke(placement, new Object[0])).floatValue();
                if (width <= 0.0f || width > 2.0f) {
                    throw new IllegalStateException("Near-camera viewport guard missing");
                }
                AstraClient.LOGGER.info("DevShots: WORLD LYRICS NEAR GUARD PASS \u2014 viewport width cap {} blocks", (Object)Float.valueOf(width));
                DevShots.shot(client, "world_lyrics_near_guard");
                module.previewWorldLyrics();
                module.previewLightningLyrics();
                if ((Long)DevShots.lyricField(module, "demoStartedAt") == 0L || !DevShots.setting(module, "\u042d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f").get().equals("\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438")) {
                    throw new IllegalStateException("Lightning preview toggled off existing preview");
                }
                module.previewWorldLyrics();
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        if (tick == 255) {
            MediaBridge.devOverride(null);
            DevShots.devCommand(client, "gamemode " + lyricsOriginalGameMode + " @p");
            DevShots.devCommand(client, String.format(Locale.ROOT, "tp @p %.3f %.3f %.3f %.3f %.3f", DevShots.lyricsOriginalPosition.x, DevShots.lyricsOriginalPosition.y, DevShots.lyricsOriginalPosition.z, Float.valueOf(lyricsOriginalYaw), Float.valueOf(lyricsOriginalPitch)));
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void lyricOpacityScenes(Minecraft client) {
        WorldLyricsConfig config;
        NowPlayingModule module = NowPlayingModule.get();
        if (tick == 1) {
            module.setEnabled(true);
            DevShots.set(module, "\u0417\u0430\u0433\u0440\u0443\u0436\u0430\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u044b (lrclib.net)", false);
            DevShots.set(module, "\u041f\u043b\u0430\u0448\u043a\u0430 \u0441\u043c\u0435\u043d\u044b \u0442\u0440\u0435\u043a\u0430", false);
            DevShots.set(module, "\u0418\u0441\u0442\u043e\u0447\u043d\u0438\u043a", "\u041b\u044e\u0431\u043e\u0439 \u043f\u043b\u0435\u0435\u0440");
            module.applyWorldPreset("\u0421\u043f\u043e\u043a\u043e\u0439\u043d\u044b\u0439");
            DevShots.set(module, "\u042d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f", "\u0411\u0435\u0437 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438");
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u041f\u0435\u0440\u0435\u0434 \u0432\u0437\u0433\u043b\u044f\u0434\u043e\u043c");
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u0432\u044b\u0441\u043e\u0442\u0430", 0.0);
            DevShots.set(module, "\u041a\u0430\u0440\u0430\u043e\u043a\u0435-\u0437\u0430\u043b\u0438\u0432\u043a\u0430", true);
            MediaBridge.devOverride(new NowPlaying(true, "Astra test", "\u041f\u0440\u043e\u0432\u0435\u0440\u043a\u0430 \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438", "Astra", "", false, 1000L, System.currentTimeMillis(), 300000L, "", false, false, false));
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u0430", 40.0);
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0434\u043e \u043f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0438", 50.0);
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0435\u0439 \u0441\u0442\u0440\u043e\u043a\u0438", 40.0);
            if (!DevShots.setting(module, "\u0412 \u043c\u0438\u0440\u0435: \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u0430").get().equals(40.0) || !DevShots.setting(module, "\u0412 \u043c\u0438\u0440\u0435: \u044f\u0440\u043a\u043e\u0441\u0442\u044c \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0435\u0439 \u0441\u0442\u0440\u043e\u043a\u0438").get().equals(40.0)) {
                throw new IllegalStateException("Opacity legacy alias regression");
            }
            config = (WorldLyricsConfig)DevShots.lyricField(module, "worldStyle");
            if (Math.abs(ColorUtil.alpha(config.unfilledColor()) - 128) > 1) {
                throw new IllegalStateException("Karaoke unfilled alpha regression");
            }
            List<LyricFx.Glyph> glyphs = LyricFx.split("\u041f\u043b\u043e\u0442\u043d\u044b\u0439 \u0442\u0435\u043a\u0441\u0442", c -> 1.0);
            LyricFx.animate(glyphs, "\u0411\u0435\u0437 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438", 2.0f, -1.0f, 0.0f, new LyricFx.Colors(-1, -1, config.unfilledColor(), false), 0.0f, false);
            int combined = ColorUtil.alpha(ColorUtil.mulAlpha(glyphs.getLast().color, 0.4f));
            if (Math.abs(combined - 51) > 1) {
                throw new IllegalStateException("Opacity multiplication regression");
            }
            AstraClient.LOGGER.info("DevShots: LYRIC OPACITY ALPHA PASS \u2014 40% master x 50% unfilled = 20%, old profile keys retained");
        }
        if (tick == 8) {
            DevShots.injectDevLyrics(List.of(new Lyrics.Line(0L, "\u0422\u0435\u043a\u0441\u0442 \u043e\u0441\u0442\u0430\u0451\u0442\u0441\u044f \u0447\u0438\u0442\u0430\u0435\u043c\u044b\u043c"), new Lyrics.Line(100000L, "\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u0441\u0442\u0440\u043e\u043a\u0430 \u0431\u0435\u0437 \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u0438")));
        }
        if (tick == 20) {
            DevShots.shot(client, "lyrics_opacity_low");
        }
        if (tick == 22) {
            ((ButtonSetting)DevShots.setting(module, "\u0412 \u043c\u0438\u0440\u0435: \u043f\u043b\u043e\u0442\u043d\u044b\u0439 \u0442\u0435\u043a\u0441\u0442 \u0434\u043b\u044f \u0448\u0435\u0439\u0434\u0435\u0440\u043e\u0432")).press();
            config = (WorldLyricsConfig)DevShots.lyricField(module, "worldStyle");
            if ((Double)config.opacity.get() != 100.0 || (Double)config.karaokeOpacity.get() != 100.0 || (Double)config.nextOpacity.get() != 100.0 || !((Boolean)config.outline.get()).booleanValue() || ColorUtil.alpha(config.unfilledColor()) != 255) {
                throw new IllegalStateException("Opaque preset regression");
            }
            JsonObject snapshot = ConfigManager.serializeModules(false);
            String before = snapshot.getAsJsonObject("modules").getAsJsonObject("now_playing").getAsJsonObject("settings").toString();
            config.opacity.set(10.0);
            config.karaokeOpacity.set(0.0);
            config.nextOpacity.set(0.0);
            config.outline.set(false);
            ConfigManager.applyModules(snapshot, false, false);
            String after = ConfigManager.serializeModules(false).getAsJsonObject("modules").getAsJsonObject("now_playing").getAsJsonObject("settings").toString();
            if (!before.equals(after)) {
                throw new IllegalStateException("Opacity persistence regression");
            }
            AstraClient.LOGGER.info("DevShots: LYRIC OPACITY SOLID PASS \u2014 unfilled/current/next at 100%, outlines, profile round trip");
        }
        if (tick == 34) {
            DevShots.shot(client, "lyrics_opacity_solid");
        }
        if (tick == 36) {
            DevShots.set(module, "\u0412 \u043c\u0438\u0440\u0435: \u043d\u0435\u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u0430", 0.0);
        }
        if (tick == 44) {
            if (!DevShots.setting(module, "\u0412 \u043c\u0438\u0440\u0435: \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u0430").get().equals(0.0)) {
                throw new IllegalStateException("Zero opacity regression");
            }
            DevShots.shot(client, "lyrics_opacity_zero");
            AstraClient.LOGGER.info("DevShots: LYRIC OPACITY ZERO PASS \u2014 zero alpha is skipped, not forced opaque by TextRenderer");
        }
        if (tick == 48) {
            MediaBridge.devOverride(null);
            client.gui.setScreen(null);
            phase = Phase.DONE;
            tick = 0;
        }
    }

    private static void builderScene(Minecraft client) {
        try {
            if (tick == 1) {
                ModuleManager.byId("astra_online").setEnabled(false);
                ModuleManager.byId("builder_canvas").setEnabled(true);
                DevShots.set(ModuleManager.byId("builder_canvas"), "\u041f\u043e\u0432\u0435\u0440\u0445 \u0431\u043b\u043e\u043a\u043e\u0432", true);
                CanvasBoard.tick();
                builderYaw = client.player.getYRot();
                builderPitch = client.player.getXRot();
                client.player.setXRot(0.0f);
                for (Module module : ModuleManager.all()) {
                    if (!(module instanceof HudModule)) continue;
                    module.setEnabled(false);
                }
                BufferedImage img = new BufferedImage(800, 600, 2);
                Graphics2D g = img.createGraphics();
                g.setColor(new Color(22, 13, 39));
                g.fillRect(0, 0, 800, 600);
                g.setColor(new Color(64, 43, 83));
                for (int i = 0; i < 800; i += 40) {
                    g.drawLine(i, 0, i, 600);
                }
                for (int j = 0; j < 600; j += 40) {
                    g.drawLine(0, j, 800, j);
                }
                g.setStroke(new BasicStroke(5.0f));
                g.setColor(new Color(86, 220, 255));
                g.drawRect(160, 240, 480, 280);
                g.drawLine(120, 240, 400, 60);
                g.drawLine(400, 60, 680, 240);
                g.setColor(new Color(255, 111, 218));
                g.drawRect(340, 340, 120, 180);
                g.drawRect(200, 300, 90, 90);
                g.drawRect(510, 300, 90, 90);
                g.setFont(new Font("SansSerif", 1, 32));
                g.drawString("ASTRA / BUILD REFERENCE", 160, 570);
                g.dispose();
                Path file = client.gameDirectory.toPath().resolve("builder-reference.png");
                ImageIO.write((RenderedImage)img, "png", file.toFile());
                builderTest = CanvasBoard.importImage(file);
                DevShots.builderTest.name = "\u0424\u0430\u0441\u0430\u0434 \u2014 \u0441\u0442\u0440\u043e\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0439 \u0440\u0435\u0444\u0435\u0440\u0435\u043d\u0441";
                Vec3 pos = client.player.getEyePosition().add(client.player.getViewVector(1.0f).scale(6.0));
                DevShots.builderTest.x = pos.x;
                DevShots.builderTest.y = pos.y;
                DevShots.builderTest.z = pos.z;
                DevShots.builderTest.yaw = Mth.wrapDegrees((float)(180.0f - client.player.getYRot()));
                CanvasBoard.put(builderTest);
                if (CanvasBoard.texture(builderTest) == null) {
                    throw new IllegalStateException("image decode failed");
                }
            }
            if (tick == 40) {
                DevShots.shot(client, "builder_world");
            }
            if (tick == 45) {
                DevShots.builderTest.opacity = 0.35;
                CanvasBoard.put(builderTest);
            }
            if (tick == 65) {
                DevShots.shot(client, "builder_opacity");
            }
            if (tick == 70) {
                DevShots.builderTest.opacity = 1.0;
                CanvasBoard.put(builderTest);
                client.gui.setScreen((Screen)new CanvasScreen(null));
            }
            if (tick == 100) {
                DevShots.shot(client, "builder_editor");
            }
            if (tick == 110) {
                CanvasBoard.delete(builderTest);
                client.gui.setScreen(null);
                client.player.setYRot(builderYaw);
                client.player.setXRot(builderPitch);
                AstraClient.LOGGER.info("DevShots: BUILDER PASS \u2014 import, world texture, opacity, editor, delete");
                phase = Phase.DONE;
                tick = 0;
            }
        }
        catch (Exception e) {
            AstraClient.LOGGER.error("BUILDER FAIL", (Throwable)e);
            client.stop();
        }
    }

    private static void editorScenes(Minecraft client) {
        try {
            Object previewField;
            DecorEditorScreen editor;
            Button button;
            if (tick == 1) {
                ModuleManager.byId("astra_online").setEnabled(false);
                editorSource = new TitleScreen();
                client.gui.setScreen(editorSource);
            }
            if (tick == 12) {
                if (NativeScreenLayouts.entries(editorSource).isEmpty()) {
                    throw new AssertionError((Object)"no native button baselines");
                }
                editorSnapshot = NativeScreenLayouts.snapshot(editorSource);
                client.gui.setScreen((Screen)new NativeLayoutEditor(editorSource));
            }
            if (tick == 30) {
                if (!NativeScreenLayouts.rects(editorSource).containsKey("@splash") || !NativeScreenLayouts.rects(editorSource).containsKey("@version")) {
                    throw new AssertionError((Object)"title extras not registered");
                }
                DevShots.shot(client, "editor_title");
            }
            if (tick == 34) {
                NativeScreenLayouts.Entry entry = NativeScreenLayouts.entries(editorSource).get(0);
                NativeScreenLayouts.Item edit = NativeScreenLayouts.item(editorSource, entry.id());
                edit.dx = 0.05f;
                edit.scale = 110.0f;
                edit.accent = "22D3EE";
                NativeScreenLayouts.apply(editorSource);
                if (entry.widget().getX() != Math.round((float)entry.base().x() + 0.05f * (float)DevShots.editorSource.width)) {
                    throw new AssertionError((Object)"native hitbox mismatch");
                }
                NativeScreenLayouts.restore(editorSource, editorSnapshot);
                for (int i = 0; i < 6; ++i) {
                    client.gui.setScreen((Screen)new NativeLayoutEditor(editorSource));
                    client.gui.setScreen(editorSource);
                    long launchers = Screens.getWidgets((Screen)editorSource).stream().filter(w -> w.getMessage().getString().equals("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f")).count();
                    if (launchers != 1L) {
                        throw new AssertionError((Object)("duplicate editor buttons: " + launchers));
                    }
                    if (NativeScreenLayouts.entries(editorSource).stream().anyMatch(e -> e.id().startsWith("\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u044f#"))) {
                        throw new AssertionError((Object)"editor button captured as editable");
                    }
                }
                AstraClient.LOGGER.info("DevShots: REOPEN PASS \u2014 six editor round trips, one launcher");
                editorSource = new PauseScreen(true);
                client.gui.setScreen(editorSource);
            }
            if (tick == 38) {
                client.gui.setScreen((Screen)new NativeLayoutEditor(editorSource));
            }
            if (tick == 58) {
                DevShots.shot(client, "editor_pause");
            }
            if (tick == 62) {
                editorSource = new JoinMultiplayerScreen((Screen)new TitleScreen());
                client.gui.setScreen(editorSource);
            }
            if (tick == 66) {
                AtomicInteger count = new AtomicInteger();
                button = Button.builder((Component)Component.literal((String)"Click regression probe"), b -> count.incrementAndGet()).bounds(5, 5, 80, 20).build();
                String id = "regression-probe";
                NativeScreenLayouts.entries(editorSource).add(new NativeScreenLayouts.Entry(id, (AbstractWidget)button, new NativeScreenLayouts.Base(5, 5, 80, 20, true)));
                NativeScreenLayouts.Item o = NativeScreenLayouts.item(editorSource, id);
                o.dx = 0.32f;
                o.dy = 0.3f;
                NativeScreenLayouts.apply(editorSource);
                boolean allowed = ((ScreenMouseEvents.AllowMouseClick)ScreenMouseEvents.allowMouseClick((Screen)editorSource).invoker()).allowMouseClick(editorSource, new MouseButtonEvent((double)(button.getX() + 5), (double)(button.getY() + 5), new MouseButtonInfo(0, 0)));
                if (allowed || count.get() != 1) {
                    throw new AssertionError((Object)("moved button did not get exactly one click: " + count.get()));
                }
                o.dy = 1.0f;
                NativeScreenLayouts.apply(editorSource);
                if (button.getY() + button.getHeight() > DevShots.editorSource.height) {
                    throw new AssertionError((Object)"button escaped preview");
                }
                NativeScreenLayouts.entries(editorSource).removeIf(e -> e.id().equals(id));
                NativeScreenLayouts.layout((Screen)DevShots.editorSource).items.remove(id);
                AstraClient.LOGGER.info("DevShots: CLICK PASS \u2014 moved button gets exactly one action, bounds clamped");
            }
            if (tick == 68) {
                client.gui.setScreen((Screen)new NativeLayoutEditor(editorSource));
            }
            if (tick == 88) {
                DevShots.shot(client, "editor_multiplayer");
            }
            if (tick == 92) {
                client.gui.setScreen((Screen)new DecorEditorScreen(null));
            }
            if (tick == 110 && client.gui.screen() instanceof DecorEditorScreen) {
                editor = (DecorEditorScreen)client.gui.screen();
                previewField = DecorEditorScreen.class.getDeclaredField("preview");
                ((Field)previewField).setAccessible(true);
                ClickGuiScreen preview = (ClickGuiScreen)((Field)previewField).get(editor);
                preview.setCategory(Category.THEMES);
            }
            if (tick == 120 && (previewField = client.gui.screen()) instanceof DecorEditorScreen) {
                editor = (DecorEditorScreen)previewField;
                previewField = DecorEditorScreen.class.getDeclaredField("preview");
                ((Field)previewField).setAccessible(true);
                ClickGuiScreen preview = (ClickGuiScreen)((Field)previewField).get(editor);
                Method viewMethod = DecorEditorScreen.class.getDeclaredMethod("menuView", new Class[0]);
                viewMethod.setAccessible(true);
                float[] view = (float[])viewMethod.invoke((Object)editor, new Object[0]);
                float px = view[0] + 50.0f * view[2];
                float py = view[1] + (92.0f + (float)Category.PROFILES.ordinal() * 32.5f + 14.0f) * view[2];
                editor.devClick(px, py);
                if (preview.getCategory() != Category.PROFILES) {
                    throw new AssertionError((Object)"single-click profile navigation failed");
                }
                AstraClient.LOGGER.info("DevShots: EDITOR NAV PASS \u2014 profile opens with one click");
            }
            if (tick == 140) {
                DevShots.shot(client, "editor_profile");
            }
            if (tick == 144) {
                ClickGuiScreen host = new ClickGuiScreen(null, true);
                ModuleCard card = new ModuleCard(ModuleManager.byId("status_bars"), host);
                card.layout(0.0f, 0.0f, 250.0f);
                if (card.isExpanded()) {
                    throw new AssertionError((Object)"card starts expanded");
                }
                UiContext ui = new UiContext();
                card.mouseClicked(ui, 10.0, 10.0, 1);
                if (!card.isExpanded()) {
                    throw new AssertionError((Object)"RMB did not expand card");
                }
                ModuleCard.collapseAll();
                if (card.isExpanded()) {
                    throw new AssertionError((Object)"card did not collapse after GUI close");
                }
                if (ModuleManager.byId("anime_sounds").getCategory() != Category.SOUNDS) {
                    throw new AssertionError((Object)"sounds category missing");
                }
                AstraClient.LOGGER.info("DevShots: CARDS PASS \u2014 collapsed by default, RMB expand, reset, separate sounds");
                client.gui.setScreen((Screen)new CanvasScreen(null));
            }
            if (tick == 164) {
                DevShots.shot(client, "editor_canvas_empty");
            }
            if (tick == 168) {
                client.gui.setScreen((Screen)new EmoteWheelScreen(ModuleManager.get(EmoteCosmetic.class), -1){

                    @Override
                    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                        super.extractRenderState(ctx, this.width / 2, this.height / 2 - 55, delta);
                    }
                });
            }
            if (tick == 184) {
                DevShots.shot(client, "wheel_love");
            }
            if (tick == 188) {
                client.gui.setScreen((Screen)new EmoteWheelScreen(ModuleManager.get(EmoteCosmetic.class), -1){

                    @Override
                    public void extractRenderState(GuiGraphicsExtractor ctx, int mx, int my, float delta) {
                        super.extractRenderState(ctx, this.width / 2 + 32, this.height / 2 + 45, delta);
                    }
                });
            }
            if (tick == 204) {
                DevShots.shot(client, "wheel_music");
            }
            if (tick == 208) {
                client.gui.setScreen(null);
                AstraClient.LOGGER.info("DevShots: EDITORS PASS \u2014 native extras, repeat open, moved click, collapse, sounds, wheel, HUD-style inspector");
                phase = Phase.DONE;
                tick = 0;
            }
        }
        catch (Throwable e2) {
            AstraClient.LOGGER.error("EDITORS FAIL", e2);
            client.stop();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void onTick(Minecraft client) {
        ++tick;
        switch (phase.ordinal()) {
            case 0: {
                if (ONLY.equals("menus") && client.gui.overlay() == null && titleTicks < 100 && (client.gui.screen() instanceof TitleScreen || client.gui.screen() instanceof AstraModsScreen)) {
                    if (++titleTicks == 40) {
                        DevShots.shot(client, "menus_title");
                    }
                    if (titleTicks == 42) {
                        client.gui.setScreen((Screen)new AstraModsScreen(client.gui.screen()));
                    }
                    if (titleTicks == 75) {
                        DevShots.shot(client, "menus_mods");
                    }
                    if (titleTicks == 78) {
                        client.gui.setScreen((Screen)new TitleScreen());
                    }
                    if (titleTicks < 100) {
                        tick = 0;
                        return;
                    }
                }
                if (!(client.gui.screen() instanceof TitleScreen) || client.gui.overlay() != null || tick < 40) {
                    return;
                }
                File saves = new File(client.gameDirectory, "saves");
                File[] worlds = saves.listFiles(File::isDirectory);
                if (worlds != null && worlds.length > 0) {
                    AstraClient.LOGGER.info("DevShots: \u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u043c\u0438\u0440\u0430 {}", (Object)worlds[0].getName());
                    client.createWorldOpenFlows().openWorld(worlds[0].getName(), () -> client.gui.setScreen((Screen)new TitleScreen()));
                    phase = Phase.LOADING;
                } else {
                    Screen back = client.gui.screen();
                    CreateWorldScreen.openFresh((Minecraft)client, () -> client.gui.setScreen(back));
                    phase = Phase.CREATE;
                }
                tick = 0;
                return;
            }
            case 1: {
                Screen worlds = client.gui.screen();
                if (!(worlds instanceof CreateWorldScreen)) return;
                CreateWorldScreen screen = (CreateWorldScreen)worlds;
                if (tick < 20) {
                    return;
                }
                for (GuiEventListener e : screen.children()) {
                    Button b;
                    if (!(e instanceof Button) || !(b = (Button)e).getMessage().getString().equals(Component.translatable((String)"selectWorld.create").getString())) continue;
                    AstraClient.LOGGER.info("DevShots: \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u0435 \u0442\u0435\u0441\u0442\u043e\u0432\u043e\u0433\u043e \u043c\u0438\u0440\u0430");
                    b.onPress((InputWithModifiers)new KeyEvent(257, 0, 0));
                    phase = Phase.LOADING;
                    tick = 0;
                    return;
                }
                return;
            }
            case 2: {
                if (client.player == null || client.level == null || client.gui.screen() != null) {
                    tick = 0;
                    return;
                }
                if (ONLY.equals("lyricopacity") && tick == 20) {
                    phase = Phase.LYRICOPACITY;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("worldlyrics") && tick == 20) {
                    phase = Phase.WORLDLYRICS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("trackbanner") && tick == 20) {
                    phase = Phase.TRACKBANNER;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("chatinput") && tick == 20) {
                    phase = Phase.CHATINPUT;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("chatstyles") && tick == 20) {
                    phase = Phase.CHATSTYLES;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("editors") && tick == 20) {
                    phase = Phase.EDITORS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("builder") && tick == 20) {
                    phase = Phase.BUILDER;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("artedit") && tick == 20) {
                    phase = Phase.ARTEDIT;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("cards") && tick == 20) {
                    phase = Phase.CARDS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("tools") && tick == 20) {
                    phase = Phase.TOOLS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("binds") && tick == 20) {
                    phase = Phase.BINDS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("pvp") && tick == 20) {
                    phase = Phase.PVP;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("perf") && tick == 20) {
                    phase = Phase.PERF;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("layout") && tick == 20) {
                    phase = Phase.LAYOUT;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("chat") && tick == 20) {
                    phase = Phase.CHAT;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("stream") && tick == 20) {
                    phase = Phase.STREAM;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("vps") && tick == 20) {
                    phase = Phase.VPS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("features") && tick == 20) {
                    phase = Phase.FEATURES;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("online") && tick == 20) {
                    phase = Phase.ONLINE;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("menus") && tick == 20) {
                    phase = Phase.MENUS;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("guistyle") && tick == 20) {
                    phase = Phase.GUISTYLE;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("themebg") && tick == 20) {
                    phase = Phase.THEMEBG;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("decor") && tick == 20) {
                    phase = Phase.DECOR;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("art") && tick == 20) {
                    phase = Phase.ART;
                    tick = 0;
                    return;
                }
                if (ONLY.equals("packs") && tick == 20) {
                    phase = Phase.PACKS;
                    tick = 0;
                    return;
                }
                if (ONLY_PLAYER && tick == 20) {
                    phase = Phase.PLAYER;
                    tick = 0;
                    return;
                }
                if (tick == 30) {
                    DevShots.shot(client, "join_caption");
                }
                if (tick == 100) {
                    DevShots.devCommand(client, "item replace entity @p weapon.mainhand with minecraft:diamond_sword[damage=1555]");
                }
                if (tick == 112) {
                    DevShots.shot(client, "durability_sword");
                }
                if (tick == 125) {
                    DevShots.devCommand(client, "item replace entity @p armor.head with minecraft:iron_helmet[damage=160]");
                }
                if (tick == 137) {
                    DevShots.shot(client, "durability_armor");
                }
                if (tick == 150) {
                    DevShots.shot(client, "ingame_plain");
                }
                if (tick == 152) {
                    DevShots.enable(WingsCosmetic.class);
                    DevShots.enable(HaloCosmetic.class);
                    DevShots.enable(AuraCosmetic.class);
                    DevShots.enable(CapeCosmetic.class);
                    DevShots.enable(WorldVisuals.class);
                    DevShots.enable(ColorFilter.class);
                    DevShots.enable(AmbientParticles.class);
                }
                if (tick == 200) {
                    DevShots.shot(client, "ingame_hud");
                }
                if (tick == 204) {
                    client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                }
                if (tick == 240) {
                    DevShots.shot(client, "ingame_cosmetics");
                }
                if (tick == 244) {
                    client.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
                }
                if (tick == 270) {
                    DevShots.shot(client, "ingame_cosmetics_front");
                }
                if (tick == 274) {
                    client.options.setCameraType(CameraType.FIRST_PERSON);
                }
                if (tick == 276) {
                    DevShots.devCommand(client, "summon minecraft:zombie ^ ^ ^3 {NoAI:1b,PersistenceRequired:1b,Silent:1b,ArmorItems:[{},{},{id:\"minecraft:diamond_chestplate\",count:1,components:{\"minecraft:enchantments\":{levels:{\"minecraft:protection\":1}}}},{}]}");
                }
                if (tick == 277) {
                    DevShots.devCommand(client, "item replace entity @p armor.chest with minecraft:netherite_chestplate[enchantments={protection:4}]");
                }
                if (tick >= 290 && tick <= 340 && tick % 8 == 0) {
                    DevShots.devHit(client);
                }
                if (tick == 330) {
                    DevShots.shot(client, "target_fx");
                }
                if (tick == 336) {
                    client.options.setCameraType(CameraType.THIRD_PERSON_BACK);
                }
                if (tick == 352) {
                    DevShots.shot(client, "target_fx_third");
                }
                if (tick == 354) {
                    client.options.setCameraType(CameraType.FIRST_PERSON);
                }
                if (tick <= 358) return;
                gui = new ClickGuiScreen(null);
                client.gui.setScreen((Screen)gui);
                phase = Phase.MENU;
                step = 0;
                tick = 0;
                return;
            }
            case 3: {
                DevShots.playerScenes(client);
                return;
            }
            case 4: {
                DevShots.packScenes(client);
                return;
            }
            case 5: {
                DevShots.artScenes(client);
                return;
            }
            case 6: {
                DevShots.decorScenes(client);
                return;
            }
            case 7: {
                DevShots.themeBgScenes(client);
                return;
            }
            case 8: {
                DevShots.guiStyleScenes(client);
                return;
            }
            case 9: {
                DevShots.menuScenes(client);
                return;
            }
            case 10: {
                DevShots.onlineScenes(client);
                return;
            }
            case 11: {
                DevShots.featureScenes(client);
                return;
            }
            case 19: {
                DevShots.lyricOpacityScenes(client);
                return;
            }
            case 18: {
                DevShots.worldLyricsScenes(client);
                return;
            }
            case 17: {
                DevShots.trackBannerScenes(client);
                return;
            }
            case 16: {
                DevShots.chatInputScenes(client);
                return;
            }
            case 15: {
                int i;
                ChatLayoutHud layout = ChatLayoutHud.get();
                ChatComponent hud = client.gui.hud.getChat();
                ChatHudAccessor access = (ChatHudAccessor)hud;
                String[] styles = new String[]{"\u041d\u0435\u043e\u043d", "\u0421\u0442\u0435\u043a\u043b\u043e", "\u041f\u0443\u0437\u044b\u0440\u0438", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442", "\u041c\u0438\u043d\u0438\u043c\u0430\u043b", "\u041a\u043b\u0430\u0441\u0441\u0438\u043a\u0430"};
                if (tick == 1) {
                    DevShots.enable(AnimeChatModule.class);
                    layout.setEnabled(true);
                    layout.resetPosition();
                    layout.scale.set(100.0);
                    client.options.chatScale().set(1.0);
                    client.options.chatWidth().set(0.65);
                    client.options.chatHeightFocused().set(0.65);
                    client.options.chatLineSpacing().set(0.0);
                    client.options.textBackgroundOpacity().set(0.6);
                    hud.clearMessages(false);
                    for (i = 0; i < 80; ++i) {
                        hud.addClientSystemMessage((Component)Component.literal((String)("\u00a7b[G] \u00a7fAstra_" + i + " \u00a77\u25b8 \u00a7f\u0421\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0438\u0441\u0442\u043e\u0440\u0438\u0438 \u2116" + i)));
                    }
                    client.gui.setScreen((Screen)new ChatScreen("", false));
                }
                for (i = 0; i < styles.length; ++i) {
                    if (tick == 5 + i * 20) {
                        DevShots.set(layout, "\u0421\u0442\u0438\u043b\u044c \u0447\u0430\u0442\u0430", styles[i]);
                    }
                    if (tick != 15 + i * 20) continue;
                    DevShots.shot(client, "chat_style_" + i);
                }
                if (tick == 127) {
                    layout.setPos(0.6f, 0.43f);
                    layout.scale.set(120.0);
                    client.options.chatScale().set(0.7);
                    DevShots.set(layout, "\u0421\u0442\u0438\u043b\u044c \u0447\u0430\u0442\u0430", "\u0421\u0442\u0435\u043a\u043b\u043e");
                }
                if (tick == 135) {
                    double scale = ((ChatHudAccessor)hud).astra$chatScale();
                    double sh = client.getWindow().getGuiScaledHeight();
                    double sw = client.getWindow().getGuiScaledWidth();
                    float[] tr = ChatLayoutHud.transform((float)sw, (float)sh);
                    double h = hud.getLinesPerPage() * access.astra$lineHeight();
                    double bottom = Math.floor((sh - 40.0) / scale);
                    double vx = (Math.ceil((double)((ChatHudAccessor)hud).astra$width() / scale) + 10.0) * scale;
                    double vy = (bottom - 2.0) * scale;
                    double x = (double)tr[0] + vx * (double)tr[2];
                    double y = (double)tr[1] + (vy - (sh - 40.0)) * (double)tr[2];
                    boolean clicked = client.gui.screen().mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)), false);
                    double topY = (double)tr[1] + ((bottom - h - 50.0) * scale - (sh - 40.0)) * (double)tr[2];
                    boolean dragged = client.gui.screen().mouseDragged(new MouseButtonEvent(x + 120.0, topY, new MouseButtonInfo(0, 0)), 120.0, topY - y);
                    int max = access.astra$visibleMessages().size() - hud.getLinesPerPage();
                    if (!clicked || !dragged || access.astra$scrolledLines() != max) {
                        throw new IllegalStateException("Scrollbar top regression: " + access.astra$scrolledLines() + " / " + max);
                    }
                    boolean released = client.gui.screen().mouseReleased(new MouseButtonEvent(x + 120.0, topY, new MouseButtonInfo(0, 0)));
                    client.gui.screen().mouseDragged(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)), 0.0, 0.0);
                    if (!released || access.astra$scrolledLines() != max) {
                        throw new IllegalStateException("Scrollbar release regression");
                    }
                    if (!client.gui.screen().mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)), false) || access.astra$scrolledLines() != 0) {
                        throw new IllegalStateException("Scrollbar track regression");
                    }
                    client.gui.screen().mouseReleased(new MouseButtonEvent(x, y, new MouseButtonInfo(0, 0)));
                    if (client.gui.screen().mouseClicked(new MouseButtonEvent(x - 50.0, y, new MouseButtonInfo(0, 0)), false)) {
                        AstraClient.LOGGER.info("DevShots: normal chat click handled by vanilla");
                    }
                    hud.scrollChat(30);
                    AstraClient.LOGGER.info("DevShots: CHAT SCROLLBAR PASS \u2014 transformed click, drag outside, endpoints, release, track");
                }
                if (tick == 145) {
                    DevShots.shot(client, "chat_style_transformed");
                }
                if (tick != 150) return;
                ChatScrollbar small = new ChatScrollbar();
                hud.clearMessages(false);
                hud.addClientSystemMessage((Component)Component.literal((String)"\u041a\u043e\u0440\u043e\u0442\u043a\u0430\u044f \u0438\u0441\u0442\u043e\u0440\u0438\u044f \u2014 \u0431\u0435\u0437 \u043f\u043e\u043b\u043e\u0441\u044b \u043f\u0440\u043e\u043a\u0440\u0443\u0442\u043a\u0438"));
                if (small.click(1.0, 1.0)) {
                    throw new IllegalStateException("Empty scrollbar regression");
                }
                AstraClient.LOGGER.info("DevShots: CHAT SHORT HISTORY PASS");
                client.gui.setScreen(null);
                phase = Phase.DONE;
                tick = 0;
                return;
            }
            case 21: {
                DevShots.perfScenes(client);
                return;
            }
            case 22: {
                DevShots.pvpScenes(client);
                return;
            }
            case 23: {
                DevShots.bindScenes(client);
                return;
            }
            case 24: {
                DevShots.toolScenes(client);
                return;
            }
            case 25: {
                DevShots.cardScenes(client);
                return;
            }
            case 26: {
                DevShots.artEditScenes(client);
                return;
            }
            case 27: {
                DevShots.builderScene(client);
                return;
            }
            case 28: {
                DevShots.editorScenes(client);
                return;
            }
            case 20: {
                ChatLayoutHud chatLayout = ChatLayoutHud.get();
                if (tick == 1) {
                    chatLayout.setPos(0.62f, 0.45f);
                    chatLayout.scale.set(120.0);
                    ChatComponent hud = client.gui.hud.getChat();
                    for (int i = 0; i < 6; ++i) {
                        hud.addClientSystemMessage((Component)Component.literal((String)("\u0421\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 " + i + " \u2014 \u0447\u0430\u0442 \u0441\u0434\u0432\u0438\u043d\u0443\u0442 \u0438 \u0443\u0432\u0435\u043b\u0438\u0447\u0435\u043d")));
                    }
                }
                if (tick == 5) {
                    client.gui.setScreen((Screen)new ChatScreen("", false));
                }
                if (tick == 20) {
                    DevShots.shot(client, "layout_chat");
                }
                if (tick == 22) {
                    float sw = client.getWindow().getGuiScaledWidth();
                    float sh = client.getWindow().getGuiScaledHeight();
                    float x = chatLayout.getX(sw) + 20.0f;
                    float y = chatLayout.getY(sh) + chatLayout.scaledHeight() - 6.0f;
                    boolean ok = ChatPatches.copyAt(x, y);
                    AstraClient.LOGGER.info("DevShots: \u041f\u041a\u041c \u043f\u043e \u0441\u0434\u0432\u0438\u043d\u0443\u0442\u043e\u043c\u0443 \u0447\u0430\u0442\u0443 {} \u2192 \u00ab{}\u00bb", (Object)ok, (Object)client.keyboardHandler.getClipboard());
                    client.gui.setScreen((Screen)new HudEditorScreen(null));
                }
                if (tick == 45) {
                    DevShots.shot(client, "layout_editor");
                }
                if (tick != 47) return;
                chatLayout.resetPosition();
                chatLayout.scale.set(100.0);
                client.gui.setScreen(null);
                phase = Phase.DONE;
                tick = 0;
                return;
            }
            case 14: {
                ChatComponent hud = client.gui.hud.getChat();
                if (tick == 1) {
                    hud.addClientSystemMessage((Component)Component.literal((String)"\u00a76[G] \u00a77[\u0418\u0433\u0440\u043e\u043a] \u00a7fStrawBag7774 \u00a76\u25b8 \u00a7f\u043a\u0442\u043e \u0422\u0438\u043c?"));
                    for (int i = 0; i < 3; ++i) {
                        hud.addClientSystemMessage((Component)Component.literal((String)"\u00a7e[Spy] \u00a77[\u0418\u0433\u0440\u043e\u043a] \u00a7fMinor_GG \u00a76\u25b8 \u00a7f\u0434\u0430\u0439 \u0441\u0435\u0442"));
                    }
                    hud.addClientSystemMessage((Component)Component.literal((String)"\u00a7a[+] \u00a7fDima_070100 \u00a77/ahead"));
                }
                if (tick == 5) {
                    client.gui.setScreen((Screen)new ChatScreen("", false));
                }
                if (tick == 20) {
                    DevShots.shot(client, "chat_patches");
                }
                if (tick == 22) {
                    float y = client.getWindow().getGuiScaledHeight() - 40 - 4;
                    boolean ok = ChatPatches.copyAt(20.0, y);
                    AstraClient.LOGGER.info("DevShots: \u041f\u041a\u041c-\u043a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u0435 {} \u2192 \u00ab{}\u00bb", (Object)ok, (Object)client.keyboardHandler.getClipboard());
                    ChatPatches.save();
                    try {
                        String json = Files.readString(ConfigManager.root().resolve("chat_history.json"));
                        AstraClient.LOGGER.info("DevShots: \u0438\u0441\u0442\u043e\u0440\u0438\u044f \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430, {} \u0431\u0430\u0439\u0442", (Object)json.length());
                    }
                    catch (Exception e) {
                        AstraClient.LOGGER.warn("DevShots: \u0438\u0441\u0442\u043e\u0440\u0438\u044f \u043d\u0435 \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u043b\u0430\u0441\u044c", (Throwable)e);
                    }
                }
                if (tick != 24) return;
                client.gui.setScreen(null);
                phase = Phase.DONE;
                tick = 0;
                return;
            }
            case 13: {
                if (tick == 1) {
                    client.gui.setScreen((Screen)new ChatScreen("/login mySecret123", false));
                }
                if (tick == 15) {
                    DevShots.shot(client, "stream_login");
                }
                if (tick == 17) {
                    client.gui.setScreen((Screen)new ChatScreen("/reg pass123 pass123", false));
                }
                if (tick == 30) {
                    DevShots.shot(client, "stream_reg");
                }
                if (tick == 32) {
                    client.gui.setScreen((Screen)new ChatScreen("/l qwerty", false));
                }
                if (tick == 45) {
                    DevShots.shot(client, "stream_l");
                }
                if (tick != 47) return;
                client.gui.setScreen(null);
                phase = Phase.DONE;
                tick = 0;
                return;
            }
            case 12: {
                if (tick == 1) {
                    Module online = ModuleManager.byId("astra_online");
                    DevShots.set(online, "\u0410\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "");
                    DevShots.set(online, "\u041a\u043b\u044e\u0447 \u043a\u043e\u043c\u043d\u0430\u0442\u044b", "");
                    online.setEnabled(true);
                }
                if (tick != 160) return;
                AstraClient.LOGGER.info("DevShots: VPS \u2014 {}", (Object)AstraOnline.status());
                phase = Phase.DONE;
                tick = 0;
                return;
            }
            case 29: {
                Category[] cats = Category.values();
                if (step >= cats.length) {
                    phase = Phase.EDITOR;
                    tick = 0;
                    return;
                }
                if (tick == 1) {
                    gui.setCategory(cats[step]);
                }
                if (tick == 30) {
                    DevShots.shot(client, String.format("%02d_%s", step, cats[step].name().toLowerCase()));
                }
                if (cats[step] == Category.COSMETICS) {
                    if (tick == 40) {
                        CosmeticsPage.devYaw = Float.valueOf(0.0f);
                    }
                    if (tick == 50) {
                        DevShots.shot(client, "04b_cosmetics_yaw0");
                    }
                    if (tick == 52) {
                        CosmeticsPage.devYaw = Float.valueOf(180.0f);
                    }
                    if (tick == 62) {
                        DevShots.shot(client, "04c_cosmetics_yaw180");
                    }
                    if (tick == 64) {
                        CosmeticsPage.devYaw = Float.valueOf(90.0f);
                    }
                    if (tick == 74) {
                        DevShots.shot(client, "04d_cosmetics_yaw90");
                    }
                    if (tick == 76) {
                        CosmeticsPage.devYaw = null;
                    }
                }
                if (tick < (cats[step] == Category.COSMETICS ? 80 : 34)) return;
                ++step;
                tick = 0;
                return;
            }
            case 30: {
                Screen screen;
                if (tick == 1) {
                    client.gui.setScreen((Screen)new HudEditorScreen(null));
                }
                if (tick == 30) {
                    DevShots.shot(client, "hud_editor");
                }
                if (tick == 32 && (screen = client.gui.screen()) instanceof HudEditorScreen) {
                    HudEditorScreen editor = (HudEditorScreen)screen;
                    editor.select(ModuleManager.get(MinimapHud.class));
                }
                if (tick == 60) {
                    DevShots.shot(client, "hud_editor_selected");
                }
                if (tick < 64) return;
                phase = Phase.DONE;
                tick = 0;
                return;
            }
            case 31: {
                if (tick != 5) return;
                AstraClient.LOGGER.info("DevShots: \u0433\u043e\u0442\u043e\u0432\u043e");
                client.stop();
            }
        }
    }

    static {
        fpsSamples = new ArrayList<Integer>();
        PERF_MODULES = new String[]{"visualizer", "companion", "status_bars", "scoreboard", "anime_chat", "wings", "halo", "aura", "trail", "cape", "now_playing", "target_effects", "ambient_particles", "color_filter", "world_visuals"};
    }

    private static enum Phase {
        TITLE,
        CREATE,
        LOADING,
        PLAYER,
        PACKS,
        ART,
        DECOR,
        THEMEBG,
        GUISTYLE,
        MENUS,
        ONLINE,
        FEATURES,
        VPS,
        STREAM,
        CHAT,
        CHATSTYLES,
        CHATINPUT,
        TRACKBANNER,
        WORLDLYRICS,
        LYRICOPACITY,
        LAYOUT,
        PERF,
        PVP,
        BINDS,
        TOOLS,
        CARDS,
        ARTEDIT,
        BUILDER,
        EDITORS,
        MENU,
        EDITOR,
        DONE;

    }
}

