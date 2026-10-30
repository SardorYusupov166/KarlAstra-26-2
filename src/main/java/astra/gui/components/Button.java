/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.render.Icon;
import java.util.function.Supplier;

public class Button
extends Widget {
    private final Supplier<String> label;
    private final Icon icon;
    private final Runnable action;
    private final boolean primary;
    private final float fixedW;
    private final Animation hover = new Animation(0.0f, 16.0f);

    public Button(String label, Icon icon, boolean primary, float width, Runnable action) {
        this(() -> label, icon, primary, width, action);
    }

    public Button(Supplier<String> label, Icon icon, boolean primary, float width, Runnable action) {
        this.label = label;
        this.icon = icon;
        this.primary = primary;
        this.fixedW = width;
        this.action = action;
    }

    @Override
    public float layout(float x, float y, float w) {
        return super.layout(x, y, this.fixedW > 0.0f ? this.fixedW : w);
    }

    @Override
    protected float measure(float width) {
        return 20.0f;
    }

    @Override
    public void render(UiContext ui) {
        float hv = this.hover.update(ui.hover(this.x, this.y, this.w, this.h) ? 1.0f : 0.0f, ui.dt);
        UiDraw.button(ui.ctx, ui.theme, this.x, this.y, this.w, this.h, this.label.get(), this.icon, hv, this.primary);
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        if (button == 0 && this.contains(mx, my)) {
            this.action.run();
            return true;
        }
        return false;
    }
}

