/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.hud;

import astra.module.Category;
import astra.module.modules.hud.StatHud;
import astra.render.Icon;

public class FpsHud
extends StatHud {
    public FpsHud() {
        super("fps", "FPS", "\u0421\u0447\u0451\u0442\u0447\u0438\u043a \u043a\u0430\u0434\u0440\u043e\u0432 \u0432 \u0441\u0435\u043a\u0443\u043d\u0434\u0443.", Category.HUD, Icon.MONITOR, 0.36f, 0.11f);
        this.setDefaultEnabled(true);
    }

    @Override
    protected String label() {
        return "FPS";
    }

    @Override
    protected String value() {
        return String.valueOf(mc.getFps());
    }
}

