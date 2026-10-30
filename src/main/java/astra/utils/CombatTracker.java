/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 */
package astra.utils;

import astra.module.ModuleManager;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.cosmetic.VictoryEffect;
import astra.module.modules.hud.CompanionHud;
import astra.module.modules.pvp.HitParticles;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class CombatTracker {
    private static LivingEntity lastTarget;
    private static long lastAttack;
    private static LivingEntity pending;
    private static long pendingAt;
    private static int pendingHurtBefore;
    private static boolean pendingCrit;
    private static int combo;
    private static long lastComboHit;
    private static int lastOwnHurtTime;
    private static float lastTargetHealth;
    private static boolean killReported;
    private static final List<DamageNumber> DAMAGE;
    private static float lastDamage;
    private static long lastDamageAt;

    private CombatTracker() {
    }

    public static void onAttack(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity living = (LivingEntity)entity;
        LocalPlayer player = Minecraft.getInstance().player;
        long now = System.currentTimeMillis();
        if (living != lastTarget) {
            lastTargetHealth = living.getHealth();
            killReported = false;
            SoundsModule sounds = SoundsModule.get();
            if (sounds != null) {
                sounds.onFightStart();
            }
        }
        lastTarget = living;
        lastAttack = now;
        pending = living;
        pendingAt = now;
        pendingHurtBefore = living.hurtTime;
        pendingCrit = player != null && player.fallDistance > 0.0 && !player.onGround() && !player.onClimbable() && !player.isInWater() && !player.isPassenger() && !player.isSprinting();
    }

    public static void tick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.level == null) {
            CombatTracker.reset();
            return;
        }
        long now = System.currentTimeMillis();
        if (player.hurtTime > lastOwnHurtTime && player.hurtTime > 0) {
            combo = 0;
        }
        lastOwnHurtTime = player.hurtTime;
        if (pending != null) {
            boolean hurtStarted;
            boolean bl = hurtStarted = CombatTracker.pending.hurtTime > 0 && (CombatTracker.pending.hurtTime > pendingHurtBefore || pendingHurtBefore == 0);
            if (hurtStarted) {
                SoundsModule sounds;
                ++combo;
                lastComboHit = now;
                HitParticles hit = ModuleManager.get(HitParticles.class);
                if (hit != null && hit.isEnabled()) {
                    hit.onHit(pending, pendingCrit);
                }
                if ((sounds = SoundsModule.get()) != null) {
                    sounds.onHit(pendingCrit, combo);
                }
                CompanionHud.onHit(pendingCrit, combo);
                pending = null;
            } else if (now - pendingAt > 650L || pending.isRemoved()) {
                pending = null;
            }
        }
        if (combo > 0 && now - lastComboHit > 3000L) {
            combo = 0;
        }
        if (lastTarget != null) {
            boolean dead;
            float hp = lastTarget.getHealth();
            if (lastTargetHealth >= 0.0f && hp < lastTargetHealth - 0.01f && now - lastAttack < 1500L) {
                float dmg = lastTargetHealth - hp;
                DAMAGE.add(new DamageNumber(dmg, now, pendingCrit));
                lastDamage = dmg;
                lastDamageAt = now;
            }
            lastTargetHealth = hp;
            boolean bl = dead = lastTarget.isDeadOrDying() || hp <= 0.0f;
            if (dead && !killReported && now - lastAttack < 4000L) {
                SoundsModule sounds;
                killReported = true;
                VictoryEffect victory = ModuleManager.get(VictoryEffect.class);
                if (victory != null && victory.isEnabled()) {
                    victory.trigger(lastTarget);
                }
                if ((sounds = SoundsModule.get()) != null) {
                    sounds.onKill();
                }
                CompanionHud.onKill();
            }
            if (now - lastAttack > 15000L || lastTarget.isRemoved() && !dead) {
                lastTarget = null;
                lastTargetHealth = -1.0f;
            }
        }
        DAMAGE.removeIf(d -> now - d.time() > 1500L);
    }

    private static void reset() {
        lastTarget = null;
        pending = null;
        combo = 0;
        DAMAGE.clear();
    }

    public static int combo() {
        return combo;
    }

    public static long lastAttack() {
        return lastAttack;
    }

    public static long lastComboHit() {
        return lastComboHit;
    }

    public static List<DamageNumber> damageNumbers() {
        return DAMAGE;
    }

    public static float lastDamage() {
        return lastDamage;
    }

    public static long lastDamageAt() {
        return lastDamageAt;
    }

    public static LivingEntity currentTarget(boolean includeMobs) {
        LivingEntity living;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return null;
        }
        long now = System.currentTimeMillis();
        if (lastTarget != null && now - lastAttack < 4000L && lastTarget.isAlive() && player.distanceToSqr((Entity)lastTarget) < 144.0 && player.hasLineOfSight((Entity)lastTarget) && (includeMobs || lastTarget instanceof Player)) {
            return lastTarget;
        }
        Entity entity = mc.crosshairPickEntity;
        if (entity instanceof LivingEntity && (living = (LivingEntity)entity).isAlive() && (includeMobs || living instanceof Player) && !living.isInvisibleTo((Player)player)) {
            return living;
        }
        return null;
    }

    static {
        lastTargetHealth = -1.0f;
        DAMAGE = new ArrayList<DamageNumber>();
    }

    public record DamageNumber(float amount, long time, boolean crit) {
    }
}

