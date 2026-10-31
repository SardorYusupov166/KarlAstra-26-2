/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.world.entity.EquipmentSlot
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
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import astra.utils.ItemPreview;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class ArmorHud
extends HudModule {
    private static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final ModeSetting layout = this.add(new ModeSetting("\u0420\u0430\u0441\u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435", "\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u043e", "\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u043e", "\u0413\u043e\u0440\u0438\u0437\u043e\u043d\u0442\u0430\u043b\u044c\u043d\u043e"));
    private final BooleanSetting mainHand = this.add(new BooleanSetting("\u041f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u0440\u0443\u043a\u0435", true));
    private final ModeSetting value = this.add(new ModeSetting("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435", "\u041f\u0440\u043e\u0446\u0435\u043d\u0442\u044b", "\u041f\u0440\u043e\u0446\u0435\u043d\u0442\u044b", "\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c", "\u041d\u0435\u0442"));

    public ArmorHud() {
        super("armor", "\u0421\u0442\u0430\u0442\u0443\u0441 \u0431\u0440\u043e\u043d\u0438", "\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c \u0431\u0440\u043e\u043d\u0438 \u0438 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430 \u0432 \u0440\u0443\u043a\u0435.", Category.PVP, Icon.SHIELD, 0.62f, 0.72f);
        this.setDefaultEnabled(true);
    }

    private List<ItemStack> items(boolean editor) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if (ArmorHud.mc.player != null) {
            for (EquipmentSlot slot : SLOTS) {
                ItemStack s = ArmorHud.mc.player.getItemBySlot(slot);
                if (s.isEmpty()) continue;
                list.add(s);
            }
            if (((Boolean)this.mainHand.get()).booleanValue() && !ArmorHud.mc.player.getMainHandItem().isEmpty()) {
                list.add(ArmorHud.mc.player.getMainHandItem());
            }
        }
        if (list.isEmpty() && editor) {
            list.add(ItemPreview.stack(Items.NETHERITE_HELMET));
            list.add(ItemPreview.stack(Items.NETHERITE_CHESTPLATE));
            list.add(ItemPreview.stack(Items.NETHERITE_LEGGINGS));
            list.add(ItemPreview.stack(Items.NETHERITE_BOOTS));
        }
        return list;
    }

    @Override
    public boolean shouldRender() {
        return !this.items(false).isEmpty();
    }

    private boolean vertical() {
        return this.layout.is("\u0412\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u044c\u043d\u043e");
    }

    private String text(ItemStack s) {
        if (!s.isDamageableItem() || this.value.is("\u041d\u0435\u0442")) {
            return this.value.is("\u041d\u0435\u0442") ? "" : (s.getCount() > 1 ? "x" + s.getCount() : "");
        }
        int left = s.getMaxDamage() - s.getDamageValue();
        return this.value.is("\u041f\u0440\u043e\u0446\u0435\u043d\u0442\u044b") ? Math.round((float)left * 100.0f / (float)s.getMaxDamage()) + "%" : String.valueOf(left);
    }

    private float cellW() {
        return this.value.is("\u041d\u0435\u0442") ? 20.0f : 44.0f;
    }

    @Override
    public float width() {
        int n = Math.max(1, this.items(true).size());
        return this.vertical() ? this.cellW() + 6.0f : (float)n * this.cellW() + 6.0f;
    }

    @Override
    public float height() {
        int n = Math.max(1, this.items(true).size());
        return this.vertical() ? (float)(n * 18 + 6) : 24.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = ArmorHud.theme();
        List<ItemStack> list = this.items(editor);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack s = list.get(i);
            float x = this.vertical() ? 3.0f : 3.0f + (float)i * this.cellW();
            float y = this.vertical() ? (float)(3 + i * 18) : 4.0f;
            ctx.item(s, Math.round(x + 1.0f), Math.round(y + 1.0f));
            String txt = this.text(s);
            if (txt.isEmpty()) continue;
            int col = t.text();
            if (s.isDamageableItem()) {
                float pct = (float)(s.getMaxDamage() - s.getDamageValue()) / (float)s.getMaxDamage();
                col = pct > 0.5f ? ColorUtil.lerp(-340971, -11870592, (pct - 0.5f) * 2.0f) : ColorUtil.lerp(-770210, -340971, pct * 2.0f);
            }
            Fonts.draw(ctx, txt, x + 20.0f, y + 5.0f, 7.0f, col, true);
        }
    }
}

