/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.PlayerFaceExtractor
 *  net.minecraft.world.entity.player.PlayerSkin
 */
package astra.module.modules.cosmetic;

import astra.gui.UiContext;
import astra.decor.DecorManager;
import astra.settings.StringSetting;
import java.util.ArrayList;
import java.util.List;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.ArtManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Icons;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.ModeSetting;
import astra.theme.Theme;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.world.entity.player.PlayerSkin;

public class ProfileStyle
extends Module {
    private static final String CUSTOM_AVATAR_PREFIX = "Файл: ";
    private final ModeSetting avatar = this.add(new ModeSetting("\u0410\u0432\u0430\u0442\u0430\u0440", "\u0410\u043d\u0438\u043c\u0435 1", "\u0410\u043d\u0438\u043c\u0435 1", "\u0410\u043d\u0438\u043c\u0435 2", "\u0410\u043d\u0438\u043c\u0435 3", "\u0410\u043d\u0438\u043c\u0435 4", "\u041b\u0438\u0446\u043e \u0441\u043a\u0438\u043d\u0430", "\u0418\u043d\u0438\u0446\u0438\u0430\u043b").dynamic(ProfileStyle::avatarOptions));
    private final ModeSetting frame = this.add(new ModeSetting("\u0420\u0430\u043c\u043a\u0430 \u043f\u0440\u043e\u0444\u0438\u043b\u044f", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u041d\u0435\u043e\u043d", "\u0417\u0432\u0451\u0437\u0434\u044b", "\u0411\u0435\u0437 \u0440\u0430\u043c\u043a\u0438"));
    private final ModeSetting badge = this.add(new ModeSetting("\u0418\u043a\u043e\u043d\u043a\u0430", "\u0417\u0432\u0435\u0437\u0434\u0430", "\u0417\u0432\u0435\u0437\u0434\u0430", "\u0421\u0435\u0440\u0434\u0446\u0435", "\u041a\u043e\u0440\u043e\u043d\u0430", "\u041c\u0435\u0434\u0430\u043b\u044c"));
    private final ModeSetting tag = this.add(new ModeSetting("\u0422\u0435\u0433", "Astra", "Astra", "Sakura", "VIP", "\u041d\u0435\u0442", "Свой"));
    private final StringSetting customTag = this.add(new StringSetting("Свой тег", "", "Введите тег", 32).visibleWhen(() -> tag.is("Свой")));

    public static List<String> avatarOptions() {
        List<String> options = new ArrayList<>(List.of("Аниме 1", "Аниме 2", "Аниме 3", "Аниме 4", "Лицо скина", "Инициал"));
        for (java.nio.file.Path file : ArtManager.customFiles()) options.add(CUSTOM_AVATAR_PREFIX + file.getFileName());
        return options;
    }

    public ProfileStyle() {
        super("profile_style", "\u0420\u0430\u043c\u043a\u0430 \u0438 \u0430\u0432\u0430\u0442\u0430\u0440", "\u0410\u0432\u0430\u0442\u0430\u0440, \u0440\u0430\u043c\u043a\u0430 \u043f\u0440\u043e\u0444\u0438\u043b\u044f, \u0438\u043a\u043e\u043d\u043a\u0430 \u0438 \u0442\u0435\u0433 \u0432 \u043c\u0435\u043d\u044e Astra.", Category.COSMETICS, Icon.FRAME);
        this.alwaysOn();
    }

    public static ProfileStyle get() {
        return ModuleManager.get(ProfileStyle.class);
    }

    public void drawAvatar(GuiGraphicsExtractor ctx, Theme t, float x, float y, float size) {
        float r = size / 2.0f;
        String f = (String)this.frame.get();
        if (!f.equals("\u0411\u0435\u0437 \u0440\u0430\u043c\u043a\u0438")) {
            Render2D.glow(ctx, x, y, size, size, r, size * 0.2f, ColorUtil.withAlpha(f.equals("\u041d\u0435\u043e\u043d") ? t.accent3() : t.accent2(), 0.4f));
        }
        Render2D.circle(ctx, x + r, y + r, r, Paint.linear(x, y, x + size, y + size, t.accent(), t.accent2()));
        float inner = size * 0.7f;
        float ix = x + (size - inner) / 2.0f;
        float iy = y + (size - inner) / 2.0f;
        if (this.avatar.get().startsWith(CUSTOM_AVATAR_PREFIX)) {
            DecorManager.Tex texture = DecorManager.texture(this.avatar.get().substring(CUSTOM_AVATAR_PREFIX.length()));
            if (texture != null) {
                float radius = size * 0.42f;

                // Equal pixel radii keep non-square uploads from stretching.
                Render2D.textureRoundRect(ctx, texture.id(), texture.aspect(), x + r - radius, y + r - radius, radius * 2, radius * 2, radius, -1);
            } else {
                ArtManager.drawAvatar(ctx, 0, x + r, y + r, size * 0.42f);
            }
        } else if (((String)this.avatar.get()).startsWith("\u0410\u043d\u0438\u043c\u0435")) {
            int index = ((String)this.avatar.get()).length() > 6 ? ((String)this.avatar.get()).charAt(6) - 49 : 0;
            ArtManager.drawAvatar(ctx, index, x + r, y + r, size * 0.42f);
        } else if (this.avatar.is("\u041b\u0438\u0446\u043e \u0441\u043a\u0438\u043d\u0430") && ProfileStyle.mc.player != null) {
            Render2D.roundRect(ctx, ix - 1.0f, iy - 1.0f, inner + 2.0f, inner + 2.0f, 3.0f, t.bg());
            PlayerFaceExtractor.extractRenderState((GuiGraphicsExtractor)ctx, (PlayerSkin)ProfileStyle.mc.player.getSkin(), (int)Math.round(ix), (int)Math.round(iy), (int)Math.round(inner));
        } else {
            Render2D.circle(ctx, x + r, y + r, size * 0.4f, t.bg2());
            String name = mc.getUser().getName();
            Fonts.drawCentered(ctx, name.isEmpty() ? "A" : name.substring(0, 1).toUpperCase(), x + r, y + r - size * 0.17f, size * 0.36f, t.text(), true);
        }
        switch (f) {
            case "\u0421\u0430\u043a\u0443\u0440\u0430": {
                int i;
                for (i = 0; i < 6; ++i) {
                    double a = Math.toRadians(i * 60 - 90);
                    Render2D.petal(ctx, x + r + (float)Math.cos(a) * r, y + r + (float)Math.sin(a) * r, size * 0.09f, i * 60, Paint.solid(-22823));
                }
                break;
            }
            case "\u041d\u0435\u043e\u043d": {
                Render2D.ring(ctx, x + r, y + r, r - 0.5f, 1.4f, t.accent3());
                break;
            }
            case "\u0417\u0432\u0451\u0437\u0434\u044b": {
                int i;
                for (i = 0; i < 5; ++i) {
                    double a = Math.toRadians(i * 72 - 90);
                    Render2D.sparkle(ctx, x + r + (float)Math.cos(a) * r, y + r + (float)Math.sin(a) * r, size * 0.08f, 0.0f, -3152);
                }
                break;
            }
        }
        Icon icon = switch ((String)this.badge.get()) {
            case "\u0421\u0435\u0440\u0434\u0446\u0435" -> Icon.HEART;
            case "\u041a\u043e\u0440\u043e\u043d\u0430" -> Icon.CROWN;
            case "\u041c\u0435\u0434\u0430\u043b\u044c" -> Icon.MEDAL;
            default -> Icon.STAR;
        };
        float bs = size * 0.3f;
        Render2D.circle(ctx, x + size - bs * 0.5f, y + size - bs * 0.5f, bs * 0.62f, t.accent());
        Icons.draw(ctx, icon, x + size - bs * 0.95f, y + size - bs * 0.95f, bs * 0.9f, -1);
    }

    public String tag() {
        return this.tag.is("Свой") ? this.customTag.get() : (this.tag.is("\u041d\u0435\u0442") ? "" : this.tag.get());
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 44.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                ProfileStyle.this.drawAvatar(ui.ctx, ui.theme, x + 4.0f, y + 2.0f, 40.0f);
                Fonts.draw(ui.ctx, mc.getUser().getName(), x + 52.0f, y + 9.0f, 8.5f, ui.theme.text(), true);
                String tg = ProfileStyle.this.tag();
                if (!tg.isEmpty()) {
                    float tw = Fonts.width(tg, 6.5f, true) + 10.0f;
                    Render2D.roundRect(ui.ctx, x + 52.0f, y + 22.0f, tw, 11.0f, 5.5f, Paint.horizontal(x + 52.0f, x + 52.0f + tw, ui.theme.accent(), ui.theme.accent2()));
                    Fonts.draw(ui.ctx, tg, x + 57.0f, y + 24.5f, 6.5f, -1, true);
                }
            }
        };
    }
}

