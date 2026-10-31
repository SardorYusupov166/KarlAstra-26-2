/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
 *  net.minecraft.client.Camera
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.screens.Screen
 *  astra.render.WorldBuffers
 *  astra.render.WorldBuffers$BufferSource
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3x2fStack
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package astra.module.modules.visual;

import astra.animation.AnimationUtils;
import astra.gui.screens.PlayerScreen;
import astra.hud.HudModule;
import astra.media.CoverArt;
import astra.media.LyricFx;
import astra.media.Lyrics;
import astra.media.MediaBridge;
import astra.media.NowPlaying;
import astra.media.WorldLyricMotion;
import astra.media.WorldLyricsConfig;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.hud.TrackBannerHud;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.LyricLightning;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ColorSetting;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.theme.Theme;
import astra.utils.FrameStats;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import astra.render.WorldBuffers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class NowPlayingModule
extends HudModule {
    private static final String ANY = "\u041b\u044e\u0431\u043e\u0439 \u043f\u043b\u0435\u0435\u0440";
    private final ModeSetting source = this.add(new ModeSetting("\u0418\u0441\u0442\u043e\u0447\u043d\u0438\u043a", "\u041b\u044e\u0431\u043e\u0439 \u043f\u043b\u0435\u0435\u0440", "\u041b\u044e\u0431\u043e\u0439 \u043f\u043b\u0435\u0435\u0440", "Spotify", "\u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0430", "\u0411\u0440\u0430\u0443\u0437\u0435\u0440", "\u041a\u0440\u043e\u043c\u0435 \u0431\u0440\u0430\u0443\u0437\u0435\u0440\u0430"));
    private final BooleanSetting muteAstra = this.add((BooleanSetting)new BooleanSetting("\u0413\u043b\u0443\u0448\u0438\u0442\u044c \u043c\u0443\u0437\u044b\u043a\u0443 Astra", true).desc("\u041f\u043e\u043a\u0430 \u043d\u0430 \u041f\u041a \u0438\u0433\u0440\u0430\u0435\u0442 \u043c\u0443\u0437\u044b\u043a\u0430, \u0444\u043e\u043d\u043e\u0432\u0430\u044f \u043c\u0443\u0437\u044b\u043a\u0430 \u043a\u043b\u0438\u0435\u043d\u0442\u0430 \u043c\u043e\u043b\u0447\u0438\u0442"));
    private static final String CARD = "\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430";
    private static final String COMPACT = "\u041a\u043e\u043c\u043f\u0430\u043a\u0442";
    private static final String VINYL = "\u041f\u043b\u0430\u0441\u0442\u0438\u043d\u043a\u0430";
    private final ModeSetting widget = this.add(new ModeSetting("\u0412\u0438\u0434 \u0432\u0438\u0434\u0436\u0435\u0442\u0430", "\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430", "\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430", "\u041a\u043e\u043c\u043f\u0430\u043a\u0442", "\u041f\u043b\u0430\u0441\u0442\u0438\u043d\u043a\u0430"));
    private final BooleanSetting coverColors = this.add((BooleanSetting)new BooleanSetting("\u0426\u0432\u0435\u0442\u0430 \u0438\u0437 \u043e\u0431\u043b\u043e\u0436\u043a\u0438", true).desc("\u041f\u043e\u0434\u0441\u0432\u0435\u0442\u043a\u0430, \u043f\u043e\u043b\u043e\u0441\u0430 \u0438 \u0442\u0435\u043a\u0441\u0442 \u043e\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u044e\u0442\u0441\u044f \u0432 \u0446\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438 \u0442\u0440\u0435\u043a\u0430"));
    private final BooleanSetting equalizer = this.add(new BooleanSetting("\u042d\u043a\u0432\u0430\u043b\u0430\u0439\u0437\u0435\u0440", true));
    private final BooleanSetting progressBar = this.add(new BooleanSetting("\u041f\u043e\u043b\u043e\u0441\u0430 \u043f\u0440\u043e\u0433\u0440\u0435\u0441\u0441\u0430", true));
    private final BooleanSetting showTime = this.add(new BooleanSetting("\u0412\u0440\u0435\u043c\u044f \u0442\u0440\u0435\u043a\u0430", true));
    private final BooleanSetting showApp = this.add(new BooleanSetting("\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043f\u043b\u0435\u0435\u0440\u0430", true));
    private final BooleanSetting marquee = this.add((BooleanSetting)new BooleanSetting("\u0411\u0435\u0433\u0443\u0449\u0430\u044f \u0441\u0442\u0440\u043e\u043a\u0430", true).desc("\u0414\u043b\u0438\u043d\u043d\u044b\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u044f \u043f\u043b\u0430\u0432\u043d\u043e \u043f\u0440\u043e\u043a\u0440\u0443\u0447\u0438\u0432\u0430\u044e\u0442\u0441\u044f"));
    private final BooleanSetting hidePaused = this.add(new BooleanSetting("\u041f\u0440\u044f\u0442\u0430\u0442\u044c \u043d\u0430 \u043f\u0430\u0443\u0437\u0435", false));
    private static final String TOP_LEFT = "\u0421\u043b\u0435\u0432\u0430 \u0441\u0432\u0435\u0440\u0445\u0443";
    private static final String TOP = "\u0421\u0432\u0435\u0440\u0445\u0443 \u043f\u043e \u0446\u0435\u043d\u0442\u0440\u0443";
    private static final String CENTER = "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443";
    private final BooleanSetting banner = this.add((BooleanSetting)new BooleanSetting("\u041f\u043b\u0430\u0448\u043a\u0430 \u0441\u043c\u0435\u043d\u044b \u0442\u0440\u0435\u043a\u0430", true).desc("\u0411\u043e\u043b\u044c\u0448\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043f\u0435\u0441\u043d\u0438 \u0441 \u043e\u0431\u043b\u043e\u0436\u043a\u043e\u0439, \u043a\u043e\u0433\u0434\u0430 \u0442\u0440\u0435\u043a \u043c\u0435\u043d\u044f\u0435\u0442\u0441\u044f"));
    private final ModeSetting bannerPos = this.add((ModeSetting)new ModeSetting("\u041f\u043b\u0430\u0448\u043a\u0430: \u0433\u0434\u0435", "\u0421\u043b\u0435\u0432\u0430 \u0441\u0432\u0435\u0440\u0445\u0443", "\u0421\u043b\u0435\u0432\u0430 \u0441\u0432\u0435\u0440\u0445\u0443", "\u0421\u0432\u0435\u0440\u0445\u0443 \u043f\u043e \u0446\u0435\u043d\u0442\u0440\u0443", "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443").visibleWhen(() -> (Boolean)this.banner.get()));
    private final ModeSetting bannerFx = this.add((ModeSetting)new ModeSetting("\u041f\u043b\u0430\u0448\u043a\u0430: \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435", "\u0412\u043e\u043b\u043d\u0430", LyricFx.EFFECTS).visibleWhen(() -> (Boolean)this.banner.get()));
    private final NumberSetting bannerTime = this.add((NumberSetting)new NumberSetting("\u041f\u043b\u0430\u0448\u043a\u0430: \u0432\u0440\u0435\u043c\u044f \u043f\u043e\u043a\u0430\u0437\u0430", 5.0, 2.0, 15.0, 0.5).unit(" \u0441").visibleWhen(() -> (Boolean)this.banner.get()));
    private final NumberSetting bannerSize = this.add((NumberSetting)new NumberSetting("\u041f\u043b\u0430\u0448\u043a\u0430: \u0440\u0430\u0437\u043c\u0435\u0440", 100.0, 50.0, 250.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.banner.get()));
    private static final String IN_WORLD = "\u0412 \u043c\u0438\u0440\u0435";
    private static final String ON_SCREEN = "\u041d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435";
    private static final String OFF = "\u0412\u044b\u043a\u043b";
    private final ModeSetting lyricsMode = this.add(new ModeSetting("\u0422\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438", "\u0412 \u043c\u0438\u0440\u0435", "\u0412 \u043c\u0438\u0440\u0435", "\u041d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", "\u0412\u044b\u043a\u043b"));
    private final ModeSetting lyricsFx = this.add((ModeSetting)(new ModeSetting("\u042d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u044f", "\u0412\u043e\u043b\u043d\u0430", LyricFx.WORLD_EFFECTS).desc("\u00ab\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438\u00bb \u0440\u0438\u0441\u0443\u0435\u0442 \u0440\u0430\u0437\u0440\u044f\u0434\u044b \u043f\u043e \u0431\u0443\u043a\u0432\u0430\u043c \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u00ab\u0412 \u043c\u0438\u0440\u0435\u00bb.")).visibleWhen(this::lyricsOn));
    private static final String COLOR_COVER = "\u0426\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438";
    private static final String COLOR_THEME = "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b";
    private static final String COLOR_RAINBOW = "\u0420\u0430\u0434\u0443\u0433\u0430";
    private static final String COLOR_CUSTOM = "\u0421\u0432\u043e\u0439";
    private final ModeSetting lyricsColor = this.add((ModeSetting)new ModeSetting("\u0426\u0432\u0435\u0442 \u0442\u0435\u043a\u0441\u0442\u0430", "\u0426\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438", "\u0426\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u0420\u0430\u0434\u0443\u0433\u0430", "\u0421\u0432\u043e\u0439").visibleWhen(this::lyricsOn));
    private final ColorSetting color1 = this.add((ColorSetting)new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 1", -45608).visibleWhen(() -> this.lyricsOn() && this.lyricsColor.is(COLOR_CUSTOM)));
    private final ColorSetting color2 = this.add((ColorSetting)new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 2", -13058568).visibleWhen(() -> this.lyricsOn() && this.lyricsColor.is(COLOR_CUSTOM)));
    private final BooleanSetting karaoke = this.add((BooleanSetting)(new BooleanSetting("\u041a\u0430\u0440\u0430\u043e\u043a\u0435-\u0437\u0430\u043b\u0438\u0432\u043a\u0430", true).desc("\u0421\u0442\u0440\u043e\u043a\u0430 \u0437\u0430\u043b\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u0446\u0432\u0435\u0442\u043e\u043c \u043f\u043e \u043c\u0435\u0440\u0435 \u0442\u043e\u0433\u043e, \u043a\u0430\u043a \u0435\u0451 \u043f\u043e\u044e\u0442")).visibleWhen(this::lyricsOn));
    private final BooleanSetting glow = this.add((BooleanSetting)new BooleanSetting("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u0442\u0435\u043a\u0441\u0442\u0430", true).visibleWhen(this::lyricsOn));
    private final BooleanSetting nextLine = this.add((BooleanSetting)(new BooleanSetting("\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f \u0441\u0442\u0440\u043e\u043a\u0430", true).desc("\u041f\u043e\u0434 \u0442\u0435\u043a\u0443\u0449\u0435\u0439 \u0441\u0442\u0440\u043e\u043a\u043e\u0439 \u0431\u043b\u0435\u0434\u043d\u043e \u0432\u0438\u0434\u043d\u0430 \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0430\u044f")).visibleWhen(this::lyricsOn));
    private final BooleanSetting sway = this.add((BooleanSetting)new BooleanSetting("\u041f\u043e\u043a\u0430\u0447\u0438\u0432\u0430\u043d\u0438\u0435 \u0431\u0443\u043a\u0432", true).visibleWhen(this::lyricsOn));
    private final NumberSetting lyricsSize = this.add((NumberSetting)new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u0442\u0435\u043a\u0441\u0442\u0430", 100.0, 40.0, 300.0, 5.0).unit("%").visibleWhen(this::lyricsOn));
    private static final String FOLLOW = "\u041f\u0435\u0440\u0435\u0434 \u0432\u0437\u0433\u043b\u044f\u0434\u043e\u043c";
    private static final String PINNED = "\u0417\u0430\u043a\u0440\u0435\u043f\u043b\u044f\u0442\u044c \u043d\u0430 \u043c\u0435\u0441\u0442\u0435";
    private final ModeSetting anchor = this.add((ModeSetting)((Setting)(new ModeSetting("\u0412 \u043c\u0438\u0440\u0435: \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u041f\u0435\u0440\u0435\u0434 \u0432\u0437\u0433\u043b\u044f\u0434\u043e\u043c", "\u041f\u0435\u0440\u0435\u0434 \u0432\u0437\u0433\u043b\u044f\u0434\u043e\u043c", "\u0417\u0430\u043a\u0440\u0435\u043f\u043b\u044f\u0442\u044c \u043d\u0430 \u043c\u0435\u0441\u0442\u0435").desc("\u00ab\u0417\u0430\u043a\u0440\u0435\u043f\u043b\u044f\u0442\u044c\u00bb \u2014 \u043a\u0430\u0436\u0434\u0430\u044f \u0441\u0442\u0440\u043e\u043a\u0430 \u043f\u043e\u044f\u0432\u043b\u044f\u0435\u0442\u0441\u044f \u0432 \u043c\u0438\u0440\u0435 \u0438 \u043e\u0441\u0442\u0430\u0451\u0442\u0441\u044f \u0442\u0430\u043c, \u0433\u0434\u0435 \u0432\u044b \u0441\u043c\u043e\u0442\u0440\u0435\u043b\u0438")).visibleWhen(() -> this.lyricsMode.is(IN_WORLD))).onChange(v -> this.resetWorldPlacement()));
    private final BooleanSetting faceCamera = this.add((BooleanSetting)new BooleanSetting("\u0412 \u043c\u0438\u0440\u0435: \u043f\u043e\u0432\u043e\u0440\u043e\u0442 \u043a \u043a\u0430\u043c\u0435\u0440\u0435", true).visibleWhen(() -> this.lyricsMode.is(IN_WORLD) && this.anchor.is(PINNED)));
    private final BooleanSetting throughBlocks = this.add((BooleanSetting)(new BooleanSetting("\u0412 \u043c\u0438\u0440\u0435: \u0441\u043a\u0432\u043e\u0437\u044c \u0431\u043b\u043e\u043a\u0438", true).desc("\u0422\u0435\u043a\u0441\u0442 \u043d\u0435 \u043f\u0440\u044f\u0447\u0435\u0442\u0441\u044f \u0437\u0430 \u0441\u043a\u043b\u043e\u043d\u0430\u043c\u0438 \u0438 \u0441\u0442\u0435\u043d\u0430\u043c\u0438")).visibleWhen(() -> this.lyricsMode.is(IN_WORLD)));
    private final NumberSetting distance = this.add((NumberSetting)(new NumberSetting("\u0412 \u043c\u0438\u0440\u0435: \u0440\u0430\u0441\u0441\u0442\u043e\u044f\u043d\u0438\u0435", 4.0, 1.5, 10.0, 0.5).unit(" \u0431\u043b.").visibleWhen(() -> this.lyricsMode.is(IN_WORLD))).onChange(v -> this.resetWorldPlacement()));
    private final NumberSetting height = this.add((NumberSetting)(new NumberSetting("\u0412 \u043c\u0438\u0440\u0435: \u0432\u044b\u0441\u043e\u0442\u0430", 0.3, -2.0, 2.0, 0.1).unit(" \u0431\u043b.").visibleWhen(() -> this.lyricsMode.is(IN_WORLD))).onChange(v -> this.resetWorldPlacement()));
    private final NumberSetting follow = this.add((NumberSetting)(new NumberSetting("\u0412 \u043c\u0438\u0440\u0435: \u043f\u043b\u0430\u0432\u043d\u043e\u0441\u0442\u044c", 5.0, 1.0, 20.0, 1.0).desc("\u041a\u0430\u043a \u0431\u044b\u0441\u0442\u0440\u043e \u0442\u0435\u043a\u0441\u0442 \u0434\u043e\u0433\u043e\u043d\u044f\u0435\u0442 \u0432\u0437\u0433\u043b\u044f\u0434")).visibleWhen(() -> this.lyricsMode.is(IN_WORLD) && this.anchor.is(FOLLOW)));
    private static final String BOTTOM = "\u0421\u043d\u0438\u0437\u0443";
    private static final String MIDDLE = "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443";
    private static final String UP = "\u0421\u0432\u0435\u0440\u0445\u0443";
    private final ModeSetting screenPos = this.add((ModeSetting)new ModeSetting("\u041d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435: \u043f\u043e\u0437\u0438\u0446\u0438\u044f", "\u0421\u043d\u0438\u0437\u0443", "\u0421\u043d\u0438\u0437\u0443", "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443", "\u0421\u0432\u0435\u0440\u0445\u0443").visibleWhen(() -> this.lyricsMode.is(ON_SCREEN)));
    private final NumberSetting offset = this.add((NumberSetting)(new NumberSetting("\u0421\u0434\u0432\u0438\u0433 \u0442\u0435\u043a\u0441\u0442\u0430", 0.0, -3000.0, 3000.0, 100.0).unit(" \u043c\u0441").desc("\u0415\u0441\u043b\u0438 \u0442\u0435\u043a\u0441\u0442 \u0441\u043f\u0435\u0448\u0438\u0442 \u0438\u043b\u0438 \u043e\u043f\u0430\u0437\u0434\u044b\u0432\u0430\u0435\u0442")).visibleWhen(this::lyricsOn));
    private final BooleanSetting fetchLyrics = this.add((BooleanSetting)new BooleanSetting("\u0417\u0430\u0433\u0440\u0443\u0436\u0430\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u044b (lrclib.net)", true).desc("\u0422\u0435\u043a\u0441\u0442\u044b \u043f\u0435\u0441\u0435\u043d \u0438\u0437 \u043e\u0442\u043a\u0440\u044b\u0442\u043e\u0439 \u0431\u0430\u0437\u044b lrclib.net; \u0432 \u0437\u0430\u043f\u0440\u043e\u0441 \u0443\u0445\u043e\u0434\u044f\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0438 \u0438\u0441\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c"));
    private final WorldLyricsConfig worldStyle = this.createWorldStyle();
    private final KeybindSetting playerKey = this.add(new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u043f\u043b\u0435\u0435\u0440\u0430", 74));
    private final KeybindSetting toggleKey = this.add(new KeybindSetting("\u041f\u0430\u0443\u0437\u0430 / \u0438\u0433\u0440\u0430\u0442\u044c", 92));
    private final KeybindSetting nextKey = this.add(new KeybindSetting("\u0421\u043b\u0435\u0434\u0443\u044e\u0449\u0438\u0439 \u0442\u0440\u0435\u043a", 93));
    private final KeybindSetting prevKey = this.add(new KeybindSetting("\u041f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u0439 \u0442\u0440\u0435\u043a", 91));
    private final ButtonSetting openButton = this.add(new ButtonSetting("\u041f\u043b\u0435\u0435\u0440", "\u25b6 \u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u043b\u0435\u0435\u0440", () -> this.openPlayer()));
    private final List<HistoryEntry> history = new ArrayList<HistoryEntry>();
    private final boolean[] keyWas = new boolean[4];
    private String lastKey = "";
    private long trackChangedAt;
    private NowPlaying bannerTrack = NowPlaying.NONE;
    private int curLine = -1;
    private int prevLine = -1;
    private long curLineAt;
    private long prevLineAt;
    private String linesKey = "";
    private Vec3 worldAnchor;
    private Quaternionf pinnedRot;
    private float worldReferenceDistance = 4.0f;
    private Vec3 previousWorldAnchor;
    private Quaternionf previousWorldRot;
    private float previousReferenceDistance = 4.0f;
    private String outgoingWorldText = "";
    private Object placementWorld;
    private String placementMode = "";
    private long demoStartedAt;
    private int demoLine = -1;
    private static final String[] DEMO_LINES = new String[]{"\u0421\u0438\u044f\u0439 \u0441\u0440\u0435\u0434\u0438 \u0437\u0432\u0451\u0437\u0434", "\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0442\u0430\u043d\u0446\u0443\u044e\u0442 \u043f\u043e\u0434 \u043c\u0443\u0437\u044b\u043a\u0443", "\u0422\u0432\u043e\u0439 \u043c\u0438\u0440 \u2014 \u0442\u0432\u043e\u0439 \u0440\u0438\u0442\u043c"};
    private static final long SAMPLE_START = System.currentTimeMillis();
    private float vinylAngleValue;
    private long vinylLast;
    private static final NowPlaying BANNER_SAMPLE = new NowPlaying(true, "Firefox.exe", "In Affliction", "Yakui The Maid", "", true, 0L, 0L, 180000L, "", false, false, false);
    private float smoothYaw;
    private float smoothPitch;
    private float smoothDist = -1.0f;
    private boolean followInit;
    private boolean followReferenceInit;
    private final Map<WorldWrapKey, List<String>> worldWrapCache = new LinkedHashMap<WorldWrapKey, List<String>>();

    private WorldLyricsConfig createWorldStyle() {
        WorldLyricsConfig config = new WorldLyricsConfig(() -> this.lyricsMode.is(IN_WORLD), this::previewWorldLyrics, this::resetWorldPlacement, this::applyWorldPreset, this::previewLightningLyrics);
        for (Setting<?> setting : config.settings()) {
            this.add(setting);
        }
        config.horizontal.onChange(v -> this.resetWorldPlacement());
        for (Setting<?> setting : config.settings()) {
            if (!setting.getName().startsWith("\u041c\u043e\u043b\u043d\u0438\u044f:")) continue;
            setting.visibleWhen(() -> this.lyricsMode.is(IN_WORLD) && this.lyricsFx.is("\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438") && (setting != config.lightningColor || (Boolean)config.lightningTextColor.get() == false));
        }
        return config;
    }

    private void rememberWorldLine(String text) {
        this.outgoingWorldText = text;
        this.previousWorldAnchor = this.worldAnchor;
        this.previousWorldRot = this.pinnedRot == null ? null : new Quaternionf((Quaternionfc)this.pinnedRot);
        this.previousReferenceDistance = this.worldReferenceDistance;
    }

    private void resetWorldPlacement() {
        this.previousWorldAnchor = null;
        this.worldAnchor = null;
        this.outgoingWorldText = "";
        this.previousWorldRot = null;
        this.pinnedRot = null;
        this.followInit = false;
        this.followReferenceInit = false;
        this.smoothDist = -1.0f;
        this.curLineAt = System.currentTimeMillis();
    }

    public void previewWorldLyrics() {
        if (this.demoStartedAt != 0L && System.currentTimeMillis() - this.demoStartedAt < 12000L) {
            this.demoStartedAt = 0L;
            this.resetWorldPlacement();
            return;
        }
        this.setEnabled(true);
        this.lyricsMode.set(IN_WORLD);
        this.resetWorldPlacement();
        this.demoStartedAt = System.currentTimeMillis();
        this.demoLine = -1;
        mc.gui.setScreen(null);
    }

    public void previewLightningLyrics() {
        this.demoStartedAt = 0L;
        this.applyWorldPreset("\u0413\u0440\u043e\u0437\u0430");
        this.previewWorldLyrics();
    }

    public void applyWorldPreset(String name) {
        this.lyricsMode.set(IN_WORLD);
        this.lyricsSize.set(100.0);
        this.glow.set(true);
        this.sway.set(true);
        this.nextLine.set(true);
        this.worldStyle.sizeMode.set("\u0420\u0430\u0437\u043c\u0435\u0440 \u0432 \u043c\u0438\u0440\u0435");
        this.worldStyle.nearProtection.set(true);
        this.worldStyle.entryTime.set(0.8);
        this.worldStyle.exitTime.set(0.45);
        this.worldStyle.intensity.set(100.0);
        this.worldStyle.maxWidth.set(5.0);
        this.worldStyle.tracking.set(100.0);
        this.worldStyle.opacity.set(100.0);
        this.worldStyle.wrap.set(true);
        this.worldStyle.bold.set(true);
        this.worldStyle.idle.set("\u041f\u0430\u0440\u0435\u043d\u0438\u0435");
        this.worldStyle.motionAmount.set(25.0);
        this.worldStyle.glowStrength.set(80.0);
        this.worldStyle.glowRadius.set(0.7);
        this.worldStyle.exit.set("\u0423\u043b\u0435\u0442\u0435\u0442\u044c \u0432\u0432\u0435\u0440\u0445");
        this.worldStyle.ornament.set("\u041d\u0435\u0442");
        this.worldStyle.particles.set("\u041d\u0435\u0442");
        this.worldStyle.shadow.set(true);
        switch (name) {
            case "\u0413\u0440\u043e\u0437\u0430": {
                this.lyricsFx.set("\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438");
                this.lyricsColor.set(COLOR_CUSTOM);
                this.color1.set(-6456065);
                this.color2.set(-10164481);
                this.worldStyle.entryTime.set(1.8);
                this.worldStyle.exitTime.set(0.55);
                this.worldStyle.exit.set("\u0420\u0430\u0441\u0441\u044b\u043f\u0430\u0442\u044c\u0441\u044f");
                this.worldStyle.ornament.set("\u041b\u0438\u043d\u0438\u044f");
                this.worldStyle.glowStrength.set(100.0);
                this.worldStyle.idle.set("\u0414\u044b\u0445\u0430\u043d\u0438\u0435");
                this.worldStyle.lightningWidth.set(0.65);
                this.worldStyle.lightningBranches.set(2.0);
                this.worldStyle.lightningBrightness.set(85.0);
                this.worldStyle.lightningTime.set(0.18);
                break;
            }
            case "\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430": {
                this.lyricsFx.set("\u0413\u043e\u043b\u043e\u0433\u0440\u0430\u043c\u043c\u0430");
                this.lyricsColor.set(COLOR_CUSTOM);
                this.color1.set(-13058568);
                this.color2.set(-6427649);
                this.worldStyle.ornament.set("\u0421\u043a\u043e\u0431\u043a\u0438");
                this.worldStyle.exit.set("\u0420\u0430\u0441\u0441\u044b\u043f\u0430\u0442\u044c\u0441\u044f");
                this.worldStyle.particles.set("\u0418\u0441\u043a\u0440\u044b");
                this.worldStyle.idle.set("\u0414\u044b\u0445\u0430\u043d\u0438\u0435");
                break;
            }
            case "\u0421\u0430\u043a\u0443\u0440\u0430": {
                this.lyricsFx.set("\u0417\u0432\u0435\u0437\u0434\u043e\u043f\u0430\u0434");
                this.lyricsColor.set(COLOR_CUSTOM);
                this.color1.set(-32581);
                this.color2.set(-15904);
                this.worldStyle.particles.set("\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438");
                this.worldStyle.exit.set("\u041e\u043f\u0430\u0441\u0442\u044c");
                this.worldStyle.ornament.set("\u0417\u0432\u0451\u0437\u0434\u044b");
                this.worldStyle.idle.set("\u0412\u043e\u043b\u043d\u0430");
                this.worldStyle.entryTime.set(1.1);
                this.worldStyle.exitTime.set(0.7);
                break;
            }
            case "\u0421\u043f\u043e\u043a\u043e\u0439\u043d\u044b\u0439": {
                this.lyricsFx.set("\u041f\u043b\u0430\u0432\u043d\u043e");
                this.lyricsColor.set(COLOR_THEME);
                this.worldStyle.idle.set("\u041d\u0435\u0442");
                this.worldStyle.intensity.set(30.0);
                this.worldStyle.glowStrength.set(30.0);
                this.worldStyle.exit.set("\u0420\u0430\u0441\u0442\u0432\u043e\u0440\u0435\u043d\u0438\u0435");
                this.worldStyle.entryTime.set(0.5);
                break;
            }
            default: {
                this.lyricsFx.set("\u0421\u0432\u0435\u0442\u043e\u0432\u043e\u0439 \u043f\u0440\u043e\u0445\u043e\u0434");
                this.lyricsColor.set(COLOR_CUSTOM);
                this.color1.set(-45608);
                this.color2.set(-13058568);
                this.worldStyle.ornament.set("\u041b\u0438\u043d\u0438\u044f");
                this.worldStyle.particles.set("\u0418\u0441\u043a\u0440\u044b");
            }
        }
        this.resetWorldPlacement();
    }

    public NowPlayingModule() {
        super("now_playing", "\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442", "\u041c\u0443\u0437\u044b\u043a\u0430 \u0441 \u041f\u041a \u0432 \u0438\u0433\u0440\u0435: \u0442\u0440\u0435\u043a, \u043e\u0431\u043b\u043e\u0436\u043a\u0430, \u0442\u0435\u043a\u0441\u0442 \u043f\u0435\u0441\u043d\u0438 \u0438 \u0443\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435.", Category.VISUAL, Icon.WAVE, 0.1f, 0.34f);
        this.setDefaultEnabled(false);
    }

    public static NowPlayingModule get() {
        return ModuleManager.get(NowPlayingModule.class);
    }

    private boolean lyricsOn() {
        return !this.lyricsMode.is(OFF);
    }

    public NowPlaying track() {
        NowPlaying np = MediaBridge.state();
        if (!np.active()) {
            return np;
        }
        String app = np.appName();
        boolean ok = switch ((String)this.source.get()) {
            case "Spotify" -> app.equals("Spotify");
            case "\u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0430" -> app.equals("\u042f\u043d\u0434\u0435\u043a\u0441 \u041c\u0443\u0437\u044b\u043a\u0430");
            case "\u0411\u0440\u0430\u0443\u0437\u0435\u0440" -> np.isBrowser();
            case "\u041a\u0440\u043e\u043c\u0435 \u0431\u0440\u0430\u0443\u0437\u0435\u0440\u0430" -> {
                if (!np.isBrowser()) {
                    yield true;
                }
                yield false;
            }
            default -> true;
        };
        return ok ? np : NowPlaying.NONE;
    }

    public static boolean pcMusicPlaying() {
        NowPlayingModule m = NowPlayingModule.get();
        if (m == null || !m.isEnabled() || !((Boolean)m.muteAstra.get()).booleanValue()) {
            return false;
        }
        NowPlaying np = m.track();
        return np.hasTrack() && np.playing();
    }

    public List<HistoryEntry> history() {
        return this.history;
    }

    public long offsetMs() {
        return this.offset.getInt();
    }

    public int[] colors(NowPlaying np) {
        CoverArt.Cover c;
        Theme t = NowPlayingModule.theme();
        if (((Boolean)this.coverColors.get()).booleanValue() && (c = CoverArt.get(np.cover())) != null) {
            return new int[]{c.color1(), c.color2()};
        }
        return new int[]{t.accent(), t.accent2()};
    }

    private int[] lyricColors(NowPlaying np) {
        int[] nArray;
        switch ((String)this.lyricsColor.get()) {
            case "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b": {
                int[] nArray2 = new int[2];
                nArray2[0] = NowPlayingModule.theme().accent();
                nArray = nArray2;
                nArray2[1] = NowPlayingModule.theme().accent2();
                break;
            }
            case "\u0421\u0432\u043e\u0439": {
                int[] nArray3 = new int[2];
                nArray3[0] = this.color1.getColor();
                nArray = nArray3;
                nArray3[1] = this.color2.getColor();
                break;
            }
            default: {
                CoverArt.Cover c = CoverArt.get(np.cover());
                if (c != null) {
                    int[] nArray4 = new int[2];
                    nArray4[0] = c.color1();
                    nArray = nArray4;
                    nArray4[1] = c.color2();
                    break;
                }
                int[] nArray5 = new int[2];
                nArray5[0] = NowPlayingModule.theme().accent();
                nArray = nArray5;
                nArray5[1] = NowPlayingModule.theme().accent2();
                break;
            }
        }
        return nArray;
    }

    public void openPlayer() {
        if (!this.isEnabled()) {
            this.setEnabled(true);
        }
        mc.gui.setScreen((Screen)new PlayerScreen(NowPlayingModule.mc.gui.screen()));
    }

    @Override
    protected void onDisable() {
        MediaBridge.stop();
        this.resetWorldPlacement();
        this.demoStartedAt = 0L;
    }

    @Override
    public void onTick() {
        MediaBridge.tick(true);
        NowPlaying np = this.track();
        Lyrics.update(np, (Boolean)this.fetchLyrics.get());
        if (np.hasTrack() && !np.key().equals(this.lastKey)) {
            this.lastKey = np.key();
            this.trackChangedAt = System.currentTimeMillis();
            this.bannerTrack = np;
            this.history.removeIf(h -> h.title().equals(np.title()) && h.artist().equals(np.artist()));
            this.history.add(0, new HistoryEntry(np.title(), np.artist(), np.appName(), np.cover()));
            while (this.history.size() > 8) {
                this.history.remove(this.history.size() - 1);
            }
        } else if (np.hasTrack() && !np.cover().isEmpty() && !this.history.isEmpty() && this.history.get(0).cover().isEmpty() && this.history.get(0).title().equals(np.title())) {
            HistoryEntry h2 = this.history.get(0);
            this.history.set(0, new HistoryEntry(h2.title(), h2.artist(), h2.app(), np.cover()));
            this.bannerTrack = np;
        }
        this.handleKeys();
    }

    private void handleKeys() {
        if (NowPlayingModule.mc.gui.screen() != null) {
            for (int i = 0; i < this.keyWas.length; ++i) {
                this.keyWas[i] = true;
            }
            return;
        }
        KeybindSetting[] keys = new KeybindSetting[]{this.playerKey, this.toggleKey, this.nextKey, this.prevKey};
        Window window = mc.getWindow();
        for (int i = 0; i < keys.length; ++i) {
            boolean down;
            boolean bl = down = keys[i].isBound() && InputConstants.isKeyDown((Window)window, (int)keys[i].getKey());
            if (down && !this.keyWas[i]) {
                switch (i) {
                    case 0: {
                        this.openPlayer();
                        break;
                    }
                    case 1: {
                        MediaBridge.togglePlay();
                        break;
                    }
                    case 2: {
                        MediaBridge.next();
                        break;
                    }
                    default: {
                        MediaBridge.previous();
                    }
                }
            }
            this.keyWas[i] = down;
        }
    }

    private void updateLine(NowPlaying np) {
        if (!this.linesKey.equals(np.key() + Lyrics.lines().size())) {
            this.linesKey = np.key() + Lyrics.lines().size();
            this.prevLine = -1;
            this.curLine = -1;
            this.resetWorldPlacement();
        }
        if (Lyrics.status() != Lyrics.Status.SYNCED) {
            return;
        }
        int idx = Lyrics.indexAt(np.position() + this.offsetMs());
        if (idx != this.curLine) {
            this.rememberWorldLine(NowPlayingModule.lineText(this.curLine));
            this.prevLine = this.curLine;
            this.prevLineAt = System.currentTimeMillis();
            this.curLine = idx;
            this.curLineAt = this.prevLineAt;
            if (this.anchor.is(PINNED)) {
                this.worldAnchor = null;
            }
        }
    }

    private float karaokeFill(NowPlaying np, int idx) {
        if (!((Boolean)this.karaoke.get()).booleanValue()) {
            return -1.0f;
        }
        List<Lyrics.Line> lines = Lyrics.lines();
        if (idx < 0 || idx >= lines.size()) {
            return -1.0f;
        }
        long start = lines.get(idx).time();
        long end = idx + 1 < lines.size() ? lines.get(idx + 1).time() : start + 5000L;
        return NowPlayingModule.LyricFxClamp((float)(np.position() + this.offsetMs() - start) / Math.max(300.0f, (float)(end - start) * 0.85f));
    }

    private static float LyricFxClamp(float v) {
        return v < 0.0f ? 0.0f : Math.min(1.0f, v);
    }

    private static String lineText(int idx) {
        List<Lyrics.Line> lines = Lyrics.lines();
        if (idx < 0 || idx >= lines.size()) {
            return "";
        }
        String t = lines.get(idx).text();
        return t.isBlank() ? "\u266a  \u266a  \u266a" : t;
    }

    @Override
    public float width() {
        return switch ((String)this.widget.get()) {
            case COMPACT -> 136.0f;
            case VINYL -> 172.0f;
            default -> 168.0f;
        };
    }

    @Override
    public float height() {
        return switch ((String)this.widget.get()) {
            case COMPACT -> 22.0f;
            case VINYL -> 58.0f;
            default -> 50.0f;
        };
    }

    @Override
    public boolean shouldRender() {
        NowPlaying np = this.track();
        return np.hasTrack() && ((Boolean)this.hidePaused.get() == false || np.playing());
    }

    private static NowPlaying sample() {
        long pos = (System.currentTimeMillis() - SAMPLE_START) % 154000L;
        return new NowPlaying(true, "Spotify", "\u0421\u0430\u043a\u0443\u0440\u0430 \u0438 \u043d\u0435\u043e\u043d", "Astra Lo-fi", "", true, pos, System.currentTimeMillis(), 154000L, "", true, true, true);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        NowPlaying np = this.track();
        if (!np.hasTrack()) {
            if (!editor) {
                return;
            }
            np = NowPlayingModule.sample();
        }
        int[] c = this.colors(np);
        switch ((String)this.widget.get()) {
            case "\u041a\u043e\u043c\u043f\u0430\u043a\u0442": {
                this.renderCompact(ctx, np, c, w, h);
                break;
            }
            case "\u041f\u043b\u0430\u0441\u0442\u0438\u043d\u043a\u0430": {
                this.renderVinyl(ctx, np, c, w, h);
                break;
            }
            default: {
                this.renderCard(ctx, np, c, w, h);
            }
        }
    }

    private void renderCard(GuiGraphicsExtractor ctx, NowPlaying np, int[] c, float w, float h) {
        Theme t = NowPlayingModule.theme();
        float cs = 38.0f;
        float cx = 6.0f;
        float cy = 6.0f;
        NowPlayingModule.drawCover(ctx, np, c, cx, cy, cs, 6.0f);
        float tx = cx + cs + 7.0f;
        float right = w - 7.0f;
        float titleRight = right - (float)((Boolean)this.equalizer.get() != false ? 16 : 0);
        this.scrollText(ctx, np.title(), tx, cy + 1.0f, titleRight - tx, 8.4f, t.text(), true);
        this.scrollText(ctx, np.artist().isEmpty() ? np.appName() : np.artist(), tx, cy + 12.0f, right - tx, 6.8f, t.textDim(), false);
        if (((Boolean)this.equalizer.get()).booleanValue()) {
            NowPlayingModule.drawEqualizer(ctx, right - 12.0f, cy + 9.0f, 12.0f, 9.0f, np.playing(), c);
        }
        float by = cy + cs - 9.0f;
        if (((Boolean)this.showApp.get()).booleanValue()) {
            String app = np.appName();
            Fonts.draw(ctx, app, tx, by - 1.0f, 5.6f, ColorUtil.withAlpha(c[0], 0.9f), true);
        }
        if (((Boolean)this.showTime.get()).booleanValue()) {
            String time = NowPlaying.time(np.position()) + (String)(np.durMs() > 0L ? " / " + NowPlaying.time(np.durMs()) : "");
            Fonts.drawRight(ctx, time, right, by - 1.0f, 5.6f, t.textMuted(), false);
        }
        if (((Boolean)this.progressBar.get()).booleanValue() && np.durMs() > 0L) {
            NowPlayingModule.drawProgress(ctx, tx, by + 6.0f, right - tx, 2.5f, np.progress(), c);
        }
    }

    private void renderCompact(GuiGraphicsExtractor ctx, NowPlaying np, int[] c, float w, float h) {
        Theme t = NowPlayingModule.theme();
        NowPlayingModule.drawCover(ctx, np, c, 3.0f, 3.0f, 16.0f, 4.0f);
        float tx = 23.0f;
        float right = w - (float)((Boolean)this.equalizer.get() != false ? 16 : 5);
        String text = np.title() + (String)(np.artist().isEmpty() ? "" : "  \u00b7  " + np.artist());
        this.scrollText(ctx, text, tx, 7.0f, right - tx, 7.4f, t.text(), true);
        if (((Boolean)this.equalizer.get()).booleanValue()) {
            NowPlayingModule.drawEqualizer(ctx, w - 13.0f, 6.0f, 9.0f, 10.0f, np.playing(), c);
        }
        if (((Boolean)this.progressBar.get()).booleanValue() && np.durMs() > 0L) {
            NowPlayingModule.drawProgress(ctx, tx, h - 3.5f, w - tx - 5.0f, 1.5f, np.progress(), c);
        }
    }

    private void renderVinyl(GuiGraphicsExtractor ctx, NowPlaying np, int[] c, float w, float h) {
        Theme t = NowPlayingModule.theme();
        float r = 23.0f;
        float cx = 6.0f + r;
        float cy = h / 2.0f;
        float spin = this.vinylAngle(np);
        Render2D.glow(ctx, cx - r, cy - r, r * 2.0f, r * 2.0f, r, 6.0f, ColorUtil.withAlpha(c[0], 0.35f));
        Render2D.circle(ctx, cx, cy, r, -15922668);
        for (int i = 0; i < 4; ++i) {
            Render2D.ring(ctx, cx, cy, r - 3.0f - (float)i * 3.2f, 0.5f, ColorUtil.withAlpha(-1, 0.07f));
        }
        Render2D.arc(ctx, cx, cy, r - 4.0f, 1.5f, spin * 0.0f - 2.4f, 0.9f, ColorUtil.withAlpha(-1, 0.18f));
        CoverArt.Cover cover = CoverArt.get(np.cover());
        float inner = r * 0.55f;
        if (cover != null) {
            Render2D.texturedCircle(ctx, Render2D.textureView(cover.id()), cx, cy, inner, 0.5f, 0.5f, 0.5f, spin, -1);
        } else {
            Render2D.circle(ctx, cx, cy, inner, Paint.vertical(cy - inner, cy + inner, c[0], c[1]));
            Render2D.star(ctx, cx, cy, inner * 0.5f, spin, -1);
        }
        Render2D.circle(ctx, cx, cy, 2.0f, -15922668);
        float tx = cx + r + 8.0f;
        float right = w - 7.0f;
        this.scrollText(ctx, np.title(), tx, 9.0f, right - tx - (float)((Boolean)this.equalizer.get() != false ? 15 : 0), 8.6f, t.text(), true);
        if (((Boolean)this.equalizer.get()).booleanValue()) {
            NowPlayingModule.drawEqualizer(ctx, right - 11.0f, 8.0f, 11.0f, 10.0f, np.playing(), c);
        }
        this.scrollText(ctx, np.artist().isEmpty() ? np.appName() : np.artist(), tx, 21.0f, right - tx, 6.8f, t.textDim(), false);
        if (((Boolean)this.progressBar.get()).booleanValue() && np.durMs() > 0L) {
            NowPlayingModule.drawProgress(ctx, tx, 37.0f, right - tx, 2.5f, np.progress(), c);
        }
        if (((Boolean)this.showTime.get()).booleanValue()) {
            Fonts.draw(ctx, NowPlaying.time(np.position()), tx, 43.0f, 5.6f, t.textMuted(), false);
            if (np.durMs() > 0L) {
                Fonts.drawRight(ctx, NowPlaying.time(np.durMs()), right, 43.0f, 5.6f, t.textMuted(), false);
            }
        }
    }

    private float vinylAngle(NowPlaying np) {
        long now = System.currentTimeMillis();
        if (this.vinylLast != 0L && np.playing()) {
            this.vinylAngleValue = (this.vinylAngleValue + (float)(now - this.vinylLast) * 0.06f) % 360.0f;
        }
        this.vinylLast = now;
        return this.vinylAngleValue;
    }

    public static void drawCover(GuiGraphicsExtractor ctx, NowPlaying np, int[] c, float x, float y, float s, float r) {
        Render2D.glow(ctx, x, y, s, s, r, Math.max(3.0f, s * 0.15f), ColorUtil.withAlpha(c[0], 0.35f));
        CoverArt.Cover cover = CoverArt.get(np.cover());
        if (cover != null) {
            Render2D.textureRoundRect(ctx, cover.id(), 1.0f, x, y, s, s, r, -1);
        } else {
            Render2D.roundRect(ctx, x, y, s, s, r, Paint.vertical(y, y + s, c[0], c[1]));
            Fonts.drawCentered(ctx, "\u266a", x + s / 2.0f, y + s * 0.22f, s * 0.6f, -1, true);
        }
        Render2D.outline(ctx, x, y, s, s, r, 0.75f, ColorUtil.withAlpha(-1, 0.18f));
    }

    public static void drawEqualizer(GuiGraphicsExtractor ctx, float x, float y, float w, float h, boolean playing, int[] c) {
        int bars = 4;
        float bw = w / ((float)bars * 1.6f);
        float time = AnimationUtils.time();
        for (int i = 0; i < bars; ++i) {
            float v = playing ? 0.25f + 0.75f * (float)Math.abs(Math.sin(time * (3.1f + (float)i * 1.7f) + (float)i * 1.3f) * Math.cos(time * (1.3f + (float)i * 0.4f))) : 0.18f;
            float bh = Math.max(1.5f, h * v);
            float bx = x + (float)i * bw * 1.6f;
            Render2D.roundRect(ctx, bx, y + h - bh, bw, bh, bw / 2.0f, Paint.vertical(y, y + h, c[1], c[0]));
        }
    }

    public static void drawProgress(GuiGraphicsExtractor ctx, float x, float y, float w, float h, float p, int[] c) {
        Render2D.roundRect(ctx, x, y, w, h, h / 2.0f, ColorUtil.withAlpha(-1, 0.12f));
        float fw = Math.max(h, w * p);
        Render2D.glow(ctx, x, y, fw, h, h / 2.0f, 3.0f, ColorUtil.withAlpha(c[0], 0.3f));
        Render2D.roundRect(ctx, x, y, fw, h, h / 2.0f, Paint.horizontal(x, x + fw, c[0], c[1]));
        Render2D.circle(ctx, x + fw, y + h / 2.0f, h * 0.9f, -1);
    }

    private void scrollText(GuiGraphicsExtractor ctx, String s, float x, float y, float maxW, float size, int color, boolean bold) {
        float cycle;
        float tw = Fonts.width(s, size, bold);
        if (tw <= maxW) {
            Fonts.draw(ctx, s, x, y, size, color, bold);
            return;
        }
        if (!((Boolean)this.marquee.get()).booleanValue()) {
            Fonts.draw(ctx, Fonts.trim(s, maxW, size, bold), x, y, size, color, bold);
            return;
        }
        float gap = size * 3.0f;
        float period = tw + gap;
        float t = AnimationUtils.time() * 16.0f;
        float pos = t % (cycle = period + 40.0f);
        float off = pos < 40.0f ? 0.0f : pos - 40.0f;
        ctx.enableScissor((int)Math.floor(x), (int)Math.floor(y - 2.0f), (int)Math.ceil(x + maxW), (int)Math.ceil(y + size + 3.0f));
        Fonts.draw(ctx, s, x - off, y, size, color, bold);
        Fonts.draw(ctx, s, x - off + period, y, size, color, bold);
        ctx.disableScissor();
    }

    public void renderOverlay(GuiGraphicsExtractor ctx) {
        NowPlaying np = this.track();
        if (((Boolean)this.banner.get()).booleanValue()) {
            this.renderBanner(ctx);
        }
        if (!np.hasTrack()) {
            return;
        }
        this.updateLine(np);
        if (this.lyricsMode.is(ON_SCREEN)) {
            this.renderScreenLyrics(ctx, np);
        }
    }

    public double bannerSize() {
        return (Double)this.bannerSize.get();
    }

    public void bannerSize(double value) {
        this.bannerSize.set(value);
    }

    public double bannerDuration() {
        return (Double)this.bannerTime.get();
    }

    public void bannerDuration(double value) {
        this.bannerTime.set(value);
    }

    public String bannerEffect() {
        return (String)this.bannerFx.get();
    }

    public void bannerEffect(String value) {
        this.bannerFx.set(value);
    }

    public boolean bannerVisible() {
        return this.isEnabled() && (Boolean)this.banner.get() != false && this.bannerTrack.hasTrack() && this.trackChangedAt != 0L && (float)(System.currentTimeMillis() - this.trackChangedAt) <= this.bannerTime.getFloat() * 1000.0f;
    }

    public float bannerDefaultX(float sw, float width, float scale) {
        return TOP.equals(this.bannerPos.get()) || "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443".equals(this.bannerPos.get()) ? (sw - width) / 2.0f : 14.0f - 6.0f * scale;
    }

    public float bannerDefaultY(float sh, float scale) {
        return switch ((String)this.bannerPos.get()) {
            case TOP -> 14.0f - 4.0f * scale;
            case "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443" -> sh * 0.3f - 4.0f * scale;
            default -> 58.0f - 4.0f * scale;
        };
    }

    private NowPlaying bannerSong(boolean editor) {
        if (!editor) {
            return this.bannerTrack;
        }
        NowPlaying current = this.track();
        return current.hasTrack() ? current : BANNER_SAMPLE;
    }

    public BannerLayout bannerLayout(boolean editor, float scale) {
        NowPlaying song = this.bannerSong(editor);
        float available = Math.max(30.0f, (float)mc.getWindow().getGuiScaledWidth() / scale - 68.0f);
        float limit = Math.min(available, (float)mc.getWindow().getGuiScaledWidth() * 0.62f / scale);
        List<LyricFx.Glyph> glyphs = LyricFx.split(song.title(), ch -> Fonts.width(ch, 1.0f, true));
        float tw = LyricFx.totalWidth(glyphs) * 24.0f;
        float size = tw > limit ? 24.0f * limit / Math.max(1.0f, tw) : 24.0f;
        tw = Math.min(tw, limit);
        String sub = song.artist().isEmpty() ? song.appName() : song.artist() + (String)((Boolean)this.showApp.get() != false ? "  \u00b7  " + song.appName() : "");
        sub = Fonts.trim(sub, available, 9.5f, false);
        float width = 68.0f + Math.max(tw, Fonts.width(sub, 9.5f, false));
        return new BannerLayout(song, glyphs, size, sub, width);
    }

    private void renderBanner(GuiGraphicsExtractor ctx) {
        TrackBannerHud layout = TrackBannerHud.get();
        if (!this.bannerVisible() || layout == null || !layout.isEnabled()) {
            return;
        }
        layout.render(ctx, ctx.guiWidth(), ctx.guiHeight(), false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drawTrackBanner(GuiGraphicsExtractor ctx, BannerLayout layout, boolean editor, int[] colors) {
        float life = this.bannerTime.getFloat();
        float age = editor ? 8.0f : (float)(System.currentTimeMillis() - this.trackChangedAt) / 1000.0f;
        float exit = editor || age < life - 0.6f ? -1.0f : age - (life - 0.6f);
        float fade = exit < 0.0f ? 1.0f : 1.0f - NowPlayingModule.LyricFxClamp(exit / 0.6f);
        float appear = editor ? 1.0f : AnimationUtils.easeOutCubic(NowPlayingModule.LyricFxClamp(age / 0.4f));
        float prev = Render2D.alpha;
        try {
            float x = 6.0f;
            float y = 4.0f;
            float cover = 46.0f;
            int[] c = colors;
            Render2D.alpha *= fade;
            Render2D.shadow(ctx, 0.0f, 0.0f, layout.width(), 54.0f, 14.0f, 18.0f, ColorUtil.withAlpha(-16054504, 0.45f * appear));
            Render2D.alpha *= appear;
            NowPlayingModule.drawCover(ctx, layout.track(), c, x, y + (1.0f - appear) * -10.0f, cover, 8.0f);
            Render2D.alpha = prev * fade;
            LyricFx.animate(layout.glyphs(), (String)this.bannerFx.get(), age, exit < 0.0f ? -1.0f : exit * 0.6f, -1.0f, new LyricFx.Colors(ColorUtil.brighten(c[0], 0.15f), ColorUtil.brighten(c[1], 0.15f), 0, false), AnimationUtils.time(), false);
            NowPlayingModule.drawGlyphs(ctx, layout.glyphs(), 62.0f, y + cover * 0.08f, layout.titleSize(), true, true, c[0], 1.0f);
            float alpha = editor ? 1.0f : NowPlayingModule.LyricFxClamp(Math.max(0.0f, age - 0.25f) / 0.4f);
            Fonts.draw(ctx, layout.subtitle(), 62.0f + (1.0f - alpha) * 8.0f, y + cover * 0.08f + layout.titleSize() + 3.0f, 9.5f, ColorUtil.withAlpha(ColorUtil.lerp(-1, c[1], 0.35f), 0.9f * alpha), false);
        }
        finally {
            Render2D.alpha = prev;
        }
    }

    private void renderScreenLyrics(GuiGraphicsExtractor ctx, NowPlaying np) {
        String next;
        String text;
        if (Lyrics.status() != Lyrics.Status.SYNCED) {
            return;
        }
        float sw = ctx.guiWidth();
        float sh = ctx.guiHeight();
        float size = 15.0f * this.lyricsSize.getFloat() / 100.0f;
        float y = switch ((String)this.screenPos.get()) {
            case UP -> sh * 0.16f;
            case "\u041f\u043e \u0446\u0435\u043d\u0442\u0440\u0443" -> sh * 0.5f + 24.0f;
            default -> sh * 0.74f;
        };
        int[] c = this.lyricColors(np);
        LyricFx.Colors colors = new LyricFx.Colors(c[0], c[1], ColorUtil.withAlpha(-1, 0.55f), this.lyricsColor.is(COLOR_RAINBOW));
        long now = System.currentTimeMillis();
        float time = AnimationUtils.time();
        if (this.prevLine >= 0 && now - this.prevLineAt < 400L) {
            this.drawScreenLine(ctx, NowPlayingModule.lineText(this.prevLine), sw, y, size, (float)(now - this.prevLineAt) / 1000.0f, -1.0f, 1.0f, colors, c[0], time, true);
        }
        if (!(text = NowPlayingModule.lineText(this.curLine)).isBlank()) {
            this.drawScreenLine(ctx, text, sw, y, size, (float)(now - this.curLineAt) / 1000.0f, this.karaokeFill(np, this.curLine), 1.0f, colors, c[0], time, false);
        }
        if (((Boolean)this.nextLine.get()).booleanValue() && !(next = NowPlayingModule.lineText(this.curLine + 1)).isBlank()) {
            float ns = size * 0.6f;
            float nw = Fonts.width(next, ns, false);
            Fonts.draw(ctx, next, (sw - Math.min(nw, sw * 0.85f)) / 2.0f, y + size * 1.25f, ns, ColorUtil.withAlpha(-1, 0.38f), false);
        }
    }

    private void drawScreenLine(GuiGraphicsExtractor ctx, String text, float sw, float y, float size, float t, float fill, float alpha, LyricFx.Colors colors, int glowColor, float time, boolean exiting) {
        float maxW;
        List<LyricFx.Glyph> gs = LyricFx.split(text, ch -> Fonts.width(ch, 1.0f, true));
        float tw = LyricFx.totalWidth(gs) * size;
        if (tw > (maxW = sw * 0.85f)) {
            size *= maxW / tw;
            tw = maxW;
        }
        LyricFx.animate(gs, (String)this.lyricsFx.get(), exiting ? 9.0f : t, exiting ? t : -1.0f, exiting ? -1.0f : fill, colors, time, (Boolean)this.sway.get());
        if (((Boolean)this.glow.get()).booleanValue()) {
            Render2D.shadow(ctx, (sw - tw) / 2.0f - 10.0f, y - 4.0f, tw + 20.0f, size + 8.0f, size * 0.6f, size, ColorUtil.withAlpha(glowColor, 0.1f * alpha * (exiting ? 1.0f - NowPlayingModule.LyricFxClamp(t / 0.35f) : 1.0f)));
        }
        NowPlayingModule.drawGlyphs(ctx, gs, (sw - tw) / 2.0f, y, size, true, (Boolean)this.glow.get(), glowColor, alpha);
    }

    public static void drawGlyphs(GuiGraphicsExtractor ctx, List<LyricFx.Glyph> gs, float left, float top, float size, boolean bold, boolean glow, int glowColor, float alphaMul) {
        Matrix3x2fStack m = ctx.pose();
        float o = Math.max(0.6f, size * 0.07f);
        for (LyricFx.Glyph g : gs) {
            float a = g.alpha * alphaMul;
            if (a < 0.03f || g.ch.isBlank()) continue;
            float cx = left + (g.x + g.w / 2.0f + g.dx) * size;
            float cy = top + (0.5f + g.dy) * size;
            Component txt = Fonts.text(g.ch, bold);
            float half = g.w * size / 2.0f;
            m.pushMatrix();
            m.translate(cx, cy);
            if (g.rot != 0.0f) {
                m.rotate(g.rot);
            }
            if (g.scale != 1.0f) {
                m.scale(g.scale, g.scale);
            }
            if (glow) {
                int gc = ColorUtil.withAlpha(ColorUtil.lerp(glowColor, g.color, 0.4f), Math.min(1.0f, 0.22f * a * g.glow));
                Fonts.drawText(ctx, txt, -half - o, -size / 2.0f, size, gc, false);
                Fonts.drawText(ctx, txt, -half + o, -size / 2.0f, size, gc, false);
                Fonts.drawText(ctx, txt, -half, -size / 2.0f - o, size, gc, false);
                Fonts.drawText(ctx, txt, -half, -size / 2.0f + o, size, gc, false);
            }
            Fonts.drawText(ctx, txt, -half + size * 0.06f, -size / 2.0f + size * 0.07f, size, ColorUtil.withAlpha(-16054504, 0.45f * a), false);
            Fonts.drawText(ctx, txt, -half, -size / 2.0f, size, ColorUtil.withAlpha(g.color, a), false);
            m.popMatrix();
        }
    }

    public void renderWorld(LevelRenderContext context) {
        Vec3 at;
        WorldPlacement previous;
        boolean showPrevious;
        float fill;
        float age;
        String next;
        String text;
        boolean demo;
        if (!this.lyricsMode.is(IN_WORLD) || NowPlayingModule.mc.player == null || NowPlayingModule.mc.level == null || NowPlayingModule.mc.gui.hud.isHidden()) {
            return;
        }
        if (this.placementWorld != NowPlayingModule.mc.level || !this.placementMode.equals(this.anchor.get())) {
            this.resetWorldPlacement();
            this.placementWorld = NowPlayingModule.mc.level;
            this.placementMode = (String)this.anchor.get();
        }
        long now = System.currentTimeMillis();
        boolean bl = demo = this.demoStartedAt != 0L && now - this.demoStartedAt < 12000L;
        if (!demo && this.demoStartedAt != 0L) {
            this.demoStartedAt = 0L;
            this.resetWorldPlacement();
        }
        NowPlaying np = this.track();
        if (demo) {
            int index = (int)((now - this.demoStartedAt) / 4000L);
            if (this.demoLine != index) {
                if (this.demoLine >= 0) {
                    this.rememberWorldLine(DEMO_LINES[this.demoLine]);
                    this.prevLineAt = now;
                }
                this.demoLine = index;
                if (this.anchor.is(PINNED)) {
                    this.worldAnchor = null;
                }
            }
            text = DEMO_LINES[index];
            next = DEMO_LINES[(index + 1) % DEMO_LINES.length];
            age = (float)((now - this.demoStartedAt) % 4000L) / 1000.0f;
            fill = (Boolean)this.karaoke.get() != false ? Math.min(1.0f, age / 3.4f) : -1.0f;
        } else {
            if (!np.hasTrack()) {
                return;
            }
            this.updateLine(np);
            if (Lyrics.status() != Lyrics.Status.SYNCED) {
                return;
            }
            text = NowPlayingModule.lineText(this.curLine);
            next = NowPlayingModule.lineText(this.curLine + 1);
            age = (float)(now - this.curLineAt) / 1000.0f;
            fill = this.karaokeFill(np, this.curLine);
        }
        float exitDuration = this.worldStyle.exitTime.getFloat();
        boolean bl2 = showPrevious = !this.outgoingWorldText.isBlank() && (float)(now - this.prevLineAt) < exitDuration * 1000.0f;
        if (text.isBlank() && !showPrevious) {
            return;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        if (this.anchor.is(FOLLOW)) {
            this.followView(camera);
            if (!this.followReferenceInit) {
                this.worldReferenceDistance = (float)Math.min((double)this.distance.getFloat(), this.worldAnchor.distanceTo(camera.position()));
                this.followReferenceInit = true;
            }
        } else if (this.worldAnchor == null) {
            this.worldAnchor = this.targetPoint(camera);
            this.pinnedRot = new Quaternionf((Quaternionfc)camera.rotation());
            this.worldReferenceDistance = (float)Math.min((double)this.distance.getFloat(), this.worldAnchor.distanceTo(camera.position()));
        }
        int[] c = this.lyricColors(np);
        LyricFx.Colors colors = new LyricFx.Colors(c[0], c[1], this.worldStyle.unfilledColor(), this.lyricsColor.is(COLOR_RAINBOW));
        WorldBuffers.BufferSource imm = WorldBuffers.forContext(context);
        float time = AnimationUtils.time();
        if (showPrevious && (previous = this.worldPlacement(camera, at = this.anchor.is(PINNED) ? this.previousWorldAnchor : this.worldAnchor, this.previousWorldRot, this.previousReferenceDistance)) != null) {
            this.drawWorldGroup(imm, previous, this.outgoingWorldText, "", (float)(now - this.prevLineAt) / 1000.0f, -1.0f, colors, c[0], time, true);
        }
        WorldPlacement current = this.worldPlacement(camera, this.worldAnchor, this.pinnedRot, this.worldReferenceDistance);
        if (!text.isBlank() && current != null) {
            this.drawWorldGroup(imm, current, text, next, age, fill, colors, c[0], time, false);
        }
        imm.endBatch();
    }

    private WorldPlacement worldPlacement(Camera camera, Vec3 at, Quaternionf fixedRotation, float reference) {
        if (at == null) {
            return null;
        }
        Vec3 delta = at.subtract(camera.position());
        float actualDistance = (float)delta.length();
        float alpha = WorldLyricMotion.distanceAlpha(actualDistance, this.worldStyle.renderDistance.getFloat());
        if (alpha <= 0.0f || delta.dot(Vec3.directionFromRotation((float)camera.xRot(), (float)camera.yRot())) <= 0.05) {
            return null;
        }
        float scale = WorldLyricMotion.scale(this.lyricsSize.getFloat(), reference, actualDistance, this.worldStyle.sizeMode.is("\u0420\u0430\u0437\u043c\u0435\u0440 \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435"));
        Quaternionf rotation = this.anchor.is(PINNED) && (Boolean)this.faceCamera.get() == false && fixedRotation != null ? fixedRotation : camera.rotation();
        Matrix4f matrix = new Matrix4f().translate((float)delta.x, (float)delta.y, (float)delta.z).rotate((Quaternionfc)rotation).rotateZ((float)Math.toRadians(this.worldStyle.roll.getFloat())).rotateX((float)Math.toRadians(this.worldStyle.tilt.getFloat())).scale(scale, -scale, scale);
        float widthLimit = this.worldStyle.maxWidth.getFloat();
        float heightLimit = Float.MAX_VALUE;
        if (((Boolean)this.worldStyle.nearProtection.get()).booleanValue()) {
            float viewHeight = 2.0f * actualDistance * (float)Math.tan(Math.toRadians((double)((Integer)NowPlayingModule.mc.options.fov().get()).intValue() / 2.0));
            float aspect = (float)mc.getWindow().getWidth() / (float)Math.max(1, mc.getWindow().getHeight());
            widthLimit = Math.min(widthLimit, viewHeight * aspect * 0.72f);
            heightLimit = viewHeight * 0.55f;
        }
        return new WorldPlacement(matrix, scale, alpha, widthLimit, heightLimit);
    }

    private void followView(Camera camera) {
        Vec3 cam = camera.position();
        float yaw = camera.yRot();
        float pitch = Math.max(-25.0f, Math.min(25.0f, camera.xRot()));
        float dt = FrameStats.delta();
        if (!this.followInit) {
            this.smoothYaw = yaw;
            this.smoothPitch = pitch;
            this.followInit = true;
        }
        float k = 1.0f - (float)Math.exp(-dt * this.follow.getFloat());
        this.smoothYaw += Mth.wrapDegrees((float)(yaw - this.smoothYaw)) * k;
        this.smoothPitch += (pitch - this.smoothPitch) * k;
        float dYaw = Mth.wrapDegrees((float)(yaw - this.smoothYaw));
        if (Math.abs(dYaw) > 15.0f) {
            this.smoothYaw = yaw - Math.signum(dYaw) * 15.0f;
        }
        if (Math.abs(pitch - this.smoothPitch) > 10.0f) {
            this.smoothPitch = pitch - Math.signum(pitch - this.smoothPitch) * 10.0f;
        }
        Vec3 look = Vec3.directionFromRotation((float)this.smoothPitch, (float)this.smoothYaw);
        double d = this.rayDistance(cam, look);
        this.smoothDist = this.smoothDist < 0.0f || d < (double)this.smoothDist ? (float)d : (this.smoothDist += (float)(d - (double)this.smoothDist) * (1.0f - (float)Math.exp(-dt * 6.0f)));
        this.worldAnchor = cam.add(look.scale((double)this.smoothDist)).add(0.0, (double)this.height.getFloat(), 0.0).add(Vec3.directionFromRotation((float)0.0f, (float)(camera.yRot() + 90.0f)).scale((double)this.worldStyle.horizontal.getFloat()));
    }

    private double rayDistance(Vec3 cam, Vec3 look) {
        double d = this.distance.getFloat();
        BlockHitResult hit = NowPlayingModule.mc.level.clip(new ClipContext(cam, cam.add(look.scale(d)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)NowPlayingModule.mc.player));
        if (hit.getType() != HitResult.Type.MISS) {
            d = Math.max(1.0, hit.getLocation().distanceTo(cam) - 0.35);
        }
        return d;
    }

    private Vec3 targetPoint(Camera camera) {
        double d;
        float pitch;
        Vec3 look;
        Vec3 end;
        Vec3 cam = camera.position();
        BlockHitResult hit = NowPlayingModule.mc.level.clip(new ClipContext(cam, end = cam.add((look = Vec3.directionFromRotation((float)(pitch = Math.max(-25.0f, Math.min(25.0f, camera.xRot()))), (float)camera.yRot())).scale(d = (double)this.distance.getFloat())), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)NowPlayingModule.mc.player));
        if (hit.getType() != HitResult.Type.MISS) {
            double hd = hit.getLocation().distanceTo(cam);
            d = Math.max(1.0, hd - 0.35);
        }
        return cam.add(look.scale(d)).add(0.0, (double)this.height.getFloat(), 0.0).add(Vec3.directionFromRotation((float)0.0f, (float)(camera.yRot() + 90.0f)).scale((double)this.worldStyle.horizontal.getFloat()));
    }

    private List<LyricFx.Glyph> worldGlyphs(String text) {
        List<LyricFx.Glyph> result = LyricFx.split(text, ch -> (float)NowPlayingModule.mc.font.width((FormattedText)Fonts.text(ch, (Boolean)this.worldStyle.bold.get())) / 9.0f);
        WorldLyricMotion.tracking(result, this.worldStyle.tracking.getFloat() / 100.0f);
        return result;
    }

    private float worldTextWidth(String text) {
        return LyricFx.totalWidth(this.worldGlyphs(text)) * 9.0f;
    }

    private List<String> worldRows(String text, float limit) {
        if (!((Boolean)this.worldStyle.wrap.get()).booleanValue()) {
            return List.of(text);
        }
        WorldWrapKey key = new WorldWrapKey(text, Math.max(12, (int)limit), this.worldStyle.maxRows.getInt(), (Boolean)this.worldStyle.bold.get(), this.worldStyle.tracking.getFloat(), NowPlayingModule.mc.font);
        List<String> cached = this.worldWrapCache.get(key);
        if (cached != null) {
            return cached;
        }
        List<String> rows = WorldLyricMotion.wrap(text, key.width(), key.rows(), this::worldTextWidth);
        if (this.worldWrapCache.size() >= 12) {
            this.worldWrapCache.remove(this.worldWrapCache.keySet().iterator().next());
        }
        this.worldWrapCache.put(key, rows);
        return rows;
    }

    private void drawWorldGroup(WorldBuffers.BufferSource imm, WorldPlacement placement, String text, String next, float age, float fill, LyricFx.Colors colors, int glowColor, float time, boolean exiting) {
        if (placement.alpha() * this.worldStyle.opacity.getFloat() / 100.0f < 0.025f) {
            return;
        }
        if (((String)text).codePointCount(0, ((String)text).length()) > 320) {
            text = ((String)text).substring(0, ((String)text).offsetByCodePoints(0, 320)) + "\u2026";
        }
        if (((String)next).codePointCount(0, ((String)next).length()) > 320) {
            next = ((String)next).substring(0, ((String)next).offsetByCodePoints(0, 320)) + "\u2026";
        }
        float limit = placement.widthLimit() / placement.scale();
        List<String> rows = this.worldRows((String)text, limit);
        ArrayList<List<LyricFx.Glyph>> glyphRows = new ArrayList<List<LyricFx.Glyph>>();
        float widest = 1.0f;
        float weight = 0.0f;
        for (String row : rows) {
            List<LyricFx.Glyph> glyphs = this.worldGlyphs(row);
            glyphRows.add(glyphs);
            float width = LyricFx.totalWidth(glyphs) * 9.0f;
            widest = Math.max(widest, width);
            weight += width;
        }
        float totalHeight = Math.max(9.0f, (float)glyphRows.size() * this.worldStyle.rowGap.getFloat() + (float)((Boolean)this.nextLine.get() != false ? 18 : 0));
        float fit = Math.min(Math.min(1.0f, limit / widest), placement.heightLimit() / (totalHeight * placement.scale()));
        Matrix4f base = new Matrix4f((Matrix4fc)placement.matrix()).scale(fit);
        float opacity = placement.alpha() * this.worldStyle.opacity.getFloat() / 100.0f;
        float gap = this.worldStyle.rowGap.getFloat();
        float offset = 0.0f;
        for (int row = 0; row < glyphRows.size(); ++row) {
            List glyphs = (List)glyphRows.get(row);
            float width = LyricFx.totalWidth(glyphs) * 9.0f;
            float rowFill = fill < 0.0f ? -1.0f : WorldLyricMotion.clamp((fill * weight - offset) / Math.max(1.0f, width));
            WorldLyricMotion.animate(glyphs, (String)this.lyricsFx.get(), (String)this.worldStyle.exit.get(), age, exiting, rowFill, this.worldStyle.entryTime.getFloat(), this.worldStyle.exitTime.getFloat(), this.worldStyle.intensity.getFloat() / 100.0f, colors, time, (Boolean)this.sway.get(), (String)this.worldStyle.idle.get(), this.worldStyle.motionAmount.getFloat() / 100.0f, this.worldStyle.motionSpeed.getFloat() / 100.0f);
            float rowY = ((float)row - (float)(rows.size() - 1) / 2.0f) * gap;
            this.drawWorldGlyphs(imm, base, glyphs, rowY, opacity, glowColor);
            if (!exiting && this.lyricsFx.is("\u0423\u0434\u0430\u0440 \u043c\u043e\u043b\u043d\u0438\u0438")) {
                int boltColor = (Boolean)this.worldStyle.lightningTextColor.get() != false ? glowColor : this.worldStyle.lightningColor.getColor();
                LyricLightning.draw(imm, base, glyphs, rowY, age, this.worldStyle.entryTime.getFloat(), this.worldStyle.lightningTime.getFloat(), this.worldStyle.lightningHeight.getFloat(), this.worldStyle.lightningWidth.getFloat(), this.worldStyle.lightningBranches.getInt(), boltColor, opacity * this.worldStyle.lightningBrightness.getFloat() / 100.0f, (Boolean)this.throughBlocks.get());
            }
            offset += width;
        }
        float bodyAlpha = exiting ? 1.0f - WorldLyricMotion.clamp(age / this.worldStyle.exitTime.getFloat()) : WorldLyricMotion.clamp(age / Math.min(0.4f, this.worldStyle.entryTime.getFloat()));
        float bottom = (float)(rows.size() - 1) * gap / 2.0f + 8.0f;
        if (!exiting) {
            this.drawWorldDecor(imm, base, widest, bottom, glowColor, opacity * bodyAlpha, age, time);
            if (((Boolean)this.nextLine.get()).booleanValue() && !((String)next).isBlank()) {
                float nextSize = this.worldStyle.nextScale.getFloat() / 100.0f;
                String line = WorldLyricMotion.wrap((String)next, limit / nextSize, 1, value -> NowPlayingModule.mc.font.width((FormattedText)Fonts.text(value, false))).stream().findFirst().orElse("");
                Component rendered = Fonts.text(line, false);
                Matrix4f nm = new Matrix4f((Matrix4fc)base).translate(0.0f, bottom + gap * 0.65f, 0.0f).scale(nextSize);
                float nextAlpha = this.worldStyle.nextOpacity.getFloat() / 100.0f * opacity * bodyAlpha;
                if (nextAlpha < 0.025f) {
                    return;
                }
                float nextX = (float)(-NowPlayingModule.mc.font.width((FormattedText)rendered)) / 2.0f;
                if (((Boolean)this.worldStyle.outline.get()).booleanValue()) {
                    float[][] border = new float[][]{{-0.55f, 0.0f}, {0.55f, 0.0f}, {0.0f, -0.55f}, {0.0f, 0.55f}};
                    Matrix4f borderMatrix = new Matrix4f((Matrix4fc)nm).translate(0.0f, 0.0f, 0.08f);
                    for (float[] at : border) {
                        WorldBuffers.drawText(NowPlayingModule.mc.font, rendered, nextX + at[0], at[1], ColorUtil.withAlpha(-16185838, nextAlpha), false, borderMatrix, (WorldBuffers)imm, this.layerType(), 0, 0xF000F0);
                    }
                    imm.endBatch();
                }
                WorldBuffers.drawText(NowPlayingModule.mc.font, rendered, nextX, 0.0f, ColorUtil.withAlpha(-1, nextAlpha), ((Boolean)this.worldStyle.shadow.get()).booleanValue(), nm, (WorldBuffers)imm, this.layerType(), 0, 0xF000F0);
            }
        }
    }

    private void drawWorldGlyphs(WorldBuffers.BufferSource imm, Matrix4f base, List<LyricFx.Glyph> glyphs, float y, float opacity, int glowColor) {
        int pass;
        Font tr = NowPlayingModule.mc.font;
        float total = LyricFx.totalWidth(glyphs) * 9.0f;
        int light = 0xF000F0;
        Font.DisplayMode layer = this.layerType();
        boolean luminous = (Boolean)this.glow.get() != false && this.worldStyle.glowStrength.getFloat() > 0.0f;
        float radius = this.worldStyle.glowRadius.getFloat();
        float[][] offsets = new float[][]{{-radius, 0.0f}, {radius, 0.0f}, {0.0f, -radius}, {0.0f, radius}, {-radius, -radius}, {radius, radius}, {-radius, radius}, {radius, -radius}};
        int n = pass = luminous ? 0 : 1;
        while (pass < 3) {
            if (pass != 1 || ((Boolean)this.worldStyle.outline.get()).booleanValue()) {
                for (LyricFx.Glyph glyph : glyphs) {
                    float alpha = opacity * glyph.alpha * (float)ColorUtil.alpha(glyph.color) / 255.0f;
                    if (alpha < 0.025f || glyph.ch.isBlank()) continue;
                    Component text = Fonts.text(glyph.ch, (Boolean)this.worldStyle.bold.get());
                    float half = (float)tr.width((FormattedText)text) / 2.0f;
                    Matrix4f matrix = new Matrix4f((Matrix4fc)base).translate((glyph.x + glyph.w / 2.0f + glyph.dx) * 9.0f - total / 2.0f, y + glyph.dy * 9.0f, 0.0f).rotateZ(glyph.rot).scale(glyph.scale);
                    if (pass == 0) {
                        float a = alpha * Math.min(0.6f, 0.28f * glyph.glow * this.worldStyle.glowStrength.getFloat() / 100.0f);
                        if (a < 0.025f) continue;
                        int color = ColorUtil.withAlpha(ColorUtil.lerp(glowColor, glyph.color, 0.3f), a);
                        for (float[] offset : offsets) {
                            WorldBuffers.drawText(tr, text, -half + offset[0], -4.5f + offset[1], color, false, matrix, (WorldBuffers)imm, layer, 0, light);
                        }
                        continue;
                    }
                    if (pass == 1) {
                        int edge = ColorUtil.withAlpha(-16185838, alpha);
                        float[][] border = new float[][]{{-0.55f, 0.0f}, {0.55f, 0.0f}, {0.0f, -0.55f}, {0.0f, 0.55f}};
                        Matrix4f borderMatrix = new Matrix4f((Matrix4fc)matrix).translate(0.0f, 0.0f, 0.08f);
                        for (float[] at : border) {
                            WorldBuffers.drawText(tr, text, -half + at[0], -4.5f + at[1], edge, false, borderMatrix, (WorldBuffers)imm, layer, 0, light);
                        }
                        continue;
                    }
                    WorldBuffers.drawText(tr, text, -half, -4.5f, ColorUtil.withAlpha(glyph.color, alpha), ((Boolean)this.worldStyle.shadow.get()).booleanValue(), matrix, (WorldBuffers)imm, layer, 0, light);
                }
                imm.endBatch();
            }
            ++pass;
        }
    }

    private void worldMark(WorldBuffers.BufferSource imm, Matrix4f base, String text, float x, float y, float size, float rotation, int color) {
        if (ColorUtil.alpha(color) < 6) {
            return;
        }
        Component rendered = Fonts.text(text, false);
        Matrix4f matrix = new Matrix4f((Matrix4fc)base).translate(x, y, -0.02f).rotateZ(rotation).scale(size);
        WorldBuffers.drawText(NowPlayingModule.mc.font, rendered, (float)(-NowPlayingModule.mc.font.width((FormattedText)rendered)) / 2.0f, -4.5f, color, false, matrix, (WorldBuffers)imm, this.layerType(), 0, 0xF000F0);
    }

    private void drawWorldDecor(WorldBuffers.BufferSource imm, Matrix4f base, float width, float bottom, int color, float alpha, float age, float time) {
        int tint = ColorUtil.withAlpha(color, alpha * 0.65f);
        switch ((String)this.worldStyle.ornament.get()) {
            case "\u041b\u0438\u043d\u0438\u044f": {
                String line = "\u2500".repeat(Math.max(2, Math.min(48, (int)(width / 6.0f))));
                float natural = Math.max(1, NowPlayingModule.mc.font.width((FormattedText)Fonts.text(line, false)));
                this.worldMark(imm, base, line, 0.0f, bottom, Math.min(1.0f, width / natural), 0.0f, tint);
                break;
            }
            case "\u0421\u043a\u043e\u0431\u043a\u0438": {
                this.worldMark(imm, base, "[", -width / 2.0f - 8.0f, 0.0f, 1.4f, 0.0f, tint);
                this.worldMark(imm, base, "]", width / 2.0f + 8.0f, 0.0f, 1.4f, 0.0f, tint);
                break;
            }
            case "\u0417\u0432\u0451\u0437\u0434\u044b": {
                this.worldMark(imm, base, "\u2726", -width / 2.0f - 9.0f, 0.0f, 1.2f, time * 0.2f, tint);
                this.worldMark(imm, base, "\u2726", width / 2.0f + 9.0f, 0.0f, 1.2f, -time * 0.2f, tint);
                break;
            }
        }
        if (this.worldStyle.particles.is("\u041d\u0435\u0442")) {
            return;
        }
        int count = this.worldStyle.particleCount.getInt();
        for (int i = 0; i < count; ++i) {
            float phase = (time * 0.18f + (float)i * 0.618034f) % 1.0f;
            float x = ((float)i * 0.754877f % 1.0f - 0.5f) * (width + 24.0f);
            float y = bottom + 8.0f - phase * (bottom + 28.0f);
            String symbol = "\u00b7";
            float rotation = 0.0f;
            if (this.worldStyle.particles.is("\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438")) {
                symbol = "\u2740";
                y = -18.0f + phase * (bottom + 30.0f);
                x = (float)((double)x + Math.sin((double)phase * 6.28 + (double)i) * 5.0);
                rotation = phase * 2.0f + (float)i;
            } else if (this.worldStyle.particles.is("\u041d\u043e\u0442\u044b")) {
                symbol = "\u266a";
            }
            float a = (float)Math.sin((double)phase * Math.PI) * alpha * 0.55f;
            this.worldMark(imm, base, symbol, x, y, 0.35f + (float)(i % 3) * 0.1f, rotation, ColorUtil.withAlpha(color, a));
        }
    }

    private Font.DisplayMode layerType() {
        return (Boolean)this.throughBlocks.get() != false ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
    }

    public record HistoryEntry(String title, String artist, String app, String cover) {
    }

    public record BannerLayout(NowPlaying track, List<LyricFx.Glyph> glyphs, float titleSize, String subtitle, float width) {
    }

    private record WorldPlacement(Matrix4f matrix, float scale, float alpha, float widthLimit, float heightLimit) {
    }

    private record WorldWrapKey(String text, int width, int rows, boolean bold, float tracking, Object font) {
    }
}

