/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.multiplayer.PlayerInfo
 */
package astra.module.modules.hud;

import astra.module.Category;
import astra.module.modules.hud.StatHud;
import astra.render.ColorUtil;
import astra.render.Icon;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingHud
extends StatHud {
    public PingHud() {
        super("ping", "\u041f\u0438\u043d\u0433", "\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u0434\u043e \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0438\u0437 \u0442\u0430\u0431\u043b\u0438\u0446\u044b \u0438\u0433\u0440\u043e\u043a\u043e\u0432.", Category.HUD, Icon.WIFI, 0.5f, 0.11f);
        this.setDefaultEnabled(true);
    }

    public static int ping() {
        if (PingHud.mc.field_1724 == null) {
            return -1;
        }
        ClientPacketListener handler = mc.method_1562();
        if (handler == null) {
            return -1;
        }
        PlayerInfo entry = handler.method_2871(PingHud.mc.field_1724.method_5667());
        return entry == null ? -1 : entry.method_2959();
    }

    @Override
    protected String label() {
        return this.style.is("\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430") ? "\u041f\u0438\u043d\u0433" : "\u043c\u0441";
    }

    @Override
    protected String value() {
        int p = PingHud.ping();
        if (p < 0) {
            return "\u2014";
        }
        return this.style.is("\u041a\u0430\u0440\u0442\u043e\u0447\u043a\u0430") ? p + " ms" : String.valueOf(p);
    }

    @Override
    protected int valueColor() {
        int p = PingHud.ping();
        if (p < 0) {
            return PingHud.theme().textDim();
        }
        if (p < 80) {
            return -11870592;
        }
        if (p < 160) {
            return ColorUtil.lerp(-11870592, -340971, (float)(p - 80) / 80.0f);
        }
        return ColorUtil.lerp(-340971, -770210, Math.min(1.0f, (float)(p - 160) / 140.0f));
    }
}

