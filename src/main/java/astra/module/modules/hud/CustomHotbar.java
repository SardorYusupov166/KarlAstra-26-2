/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 */
package astra.module.modules.hud;

import astra.animation.Animation;
import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.theme.Theme;
import astra.utils.FrameStats;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class CustomHotbar
extends HudModule {
    private static final float SLOT = 20.0f;
    private static final float GAP = 2.0f;
    private final BooleanSetting numbers = this.add(new BooleanSetting("\u041d\u043e\u043c\u0435\u0440\u0430 \u0441\u043b\u043e\u0442\u043e\u0432", false));
    private final BooleanSetting smooth = this.add(new BooleanSetting("\u041f\u043b\u0430\u0432\u043d\u044b\u0439 \u0432\u044b\u0431\u043e\u0440", true));
    private final Animation selected = new Animation(0.0f, 22.0f);
    private static final ItemStack[] SAMPLE = new ItemStack[]{new ItemStack((ItemLike)Items.field_22022), new ItemStack((ItemLike)Items.field_8102), new ItemStack((ItemLike)Items.field_8377), new ItemStack((ItemLike)Items.field_8556), new ItemStack((ItemLike)Items.field_8634, 16), new ItemStack((ItemLike)Items.field_8463, 8), new ItemStack((ItemLike)Items.field_27064, 32), new ItemStack((ItemLike)Items.field_8574), new ItemStack((ItemLike)Items.field_8705)};

    public CustomHotbar() {
        super("custom_hotbar", "\u0410\u043d\u0438\u043c\u0435-\u0445\u043e\u0442\u0431\u0430\u0440", "\u0421\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u0439 \u0445\u043e\u0442\u0431\u0430\u0440 \u0441\u043e \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435\u043c \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u043e\u0433\u043e \u0441\u043b\u043e\u0442\u0430.", Category.HUD, Icon.LAYERS, 0.5f, 0.955f);
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    public boolean canReplace() {
        return CustomHotbar.mc.field_1724 != null && !CustomHotbar.mc.field_1724.method_7325() && CustomHotbar.mc.field_1761 != null;
    }

    @Override
    public boolean shouldRender() {
        return this.canReplace();
    }

    @Override
    public float width() {
        return 204.0f;
    }

    @Override
    public float height() {
        return 28.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        ItemStack off;
        float pos;
        Theme t = CustomHotbar.theme();
        int acc = this.accentColor();
        LocalPlayer player = CustomHotbar.mc.field_1724;
        int sel = player != null ? player.method_31548().method_67532() : 0;
        float target = sel;
        float f = pos = (Boolean)this.smooth.get() != false ? this.selected.update(target, FrameStats.delta()) : target;
        if (!((Boolean)this.smooth.get()).booleanValue()) {
            this.selected.set(target);
        }
        Render2D.roundRect(ctx, 0.0f, 0.0f, w, h, 7.0f, Paint.vertical(0.0f, h, ColorUtil.withAlpha(t.bg2(), 0.78f), ColorUtil.withAlpha(t.bg(), 0.85f)));
        Render2D.outline(ctx, 0.0f, 0.0f, w, h, 7.0f, 0.8f, Paint.horizontal(0.0f, w, ColorUtil.withAlpha(acc, 0.7f), ColorUtil.withAlpha(t.accent2(), 0.5f)));
        for (int i = 0; i < 9; ++i) {
            float sx = 4.0f + (float)i * 22.0f;
            Render2D.roundRect(ctx, sx, 4.0f, 20.0f, 20.0f, 4.0f, ColorUtil.withAlpha(t.bg(), 0.55f));
            Render2D.outline(ctx, sx, 4.0f, 20.0f, 20.0f, 4.0f, 0.6f, ColorUtil.withAlpha(acc, 0.25f));
        }
        float hx = 4.0f + pos * 22.0f;
        Render2D.glow(ctx, hx, 4.0f, 20.0f, 20.0f, 4.0f, 6.0f, ColorUtil.withAlpha(acc, 0.55f));
        Render2D.roundRect(ctx, hx, 4.0f, 20.0f, 20.0f, 4.0f, Paint.vertical(4.0f, 24.0f, ColorUtil.withAlpha(acc, 0.45f), ColorUtil.withAlpha(t.accent2(), 0.25f)));
        Render2D.outline(ctx, hx, 4.0f, 20.0f, 20.0f, 4.0f, 1.1f, ColorUtil.withAlpha(0xFFFFFF, 0.85f));
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = player != null ? player.method_31548().method_5438(i) : SAMPLE[i];
            float sx = 4.0f + (float)i * 22.0f;
            if (!stack.method_7960()) {
                int ix = Math.round(sx + 2.0f);
                int iy = Math.round(6.0f);
                ctx.method_51427(stack, ix, iy);
                ctx.method_51431(CustomHotbar.mc.field_1772, stack, ix, iy);
            }
            if (!((Boolean)this.numbers.get()).booleanValue()) continue;
            Fonts.draw(ctx, String.valueOf(i + 1), sx + 1.5f, 4.5f, 5.0f, ColorUtil.withAlpha(t.textDim(), 0.8f), true);
        }
        if (player != null && !(off = player.method_6079()).method_7960()) {
            boolean left = player.method_6068() == HumanoidArm.field_6183;
            float ox = left ? -26.0f : w + 6.0f;
            Render2D.roundRect(ctx, ox, 4.0f, 20.0f, 20.0f, 4.0f, ColorUtil.withAlpha(t.bg(), 0.75f));
            Render2D.outline(ctx, ox, 4.0f, 20.0f, 20.0f, 4.0f, 0.8f, ColorUtil.withAlpha(acc, 0.5f));
            ctx.method_51427(off, Math.round(ox + 2.0f), 6);
            ctx.method_51431(CustomHotbar.mc.field_1772, off, Math.round(ox + 2.0f), 6);
        }
    }

    private static int selectedSlot(Inventory inventory) {
        return inventory.method_67532();
    }
}

