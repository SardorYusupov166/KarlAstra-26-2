/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 */
package astra.hud;

import astra.AstraClient;
import astra.decor.DecorManager;
import astra.gui.screens.HudEditorScreen;
import astra.hud.HudModule;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.pvp.TargetEffects;
import astra.module.modules.visual.NowPlayingModule;
import astra.module.modules.visual.VisualizerModule;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class HudManager {
    private HudManager() {
    }

    public static void render(GuiGraphics ctx, DeltaTracker tickCounter) {
        TargetEffects fx;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) {
            return;
        }
        if (mc.screen instanceof HudEditorScreen) {
            return;
        }
        float w = ctx.guiWidth();
        float h = ctx.guiHeight();
        DecorManager.renderScreen(ctx, "\u041f\u043e\u0434 HUD", w, h);
        VisualizerModule viz = VisualizerModule.get();
        if (viz != null && viz.isEnabled()) {
            try {
                viz.renderOverlay(ctx);
            }
            catch (RuntimeException e) {
                AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u0432\u0438\u0437\u0443\u0430\u043b\u0438\u0437\u0430\u0442\u043e\u0440\u0430", (Throwable)e);
                viz.setEnabled(false);
            }
        }
        if ((fx = TargetEffects.get()) != null) {
            fx.renderHud(ctx);
        }
        for (HudModule hud : ModuleManager.hudModules()) {
            if (!hud.isEnabled() || !hud.shouldRender()) continue;
            try {
                hud.render(ctx, w, h, false);
            }
            catch (RuntimeException e) {
                AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 HUD {}", (Object)hud.getId(), (Object)e);
                hud.setEnabled(false);
            }
        }
        NowPlayingModule nowPlaying = NowPlayingModule.get();
        if (nowPlaying != null && nowPlaying.isEnabled()) {
            try {
                nowPlaying.renderOverlay(ctx);
            }
            catch (RuntimeException e) {
                AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u00ab\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442\u00bb", (Throwable)e);
            }
        }
        DecorManager.renderScreen(ctx, "\u041f\u043e\u0432\u0435\u0440\u0445 HUD", w, h);
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.renderHud(ctx);
        }
    }
}

