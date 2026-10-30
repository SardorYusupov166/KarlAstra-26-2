/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.CloudStatus
 *  net.minecraft.client.GraphicsPreset
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.server.level.ParticleStatus
 */
package astra.module.modules.performance;

import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.ClientSettings;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.hud.FpsGraphHud;
import astra.module.modules.hud.MemoryHud;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.utils.FrameStats;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.client.OptionInstance;
import net.minecraft.server.level.ParticleStatus;

public class PerformanceModule
extends Module {
    private static final String[] GRAPHICS = new String[]{"\u0411\u044b\u0441\u0442\u0440\u0430\u044f", "\u0414\u0435\u0442\u0430\u043b\u044c\u043d\u0430\u044f", "\u041f\u043e\u0442\u0440\u044f\u0441\u0430\u044e\u0449\u0430\u044f"};
    private static final String[] PARTICLES = new String[]{"\u0412\u0441\u0435", "\u041c\u0435\u043d\u044c\u0448\u0435", "\u041c\u0438\u043d\u0438\u043c\u0443\u043c"};
    private static final String[] CLOUDS = new String[]{"\u0412\u044b\u043a\u043b.", "\u0411\u044b\u0441\u0442\u0440\u044b\u0435", "\u0414\u0435\u0442\u0430\u043b\u044c\u043d\u044b\u0435"};
    private final ModeSetting preset = this.add((ModeSetting)((Setting)new ModeSetting("\u0420\u0435\u0436\u0438\u043c \u043f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u0438", "\u0421\u0432\u043e\u0451", "\u0421\u0432\u043e\u0451", "\u041a\u0430\u0447\u0435\u0441\u0442\u0432\u043e", "\u0421\u0431\u0430\u043b\u0430\u043d\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439", "\u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c").desc("\u0412\u044b\u0441\u0442\u0430\u0432\u043b\u044f\u0435\u0442 \u043d\u0430\u0431\u043e\u0440 \u0448\u0442\u0430\u0442\u043d\u044b\u0445 \u043e\u043f\u0446\u0438\u0439 \u0433\u0440\u0430\u0444\u0438\u043a\u0438")).onChange(this::applyPreset));
    private final NumberSetting renderDistance = this.add((NumberSetting)new NumberSetting("\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u043f\u0440\u043e\u0440\u0438\u0441\u043e\u0432\u043a\u0438", 12.0, 2.0, 32.0, 1.0).unit(" \u0447\u0430\u043d\u043a\u043e\u0432").bind(() -> (double)((Integer)PerformanceModule.mc.field_1690.method_42503().method_41753()), v -> this.setOpt(PerformanceModule.mc.field_1690.method_42503(), (int)Math.round(v))));
    private final NumberSetting simulation = this.add((NumberSetting)new NumberSetting("\u0414\u0430\u043b\u044c\u043d\u043e\u0441\u0442\u044c \u0441\u0438\u043c\u0443\u043b\u044f\u0446\u0438\u0438", 12.0, 5.0, 32.0, 1.0).unit(" \u0447\u0430\u043d\u043a\u043e\u0432").bind(() -> (double)((Integer)PerformanceModule.mc.field_1690.method_42510().method_41753()), v -> this.setOpt(PerformanceModule.mc.field_1690.method_42510(), (int)Math.round(v))));
    private final NumberSetting maxFps = this.add((NumberSetting)new NumberSetting("\u041c\u0430\u043a\u0441. FPS", 120.0, 10.0, 260.0, 10.0).format(v -> v >= 260.0 ? "\u0411\u0435\u0437 \u043e\u0433\u0440." : String.valueOf((int)Math.round(v))).bind(() -> (double)((Integer)PerformanceModule.mc.field_1690.method_42524().method_41753()), v -> this.setOpt(PerformanceModule.mc.field_1690.method_42524(), (int)Math.round(v))));
    private final BooleanSetting vsync = this.add((BooleanSetting)new BooleanSetting("VSync", false).bind(() -> (Boolean)PerformanceModule.mc.field_1690.method_42433().method_41753(), v -> this.setOpt(PerformanceModule.mc.field_1690.method_42433(), v)));
    private final ModeSetting graphics = this.add((ModeSetting)new ModeSetting("\u0413\u0440\u0430\u0444\u0438\u043a\u0430", "\u0414\u0435\u0442\u0430\u043b\u044c\u043d\u0430\u044f", GRAPHICS).bind(() -> GRAPHICS[Math.min(GRAPHICS.length - 1, ((GraphicsPreset)PerformanceModule.mc.field_1690.method_75329().method_41753()).ordinal())], v -> PerformanceModule.applyPreset(GraphicsPreset.values()[PerformanceModule.indexOf(GRAPHICS, v)])));
    private final ModeSetting particles = this.add((ModeSetting)new ModeSetting("\u0427\u0430\u0441\u0442\u0438\u0446\u044b Minecraft", "\u0412\u0441\u0435", PARTICLES).bind(() -> PARTICLES[((ParticleStatus)PerformanceModule.mc.field_1690.method_42475().method_41753()).ordinal()], v -> this.setOpt(PerformanceModule.mc.field_1690.method_42475(), ParticleStatus.values()[PerformanceModule.indexOf(PARTICLES, v)])));
    private final ModeSetting clouds = this.add((ModeSetting)new ModeSetting("\u041e\u0431\u043b\u0430\u043a\u0430", "\u0414\u0435\u0442\u0430\u043b\u044c\u043d\u044b\u0435", CLOUDS).bind(() -> CLOUDS[((CloudStatus)PerformanceModule.mc.field_1690.method_42528().method_41753()).ordinal()], v -> this.setOpt(PerformanceModule.mc.field_1690.method_42528(), CloudStatus.values()[PerformanceModule.indexOf(CLOUDS, v)])));
    private final BooleanSetting shadows = this.add((BooleanSetting)new BooleanSetting("\u0422\u0435\u043d\u0438 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", true).bind(() -> (Boolean)PerformanceModule.mc.field_1690.method_42435().method_41753(), v -> this.setOpt(PerformanceModule.mc.field_1690.method_42435(), v)));
    private final BooleanSetting smoothLight = this.add((BooleanSetting)new BooleanSetting("\u041c\u044f\u0433\u043a\u043e\u0435 \u043e\u0441\u0432\u0435\u0449\u0435\u043d\u0438\u0435", true).bind(() -> (Boolean)PerformanceModule.mc.field_1690.method_41792().method_41753(), v -> this.setOpt(PerformanceModule.mc.field_1690.method_41792(), v)));
    private final NumberSetting biomeBlend = this.add((NumberSetting)new NumberSetting("\u0421\u0433\u043b\u0430\u0436\u0438\u0432\u0430\u043d\u0438\u0435 \u0431\u0438\u043e\u043c\u043e\u0432", 2.0, 0.0, 7.0, 1.0).bind(() -> (double)((Integer)PerformanceModule.mc.field_1690.method_41805().method_41753()), v -> this.setOpt(PerformanceModule.mc.field_1690.method_41805(), (int)Math.round(v))));
    private final NumberSetting decorLimit = this.add((NumberSetting)new NumberSetting("\u041b\u0438\u043c\u0438\u0442 \u0447\u0430\u0441\u0442\u0438\u0446 Astra", 100.0, 0.0, 100.0, 5.0).unit("%").desc("\u0414\u0435\u043a\u043e\u0440\u0430\u0442\u0438\u0432\u043d\u044b\u0435 \u0447\u0430\u0441\u0442\u0438\u0446\u044b \u043a\u043b\u0438\u0435\u043d\u0442\u0430 (\u0430\u0442\u043c\u043e\u0441\u0444\u0435\u0440\u0430, \u0443\u0434\u0430\u0440\u044b, \u043a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0430)"));
    private final BooleanSetting lightUi = this.add((BooleanSetting)((Setting)new BooleanSetting("\u041b\u0451\u0433\u043a\u0438\u0439 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441", false).desc("\u041e\u0442\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u0435 \u0438 \u0441\u0432\u0435\u0447\u0435\u043d\u0438\u0435 \u043c\u0435\u043d\u044e")).onChange(v -> {
        ClientSettings cs = ClientSettings.get();
        if (cs != null) {
            cs.blur.set(v == false);
            cs.glow.set(v == false);
        }
    }));

    public PerformanceModule() {
        super("performance", "\u041e\u0441\u043d\u043e\u0432\u043d\u044b\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", "\u0413\u0440\u0430\u0444\u0438\u0447\u0435\u0441\u043a\u0438\u0435 \u043e\u043f\u0446\u0438\u0438 Minecraft \u0438 \u043d\u0430\u0433\u0440\u0443\u0437\u043a\u0430 \u043a\u043b\u0438\u0435\u043d\u0442\u0430.", Category.PERFORMANCE, Icon.GEAR);
        this.alwaysOn();
    }

    private static int indexOf(String[] arr, String v) {
        for (int i = 0; i < arr.length; ++i) {
            if (!arr[i].equals(v)) continue;
            return i;
        }
        return 0;
    }

    private <T> void setOpt(OptionInstance<T> option, T value) {
        option.method_41748(value);
        PerformanceModule.mc.field_1690.method_1640();
    }

    private void applyPreset(String name) {
        switch (name) {
            case "\u041a\u0430\u0447\u0435\u0441\u0442\u0432\u043e": {
                PerformanceModule.applyPreset(GraphicsPreset.field_25428);
                this.setOpt(PerformanceModule.mc.field_1690.method_42475(), ParticleStatus.field_18197);
                this.setOpt(PerformanceModule.mc.field_1690.method_42528(), CloudStatus.field_18164);
                this.setOpt(PerformanceModule.mc.field_1690.method_42435(), true);
                this.setOpt(PerformanceModule.mc.field_1690.method_41792(), true);
                this.setOpt(PerformanceModule.mc.field_1690.method_41805(), 3);
                this.decorLimit.set(100.0);
                break;
            }
            case "\u0421\u0431\u0430\u043b\u0430\u043d\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439": {
                PerformanceModule.applyPreset(GraphicsPreset.field_25428);
                this.setOpt(PerformanceModule.mc.field_1690.method_42475(), ParticleStatus.field_18198);
                this.setOpt(PerformanceModule.mc.field_1690.method_42528(), CloudStatus.field_18163);
                this.setOpt(PerformanceModule.mc.field_1690.method_42435(), true);
                this.setOpt(PerformanceModule.mc.field_1690.method_41792(), true);
                this.setOpt(PerformanceModule.mc.field_1690.method_41805(), 2);
                if ((Integer)PerformanceModule.mc.field_1690.method_42503().method_41753() > 12) {
                    this.setOpt(PerformanceModule.mc.field_1690.method_42503(), 12);
                }
                this.decorLimit.set(60.0);
                break;
            }
            case "\u041f\u0440\u043e\u0438\u0437\u0432\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0441\u0442\u044c": {
                PerformanceModule.applyPreset(GraphicsPreset.field_25427);
                this.setOpt(PerformanceModule.mc.field_1690.method_42475(), ParticleStatus.field_18199);
                this.setOpt(PerformanceModule.mc.field_1690.method_42528(), CloudStatus.field_18162);
                this.setOpt(PerformanceModule.mc.field_1690.method_42435(), false);
                this.setOpt(PerformanceModule.mc.field_1690.method_41792(), false);
                this.setOpt(PerformanceModule.mc.field_1690.method_41805(), 0);
                if ((Integer)PerformanceModule.mc.field_1690.method_42503().method_41753() > 8) {
                    this.setOpt(PerformanceModule.mc.field_1690.method_42503(), 8);
                }
                this.decorLimit.set(25.0);
                break;
            }
        }
    }

    public static int limitParticles(int count) {
        PerformanceModule m = ModuleManager.get(PerformanceModule.class);
        if (m == null) {
            return count;
        }
        return Math.round((float)count * m.decorLimit.getFloat() / 100.0f);
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(this){

            @Override
            public float height(float width) {
                return 80.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, ColorUtil.withAlpha(ui.theme.bg(), 0.5f));
                Fonts.draw(ui.ctx, "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u0430\u043c\u044f\u0442\u0438", x + 7.0f, y + 6.0f, 6.8f, ui.theme.text(), true);
                MemoryHud.drawBar(ui.ctx, x + 7.0f, y + 17.0f, w - 14.0f, ui.theme.accent(), ui.theme, 6.5f);
                float gy = y + 40.0f;
                FpsGraphHud.drawGraph(ui.ctx, x + 7.0f, gy, w * 0.6f, h - 46.0f, ui.theme.accent(), ui.theme);
                float sx = x + 14.0f + w * 0.6f;
                Fonts.draw(ui.ctx, "FPS", sx, gy, 6.0f, ui.theme.textDim(), true);
                Fonts.draw(ui.ctx, String.valueOf(FrameStats.fps()), sx, gy + 7.0f, 11.0f, ui.theme.accent3(), true);
                Fonts.draw(ui.ctx, "\u041c\u0438\u043d " + Math.round(FrameStats.min()) + " \u00b7 \u041c\u0430\u043a\u0441 " + Math.round(FrameStats.max()), sx, gy + 22.0f, 5.5f, ui.theme.textMuted(), false);
            }
        };
    }

    private static void applyPreset(GraphicsPreset mode) {
        if (PerformanceModule.mc.field_1690.method_75329().method_41753() == mode) {
            return;
        }
        PerformanceModule.mc.field_1690.method_75329().method_41748((Object)mode);
        mode.method_75306(mc);
        PerformanceModule.mc.field_1690.method_1640();
    }
}

