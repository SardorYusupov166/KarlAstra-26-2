/*
 * Decompiled with CFR 0.152.
 */
package astra.module;

import astra.animation.AnimationUtils;
import astra.module.Category;
import astra.module.Module;
import astra.render.ArtManager;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.theme.ThemeManager;

public class ClientSettings
extends Module {
    private static ClientSettings instance;
    public final ModeSetting theme = this.add((ModeSetting)new ModeSetting("\u0422\u0435\u043c\u0430", "Astra Purple", ThemeManager.names()).onChange(ThemeManager::setTheme));
    public final BooleanSetting customAccent = this.add((BooleanSetting)new BooleanSetting("\u0421\u0432\u043e\u0439 \u0430\u043a\u0446\u0435\u043d\u0442", false).onChange(v -> this.applyAccent()));
    public final ColorSetting accent = this.add((ColorSetting)(new ColorSetting("\u0426\u0432\u0435\u0442 \u0430\u043a\u0446\u0435\u043d\u0442\u0430", -5745153).visibleWhen(() -> (Boolean)this.customAccent.get())).onChange(v -> this.applyAccent()));
    public final BooleanSetting glow = this.add((BooleanSetting)new BooleanSetting("UI Glow", true).desc("\u0421\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043f\u0430\u043d\u0435\u043b\u0435\u0439 \u0438 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u043e\u0432"));
    public final NumberSetting glowIntensity = this.add((NumberSetting)new NumberSetting("\u0421\u0438\u043b\u0430 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u044f", 100.0, 0.0, 200.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.glow.get()));
    public final BooleanSetting blur = this.add((BooleanSetting)new BooleanSetting("UI Blur", true).desc("\u0420\u0430\u0437\u043c\u044b\u0442\u0438\u0435 \u043c\u0438\u0440\u0430 \u043f\u043e\u0434 \u043c\u0435\u043d\u044e"));
    public final NumberSetting dim = this.add(new NumberSetting("\u0417\u0430\u0442\u0435\u043c\u043d\u0435\u043d\u0438\u0435 \u043c\u0438\u0440\u0430", 25.0, 0.0, 90.0, 5.0).unit("%"));
    public final BooleanSetting animations = this.add((BooleanSetting)new BooleanSetting("\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430", true).onChange(v -> this.applyAnimations()));
    public final NumberSetting animSpeed = this.add((NumberSetting)(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0439", 100.0, 50.0, 200.0, 10.0).unit("%").visibleWhen(() -> (Boolean)this.animations.get())).onChange(v -> this.applyAnimations()));
    public final BooleanSetting artBackground = this.add((BooleanSetting)new BooleanSetting("\u0410\u0440\u0442-\u0444\u043e\u043d \u043e\u043a\u043d\u0430", true).desc("\u0410\u043d\u0438\u043c\u0435-\u0438\u043b\u043b\u044e\u0441\u0442\u0440\u0430\u0446\u0438\u044f \u0437\u0430 \u043f\u0430\u043d\u0435\u043b\u044f\u043c\u0438 \u043c\u0435\u043d\u044e"));
    public final BooleanSetting artBackgroundThemed = this.add((BooleanSetting)new BooleanSetting("\u0410\u0440\u0442-\u0444\u043e\u043d \u0432 \u0446\u0432\u0435\u0442\u0430\u0445 \u0442\u0435\u043c\u044b", true).desc("\u0424\u043e\u043d \u043e\u043a\u043d\u0430 \u043f\u0435\u0440\u0435\u043a\u0440\u0430\u0448\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u043f\u043e\u0434 \u0442\u0435\u043c\u0443; \u0432\u044b\u043a\u043b\u044e\u0447\u0438\u0442\u0435 \u2014 \u043e\u0441\u0442\u0430\u043d\u0435\u0442\u0441\u044f \u0438\u0441\u0445\u043e\u0434\u043d\u0430\u044f \u0440\u043e\u0437\u043e\u0432\u0430\u044f \u0441\u0430\u043a\u0443\u0440\u0430"));
    public final NumberSetting artBackgroundAlpha = this.add((NumberSetting)new NumberSetting("\u042f\u0440\u043a\u043e\u0441\u0442\u044c \u0430\u0440\u0442-\u0444\u043e\u043d\u0430", 35.0, 5.0, 100.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.artBackground.get()));
    public final BooleanSetting petals = this.add((BooleanSetting)new BooleanSetting("\u041b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0432 \u043c\u0435\u043d\u044e", true).desc("\u0414\u0435\u043a\u043e\u0440\u0430\u0442\u0438\u0432\u043d\u044b\u0435 \u043b\u0435\u043f\u0435\u0441\u0442\u043a\u0438 \u0441\u0430\u043a\u0443\u0440\u044b"));
    public final ModeSetting sidebarArt = this.add((ModeSetting)new ModeSetting("\u0410\u0440\u0442 \u0432 \u043c\u0435\u043d\u044e", "\u0410\u0432\u0442\u043e", "\u0410\u0432\u0442\u043e", "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439").dynamic(ArtManager::artChoices).desc("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430 \u0432 \u0431\u043e\u043a\u043e\u0432\u043e\u0439 \u043f\u0430\u043d\u0435\u043b\u0438 \u043c\u0435\u043d\u044e: \u0438\u0437 \u043f\u0430\u043f\u043a\u0438 config/astra/art (PNG, JPG, BMP, GIF) \u0438\u043b\u0438 \u0432\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u0430\u044f"));
    public final BooleanSetting gameMenuStyle = this.add((BooleanSetting)new BooleanSetting("\u0421\u0442\u0438\u043b\u044c Astra \u0432 \u043c\u0435\u043d\u044e \u0438\u0433\u0440\u044b", true).desc("\u041a\u043d\u043e\u043f\u043a\u0438, \u043f\u043e\u043b\u0437\u0443\u043d\u043a\u0438, \u0444\u043e\u043d \u0438 \u043b\u043e\u0433\u043e\u0442\u0438\u043f \u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u043c \u043c\u0435\u043d\u044e, \u043f\u0430\u0443\u0437\u0435 \u0438 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430\u0445 Minecraft"));
    public final BooleanSetting customModsScreen = this.add((BooleanSetting)new BooleanSetting("\u0421\u0432\u043e\u0439 \u044d\u043a\u0440\u0430\u043d \u043c\u043e\u0434\u043e\u0432", true).desc("\u041a\u043d\u043e\u043f\u043a\u0430 \u00ab\u041c\u043e\u0434\u044b\u00bb \u043e\u0442\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u044d\u043a\u0440\u0430\u043d \u043c\u043e\u0434\u043e\u0432 Astra \u0432\u043c\u0435\u0441\u0442\u043e Mod Menu"));
    public final NumberSetting uiScale = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u043c\u0435\u043d\u044e", 100.0, 70.0, 130.0, 5.0).unit("%"));

    public ClientSettings() {
        super("client_settings", "\u0418\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441", "\u0422\u0435\u043c\u0430, \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435, \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u0435 \u0438 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u043c\u0435\u043d\u044e.", Category.THEMES, Icon.PALETTE);
        this.alwaysOn();
        instance = this;
    }

    private void applyAccent() {
        ThemeManager.setAccentOverride((Boolean)this.customAccent.get() != false ? Integer.valueOf(this.accent.getColor()) : null);
    }

    private void applyAnimations() {
        AnimationUtils.configure((Boolean)this.animations.get(), this.animSpeed.getFloat() / 100.0f);
    }

    public static ClientSettings get() {
        return instance;
    }

    public static float glowFactor() {
        if (instance == null) {
            return 1.0f;
        }
        if (!((Boolean)ClientSettings.instance.glow.get()).booleanValue()) {
            return 0.0f;
        }
        return ClientSettings.instance.glowIntensity.getFloat() / 100.0f;
    }
}

