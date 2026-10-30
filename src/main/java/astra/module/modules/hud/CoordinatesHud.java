/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.util.Util
 *  net.minecraft.world.level.Level
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.render.Fonts;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;

public class CoordinatesHud
extends HudModule {
    private final ModeSetting format = this.add(new ModeSetting("\u0424\u043e\u0440\u043c\u0430\u0442", "X: Y: Z", "X: Y: Z", "\u041e\u0434\u043d\u0430 \u0441\u0442\u0440\u043e\u043a\u0430"));
    private final BooleanSetting biome = this.add(new BooleanSetting("\u0411\u0438\u043e\u043c", true));
    private final BooleanSetting nether = this.add((BooleanSetting)new BooleanSetting("\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u041d\u0435\u0437\u0435\u0440\u0430", false).desc("\u041f\u0435\u0440\u0435\u0441\u0447\u0451\u0442 X/Z \u0434\u043b\u044f \u041d\u0435\u0437\u0435\u0440\u0430 \u0438\u043b\u0438 \u0412\u0435\u0440\u0445\u043d\u0435\u0433\u043e \u043c\u0438\u0440\u0430"));

    public CoordinatesHud() {
        super("coordinates", "\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b", "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442 \u0442\u0435\u043a\u0443\u0449\u0438\u0435 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b.", Category.MOVEMENT, Icon.PIN, 0.92f, 0.37f);
        this.setDefaultEnabled(true);
    }

    public static String biomeName() {
        if (CoordinatesHud.mc.level == null || CoordinatesHud.mc.player == null) {
            return "\u0412\u0438\u0448\u043d\u0451\u0432\u044b\u0435 \u0445\u043e\u043b\u043c\u044b";
        }
        BlockPos pos = CoordinatesHud.mc.player.blockPosition();
        return CoordinatesHud.mc.level.getBiome(pos).unwrapKey().map(ResourceKey::identifier).map(id -> Component.translatable((String)Util.makeDescriptionId((String)"biome", (Identifier)id)).getString()).orElse("?");
    }

    private List<String> lines() {
        ArrayList<String> out = new ArrayList<String>();
        double x = 1236.0;
        double y = 98.0;
        double z = -452.0;
        if (CoordinatesHud.mc.player != null) {
            x = CoordinatesHud.mc.player.getX();
            y = CoordinatesHud.mc.player.getY();
            z = CoordinatesHud.mc.player.getZ();
        }
        if (this.format.is("\u041e\u0434\u043d\u0430 \u0441\u0442\u0440\u043e\u043a\u0430")) {
            out.add(String.format("%d  %d  %d", (int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)));
        } else {
            out.add("X: " + (int)Math.floor(x));
            out.add("Y: " + (int)Math.floor(y));
            out.add("Z: " + (int)Math.floor(z));
        }
        if (((Boolean)this.nether.get()).booleanValue() && CoordinatesHud.mc.level != null) {
            boolean inNether = CoordinatesHud.mc.level.dimension() == Level.NETHER;
            double k = inNether ? 8.0 : 0.125;
            out.add((inNether ? "\u041c\u0438\u0440: " : "\u041d\u0435\u0437\u0435\u0440: ") + (int)Math.floor(x * k) + ", " + (int)Math.floor(z * k));
        }
        if (((Boolean)this.biome.get()).booleanValue()) {
            out.add("\u0411\u0438\u043e\u043c: " + CoordinatesHud.biomeName());
        }
        return out;
    }

    @Override
    public float width() {
        float max = 50.0f;
        for (String s : this.lines()) {
            max = Math.max(max, Fonts.width(s, 7.5f, true));
        }
        return max + 14.0f;
    }

    @Override
    public float height() {
        return this.lines().size() * 10 + 8;
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        Theme t = CoordinatesHud.theme();
        float y = 5.0f;
        for (String line : this.lines()) {
            int sep = line.indexOf(58);
            if (sep > 0 && sep < 6) {
                String key = line.substring(0, sep + 1);
                Fonts.draw(ctx, key, 7.0f, y, 7.5f, this.accentColor(), true);
                Fonts.draw(ctx, line.substring(sep + 1), 7.0f + Fonts.width(key, 7.5f, true), y, 7.5f, t.text(), true);
            } else {
                Fonts.draw(ctx, line, 7.0f, y, 7.5f, t.text(), true);
            }
            y += 10.0f;
        }
    }
}

