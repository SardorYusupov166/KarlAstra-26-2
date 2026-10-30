/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 */
package astra.settings;

import astra.settings.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import java.util.function.Supplier;

public class ButtonSetting
extends Setting<String> {
    private final Runnable action;
    private Supplier<String> label;

    public ButtonSetting(String name, String buttonText, Runnable action) {
        super(name, buttonText);
        this.action = action;
    }

    public ButtonSetting label(Supplier<String> label) {
        this.label = label;
        return this;
    }

    public String buttonText() {
        return this.label != null ? this.label.get() : (String)this.get();
    }

    public void press() {
        this.action.run();
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public JsonElement toJson() {
        return JsonNull.INSTANCE;
    }

    @Override
    public void fromJson(JsonElement element) {
    }
}

