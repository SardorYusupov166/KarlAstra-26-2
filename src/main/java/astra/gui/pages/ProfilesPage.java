/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Util
 */
package astra.gui.pages;

import astra.animation.Animation;
import astra.config.ConfigManager;
import astra.gui.UiContext;
import astra.gui.components.TextField;
import astra.gui.components.UiDraw;
import astra.gui.components.Widget;
import astra.gui.pages.Page;
import astra.gui.screens.ClickGuiScreen;
import astra.module.modules.cosmetic.ProfileStyle;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

public class ProfilesPage
extends Page {
    private static final float ROW_H = 30.0f;
    private final Header headerCard = new Header(this);
    private final TextField createField;
    private final TextField renameField;
    private String renaming;
    private String armedDelete;
    private long armedAt;
    private final Map<String, Animation> rowHover = new HashMap<String, Animation>();
    private final Animation[] btnHover = new Animation[]{new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f)};
    private List<String> profiles = List.of();
    private float listY;
    private float createY;
    private static final String[] ACTIONS = new String[]{"\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c", "\u041f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c", "\u041a\u043e\u043f\u0438\u044f", "\u0423\u0434\u0430\u043b\u0438\u0442\u044c"};
    private static final Icon[] ACTION_ICONS = new Icon[]{Icon.DOWNLOAD, Icon.EDIT, Icon.COPY, Icon.TRASH};

    public ProfilesPage(ClickGuiScreen screen) {
        super(screen);
        this.createField = new TextField(screen, "\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u043d\u043e\u0432\u043e\u0433\u043e \u043f\u0440\u043e\u0444\u0438\u043b\u044f\u2026", 32).onSubmit(this::create);
        this.renameField = new TextField(screen, "\u041d\u043e\u0432\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u2026", 32).onSubmit(this::finishRename).onCancel(() -> {
            this.renaming = null;
        });
    }

    @Override
    public void onShow() {
        this.profiles = ConfigManager.listProfiles();
    }

    private void create(String name) {
        if (name.isEmpty()) {
            name = ConfigManager.uniqueName("\u041f\u0440\u043e\u0444\u0438\u043b\u044c");
        }
        if (ConfigManager.createProfile(name)) {
            this.screen.toast("\u0421\u043e\u0437\u0434\u0430\u043d \u043f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name + "\u00bb");
            this.createField.setText("");
            this.profiles = ConfigManager.listProfiles();
        } else {
            this.screen.toast(ConfigManager.isValidName(name) ? "\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u0441 \u0442\u0430\u043a\u0438\u043c \u0438\u043c\u0435\u043d\u0435\u043c \u0443\u0436\u0435 \u0435\u0441\u0442\u044c" : "\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u0438\u043c\u044f \u043f\u0440\u043e\u0444\u0438\u043b\u044f");
        }
    }

    private void createDefault() {
        String name = this.createField.getText().trim();
        if (name.isEmpty()) {
            name = ConfigManager.uniqueName("\u041d\u043e\u0432\u044b\u0439");
        }
        if (ConfigManager.createDefaultProfile(name)) {
            this.screen.toast("\u0421\u043e\u0437\u0434\u0430\u043d \u0441\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u044b\u0439 \u043f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name + "\u00bb");
            this.createField.setText("");
            this.profiles = ConfigManager.listProfiles();
        } else {
            this.screen.toast("\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e\u0435 \u0438\u043b\u0438 \u0437\u0430\u043d\u044f\u0442\u043e\u0435 \u0438\u043c\u044f");
        }
    }

    private void finishRename(String name) {
        String old = this.renaming;
        this.renaming = null;
        if (old == null || name.isEmpty() || name.equals(old)) {
            return;
        }
        if (ConfigManager.renameProfile(old, name)) {
            this.screen.toast("\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u043f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u043d \u0432 \u00ab" + name + "\u00bb");
            this.profiles = ConfigManager.listProfiles();
        } else {
            this.screen.toast("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c: \u0438\u043c\u044f \u0437\u0430\u043d\u044f\u0442\u043e \u0438\u043b\u0438 \u043d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u043e");
        }
    }

    @Override
    protected float layout(float top) {
        if (this.profiles.isEmpty()) {
            this.profiles = ConfigManager.listProfiles();
        }
        float cy = top;
        cy += this.headerCard.layout(this.x, cy, this.w) + 10.0f;
        this.createY = cy;
        this.listY = cy += 62.0f;
        return (cy += 28.0f + (float)this.profiles.size() * 34.0f) - top;
    }

    private float[] actionRect(int row, int i) {
        float ry = this.listY + 26.0f + (float)row * 34.0f;
        float bw = 22.0f;
        float gap = 5.0f;
        float bx = this.x + this.w - 10.0f - (float)(ACTIONS.length - i) * (bw + gap) + gap;
        return new float[]{bx, ry + (30.0f - bw) / 2.0f, bw, bw};
    }

    @Override
    protected void renderContent(UiContext ui) {
        Theme t = ui.theme;
        this.headerCard.render(ui);
        UiDraw.card(ui.ctx, t, this.x, this.createY, this.w, 52.0f, 9.0f, 0.0f, false);
        Fonts.draw(ui.ctx, "\u041d\u043e\u0432\u044b\u0439 \u043f\u0440\u043e\u0444\u0438\u043b\u044c", this.x + 12.0f, this.createY + 9.0f, 8.2f, t.text(), true);
        float fw = this.w - 24.0f - 230.0f;
        this.createField.layout(this.x + 12.0f, this.createY + 24.0f, fw);
        this.createField.render(ui);
        float b1x = this.x + 18.0f + fw;
        float b2x = b1x + 112.0f;
        UiDraw.button(ui.ctx, t, b1x, this.createY + 24.0f, 106.0f, 18.0f, "\u0418\u0437 \u0442\u0435\u043a\u0443\u0449\u0435\u0433\u043e", Icon.PLUS, this.btnHover[0].update(ui.hover(b1x, this.createY + 24.0f, 106.0f, 18.0f) ? 1.0f : 0.0f, ui.dt), true);
        UiDraw.button(ui.ctx, t, b2x, this.createY + 24.0f, 106.0f, 18.0f, "\u0421\u0442\u0430\u043d\u0434\u0430\u0440\u0442\u043d\u044b\u0439", Icon.RESET, this.btnHover[1].update(ui.hover(b2x, this.createY + 24.0f, 106.0f, 18.0f) ? 1.0f : 0.0f, ui.dt), false);
        Fonts.draw(ui.ctx, "\u041f\u0440\u043e\u0444\u0438\u043b\u0438 (" + this.profiles.size() + ")", this.x + 2.0f, this.listY + 6.0f, 8.2f, t.text(), true);
        Fonts.drawRight(ui.ctx, "\u0418\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f \u0441\u043e\u0445\u0440\u0430\u043d\u044f\u044e\u0442\u0441\u044f \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438", this.x + this.w - 2.0f, this.listY + 7.0f, 6.2f, t.textMuted(), false);
        String active = ConfigManager.activeProfile();
        for (int i = 0; i < this.profiles.size(); ++i) {
            String name = this.profiles.get(i);
            float ry = this.listY + 26.0f + (float)i * 34.0f;
            boolean isActive = name.equals(active);
            float hv = this.rowHover.computeIfAbsent(name, k -> new Animation(0.0f, 14.0f)).update(ui.hover(this.x, ry, this.w, 30.0f) ? 1.0f : 0.0f, ui.dt);
            Render2D.roundRect(ui.ctx, this.x, ry, this.w, 30.0f, 7.0f, isActive ? Paint.horizontal(this.x, this.x + this.w, ColorUtil.withAlpha(t.accent(), 0.35f), ColorUtil.withAlpha(t.card(), 0.8f)) : Paint.solid(ColorUtil.lerp(t.card(), t.cardHover(), hv)));
            Render2D.outline(ui.ctx, this.x, ry, this.w, 30.0f, 7.0f, 0.8f, ColorUtil.withAlpha(t.accent(), isActive ? 0.75f : 0.18f + 0.3f * hv));
            Icons.draw(ui.ctx, Icon.USER, this.x + 10.0f, ry + 8.0f, 14.0f, isActive ? t.accent() : t.textDim());
            if (name.equals(this.renaming)) {
                this.renameField.layout(this.x + 30.0f, ry + 6.0f, Math.min(220.0f, this.w * 0.4f));
                this.renameField.render(ui);
            } else {
                Fonts.draw(ui.ctx, name, this.x + 30.0f, ry + 7.0f, 8.0f, t.text(), true);
                Fonts.draw(ui.ctx, isActive ? "\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0439 \u043f\u0440\u043e\u0444\u0438\u043b\u044c" : "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u00ab\u0417\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c\u00bb, \u0447\u0442\u043e\u0431\u044b \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c", this.x + 30.0f, ry + 18.0f, 5.8f, isActive ? t.accent() : t.textMuted(), false);
            }
            for (int a = 0; a < ACTIONS.length; ++a) {
                float[] r = this.actionRect(i, a);
                boolean hov = ui.hover(r[0], r[1], r[2], r[3]);
                boolean armed = a == 3 && name.equals(this.armedDelete) && System.currentTimeMillis() - this.armedAt < 3000L;
                UiDraw.iconButton(ui.ctx, t, r[0], r[1], r[2], ACTION_ICONS[a], hov || armed ? 1.0f : 0.0f);
                if (!armed) continue;
                Render2D.outline(ui.ctx, r[0], r[1], r[2], r[3], 5.0f, 1.2f, -770210);
            }
        }
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        if (!this.inView(mx, my)) {
            return false;
        }
        if (this.headerCard.contains(mx, my)) {
            return this.headerCard.mouseClicked(ui, mx, my, button);
        }
        if (this.createField.mouseClicked(ui, mx, my, button)) {
            return true;
        }
        float fw = this.w - 24.0f - 230.0f;
        float b1x = this.x + 18.0f + fw;
        float b2x = b1x + 112.0f;
        if (my >= (double)(this.createY + 24.0f) && my < (double)(this.createY + 42.0f)) {
            if (mx >= (double)b1x && mx < (double)(b1x + 106.0f)) {
                this.create(this.createField.getText().trim());
                return true;
            }
            if (mx >= (double)b2x && mx < (double)(b2x + 106.0f)) {
                this.createDefault();
                return true;
            }
        }
        if (this.renaming != null && this.renameField.mouseClicked(ui, mx, my, button)) {
            return true;
        }
        for (int i = 0; i < this.profiles.size(); ++i) {
            String name = this.profiles.get(i);
            for (int a = 0; a < ACTIONS.length; ++a) {
                float[] r = this.actionRect(i, a);
                if (!(mx >= (double)r[0]) || !(mx < (double)(r[0] + r[2])) || !(my >= (double)r[1]) || !(my < (double)(r[1] + r[3]))) continue;
                this.action(a, name);
                return true;
            }
        }
        return false;
    }

    private void action(int a, String name) {
        switch (a) {
            case 0: {
                ConfigManager.loadProfile(name);
                this.screen.toast("\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name + "\u00bb \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d");
                break;
            }
            case 1: {
                this.renaming = name;
                this.renameField.setText(name);
                this.renameField.focus();
                break;
            }
            case 2: {
                String copy = ConfigManager.uniqueName(name + " \u043a\u043e\u043f\u0438\u044f");
                if (!ConfigManager.duplicateProfile(name, copy)) break;
                this.screen.toast("\u0421\u043e\u0437\u0434\u0430\u043d\u0430 \u043a\u043e\u043f\u0438\u044f \u00ab" + copy + "\u00bb");
                break;
            }
            case 3: {
                if (name.equals(this.armedDelete) && System.currentTimeMillis() - this.armedAt < 3000L) {
                    this.armedDelete = null;
                    if (ConfigManager.deleteProfile(name)) {
                        this.screen.toast("\u041f\u0440\u043e\u0444\u0438\u043b\u044c \u00ab" + name + "\u00bb \u0443\u0434\u0430\u043b\u0451\u043d");
                        break;
                    }
                    this.screen.toast("\u041d\u0435\u043b\u044c\u0437\u044f \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0435\u0434\u0438\u043d\u0441\u0442\u0432\u0435\u043d\u043d\u044b\u0439 \u043f\u0440\u043e\u0444\u0438\u043b\u044c");
                    break;
                }
                this.armedDelete = name;
                this.armedAt = System.currentTimeMillis();
                this.screen.toast("\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0435\u0449\u0451 \u0440\u0430\u0437, \u0447\u0442\u043e\u0431\u044b \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u00ab" + name + "\u00bb");
                break;
            }
        }
        this.profiles = ConfigManager.listProfiles();
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        return false;
    }

    private final class Header
    extends Widget {
        private final Animation folderHover = new Animation(0.0f, 16.0f);

        private Header(ProfilesPage profilesPage) {
        }

        @Override
        protected float measure(float width) {
            return 74.0f;
        }

        @Override
        public void render(UiContext ui) {
            Theme t = ui.theme;
            UiDraw.card(ui.ctx, t, this.x, this.y, this.w, this.h, 9.0f, 0.0f, true);
            Render2D.roundRect(ui.ctx, this.x, this.y, this.w, this.h, 9.0f, Paint.horizontal(this.x, this.x + this.w, ColorUtil.withAlpha(t.accent2(), 0.14f), 0));
            ProfileStyle style = ProfileStyle.get();
            if (style != null) {
                style.drawAvatar(ui.ctx, t, this.x + 14.0f, this.y + 12.0f, 50.0f);
            }
            String user = Minecraft.getInstance().getUser().getName();
            Fonts.draw(ui.ctx, user, this.x + 76.0f, this.y + 16.0f, 12.0f, t.text(), true);
            String tag = style != null ? style.tag() : "";
            float tx = this.x + 76.0f + Fonts.width(user, 12.0f, true) + 8.0f;
            if (!tag.isEmpty()) {
                float tw = Fonts.width(tag, 6.5f, true) + 12.0f;
                Render2D.roundRect(ui.ctx, tx, this.y + 17.0f, tw, 12.0f, 6.0f, Paint.horizontal(tx, tx + tw, t.accent(), t.accent2()));
                Fonts.draw(ui.ctx, tag, tx + 6.0f, this.y + 19.5f, 6.5f, -1, true);
            }
            Fonts.draw(ui.ctx, "\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0439 \u043f\u0440\u043e\u0444\u0438\u043b\u044c: " + ConfigManager.activeProfile(), this.x + 76.0f, this.y + 36.0f, 7.0f, t.textDim(), false);
            Fonts.draw(ui.ctx, "\u0410\u0432\u0430\u0442\u0430\u0440 \u0438 \u0440\u0430\u043c\u043a\u0443 \u043c\u043e\u0436\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0432 \u00ab\u041a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0435\u00bb.", this.x + 76.0f, this.y + 48.0f, 6.2f, t.textMuted(), false);
            float bx = this.x + this.w - 136.0f;
            float by = this.y + this.h / 2.0f - 10.0f;
            UiDraw.button(ui.ctx, t, bx, by, 122.0f, 20.0f, "\u041f\u0430\u043f\u043a\u0430 \u043f\u0440\u043e\u0444\u0438\u043b\u0435\u0439", Icon.FOLDER, this.folderHover.update(ui.hover(bx, by, 122.0f, 20.0f) ? 1.0f : 0.0f, ui.dt), false);
        }

        @Override
        public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
            float bx = this.x + this.w - 136.0f;
            float by = this.y + this.h / 2.0f - 10.0f;
            if (mx >= (double)bx && mx < (double)(bx + 122.0f) && my >= (double)by && my < (double)(by + 20.0f)) {
                Util.getPlatform().openFile(ConfigManager.profilesDir().toFile());
            }
            return true;
        }
    }
}

