/*
 * Decompiled with CFR 0.152.
 */
package astra.gui;

import astra.gui.components.Popup;
import astra.gui.components.Widget;

public interface PopupHost {
    public void openPopup(Popup var1);

    public void closePopup();

    public Popup currentPopup();

    public void toast(String var1);

    public void setFocus(Widget var1);

    public Widget getFocus();
}

