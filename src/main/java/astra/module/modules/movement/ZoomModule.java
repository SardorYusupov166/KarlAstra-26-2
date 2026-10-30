/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 */
package astra.module.modules.movement;

import astra.animation.Animation;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.movement.CameraModule;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.utils.FrameStats;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;

public class ZoomModule
extends Module {
    private final KeybindSetting key = this.add(new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430", 67));
    private final ModeSetting mode = this.add(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u0423\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435", "\u0423\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435", "\u041f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435"));
    private final NumberSetting strength = this.add(new NumberSetting("\u0421\u0438\u043b\u0430 \u0437\u0443\u043c\u0430", 4.0, 1.5, 12.0, 0.5).unit("x"));
    private final BooleanSetting smooth = this.add(new BooleanSetting("\u041f\u043b\u0430\u0432\u043d\u043e\u0441\u0442\u044c", true));
    private final BooleanSetting scroll = this.add(new BooleanSetting("\u041a\u043e\u043b\u0451\u0441\u0438\u043a\u043e \u043c\u0435\u043d\u044f\u0435\u0442 \u0437\u0443\u043c", true));
    private final BooleanSetting cinematic = this.add(new BooleanSetting("\u041a\u0438\u043d\u043e\u043a\u0430\u043c\u0435\u0440\u0430 \u043f\u0440\u0438 \u0437\u0443\u043c\u0435", false));
    private final BooleanSetting sensitivity = this.add(new BooleanSetting("\u0421\u043d\u0438\u0436\u0430\u0442\u044c \u0447\u0443\u0432\u0441\u0442\u0432\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c", true));
    private final Animation zoom = new Animation(1.0f, 12.0f);
    private boolean active;
    private boolean wasDown;
    private boolean wasActive;
    private float scrollFactor = 1.0f;
    private boolean cinematicApplied;

    public ZoomModule() {
        super("zoom", "\u041f\u043b\u0430\u0432\u043d\u044b\u0439 \u0437\u0443\u043c", "\u041f\u043b\u0430\u0432\u043d\u043e\u0435 \u043f\u0440\u0438\u0431\u043b\u0438\u0436\u0435\u043d\u0438\u0435 \u043a\u0430\u043c\u0435\u0440\u044b \u0434\u043b\u044f \u043b\u0443\u0447\u0448\u0435\u0433\u043e \u043e\u0431\u0437\u043e\u0440\u0430.", Category.MOVEMENT, Icon.ZOOM);
        this.setDefaultEnabled(true);
    }

    @Override
    public void onTick() {
        boolean wantCinematic;
        SoundsModule sounds;
        boolean down;
        boolean bl = down = ZoomModule.mc.field_1755 == null && this.key.isBound() && InputConstants.method_15987((Window)mc.method_22683(), (int)this.key.getKey());
        if (this.mode.is("\u0423\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435")) {
            this.active = down;
        } else if (down && !this.wasDown) {
            this.active = !this.active;
        }
        this.wasDown = down;
        if (this.active && !this.wasActive && (sounds = SoundsModule.get()) != null) {
            sounds.onZoom();
        }
        this.wasActive = this.active;
        if (!this.active) {
            this.scrollFactor = 1.0f;
        }
        boolean bl2 = wantCinematic = this.active && (Boolean)this.cinematic.get() != false;
        if (wantCinematic != this.cinematicApplied) {
            CameraModule camera = ModuleManager.get(CameraModule.class);
            ZoomModule.mc.field_1690.field_1914 = wantCinematic || camera != null && camera.isEnabled();
            this.cinematicApplied = wantCinematic;
        }
    }

    @Override
    protected void onDisable() {
        this.active = false;
    }

    public float currentFactor() {
        float target;
        float f = target = this.active ? this.strength.getFloat() * this.scrollFactor : 1.0f;
        if (!((Boolean)this.smooth.get()).booleanValue()) {
            this.zoom.set(target);
            return target;
        }
        return this.zoom.update(target, FrameStats.delta());
    }

    public boolean isZooming() {
        return this.isEnabled() && this.active;
    }

    public boolean onScroll(double amount) {
        if (!this.isZooming() || !((Boolean)this.scroll.get()).booleanValue()) {
            return false;
        }
        this.scrollFactor = (float)Math.max(0.5, Math.min(4.0, (double)this.scrollFactor * (amount > 0.0 ? 1.15 : 0.8695652173913044)));
        return true;
    }

    public double sensitivityFactor() {
        if (!this.isZooming() || !((Boolean)this.sensitivity.get()).booleanValue()) {
            return 1.0;
        }
        return 1.0 / (double)Math.max(1.0f, this.zoom.get());
    }
}

