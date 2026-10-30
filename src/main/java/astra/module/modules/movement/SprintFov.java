/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.movement;

import astra.animation.Animation;
import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import astra.settings.NumberSetting;
import astra.utils.FrameStats;

public class SprintFov
extends Module {
    private final NumberSetting increase = this.add(new NumberSetting("\u0423\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u0438\u0435", 15.0, 0.0, 40.0, 1.0).unit("\u00b0"));
    private final NumberSetting speed = this.add(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 0.4, 0.1, 2.0, 0.1).unit(" \u0441"));
    private Animation anim = new Animation(0.0f, 8.0f);
    private double lastSpeed = -1.0;

    public SprintFov() {
        super("sprint_fov", "\u0423\u0433\u043e\u043b \u043e\u0431\u0437\u043e\u0440\u0430 \u043f\u0440\u0438 \u0431\u0435\u0433\u0435", "\u0414\u0438\u043d\u0430\u043c\u0438\u0447\u0435\u0441\u043a\u0438\u0439 FOV \u043f\u0440\u0438 \u0431\u0435\u0433\u0435.", Category.MOVEMENT, Icon.SPEED);
    }

    public float extraDegrees() {
        if (this.lastSpeed != (Double)this.speed.get()) {
            this.lastSpeed = (Double)this.speed.get();
            this.anim = new Animation(this.anim.get(), (float)(3.0 / Math.max(0.1, (Double)this.speed.get())));
        }
        boolean sprinting = SprintFov.mc.field_1724 != null && SprintFov.mc.field_1724.method_5624();
        return this.anim.update(sprinting ? this.increase.getFloat() : 0.0f, FrameStats.delta());
    }
}

