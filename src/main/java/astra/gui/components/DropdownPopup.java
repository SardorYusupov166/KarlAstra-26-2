/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.Popup;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DropdownPopup
extends Popup {
    private static final float ITEM_H = 14.0f;
    private static final int MAX_VISIBLE = 9;
    private final List<String> options;
    private final Supplier<String> current;
    private final Consumer<String> onSelect;
    private final Runnable onClose;
    private float scroll;
    private final List<Animation> hovers = new ArrayList<Animation>();

    public DropdownPopup(float x, float y, float w, List<String> options, Supplier<String> current, Consumer<String> onSelect, Runnable onClose) {
        this.options = options;
        this.current = current;
        this.onSelect = onSelect;
        this.onClose = onClose;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = (float)Math.min(options.size(), 9) * 14.0f + 6.0f;
        for (int i = 0; i < options.size(); ++i) {
            this.hovers.add(new Animation(0.0f, 20.0f));
        }
        int idx = options.indexOf(current.get());
        if (idx >= 9) {
            this.scroll = Math.min(((float)idx - 4.5f) * 14.0f, this.maxScroll());
        }
    }

    private float maxScroll() {
        return Math.max(0.0f, (float)this.options.size() * 14.0f - (this.h - 6.0f));
    }

    @Override
    public void render(UiContext ui) {
        Theme t = ui.theme;
        float a = this.open.update(1.0f, ui.dt);
        float prevAlpha = Render2D.alpha;
        Render2D.alpha *= a;
        float dy = (1.0f - a) * -4.0f;
        Render2D.shadow(ui.ctx, this.x, this.y + dy, this.w, this.h, 5.0f, 8.0f, Integer.MIN_VALUE);
        Render2D.roundRect(ui.ctx, this.x, this.y + dy, this.w, this.h, 5.0f, Paint.vertical(this.y, this.y + this.h, t.bg2(), t.bg()));
        Render2D.outline(ui.ctx, this.x, this.y + dy, this.w, this.h, 5.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.6f));
        ui.ctx.method_44379((int)Math.floor(this.x), (int)Math.floor(this.y + 3.0f), (int)Math.ceil(this.x + this.w), (int)Math.ceil(this.y + this.h - 3.0f));
        String cur = this.current.get();
        for (int i = 0; i < this.options.size(); ++i) {
            float iy = this.y + 3.0f + (float)i * 14.0f - this.scroll + dy;
            if (iy + 14.0f < this.y || iy > this.y + this.h) continue;
            String opt = this.options.get(i);
            boolean hov = ui.mx >= this.x && ui.mx < this.x + this.w && ui.my >= iy && ui.my < iy + 14.0f && ui.my >= this.y + 3.0f && ui.my < this.y + this.h - 3.0f;
            float ha = this.hovers.get(i).update(hov ? 1.0f : 0.0f, ui.dt);
            boolean sel = opt.equals(cur);
            if (sel) {
                Render2D.roundRect(ui.ctx, this.x + 3.0f, iy + 1.0f, this.w - 6.0f, 12.0f, 3.5f, Paint.horizontal(this.x, this.x + this.w, ColorUtil.withAlpha(t.accent(), 0.45f), ColorUtil.withAlpha(t.accent2(), 0.15f)));
            } else if (ha > 0.01f) {
                Render2D.roundRect(ui.ctx, this.x + 3.0f, iy + 1.0f, this.w - 6.0f, 12.0f, 3.5f, ColorUtil.withAlpha(t.accent(), 0.18f * ha));
            }
            Fonts.draw(ui.ctx, Fonts.trim(opt, this.w - 22.0f, 7.0f, false), this.x + 8.0f, iy + 3.5f + 0.4f, 7.0f, sel ? t.text() : ColorUtil.lerp(t.textDim(), t.text(), ha), sel);
            if (!sel) continue;
            Icons.draw(ui.ctx, Icon.CHECK, this.x + this.w - 14.0f, iy + 3.5f, 7.0f, t.text());
        }
        ui.ctx.method_44380();
        if (this.maxScroll() > 0.0f) {
            float viewH = this.h - 6.0f;
            float barH = Math.max(12.0f, viewH * viewH / ((float)this.options.size() * 14.0f));
            float barY = this.y + 3.0f + (viewH - barH) * (this.scroll / this.maxScroll());
            Render2D.roundRect(ui.ctx, this.x + this.w - 3.5f, barY, 2.0f, barH, 1.0f, ColorUtil.withAlpha(t.accent(), 0.6f));
        }
        Render2D.alpha = prevAlpha;
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        if (!this.contains(mx, my)) {
            return false;
        }
        int i = (int)((my - (double)this.y - 3.0 + (double)this.scroll) / 14.0);
        if (i >= 0 && i < this.options.size() && my >= (double)(this.y + 3.0f) && my < (double)(this.y + this.h - 3.0f)) {
            this.onSelect.accept(this.options.get(i));
            this.onClose.run();
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        if (!this.contains(mx, my)) {
            return false;
        }
        this.scroll = (float)Math.max(0.0, Math.min((double)this.maxScroll(), (double)this.scroll - amount * 14.0 * 1.5));
        return true;
    }
}

