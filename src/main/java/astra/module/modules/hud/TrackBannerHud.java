/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 */
package astra.module.modules.hud;

import astra.gui.screens.HudEditorScreen;
import astra.hud.HudModule;
import astra.media.LyricFx;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.visual.NowPlayingModule;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import net.minecraft.client.gui.GuiGraphics;

public final class TrackBannerHud
extends HudModule {
    private final ModeSetting panel = this.add(new ModeSetting("\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435 \u043f\u043b\u0430\u0448\u043a\u0438", "\u0411\u0435\u0437 \u043f\u0430\u043d\u0435\u043b\u0438", "\u0411\u0435\u0437 \u043f\u0430\u043d\u0435\u043b\u0438", "\u041f\u0430\u043d\u0435\u043b\u044c"));
    private final ModeSetting effect = this.add((ModeSetting)new ModeSetting("\u041f\u043e\u044f\u0432\u043b\u0435\u043d\u0438\u0435 \u043f\u043b\u0430\u0448\u043a\u0438", "\u0412\u043e\u043b\u043d\u0430", LyricFx.EFFECTS).bind(() -> TrackBannerHud.player().bannerEffect(), v -> TrackBannerHud.player().bannerEffect((String)v)));
    private final NumberSetting duration = this.add((NumberSetting)new NumberSetting("\u0412\u0440\u0435\u043c\u044f \u043f\u043e\u043a\u0430\u0437\u0430 \u043f\u043b\u0430\u0448\u043a\u0438", 5.0, 2.0, 15.0, 0.5).unit(" \u0441").bind(() -> TrackBannerHud.player().bannerDuration(), v -> TrackBannerHud.player().bannerDuration((double)v)));

    public TrackBannerHud() {
        super("track_banner", "\u041f\u043b\u0430\u0448\u043a\u0430 \u0441\u043c\u0435\u043d\u044b \u0442\u0440\u0435\u043a\u0430", "\u0411\u043e\u043b\u044c\u0448\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043d\u043e\u0432\u043e\u0439 \u043f\u0435\u0441\u043d\u0438 \u0441 \u043e\u0431\u043b\u043e\u0436\u043a\u043e\u0439: \u0434\u0432\u0438\u0433\u0430\u0439\u0442\u0435 \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u043e \u043e\u0442 \u0432\u0438\u0434\u0436\u0435\u0442\u0430 \u00ab\u0421\u0435\u0439\u0447\u0430\u0441 \u0438\u0433\u0440\u0430\u0435\u0442\u00bb.", Category.HUD, Icon.WAVE, 0.5f, 0.18f);
        this.scale.bind(() -> TrackBannerHud.player().bannerSize(), v -> TrackBannerHud.player().bannerSize((double)v));
        this.themeAccent.desc("\u0412\u043a\u043b\u044e\u0447\u0435\u043d\u043e \u2014 \u0446\u0432\u0435\u0442\u0430 \u043e\u0431\u043b\u043e\u0436\u043a\u0438/\u043f\u043b\u0435\u0435\u0440\u0430; \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u043e \u2014 \u0441\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0430\u043a\u0446\u0435\u043d\u0442\u0430 \u043f\u043b\u0430\u0448\u043a\u0438.");
        this.setDefaultEnabled(true);
    }

    public static TrackBannerHud get() {
        return ModuleManager.get(TrackBannerHud.class);
    }

    private static NowPlayingModule player() {
        return NowPlayingModule.get();
    }

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    public float width() {
        return TrackBannerHud.player().bannerLayout(TrackBannerHud.mc.screen instanceof HudEditorScreen || !TrackBannerHud.player().bannerVisible(), this.getScale()).width();
    }

    @Override
    public float height() {
        return 54.0f;
    }

    @Override
    public float getX(float sw) {
        if (!this.isAtDefault()) {
            return super.getX(sw);
        }
        return Math.max(0.0f, Math.min(sw - this.scaledWidth(), TrackBannerHud.player().bannerDefaultX(sw, this.scaledWidth(), this.getScale())));
    }

    @Override
    public float getY(float sh) {
        if (!this.isAtDefault()) {
            return super.getY(sh);
        }
        return Math.max(0.0f, Math.min(sh - this.scaledHeight(), TrackBannerHud.player().bannerDefaultY(sh, this.getScale())));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        int[] nArray;
        if (!editor && !TrackBannerHud.player().bannerVisible()) {
            return;
        }
        NowPlayingModule.BannerLayout layout = TrackBannerHud.player().bannerLayout(editor, this.getScale());
        if (this.panel.is("\u041f\u0430\u043d\u0435\u043b\u044c") && ((Boolean)this.background.get()).booleanValue()) {
            this.drawPanel(ctx, 0.0f, 0.0f, w, h, 8.0f);
        }
        if (((Boolean)this.themeAccent.get()).booleanValue()) {
            nArray = TrackBannerHud.player().colors(layout.track());
        } else {
            int[] nArray2 = new int[2];
            nArray2[0] = this.accent.getColor();
            nArray = nArray2;
            nArray2[1] = ColorUtil.brighten(this.accent.getColor(), 0.2f);
        }
        int[] colors = nArray;
        float previous = Render2D.alpha;
        Render2D.alpha *= this.opacity.getFloat() / 100.0f;
        try {
            TrackBannerHud.player().drawTrackBanner(ctx, layout, editor, colors);
        }
        finally {
            Render2D.alpha = previous;
        }
    }
}

