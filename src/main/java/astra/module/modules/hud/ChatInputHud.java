/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.hud.AnimeChatModule;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.joml.Matrix3x2fStack;

public final class ChatInputHud
extends HudModule {
    private final NumberSetting width = this.add(new NumberSetting("\u0428\u0438\u0440\u0438\u043d\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", 100.0, 15.0, 100.0, 1.0).unit("%"));
    private final NumberSetting height = this.add(new NumberSetting("\u0412\u044b\u0441\u043e\u0442\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", 14.0, 14.0, 48.0, 1.0));
    private final ModeSetting style = this.add(new ModeSetting("\u041e\u0444\u043e\u0440\u043c\u043b\u0435\u043d\u0438\u0435 \u0441\u0442\u0440\u043e\u043a\u0438", "\u041a\u0430\u043a \u0443 \u0447\u0430\u0442\u0430", "\u041a\u0430\u043a \u0443 \u0447\u0430\u0442\u0430", "\u0421\u0432\u043e\u044f \u043f\u0430\u043d\u0435\u043b\u044c", "\u0411\u0435\u0437 \u0444\u043e\u043d\u0430"));
    private final ColorSetting textColor = this.add(new ColorSetting("\u0426\u0432\u0435\u0442 \u0442\u0435\u043a\u0441\u0442\u0430 \u0441\u0442\u0440\u043e\u043a\u0438", -1));

    public ChatInputHud() {
        super("chat_input", "\u0421\u0442\u0440\u043e\u043a\u0430 \u0432\u0432\u043e\u0434\u0430 \u0447\u0430\u0442\u0430", "\u041f\u043e\u043b\u0435 \u043f\u043e\u0441\u043b\u0435 T: \u043f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435, \u0448\u0438\u0440\u0438\u043d\u0430, \u0432\u044b\u0441\u043e\u0442\u0430, \u0444\u043e\u043d \u0438 \u0446\u0432\u0435\u0442 \u0442\u0435\u043a\u0441\u0442\u0430. \u0420\u0430\u0437\u043c\u0435\u0440 \u043c\u0435\u043d\u044f\u0435\u0442 \u043e\u0431\u043b\u0430\u0441\u0442\u044c \u0432\u0432\u043e\u0434\u0430, \u043d\u0435 \u0448\u0440\u0438\u0444\u0442.", Category.HUD, Icon.KEYBOARD, 0.5f, 0.97f);
        this.setDefaultEnabled(true);
    }

    public static ChatInputHud get() {
        return ModuleManager.get(ChatInputHud.class);
    }

    @Override
    public boolean shouldRender() {
        return false;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    public float width() {
        float available = Math.max(24, mc.method_22683().method_4486() - 4);
        return Math.max(24.0f, Math.min(available, available * this.width.getFloat() / 100.0f * this.getScale())) / this.getScale();
    }

    @Override
    public float height() {
        return Math.max(14.0f, this.height.getFloat() * this.getScale()) / this.getScale();
    }

    @Override
    public float getX(float sw) {
        return this.isAtDefault() ? 2.0f : super.getX(sw);
    }

    @Override
    public float getY(float sh) {
        return this.isAtDefault() ? Math.max(0.0f, sh - this.scaledHeight() - 2.0f) : super.getY(sh);
    }

    public Bounds bounds() {
        Window w = mc.method_22683();
        return new Bounds(Math.round(this.getX(w.method_4486())), Math.round(this.getY(w.method_4502())), Math.round(this.scaledWidth()), Math.round(this.scaledHeight()));
    }

    public void place(EditBox field) {
        Bounds b = this.bounds();
        field.method_46421(b.x + 4);
        field.method_46419(b.y + (b.height - 8) / 2);
        field.method_25358(Math.max(16, b.width - 8));
        field.method_53533(10);
        field.method_1868(this.textColor.getColor());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void drawBackground(GuiGraphics ctx, int x, int y, int w, int h) {
        if (!((Boolean)this.background.get()).booleanValue() || this.style.is("\u0411\u0435\u0437 \u0444\u043e\u043d\u0430")) {
            return;
        }
        if (this.style.is("\u0421\u0432\u043e\u044f \u043f\u0430\u043d\u0435\u043b\u044c")) {
            this.drawPanel(ctx, x, y, w, h, 4.0f);
        } else if (AnimeChatModule.chatStyled()) {
            float previous = Render2D.alpha;
            Render2D.alpha *= this.opacity.getFloat() / 100.0f;
            try {
                AnimeChatModule.chatInput(ctx, x, y, x + w, y + h);
            }
            finally {
                Render2D.alpha = previous;
            }
        } else {
            ctx.method_25294(x, y, x + w, y + h, ChatInputHud.mc.field_1690.method_19344(Integer.MIN_VALUE));
        }
    }

    @Override
    protected void renderContent(GuiGraphics ctx, float w, float h, boolean editor) {
        if (!editor) {
            return;
        }
        Matrix3x2fStack m = ctx.method_51448();
        m.pushMatrix();
        m.scale(1.0f / this.getScale(), 1.0f / this.getScale());
        this.drawBackground(ctx, 0, 0, Math.round(w * this.getScale()), Math.round(h * this.getScale()));
        ctx.method_51433(ChatInputHud.mc.field_1772, "\u041d\u0430\u043f\u0438\u0441\u0430\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435\u2026 |", 4, (Math.round(h * this.getScale()) - 8) / 2, this.textColor.getColor(), true);
        m.popMatrix();
    }

    public record Bounds(int x, int y, int width, int height) {
        public boolean contains(double px, double py) {
            return px >= (double)this.x && px < (double)(this.x + this.width) && py >= (double)this.y && py < (double)(this.y + this.height);
        }
    }
}

