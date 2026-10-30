/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
 *  net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
 *  net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
 *  net.minecraft.client.GuiMessage$Line
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.ChatComponent
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.ChatVisiblity
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.hud;

import astra.mixin.ChatHudAccessor;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.hud.ChatLayoutHud;
import astra.module.modules.hud.CompanionHud;
import astra.module.modules.hud.TabLayoutHud;
import astra.render.ColorUtil;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import astra.theme.ThemeManager;
import astra.utils.ChatPatches;
import astra.utils.StreamerProtect;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.joml.Matrix3x2fStack;

public class AnimeChatModule
extends Module {
    private final BooleanSetting chat = this.add(new BooleanSetting("\u0421\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u0439 \u0447\u0430\u0442", true));
    private final BooleanSetting tab = this.add((BooleanSetting)new BooleanSetting("\u0421\u0432\u043e\u0439 \u0442\u0430\u0431-\u043b\u0438\u0441\u0442", true).desc("\u0421\u043f\u0438\u0441\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 (Tab) \u0432 \u0441\u0442\u0438\u043b\u0435 Astra"));
    private final BooleanSetting pingNumbers = this.add((BooleanSetting)new BooleanSetting("\u041f\u0438\u043d\u0433 \u0446\u0438\u0444\u0440\u0430\u043c\u0438", true).visibleWhen(() -> (Boolean)this.tab.get()));
    private final BooleanSetting timestamps = this.add(new BooleanSetting("\u0412\u0440\u0435\u043c\u044f \u0443 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0439", true));
    private final BooleanSetting stack = this.add((BooleanSetting)new BooleanSetting("\u0421\u043a\u043b\u0435\u0438\u0432\u0430\u0442\u044c \u043f\u043e\u0432\u0442\u043e\u0440\u044b", true).desc("\u041e\u0434\u0438\u043d\u0430\u043a\u043e\u0432\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u043f\u043e\u0434\u0440\u044f\u0434 \u2014 \u043e\u0434\u043d\u043e\u0439 \u0441\u0442\u0440\u043e\u043a\u043e\u0439 \u0441\u043e \u0441\u0447\u0451\u0442\u0447\u0438\u043a\u043e\u043c (x3)"));
    private final BooleanSetting keep = this.add((BooleanSetting)new BooleanSetting("\u0425\u0440\u0430\u043d\u0438\u0442\u044c \u0447\u0430\u0442", true).desc("\u0427\u0430\u0442 \u043d\u0435 \u0441\u0442\u0438\u0440\u0430\u0435\u0442\u0441\u044f \u043f\u0440\u0438 \u0441\u043c\u0435\u043d\u0435 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0438 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u0430\u0432\u043b\u0438\u0432\u0430\u0435\u0442\u0441\u044f \u043f\u043e\u0441\u043b\u0435 \u043f\u0435\u0440\u0435\u0437\u0430\u043f\u0443\u0441\u043a\u0430 \u0438\u0433\u0440\u044b"));
    private final BooleanSetting divider = this.add((BooleanSetting)new BooleanSetting("\u0420\u0430\u0437\u0434\u0435\u043b\u0438\u0442\u0435\u043b\u044c \u0441\u0435\u0441\u0441\u0438\u0439", true).desc("\u0421\u0442\u0440\u043e\u043a\u0430 \u0441 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0438 \u0432\u0440\u0435\u043c\u0435\u043d\u0435\u043c \u043f\u0440\u0438 \u043a\u0430\u0436\u0434\u043e\u043c \u0432\u0445\u043e\u0434\u0435"));
    private final BooleanSetting longHistory = this.add(new BooleanSetting("\u0414\u043b\u0438\u043d\u043d\u0430\u044f \u0438\u0441\u0442\u043e\u0440\u0438\u044f (1000)", true));
    private final BooleanSetting copy = this.add((BooleanSetting)new BooleanSetting("\u041f\u041a\u041c \u2014 \u043a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u0442\u044c", true).desc("\u041f\u0440\u0430\u0432\u044b\u0439 \u043a\u043b\u0438\u043a \u043f\u043e \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044e \u0432 \u043e\u0442\u043a\u0440\u044b\u0442\u043e\u043c \u0447\u0430\u0442\u0435 \u043a\u043e\u043f\u0438\u0440\u0443\u0435\u0442 \u0435\u0433\u043e \u0442\u0435\u043a\u0441\u0442"));
    private final BooleanSetting hidePasswords = this.add((BooleanSetting)new BooleanSetting("\u0421\u043a\u0440\u044b\u0432\u0430\u0442\u044c \u043f\u0430\u0440\u043e\u043b\u0438 (\u0441\u0442\u0440\u0438\u043c)", true).desc("/login, /l, /register, /reg \u0438 \u0442.\u043f.: \u043f\u0430\u0440\u043e\u043b\u044c \u0432 \u0447\u0430\u0442\u0435 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442\u0441\u044f \u0442\u043e\u0447\u043a\u0430\u043c\u0438"));
    private final BooleanSetting mentions = this.add((BooleanSetting)new BooleanSetting("\u0423\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f", true).desc("\u0417\u0432\u0443\u043a \u0438 \u0440\u0435\u0430\u043a\u0446\u0438\u044f \u043a\u043e\u043c\u043f\u0430\u043d\u044c\u043e\u043d\u0430, \u043a\u043e\u0433\u0434\u0430 \u0432\u0430\u0448 \u043d\u0438\u043a \u043f\u0438\u0448\u0443\u0442 \u0432 \u0447\u0430\u0442\u0435"));
    private final NumberSetting mentionVolume = this.add((NumberSetting)new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f", 70.0, 0.0, 100.0, 5.0).unit("%").visibleWhen(() -> (Boolean)this.mentions.get()));
    private static long joinedAt;
    private static long lastMention;

    public static float mentionVolume() {
        AnimeChatModule m = AnimeChatModule.get();
        return m == null ? 0.7f : m.mentionVolume.getFloat() / 100.0f;
    }

    public AnimeChatModule() {
        super("anime_chat", "\u0410\u043d\u0438\u043c\u0435-\u0447\u0430\u0442", "\u0421\u0442\u0435\u043a\u043b\u044f\u043d\u043d\u044b\u0439 \u0447\u0430\u0442 \u0438 \u0442\u0430\u0431-\u043b\u0438\u0441\u0442 \u0432 \u0446\u0432\u0435\u0442\u0430\u0445 \u0442\u0435\u043c\u044b, \u043f\u0438\u043d\u0433 \u0446\u0438\u0444\u0440\u0430\u043c\u0438, \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f \u0441\u043e \u0437\u0432\u0443\u043a\u043e\u043c.", Category.HUD, Icon.TAG);
        this.setDefaultEnabled(true);
    }

    public static AnimeChatModule get() {
        return ModuleManager.get(AnimeChatModule.class);
    }

    public static boolean chatStyled() {
        AnimeChatModule m = AnimeChatModule.get();
        return m != null && m.isEnabled() && (Boolean)m.chat.get() != false;
    }

    public static boolean tabStyled() {
        AnimeChatModule m = AnimeChatModule.get();
        return m != null && m.isEnabled() && (Boolean)m.tab.get() != false;
    }

    public static boolean hidePasswords() {
        AnimeChatModule m = AnimeChatModule.get();
        return m == null || (Boolean)m.hidePasswords.get() != false;
    }

    public boolean timestamps() {
        return (Boolean)this.timestamps.get();
    }

    public boolean stackRepeats() {
        return (Boolean)this.stack.get();
    }

    public boolean keepChat() {
        return (Boolean)this.keep.get();
    }

    public boolean sessionDivider() {
        return (Boolean)this.divider.get();
    }

    public boolean longHistory() {
        return (Boolean)this.longHistory.get();
    }

    public boolean rightClickCopy() {
        return (Boolean)this.copy.get();
    }

    public static boolean pingNumbers() {
        AnimeChatModule m = AnimeChatModule.get();
        return AnimeChatModule.tabStyled() && (Boolean)m.pingNumbers.get() != false;
    }

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((h, s, c) -> {
            joinedAt = System.currentTimeMillis();
            c.execute(ChatPatches::onJoin);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> c.execute(ChatPatches::save));
        ClientLifecycleEvents.CLIENT_STOPPING.register(c -> ChatPatches.save());
        ClientReceiveMessageEvents.CHAT.register((message, signed, sender, params, time) -> {
            Minecraft mc = Minecraft.getInstance();
            if (sender != null && mc.player != null && sender.id().equals(mc.player.getUUID())) {
                return;
            }
            AnimeChatModule.mention(message);
        });
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> StreamerProtect.maskMessage(message));
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) {
                AnimeChatModule.mention(message);
            }
        });
    }

    private static void mention(Component message) {
        AnimeChatModule m = AnimeChatModule.get();
        Minecraft mc = Minecraft.getInstance();
        if (m == null || !m.isEnabled() || !((Boolean)m.mentions.get()).booleanValue() || mc.player == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - joinedAt < 6000L || now - lastMention < 3000L) {
            return;
        }
        String name = mc.player.getGameProfile().name().toLowerCase(Locale.ROOT);
        if (name.length() < 3 || !message.getString().toLowerCase(Locale.ROOT).contains(name)) {
            return;
        }
        lastMention = now;
        mc.execute(() -> {
            CompanionHud.playMentionSound();
            CompanionHud.react(CompanionHud.Mood.EXCITED, "\u0422\u0435\u0431\u044f \u0437\u043e\u0432\u0443\u0442 \u0432 \u0447\u0430\u0442\u0435!", "\u041a\u0442\u043e-\u0442\u043e \u0443\u043f\u043e\u043c\u044f\u043d\u0443\u043b \u0442\u0435\u0431\u044f \u2661", "\u042d\u0439, \u0442\u0435\u0431\u0435 \u043f\u0438\u0448\u0443\u0442!");
        });
    }

    public static String chatStyle() {
        ChatLayoutHud layout = ChatLayoutHud.get();
        return layout == null ? "\u041d\u0435\u043e\u043d" : layout.style();
    }

    private static int chatBackground(Theme t) {
        ChatLayoutHud layout = ChatLayoutHud.get();
        int bg = layout == null ? 0 : layout.background(t);
        return bg == 0 ? -16777216 : bg;
    }

    public static void chatPanel(GuiGraphics ctx, int tick, boolean focused) {
        if (!AnimeChatModule.chatStyled() || !AnimeChatModule.chatStyle().equals("\u0421\u0442\u0435\u043a\u043b\u043e")) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.chatVisibility().get() == ChatVisiblity.HIDDEN) {
            return;
        }
        ChatComponent hud = mc.gui.getChat();
        ChatHudAccessor access = (ChatHudAccessor)hud;
        List<GuiMessage.Line> lines = access.astra$visibleMessages();
        int count = Math.min(hud.getLinesPerPage(), lines.size() - access.astra$scrolledLines());
        int first = -1;
        int last = -1;
        float fade = 0.0f;
        for (int i = 0; i < count; ++i) {
            float f;
            int age = tick - lines.get(i + access.astra$scrolledLines()).addedTime();
            float f2 = f = focused ? 1.0f : (float)Math.pow(Math.max(0.0, Math.min(1.0, (1.0 - (double)age / 200.0) * 10.0)), 2.0);
            if (f <= 1.0E-5f) continue;
            if (first < 0) {
                first = i;
            }
            last = i;
            fade = Math.max(fade, f);
        }
        float alpha = ((Double)mc.options.textBackgroundOpacity().get()).floatValue() * fade;
        if (first < 0 || alpha <= 0.0f) {
            return;
        }
        float scale = (float)((ChatHudAccessor)hud).astra$chatScale();
        if (scale <= 0.0f) {
            return;
        }
        float bottom = (float)Math.floor((float)(ctx.guiHeight() - 40) / scale);
        float top = bottom - (float)((last + 1) * access.astra$lineHeight());
        bottom -= (float)(first * access.astra$lineHeight());
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.scale(scale, scale);
        m.translate(4.0f, 0.0f);
        AnimeChatModule.drawGlass(ctx, -4.0f, top, (float)Math.ceil((float)((ChatHudAccessor)hud).astra$width() / scale) + 12.0f, bottom - top, alpha);
        m.popMatrix();
    }

    public static void drawGlass(GuiGraphics ctx, float x, float y, float w, float h, float alpha) {
        Theme t = ThemeManager.get();
        int bg = AnimeChatModule.chatBackground(t);
        Render2D.roundRect(ctx, x, y, w, h, 4.0f, Paint.vertical(y, y + h, ColorUtil.withAlpha(ColorUtil.lerp(bg, t.accent(), 0.12f), alpha), ColorUtil.withAlpha(bg, alpha * 0.85f)));
        Render2D.outline(ctx, x, y, w, h, 4.0f, 0.65f, Paint.horizontal(x, x + w, ColorUtil.withAlpha(t.accent(), alpha), ColorUtil.withAlpha(t.accent2(), alpha * 0.4f)));
    }

    public static void chatFill(GuiGraphics ctx, int x1, int y1, int x2, int y2, int color) {
        float alpha = (float)(color >>> 24) / 255.0f;
        if (alpha <= 0.0f) {
            return;
        }
        String style = AnimeChatModule.chatStyle();
        Theme t = ThemeManager.get();
        int bg = AnimeChatModule.chatBackground(t);
        switch (style) {
            case "\u041a\u043b\u0430\u0441\u0441\u0438\u043a\u0430": {
                ctx.fill(x1, y1, x2, y2, color);
                break;
            }
            case "\u041c\u0438\u043d\u0438\u043c\u0430\u043b": 
            case "\u0421\u0442\u0435\u043a\u043b\u043e": {
                break;
            }
            case "\u041f\u0443\u0437\u044b\u0440\u0438": {
                Render2D.roundRect(ctx, (float)(x1 + 1), (float)y1 + 0.5f, (float)(x2 - x1 - 2), (float)(y2 - y1 - 1), 3.0f, ColorUtil.withAlpha(bg, alpha));
                break;
            }
            case "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442": {
                Render2D.roundRect(ctx, (float)x1, (float)y1, (float)(x2 - x1), (float)(y2 - y1), 0.0f, Paint.horizontal(x1, x2, ColorUtil.withAlpha(bg, alpha), ColorUtil.withAlpha(bg, 0.0f)));
                break;
            }
            default: {
                ctx.fill(x1, y1, x2, y2, ColorUtil.withAlpha(bg, Math.min(1.0f, alpha * 1.15f)));
                ChatLayoutHud layout = ChatLayoutHud.get();
                if (layout != null && !layout.stripe()) break;
                ctx.fill(x1, y1, x1 + 1, y2, ColorUtil.withAlpha(t.accent(), Math.min(1.0f, alpha * 1.6f)));
            }
        }
    }

    public static void chatInput(GuiGraphics ctx, int x1, int y1, int x2, int y2) {
        switch (AnimeChatModule.chatStyle()) {
            case "\u041a\u043b\u0430\u0441\u0441\u0438\u043a\u0430": {
                ctx.fill(x1, y1, x2, y2, Minecraft.getInstance().options.getBackgroundColor(Integer.MIN_VALUE));
                break;
            }
            case "\u041c\u0438\u043d\u0438\u043c\u0430\u043b": {
                break;
            }
            case "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442": 
            case "\u041d\u0435\u043e\u043d": {
                AnimeChatModule.chatFill(ctx, x1, y1, x2, y2, -1157627904);
                break;
            }
            case "\u041f\u0443\u0437\u044b\u0440\u0438": {
                Render2D.roundRect(ctx, (float)x1, (float)y1, (float)(x2 - x1), (float)(y2 - y1), 4.0f, ColorUtil.withAlpha(AnimeChatModule.chatBackground(ThemeManager.get()), 0.85f));
                break;
            }
            default: {
                AnimeChatModule.drawGlass(ctx, x1, y1, x2 - x1, y2 - y1, 0.88f);
            }
        }
    }

    public static void tabFill(GuiGraphics ctx, int x1, int y1, int x2, int y2, int color) {
        float r;
        Theme t = ThemeManager.get();
        float w = x2 - x1;
        float h = y2 - y1;
        TabLayoutHud layout = TabLayoutHud.get();
        int bg = layout != null ? layout.background(t) : t.bg2();
        float a = layout != null ? layout.bgAlpha() : 0.88f;
        float f = r = layout != null ? layout.radius() : 4.0f;
        if (color == Integer.MIN_VALUE) {
            Render2D.roundRect(ctx, (float)(x1 - 1), (float)(y1 - 1), w + 2.0f, h + 2.0f, r, Paint.vertical(y1, y2, ColorUtil.withAlpha(bg, a), ColorUtil.withAlpha(ColorUtil.darken(bg, 0.2f), a)));
            if (layout == null || layout.outline()) {
                Render2D.outline(ctx, (float)(x1 - 1), (float)(y1 - 1), w + 2.0f, h + 2.0f, r, 0.7f, ColorUtil.withAlpha(t.accent(), 0.6f));
            }
        } else if (color >>> 24 > 0 && color >>> 24 < 96) {
            Render2D.roundRect(ctx, (float)x1, (float)y1, w, h, Math.min(2.0f, r), ColorUtil.withAlpha(t.accent(), layout != null ? layout.entryAlpha() : 0.14f));
        } else {
            ctx.fill(x1, y1, x2, y2, color);
        }
    }

    public static void ping(GuiGraphics ctx, int width, int x, int y, int latency) {
        int color = latency < 0 ? -7699815 : (latency < 80 ? -8585294 : (latency < 150 ? -2492003 : (latency < 250 ? -19641 : -41094)));
        String text = latency < 0 ? "?" : String.valueOf(Math.min(9999, latency));
        Font tr = Minecraft.getInstance().font;
        float s = 0.62f;
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate((float)(x + width - 1) - (float)tr.width(text) * s, (float)y + 1.5f);
        m.scale(s, s);
        ctx.drawString(tr, text, 0, 0, color, true);
        m.popMatrix();
    }
}

