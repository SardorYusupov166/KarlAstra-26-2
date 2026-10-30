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
import java.util.Locale;

public class ColorSetting
extends Setting<Integer> {
    private boolean allowAlpha;

    public ColorSetting(String name, int argb) {
        super(name, argb);
    }

    public ColorSetting withAlpha() {
        this.allowAlpha = true;
        return this;
    }

    public boolean allowsAlpha() {
        return this.allowAlpha;
    }

    public int getColor() {
        return (Integer)this.get();
    }

    public String hex() {
        int c = (Integer)this.get();
        return this.allowAlpha ? String.format(Locale.ROOT, "#%08X", c) : String.format(Locale.ROOT, "#%06X", c & 0xFFFFFF);
    }

    @Override
    protected Integer sanitize(Integer v) {
        if (v == null) {
            return (Integer)this.defaultValue;
        }
        return this.allowAlpha ? v : v | 0xFF000000;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(String.format(Locale.ROOT, "#%08X", this.value));
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }
        try {
            String s = element.getAsString().replace("#", "");
            long parsed = Long.parseLong(s, 16);
            if (s.length() <= 6) {
                parsed |= 0xFF000000L;
            }
            this.setSilently((int)parsed);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }
}

