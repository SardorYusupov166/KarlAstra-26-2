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

public class StringSetting
extends Setting<String> {
    private final String placeholder;
    private final int maxLength;

    public StringSetting(String name, String defaultValue, String placeholder, int maxLength) {
        super(name, defaultValue);
        this.placeholder = placeholder;
        this.maxLength = maxLength;
    }

    public String placeholder() {
        return this.placeholder;
    }

    public int maxLength() {
        return this.maxLength;
    }

    @Override
    protected String sanitize(String v) {
        if (v == null) {
            return (String)this.defaultValue;
        }
        return (v = v.replaceAll("[\\r\\n\\t]", "").trim()).length() > this.maxLength ? v.substring(0, this.maxLength) : v;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive((String)this.get());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            this.setSilently(element.getAsString());
        }
    }
}

