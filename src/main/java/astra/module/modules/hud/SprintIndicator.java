/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.hud;

import astra.module.modules.hud.StateIndicator;
import astra.render.Icon;

public class SprintIndicator
extends StateIndicator {
    public SprintIndicator() {
        super("sprint_indicator", "\u0418\u043d\u0434\u0438\u043a\u0430\u0442\u043e\u0440 \u0431\u0435\u0433\u0430", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442, \u043a\u043e\u0433\u0434\u0430 \u0432\u044b \u0431\u0435\u0436\u0438\u0442\u0435.", Icon.RUN, 0.43f, 0.86f);
    }

    @Override
    protected boolean isActive() {
        return SprintIndicator.mc.player != null && SprintIndicator.mc.player.isSprinting();
    }

    @Override
    protected String text() {
        return "\u0411\u0435\u0433";
    }
}

