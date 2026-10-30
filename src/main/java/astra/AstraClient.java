/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.brigadier.CommandDispatcher
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
 *  net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
 *  net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
 *  net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
 *  net.fabricmc.fabric.api.event.player.AttackEntityCallback
 *  net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
 *  net.fabricmc.fabric.api.resource.ResourceManagerHelper
 *  net.fabricmc.fabric.api.resource.ResourcePackActivationType
 *  net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
 *  net.fabricmc.loader.api.FabricLoader
 *  net.fabricmc.loader.api.ModContainer
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.KeyMapping$Category
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.world.InteractionResult
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package astra;

import astra.builder.CanvasModule;
import astra.config.ConfigManager;
import astra.cosmetic.CosmeticRenderer;
import astra.gui.screens.ClickGuiScreen;
import astra.gui.screens.HudEditorScreen;
import astra.gui.style.NativeScreenLayouts;
import astra.gui.vanilla.MenuStyle;
import astra.hud.HudManager;
import astra.media.MediaBridge;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.CompanionHud;
import astra.module.modules.hud.CustomHotbar;
import astra.module.modules.hud.ScoreboardHud;
import astra.module.modules.hud.StatusBars;
import astra.module.modules.minimap.WaypointsModule;
import astra.module.modules.pvp.CrosshairModule;
import astra.module.modules.pvp.PotionHud;
import astra.module.modules.pvp.TargetEffects;
import astra.module.modules.visual.NowPlayingModule;
import astra.particle.AstraParticles;
import astra.render.Fonts;
import astra.render.LyricLightning;
import astra.sound.user.SoundPacks;
import astra.utils.CombatTracker;
import astra.utils.DevShots;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.brigadier.CommandDispatcher;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AstraClient
implements ClientModInitializer {
    private static final KeyMapping.Category ASTRA_KEYS = KeyMapping.Category.register((Identifier)Identifier.fromNamespaceAndPath((String)"astra", (String)"main"));
    public static final String MOD_ID = "astra";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"Astra");
    public static KeyMapping openGuiKey;
    public static KeyMapping hudEditorKey;
    private static final Map<Module, Boolean> KEY_STATE;

    public void onInitializeClient() {
        LyricLightning.init();
        ModuleManager.init();
        CanvasModule.init();
        ConfigManager.init();
        openGuiKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.astra.clickgui", InputConstants.Type.KEYSYM, 344, ASTRA_KEYS));
        hudEditorKey = KeyMappingHelper.registerKeyMapping((KeyMapping)new KeyMapping("key.astra.hudeditor", InputConstants.Type.KEYSYM, -1, ASTRA_KEYS));
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save());
        HudElementRegistry.attachElementBefore((Identifier)VanillaHudElements.CHAT, (Identifier)Identifier.fromNamespaceAndPath((String)MOD_ID, (String)"hud"), HudManager::render);
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.CROSSHAIR, vanilla -> (ctx, tick) -> {
            CrosshairModule crosshair = ModuleManager.get(CrosshairModule.class);
            if (crosshair != null && crosshair.isEnabled()) {
                crosshair.renderCrosshair(ctx, tick);
            } else {
                vanilla.extractRenderState(ctx, tick);
            }
        });
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.HOTBAR, vanilla -> (ctx, tick) -> {
            CustomHotbar hotbar = ModuleManager.get(CustomHotbar.class);
            if (hotbar == null || !hotbar.isEnabled() || !hotbar.canReplace()) {
                vanilla.extractRenderState(ctx, tick);
            }
        });
        for (Identifier bar : new Identifier[]{VanillaHudElements.HEALTH_BAR, VanillaHudElements.ARMOR_BAR, VanillaHudElements.FOOD_BAR, VanillaHudElements.AIR_BAR, VanillaHudElements.MOUNT_HEALTH}) {
            HudElementRegistry.replaceElement((Identifier)bar, vanilla -> (ctx, tick) -> {
                if (!StatusBars.replacing()) {
                    vanilla.extractRenderState(ctx, tick);
                }
            });
        }
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.INFO_BAR, vanilla -> (ctx, tick) -> {
            if (!StatusBars.replacingXp()) {
                vanilla.extractRenderState(ctx, tick);
            }
        });
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.EXPERIENCE_LEVEL, vanilla -> (ctx, tick) -> {
            if (!StatusBars.replacingXp()) {
                vanilla.extractRenderState(ctx, tick);
            }
        });
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.SCOREBOARD, vanilla -> (ctx, tick) -> {
            if (!ScoreboardHud.replacing()) {
                vanilla.extractRenderState(ctx, tick);
            }
        });
        HudElementRegistry.replaceElement((Identifier)VanillaHudElements.MOB_EFFECTS, vanilla -> (ctx, tick) -> {
            PotionHud potions = ModuleManager.get(PotionHud.class);
            if (potions == null || !potions.hidesVanilla()) {
                vanilla.extractRenderState(ctx, tick);
            }
        });
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClientSide()) {
                CombatTracker.onAttack(entity);
            }
            return InteractionResult.PASS;
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SoundsModule sounds = SoundsModule.get();
            if (sounds != null) {
                sounds.onJoin();
            }
            client.execute(CompanionHud::onJoin);
        });
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack((Identifier)Identifier.fromNamespaceAndPath((String)MOD_ID, (String)"astra_pink_glint"), (ModContainer)container, (Component)Component.literal((String)"Astra: \u0440\u043e\u0437\u043e\u0432\u043e\u0435 \u0441\u0438\u044f\u043d\u0438\u0435 \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u0439"), (ResourcePackActivationType)ResourcePackActivationType.DEFAULT_ENABLED));
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            TargetEffects fx = TargetEffects.get();
            if (fx == null) {
                return;
            }
            try {
                fx.renderWorld(context);
            }
            catch (RuntimeException e) {
                LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u044d\u0444\u0444\u0435\u043a\u0442\u043e\u0432 \u0446\u0435\u043b\u0438", (Throwable)e);
            }
        });
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            NowPlayingModule np = NowPlayingModule.get();
            if (np == null || !np.isEnabled()) {
                return;
            }
            try {
                np.renderWorld(context);
            }
            catch (RuntimeException e) {
                LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u0442\u0435\u043a\u0441\u0442\u0430 \u043f\u0435\u0441\u043d\u0438 \u0432 \u043c\u0438\u0440\u0435", (Throwable)e);
            }
        });
        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            WaypointsModule wp = WaypointsModule.get();
            if (wp == null || !wp.isEnabled()) {
                return;
            }
            try {
                wp.renderWorld(context);
            }
            catch (RuntimeException e) {
                LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u0442\u043e\u0447\u0435\u043a \u043c\u0430\u0440\u0448\u0440\u0443\u0442\u0430", (Throwable)e);
            }
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> WaypointsModule.registerCommands((CommandDispatcher<FabricClientCommandSource>)dispatcher));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> MediaBridge.stop());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(() -> {
            SoundsModule sounds = SoundsModule.get();
            if (sounds != null) {
                sounds.onLeave();
            }
        }));
        CosmeticRenderer.init();
        MenuStyle.init();
        NativeScreenLayouts.init();
        ResourceManagerHelper.get((PackType)PackType.CLIENT_RESOURCES).registerReloadListener((IdentifiableResourceReloadListener)new SimpleSynchronousResourceReloadListener(){

            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath((String)AstraClient.MOD_ID, (String)"font_cache");
            }

            public void onResourceManagerReload(ResourceManager manager) {
                Fonts.clearCache();
            }
        });
        AnimeChatModule.init();
        AstraParticles.init();
        DevShots.init();
        astra.utils.ClientSmokeTest.init();
        LOGGER.info("Astra Anime Client \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d: {} \u043c\u043e\u0434\u0443\u043b\u0435\u0439", (Object)ModuleManager.all().size());
    }

    private void onTick(Minecraft client) {
        while (openGuiKey.consumeClick()) {
            if (client.gui.screen() != null) continue;
            client.gui.setScreen((Screen)new ClickGuiScreen(null));
        }
        while (hudEditorKey.consumeClick()) {
            if (client.gui.screen() != null) continue;
            client.gui.setScreen((Screen)new HudEditorScreen(null));
        }
        this.handleModuleKeys(client);
        CombatTracker.tick(client);
        SoundPacks.tick();
        for (Module module : ModuleManager.all()) {
            if (!module.isEnabled()) continue;
            try {
                module.onTick();
            }
            catch (RuntimeException e) {
                LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u043c\u043e\u0434\u0443\u043b\u0435 {}", (Object)module.getId(), (Object)e);
            }
        }
        ConfigManager.tick();
    }

    private void handleModuleKeys(Minecraft client) {
        Window window = client.getWindow();
        boolean canUse = client.gui.screen() == null;
        for (Module module : ModuleManager.all()) {
            if (!module.hasToggleKey() || !module.isToggleable() || !module.usesToggleKey()) continue;
            boolean down = canUse && InputConstants.isKeyDown((Window)window, (int)module.getKeybind().getKey());
            boolean was = KEY_STATE.getOrDefault(module, false);
            if (down && !was) {
                module.toggle();
            }
            KEY_STATE.put(module, down);
        }
    }

    static {
        KEY_STATE = new HashMap<Module, Boolean>();
    }
}

