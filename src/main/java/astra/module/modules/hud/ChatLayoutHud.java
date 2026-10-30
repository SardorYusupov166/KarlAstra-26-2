/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.OptionInstance
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.ChatComponent
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.mixin.ChatHudAccessor;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.hud.AnimeChatModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import com.mojang.blaze3d.platform.Window;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.joml.Matrix3x2fStack;

public class ChatLayoutHud
extends HudModule {
    private final NumberSetting width = this.add(ChatLayoutHud.option("\u0428\u0438\u0440\u0438\u043d\u0430", () -> ChatLayoutHud.mc.options.chatWidth()));
    private final NumberSetting heightFocused = this.add(ChatLayoutHud.option("\u0412\u044b\u0441\u043e\u0442\u0430 (\u043e\u0442\u043a\u0440\u044b\u0442)", () -> ChatLayoutHud.mc.options.chatHeightFocused()));
    private final NumberSetting heightUnfocused = this.add(ChatLayoutHud.option("\u0412\u044b\u0441\u043e\u0442\u0430 (\u0437\u0430\u043a\u0440\u044b\u0442)", () -> ChatLayoutHud.mc.options.chatHeightUnfocused()));
    private final NumberSetting textScale = this.add(ChatLayoutHud.option("\u0420\u0430\u0437\u043c\u0435\u0440 \u0442\u0435\u043a\u0441\u0442\u0430", () -> ChatLayoutHud.mc.options.chatScale()));
    private final NumberSetting lineSpacing = this.add(ChatLayoutHud.option("\u041c\u0435\u0436\u0441\u0442\u0440\u043e\u0447\u043d\u044b\u0439 \u0438\u043d\u0442\u0435\u0440\u0432\u0430\u043b", () -> ChatLayoutHud.mc.options.chatLineSpacing()));
    private final NumberSetting textOpacity = this.add(ChatLayoutHud.option("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0442\u0435\u043a\u0441\u0442\u0430", () -> ChatLayoutHud.mc.options.chatOpacity()));
    private final NumberSetting bgOpacity = this.add(ChatLayoutHud.option("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u0444\u043e\u043d\u0430", () -> ChatLayoutHud.mc.options.textBackgroundOpacity()));
    private final ModeSetting style = this.add(new ModeSetting("\u0421\u0442\u0438\u043b\u044c \u0447\u0430\u0442\u0430", "\u041d\u0435\u043e\u043d", "\u041d\u0435\u043e\u043d", "\u0421\u0442\u0435\u043a\u043b\u043e", "\u041f\u0443\u0437\u044b\u0440\u0438", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442", "\u041c\u0438\u043d\u0438\u043c\u0430\u043b", "\u041a\u043b\u0430\u0441\u0441\u0438\u043a\u0430"));
    private final ModeSetting bgColor = this.add(new ModeSetting("\u0426\u0432\u0435\u0442 \u0444\u043e\u043d\u0430", "\u0422\u0435\u043c\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439", "\u041a\u0430\u043a \u0432 Minecraft"));
    private final ColorSetting customBg = this.add((ColorSetting)new ColorSetting("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0444\u043e\u043d\u0430", -15069133).visibleWhen(() -> this.bgColor.is("\u0421\u0432\u043e\u0439")));
    private final BooleanSetting stripe = this.add((BooleanSetting)new BooleanSetting("\u041d\u0435\u043e\u043d\u043e\u0432\u0430\u044f \u043f\u043e\u043b\u043e\u0441\u043a\u0430", true).visibleWhen(() -> !this.bgColor.is("\u041a\u0430\u043a \u0432 Minecraft")));
    private static boolean optionsDirty;
    private int saveTicks;

    public ChatLayoutHud() {
        super("chat_layout", "\u0427\u0430\u0442", "\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435, \u0440\u0430\u0437\u043c\u0435\u0440 \u0438 \u0446\u0432\u0435\u0442\u0430 \u0447\u0430\u0442\u0430 \u2014 \u0434\u0432\u0438\u0433\u0430\u0439\u0442\u0435 \u0435\u0433\u043e \u0432 \u0440\u0435\u0434\u0430\u043a\u0442\u043e\u0440\u0435 HUD.", Category.HUD, Icon.TAG, 0.2f, 0.7f);
        this.setDefaultEnabled(true);
    }

    public static ChatLayoutHud get() {
        return ModuleManager.get(ChatLayoutHud.class);
    }

    private static NumberSetting option(String name, Supplier<OptionInstance<Double>> opt) {
        return (NumberSetting)new NumberSetting(name, 50.0, 0.0, 100.0, 1.0).unit("%").bind(() -> mc == null || ChatLayoutHud.mc.options == null ? 50.0 : (Double)((OptionInstance)opt.get()).get() * 100.0, v -> {
            if (mc == null || ChatLayoutHud.mc.options == null) {
                return;
            }
            ((OptionInstance)opt.get()).set((Object)Math.max(0.0, Math.min(1.0, v / 100.0)));
            optionsDirty = true;
            if (ChatLayoutHud.mc.gui != null) {
                ChatLayoutHud.mc.gui.getChat().rescaleChat();
            }
        });
    }

    @Override
    public void onTick() {
        if (optionsDirty && ++this.saveTicks >= 20) {
            this.saveTicks = 0;
            optionsDirty = false;
            ChatLayoutHud.mc.options.save();
        }
    }

    private static ChatComponent hud() {
        return ChatLayoutHud.mc.gui.getChat();
    }

    @Override
    public float width() {
        ChatComponent h = ChatLayoutHud.hud();
        return (float)((double)((ChatHudAccessor)h).astra$width() * ((ChatHudAccessor)h).astra$chatScale()) + 8.0f;
    }

    @Override
    public float height() {
        ChatComponent h = ChatLayoutHud.hud();
        return (float)((double)ChatComponent.getHeight((double)((Double)ChatLayoutHud.mc.options.chatHeightFocused().get())) * ((ChatHudAccessor)h).astra$chatScale());
    }

    private boolean untouched() {
        return this.isAtDefault() && this.scale.getFloat() == 100.0f;
    }

    @Override
    public float getX(float screenW) {
        return this.untouched() ? 0.0f : super.getX(screenW);
    }

    @Override
    public float getY(float screenH) {
        return this.untouched() ? screenH - 40.0f - this.scaledHeight() : super.getY(screenH);
    }

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    public static float[] transform(float sw, float sh) {
        ChatLayoutHud m = ChatLayoutHud.get();
        if (m == null || !m.isEnabled() || m.untouched()) {
            return null;
        }
        float s = m.getScale();
        float x = m.getX(sw);
        float bottom = m.getY(sh) + m.scaledHeight();
        return new float[]{x, bottom, s};
    }

    public static boolean begin(GuiGraphics ctx) {
        float sh = ctx.guiHeight();
        float[] t = ChatLayoutHud.transform(ctx.guiWidth(), sh);
        if (t == null) {
            return false;
        }
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(t[0], t[1]);
        m.scale(t[2], t[2]);
        m.translate(0.0f, -(sh - 40.0f));
        return true;
    }

    public static double unmapX(double x) {
        Window w = mc.getWindow();
        float[] t = ChatLayoutHud.transform(w.getGuiScaledWidth(), w.getGuiScaledHeight());
        return t == null ? x : (x - (double)t[0]) / (double)t[2];
    }

    public static double unmapY(double y) {
        Window w = mc.getWindow();
        float sh = w.getGuiScaledHeight();
        float[] t = ChatLayoutHud.transform(w.getGuiScaledWidth(), sh);
        return t == null ? y : (y - (double)t[1]) / (double)t[2] + (double)(sh - 40.0f);
    }

    public int background(Theme t) {
        return switch ((String)this.bgColor.get()) {
            case "\u0421\u0432\u043e\u0439" -> this.customBg.getColor();
            case "\u041a\u0430\u043a \u0432 Minecraft" -> 0;
            default -> ColorUtil.lerp(t.bg(), t.bg2(), 0.5f);
        };
    }

    public String style() {
        return (String)this.style.get();
    }

    public boolean stripe() {
        return (Boolean)this.stripe.get();
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        if (!editor) {
            return;
        }
        Theme t = ChatLayoutHud.theme();
        int bg = this.background(t);
        float a = (float)((Double)ChatLayoutHud.mc.options.textBackgroundOpacity().get() * 0.9 + 0.1);
        if (AnimeChatModule.chatStyled() && this.style.is("\u0421\u0442\u0435\u043a\u043b\u043e")) {
            AnimeChatModule.drawGlass(ctx, 0.0f, h - 44.0f, w, 44.0f, a);
        }
        String[] sample = new String[]{"[15:41] [G] \u0418\u0433\u0440\u043e\u043a \u25b8 \u043f\u0440\u0438\u0432\u0435\u0442!", "[15:41] [Spy] Minor_GG \u25b8 \u0434\u0430\u0439 \u0441\u0435\u0442 (x3)", "[15:42] [+] Dima /ah", "[15:42] \u2014\u2014\u2014\u2014 \u0421\u0435\u0440\u0432\u0435\u0440 \u00b7 15:42 \u2014\u2014\u2014\u2014"};
        float y = h - 10.0f;
        for (int i = sample.length - 1; i >= 0 && y > 0.0f; y -= 10.0f, --i) {
            if (AnimeChatModule.chatStyled()) {
                AnimeChatModule.chatFill(ctx, 0, (int)y - 1, (int)w, (int)y + 9, ColorUtil.withAlpha(-16777216, a));
            } else {
                ctx.fill(0, (int)y - 1, (int)w, (int)y + 9, ColorUtil.withAlpha(-16777216, a));
            }
            Fonts.draw(ctx, sample[i], 4.0f, y, 7.0f, ColorUtil.withAlpha(t.text(), 0.9f), false);
        }
        Fonts.draw(ctx, "\u0427\u0430\u0442", 4.0f, 3.0f, 7.0f, t.accent2(), true);
    }
}

