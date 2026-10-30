/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.hud;

import astra.module.modules.hud.StateIndicator;
import astra.render.Icon;

public class SneakIndicator
extends StateIndicator {
    public SneakIndicator() {
        super("sneak_indicator", "\u0418\u043d\u0434\u0438\u043a\u0430\u0442\u043e\u0440 \u043f\u0440\u0438\u0441\u0435\u0434\u0430", "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0430\u0435\u0442 \u0442\u0435\u043a\u0443\u0449\u0435\u0435 \u0441\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u043f\u0440\u0438\u0441\u0435\u0434\u0430.", Icon.SNEAK, 0.57f, 0.86f);
    }

    @Override
    protected boolean isActive() {
        return SneakIndicator.mc.player != null && SneakIndicator.mc.player.isShiftKeyDown();
    }

    @Override
    protected String text() {
        return "\u041f\u0440\u0438\u0441\u0435\u0434";
    }
}

