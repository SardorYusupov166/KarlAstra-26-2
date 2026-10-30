/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.gui;

import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class UiContext {
    public GuiGraphicsExtractor ctx;
    public float mx;
    public float my;
    public float dt;
    public Theme theme;
    private float clipX0 = -1.0E9f;
    private float clipY0 = -1.0E9f;
    private float clipX1 = 1.0E9f;
    private float clipY1 = 1.0E9f;
    public boolean blocked;

    public boolean hover(float x, float y, float w, float h) {
        if (this.blocked) {
            return false;
        }
        return this.mx >= x && this.mx < x + w && this.my >= y && this.my < y + h && this.mx >= this.clipX0 && this.mx < this.clipX1 && this.my >= this.clipY0 && this.my < this.clipY1;
    }

    public void setClip(float x, float y, float w, float h) {
        this.clipX0 = x;
        this.clipY0 = y;
        this.clipX1 = x + w;
        this.clipY1 = y + h;
    }

    public void clearClip() {
        this.clipY0 = -1.0E9f;
        this.clipX0 = -1.0E9f;
        this.clipY1 = 1.0E9f;
        this.clipX1 = 1.0E9f;
    }

    public boolean inClip(double x, double y) {
        return x >= (double)this.clipX0 && x < (double)this.clipX1 && y >= (double)this.clipY0 && y < (double)this.clipY1;
    }
}

