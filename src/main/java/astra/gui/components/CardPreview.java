/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.gui.UiContext;

public interface CardPreview {
    public float height(float var1);

    public void render(UiContext var1, float var2, float var3, float var4, float var5);

    default public boolean mouseClicked(UiContext ui, double mx, double my, int button, float x, float y, float w, float h) {
        return false;
    }

    default public boolean mouseDragged(UiContext ui, double mx, double my, int button, float x, float y, float w, float h) {
        return false;
    }

    default public void mouseReleased() {
    }
}

