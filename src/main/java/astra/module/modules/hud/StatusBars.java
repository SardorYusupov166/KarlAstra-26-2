/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 */
package astra.module.modules.hud;

import astra.animation.AnimationUtils;
import astra.hud.HudModule;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import astra.utils.FrameStats;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class StatusBars
extends HudModule {
    private static final float W = 204.0f;
    private static final float BAR_H = 9.0f;
    private static final float GAP = 4.0f;
    private final BooleanSetting numbers = this.add(new BooleanSetting("\u0427\u0438\u0441\u043b\u0430 \u043d\u0430 \u043f\u043e\u043b\u043e\u0441\u043a\u0430\u0445", true));
    private final ModeSetting healthStyle = this.add(new ModeSetting("\u0426\u0432\u0435\u0442 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0420\u0443\u0431\u0438\u043d", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u0421\u0432\u043e\u0439"));
    private final ColorSetting healthColor = this.add((ColorSetting)new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f", -45683).visibleWhen(() -> this.healthStyle.is("\u0421\u0432\u043e\u0439")));
    private final ColorSetting foodColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u0435\u0434\u044b", -19641));
    private final ColorSetting armorColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u0431\u0440\u043e\u043d\u0438", -8530948));
    private final ColorSetting xpColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u043e\u043f\u044b\u0442\u0430", -8585294));
    private final BooleanSetting ghost = this.add((BooleanSetting)new BooleanSetting("\u0421\u043b\u0435\u0434 \u0443\u0440\u043e\u043d\u0430", true).desc("\u041f\u043e\u0442\u0435\u0440\u044f\u043d\u043d\u043e\u0435 \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435 \u0435\u0449\u0451 \u043c\u0433\u043d\u043e\u0432\u0435\u043d\u0438\u0435 \u0432\u0438\u0434\u043d\u043e \u0431\u0435\u043b\u043e\u0439 \u043f\u043e\u043b\u043e\u0441\u043a\u043e\u0439"));
    private final BooleanSetting xpBar = this.add(new BooleanSetting("\u041f\u043e\u043b\u043e\u0441\u0430 \u043e\u043f\u044b\u0442\u0430", true));
    private float shownHealth = -1.0f;
    private float ghostHealth = -1.0f;
    private float shownFood = -1.0f;
    private long lastDamage;

    public StatusBars() {
        super("status_bars", "\u041f\u043e\u043b\u043e\u0441\u043a\u0438 \u0441\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u044f", "\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u0435, \u0435\u0434\u0430, \u0431\u0440\u043e\u043d\u044f, \u0432\u043e\u0437\u0434\u0443\u0445 \u0438 \u043e\u043f\u044b\u0442 \u0432 \u0441\u0442\u0438\u043b\u0435 Astra \u0432\u043c\u0435\u0441\u0442\u043e \u0441\u0435\u0440\u0434\u0435\u0447\u0435\u043a.", Category.HUD, Icon.HEART, 0.5f, 0.885f);
        this.setDefaultEnabled(true);
    }

    public static StatusBars get() {
        return ModuleManager.get(StatusBars.class);
    }

    public static boolean replacing() {
        StatusBars m = StatusBars.get();
        return m != null && m.isEnabled() && StatusBars.mc.player != null && StatusBars.mc.gameMode != null && StatusBars.mc.gameMode.canHurtPlayer();
    }

    public static boolean replacingXp() {
        StatusBars m = StatusBars.get();
        return StatusBars.replacing() && (Boolean)m.xpBar.get() != false && StatusBars.mc.player.jumpableVehicle() == null;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    public boolean shouldRender() {
        return StatusBars.replacing();
    }

    @Override
    public float width() {
        return 204.0f;
    }

    @Override
    public float height() {
        return 31.0f;
    }

    private int[] healthColors(Player p, Theme t) {
        int[] nArray;
        if (p != null && p.hasEffect(MobEffects.WITHER)) {
            return new int[]{-11910056, -14015437};
        }
        if (p != null && p.hasEffect(MobEffects.POISON)) {
            return new int[]{-7610290, -11751600};
        }
        if (p != null && p.isFullyFrozen()) {
            return new int[]{-4199937, -8530948};
        }
        switch ((String)this.healthStyle.get()) {
            case "\u0420\u0443\u0431\u0438\u043d": {
                int[] nArray2 = new int[2];
                nArray2[0] = -50340;
                nArray = nArray2;
                nArray2[1] = -5041618;
                break;
            }
            case "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b": {
                int[] nArray3 = new int[2];
                nArray3[0] = t.accent2();
                nArray = nArray3;
                nArray3[1] = t.accent();
                break;
            }
            case "\u0421\u0432\u043e\u0439": {
                int[] nArray4 = new int[2];
                nArray4[0] = this.healthColor.getColor();
                nArray = nArray4;
                nArray4[1] = ColorUtil.darken(this.healthColor.getColor(), 0.3f);
                break;
            }
            default: {
                int[] nArray5 = new int[2];
                nArray5[0] = -36946;
                nArray = nArray5;
                nArray5[1] = -53653;
            }
        }
        return nArray;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        boolean low;
        LivingEntity le;
        Entity entity;
        LivingEntity mount;
        Theme t = StatusBars.theme();
        LocalPlayer p = StatusBars.mc.player;
        float dt = FrameStats.delta();
        float health = p != null ? p.getHealth() : 16.0f;
        float maxHealth = p != null ? p.getMaxHealth() : 20.0f;
        float absorb = p != null ? p.getAbsorptionAmount() : 4.0f;
        int food = p != null ? p.getFoodData().getFoodLevel() : 17;
        float saturation = p != null ? p.getFoodData().getSaturationLevel() : 5.0f;
        int armor = p != null ? p.getArmorValue() : 12;
        int air = p != null ? p.getAirSupply() : 300;
        int maxAir = p != null ? p.getMaxAirSupply() : 300;
        boolean underwater = p != null && (p.isEyeInFluid(FluidTags.WATER) || air < maxAir);
        LivingEntity livingEntity = mount = p != null && (entity = p.getVehicle()) instanceof LivingEntity ? (le = (LivingEntity)entity) : null;
        if (this.shownHealth < 0.0f) {
            this.shownHealth = this.ghostHealth = health;
        }
        if (health < this.shownHealth - 0.01f) {
            this.lastDamage = System.currentTimeMillis();
        }
        this.shownHealth += (health - this.shownHealth) * Math.min(1.0f, dt * 12.0f);
        if (System.currentTimeMillis() - this.lastDamage > 450L) {
            this.ghostHealth += (this.shownHealth - this.ghostHealth) * Math.min(1.0f, dt * 4.0f);
        }
        if (this.ghostHealth < this.shownHealth) {
            this.ghostHealth = this.shownHealth;
        }
        if (this.shownFood < 0.0f) {
            this.shownFood = food;
        }
        this.shownFood += ((float)food - this.shownFood) * Math.min(1.0f, dt * 10.0f);
        float half = (w - 4.0f) / 2.0f;
        float row1 = 0.0f;
        float row2 = 13.0f;
        if (armor > 0) {
            int c = this.armorColor.getColor();
            this.bar(ctx, t, 0.0f, row1, half, (float)armor / 20.0f, -1.0f, 0.0f, c, ColorUtil.darken(c, 0.35f), Icon.SHIELD, (Boolean)this.numbers.get() != false ? String.valueOf(armor) : null, false);
        }
        if (underwater || editor) {
            float a = (float)Math.max(0, air) / (float)Math.max(1, maxAir);
            this.bar(ctx, t, half + 4.0f, row1, half, a, -1.0f, 0.0f, -9967367, -15817239, Icon.DROPLET, (Boolean)this.numbers.get() != false ? Math.round(a * 100.0f) + "%" : null, a < 0.25f);
        }
        int[] hc = this.healthColors((Player)p, t);
        boolean bl = low = health <= maxHealth * 0.25f;
        String hpText = (Boolean)this.numbers.get() != false ? StatusBars.fmt(health + absorb) + (absorb > 0.0f ? "" : "") : null;
        float total = Math.max(maxHealth, maxHealth + absorb);
        this.bar(ctx, t, 0.0f, row2, half, this.shownHealth / total, (Boolean)this.ghost.get() != false ? this.ghostHealth / total : -1.0f, absorb / total, hc[0], hc[1], Icon.HEART, hpText, low);
        if (mount != null) {
            this.bar(ctx, t, half + 4.0f, row2, half, mount.getHealth() / mount.getMaxHealth(), -1.0f, 0.0f, -680309, -2071990, Icon.HEART, (Boolean)this.numbers.get() != false ? StatusBars.fmt(mount.getHealth()) : null, false);
        } else {
            int fc = this.foodColor.getColor();
            float x = half + 4.0f;
            this.bar(ctx, t, x, row2, half, this.shownFood / 20.0f, -1.0f, 0.0f, fc, ColorUtil.darken(fc, 0.3f), Icon.FLAME, (Boolean)this.numbers.get() != false ? String.valueOf(food) : null, food <= 6);
            float sw = (half - 2.0f) * Math.min(1.0f, saturation / 20.0f);
            if (sw > 0.5f) {
                Render2D.roundRect(ctx, x + 1.0f, row2 + 9.0f - 2.2f, sw, 1.2f, 0.6f, ColorUtil.withAlpha(-1, 0.6f));
            }
        }
        if (((Boolean)this.xpBar.get()).booleanValue()) {
            float xp = p != null ? p.experienceProgress : 0.42f;
            int level = p != null ? p.experienceLevel : 30;
            float y = row2 + 9.0f + 4.0f;
            float bh = 3.2f;
            int xc = this.xpColor.getColor();
            Render2D.roundRect(ctx, 0.0f, y, w, bh, bh / 2.0f, ColorUtil.withAlpha(t.bg(), 0.75f));
            float fw = Math.max(bh, w * xp);
            if (xp > 0.001f) {
                Render2D.glow(ctx, 0.0f, y, fw, bh, bh / 2.0f, 3.0f, ColorUtil.withAlpha(xc, 0.35f));
                Render2D.roundRect(ctx, 0.0f, y, fw, bh, bh / 2.0f, Paint.horizontal(0.0f, w, ColorUtil.darken(xc, 0.25f), xc));
            }
            if (level > 0) {
                String lv = String.valueOf(level);
                float tw = Fonts.width(lv, 6.6f, true) + 8.0f;
                float lx = (w - tw) / 2.0f;
                float ly = y - 3.2f;
                Render2D.glow(ctx, lx, ly, tw, 9.5f, 4.7f, 4.0f, ColorUtil.withAlpha(xc, 0.45f));
                Render2D.roundRect(ctx, lx, ly, tw, 9.5f, 4.7f, Paint.vertical(ly, ly + 9.5f, ColorUtil.withAlpha(t.bg2(), 0.95f), ColorUtil.withAlpha(t.bg(), 0.95f)));
                Render2D.outline(ctx, lx, ly, tw, 9.5f, 4.7f, 0.8f, xc);
                Fonts.drawCentered(ctx, lv, w / 2.0f, ly + 1.8f, 6.6f, xc, true);
            }
        }
    }

    private static String fmt(float v) {
        float r = (float)Math.round(v * 2.0f) / 2.0f;
        return r == (float)((int)r) ? String.valueOf((int)r) : String.valueOf(r);
    }

    private void bar(GuiGraphics ctx, Theme t, float x, float y, float w, float fill, float ghostFill, float extra, int c1, int c2, Icon icon, String text, boolean warn) {
        float ex;
        float ew;
        float r = 4.5f;
        float pulse = warn ? 0.5f + 0.5f * (float)Math.sin(AnimationUtils.time() * 9.0f) : 0.0f;
        Render2D.roundRect(ctx, x, y, w, 9.0f, r, Paint.vertical(y, y + 9.0f, ColorUtil.withAlpha(t.bg2(), 0.8f), ColorUtil.withAlpha(t.bg(), 0.88f)));
        float inner = w - 2.0f;
        fill = Math.max(0.0f, Math.min(1.0f, fill));
        if (ghostFill > fill) {
            Render2D.roundRect(ctx, x + 1.0f, y + 1.0f, inner * Math.min(1.0f, ghostFill), 7.0f, r - 1.0f, ColorUtil.withAlpha(-1, 0.55f));
        }
        if (fill > 0.001f) {
            float fw = Math.max(7.0f, inner * fill);
            Render2D.glow(ctx, x + 1.0f, y + 1.0f, fw, 7.0f, r - 1.0f, 3.0f + pulse * 3.0f, ColorUtil.withAlpha(c1, 0.35f + pulse * 0.3f));
            Render2D.roundRect(ctx, x + 1.0f, y + 1.0f, fw, 7.0f, r - 1.0f, Paint.horizontal(x, x + w, c2, c1));
            Render2D.roundRect(ctx, x + 2.0f, y + 1.5f, Math.max(0.0f, fw - 2.0f), 1.3f, 0.6f, ColorUtil.withAlpha(-1, 0.28f));
        }
        if (extra > 0.001f && (ew = Math.min(inner * extra, x + 1.0f + inner - (ex = x + 1.0f + inner * fill))) > 0.5f) {
            Render2D.roundRect(ctx, ex, y + 1.0f, ew, 7.0f, r - 1.0f, Paint.horizontal(ex, ex + ew, -7558, -15811));
        }
        Render2D.outline(ctx, x, y, w, 9.0f, r, 0.7f, ColorUtil.withAlpha(ColorUtil.lerp(c1, -1, 0.2f), 0.55f + pulse * 0.4f));
        Icons.draw(ctx, icon, x + 2.0f, y + 1.2f, 6.6f, ColorUtil.withAlpha(-1, 0.92f));
        if (text != null) {
            Fonts.drawRight(ctx, text, x + w - 3.5f, y + 1.6f, 6.0f, -1, true);
        }
    }
}

