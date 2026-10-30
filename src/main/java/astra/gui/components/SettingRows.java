/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.animation.Animation;
import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.ColorPickerPopup;
import astra.gui.components.DropdownPopup;
import astra.gui.components.Popup;
import astra.gui.components.TextField;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ColorSetting;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.settings.Setting;
import astra.settings.StringSetting;
import astra.theme.Theme;

public final class SettingRows {
    public static final float ROW_H = 15.0f;
    public static final float LABEL_SIZE = 7.2f;

    private SettingRows() {
    }

    public static Widget create(Setting<?> setting, PopupHost host) {
        if (setting instanceof BooleanSetting) {
            BooleanSetting b = (BooleanSetting)setting;
            return new ToggleRow(b);
        }
        if (setting instanceof NumberSetting) {
            NumberSetting n = (NumberSetting)setting;
            return new SliderRow(n);
        }
        if (setting instanceof ModeSetting) {
            ModeSetting m = (ModeSetting)setting;
            return new ModeRow(m, host);
        }
        if (setting instanceof ColorSetting) {
            ColorSetting c = (ColorSetting)setting;
            return new ColorRow(c, host);
        }
        if (setting instanceof KeybindSetting) {
            KeybindSetting k = (KeybindSetting)setting;
            return new KeybindRow(k, host);
        }
        if (setting instanceof ButtonSetting) {
            ButtonSetting b = (ButtonSetting)setting;
            return new ButtonRow(b);
        }
        if (setting instanceof StringSetting) {
            StringSetting s = (StringSetting)setting;
            return new TextRow(s, host);
        }
        return null;
    }

    public static class ToggleRow
    extends Row<BooleanSetting> {
        private final Animation anim;

        ToggleRow(BooleanSetting s) {
            super(s);
            this.anim = new Animation((Boolean)s.get() != false ? 1.0f : 0.0f, 14.0f);
        }

        @Override
        public void render(UiContext ui) {
            boolean hov = ui.hover(this.x, this.y, this.w, this.h);
            this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
            float p = this.anim.update((Boolean)((BooleanSetting)this.setting).get() != false ? 1.0f : 0.0f, ui.dt);
            this.label(ui, this.w - 22.0f - 6.0f);
            UiDraw.toggle(ui.ctx, ui.theme, this.x + this.w - 22.0f, this.y + (this.h - 12.0f) / 2.0f, p, this.hover.get());
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (button == 0 && this.contains(mx, my)) {
                ((BooleanSetting)this.setting).toggle();
                return true;
            }
            return false;
        }
    }

    public static class SliderRow
    extends Row<NumberSetting> {
        private final Animation value;
        private boolean dragging;
        private float trackX;
        private float trackW;

        SliderRow(NumberSetting s) {
            super(s);
            this.value = new Animation((float)s.getPercent(), 18.0f);
        }

        private void layoutTrack() {
            float labelW = Math.min(this.w * 0.42f, Fonts.width(((NumberSetting)this.setting).getLabel(), 7.2f, false) + 8.0f);
            float valueW = 30.0f;
            this.trackX = this.x + labelW + 4.0f;
            this.trackW = Math.max(20.0f, this.w - labelW - valueW - 8.0f);
        }

        @Override
        public void render(UiContext ui) {
            this.layoutTrack();
            boolean hov = ui.hover(this.x, this.y, this.w, this.h) || this.dragging;
            this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
            float p = this.value.update((float)((NumberSetting)this.setting).getPercent(), ui.dt);
            this.label(ui, this.trackX - this.x - 4.0f);
            UiDraw.sliderTrack(ui.ctx, ui.theme, this.trackX, this.y + this.h / 2.0f, this.trackW, p, this.hover.get());
            Fonts.drawRight(ui.ctx, ((NumberSetting)this.setting).display(), this.x + this.w, this.y + (this.h - 7.0f) / 2.0f + 0.4f, 7.0f, ui.theme.text(), false);
        }

        private void update(double mx) {
            ((NumberSetting)this.setting).setPercent((mx - (double)this.trackX) / (double)this.trackW);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            this.layoutTrack();
            if (button == 0 && this.contains(mx, my) && mx >= (double)(this.trackX - 5.0f) && mx <= (double)(this.trackX + this.trackW + 5.0f)) {
                this.dragging = true;
                this.update(mx);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
            if (!this.dragging) {
                return false;
            }
            this.update(mx);
            return true;
        }

        @Override
        public void mouseReleased(UiContext ui, double mx, double my, int button) {
            this.dragging = false;
        }

        @Override
        public boolean keyPressed(int key, int scanCode, int modifiers) {
            return false;
        }
    }

    public static class ModeRow
    extends Row<ModeSetting> {
        private final PopupHost host;
        private boolean open;
        private float boxX;
        private float boxW;
        private DropdownPopup popup;

        ModeRow(ModeSetting s, PopupHost host) {
            super(s);
            this.host = host;
        }

        private boolean fullWidth() {
            return ((ModeSetting)this.setting).getName().startsWith("#");
        }

        @Override
        protected float measure(float width) {
            return this.fullWidth() ? 16.0f : 15.0f;
        }

        private void layoutBox() {
            if (this.fullWidth()) {
                this.boxX = this.x;
                this.boxW = this.w;
            } else {
                this.boxW = Math.max(70.0f, Math.min(this.w * 0.55f, 120.0f));
                this.boxX = this.x + this.w - this.boxW;
            }
        }

        @Override
        public void render(UiContext ui) {
            DropdownPopup d;
            Popup popup;
            this.layoutBox();
            boolean hov = ui.hover(this.boxX, this.y, this.boxW, this.h);
            this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
            if (!this.fullWidth()) {
                this.label(ui, this.boxX - this.x - 4.0f);
            }
            this.open = (popup = this.host.currentPopup()) instanceof DropdownPopup && (d = (DropdownPopup)popup) == this.popup;
            UiDraw.dropdownBox(ui.ctx, ui.theme, this.boxX, this.y + 1.0f, this.boxW, this.h - 2.0f, (String)((ModeSetting)this.setting).get(), this.hover.get(), this.open);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            this.layoutBox();
            if (this.contains(mx, my) && mx >= (double)this.boxX) {
                if (button == 1) {
                    ((ModeSetting)this.setting).cycle(1);
                    return true;
                }
                this.popup = new DropdownPopup(this.boxX, this.y + this.h + 1.0f, this.boxW, ((ModeSetting)this.setting).getModes(), ((ModeSetting)this.setting)::get, ((ModeSetting)this.setting)::set, this.host::closePopup);
                this.host.openPopup(this.popup);
                return true;
            }
            return false;
        }
    }

    public static class ColorRow
    extends Row<ColorSetting> {
        private final PopupHost host;

        ColorRow(ColorSetting s, PopupHost host) {
            super(s);
            this.host = host;
        }

        @Override
        public void render(UiContext ui) {
            boolean hov = ui.hover(this.x, this.y, this.w, this.h);
            this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
            String hex = ((ColorSetting)this.setting).hex();
            float hexW = Fonts.width(hex, 6.5f, false);
            this.label(ui, this.w - hexW - 24.0f);
            float sw = 16.0f;
            float sh = 9.0f;
            float sx = this.x + this.w - sw;
            float sy = this.y + (this.h - sh) / 2.0f;
            if (UiDraw.glowStrength() > 0.0f) {
                Render2D.glow(ui.ctx, sx, sy, sw, sh, 3.0f, 4.0f, ColorUtil.withAlpha(((ColorSetting)this.setting).getColor(), 0.4f));
            }
            Render2D.roundRect(ui.ctx, sx, sy, sw, sh, 3.0f, ((ColorSetting)this.setting).getColor());
            Render2D.outline(ui.ctx, sx, sy, sw, sh, 3.0f, 0.7f, ColorUtil.withAlpha(0xFFFFFF, 0.25f + 0.4f * this.hover.get()));
            Fonts.drawRight(ui.ctx, hex, sx - 4.0f, this.y + (this.h - 6.5f) / 2.0f + 0.4f, 6.5f, ui.theme.textMuted(), false);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (button == 0 && this.contains(mx, my)) {
                this.host.openPopup(new ColorPickerPopup(this.x + this.w - 140.0f, this.y + this.h + 2.0f, (ColorSetting)this.setting));
                return true;
            }
            return false;
        }
    }

    public static class KeybindRow
    extends Row<KeybindSetting> {
        private final PopupHost host;

        KeybindRow(KeybindSetting s, PopupHost host) {
            super(s);
            this.host = host;
        }

        private boolean listening() {
            return this.host.getFocus() == this;
        }

        @Override
        public void render(UiContext ui) {
            String key = this.listening() ? "\u2026" : ((KeybindSetting)this.setting).keyName();
            float kw = Math.max(26.0f, Fonts.width(key, 7.0f, true) + 12.0f);
            boolean hov = ui.hover(this.x + this.w - kw, this.y, kw, this.h);
            this.hover.update(hov || this.listening() ? 1.0f : 0.0f, ui.dt);
            this.label(ui, this.w - kw - 6.0f);
            float kx = this.x + this.w - kw;
            Theme t = ui.theme;
            Render2D.roundRect(ui.ctx, kx, this.y + 1.0f, kw, this.h - 2.0f, 4.0f, ColorUtil.withAlpha(t.bg(), 0.8f));
            Render2D.outline(ui.ctx, kx, this.y + 1.0f, kw, this.h - 2.0f, 4.0f, 0.8f, ColorUtil.withAlpha(t.accent(), 0.35f + 0.5f * this.hover.get()));
            Fonts.drawCentered(ui.ctx, key, kx + kw / 2.0f, this.y + (this.h - 7.0f) / 2.0f + 0.4f, 7.0f, t.text(), true);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (this.contains(mx, my)) {
                if (button == 1) {
                    ((KeybindSetting)this.setting).set(-1);
                    this.host.setFocus(null);
                } else {
                    this.host.setFocus(this.listening() ? null : this);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int key, int scanCode, int modifiers) {
            if (!this.listening()) {
                return false;
            }
            if (key == 256) {
                this.host.setFocus(null);
            } else if (key == 259 || key == 261) {
                ((KeybindSetting)this.setting).set(-1);
                this.host.setFocus(null);
            } else {
                ((KeybindSetting)this.setting).set(key);
                this.host.setFocus(null);
            }
            return true;
        }
    }

    public static class ButtonRow
    extends Row<ButtonSetting> {
        ButtonRow(ButtonSetting s) {
            super(s);
        }

        @Override
        protected float measure(float width) {
            return 17.0f;
        }

        @Override
        public void render(UiContext ui) {
            boolean hov = ui.hover(this.x, this.y, this.w, this.h);
            this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
            UiDraw.button(ui.ctx, ui.theme, this.x, this.y + 1.0f, this.w, this.h - 2.0f, ((ButtonSetting)this.setting).buttonText(), null, this.hover.get(), false);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            if (button == 0 && this.contains(mx, my)) {
                ((ButtonSetting)this.setting).press();
                return true;
            }
            return false;
        }
    }

    public static class TextRow
    extends Row<StringSetting> {
        private final TextField field;
        private boolean wasFocused;

        TextRow(StringSetting s, PopupHost host) {
            super(s);
            this.field = new TextField(host, s.placeholder(), s.maxLength()).onSubmit(v -> ((StringSetting)this.setting).set(v));
            this.field.setText((String)s.get());
        }

        @Override
        protected float measure(float width) {
            return 33.0f;
        }

        @Override
        public void render(UiContext ui) {
            boolean hov = ui.hover(this.x, this.y, this.w, this.h);
            this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
            Fonts.draw(ui.ctx, ((StringSetting)this.setting).getLabel(), this.x, this.y + 2.0f, 7.2f, ColorUtil.lerp(ui.theme.textDim(), ui.theme.text(), this.hover.get()), false);
            this.field.layout(this.x, this.y + 14.0f, this.w);
            if (!this.field.isFocused()) {
                if (this.wasFocused) {
                    ((StringSetting)this.setting).set(this.field.getText());
                } else if (!this.field.getText().equals(((StringSetting)this.setting).get())) {
                    this.field.setText((String)((StringSetting)this.setting).get());
                }
            }
            this.wasFocused = this.field.isFocused();
            this.field.render(ui);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            return this.field.mouseClicked(ui, mx, my, button);
        }
    }

    private static abstract class Row<S extends Setting<?>>
    extends Widget {
        protected final S setting;
        protected final Animation hover = new Animation(0.0f, 16.0f);

        Row(S setting) {
            this.setting = setting;
        }

        @Override
        public boolean isVisible() {
            return ((Setting)this.setting).isVisible();
        }

        @Override
        protected float measure(float width) {
            return 15.0f;
        }

        protected void label(UiContext ui, float maxW) {
            Theme t = ui.theme;
            Fonts.draw(ui.ctx, Fonts.trim(((Setting)this.setting).getLabel(), maxW, 7.2f, false), this.x, this.y + (this.h - 7.2f) / 2.0f + 0.4f, 7.2f, ColorUtil.lerp(t.textDim(), t.text(), this.hover.get()), false);
        }
    }
}

