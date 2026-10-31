/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.module.modules.hud;

import astra.animation.Animation;
import astra.hud.HudModule;
import astra.module.Category;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import astra.utils.FrameStats;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class StateIndicator
extends HudModule {
    protected final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c", "\u0418\u043a\u043e\u043d\u043a\u0430 + \u0442\u0435\u043a\u0441\u0442", "\u0418\u043a\u043e\u043d\u043a\u0430", "\u0422\u0435\u043a\u0441\u0442", "\u0418\u043a\u043e\u043d\u043a\u0430 + \u0442\u0435\u043a\u0441\u0442"));
    protected final BooleanSetting onlyActive = this.add(new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u043a\u043e\u0433\u0434\u0430 \u0430\u043a\u0442\u0438\u0432\u043d\u043e", true));
    private final Animation active = new Animation(0.0f, 12.0f);
    private final Icon stateIcon;

    protected StateIndicator(String id, String name, String description, Icon icon, float x, float y) {
        super(id, name, description, Category.MOVEMENT, icon, x, y);
        this.stateIcon = icon;
    }

    protected abstract boolean isActive();

    protected abstract String text();

    @Override
    public boolean shouldRender() {
        return (Boolean)this.onlyActive.get() == false || this.isActive() || this.active.get() > 0.02f;
    }

    @Override
    public float width() {
        return switch ((String)this.style.get()) {
            case "\u0418\u043a\u043e\u043d\u043a\u0430" -> 20.0f;
            case "\u0422\u0435\u043a\u0441\u0442" -> Fonts.width(this.text(), 7.5f, true) + 12.0f;
            default -> Fonts.width(this.text(), 7.5f, true) + 28.0f;
        };
    }

    @Override
    public float height() {
        return 18.0f;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = StateIndicator.theme();
        float a = this.active.update(this.isActive() || editor ? 1.0f : 0.0f, FrameStats.delta());
        int col = ColorUtil.lerp(t.textMuted(), this.accentColor(), a);
        String s = (String)this.style.get();
        float x = 6.0f;
        if (!s.equals("\u0422\u0435\u043a\u0441\u0442")) {
            Icons.draw(ctx, this.stateIcon, s.equals("\u0418\u043a\u043e\u043d\u043a\u0430") ? (w - 11.0f) / 2.0f : x, (h - 11.0f) / 2.0f, 11.0f, col);
            x += 16.0f;
        }
        if (!s.equals("\u0418\u043a\u043e\u043d\u043a\u0430")) {
            Fonts.draw(ctx, this.text(), x, (h - 7.5f) / 2.0f + 0.4f, 7.5f, ColorUtil.lerp(t.textDim(), t.text(), a), true);
        }
    }
}

