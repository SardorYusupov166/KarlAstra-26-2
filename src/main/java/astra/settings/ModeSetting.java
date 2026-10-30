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
import java.util.List;
import java.util.function.Supplier;

public class ModeSetting
extends Setting<String> {
    private List<String> modes;
    private Supplier<List<String>> dynamicModes;

    public ModeSetting(String name, String defaultValue, String ... modes) {
        super(name, defaultValue);
        this.modes = List.of(modes);
    }

    public ModeSetting dynamic(Supplier<List<String>> supplier) {
        this.dynamicModes = supplier;
        return this;
    }

    public List<String> getModes() {
        List<String> list;
        if (this.dynamicModes != null && (list = this.dynamicModes.get()) != null && !list.isEmpty()) {
            return list;
        }
        return this.modes;
    }

    public boolean is(String mode) {
        return ((String)this.get()).equals(mode);
    }

    public int index() {
        return Math.max(0, this.getModes().indexOf(this.get()));
    }

    public void cycle(int dir) {
        List<String> list = this.getModes();
        if (list.isEmpty()) {
            return;
        }
        int i = Math.floorMod(this.index() + dir, list.size());
        this.set(list.get(i));
    }

    @Override
    protected String sanitize(String v) {
        if (v == null) {
            return (String)this.defaultValue;
        }
        if (this.dynamicModes != null) {
            return v;
        }
        return this.modes.contains(v) ? v : (String)this.defaultValue;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive((String)this.value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            this.setSilently(element.getAsString());
        }
    }
}

