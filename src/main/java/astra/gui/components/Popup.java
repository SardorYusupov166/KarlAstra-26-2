/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.animation.Animation;
import astra.gui.UiContext;

public abstract class Popup {
    public float x;
    public float y;
    public float w;
    public float h;
    protected final Animation open = new Animation(0.0f, 18.0f);

    public abstract void render(UiContext var1);

    public abstract boolean mouseClicked(UiContext var1, double var2, double var4, int var6);

    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        return false;
    }

    public void mouseReleased(UiContext ui, double mx, double my, int button) {
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        return false;
    }

    public boolean contains(double mx, double my) {
        return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + this.h);
    }

    public void fit(float areaW, float areaH) {
        if (this.x + this.w > areaW - 4.0f) {
            this.x = areaW - 4.0f - this.w;
        }
        if (this.y + this.h > areaH - 4.0f) {
            this.y = areaH - 4.0f - this.h;
        }
        if (this.x < 4.0f) {
            this.x = 4.0f;
        }
        if (this.y < 4.0f) {
            this.y = 4.0f;
        }
    }
}

