/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.repository.PackRepository
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 */
package astra.module.modules.visual;

import astra.AstraClient;
import astra.module.Category;
import astra.module.Module;
import astra.particle.AstraParticle;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import java.util.Random;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EnchantShimmer
extends Module {
    public static final String PACK_ID = "astra:astra_pink_glint";
    private static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final float[][] BANDS = new float[][]{{0.78f, 1.0f}, {0.42f, 0.78f}, {0.22f, 0.5f}, {0.0f, 0.22f}};
    private final BooleanSetting pinkGlint = this.add((BooleanSetting)(new BooleanSetting("\u0420\u043e\u0437\u043e\u0432\u044b\u0439 \u0431\u043b\u0435\u0441\u043a \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u0439", true).desc("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439 \u0440\u0435\u0441\u0443\u0440\u0441\u043f\u0430\u043a, \u043f\u0440\u0438\u043c\u0435\u043d\u044f\u0435\u0442\u0441\u044f \u0441\u0440\u0430\u0437\u0443")).onChange(this::applyPack));
    private final BooleanSetting sparkles = this.add(new BooleanSetting("\u0418\u0441\u043a\u0440\u044b \u043d\u0430 \u0431\u0440\u043e\u043d\u0435", true));
    private final NumberSetting intensity = this.add((NumberSetting)new NumberSetting("\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c", 100.0, 20.0, 300.0, 10.0).unit("%").visibleWhen(() -> (Boolean)this.sparkles.get()));
    private final ColorSetting color = this.add((ColorSetting)new ColorSetting("\u0426\u0432\u0435\u0442 \u0438\u0441\u043a\u0440", -34080).visibleWhen(() -> (Boolean)this.sparkles.get()));
    private final BooleanSetting others = this.add((BooleanSetting)new BooleanSetting("\u0423 \u0434\u0440\u0443\u0433\u0438\u0445 \u0438\u0433\u0440\u043e\u043a\u043e\u0432", true).visibleWhen(() -> (Boolean)this.sparkles.get()));
    private final Random random = new Random();

    public EnchantShimmer() {
        super("enchant_shimmer", "\u0421\u0438\u044f\u043d\u0438\u0435 \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u0439", "\u041c\u0435\u0440\u0446\u0430\u043d\u0438\u0435 \u0438 \u0440\u043e\u0437\u043e\u0432\u044b\u0439 \u0431\u043b\u0435\u0441\u043a \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u043e\u0439 \u0431\u0440\u043e\u043d\u0438.", Category.VISUAL, Icon.SPARKLE);
        this.setDefaultEnabled(true);
    }

    private void applyPack(boolean on) {
        boolean active;
        PackRepository packs = mc.getResourcePackRepository();
        if (packs == null || !packs.getAvailableIds().contains(PACK_ID)) {
            return;
        }
        boolean wanted = on && this.isEnabled();
        if (wanted == (active = packs.getSelectedIds().contains(PACK_ID))) {
            return;
        }
        if (wanted) {
            packs.addPack(PACK_ID);
        } else {
            packs.removePack(PACK_ID);
        }
        EnchantShimmer.mc.options.updateResourcePacks(packs);
        AstraClient.LOGGER.info("Astra: \u0440\u043e\u0437\u043e\u0432\u044b\u0439 \u0431\u043b\u0435\u0441\u043a \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u0439 {}", (Object)(wanted ? "\u0432\u043a\u043b\u044e\u0447\u0451\u043d" : "\u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d"));
    }

    @Override
    protected void onEnable() {
        if (mc.getResourcePackRepository() != null && mc.gui.overlay() == null) {
            this.applyPack((Boolean)this.pinkGlint.get());
        }
    }

    @Override
    protected void onDisable() {
        if (mc.getResourcePackRepository() != null) {
            this.applyPack(false);
        }
    }

    @Override
    public void onTick() {
        if (!((Boolean)this.sparkles.get()).booleanValue() || EnchantShimmer.mc.level == null || EnchantShimmer.mc.player == null || mc.isPaused()) {
            return;
        }
        float chance = 0.08f * this.intensity.getFloat() / 100.0f;
        for (Entity entity : EnchantShimmer.mc.level.entitiesForRendering()) {
            boolean self;
            LivingEntity living;
            if (!(entity instanceof LivingEntity) || !(living = (LivingEntity)entity).isAlive()) continue;
            boolean bl = self = living == EnchantShimmer.mc.player;
            if (!self ? (Boolean)this.others.get() == false || living.distanceToSqr((Entity)EnchantShimmer.mc.player) > 576.0 || !EnchantShimmer.mc.player.hasLineOfSight((Entity)living) : EnchantShimmer.mc.options.getCameraType().isFirstPerson()) continue;
            for (int i = 0; i < SLOTS.length; ++i) {
                ItemStack stack = living.getItemBySlot(SLOTS[i]);
                if (stack.isEmpty() || !stack.hasFoil() || this.random.nextFloat() > chance) continue;
                double h = living.getBbHeight();
                double y = living.getY() + h * (double)(BANDS[i][0] + this.random.nextFloat() * (BANDS[i][1] - BANDS[i][0]));
                double a = this.random.nextDouble() * Math.PI * 2.0;
                double r = (double)living.getBbWidth() * 0.55;
                int c = ColorUtil.lerp(this.color.getColor(), -1, this.random.nextFloat() * 0.5f);
                AstraParticle p = AstraParticles.spawn(ParticleSprite.SPARKLE, living.getX() + Math.cos(a) * r, y, living.getZ() + Math.sin(a) * r, 0.0, 0.004, 0.0, 0.045f + this.random.nextFloat() * 0.04f, c, 14 + this.random.nextInt(10));
                if (p == null) continue;
                p.twinkle().drag(0.9f);
            }
        }
    }
}

