/*
 * Decompiled with CFR 0.152.
 */
package astra.module;

import astra.builder.CanvasModule;
import astra.hud.HudEditorPrefs;
import astra.hud.HudModule;
import astra.module.Category;
import astra.module.ClientSettings;
import astra.module.Module;
import astra.module.modules.cosmetic.AuraCosmetic;
import astra.module.modules.cosmetic.CapeCosmetic;
import astra.module.modules.cosmetic.EmoteCosmetic;
import astra.module.modules.cosmetic.HaloCosmetic;
import astra.module.modules.cosmetic.OnlineModule;
import astra.module.modules.cosmetic.ProfileStyle;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.cosmetic.TrailCosmetic;
import astra.module.modules.cosmetic.VictoryEffect;
import astra.module.modules.cosmetic.WingsCosmetic;
import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.ChatInputHud;
import astra.module.modules.hud.ChatLayoutHud;
import astra.module.modules.hud.CompanionHud;
import astra.module.modules.hud.CoordinatesHud;
import astra.module.modules.hud.CustomHotbar;
import astra.module.modules.hud.DirectionHud;
import astra.module.modules.hud.FpsGraphHud;
import astra.module.modules.hud.FpsHud;
import astra.module.modules.hud.MemoryHud;
import astra.module.modules.hud.PingHud;
import astra.module.modules.hud.ScoreboardHud;
import astra.module.modules.hud.SneakIndicator;
import astra.module.modules.hud.SprintIndicator;
import astra.module.modules.hud.StatusBars;
import astra.module.modules.hud.TabLayoutHud;
import astra.module.modules.hud.TrackBannerHud;
import astra.module.modules.hud.WatermarkHud;
import astra.module.modules.minimap.MinimapHud;
import astra.module.modules.minimap.WaypointsModule;
import astra.module.modules.movement.ArmAnimation;
import astra.module.modules.movement.AutoJump;
import astra.module.modules.movement.CameraModule;
import astra.module.modules.movement.FovModule;
import astra.module.modules.movement.PerspectiveModule;
import astra.module.modules.movement.SprintFov;
import astra.module.modules.movement.ViewBobbing;
import astra.module.modules.movement.ZoomModule;
import astra.module.modules.performance.BenchmarkModule;
import astra.module.modules.performance.PerformanceModule;
import astra.module.modules.pvp.ArmorHud;
import astra.module.modules.pvp.ComboHud;
import astra.module.modules.pvp.CooldownHud;
import astra.module.modules.pvp.CpsHud;
import astra.module.modules.pvp.CrosshairModule;
import astra.module.modules.pvp.DamageIndicatorHud;
import astra.module.modules.pvp.HitParticles;
import astra.module.modules.pvp.ItemCounterHud;
import astra.module.modules.pvp.KeybindsModule;
import astra.module.modules.pvp.KeystrokesHud;
import astra.module.modules.pvp.PotionHud;
import astra.module.modules.pvp.PvpTweaks;
import astra.module.modules.pvp.ShieldStatus;
import astra.module.modules.pvp.TargetEffects;
import astra.module.modules.pvp.TargetHud;
import astra.module.modules.replay.ReplayModule;
import astra.module.modules.replay.ScreenshotModule;
import astra.module.modules.visual.AmbientParticles;
import astra.module.modules.visual.ColorFilter;
import astra.module.modules.visual.EnchantShimmer;
import astra.module.modules.visual.MusicModule;
import astra.module.modules.visual.NowPlayingModule;
import astra.module.modules.visual.ShadersModule;
import astra.module.modules.visual.VisualizerModule;
import astra.module.modules.visual.WorldVisuals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<Module>();
    private static final Map<Class<?>, Module> BY_CLASS = new HashMap();

    private ModuleManager() {
    }

    public static void init() {
        ModuleManager.register(new ClientSettings());
        ModuleManager.register(new HudEditorPrefs());
        ModuleManager.register(new WatermarkHud());
        ModuleManager.register(new FpsHud());
        ModuleManager.register(new PingHud());
        ModuleManager.register(new CustomHotbar());
        ModuleManager.register(new StatusBars());
        ModuleManager.register(new ScoreboardHud());
        ModuleManager.register(new CompanionHud());
        ModuleManager.register(new AnimeChatModule());
        ModuleManager.register(new ChatLayoutHud());
        ModuleManager.register(new ChatInputHud());
        ModuleManager.register(new TabLayoutHud());
        ModuleManager.register(new ShadersModule());
        ModuleManager.register(new AmbientParticles());
        ModuleManager.register(new ColorFilter());
        ModuleManager.register(new WorldVisuals());
        ModuleManager.register(new MusicModule());
        ModuleManager.register(new NowPlayingModule());
        ModuleManager.register(new TrackBannerHud());
        ModuleManager.register(new VisualizerModule());
        ModuleManager.register(new EnchantShimmer());
        ModuleManager.register(new KeystrokesHud());
        ModuleManager.register(new CpsHud());
        ModuleManager.register(new CrosshairModule());
        ModuleManager.register(new ComboHud());
        ModuleManager.register(new TargetHud());
        ModuleManager.register(new ArmorHud());
        ModuleManager.register(new PotionHud());
        ModuleManager.register(new DamageIndicatorHud());
        ModuleManager.register(new HitParticles());
        ModuleManager.register(new PvpTweaks());
        ModuleManager.register(new ShieldStatus());
        ModuleManager.register(new KeybindsModule());
        ModuleManager.register(new CooldownHud());
        ModuleManager.register(new ItemCounterHud());
        ModuleManager.register(new TargetEffects());
        ModuleManager.register(new ZoomModule());
        ModuleManager.register(new FovModule());
        ModuleManager.register(new SprintFov());
        ModuleManager.register(new ViewBobbing());
        ModuleManager.register(new CameraModule());
        ModuleManager.register(new PerspectiveModule());
        ModuleManager.register(new AutoJump());
        ModuleManager.register(new ArmAnimation());
        ModuleManager.register(new SprintIndicator());
        ModuleManager.register(new SneakIndicator());
        ModuleManager.register(new DirectionHud());
        ModuleManager.register(new CoordinatesHud());
        ModuleManager.register(new CapeCosmetic());
        ModuleManager.register(new WingsCosmetic());
        ModuleManager.register(new HaloCosmetic());
        ModuleManager.register(new AuraCosmetic());
        ModuleManager.register(new TrailCosmetic());
        ModuleManager.register(new EmoteCosmetic());
        ModuleManager.register(new VictoryEffect());
        ModuleManager.register(new ProfileStyle());
        ModuleManager.register(new SoundsModule());
        ModuleManager.register(new OnlineModule());
        ModuleManager.register(new PerformanceModule());
        ModuleManager.register(new BenchmarkModule());
        ModuleManager.register(new FpsGraphHud());
        ModuleManager.register(new MemoryHud());
        ModuleManager.register(new MinimapHud());
        ModuleManager.register(new WaypointsModule());
        ModuleManager.register(new CanvasModule());
        ModuleManager.register(new ScreenshotModule());
        ModuleManager.register(new ReplayModule());
    }

    private static void register(Module module) {
        MODULES.add(module);
        BY_CLASS.put(module.getClass(), module);
    }

    public static List<Module> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static List<Module> byCategory(Category category) {
        ArrayList<Module> list = new ArrayList<Module>();
        for (Module m : MODULES) {
            if (m.getCategory() != category) continue;
            list.add(m);
        }
        return list;
    }

    public static List<HudModule> hudModules() {
        ArrayList<HudModule> list = new ArrayList<HudModule>();
        for (Module m : MODULES) {
            if (!(m instanceof HudModule)) continue;
            HudModule h = (HudModule)m;
            list.add(h);
        }
        return list;
    }

    public static <T extends Module> T get(Class<T> type) {
        return (T)BY_CLASS.get(type);
    }

    public static Module byId(String id) {
        for (Module m : MODULES) {
            if (!m.getId().equals(id)) continue;
            return m;
        }
        return null;
    }
}

