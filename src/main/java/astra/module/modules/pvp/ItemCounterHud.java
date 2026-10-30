/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 */
package astra.module.modules.pvp;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class ItemCounterHud
extends HudModule {
    private final ModeSetting layout = this.add(new ModeSetting("\u0420\u0430\u0441\u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u043e", "\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u043e", "\u0413\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u044c\u043d\u043e"));
    private final BooleanSetting hideZero = this.add(new BooleanSetting("\u0421\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043d\u0443\u043b\u0435\u0432\u044b\u0435", false));
    private final NumberSetting lowAt = this.add(new NumberSetting("\u041c\u0438\u0433\u0430\u0442\u044c, \u043a\u043e\u0433\u0434\u0430 \u043c\u0435\u043d\u044c\u0448\u0435", 2.0, 0.0, 16.0, 1.0));
    private final List<Kind> kinds = new ArrayList<Kind>();
    private final List<int[]> counts = new ArrayList<int[]>();

    public ItemCounterHud() {
        super("item_counter", "\u0421\u0447\u0451\u0442\u0447\u0438\u043a \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", "\u0422\u043e\u0442\u0435\u043c\u044b, \u043f\u0435\u0440\u043b\u044b, \u044f\u0431\u043b\u043e\u043a\u0438, \u0441\u0442\u0440\u0435\u043b\u044b \u0438 \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435.", Category.PVP, Icon.GRID, 0.06f, 0.5f);
        this.kind("\u0422\u043e\u0442\u0435\u043c\u044b", Items.field_8288, true, Items.field_8288);
        this.kind("\u041f\u0435\u0440\u043b\u044b", Items.field_8634, true, Items.field_8634);
        this.kind("\u0417\u043e\u043b\u043e\u0442\u044b\u0435 \u044f\u0431\u043b\u043e\u043a\u0438", Items.field_8463, true, Items.field_8463);
        this.kind("\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0435 \u044f\u0431\u043b\u043e\u043a\u0438", Items.field_8367, true, Items.field_8367);
        this.kind("\u0421\u0442\u0440\u0435\u043b\u044b", Items.field_8107, true, Items.field_8107, Items.field_8236, Items.field_8087);
        this.kind("\u041a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u044b", Items.field_8301, false, Items.field_8301);
        this.kind("\u041e\u0431\u0441\u0438\u0434\u0438\u0430\u043d", Items.field_8281, false, Items.field_8281);
        this.kind("\u0425\u043e\u0440\u0443\u0441", Items.field_8233, false, Items.field_8233);
        this.kind("\u0417\u0430\u0440\u044f\u0434\u044b \u0432\u0435\u0442\u0440\u0430", Items.field_49098, false, Items.field_49098);
        this.kind("\u0411\u0443\u0442\u044b\u043b\u044c\u043a\u0438 \u043e\u043f\u044b\u0442\u0430", Items.field_8287, false, Items.field_8287);
        this.setDefaultEnabled(true);
    }

    private void kind(String name, Item icon, boolean on, Item ... items) {
        this.kinds.add(new Kind(name, icon, items, this.add(new BooleanSetting(name, on))));
    }

    private static int count(Inventory inv, Item[] items) {
        int n = 0;
        for (int i = 0; i < inv.method_5439(); ++i) {
            ItemStack s = inv.method_5438(i);
            for (Item item : items) {
                if (!s.method_31574(item)) continue;
                n += s.method_7947();
            }
        }
        return n;
    }

    private List<int[]> rows(boolean editor) {
        this.counts.clear();
        Inventory inv = ItemCounterHud.mc.field_1724 != null ? ItemCounterHud.mc.field_1724.method_31548() : null;
        for (int k = 0; k < this.kinds.size(); ++k) {
            int n;
            Kind kind = this.kinds.get(k);
            if (!((Boolean)kind.enabled.get()).booleanValue()) continue;
            int n2 = n = inv != null ? ItemCounterHud.count(inv, kind.items) : 0;
            if (editor && inv == null) {
                n = 1 + k * 3;
            }
            if (n == 0 && ((Boolean)this.hideZero.get()).booleanValue() && !editor) continue;
            this.counts.add(new int[]{k, n});
        }
        return this.counts;
    }

    private boolean vertical() {
        return this.layout.is("\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u043e");
    }

    @Override
    public boolean shouldRender() {
        return !this.rows(false).isEmpty();
    }

    @Override
    public float width() {
        int n = Math.max(1, this.rows(true).size());
        return this.vertical() ? 44.0f : (float)(n * 38 + 4);
    }

    @Override
    public float height() {
        int n = Math.max(1, this.rows(true).size());
        return this.vertical() ? (float)(n * 18 + 4) : 22.0f;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        Theme t = ItemCounterHud.theme();
        List<int[]> list = this.rows(editor);
        float blink = 0.5f + 0.5f * (float)Math.sin((double)System.currentTimeMillis() / 130.0);
        for (int i = 0; i < list.size(); ++i) {
            boolean low;
            Kind kind = this.kinds.get(list.get(i)[0]);
            int n = list.get(i)[1];
            float x = this.vertical() ? 2.0f : (float)(2 + i * 38);
            float y = this.vertical() ? (float)(2 + i * 18) : 3.0f;
            boolean bl = low = n < this.lowAt.getInt();
            if (low) {
                Render2D.roundRect(ctx, x, y, this.vertical() ? w - 4.0f : 36.0f, 16.0f, 4.0f, ColorUtil.withAlpha(-770210, (n == 0 ? 0.18f : 0.1f) + 0.22f * blink));
            }
            ctx.method_51427(new ItemStack((ItemLike)kind.icon), Math.round(x + 1.0f), Math.round(y));
            int col = n == 0 ? -770210 : (low ? ColorUtil.lerp(-340971, -770210, blink) : t.text());
            Fonts.draw(ctx, String.valueOf(n), x + 20.0f, y + 4.5f, 7.5f, col, true);
        }
    }

    private record Kind(String name, Item icon, Item[] items, BooleanSetting enabled) {
    }
}

