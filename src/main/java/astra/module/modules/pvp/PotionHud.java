/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.renderer.RenderPipelines
 *  net.minecraft.core.Holder
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffectUtil
 *  net.minecraft.world.effect.MobEffects
 */
package astra.module.modules.pvp;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;

public class PotionHud
extends HudModule {
    private final ModeSetting sort = this.add(new ModeSetting("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430", "\u041f\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438", "\u041f\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438", "\u041f\u043e \u0438\u043c\u0435\u043d\u0438"));
    private final BooleanSetting showTime = this.add(new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u0432\u0440\u0435\u043c\u044f", true));
    private final BooleanSetting compact = this.add(new BooleanSetting("\u041a\u043e\u043c\u043f\u0430\u043a\u0442\u043d\u043e", false));
    private final BooleanSetting hideVanilla = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0442\u044c \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u044b\u0435 \u0438\u043a\u043e\u043d\u043a\u0438", true));

    public PotionHud() {
        super("potions", "\u042d\u0444\u0444\u0435\u043a\u0442\u044b \u0437\u0435\u043b\u0438\u0439", "\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0435 \u044d\u0444\u0444\u0435\u043a\u0442\u044b, \u0443\u0440\u043e\u0432\u0435\u043d\u044c \u0438 \u043e\u0441\u0442\u0430\u0432\u0448\u0435\u0435\u0441\u044f \u0432\u0440\u0435\u043c\u044f.", Category.PVP, Icon.POTION, 0.06f, 0.82f);
        this.setDefaultEnabled(true);
    }

    public boolean hidesVanilla() {
        return this.isEnabled() && (Boolean)this.hideVanilla.get() != false;
    }

    private static String roman(int level) {
        String[] r = new String[]{"", "", " II", " III", " IV", " V", " VI", " VII", " VIII", " IX", " X"};
        return level < r.length ? r[level] : " " + level;
    }

    private List<Entry> entries(boolean editor) {
        ArrayList<Entry> list = new ArrayList<Entry>();
        if (PotionHud.mc.player != null && PotionHud.mc.level != null) {
            ArrayList<MobEffectInstance> effects = new ArrayList<MobEffectInstance>(PotionHud.mc.player.getActiveEffects());
            if (this.sort.is("\u041f\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438")) {
                effects.sort(Comparator.comparingInt(MobEffectInstance::getDuration));
            } else {
                effects.sort(Comparator.comparing(e -> Component.translatable((String)e.getDescriptionId()).getString()));
            }
            float tickRate = PotionHud.mc.level.tickRateManager().tickrate();
            for (MobEffectInstance e2 : effects) {
                String name = Component.translatable((String)e2.getDescriptionId()).getString() + PotionHud.roman(e2.getAmplifier() + 1);
                String time = MobEffectUtil.formatDuration((MobEffectInstance)e2, (float)1.0f, (float)tickRate).getString();
                list.add(new Entry((Holder<MobEffect>)e2.getEffect(), name, time, e2.isAmbient()));
            }
        }
        if (list.isEmpty() && editor) {
            list.add(new Entry((Holder<MobEffect>)MobEffects.SPEED, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c II", "0:16", false));
            list.add(new Entry((Holder<MobEffect>)MobEffects.STRENGTH, "\u0421\u0438\u043b\u0430 II", "0:16", false));
            list.add(new Entry((Holder<MobEffect>)MobEffects.ABSORPTION, "\u041f\u043e\u0433\u043b\u043e\u0449\u0435\u043d\u0438\u0435", "1:52", false));
            list.add(new Entry((Holder<MobEffect>)MobEffects.FIRE_RESISTANCE, "\u041e\u0433\u043d\u0435\u0441\u0442\u043e\u0439\u043a\u043e\u0441\u0442\u044c", "4:52", false));
        }
        return list;
    }

    @Override
    public boolean shouldRender() {
        return PotionHud.mc.player != null && !PotionHud.mc.player.getActiveEffects().isEmpty();
    }

    private float rowH() {
        return (Boolean)this.compact.get() != false ? 13.0f : 21.0f;
    }

    @Override
    public float width() {
        float max = 60.0f;
        for (Entry e : this.entries(true)) {
            float w = Fonts.width(e.name, 7.0f, true);
            if (((Boolean)this.compact.get()).booleanValue() && ((Boolean)this.showTime.get()).booleanValue()) {
                w += Fonts.width("  " + e.time, 7.0f, false);
            }
            max = Math.max(max, w);
        }
        return max + (float)((Boolean)this.compact.get() != false ? 22 : 30);
    }

    @Override
    public float height() {
        return (float)Math.max(1, this.entries(true).size()) * this.rowH() + 6.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = PotionHud.theme();
        float y = 3.0f;
        boolean small = (Boolean)this.compact.get();
        for (Entry e : this.entries(editor)) {
            float tx;
            int icon = small ? 10 : 16;
            ctx.blitSprite(RenderPipelines.GUI_TEXTURED, net.minecraft.client.gui.Hud.getMobEffectSprite(e.type), 4, Math.round(y + (this.rowH() - (float)icon) / 2.0f), icon, icon);
            float f = tx = small ? 18.0f : 25.0f;
            if (small) {
                Fonts.draw(ctx, e.name, tx, y + 3.0f, 7.0f, t.text(), true);
                if (((Boolean)this.showTime.get()).booleanValue()) {
                    Fonts.draw(ctx, e.time, tx + Fonts.width(e.name + "  ", 7.0f, true), y + 3.0f, 7.0f, t.textDim(), false);
                }
            } else {
                Fonts.draw(ctx, e.name, tx, y + 2.5f, 7.0f, t.text(), true);
                if (((Boolean)this.showTime.get()).booleanValue()) {
                    Fonts.draw(ctx, e.time, tx, y + 11.0f, 6.5f, ColorUtil.lerp(t.textDim(), this.accentColor(), 0.4f), false);
                }
            }
            y += this.rowH();
        }
    }

    private record Entry(Holder<MobEffect> type, String name, String time, boolean ambient) {
    }
}

