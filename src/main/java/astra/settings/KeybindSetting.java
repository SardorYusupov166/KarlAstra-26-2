/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  com.mojang.blaze3d.platform.InputConstants$Type
 */
package astra.settings;

import astra.settings.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.InputConstants;

public class KeybindSetting
extends Setting<Integer> {
    public KeybindSetting(String name, int key) {
        super(name, key);
    }

    public int getKey() {
        return (Integer)this.get();
    }

    public boolean isBound() {
        return (Integer)this.get() != -1;
    }

    public String keyName() {
        int key = (Integer)this.get();
        if (key == -1) {
            return "\u2014";
        }
        String name = InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString();
        if (key == 32) {
            return "\u041f\u0440\u043e\u0431\u0435\u043b";
        }
        if (name.length() == 1) {
            return name.toUpperCase();
        }
        return name;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive((Number)this.value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            this.setSilently(element.getAsInt());
        }
    }
}

