/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.gui.UiContext;

public abstract class Widget {
    public float x;
    public float y;
    public float w;
    public float h;

    public float layout(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = this.measure(w);
        return this.h;
    }

    protected abstract float measure(float var1);

    public abstract void render(UiContext var1);

    public boolean isVisible() {
        return true;
    }

    public boolean contains(double mx, double my) {
        return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + this.h);
    }

    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        return false;
    }

    public void mouseReleased(UiContext ui, double mx, double my, int button) {
    }

    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        return false;
    }

    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}

