/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 */
package astra.settings;

import astra.config.ConfigManager;
import com.google.gson.JsonElement;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Setting<T> {
    private final String name;
    private String description = "";
    private String label;
    protected final T defaultValue;
    protected T value;
    private BooleanSupplier visibility = () -> true;
    private Consumer<T> onChange;
    private Supplier<T> boundGetter;
    private Consumer<T> boundSetter;
    private boolean editorOnly;

    protected Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel() {
        return this.label != null ? this.label : this.name;
    }

    public <S extends Setting<T>> S displayAs(String label) {
        this.label = label;
        return (S)this;
    }

    public String getDescription() {
        return this.description;
    }

    public T get() {
        if (this.boundGetter != null) {
            try {
                return this.boundGetter.get();
            }
            catch (RuntimeException e) {
                return this.value;
            }
        }
        return this.value;
    }

    public T getDefault() {
        return this.defaultValue;
    }

    public void set(T newValue) {
        T sanitized = this.sanitize(newValue);
        T old = this.get();
        if (this.boundSetter != null) {
            this.boundSetter.accept(sanitized);
        } else {
            this.value = sanitized;
        }
        if (!sanitized.equals(old)) {
            if (this.onChange != null) {
                this.onChange.accept(sanitized);
            }
            if (this.isPersistent()) {
                ConfigManager.markDirty();
            }
        }
    }

    protected void setSilently(T newValue) {
        this.value = this.sanitize(newValue);
    }

    public void reset() {
        this.set(this.defaultValue);
    }

    protected T sanitize(T v) {
        return v == null ? this.defaultValue : v;
    }

    public boolean isVisible() {
        return this.visibility.getAsBoolean();
    }

    public boolean isPersistent() {
        return this.boundGetter == null;
    }

    public <S extends Setting<T>> S desc(String description) {
        this.description = description;
        return (S)this;
    }

    public <S extends Setting<T>> S visibleWhen(BooleanSupplier visibility) {
        this.visibility = visibility;
        return (S)this;
    }

    public <S extends Setting<T>> S editorOnly() {
        this.editorOnly = true;
        return (S)this;
    }

    public boolean isEditorOnly() {
        return this.editorOnly;
    }

    public <S extends Setting<T>> S onChange(Consumer<T> onChange) {
        this.onChange = onChange;
        return (S)this;
    }

    public <S extends Setting<T>> S bind(Supplier<T> getter, Consumer<T> setter) {
        this.boundGetter = getter;
        this.boundSetter = setter;
        return (S)this;
    }

    public void fireChange() {
        if (this.onChange != null) {
            this.onChange.accept(this.get());
        }
    }

    public abstract JsonElement toJson();

    public abstract void fromJson(JsonElement var1);
}

