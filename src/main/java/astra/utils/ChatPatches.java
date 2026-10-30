/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.GuiMessage
 *  net.minecraft.client.GuiMessage$Line
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.ChatComponent
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.TextColor
 */
package astra.utils;

import astra.AstraClient;
import astra.config.ConfigManager;
import astra.mixin.ChatHudAccessor;
import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.hud.ChatLayoutHud;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class ChatPatches {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final Pattern STAMP = Pattern.compile("^\\[\\d{2}:\\d{2}] ");
    private static final Pattern COUNT = Pattern.compile(" \\(x\\d+\\)$");
    private static final int SAVE_LIMIT = 500;
    public static boolean raw;
    private static String lastPlain;
    private static GuiMessage lastLine;
    private static int repeat;
    private static boolean needRefresh;
    private static boolean restored;

    private ChatPatches() {
    }

    private static AnimeChatModule m() {
        return AnimeChatModule.get();
    }

    public static boolean active() {
        AnimeChatModule m = ChatPatches.m();
        return m != null && m.isEnabled();
    }

    public static int limit() {
        return ChatPatches.active() && ChatPatches.m().longHistory() ? 1000 : 100;
    }

    public static boolean keepChat() {
        return ChatPatches.active() && ChatPatches.m().keepChat();
    }

    public static Component decorate(ChatComponent hud, Component message) {
        if (raw || !ChatPatches.active() || message == null) {
            return message;
        }
        String plain = message.getString();
        List<GuiMessage> lines = ((ChatHudAccessor)hud).astra$messages();
        if (ChatPatches.m().stackRepeats() && plain.equals(lastPlain) && !lines.isEmpty() && lines.get(0) == lastLine) {
            ++repeat;
            lines.remove(0);
            needRefresh = true;
        } else {
            repeat = 1;
        }
        lastPlain = plain;
        MutableComponent out = Component.empty();
        if (ChatPatches.m().timestamps()) {
            out.append((Component)Component.literal((String)("[" + LocalTime.now().format(TIME) + "] ")).setStyle(Style.EMPTY.withColor(-7700568)));
        }
        out.append(message);
        if (repeat > 1) {
            out.append((Component)Component.literal((String)(" (x" + repeat + ")")).setStyle(Style.EMPTY.withColor(-5011201)));
        }
        return out;
    }

    public static void afterAdd(ChatComponent hud) {
        List<GuiMessage> lines = ((ChatHudAccessor)hud).astra$messages();
        if (!raw) {
            GuiMessage guiMessage = lastLine = lines.isEmpty() ? null : lines.get(0);
        }
        if (needRefresh) {
            needRefresh = false;
            ((ChatHudAccessor)hud).astra$refresh();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onJoin() {
        Minecraft mc = Minecraft.getInstance();
        if (!ChatPatches.active()) {
            return;
        }
        ChatComponent hud = mc.gui.getChat();
        raw = true;
        try {
            if (!restored && ChatPatches.keepChat()) {
                restored = true;
                for (Component t : ChatPatches.load()) {
                    hud.addMessage(t);
                }
            }
            if (ChatPatches.m().sessionDivider() && !((ChatHudAccessor)hud).astra$messages().isEmpty()) {
                ServerData server = mc.getCurrentServer();
                String where = server != null ? (server.name == null || server.name.isBlank() ? server.ip : server.name) : "\u041e\u0434\u0438\u043d\u043e\u0447\u043d\u0430\u044f \u0438\u0433\u0440\u0430";
                hud.addMessage((Component)Component.literal((String)"\u2014\u2014\u2014\u2014 ").withStyle(ChatFormatting.DARK_GRAY).append((Component)Component.literal((String)where).setStyle(Style.EMPTY.withColor(-28720).withBold(Boolean.valueOf(true)))).append((Component)Component.literal((String)("  \u00b7  " + LocalTime.now().format(TIME) + " \u2014\u2014\u2014\u2014")).withStyle(ChatFormatting.DARK_GRAY)));
            }
        }
        finally {
            raw = false;
        }
        lastPlain = null;
        lastLine = null;
    }

    private static Path file() {
        return ConfigManager.root().resolve("chat_history.json");
    }

    public static void save() {
        if (!ChatPatches.keepChat()) {
            return;
        }
        try {
            List<GuiMessage> lines = ((ChatHudAccessor)Minecraft.getInstance().gui.getChat()).astra$messages();
            JsonArray arr = new JsonArray();
            int n = Math.min(500, lines.size());
            for (int i = n - 1; i >= 0; --i) {
                arr.add((JsonElement)ChatPatches.toJson(lines.get(i).content()));
            }
            Files.createDirectories(ChatPatches.file().getParent(), new FileAttribute[0]);
            Files.writeString(ChatPatches.file(), (CharSequence)arr.toString(), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u0427\u0430\u0442: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0438\u0441\u0442\u043e\u0440\u0438\u044e", (Throwable)e);
        }
    }

    private static List<Component> load() {
        ArrayList<Component> out = new ArrayList<Component>();
        try {
            if (!Files.isRegularFile(ChatPatches.file(), new LinkOption[0])) {
                return out;
            }
            JsonArray arr = JsonParser.parseString((String)Files.readString(ChatPatches.file(), StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement el : arr) {
                out.add(ChatPatches.fromJson(el.getAsJsonArray()));
            }
        }
        catch (Exception e) {
            AstraClient.LOGGER.warn("\u0427\u0430\u0442: \u043d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u0438\u0441\u0442\u043e\u0440\u0438\u044e", (Throwable)e);
        }
        return out;
    }

    private static JsonArray toJson(Component text) {
        JsonArray parts = new JsonArray();
        text.visit((style, str) -> {
            if (str.isEmpty()) {
                return Optional.empty();
            }
            JsonObject o = new JsonObject();
            o.addProperty("t", str);
            if (style.getColor() != null) {
                o.addProperty("c", (Number)style.getColor().getValue());
            }
            if (style.isBold()) {
                o.addProperty("b", Boolean.valueOf(true));
            }
            if (style.isItalic()) {
                o.addProperty("i", Boolean.valueOf(true));
            }
            if (style.isUnderlined()) {
                o.addProperty("u", Boolean.valueOf(true));
            }
            if (style.isStrikethrough()) {
                o.addProperty("s", Boolean.valueOf(true));
            }
            parts.add((JsonElement)o);
            return Optional.empty();
        }, Style.EMPTY);
        return parts;
    }

    private static Component fromJson(JsonArray parts) {
        MutableComponent out = Component.empty();
        for (JsonElement el : parts) {
            JsonObject o = el.getAsJsonObject();
            Style st = Style.EMPTY;
            if (o.has("c")) {
                st = st.withColor(TextColor.fromRgb((int)o.get("c").getAsInt()));
            }
            if (o.has("b")) {
                st = st.withBold(Boolean.valueOf(true));
            }
            if (o.has("i")) {
                st = st.withItalic(Boolean.valueOf(true));
            }
            if (o.has("u")) {
                st = st.withUnderlined(Boolean.valueOf(true));
            }
            if (o.has("s")) {
                st = st.withStrikethrough(Boolean.valueOf(true));
            }
            out.append((Component)Component.literal((String)o.get("t").getAsString()).setStyle(st));
        }
        return out;
    }

    private static int lineAt(ChatComponent hud, ChatHudAccessor acc, int size, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        double x = ChatLayoutHud.unmapX(mouseX);
        double y = ChatLayoutHud.unmapY(mouseY);
        double scale = acc.astra$chatScale();
        double cx = x / scale - 4.0;
        double cy = y / scale;
        int bottom = (int)Math.floor((double)(mc.getWindow().getGuiScaledHeight() - 40) / scale);
        int lineHeight = acc.astra$lineHeight();
        if (cx < -4.0 || cx > Math.ceil((double)acc.astra$width() / scale) + 4.0 || cy > (double)bottom) {
            return -1;
        }
        int k = (int)Math.floor(((double)bottom - cy) / (double)lineHeight);
        int shown = Math.min(size - acc.astra$scrolledLines(), hud.getLinesPerPage());
        if (k < 0 || k >= shown) {
            return -1;
        }
        return k + acc.astra$scrolledLines();
    }

    public static boolean copyAt(double mouseX, double mouseY) {
        List<GuiMessage.Line> visible;
        ChatHudAccessor acc;
        if (!ChatPatches.active() || !ChatPatches.m().rightClickCopy()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        ChatComponent hud = mc.gui.getChat();
        int idx = ChatPatches.lineAt(hud, acc = (ChatHudAccessor)hud, (visible = acc.astra$visibleMessages()).size(), mouseX, mouseY);
        if (idx < 0 || idx >= visible.size()) {
            return false;
        }
        int tick = visible.get(idx).addedTime();
        for (GuiMessage line : acc.astra$messages()) {
            if (line.addedTime() != tick) continue;
            String text = COUNT.matcher(STAMP.matcher(line.content().getString()).replaceFirst("")).replaceFirst("").replaceAll("\u00a7.", "");
            mc.keyboardHandler.setClipboard(text);
            mc.gui.setOverlayMessage((Component)Component.literal((String)("\u0421\u043a\u043e\u043f\u0438\u0440\u043e\u0432\u0430\u043d\u043e: " + (String)(text.length() > 40 ? text.substring(0, 40) + "\u2026" : text))).setStyle(Style.EMPTY.withColor(-28720)), false);
            return true;
        }
        return false;
    }

    static {
        repeat = 1;
    }
}

