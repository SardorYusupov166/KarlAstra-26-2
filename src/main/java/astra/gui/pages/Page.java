/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.pages;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.screens.ClickGuiScreen;
import astra.render.ColorUtil;
import astra.render.Render2D;

public abstract class Page {
    protected final ClickGuiScreen screen;
    protected float x;
    protected float y;
    protected float w;
    protected float h;
    protected float contentHeight;
    protected float scrollTarget;
    protected final Animation scroll = new Animation(0.0f, 16.0f);

    protected Page(ClickGuiScreen screen) {
        this.screen = screen;
    }

    public void setBounds(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    protected float maxScroll() {
        return Math.max(0.0f, this.contentHeight - this.h);
    }

    protected float scrollY() {
        return this.scroll.get();
    }

    protected abstract float layout(float var1);

    protected abstract void renderContent(UiContext var1);

    public final void render(UiContext ui) {
        this.scrollTarget = Math.max(0.0f, Math.min(this.maxScroll(), this.scrollTarget));
        this.scroll.update(this.scrollTarget, ui.dt);
        this.contentHeight = this.layout(this.y - this.scroll.get());
        ui.setClip(this.x, this.y, this.w, this.h);
        ui.ctx.method_44379((int)Math.floor(this.x - 12.0f), (int)Math.floor(this.y), (int)Math.ceil(this.x + this.w + 12.0f), (int)Math.ceil(this.y + this.h));
        this.renderContent(ui);
        ui.ctx.method_44380();
        ui.clearClip();
        this.renderScrollbar(ui);
    }

    private void renderScrollbar(UiContext ui) {
        if (this.maxScroll() <= 0.0f) {
            return;
        }
        float barH = Math.max(20.0f, this.h * this.h / this.contentHeight);
        float barY = this.y + (this.h - barH) * (this.scroll.get() / this.maxScroll());
        Render2D.roundRect(ui.ctx, this.x + this.w + 4.0f, this.y, 2.5f, this.h, 1.25f, ColorUtil.withAlpha(ui.theme.track(), 0.6f));
        Render2D.roundRect(ui.ctx, this.x + this.w + 4.0f, barY, 2.5f, barH, 1.25f, ColorUtil.withAlpha(ui.theme.accent(), 0.8f));
    }

    public boolean mouseScrolled(double mx, double my, double amount) {
        if (mx < (double)this.x || mx > (double)(this.x + this.w + 10.0f) || my < (double)this.y || my > (double)(this.y + this.h)) {
            return false;
        }
        this.scrollTarget = (float)Math.max(0.0, Math.min((double)this.maxScroll(), (double)this.scrollTarget - amount * 28.0));
        return true;
    }

    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        return false;
    }

    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        return false;
    }

    public void mouseReleased(UiContext ui, double mx, double my, int button) {
    }

    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    public void onShow() {
    }

    protected boolean inView(double mx, double my) {
        return mx >= (double)this.x && mx < (double)(this.x + this.w) && my >= (double)this.y && my < (double)(this.y + this.h);
    }
}

