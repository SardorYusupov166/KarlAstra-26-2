/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.gui.components;

import astra.gui.UiContext;
import astra.gui.components.Popup;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.ColorSetting;
import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ColorPickerPopup
extends Popup {
    private static final int[] SWATCHES = new int[]{-5745153, -4160260, -45608, -28744, -13058568, -14494738, -13315175, -340971, -290244, -770210, -1, -16054504};
    private final ColorSetting setting;
    private float hue;
    private float sat;
    private float val;
    private int alpha;
    private int dragging;
    private final float pad = 7.0f;
    private final float svSize = 92.0f;
    private final float barW = 9.0f;

    public ColorPickerPopup(float x, float y, ColorSetting setting) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.w = 121.0f + (setting.allowsAlpha() ? 15.0f : 0.0f);
        this.h = 142.0f;
        float[] hsv = ColorUtil.toHsv(setting.getColor());
        this.hue = hsv[0];
        this.sat = hsv[1];
        this.val = hsv[2];
        this.alpha = ColorUtil.alpha(setting.getColor());
    }

    private void apply() {
        this.setting.set(ColorUtil.fromHsv(this.hue, this.sat, this.val, this.setting.allowsAlpha() ? this.alpha : 255));
    }

    @Override
    public void render(UiContext ui) {
        Theme t = ui.theme;
        float a = this.open.update(1.0f, ui.dt);
        float prev = Render2D.alpha;
        Render2D.alpha *= a;
        GuiGraphicsExtractor ctx = ui.ctx;
        Render2D.shadow(ctx, this.x, this.y, this.w, this.h, 6.0f, 10.0f, -1879048192);
        Render2D.roundRect(ctx, this.x, this.y, this.w, this.h, 6.0f, Paint.vertical(this.y, this.y + this.h, t.bg2(), t.bg()));
        Render2D.outline(ctx, this.x, this.y, this.w, this.h, 6.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.6f));
        float sx = this.x + 7.0f;
        float sy = this.y + 7.0f;
        int pure = ColorUtil.fromHsv(this.hue, 1.0f, 1.0f, 255);
        Render2D.roundRect(ctx, sx, sy, 92.0f, 92.0f, 4.0f, Paint.horizontal(sx, sx + 92.0f, -1, pure));
        Render2D.roundRect(ctx, sx, sy, 92.0f, 92.0f, 4.0f, Paint.vertical(sy, sy + 92.0f, 0, -16777216));
        float kx = sx + this.sat * 92.0f;
        float ky = sy + (1.0f - this.val) * 92.0f;
        Render2D.ring(ctx, kx, ky, 3.5f, 1.4f, -1);
        Render2D.ring(ctx, kx, ky, 4.8f, 0.8f, Integer.MIN_VALUE);
        float hx = sx + 92.0f + 6.0f;
        for (int i = 0; i < 6; ++i) {
            float y0 = sy + 92.0f * (float)i / 6.0f;
            float y1 = sy + 92.0f * (float)(i + 1) / 6.0f;
            Render2D.rect(ctx, hx, y0, 9.0f, y1 - y0 + 0.2f, Paint.vertical(y0, y1, ColorUtil.fromHsv((float)i / 6.0f, 1.0f, 1.0f, 255), ColorUtil.fromHsv((float)(i + 1) / 6.0f, 1.0f, 1.0f, 255)));
        }
        Render2D.outline(ctx, hx, sy, 9.0f, 92.0f, 2.0f, 0.7f, 0x60FFFFFF);
        float hy = sy + this.hue * 92.0f;
        Render2D.roundRect(ctx, hx - 1.5f, hy - 1.5f, 12.0f, 3.0f, 1.5f, -1);
        if (this.setting.allowsAlpha()) {
            float ax = hx + 9.0f + 6.0f;
            int opaque = ColorUtil.fromHsv(this.hue, this.sat, this.val, 255);
            for (int i = 0; i < 12; ++i) {
                Render2D.rect(ctx, ax + (float)(i % 2) * 9.0f / 2.0f, sy + (float)i * 92.0f / 12.0f, 4.5f, 7.6666665f, -8355712);
            }
            Render2D.rect(ctx, ax, sy, 9.0f, 92.0f, Paint.vertical(sy, sy + 92.0f, opaque, opaque & 0xFFFFFF));
            Render2D.outline(ctx, ax, sy, 9.0f, 92.0f, 2.0f, 0.7f, 0x60FFFFFF);
            float ay = sy + (1.0f - (float)this.alpha / 255.0f) * 92.0f;
            Render2D.roundRect(ctx, ax - 1.5f, ay - 1.5f, 12.0f, 3.0f, 1.5f, -1);
        }
        float ry = sy + 92.0f + 8.0f;
        float sw = (this.w - 14.0f - (float)(SWATCHES.length - 1) * 2.5f) / (float)SWATCHES.length;
        for (int i = 0; i < SWATCHES.length; ++i) {
            float cx = sx + (float)i * (sw + 2.5f);
            boolean hov = ui.mx >= cx && ui.mx < cx + sw && ui.my >= ry && ui.my < ry + 12.0f;
            Render2D.roundRect(ctx, cx, ry, sw, 12.0f, 3.0f, SWATCHES[i]);
            Render2D.outline(ctx, cx, ry, sw, 12.0f, 3.0f, hov ? 1.2f : 0.6f, hov ? -1 : 0x50FFFFFF);
        }
        float ty = ry + 12.0f + 5.0f;
        Render2D.circle(ctx, sx + 4.0f, ty + 4.0f, 4.0f, this.setting.getColor());
        Fonts.draw(ctx, this.setting.hex(), sx + 12.0f, ty + 0.5f, 7.0f, t.text(), true);
        Render2D.alpha = prev;
    }

    private void updateDrag(double mx, double my) {
        float sx = this.x + 7.0f;
        float sy = this.y + 7.0f;
        if (this.dragging == 1) {
            this.sat = (float)Math.max(0.0, Math.min(1.0, (mx - (double)sx) / 92.0));
            this.val = 1.0f - (float)Math.max(0.0, Math.min(1.0, (my - (double)sy) / 92.0));
        } else if (this.dragging == 2) {
            this.hue = (float)Math.max(0.0, Math.min(0.999, (my - (double)sy) / 92.0));
        } else if (this.dragging == 3) {
            this.alpha = Math.round(255.0f * (1.0f - (float)Math.max(0.0, Math.min(1.0, (my - (double)sy) / 92.0))));
        }
        this.apply();
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        int i;
        if (!this.contains(mx, my)) {
            return false;
        }
        float sx = this.x + 7.0f;
        float sy = this.y + 7.0f;
        float hx = sx + 92.0f + 6.0f;
        if (mx >= (double)sx && mx < (double)(sx + 92.0f) && my >= (double)sy && my < (double)(sy + 92.0f)) {
            this.dragging = 1;
        } else if (mx >= (double)(hx - 2.0f) && mx < (double)(hx + 9.0f + 2.0f) && my >= (double)sy && my < (double)(sy + 92.0f)) {
            this.dragging = 2;
        } else if (this.setting.allowsAlpha() && mx >= (double)(hx + 9.0f + 4.0f) && mx < (double)(hx + 18.0f + 8.0f) && my >= (double)sy && my < (double)(sy + 92.0f)) {
            this.dragging = 3;
        }
        if (this.dragging != 0) {
            this.updateDrag(mx, my);
            return true;
        }
        float ry = sy + 92.0f + 8.0f;
        float sw = (this.w - 14.0f - (float)(SWATCHES.length - 1) * 2.5f) / (float)SWATCHES.length;
        if (my >= (double)ry && my < (double)(ry + 12.0f) && (i = (int)((mx - (double)sx) / (double)(sw + 2.5f))) >= 0 && i < SWATCHES.length) {
            float[] hsv = ColorUtil.toHsv(SWATCHES[i]);
            this.hue = hsv[0];
            this.sat = hsv[1];
            this.val = hsv[2];
            this.apply();
        }
        return true;
    }

    @Override
    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        if (this.dragging == 0) {
            return false;
        }
        this.updateDrag(mx, my);
        return true;
    }

    @Override
    public void mouseReleased(UiContext ui, double mx, double my, int button) {
        this.dragging = 0;
    }
}

