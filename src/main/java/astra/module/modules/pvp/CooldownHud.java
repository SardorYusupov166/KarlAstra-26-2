/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemCooldowns
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.pvp;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.joml.Matrix3x2fStack;

public class CooldownHud
extends HudModule {
    private static final Item[] ITEMS = new Item[]{Items.ENDER_PEARL, Items.CHORUS_FRUIT, Items.WIND_CHARGE, Items.SHIELD, Items.GOAT_HORN, Items.TRIDENT, Items.MACE};
    private static final float CELL = 22.0f;
    private final BooleanSetting attack = this.add(new BooleanSetting("\u0417\u0430\u0440\u044f\u0434\u043a\u0430 \u0443\u0434\u0430\u0440\u0430", true));
    private final BooleanSetting attackOnlyCharging = this.add(new BooleanSetting("\u0423\u0434\u0430\u0440 \u2014 \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0437\u0430\u0440\u044f\u0434\u043a\u0435", true));
    private final BooleanSetting items = this.add(new BooleanSetting("\u041e\u0442\u043a\u0430\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", true));
    private final BooleanSetting percent = this.add(new BooleanSetting("\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043f\u0440\u043e\u0446\u0435\u043d\u0442\u044b", false));

    public CooldownHud() {
        super("cooldowns", "\u041a\u0443\u043b\u0434\u0430\u0443\u043d\u044b", "\u0417\u0430\u0440\u044f\u0434\u043a\u0430 \u0443\u0434\u0430\u0440\u0430 \u0438 \u043e\u0442\u043a\u0430\u0442 \u043f\u0435\u0440\u043b\u0430, \u0445\u043e\u0440\u0443\u0441\u0430, \u0449\u0438\u0442\u0430 \u0440\u044f\u0434\u043e\u043c \u0441 \u043f\u0440\u0438\u0446\u0435\u043b\u043e\u043c.", Category.PVP, Icon.CLOCK, 0.5f, 0.56f);
        this.setDefaultEnabled(true);
    }

    private List<Entry> entries(boolean editor) {
        ArrayList<Entry> list = new ArrayList<Entry>();
        if (CooldownHud.mc.player != null) {
            if (((Boolean)this.attack.get()).booleanValue()) {
                float charge = CooldownHud.mc.player.getAttackStrengthScale(0.0f);
                if (!((Boolean)this.attackOnlyCharging.get()).booleanValue() || charge < 1.0f) {
                    list.add(new Entry(CooldownHud.mc.player.getMainHandItem(), 1.0f - charge, true));
                }
            }
            if (((Boolean)this.items.get()).booleanValue()) {
                ItemCooldowns cd = CooldownHud.mc.player.getCooldowns();
                for (Item item : ITEMS) {
                    ItemStack stack = new ItemStack((ItemLike)item);
                    float p = cd.getCooldownPercent(stack, mc.getDeltaTracker().getGameTimeDeltaPartialTick(true));
                    if (!(p > 0.0f)) continue;
                    list.add(new Entry(stack, p, false));
                }
            }
        }
        if (list.isEmpty() && editor) {
            list.add(new Entry(new ItemStack((ItemLike)Items.NETHERITE_SWORD), 0.45f, true));
            list.add(new Entry(new ItemStack((ItemLike)Items.ENDER_PEARL), 0.7f, false));
        }
        return list;
    }

    @Override
    public boolean shouldRender() {
        return !this.entries(false).isEmpty();
    }

    @Override
    public float width() {
        return (float)Math.max(1, this.entries(true).size()) * 22.0f + 4.0f;
    }

    @Override
    public float height() {
        return 26.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        Theme t = CooldownHud.theme();
        List<Entry> list = this.entries(editor);
        for (int i = 0; i < list.size(); ++i) {
            Entry e = list.get(i);
            float cx = 2.0f + (float)i * 22.0f + 11.0f;
            float cy = 13.0f;
            float r = 9.5f;
            int col = e.attack ? ColorUtil.lerp(this.accentColor(), -770210, e.progress) : this.accentColor();
            Render2D.ring(ctx, cx, cy, r, 2.0f, ColorUtil.withAlpha(t.track(), 0.8f));
            Render2D.arc(ctx, cx, cy, r, 2.0f, -90.0f, 360.0f * e.progress, col);
            if (e.stack.isEmpty()) {
                Render2D.circle(ctx, cx, cy, 3.0f, col);
            } else {
                Matrix3x2fStack m = ctx.pose();
                m.pushMatrix();
                m.translate(cx - 6.0f, cy - 6.0f);
                m.scale(0.75f, 0.75f);
                ctx.renderItem(e.stack, 0, 0);
                m.popMatrix();
            }
            if (!((Boolean)this.percent.get()).booleanValue()) continue;
            String s = Math.round(e.progress * 100.0f) + "%";
            Fonts.drawCentered(ctx, s, cx, cy + r - 3.0f, 5.0f, t.text(), true);
        }
    }

    private record Entry(ItemStack stack, float progress, boolean attack) {
    }
}

