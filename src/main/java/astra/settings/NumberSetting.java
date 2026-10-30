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
import java.util.function.DoubleFunction;

public class NumberSetting
extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;
    private String unit = "";
    private DoubleFunction<String> formatter;

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public NumberSetting unit(String unit) {
        this.unit = unit;
        return this;
    }

    public NumberSetting format(DoubleFunction<String> formatter) {
        this.formatter = formatter;
        return this;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double getStep() {
        return this.step;
    }

    public float getFloat() {
        return ((Double)this.get()).floatValue();
    }

    public int getInt() {
        return (int)Math.round((Double)this.get());
    }

    public double getPercent() {
        return this.max == this.min ? 0.0 : ((Double)this.get() - this.min) / (this.max - this.min);
    }

    public void setPercent(double percent) {
        this.set(this.min + Math.max(0.0, Math.min(1.0, percent)) * (this.max - this.min));
    }

    @Override
    protected Double sanitize(Double v) {
        if (v == null || v.isNaN()) {
            return (Double)this.defaultValue;
        }
        double clamped = Math.max(this.min, Math.min(this.max, v));
        if (this.step > 0.0) {
            clamped = this.min + (double)Math.round((clamped - this.min) / this.step) * this.step;
            clamped = Math.max(this.min, Math.min(this.max, clamped));
            clamped = (double)Math.round(clamped * 1000000.0) / 1000000.0;
        }
        return clamped;
    }

    public String display() {
        double v = (Double)this.get();
        if (this.formatter != null) {
            return this.formatter.apply(v);
        }
        String number = this.step >= 1.0 ? String.valueOf(Math.round(v)) : (this.step >= 0.1 ? String.format(Locale.ROOT, "%.1f", v) : String.format(Locale.ROOT, "%.2f", v));
        return number + this.unit;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive((Number)this.value);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            this.setSilently(element.getAsDouble());
        }
    }
}

