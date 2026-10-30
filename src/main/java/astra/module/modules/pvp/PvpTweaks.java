/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.Level
 */
package astra.module.modules.pvp;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PvpTweaks
extends Module {
    private final ModeSetting totemAnim = this.add(new ModeSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0442\u043e\u0442\u0435\u043c\u0430", "\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0430\u044f", "\u041e\u0431\u044b\u0447\u043d\u0430\u044f", "\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0430\u044f", "\u0421\u043a\u0440\u044b\u0442\u044c"));
    private final NumberSetting totemScale = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u0442\u043e\u0442\u0435\u043c\u0430", 40.0, 10.0, 100.0, 5.0).unit("%"));
    private final BooleanSetting totemParticles = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0442\u043e\u0442\u0435\u043c\u0430", true));
    private final BooleanSetting explosionParticles = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0432\u0437\u0440\u044b\u0432\u0430", true));
    private final ModeSetting fire = this.add(new ModeSetting("\u041e\u0433\u043e\u043d\u044c \u043d\u0430 \u044d\u043a\u0440\u0430\u043d\u0435", "\u041d\u0438\u0437\u043a\u0438\u0439", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041d\u0438\u0437\u043a\u0438\u0439", "\u0421\u043a\u0440\u044b\u0442\u044c"));
    private final NumberSetting fireHeight = this.add(new NumberSetting("\u041e\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u043e\u0433\u043e\u043d\u044c", 15.0, 5.0, 60.0, 5.0).unit("%"));
    private final NumberSetting hurtTilt = this.add(new NumberSetting("\u0422\u0440\u044f\u0441\u043a\u0430 \u043f\u0440\u0438 \u0443\u0440\u043e\u043d\u0435", 30.0, 0.0, 100.0, 5.0).unit("%"));
    private final BooleanSetting ownEffectParticles = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0441\u0432\u043e\u0438 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0437\u0435\u043b\u0438\u0439", true));
    private final BooleanSetting pumpkin = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0442\u044b\u043a\u0432\u0443 \u043d\u0430 \u0433\u043e\u043b\u043e\u0432\u0435", true));
    private final BooleanSetting powderSnow = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0438\u043d\u0435\u0439 (\u0441\u043d\u0435\u0433)", false));
    private final BooleanSetting inWall = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0431\u043b\u043e\u043a \u0432 \u043b\u0438\u0446\u043e", true));
    private final BooleanSetting underwater = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u043e\u0432\u0435\u0440\u043b\u0435\u0439 \u043f\u043e\u0434 \u0432\u043e\u0434\u043e\u0439", false));
    private final NumberSetting shieldLower = this.add(new NumberSetting("\u041e\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u0449\u0438\u0442", 25.0, 0.0, 60.0, 5.0).unit("%"));
    private final BooleanSetting popCounter = this.add(new BooleanSetting("\u0421\u0447\u0451\u0442\u0447\u0438\u043a \u0442\u043e\u0442\u0435\u043c\u043e\u0432 \u0432 \u0447\u0430\u0442\u0435", true));
    private final BooleanSetting popSelf = this.add(new BooleanSetting("\u0421\u0447\u0438\u0442\u0430\u0442\u044c \u0441\u0432\u043e\u0438 \u0442\u043e\u0442\u0435\u043c\u044b", false));
    private static final Map<UUID, Integer> POPS = new HashMap<UUID, Integer>();
    private Level lastWorld;

    public PvpTweaks() {
        super("pvp_tweaks", "\u0427\u0438\u0441\u0442\u044b\u0439 PvP", "\u0411\u0435\u0437 \u0442\u043e\u0442\u0435\u043c\u0430 \u0438 \u043e\u0433\u043d\u044f \u043d\u0430 \u0432\u0435\u0441\u044c \u044d\u043a\u0440\u0430\u043d, \u0431\u0435\u0437 \u0447\u0430\u0441\u0442\u0438\u0446 \u0432\u0437\u0440\u044b\u0432\u0430, \u0441\u0447\u0451\u0442\u0447\u0438\u043a \u0442\u043e\u0442\u0435\u043c\u043e\u0432.", Category.PVP, Icon.SHIELD);
        this.setDefaultEnabled(true);
    }

    private static PvpTweaks active() {
        PvpTweaks m = ModuleManager.get(PvpTweaks.class);
        return m != null && m.isEnabled() ? m : null;
    }

    public static boolean hideTotemAnimation() {
        PvpTweaks m = PvpTweaks.active();
        return m != null && m.totemAnim.is("\u0421\u043a\u0440\u044b\u0442\u044c");
    }

    public static float totemAnimationScale() {
        PvpTweaks m = PvpTweaks.active();
        return m != null && m.totemAnim.is("\u041c\u0430\u043b\u0435\u043d\u044c\u043a\u0430\u044f") ? m.totemScale.getFloat() / 100.0f : 1.0f;
    }

    public static boolean hideFire() {
        PvpTweaks m = PvpTweaks.active();
        return m != null && m.fire.is("\u0421\u043a\u0440\u044b\u0442\u044c");
    }

    public static float fireOffset() {
        PvpTweaks m = PvpTweaks.active();
        return m != null && m.fire.is("\u041d\u0438\u0437\u043a\u0438\u0439") ? m.fireHeight.getFloat() / 100.0f : 0.0f;
    }

    public static float hurtTiltFactor() {
        PvpTweaks m = PvpTweaks.active();
        return m != null ? m.hurtTilt.getFloat() / 100.0f : 1.0f;
    }

    public static boolean hideInWall() {
        PvpTweaks m = PvpTweaks.active();
        return m != null && (Boolean)m.inWall.get() != false;
    }

    public static boolean hideUnderwater() {
        PvpTweaks m = PvpTweaks.active();
        return m != null && (Boolean)m.underwater.get() != false;
    }

    public static boolean hideHudOverlay(Identifier texture) {
        PvpTweaks m = PvpTweaks.active();
        if (m == null || texture == null) {
            return false;
        }
        String path = texture.getPath();
        if (((Boolean)m.pumpkin.get()).booleanValue() && path.contains("pumpkin")) {
            return true;
        }
        return (Boolean)m.powderSnow.get() != false && path.contains("powder_snow");
    }

    public static void applyHeldItem(PoseStack matrices, ItemStack item) {
        PvpTweaks m = PvpTweaks.active();
        if (m == null || item == null || !item.is(Items.SHIELD)) {
            return;
        }
        float lower = m.shieldLower.getFloat() / 100.0f;
        if (lower > 0.0f) {
            matrices.translate(0.0f, -lower * 0.6f, 0.0f);
        }
    }

    public static boolean hideParticle(ParticleOptions effect, double x, double y, double z) {
        PvpTweaks m = PvpTweaks.active();
        if (m == null) {
            return false;
        }
        ParticleType type = effect.getType();
        if (((Boolean)m.totemParticles.get()).booleanValue() && type == ParticleTypes.TOTEM_OF_UNDYING) {
            return true;
        }
        if (((Boolean)m.explosionParticles.get()).booleanValue() && (type == ParticleTypes.EXPLOSION || type == ParticleTypes.EXPLOSION_EMITTER)) {
            return true;
        }
        if (((Boolean)m.ownEffectParticles.get()).booleanValue() && type == ParticleTypes.ENTITY_EFFECT && PvpTweaks.mc.player != null && PvpTweaks.mc.options.getCameraType().isFirstPerson()) {
            double dx = x - PvpTweaks.mc.player.getX();
            double dz = z - PvpTweaks.mc.player.getZ();
            double dy = y - PvpTweaks.mc.player.getY();
            return dx * dx + dz * dz < 1.0 && dy > -0.2 && dy < 2.2;
        }
        return false;
    }

    public static void onTotemPop(Entity entity) {
        Player player;
        PvpTweaks m;
        block5: {
            block4: {
                m = PvpTweaks.active();
                if (m == null || !(entity instanceof Player)) break block4;
                player = (Player)entity;
                if (PvpTweaks.mc.gui != null) break block5;
            }
            return;
        }
        boolean self = player == PvpTweaks.mc.player;
        int count = POPS.merge(player.getUUID(), 1, Integer::sum);
        if (!((Boolean)m.popCounter.get()).booleanValue() || self && !((Boolean)m.popSelf.get()).booleanValue()) {
            return;
        }
        String name = self ? "\u0412\u044b" : player.getName().getString();
        MutableComponent msg = Component.literal((String)"\u2726 ").withColor(16731608).append((Component)Component.literal((String)name).withColor(0xFFFFFF)).append((Component)Component.literal((String)(self ? " \u043f\u043e\u0442\u0435\u0440\u044f\u043b\u0438 \u0442\u043e\u0442\u0435\u043c " : " \u043f\u043e\u0442\u0435\u0440\u044f\u043b \u0442\u043e\u0442\u0435\u043c ")).withColor(13220057)).append((Component)Component.literal((String)("\u00d7" + count)).withColor(16731608));
        PvpTweaks.mc.gui.getChat().addMessage((Component)msg);
    }

    public static int pops(Entity entity) {
        return entity == null ? 0 : POPS.getOrDefault(entity.getUUID(), 0);
    }

    @Override
    public void onTick() {
        if (PvpTweaks.mc.level != this.lastWorld) {
            POPS.clear();
            this.lastWorld = PvpTweaks.mc.level;
        }
        if (PvpTweaks.mc.level == null || POPS.isEmpty()) {
            return;
        }
        for (Player p : PvpTweaks.mc.level.players()) {
            Integer popped;
            if (!p.isDeadOrDying() || (popped = POPS.remove(p.getUUID())) == null || !((Boolean)this.popCounter.get()).booleanValue() || p == PvpTweaks.mc.player) continue;
            PvpTweaks.mc.gui.getChat().addMessage((Component)Component.literal((String)"\u2726 ").withColor(16731608).append((Component)Component.literal((String)p.getName().getString()).withColor(0xFFFFFF)).append((Component)Component.literal((String)" \u0443\u043c\u0435\u0440, \u0441\u043d\u0435\u0441\u0435\u043d\u043e \u0442\u043e\u0442\u0435\u043c\u043e\u0432: ").withColor(13220057)).append((Component)Component.literal((String)String.valueOf(popped)).withColor(16731608)));
        }
    }
}

