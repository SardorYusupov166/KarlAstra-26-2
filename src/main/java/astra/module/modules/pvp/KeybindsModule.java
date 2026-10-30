/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.world.entity.LivingEntity
 */
package astra.module.modules.pvp;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.settings.StringSetting;
import astra.utils.CombatTracker;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.world.entity.LivingEntity;

public class KeybindsModule
extends Module {
    public static final int MAX_BINDS = 12;
    private static final String CHAT = "\u0427\u0430\u0442 / \u043a\u043e\u043c\u0430\u043d\u0434\u0430";
    private static final String TOGGLE = "\u041f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u043c\u043e\u0434\u0443\u043b\u044c";
    private static final String OFF = "\u0412\u044b\u043a\u043b";
    private static final int SEND_GAP_TICKS = 10;
    private static final long COOLDOWN_MS = 800L;
    private final NumberSetting count = this.add(new NumberSetting("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0431\u0438\u043d\u0434\u043e\u0432", 3.0, 1.0, 12.0, 1.0));
    private final List<Bind> binds = new ArrayList<Bind>();
    private final ArrayDeque<String> queue = new ArrayDeque();
    private int sendDelay;

    public KeybindsModule() {
        super("keybinds", "\u0411\u0438\u043d\u0434\u044b", "\u0421\u0432\u043e\u044f \u043a\u043b\u0430\u0432\u0438\u0448\u0430 \u043d\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0443, \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0432 \u0447\u0430\u0442 \u0438\u043b\u0438 \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043c\u043e\u0434\u0443\u043b\u044f.", Category.PVP, Icon.KEYBOARD);
        for (int n = 1; n <= 12; ++n) {
            this.binds.add(new Bind(n));
        }
        this.setDefaultEnabled(true);
    }

    private static List<String> moduleNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Module m : ModuleManager.all()) {
            if (!m.isToggleable() || m.isHidden() || m instanceof KeybindsModule) continue;
            names.add(m.getName());
        }
        return names;
    }

    @Override
    public void onTick() {
        if (this.sendDelay > 0) {
            --this.sendDelay;
        }
        if (this.sendDelay == 0 && !this.queue.isEmpty()) {
            KeybindsModule.send(this.queue.poll());
            this.sendDelay = 10;
        }
        if (KeybindsModule.mc.field_1724 == null) {
            return;
        }
        Window window = mc.method_22683();
        boolean canUse = KeybindsModule.mc.field_1755 == null;
        int active = this.count.getInt();
        for (int i = 0; i < this.binds.size(); ++i) {
            boolean down;
            Bind b = this.binds.get(i);
            boolean bl = down = canUse && i < active && b.key.isBound() && InputConstants.method_15987((Window)window, (int)b.key.getKey());
            if (down && !b.wasDown) {
                this.fire(b);
            }
            b.wasDown = down;
        }
    }

    @Override
    protected void onDisable() {
        this.queue.clear();
    }

    public void trigger(int n) {
        if (n >= 1 && n <= this.binds.size()) {
            this.fire(this.binds.get(n - 1));
        }
    }

    private void fire(Bind b) {
        block4: {
            block3: {
                long now = System.currentTimeMillis();
                if (now - b.lastFired < 800L) {
                    return;
                }
                b.lastFired = now;
                if (!b.action.is(TOGGLE)) break block3;
                for (Module m : ModuleManager.all()) {
                    if (!m.getName().equals(b.module.get()) || !m.isToggleable()) continue;
                    m.toggle();
                    return;
                }
                break block4;
            }
            if (!b.action.is(CHAT)) break block4;
            int added = 0;
            for (String part : ((String)b.text.get()).split("&&")) {
                String msg = KeybindsModule.fill(part.trim());
                if (msg.isEmpty() || added++ >= 5) continue;
                this.queue.add(msg);
            }
        }
    }

    private static String fill(String s) {
        if (s.indexOf(123) < 0 || KeybindsModule.mc.field_1724 == null) {
            return s;
        }
        LivingEntity target = CombatTracker.currentTarget(false);
        return s.replace("{x}", String.valueOf(KeybindsModule.mc.field_1724.method_31477())).replace("{y}", String.valueOf(KeybindsModule.mc.field_1724.method_31478())).replace("{z}", String.valueOf(KeybindsModule.mc.field_1724.method_31479())).replace("{target}", target != null ? target.method_5477().getString() : "").replace("{hp}", String.format(Locale.ROOT, "%.1f", Float.valueOf(KeybindsModule.mc.field_1724.method_6032())));
    }

    private static void send(String msg) {
        if (KeybindsModule.mc.field_1724 == null || mc.method_1562() == null) {
            return;
        }
        if (msg.length() > 256) {
            msg = msg.substring(0, 256);
        }
        if (msg.startsWith("/")) {
            mc.method_1562().method_45730(msg.substring(1));
        } else {
            mc.method_1562().method_45729(msg);
        }
    }

    private final class Bind {
        final KeybindSetting key;
        final ModeSetting action;
        final StringSetting text;
        final ModeSetting module;
        boolean wasDown;
        long lastFired;

        Bind(int n) {
            int i = n - 1;
            this.key = (KeybindSetting)KeybindsModule.this.add((KeybindSetting)new KeybindSetting("\u0411\u0438\u043d\u0434 " + n + " \u00b7 \u043a\u043b\u0430\u0432\u0438\u0448\u0430", -1).visibleWhen(() -> i < KeybindsModule.this.count.getInt()));
            this.action = (ModeSetting)KeybindsModule.this.add((ModeSetting)new ModeSetting("\u0411\u0438\u043d\u0434 " + n + " \u00b7 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435", KeybindsModule.CHAT, KeybindsModule.CHAT, KeybindsModule.TOGGLE, KeybindsModule.OFF).visibleWhen(() -> i < KeybindsModule.this.count.getInt()));
            this.text = (StringSetting)KeybindsModule.this.add((StringSetting)((Setting)new StringSetting("\u0411\u0438\u043d\u0434 " + n + " \u00b7 \u0442\u0435\u043a\u0441\u0442", "", "/spawn  \u0438\u043b\u0438  gg && gl", 256).desc("\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0430\u0447\u0438\u043d\u0430\u0435\u0442\u0441\u044f \u0441 \u00ab/\u00bb. \u041d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0439 \u2014 \u0447\u0435\u0440\u0435\u0437 &&. \u041f\u043e\u0434\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0438: {x} {y} {z} {target} {hp}")).visibleWhen(() -> i < KeybindsModule.this.count.getInt() && this.action.is(KeybindsModule.CHAT)));
            this.module = (ModeSetting)KeybindsModule.this.add((ModeSetting)new ModeSetting("\u0411\u0438\u043d\u0434 " + n + " \u00b7 \u043c\u043e\u0434\u0443\u043b\u044c", "", "").dynamic(KeybindsModule::moduleNames).visibleWhen(() -> i < KeybindsModule.this.count.getInt() && this.action.is(KeybindsModule.TOGGLE)));
        }
    }
}

