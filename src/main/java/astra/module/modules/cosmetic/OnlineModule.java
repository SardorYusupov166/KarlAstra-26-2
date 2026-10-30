/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.cosmetic;

import astra.module.Category;
import astra.module.Module;
import astra.online.AstraOnline;
import astra.online.RemoteCosmetics;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.StringSetting;

public class OnlineModule
extends Module {
    public static final String DEFAULT_SERVER = "78.153.149.133:8765";
    private final StringSetting server = this.add((StringSetting)new StringSetting("\u0410\u0434\u0440\u0435\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430", "", "\u043e\u0444\u0438\u0446\u0438\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0435\u0440\u0432\u0435\u0440 Astra", 120).desc("\u041f\u0443\u0441\u0442\u043e \u2014 \u043e\u0444\u0438\u0446\u0438\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0435\u0440\u0432\u0435\u0440 Astra (\u0432\u0441\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 \u0441 \u043c\u043e\u0434\u043e\u043c). \u041c\u043e\u0436\u043d\u043e \u0443\u043a\u0430\u0437\u0430\u0442\u044c \u0441\u0432\u043e\u0439: \u0434\u043e\u043c\u0435\u043d \u0438\u043b\u0438 IP:\u043f\u043e\u0440\u0442"));
    private final StringSetting room = this.add((StringSetting)new StringSetting("\u041a\u043b\u044e\u0447 \u043a\u043e\u043c\u043d\u0430\u0442\u044b", "", "\u043e\u0431\u0449\u0430\u044f \u043a\u043e\u043c\u043d\u0430\u0442\u0430", 64).desc("\u041f\u0443\u0441\u0442\u043e \u2014 \u043e\u0431\u0449\u0430\u044f \u043a\u043e\u043c\u043d\u0430\u0442\u0430: \u0432\u0430\u0441 \u0432\u0438\u0434\u044f\u0442 \u0432\u0441\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 \u0441 Astra. \u0421\u0432\u043e\u0439 \u043a\u043b\u044e\u0447 \u2014 \u0442\u043e\u043b\u044c\u043a\u043e \u0434\u0440\u0443\u0437\u044c\u044f \u0441 \u0442\u0435\u043c \u0436\u0435 \u043a\u043b\u044e\u0447\u043e\u043c"));
    private final BooleanSetting sendMine = this.add(new BooleanSetting("\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c \u043c\u043e\u044e \u043a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0443", true));
    private final BooleanSetting showOthers = this.add((BooleanSetting)new BooleanSetting("\u041a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0430 \u0434\u0440\u0443\u0433\u0438\u0445", true).desc("\u041a\u0440\u044b\u043b\u044c\u044f, \u043d\u0438\u043c\u0431\u044b, \u043f\u043b\u0430\u0449\u0438, \u0430\u0443\u0440\u044b \u0438 \u0441\u043b\u0435\u0434\u044b \u0434\u0440\u0443\u0437\u0435\u0439 \u0441 Astra"));
    private final BooleanSetting showEmotes = this.add(new BooleanSetting("\u042d\u043c\u043e\u0446\u0438\u0438 \u0434\u0440\u0443\u0433\u0438\u0445", true));
    private final ButtonSetting status = this.add((ButtonSetting)new ButtonSetting("\u0421\u0442\u0430\u0442\u0443\u0441", "\u0421\u0442\u0430\u0442\u0443\u0441", AstraOnline::reconnect).label(() -> "\u25cf " + AstraOnline.status()).desc("\u041d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0435\u0440\u0435\u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0438\u0442\u044c\u0441\u044f"));

    public OnlineModule() {
        super("astra_online", "Astra Online", "\u0414\u0440\u0443\u0437\u044c\u044f \u0441 Astra \u0432\u0438\u0434\u044f\u0442 \u0432\u0430\u0448\u0438 \u043a\u0440\u044b\u043b\u044c\u044f, \u043d\u0438\u043c\u0431, \u043f\u043b\u0430\u0449, \u0430\u0443\u0440\u0443, \u0441\u043b\u0435\u0434 \u0438 \u044d\u043c\u043e\u0446\u0438\u0438 \u2014 \u0430 \u0432\u044b \u0438\u0445.", Category.COSMETICS, Icon.WIFI);
        this.setDefaultEnabled(true);
    }

    @Override
    public void onTick() {
        RemoteCosmetics.showOthers = (Boolean)this.showOthers.get();
        RemoteCosmetics.showEmotes = (Boolean)this.showEmotes.get();
        String address = ((String)this.server.get()).isBlank() ? DEFAULT_SERVER : (String)this.server.get();
        AstraOnline.tick(address, (String)this.room.get(), (Boolean)this.sendMine.get());
        RemoteCosmetics.tick();
    }

    @Override
    protected void onDisable() {
        AstraOnline.disconnect();
        RemoteCosmetics.showOthers = false;
    }
}

