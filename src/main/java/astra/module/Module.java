/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package astra.module;

import astra.AstraClient;
import astra.config.ConfigManager;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.modules.cosmetic.SoundsModule;
import astra.render.Icon;
import astra.settings.KeybindSetting;
import astra.settings.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getInstance();
    private final String id;
    private final String name;
    private final String description;
    private final Category category;
    private final Icon icon;
    private final List<Setting<?>> settings = new ArrayList();
    private boolean enabled;
    private boolean defaultEnabled;
    private boolean toggleable = true;
    private boolean hidden;
    private KeybindSetting keybind;

    protected Module(String id, String name, String description, Category category, Icon icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.icon = icon;
    }

    protected <S extends Setting<?>> S add(S setting) {
        this.settings.add(setting);
        return setting;
    }

    protected KeybindSetting withToggleKey(int defaultKey) {
        this.keybind = this.add(new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430", defaultKey));
        return this.keybind;
    }

    protected void setDefaultEnabled(boolean value) {
        this.defaultEnabled = value;
        this.enabled = value;
    }

    protected void alwaysOn() {
        this.toggleable = false;
        this.setDefaultEnabled(true);
    }

    protected void hide() {
        this.hidden = true;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public final void toggle() {
        this.setEnabled(!this.enabled);
        if (!this.toggleable) {
            return;
        }
        SoundsModule sounds = SoundsModule.get();
        if (sounds != null) {
            sounds.onModuleToggled(this.enabled);
        }
    }

    public final void setEnabled(boolean value) {
        if (!this.toggleable) {
            value = true;
        }
        if (value == this.enabled) {
            return;
        }
        this.enabled = value;
        try {
            if (this.enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
        catch (RuntimeException e) {
            AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u043c\u043e\u0434\u0443\u043b\u044f {}", (Object)this.id, (Object)e);
        }
        ConfigManager.markDirty();
    }

    public final void setEnabledQuiet(boolean value) {
        this.enabled = !this.toggleable || value;
    }

    public final void applyLoadedState(boolean value) {
        this.setEnabled(value);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void onTick() {
    }

    public CardPreview getPreview() {
        return null;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isDefaultEnabled() {
        return this.defaultEnabled;
    }

    public boolean isToggleable() {
        return this.toggleable;
    }

    public KeybindSetting getKeybind() {
        return this.keybind;
    }

    public boolean usesToggleKey() {
        return true;
    }

    public boolean hasToggleKey() {
        return this.keybind != null && this.keybind.getKey() != -1;
    }

    public List<Setting<?>> getSettings() {
        return Collections.unmodifiableList(this.settings);
    }

    protected static boolean inGame() {
        return Module.mc.player != null && Module.mc.level != null;
    }
}

