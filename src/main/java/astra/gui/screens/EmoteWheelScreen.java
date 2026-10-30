/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 */
package astra.gui.screens;

import astra.animation.AnimationUtils;
import astra.module.modules.cosmetic.EmoteCosmetic;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class EmoteWheelScreen
extends Screen {
    private static final String[] EMOTES = new String[]{"\u041b\u044e\u0431\u043e\u0432\u044c", "\u0412\u043e\u0441\u0442\u043e\u0440\u0433", "\u041c\u0443\u0437\u044b\u043a\u0430", "\u0417\u043b\u043e\u0441\u0442\u044c", "\u0421\u043e\u043d"};
    private static final String[] ICONS = new String[]{"\u2665", "\u2605", "\u266a", "!!", "Zz"};
    private static final int[] COLORS = new int[]{-41048, -8090, -8530948, -50340, -3885571};
    private final EmoteCosmetic emotes;
    private final int key;
    private final long openedAt = System.currentTimeMillis();
    private int hovered = -1;

    public EmoteWheelScreen(EmoteCosmetic emotes, int key) {
        super((Component)Component.method_43470((String)"\u042d\u043c\u043e\u0446\u0438\u0438"));
        this.emotes = emotes;
        this.key = key;
    }

    public boolean method_25421() {
        return false;
    }

    public void method_25420(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        Theme t = ThemeManager.get();
        ctx.method_25296(0, 0, this.field_22789, this.field_22790, ColorUtil.withAlpha(t.bg(), 0.25f), ColorUtil.withAlpha(t.bg(), 0.45f));
    }

    public void method_25394(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        super.method_25394(ctx, mouseX, mouseY, delta);
        Theme t = ThemeManager.get();
        float open = AnimationUtils.easeOutBack(Math.min(1.0f, (float)(System.currentTimeMillis() - this.openedAt) / 220.0f));
        float cx = (float)this.field_22789 / 2.0f;
        float cy = (float)this.field_22790 / 2.0f;
        float r = 78.0f * open;
        float inner = 26.0f * open;
        double dx = (float)mouseX - cx;
        double dy = (float)mouseY - cy;
        int n = EMOTES.length;
        this.hovered = Math.hypot(dx, dy) < (double)inner ? -1 : Math.floorMod(Math.round((Math.atan2(dy, dx) + 1.5707963267948966) / (Math.PI * 2) * (double)n), n);
        Render2D.glow(ctx, cx - r, cy - r, r * 2.0f, r * 2.0f, r, 16.0f, ColorUtil.withAlpha(t.accent(), 0.3f));
        Render2D.circle(ctx, cx, cy, r, Paint.linear(cx - r, cy - r, cx + r, cy + r, ColorUtil.withAlpha(t.bg2(), 0.92f), ColorUtil.withAlpha(t.bg(), 0.95f)));
        Render2D.ring(ctx, cx, cy, r, 1.4f, ColorUtil.withAlpha(t.accent2(), 0.8f));
        for (int i = 0; i < n; ++i) {
            boolean hov;
            float start = (float)(-1.5707963267948966 + ((double)i - 0.5) * 2.0 * Math.PI / (double)n);
            float sweep = (float)(Math.PI * 2 / (double)n);
            boolean bl = hov = i == this.hovered;
            if (hov) {
                Render2D.arc(ctx, cx, cy, (r + inner) / 2.0f, r - inner - 4.0f, (float)Math.toDegrees(start) + 2.0f, (float)Math.toDegrees(sweep) - 4.0f, ColorUtil.withAlpha(COLORS[i], 0.35f));
            }
            float ax = cx + (float)Math.cos(start) * inner;
            float ay = cy + (float)Math.sin(start) * inner;
            float bx = cx + (float)Math.cos(start) * r;
            float by = cy + (float)Math.sin(start) * r;
            Render2D.line(ctx, ax, ay, bx, by, 0.8f, ColorUtil.withAlpha(t.accent(), 0.35f));
            double mid = start + sweep / 2.0f;
            float ix = cx + (float)Math.cos(mid) * (r + inner) / 2.0f;
            float iy = cy + (float)Math.sin(mid) * (r + inner) / 2.0f;
            float size = hov ? 15.0f : 12.0f;
            Fonts.drawCentered(ctx, ICONS[i], ix, iy - size * 0.7f, size, COLORS[i], true);
            Fonts.drawCentered(ctx, EMOTES[i], ix, iy + 5.0f, 6.2f, hov ? t.text() : t.textDim(), hov);
        }
        Render2D.circle(ctx, cx, cy, inner - 2.0f, ColorUtil.withAlpha(t.bg(), 0.95f));
        Render2D.ring(ctx, cx, cy, inner - 2.0f, 1.0f, ColorUtil.withAlpha(t.accent(), 0.6f));
        Fonts.drawCentered(ctx, this.hovered >= 0 ? EMOTES[this.hovered] : "\u041e\u0442\u043c\u0435\u043d\u0430", cx, cy - 3.5f, 7.0f, t.text(), true);
    }

    private void choose() {
        if (this.hovered >= 0) {
            this.emotes.playEmote(EMOTES[this.hovered]);
        }
        this.method_25419();
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == this.key) {
            this.choose();
            return true;
        }
        return super.method_16803(new KeyEvent(keyCode, scanCode, modifiers));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.choose();
        return true;
    }

    public boolean method_25402(MouseButtonEvent click, boolean doubled) {
        return this.mouseClicked(click.comp_4798(), click.comp_4799(), click.method_74245());
    }

    public boolean method_16803(KeyEvent input) {
        return this.keyReleased(input.comp_4795(), input.comp_4796(), input.comp_4797());
    }
}

