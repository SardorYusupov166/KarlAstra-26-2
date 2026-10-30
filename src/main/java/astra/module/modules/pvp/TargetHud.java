/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.PlayerFaceRenderer
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.PlayerSkin
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.pvp;

import astra.animation.Animation;
import astra.hud.HudModule;
import astra.module.Category;
import astra.module.modules.cosmetic.SoundsModule;
import astra.module.modules.pvp.PvpTweaks;
import astra.module.modules.pvp.ShieldStatus;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import astra.utils.CombatTracker;
import astra.utils.FrameStats;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerSkin;
import org.joml.Matrix3x2fStack;

public class TargetHud
extends HudModule {
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "Astra Modern", "Astra Modern", "\u041a\u043e\u043c\u043f\u0430\u043a\u0442\u043d\u044b\u0439"));
    private final BooleanSetting mobs = this.add(new BooleanSetting("\u041c\u043e\u0431\u044b \u0442\u043e\u0436\u0435", false));
    private final BooleanSetting hearts = this.add(new BooleanSetting("\u0421\u0435\u0440\u0434\u0435\u0447\u043a\u0438", true));
    private final BooleanSetting grow = this.add((BooleanSetting)new BooleanSetting("\u0423\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u0438\u0435 \u0432 \u0431\u043e\u044e", true).desc("\u0427\u0443\u0442\u044c \u043a\u0440\u0443\u043f\u043d\u0435\u0435 \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u0434\u0440\u0430\u043a\u0438 \u0438 \u00ab\u043f\u0440\u044b\u0433\u0430\u0435\u0442\u00bb \u043d\u0430 \u0443\u0434\u0430\u0440"));
    private final NumberSetting growAmount = this.add((NumberSetting)new NumberSetting("\u0421\u0438\u043b\u0430 \u0443\u0432\u0435\u043b\u0438\u0447\u0435\u043d\u0438\u044f", 10.0, 2.0, 30.0, 1.0).unit("%").visibleWhen(() -> (Boolean)this.grow.get()));
    private final Animation combat = new Animation(0.0f, 6.0f);
    private final Animation appear = new Animation(0.0f, 10.0f);
    private final Animation hpBar = new Animation(1.0f, 8.0f);
    private LivingEntity shown;

    public TargetHud() {
        super("target_hud", "Target HUD", "\u0426\u0435\u043b\u044c, \u0441 \u043a\u043e\u0442\u043e\u0440\u043e\u0439 \u0432\u044b \u0441\u0440\u0430\u0436\u0430\u0435\u0442\u0435\u0441\u044c: \u043d\u0438\u043a, \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u0435, \u0430\u0432\u0430\u0442\u0430\u0440.", Category.PVP, Icon.TARGET, 0.5f, 0.68f);
        this.setDefaultEnabled(true);
    }

    @Override
    public boolean shouldRender() {
        SoundsModule sounds;
        LivingEntity target = CombatTracker.currentTarget((Boolean)this.mobs.get());
        if (target != null && target != this.shown && (sounds = SoundsModule.get()) != null) {
            sounds.onTargetFound(target);
        }
        if (target != null) {
            this.shown = target;
        }
        float a = this.appear.update(target != null ? 1.0f : 0.0f, FrameStats.delta());
        return a > 0.02f && this.shown != null;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    private boolean compact() {
        return this.style.is("\u041a\u043e\u043c\u043f\u0430\u043a\u0442\u043d\u044b\u0439");
    }

    @Override
    public float width() {
        return this.compact() ? 110.0f : 128.0f;
    }

    @Override
    public float height() {
        return this.compact() ? 26.0f : 42.0f;
    }

    private static float badge(GuiGraphics ctx, String text, float right, float y, int bg, int fg) {
        float w = Fonts.width(text, 6.0f, true) + 8.0f;
        float x = right - w;
        Render2D.roundRect(ctx, x, y, w, 9.0f, 4.5f, bg);
        Fonts.draw(ctx, text, x + 4.0f, y + 1.2f, 6.0f, fg, true);
        return x - 3.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        float shield;
        float a;
        Theme t = TargetHud.theme();
        float f = a = editor ? 1.0f : this.appear.get();
        LivingEntity e = editor ? (this.shown != null && !this.shown.isRemoved() ? this.shown : TargetHud.mc.player) : this.shown;
        String name = e != null ? e.getName().getString() : "Sakura4ik_";
        float hp = e != null ? e.getHealth() : 12.6f;
        float max = e != null ? Math.max(1.0f, e.getMaxHealth()) : 20.0f;
        float pct = Math.max(0.0f, Math.min(1.0f, hp / max));
        float bar = this.hpBar.update(pct, FrameStats.delta());
        float prev = Render2D.alpha;
        Render2D.alpha *= a;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        float sc = 0.9f + 0.1f * a;
        if (((Boolean)this.grow.get()).booleanValue() && !editor) {
            long now = System.currentTimeMillis();
            boolean fighting = now - CombatTracker.lastAttack() < 3000L;
            float fight = this.combat.update(fighting ? 1.0f : 0.0f, FrameStats.delta());
            float hit = 1.0f - Math.min(1.0f, (float)(now - CombatTracker.lastComboHit()) / 220.0f);
            sc *= 1.0f + this.growAmount.getFloat() / 100.0f * (0.6f * fight + 0.4f * hit);
        }
        m.translate(w / 2.0f, h / 2.0f);
        m.scale(sc, sc);
        m.translate(-w / 2.0f, -h / 2.0f);
        this.drawPanel(ctx, 0.0f, 0.0f, w, h, 6.0f);
        float x = 5.0f;
        if (!this.compact()) {
            float av = h - 10.0f;
            Render2D.roundRect(ctx, 4.0f, 4.0f, av + 2.0f, av + 2.0f, 4.0f, ColorUtil.withAlpha(this.accentColor(), 0.6f));
            if (e instanceof AbstractClientPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer)e;
                PlayerFaceRenderer.draw((GuiGraphics)ctx, (PlayerSkin)player.getSkin(), (int)5, (int)5, (int)Math.round(av));
            } else {
                Render2D.roundRect(ctx, 5.0f, 5.0f, av, av, 3.0f, Paint.linear(5.0f, 5.0f, 5.0f + av, 5.0f + av, t.accent(), t.accent2()));
                Fonts.drawCentered(ctx, name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase(Locale.ROOT), 5.0f + av / 2.0f, 5.0f + av / 2.0f - 6.0f, 12.0f, -1, true);
            }
            x = av + 12.0f;
        }
        float tw = w - x - 6.0f;
        Fonts.draw(ctx, Fonts.trim(name, tw, 8.0f, true), x, this.compact() ? 4.0f : 6.0f, 8.0f, t.text(), true);
        float bx = w - 2.0f;
        int pops = PvpTweaks.pops((Entity)e);
        if (pops > 0) {
            bx = TargetHud.badge(ctx, "\u0442\u043e\u0442\u0435\u043c \u00d7" + pops, bx, -11.0f, ColorUtil.withAlpha(this.accentColor(), 0.75f), -1);
        }
        if ((shield = ShieldStatus.secondsLeft(e)) > 0.0f) {
            TargetHud.badge(ctx, String.format(Locale.ROOT, "\u0449\u0438\u0442 \u0441\u043b\u043e\u043c\u0430\u043d %.1f\u0441", Float.valueOf(shield)), bx, -11.0f, -421514161, -1);
        }
        float ty = this.compact() ? 14.0f : 17.0f;
        String hpText = String.format(Locale.ROOT, "%.1f", Float.valueOf(hp));
        if (((Boolean)this.hearts.get()).booleanValue() && !this.compact()) {
            int full = Math.min(10, (int)Math.ceil(hp / 2.0f));
            for (int i = 0; i < 10; ++i) {
                int col = i < full ? -45686 : ColorUtil.withAlpha(t.textMuted(), 0.6f);
                Render2D.heart(ctx, x + 3.0f + (float)i * 6.6f, ty + 3.0f, 2.8f, col);
            }
            ty += 9.0f;
        }
        Fonts.draw(ctx, hpText, x, ty, 7.0f, t.text(), true);
        Render2D.heart(ctx, x + Fonts.width(hpText, 7.0f, true) + 5.0f, ty + 3.3f, 2.8f, -45686);
        float by = h - 6.0f;
        float bw = tw;
        Render2D.roundRect(ctx, x, by, bw, 3.0f, 1.5f, ColorUtil.withAlpha(t.track(), 0.9f));
        int low = -770210;
        Render2D.roundRect(ctx, x, by, Math.max(3.0f, bw * bar), 3.0f, 1.5f, Paint.horizontal(x, x + bw, ColorUtil.lerp(low, this.accentColor(), bar), ColorUtil.lerp(low, t.accent2(), bar)));
        m.popMatrix();
        Render2D.alpha = prev;
    }
}

