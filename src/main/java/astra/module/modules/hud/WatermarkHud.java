/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.module.modules.hud;

import astra.gui.screens.ClickGuiScreen;
import astra.hud.HudModule;
import astra.module.Category;
import astra.render.Fonts;
import astra.render.Icon;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class WatermarkHud
extends HudModule {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final ModeSetting info = this.add(new ModeSetting("\u041f\u043e\u0434\u043f\u0438\u0441\u044c", "\u041d\u0435\u0442", "\u041d\u0435\u0442", "\u041d\u0438\u043a", "\u0412\u0440\u0435\u043c\u044f", "FPS"));

    public WatermarkHud() {
        super("watermark", "\u0412\u043e\u0434\u044f\u043d\u043e\u0439 \u0437\u043d\u0430\u043a", "\u041b\u043e\u0433\u043e\u0442\u0438\u043f Astra \u0432 \u0443\u0433\u043b\u0443 \u044d\u043a\u0440\u0430\u043d\u0430.", Category.HUD, Icon.LOGO, 0.07f, 0.05f);
        this.setDefaultEnabled(true);
    }

    private String infoText() {
        return switch ((String)this.info.get()) {
            case "\u041d\u0438\u043a" -> mc.getUser().getName();
            case "\u0412\u0440\u0435\u043c\u044f" -> LocalTime.now().format(TIME);
            case "FPS" -> mc.getFps() + " FPS";
            default -> "";
        };
    }

    @Override
    public float width() {
        String s = this.infoText();
        return Math.max(106.0f, s.isEmpty() ? 0.0f : Fonts.width(s, 6.5f, true) + 16.0f);
    }

    @Override
    public float height() {
        return this.infoText().isEmpty() ? 44.0f : 54.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = WatermarkHud.theme();
        ClickGuiScreen.drawLogo(ctx, t, 4.0f, 3.0f, 0.56f);
        String s = this.infoText();
        if (!s.isEmpty()) {
            Fonts.drawCentered(ctx, s, w / 2.0f, 43.0f, 6.5f, t.textDim(), true);
        }
    }
}

