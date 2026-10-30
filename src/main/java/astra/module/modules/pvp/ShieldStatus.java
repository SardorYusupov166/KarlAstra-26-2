/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 */
package astra.module.modules.pvp;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.NumberSetting;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ShieldStatus
extends Module {
    private final BooleanSetting own = this.add(new BooleanSetting("\u0421\u0432\u043e\u0439 \u0449\u0438\u0442", true));
    private final BooleanSetting enemies = this.add(new BooleanSetting("\u0429\u0438\u0442 \u0432\u0440\u0430\u0433\u043e\u0432", true));
    private final ColorSetting color = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u0441\u043b\u043e\u043c\u0430\u043d\u043d\u043e\u0433\u043e \u0449\u0438\u0442\u0430", -50340));
    private final BooleanSetting fade = this.add(new BooleanSetting("\u041f\u043b\u0430\u0432\u043d\u043e \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c \u0446\u0432\u0435\u0442", true));
    private final NumberSetting enemyTime = this.add(new NumberSetting("\u041e\u0442\u043a\u0430\u0442 \u0449\u0438\u0442\u0430 \u0432\u0440\u0430\u0433\u0430", 5.0, 1.0, 10.0, 0.5).unit(" \u0441"));
    private final BooleanSetting targetHud = this.add(new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432 Target HUD", true));
    private static final Map<Integer, long[]> BROKEN = new HashMap<Integer, long[]>();
    private Level lastWorld;

    public ShieldStatus() {
        super("shield_status", "\u0421\u043b\u043e\u043c\u0430\u043d\u043d\u044b\u0439 \u0449\u0438\u0442", "\u0429\u0438\u0442 \u043a\u0440\u0430\u0441\u043d\u0435\u0435\u0442, \u043f\u043e\u043a\u0430 \u0441\u043b\u043e\u043c\u0430\u043d \u0442\u043e\u043f\u043e\u0440\u043e\u043c: \u0443 \u0432\u0430\u0441 \u0438 \u0443 \u0432\u0440\u0430\u0433\u043e\u0432.", Category.PVP, Icon.SHIELD);
        this.setDefaultEnabled(true);
    }

    private static ShieldStatus active() {
        ShieldStatus m = ModuleManager.get(ShieldStatus.class);
        return m != null && m.isEnabled() ? m : null;
    }

    public static void onShieldBreakSound(double x, double y, double z) {
        ShieldStatus m = ShieldStatus.active();
        if (m == null || !((Boolean)m.enemies.get()).booleanValue() || ShieldStatus.mc.level == null) {
            return;
        }
        Player best = null;
        double bestDist = 6.25;
        for (Player p : ShieldStatus.mc.level.players()) {
            double d;
            if (p == ShieldStatus.mc.player || !ShieldStatus.holdsShield((LivingEntity)p) || !((d = p.distanceToSqr(x, y, z)) < bestDist)) continue;
            bestDist = d;
            best = p;
        }
        if (best == null) {
            return;
        }
        long now = System.currentTimeMillis();
        BROKEN.put(best.getId(), new long[]{now, now + (long)((Double)m.enemyTime.get() * 1000.0)});
    }

    private static boolean holdsShield(LivingEntity e) {
        return e.getMainHandItem().is(Items.SHIELD) || e.getOffhandItem().is(Items.SHIELD);
    }

    public static float brokenProgress(LivingEntity entity) {
        ShieldStatus m = ShieldStatus.active();
        if (m == null || entity == null) {
            return 0.0f;
        }
        if (entity == ShieldStatus.mc.player) {
            if (!((Boolean)m.own.get()).booleanValue() || ShieldStatus.mc.player == null) {
                return 0.0f;
            }
            ItemStack shield = ShieldStatus.mc.player.getOffhandItem().is(Items.SHIELD) ? ShieldStatus.mc.player.getOffhandItem() : (ShieldStatus.mc.player.getMainHandItem().is(Items.SHIELD) ? ShieldStatus.mc.player.getMainHandItem() : new ItemStack((ItemLike)Items.SHIELD));
            return ShieldStatus.mc.player.getCooldowns().getCooldownPercent(shield, 0.0f);
        }
        if (!((Boolean)m.enemies.get()).booleanValue()) {
            return 0.0f;
        }
        long[] t = BROKEN.get(entity.getId());
        if (t == null) {
            return 0.0f;
        }
        long now = System.currentTimeMillis();
        if (now >= t[1]) {
            return 0.0f;
        }
        return (float)(t[1] - now) / (float)(t[1] - t[0]);
    }

    public static int tint(LivingEntity entity, ItemStack stack) {
        if (entity == null || stack == null || !stack.is(Items.SHIELD)) {
            return 0;
        }
        ShieldStatus m = ShieldStatus.active();
        if (m == null) {
            return 0;
        }
        float p = ShieldStatus.brokenProgress(entity);
        if (p <= 0.0f) {
            return 0;
        }
        int c = m.color.getColor() | 0xFF000000;
        if (!((Boolean)m.fade.get()).booleanValue()) {
            return c;
        }
        float step = (float)Math.ceil(p * 8.0f) / 8.0f;
        return ColorUtil.lerp(-1, c, 0.35f + 0.65f * step) | 0xFF000000;
    }

    public static float secondsLeft(LivingEntity entity) {
        ShieldStatus m = ShieldStatus.active();
        if (m == null || !((Boolean)m.targetHud.get()).booleanValue() || entity == null) {
            return 0.0f;
        }
        if (entity == ShieldStatus.mc.player) {
            return 0.0f;
        }
        long[] t = BROKEN.get(entity.getId());
        if (t == null) {
            return 0.0f;
        }
        return Math.max(0.0f, (float)(t[1] - System.currentTimeMillis()) / 1000.0f);
    }

    @Override
    public void onTick() {
        if (ShieldStatus.mc.level != this.lastWorld) {
            BROKEN.clear();
            this.lastWorld = ShieldStatus.mc.level;
        }
        if (BROKEN.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        BROKEN.values().removeIf(t -> now >= t[1]);
    }
}

