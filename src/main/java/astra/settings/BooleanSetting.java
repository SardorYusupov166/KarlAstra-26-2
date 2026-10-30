/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 */
package astra.settings;

import astra.settings.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting
extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public boolean isOn() {
        return (Boolean)this.get();
    }

    public void toggle() {
        this.set((Boolean)this.get() == false);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive((Boolean)this.value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            this.setSilently(element.getAsBoolean());
        }
    }
}

