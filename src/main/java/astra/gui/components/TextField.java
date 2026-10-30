/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package astra.gui.components;

import astra.animation.Animation;
import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.Widget;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class TextField
extends Widget {
    private final PopupHost host;
    private final String placeholder;
    private final int maxLength;
    private String text = "";
    private int cursor;
    private Consumer<String> onSubmit;
    private Runnable onCancel;
    private final Animation focusAnim = new Animation(0.0f, 14.0f);

    public TextField(PopupHost host, String placeholder, int maxLength) {
        this.host = host;
        this.placeholder = placeholder;
        this.maxLength = maxLength;
    }

    public TextField onSubmit(Consumer<String> onSubmit) {
        this.onSubmit = onSubmit;
        return this;
    }

    public TextField onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text.length() > this.maxLength ? text.substring(0, this.maxLength) : text;
        this.cursor = this.text.length();
    }

    public boolean isFocused() {
        return this.host.getFocus() == this;
    }

    public void focus() {
        this.host.setFocus(this);
    }

    @Override
    protected float measure(float width) {
        return 18.0f;
    }

    @Override
    public void render(UiContext ui) {
        Theme t = ui.theme;
        float f = this.focusAnim.update(this.isFocused() ? 1.0f : 0.0f, ui.dt);
        Render2D.roundRect(ui.ctx, this.x, this.y, this.w, this.h, 5.0f, ColorUtil.withAlpha(t.bg(), 0.75f));
        Render2D.outline(ui.ctx, this.x, this.y, this.w, this.h, 5.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.25f + 0.6f * f));
        float size = 7.2f;
        float ty = this.y + (this.h - size) / 2.0f + 0.4f;
        ui.ctx.enableScissor((int)this.x + 2, (int)this.y, (int)(this.x + this.w - 2.0f), (int)(this.y + this.h));
        if (this.text.isEmpty() && !this.isFocused()) {
            Fonts.draw(ui.ctx, this.placeholder, this.x + 6.0f, ty, size, t.textMuted(), false);
        } else {
            float before = Fonts.width(this.text.substring(0, this.cursor), size, false);
            float shift = Math.max(0.0f, before - (this.w - 14.0f));
            Fonts.draw(ui.ctx, this.text, this.x + 6.0f - shift, ty, size, t.text(), false);
            if (this.isFocused() && System.currentTimeMillis() / 500L % 2L == 0L) {
                Render2D.rect(ui.ctx, this.x + 6.0f - shift + before, this.y + 4.0f, 0.8f, this.h - 8.0f, t.accent());
            }
        }
        ui.ctx.disableScissor();
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        if (this.contains(mx, my)) {
            this.focus();
            this.cursor = this.text.length();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }
        boolean ctrl = (modifiers & 2) != 0;
        switch (key) {
            case 257: 
            case 335: {
                this.host.setFocus(null);
                if (this.onSubmit == null) break;
                this.onSubmit.accept(this.text.trim());
                break;
            }
            case 256: {
                this.host.setFocus(null);
                if (this.onCancel == null) break;
                this.onCancel.run();
                break;
            }
            case 259: {
                if (this.cursor <= 0) break;
                this.text = this.text.substring(0, this.cursor - 1) + this.text.substring(this.cursor);
                --this.cursor;
                break;
            }
            case 261: {
                if (this.cursor >= this.text.length()) break;
                this.text = this.text.substring(0, this.cursor) + this.text.substring(this.cursor + 1);
                break;
            }
            case 263: {
                this.cursor = Math.max(0, this.cursor - 1);
                break;
            }
            case 262: {
                this.cursor = Math.min(this.text.length(), this.cursor + 1);
                break;
            }
            case 268: {
                this.cursor = 0;
                break;
            }
            case 269: {
                this.cursor = this.text.length();
                break;
            }
            case 86: {
                if (!ctrl) break;
                this.insert(Minecraft.getInstance().keyboardHandler.getClipboard());
                break;
            }
            case 65: {
                if (!ctrl) break;
                this.cursor = this.text.length();
                break;
            }
        }
        return true;
    }

    private void insert(String s) {
        s = s.replaceAll("[\\r\\n\\t]", "");
        int room = this.maxLength - this.text.length();
        if (room <= 0 || s.isEmpty()) {
            return;
        }
        if (s.length() > room) {
            s = s.substring(0, room);
        }
        this.text = this.text.substring(0, this.cursor) + s + this.text.substring(this.cursor);
        this.cursor += s.length();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isFocused()) {
            return false;
        }
        if (chr >= ' ' && chr != '\u007f') {
            this.insert(String.valueOf(chr));
        }
        return true;
    }
}

