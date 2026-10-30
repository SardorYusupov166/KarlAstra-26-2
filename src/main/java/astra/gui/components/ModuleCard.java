/*
 * Decompiled with CFR 0.152.
 */
package astra.gui.components;

import astra.animation.Animation;
import astra.gui.PopupHost;
import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.gui.components.SettingRows;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.module.Module;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.Setting;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.List;

public class ModuleCard
extends Widget {
    private static final float PAD = 9.0f;
    private static final float TITLE = 8.6f;
    private static final float DESC = 6.3f;
    private final Module module;
    private final List<Widget> rows = new ArrayList<Widget>();
    private final Animation hover = new Animation(0.0f, 12.0f);
    private final Animation footHover = new Animation(0.0f, 14.0f);
    private final Animation openAnim = new Animation(0.0f, 12.0f);
    private static final float FOOT = 15.0f;
    private float footY;
    private final Animation toggleAnim;
    private final Animation enabledAnim;
    private List<String> descLines = List.of();
    private float headerH;
    private float previewY;
    private float previewH;
    private boolean previewDrag;
    private boolean expanded;
    private static long epoch;
    private long localEpoch = epoch;
    private final PopupHost host;
    private final String sectionTitle;
    private final String sectionDesc;
    private final Icon sectionIcon;
    private final BooleanSetting sectionToggle;
    private boolean moduleSwitch;

    public static void collapseAll() {
        ++epoch;
    }

    public void setExpanded(boolean value) {
        this.syncExpansion();
        this.expanded = value;
    }

    public boolean isExpanded() {
        this.syncExpansion();
        return this.expanded;
    }

    private void syncExpansion() {
        if (this.localEpoch != epoch) {
            this.localEpoch = epoch;
            this.expanded = false;
            this.previewDrag = false;
        }
    }

    public static ModuleCard moduleSection(Module module, PopupHost host, String title, String description, Icon icon, List<Setting<?>> settings) {
        ModuleCard card = new ModuleCard(module, host, title, description, icon, null, settings);
        card.moduleSwitch = true;
        card.toggleAnim.set(module.isEnabled() ? 1.0f : 0.0f);
        card.enabledAnim.set(module.isEnabled() ? 1.0f : 0.0f);
        return card;
    }

    public ModuleCard(Module module, PopupHost host) {
        this(module, host, null, null, null, null, module.getSettings());
    }

    public ModuleCard(Module module, PopupHost host, String title, String description, Icon icon, BooleanSetting toggle, List<Setting<?>> settings) {
        this.module = module;
        this.host = host;
        this.sectionTitle = title;
        this.sectionDesc = description;
        this.sectionIcon = icon;
        this.sectionToggle = toggle;
        for (Setting<?> s : settings) {
            Widget row;
            if (s.isEditorOnly() || s == toggle || (row = SettingRows.create(s, host)) == null) continue;
            this.rows.add(row);
        }
        this.toggleAnim = new Animation(this.isOn() ? 1.0f : 0.0f, 14.0f);
        this.enabledAnim = new Animation(this.isOn() ? 1.0f : 0.0f, 10.0f);
    }

    private boolean isSection() {
        return this.sectionTitle != null;
    }

    private String title() {
        return this.isSection() ? this.sectionTitle : this.module.getName();
    }

    private String description() {
        return this.isSection() ? (this.sectionDesc == null ? "" : this.sectionDesc) : this.module.getDescription();
    }

    private Icon icon() {
        return this.isSection() && this.sectionIcon != null ? this.sectionIcon : this.module.getIcon();
    }

    private boolean toggleable() {
        if (this.moduleSwitch) {
            return this.module.isToggleable();
        }
        return this.isSection() ? this.sectionToggle != null : this.module.isToggleable();
    }

    private boolean isOn() {
        if (!this.isSection() || this.moduleSwitch) {
            return this.module.isEnabled();
        }
        boolean own = this.sectionToggle == null || (Boolean)this.sectionToggle.get() != false;
        return own && (!this.module.isToggleable() || this.module.isEnabled());
    }

    private void flip() {
        if (!this.isSection() || this.moduleSwitch) {
            this.module.toggle();
            return;
        }
        this.sectionToggle.set((Boolean)this.sectionToggle.get() == false);
        if (((Boolean)this.sectionToggle.get()).booleanValue() && this.module.isToggleable() && !this.module.isEnabled()) {
            this.module.setEnabled(true);
        }
    }

    private CardPreview preview() {
        return this.isSection() ? null : this.module.getPreview();
    }

    public Module getModule() {
        return this.module;
    }

    @Override
    protected float measure(float width) {
        this.syncExpansion();
        float textW = width - 18.0f - 22.0f - (this.toggleable() ? 28.0f : 0.0f);
        List<String> list = this.descLines = this.description().isEmpty() ? List.of() : Fonts.wrap(this.description(), textW, 6.3f, false);
        if (this.descLines.size() > 3) {
            this.descLines = this.descLines.subList(0, 3);
        }
        this.headerH = Math.max(18.0f, 11.0f + (float)this.descLines.size() * 7.9f);
        if (!this.expanded) {
            this.previewH = 0.0f;
            return 18.0f + this.headerH + (this.hasSettings() ? 15.0f : 0.0f);
        }
        float cy = this.y + 9.0f + this.headerH + 5.0f;
        CardPreview preview = this.preview();
        if (preview != null) {
            this.previewY = cy;
            this.previewH = preview.height(width - 18.0f);
            cy += this.previewH + 5.0f;
        } else {
            this.previewH = 0.0f;
        }
        for (Widget row : this.rows) {
            if (!row.isVisible()) continue;
            cy += row.layout(this.x + 9.0f, cy, width - 18.0f) + 2.5f;
        }
        return cy - this.y + 9.0f - 4.0f + 15.0f;
    }

    private boolean hasSettings() {
        for (Widget row : this.rows) {
            if (!row.isVisible()) continue;
            return true;
        }
        return this.preview() != null;
    }

    private int settingsCount() {
        int n = 0;
        for (Widget row : this.rows) {
            if (!row.isVisible()) continue;
            ++n;
        }
        return n;
    }

    private void drawFooter(UiContext ui, Theme t) {
        this.footY = this.y + this.h - 9.0f - 15.0f + 4.0f;
        float fx = this.x + 9.0f;
        float fw = this.w - 18.0f;
        boolean hov = ui.hover(this.x, this.footY - 3.0f, this.w, this.y + this.h - this.footY + 3.0f);
        float hv = this.footHover.update(hov ? 1.0f : 0.0f, ui.dt);
        float open = this.openAnim.update(this.expanded ? 1.0f : 0.0f, ui.dt);
        Render2D.rect(ui.ctx, fx, this.footY - 2.5f, fw, 0.7f, ColorUtil.withAlpha(t.textMuted(), 0.16f + 0.14f * hv));
        if (hv > 0.01f) {
            Render2D.roundRect(ui.ctx, fx - 3.0f, this.footY - 1.0f, fw + 6.0f, 11.0f, 4.0f, ColorUtil.withAlpha(t.accent(), 0.1f * hv));
        }
        int col = ColorUtil.lerp(t.textMuted(), t.accent(), Math.max(hv, open * 0.6f));
        float ty = this.footY + 1.6f;
        Icons.draw(ui.ctx, Icon.GEAR, fx, ty - 0.6f, 8.0f, col);
        String label = this.expanded ? "\u0421\u0432\u0435\u0440\u043d\u0443\u0442\u044c" : "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438";
        Fonts.draw(ui.ctx, label, fx + 11.0f, ty, 6.6f, ColorUtil.lerp(t.textDim(), t.text(), hv), true);
        int n = this.settingsCount();
        if (!this.expanded && n > 0) {
            String cnt = String.valueOf(n);
            float lx = fx + 11.0f + Fonts.width(label, 6.6f, true) + 4.0f;
            float cw = Fonts.width(cnt, 5.6f, true) + 6.0f;
            Render2D.roundRect(ui.ctx, lx, ty - 0.6f, cw, 8.0f, 4.0f, ColorUtil.withAlpha(t.accent(), 0.18f + 0.2f * hv));
            Fonts.draw(ui.ctx, cnt, lx + 3.0f, ty + 0.5f, 5.6f, col, true);
        }
        float ax = fx + fw - 4.0f;
        float ay = ty + 3.0f;
        float dir = 1.0f - 2.0f * open;
        float s = 2.6f;
        Render2D.line(ui.ctx, ax - s, ay - s * 0.5f * dir, ax, ay + s * 0.5f * dir, 1.2f, col);
        Render2D.line(ui.ctx, ax, ay + s * 0.5f * dir, ax + s, ay - s * 0.5f * dir, 1.2f, col);
    }

    @Override
    public void render(UiContext ui) {
        CardPreview preview;
        this.syncExpansion();
        Theme t = ui.theme;
        boolean hov = ui.hover(this.x, this.y, this.w, this.h);
        float hv = this.hover.update(hov ? 1.0f : 0.0f, ui.dt);
        float en = this.enabledAnim.update(this.isOn() ? 1.0f : 0.0f, ui.dt);
        UiDraw.card(ui.ctx, t, this.x, this.y, this.w, this.h, 7.0f, hv, this.isOn());
        float ix = this.x + 9.0f;
        float iy = this.y + 9.0f;
        int iconColor = ColorUtil.lerp(t.textDim(), t.accent(), en);
        if (en > 0.01f && UiDraw.glowStrength() > 0.0f) {
            Render2D.shadow(ui.ctx, ix + 3.0f, iy + 3.0f, 10.0f, 10.0f, 5.0f, 7.0f, ColorUtil.withAlpha(t.accent(), 0.16f * en * UiDraw.glowStrength()));
        }
        Icons.drawNeon(ui.ctx, this.icon(), ix, iy, 16.0f, ColorUtil.lerp(iconColor, -1, en * 0.25f), ColorUtil.lerp(t.accent(), t.accent2(), 0.5f), en);
        float tx = ix + 22.0f;
        Fonts.draw(ui.ctx, this.title(), tx, iy + 0.5f, 8.6f, t.text(), true);
        float dy = iy + 11.5f;
        for (String line : this.descLines) {
            Fonts.draw(ui.ctx, line, tx, dy, 6.3f, t.textMuted(), false);
            dy += 7.9f;
        }
        if (this.toggleable()) {
            float tp = this.toggleAnim.update(this.isOn() ? 1.0f : 0.0f, ui.dt);
            boolean th = ui.hover(this.x + this.w - 9.0f - 22.0f, iy, 22.0f, 12.0f);
            UiDraw.toggle(ui.ctx, t, this.x + this.w - 9.0f - 22.0f, iy + 1.0f, tp, th ? 1.0f : 0.0f);
        }
        if (!this.expanded) {
            if (this.hasSettings()) {
                this.drawFooter(ui, t);
            }
            return;
        }
        float prevAlpha = Render2D.alpha;
        if (this.toggleable()) {
            Render2D.alpha *= 0.55f + 0.45f * en;
        }
        if ((preview = this.preview()) != null && this.previewH > 0.0f) {
            preview.render(ui, this.x + 9.0f, this.previewY, this.w - 18.0f, this.previewH);
        }
        for (Widget row : this.rows) {
            if (!row.isVisible()) continue;
            row.render(ui);
        }
        Render2D.alpha = prevAlpha;
        this.drawFooter(ui, t);
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        boolean onFoot;
        this.syncExpansion();
        if (!this.contains(mx, my)) {
            return false;
        }
        boolean bl = onFoot = this.hasSettings() && my >= (double)(this.footY - 3.0f);
        if (onFoot && (button == 0 || button == 1)) {
            this.expanded = !this.expanded;
            this.previewDrag = false;
            this.host.closePopup();
            this.host.setFocus(null);
            return true;
        }
        if (button == 1 && (!this.expanded || my < (double)(this.y + 9.0f + this.headerH))) {
            this.expanded = !this.expanded;
            this.previewDrag = false;
            this.host.closePopup();
            this.host.setFocus(null);
            return true;
        }
        if (this.toggleable() && my < (double)(this.y + 9.0f + this.headerH) && button == 0) {
            this.flip();
            return true;
        }
        if (!this.expanded) {
            return true;
        }
        CardPreview preview = this.preview();
        if (preview != null && my >= (double)this.previewY && my < (double)(this.previewY + this.previewH)) {
            this.previewDrag = preview.mouseClicked(ui, mx, my, button, this.x + 9.0f, this.previewY, this.w - 18.0f, this.previewH);
            if (this.previewDrag) {
                return true;
            }
        }
        for (Widget row : this.rows) {
            if (!row.isVisible() || !row.mouseClicked(ui, mx, my, button)) continue;
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        CardPreview preview;
        if (!this.isExpanded()) {
            return false;
        }
        if (this.previewDrag && (preview = this.preview()) != null) {
            return preview.mouseDragged(ui, mx, my, button, this.x + 9.0f, this.previewY, this.w - 18.0f, this.previewH);
        }
        for (Widget row : this.rows) {
            if (!row.isVisible() || !row.mouseDragged(ui, mx, my, button)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(UiContext ui, double mx, double my, int button) {
        if (this.previewDrag) {
            CardPreview preview = this.preview();
            if (preview != null) {
                preview.mouseReleased();
            }
            this.previewDrag = false;
        }
        for (Widget row : this.rows) {
            row.mouseReleased(ui, mx, my, button);
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (!this.isExpanded()) {
            return false;
        }
        for (Widget row : this.rows) {
            if (!row.keyPressed(key, scanCode, modifiers)) continue;
            return true;
        }
        return false;
    }
}

