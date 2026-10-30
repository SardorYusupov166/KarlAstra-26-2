/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.util.FormattedCharSequence
 */
package astra.utils;

import astra.module.modules.hud.AnimeChatModule;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class StreamerProtect {
    private static final Set<String> COMMANDS = Set.of("login", "l", "log", "register", "reg", "changepassword", "changepass", "cp", "unregister", "unreg", "premium", "2fa", "totp");
    private static final Pattern IN_TEXT = Pattern.compile("(?i)(/(?:login|l|log|register|reg|changepassword|changepass|cp|unregister|unreg)\\s+)(\\S+(?:\\s+\\S+)*)");
    public static final char DOT = '\u2022';

    private StreamerProtect() {
    }

    public static boolean enabled() {
        return AnimeChatModule.hidePasswords();
    }

    public static int secretStart(String text) {
        int start;
        if (text == null || !text.startsWith("/")) {
            return -1;
        }
        int space = text.indexOf(32);
        if (space < 0) {
            return -1;
        }
        String cmd = text.substring(1, space).toLowerCase(Locale.ROOT);
        int colon = cmd.indexOf(58);
        if (colon >= 0) {
            cmd = cmd.substring(colon + 1);
        }
        if (!COMMANDS.contains(cmd)) {
            return -1;
        }
        for (start = space + 1; start < text.length() && text.charAt(start) == ' '; ++start) {
        }
        return start;
    }

    public static FormattedCharSequence maskInput(String fullText, String visible, int firstIndex) {
        if (!StreamerProtect.enabled()) {
            return null;
        }
        int secret = StreamerProtect.secretStart(fullText);
        if (secret < 0) {
            return null;
        }
        StringBuilder open = new StringBuilder();
        StringBuilder hidden = new StringBuilder();
        for (int i = 0; i < visible.length(); ++i) {
            char c = visible.charAt(i);
            if (firstIndex + i >= secret && c != ' ') {
                hidden.append('\u2022');
                continue;
            }
            if (hidden.isEmpty()) {
                open.append(c);
                continue;
            }
            hidden.append(c);
        }
        MutableComponent t = Component.literal((String)open.toString()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)).append((Component)Component.literal((String)hidden.toString()).setStyle(Style.EMPTY.withColor(-5011201)));
        return t.getVisualOrderText();
    }

    public static Component maskMessage(Component message) {
        if (!StreamerProtect.enabled()) {
            return message;
        }
        String s = message.getString();
        Matcher m = IN_TEXT.matcher(s);
        if (!m.find()) {
            return message;
        }
        StringBuilder out = new StringBuilder();
        m.reset();
        while (m.find()) {
            m.appendReplacement(out, Matcher.quoteReplacement(m.group(1) + m.group(2).replaceAll("\\S", String.valueOf('\u2022'))));
        }
        m.appendTail(out);
        return Component.literal((String)out.toString()).setStyle(message.getStyle());
    }
}

