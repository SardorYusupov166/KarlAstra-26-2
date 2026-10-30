/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FontDescription
 *  net.minecraft.network.chat.FontDescription$Resource
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.chat.numbers.NumberFormat
 *  net.minecraft.network.chat.numbers.StyledFormat
 *  net.minecraft.world.scores.DisplaySlot
 *  net.minecraft.world.scores.Objective
 *  net.minecraft.world.scores.PlayerScoreEntry
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Scoreboard
 *  net.minecraft.world.scores.Team
 */
package astra.module.modules.hud;

import astra.hud.HudModule;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ColorSetting;
import astra.settings.ModeSetting;
import astra.settings.NumberSetting;
import astra.theme.Theme;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class ScoreboardHud
extends HudModule {
    private static final Comparator<PlayerScoreEntry> ORDER = Comparator.comparing(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
    private final ModeSetting titleStyle = this.add(new ModeSetting("\u0417\u0430\u0433\u043e\u043b\u043e\u0432\u043e\u043a", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442 \u0442\u0435\u043c\u044b", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442 \u0442\u0435\u043c\u044b", "\u041a\u0430\u043a \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442"));
    private final ColorSetting titleColor = this.add((ColorSetting)new ColorSetting("\u0426\u0432\u0435\u0442 \u0437\u0430\u0433\u043e\u043b\u043e\u0432\u043a\u0430", -45608).visibleWhen(() -> this.titleStyle.is("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442")));
    private final ModeSetting numbers = this.add(new ModeSetting("\u0427\u0438\u0441\u043b\u0430 \u0441\u043f\u0440\u0430\u0432\u0430", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b", "\u041a\u0430\u043a \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435", "\u0421\u043a\u0440\u044b\u0442\u044c"));
    private final ModeSetting font = this.add(new ModeSetting("\u0428\u0440\u0438\u0444\u0442", "Astra (Rubik)", "Astra (Rubik)", "Minecraft"));
    private final BooleanSetting separator = this.add(new BooleanSetting("\u041b\u0438\u043d\u0438\u044f \u043f\u043e\u0434 \u0437\u0430\u0433\u043e\u043b\u043e\u0432\u043a\u043e\u043c", true));
    private final BooleanSetting lineShadow = this.add(new BooleanSetting("\u0422\u0435\u043d\u044c \u0442\u0435\u043a\u0441\u0442\u0430", true));
    private final NumberSetting lineGap = this.add(new NumberSetting("\u041c\u0435\u0436\u0441\u0442\u0440\u043e\u0447\u043d\u044b\u0439 \u0438\u043d\u0442\u0435\u0440\u0432\u0430\u043b", 2.0, 0.0, 6.0, 0.5));
    private Component title = Component.literal((String)"Astra Anime");
    private final List<Line> lines = new ArrayList<Line>();
    private float cachedW = 110.0f;
    private float cachedH = 80.0f;

    public ScoreboardHud() {
        super("scoreboard", "\u0421\u043a\u043e\u0440\u0431\u043e\u0440\u0434", "\u0411\u043e\u043a\u043e\u0432\u0430\u044f \u0442\u0430\u0431\u043b\u0438\u0446\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0432 \u0441\u0442\u0438\u043b\u0435 Astra: \u0446\u0432\u0435\u0442\u0430, \u0448\u0440\u0438\u0444\u0442, \u043f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c.", Category.HUD, Icon.CHART, 0.93f, 0.6f);
        this.setDefaultEnabled(true);
    }

    public static ScoreboardHud get() {
        return ModuleManager.get(ScoreboardHud.class);
    }

    public static boolean replacing() {
        ScoreboardHud m = ScoreboardHud.get();
        return m != null && m.isEnabled();
    }

    private Objective objective() {
        DisplaySlot slot;
        if (ScoreboardHud.mc.level == null || ScoreboardHud.mc.player == null) {
            return null;
        }
        Scoreboard sb = ScoreboardHud.mc.level.getScoreboard();
        PlayerTeam team = sb.getPlayersTeam(ScoreboardHud.mc.player.getScoreboardName());
        if (team != null && (slot = team.getColor().map(net.minecraft.world.scores.TeamColor::displaySlot).orElse(null)) != null && sb.getDisplayObjective(slot) != null) {
            return sb.getDisplayObjective(slot);
        }
        return sb.getDisplayObjective(DisplaySlot.SIDEBAR);
    }

    private boolean collect() {
        Objective obj = this.objective();
        this.lines.clear();
        if (obj == null) {
            return false;
        }
        Scoreboard sb = obj.getScoreboard();
        NumberFormat fmt = obj.numberFormatOrDefault((NumberFormat)StyledFormat.SIDEBAR_DEFAULT);
        this.title = obj.getDisplayName();
        sb.listPlayerScores(obj).stream().filter(e -> !e.isHidden()).sorted(ORDER).limit(15L).forEach(e -> {
            PlayerTeam team = sb.getPlayersTeam(e.owner());
            this.lines.add(new Line((Component)PlayerTeam.formatNameForTeam((Team)team, (Component)e.ownerName()), (Component)e.formatValue(fmt)));
        });
        return true;
    }

    private void sample() {
        this.lines.clear();
        this.title = Component.literal((String)"ASTRA SERVER").withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD});
        String[][] rows = new String[][]{{"\u041d\u0438\u043a: ", "Player"}, {"\u0411\u0430\u043b\u0430\u043d\u0441: ", "5k"}, {"\u0423\u0431\u0438\u0439\u0441\u0442\u0432: ", "12"}, {"\u0421\u043c\u0435\u0440\u0442\u0435\u0439: ", "3"}, {"", ""}, {"play.astra.gg", ""}};
        int v = rows.length;
        for (String[] r : rows) {
            this.lines.add(new Line((Component)Component.literal((String)r[0]).withStyle(ChatFormatting.WHITE).append((Component)Component.literal((String)r[1]).withStyle(ChatFormatting.LIGHT_PURPLE)), (Component)Component.literal((String)String.valueOf(v--)).withStyle(ChatFormatting.RED)));
        }
    }

    @Override
    public boolean shouldRender() {
        return this.objective() != null;
    }

    @Override
    protected boolean drawsPanel() {
        return true;
    }

    @Override
    public float width() {
        return this.cachedW;
    }

    @Override
    public float height() {
        return this.cachedH;
    }

    private Component styled(Component t) {
        if (this.font.is("Minecraft")) {
            return t;
        }
        return t.copy().withStyle(Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Fonts.REGULAR)));
    }

    private Component styledBold(Component t) {
        if (this.font.is("Minecraft")) {
            return t;
        }
        return t.copy().withStyle(Style.EMPTY.withFont((FontDescription)new FontDescription.Resource(Fonts.BOLD)));
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        float titleW;
        if (!this.collect()) {
            if (!editor) {
                return;
            }
            this.sample();
        }
        Theme t = ScoreboardHud.theme();
        int acc = this.accentColor();
        Font tr = ScoreboardHud.mc.font;
        float pad = 6.0f;
        float lh = 9.0f + this.lineGap.getFloat();
        boolean showNums = !this.numbers.is("\u0421\u043a\u0440\u044b\u0442\u044c");
        float maxW = titleW = (float)tr.width((FormattedText)this.styledBold(this.title));
        for (Line l : this.lines) {
            float lw = tr.width((FormattedText)this.styled(l.name())) + (showNums ? tr.width((FormattedText)this.styled(l.score())) + 10 : 0);
            maxW = Math.max(maxW, lw);
        }
        this.cachedW = Math.max(70.0f, maxW + pad * 2.0f);
        this.cachedH = pad + 11.0f + (float)((Boolean)this.separator.get() != false ? 4 : 1) + (float)this.lines.size() * lh + pad - 2.0f;
        float ty = pad - 1.0f;
        String plain = this.title.getString();
        if (this.titleStyle.is("\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442 \u0442\u0435\u043c\u044b")) {
            float gw = Fonts.width(plain, 9.0f, true);
            Fonts.drawGradient(ctx, plain, (w - gw) / 2.0f, ty, 9.0f, ColorUtil.brighten(acc, 0.2f), ColorUtil.brighten(t.accent2(), 0.2f), true);
        } else if (this.titleStyle.is("\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442")) {
            Fonts.drawCentered(ctx, plain, w / 2.0f, ty, 9.0f, this.titleColor.getColor(), true);
        } else {
            ctx.text(tr, this.styledBold(this.title), (int)((w - titleW) / 2.0f), (int)ty, -1, ((Boolean)this.lineShadow.get()).booleanValue());
        }
        float y = ty + 11.0f;
        if (((Boolean)this.separator.get()).booleanValue()) {
            Render2D.rect(ctx, pad, y, w - pad * 2.0f, 0.8f, Paint.horizontal(pad, w - pad, ColorUtil.withAlpha(acc, 0.0f), ColorUtil.withAlpha(acc, 0.9f)));
            Render2D.rect(ctx, w / 2.0f, y, w / 2.0f - pad, 0.8f, Paint.horizontal(w / 2.0f, w - pad, ColorUtil.withAlpha(t.accent2(), 0.9f), ColorUtil.withAlpha(t.accent2(), 0.0f)));
            y += 4.0f;
        } else {
            y += 1.0f;
        }
        int alpha = (int)(255.0f * Render2D.alpha) << 24;
        for (Line l : this.lines) {
            ctx.text(tr, this.styled(l.name()), (int)pad, (int)y, 0xFFFFFF | alpha, ((Boolean)this.lineShadow.get()).booleanValue());
            if (showNums) {
                Component sc = this.styled(l.score());
                int sw = tr.width((FormattedText)sc);
                if (this.numbers.is("\u0426\u0432\u0435\u0442 \u0442\u0435\u043c\u044b")) {
                    Fonts.drawRight(ctx, sc.getString(), w - pad, y, 9.0f, ColorUtil.brighten(acc, 0.25f), false);
                } else {
                    ctx.text(tr, sc, (int)(w - pad - (float)sw), (int)y, 0xFFFFFF | alpha, ((Boolean)this.lineShadow.get()).booleanValue());
                }
            }
            y += lh;
        }
    }

    private record Line(Component name, Component score) {
    }
}

