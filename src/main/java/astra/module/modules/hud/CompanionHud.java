/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.player.LocalPlayer
 *  org.joml.Matrix3x2fStack
 */
package astra.module.modules.hud;

import astra.AstraClient;
import astra.animation.AnimationUtils;
import astra.decor.DecorManager;
import astra.hud.HudModule;
import astra.media.NowPlaying;
import astra.module.Category;
import astra.module.ModuleManager;
import astra.module.modules.hud.AnimeChatModule;
import astra.module.modules.visual.NowPlayingModule;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.ModeSetting;
import astra.settings.Setting;
import astra.settings.StringSetting;
import astra.sound.SoundSlot;
import astra.sound.user.SoundPacks;
import astra.theme.Theme;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Matrix3x2fStack;

public class CompanionHud
extends HudModule {
    private final ModeSetting image = this.add(new ModeSetting("\u041a\u0430\u0440\u0442\u0438\u043d\u043a\u0430", "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0430\u0432\u0430\u0442\u0430\u0440 1", "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439: \u0430\u0432\u0430\u0442\u0430\u0440 1").dynamic(DecorManager::images));
    private final StringSetting name = this.add(new StringSetting("\u0418\u043c\u044f", "\u0421\u0430\u043a\u0443\u0440\u0430", "\u0438\u043c\u044f \u043a\u043e\u043c\u043f\u0430\u043d\u044c\u043e\u043d\u0430", 16));
    private final BooleanSetting talk = this.add(new BooleanSetting("\u0420\u0435\u043f\u043b\u0438\u043a\u0438", true));
    private final ModeSetting chatty = this.add((ModeSetting)new ModeSetting("\u0420\u0430\u0437\u0433\u043e\u0432\u043e\u0440\u0447\u0438\u0432\u043e\u0441\u0442\u044c", "\u041e\u0431\u044b\u0447\u043d\u043e", "\u0420\u0435\u0434\u043a\u043e", "\u041e\u0431\u044b\u0447\u043d\u043e", "\u0427\u0430\u0441\u0442\u043e").visibleWhen(() -> (Boolean)this.talk.get()));
    private final ModeSetting side = this.add(new ModeSetting("\u041e\u0431\u043b\u0430\u0447\u043a\u043e", "\u0421\u043f\u0440\u0430\u0432\u0430", "\u0421\u043f\u0440\u0430\u0432\u0430", "\u0421\u043b\u0435\u0432\u0430"));
    private final BooleanSetting music = this.add(new BooleanSetting("\u0420\u0435\u0430\u0433\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043d\u0430 \u043c\u0443\u0437\u044b\u043a\u0443", true));
    private final SoundSlot mentionSound = this.mentionSlot();
    private final ButtonSetting ownMentionSounds = this.add((ButtonSetting)new ButtonSetting("\u0421\u0432\u043e\u0438 \u0437\u0432\u0443\u043a\u0438 \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0439", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u0437\u0432\u0443\u043a\u043e\u0432 \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f", () -> {
        Path dir = SoundPacks.root().resolve("\u041c\u043e\u0438 \u0443\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f").resolve("\u0423\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0435");
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
            SoundPacks.open(dir);
            SoundPacks.rescanAsync(true);
        }
        catch (IOException e) {
            AstraClient.LOGGER.warn("\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0439", (Throwable)e);
        }
    }).desc("\u041f\u043e\u043b\u043e\u0436\u0438\u0442\u0435 MP3, WAV \u0438\u043b\u0438 OGG. \u0427\u0435\u0440\u0435\u0437 \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u043a\u0443\u043d\u0434 \u0444\u0430\u0439\u043b \u043f\u043e\u044f\u0432\u0438\u0442\u0441\u044f \u0432 \u0441\u043f\u0438\u0441\u043a\u0435 \u0437\u0432\u0443\u043a\u043e\u0432."));
    private final ButtonSetting refreshMentionSounds = this.add(new ButtonSetting("\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u0437\u0432\u0443\u043a\u0438 \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0439", "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u0437\u0432\u0443\u043a\u043e\u0432", () -> SoundPacks.rescanAsync(true)));
    private static final Random RANDOM = new Random();
    private Mood mood = Mood.NEUTRAL;
    private long moodAt;
    private String bubble;
    private long bubbleAt;
    private long lastTalk;
    private float lastHealth = -1.0f;
    private int lastLevel = -1;
    private boolean wasDead;
    private boolean warnedLow;
    private double idleX;
    private double idleZ;
    private float idleYaw;
    private long idleSince = System.currentTimeMillis();
    private String lastTrack = "";

    private SoundSlot mentionSlot() {
        SoundSlot slot = new SoundSlot("\u0423\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u0435", "mention", "mention", new String[]{"menu_2", "menu_1", "menu_3"}, 100, AnimeChatModule::mentionVolume);
        slot.volume.desc("\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e \u0443\u0447\u0438\u0442\u044b\u0432\u0430\u0435\u0442\u0441\u044f \u0433\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f \u0438\u0437 \u043c\u043e\u0434\u0443\u043b\u044f \u00ab\u0410\u043d\u0438\u043c\u0435-\u0447\u0430\u0442\u00bb.");
        slot.variant.desc("\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0439 \u0432\u0430\u0440\u0438\u0430\u043d\u0442 \u0438\u043b\u0438 \u0441\u0432\u043e\u0439 \u0444\u0430\u0439\u043b \u0438\u0437 \u043f\u0430\u043f\u043a\u0438 \u0443\u043f\u043e\u043c\u0438\u043d\u0430\u043d\u0438\u044f. \u041d\u0435 \u0437\u0430\u0432\u0438\u0441\u0438\u0442 \u043e\u0442 \u0432\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u0438 \u043a\u043e\u043c\u043f\u0430\u043d\u044c\u043e\u043d\u0430.");
        for (Setting<?> setting : slot.settings()) {
            this.add(setting);
        }
        return slot;
    }

    public static void playMentionSound() {
        CompanionHud c = CompanionHud.get();
        if (c != null) {
            c.mentionSound.play(AnimeChatModule::mentionVolume);
        }
    }

    public CompanionHud() {
        super("companion", "\u0410\u043d\u0438\u043c\u0435-\u043a\u043e\u043c\u043f\u0430\u043d\u044c\u043e\u043d", "\u0414\u0435\u0432\u043e\u0447\u043a\u0430-\u043c\u0430\u0441\u043a\u043e\u0442 \u0440\u0435\u0430\u0433\u0438\u0440\u0443\u0435\u0442 \u043d\u0430 \u0431\u043e\u0439, \u0443\u0440\u043e\u043d, \u043c\u0443\u0437\u044b\u043a\u0443 \u0438 \u0441\u043a\u0443\u043a\u0443.", Category.HUD, Icon.SMILE, 0.08f, 0.72f);
        this.setDefaultEnabled(true);
    }

    public static CompanionHud get() {
        return ModuleManager.get(CompanionHud.class);
    }

    public static void react(Mood mood, String ... phrases) {
        CompanionHud c = CompanionHud.get();
        if (c == null || !c.isEnabled()) {
            return;
        }
        c.setMood(mood, phrases);
    }

    private void setMood(Mood m, String ... phrases) {
        boolean important;
        this.mood = m;
        this.moodAt = System.currentTimeMillis();
        if (!((Boolean)this.talk.get()).booleanValue() || phrases.length == 0) {
            return;
        }
        long gap = switch ((String)this.chatty.get()) {
            case "\u0420\u0435\u0434\u043a\u043e" -> 9000L;
            case "\u0427\u0430\u0441\u0442\u043e" -> 1200L;
            default -> 3500L;
        };
        boolean bl = important = m == Mood.WORRIED || m == Mood.SAD;
        if (!important && System.currentTimeMillis() - this.lastTalk < gap) {
            return;
        }
        this.bubble = phrases[RANDOM.nextInt(phrases.length)];
        this.bubbleAt = this.lastTalk = System.currentTimeMillis();
    }

    public static void onHit(boolean crit, int combo) {
        if (combo >= 5 && combo % 5 == 0) {
            CompanionHud.react(Mood.EXCITED, "\u041a\u043e\u043c\u0431\u043e " + combo + "! \u0421\u0443\u0433\u043e\u0439!", "\u0415\u0449\u0451-\u0435\u0449\u0451!", "\u0422\u044b \u0432 \u0443\u0434\u0430\u0440\u0435!");
        } else if (crit) {
            CompanionHud.react(Mood.HAPPY, "\u041a\u0440\u0438\u0442!", "\u0411\u0430\u0445!", "\u041a\u044c\u044f~");
        } else {
            CompanionHud.react(Mood.HAPPY, new String[0]);
        }
    }

    public static void onKill() {
        CompanionHud.react(Mood.EXCITED, "\u041f\u043e\u0431\u0435\u0434\u0430! \u0422\u044b \u043b\u0443\u0447\u0448\u0438\u0439 \u2661", "\u0423\u0440\u0430-\u0430-\u0430!", "\u0418\u0437\u0438~", "\u0422\u0430\u043a \u0435\u043c\u0443!");
    }

    public static void onTotem(boolean self) {
        if (self) {
            CompanionHud.react(Mood.WORRIED, "\u0424\u0443\u0445\u2026 \u0442\u043e\u0442\u0435\u043c \u0441\u043f\u0430\u0441!", "\u0427\u0443\u0442\u044c \u043d\u0435 \u0443\u043c\u0435\u0440!");
        } else {
            CompanionHud.react(Mood.EXCITED, "\u0422\u043e\u0442\u0435\u043c \u0432\u044b\u0431\u0438\u0442! \u0414\u043e\u0431\u0438\u0432\u0430\u0439!", "\u0425\u0438-\u0445\u0438, \u043b\u043e\u0445!");
        }
    }

    public static void onJoin() {
        CompanionHud.react(Mood.HAPPY, "\u041f\u0440\u0438\u0432\u0435\u0442, \u0441\u0435\u043c\u043f\u0430\u0439!", "\u041f\u043e\u0438\u0433\u0440\u0430\u0435\u043c? \u2661", "\u042f \u0441\u043a\u0443\u0447\u0430\u043b\u0430!");
    }

    @Override
    public void onTick() {
        long moodLife;
        boolean moved;
        LocalPlayer p = CompanionHud.mc.player;
        if (p == null) {
            return;
        }
        long now = System.currentTimeMillis();
        float hp = p.getHealth();
        if (this.lastHealth >= 0.0f && hp < this.lastHealth - 0.5f && !p.isDeadOrDying()) {
            if (hp <= p.getMaxHealth() * 0.3f && !this.warnedLow) {
                this.warnedLow = true;
                CompanionHud.react(Mood.WORRIED, "\u041e\u0441\u0442\u043e\u0440\u043e\u0436\u043d\u043e! \u041c\u0430\u043b\u043e \u0437\u0434\u043e\u0440\u043e\u0432\u044c\u044f!", "\u0411\u0435\u0433\u0438 \u0438 \u043b\u0435\u0447\u0438\u0441\u044c!", "\u0410\u0439-\u0430\u0439, \u0431\u043e\u043b\u044c\u043d\u043e!");
            } else if (hp > p.getMaxHealth() * 0.3f) {
                CompanionHud.react(Mood.WORRIED, "\u041e\u0439!", "\u0410\u0439!", "\u0411\u043e\u043b\u044c\u043d\u043e \u0436\u0435!");
            }
        }
        if (hp > p.getMaxHealth() * 0.5f) {
            this.warnedLow = false;
        }
        this.lastHealth = hp;
        if (p.isDeadOrDying() && !this.wasDead) {
            CompanionHud.react(Mood.SAD, "\u041d\u0435\u0435\u0435\u0442\u2026", "\u042f \u0431\u0443\u0434\u0443 \u0436\u0434\u0430\u0442\u044c \u0442\u0435\u0431\u044f\u2026", "\u041c\u044b \u0435\u0449\u0451 \u043e\u0442\u043e\u043c\u0441\u0442\u0438\u043c!");
        }
        if (!p.isDeadOrDying() && this.wasDead) {
            CompanionHud.react(Mood.HAPPY, "\u0422\u044b \u0432\u0435\u0440\u043d\u0443\u043b\u0441\u044f!", "\u0412\u043f\u0435\u0440\u0451\u0434, \u0432 \u0431\u043e\u0439!");
        }
        this.wasDead = p.isDeadOrDying();
        if (this.lastLevel >= 0 && p.experienceLevel > this.lastLevel) {
            CompanionHud.react(Mood.EXCITED, "\u041d\u043e\u0432\u044b\u0439 \u0443\u0440\u043e\u0432\u0435\u043d\u044c: " + p.experienceLevel + "!", "\u0420\u0430\u0441\u0442\u0451\u043c!");
        }
        this.lastLevel = p.experienceLevel;
        boolean bl = moved = Math.abs(p.getX() - this.idleX) > 0.1 || Math.abs(p.getZ() - this.idleZ) > 0.1 || Math.abs(p.getYRot() - this.idleYaw) > 1.0f;
        if (moved) {
            this.idleX = p.getX();
            this.idleZ = p.getZ();
            this.idleYaw = p.getYRot();
            this.idleSince = now;
            if (this.mood == Mood.SLEEPY) {
                this.mood = Mood.NEUTRAL;
            }
        } else if (now - this.idleSince > 45000L && this.mood != Mood.SLEEPY) {
            CompanionHud.react(Mood.SLEEPY, "\u0421\u043a\u0443\u0447\u043d\u043e-\u043e\u2026", "\u042f \u043f\u043e\u0441\u043f\u043b\u044e, \u043b\u0430\u0434\u043d\u043e?", "Zzz\u2026");
        }
        if (((Boolean)this.music.get()).booleanValue()) {
            NowPlaying track;
            NowPlayingModule np = NowPlayingModule.get();
            NowPlaying nowPlaying = track = np != null && np.isEnabled() ? np.track() : NowPlaying.NONE;
            if (track.hasTrack() && !track.key().equals(this.lastTrack)) {
                this.lastTrack = track.key();
                CompanionHud.react(Mood.MUSIC, "\u041e\u043e\u043e, \u00ab" + Fonts.trim(track.title(), 90.0f, 6.6f, false) + "\u00bb!", "\u041b\u044e\u0431\u043b\u044e \u044d\u0442\u0443 \u043f\u0435\u0441\u043d\u044e \u266a", "\u041f\u043e\u0434\u043f\u0435\u0432\u0430\u0439!");
            } else if (track.hasTrack() && track.playing() && this.mood == Mood.NEUTRAL) {
                this.mood = Mood.MUSIC;
                this.moodAt = now;
            }
        }
        long l = moodLife = this.mood == Mood.SLEEPY || this.mood == Mood.MUSIC ? Long.MAX_VALUE : 2600L;
        if (now - this.moodAt > moodLife) {
            this.mood = Mood.NEUTRAL;
        }
    }

    @Override
    public float width() {
        return 46.0f;
    }

    @Override
    public float height() {
        return 52.0f;
    }

    @Override
    protected boolean drawsPanel() {
        return false;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor ctx, float w, float h, boolean editor) {
        Theme t = CompanionHud.theme();
        int acc = this.accentColor();
        float time = AnimationUtils.time();
        float age = (float)(System.currentTimeMillis() - this.moodAt) / 1000.0f;
        float r = 20.0f;
        float cx = w / 2.0f;
        float cy = 24.0f;
        float dx = 0.0f;
        float dy = 0.0f;
        float rot = 0.0f;
        float scale = 1.0f + 0.015f * (float)Math.sin(time * 2.2f);
        switch (this.mood.ordinal()) {
            case 1: {
                dy = -Math.abs((float)Math.sin(age * 10.0f)) * 5.0f * Math.max(0.0f, 1.0f - age / 1.2f);
                break;
            }
            case 2: {
                dy = -Math.abs((float)Math.sin(age * 12.0f)) * 8.0f * Math.max(0.0f, 1.0f - age / 1.6f);
                scale += 0.06f * Math.max(0.0f, 1.0f - age);
                break;
            }
            case 3: {
                dx = (float)Math.sin(age * 45.0f) * 2.5f * Math.max(0.0f, 1.0f - age / 0.8f);
                break;
            }
            case 4: {
                dy = 3.0f;
                rot = -6.0f;
                break;
            }
            case 5: {
                rot = (float)Math.sin(time * 1.4f) * 7.0f;
                dy = 1.5f;
                break;
            }
            case 6: {
                rot = (float)Math.sin(time * 4.2f) * 6.0f;
                dy = -Math.abs((float)Math.sin(time * 4.2f)) * 2.0f;
                break;
            }
        }
        Matrix3x2fStack m = ctx.pose();
        m.pushMatrix();
        m.translate(cx + dx, cy + dy);
        m.rotate((float)Math.toRadians(rot));
        m.scale(scale, scale);
        Render2D.glow(ctx, -r, -r, r * 2.0f, r * 2.0f, r, 7.0f, ColorUtil.withAlpha(acc, this.mood == Mood.EXCITED ? 0.65f : 0.4f));
        Render2D.circle(ctx, 0.0f, 0.0f, r + 1.8f, Paint.linear(-r, -r, r, r, acc, t.accent2()));
        DecorManager.Tex tex = DecorManager.texture((String)this.image.get());
        if (tex != null) {
            Render2D.texturedCircle(ctx, Render2D.textureView(tex.id()), 0.0f, 0.0f, r, 0.5f, 0.45f, 0.5f, 0.0f, -1);
        } else {
            Render2D.circle(ctx, 0.0f, 0.0f, r, t.bg2());
        }
        if (this.mood == Mood.SLEEPY) {
            Render2D.circle(ctx, 0.0f, 0.0f, r, ColorUtil.withAlpha(-15069133, 0.35f));
        }
        m.popMatrix();
        this.drawMoodIcons(ctx, t, acc, cx + dx, cy + dy, r, time, age);
        String n = ((String)this.name.get()).isBlank() ? "\u0421\u0430\u043a\u0443\u0440\u0430" : (String)this.name.get();
        float nw = Fonts.width(n, 6.0f, true) + 8.0f;
        Render2D.roundRect(ctx, cx - nw / 2.0f, h - 9.0f, nw, 9.0f, 4.5f, ColorUtil.withAlpha(t.bg2(), 0.9f));
        Render2D.outline(ctx, cx - nw / 2.0f, h - 9.0f, nw, 9.0f, 4.5f, 0.6f, ColorUtil.withAlpha(acc, 0.8f));
        Fonts.drawCentered(ctx, n, cx, h - 7.5f, 6.0f, t.text(), true);
        String text = this.bubble;
        if (editor && text == null) {
            text = "\u041f\u0440\u0438\u0432\u0435\u0442, \u0441\u0435\u043c\u043f\u0430\u0439! \u2661";
        }
        if (text != null) {
            this.drawBubble(ctx, t, acc, text, w, editor);
        }
    }

    private void drawMoodIcons(GuiGraphicsExtractor ctx, Theme t, int acc, float cx, float cy, float r, float time, float age) {
        switch (this.mood.ordinal()) {
            case 1: 
            case 2: {
                for (int i = 0; i < (this.mood == Mood.EXCITED ? 3 : 2); ++i) {
                    float a = Math.max(0.0f, 1.0f - (age - (float)i * 0.15f) / 1.4f);
                    if (a <= 0.0f) continue;
                    float hx = cx + r * 0.9f + (float)(i * 5) - 4.0f;
                    float hy = cy - r * 0.6f - age * 12.0f - (float)(i * 6);
                    Render2D.heart(ctx, hx, hy, 3.4f, ColorUtil.withAlpha(-41048, a));
                }
                if (this.mood != Mood.EXCITED) break;
                Render2D.sparkle(ctx, cx - r, cy - r + 2.0f, 4.0f, time * 90.0f, ColorUtil.withAlpha(-1, 0.9f));
                break;
            }
            case 3: {
                Render2D.circle(ctx, cx + r * 0.85f, cy - r * 0.55f, 2.6f, -8530948);
                Fonts.draw(ctx, "!", cx - r - 3.0f, cy - r - 2.0f, 10.0f, -41121, true);
                break;
            }
            case 4: {
                Fonts.draw(ctx, "\u2026", cx + r - 2.0f, cy - r - 2.0f, 9.0f, t.textDim(), true);
                break;
            }
            case 5: {
                for (int i = 0; i < 3; ++i) {
                    float k = (time * 0.6f + (float)i / 3.0f) % 1.0f;
                    Fonts.draw(ctx, "z", cx + r * 0.6f + k * 10.0f, cy - r * 0.4f - k * 16.0f, 5.0f + k * 4.0f, ColorUtil.withAlpha(-3885571, 1.0f - k), true);
                }
                break;
            }
            case 6: {
                for (int i = 0; i < 2; ++i) {
                    float k = (time * 0.5f + (float)i / 2.0f) % 1.0f;
                    Fonts.draw(ctx, "\u266a", cx - r - 2.0f + (float)i * (r * 2.0f + 2.0f) - k * 4.0f * (float)(i == 0 ? 1 : -1), cy - r * 0.2f - k * 16.0f, 7.0f, ColorUtil.withAlpha(acc, 1.0f - k), true);
                }
                break;
            }
        }
    }

    private void drawBubble(GuiGraphicsExtractor ctx, Theme t, int acc, String text, float w, boolean editor) {
        long age = editor ? 1000L : System.currentTimeMillis() - this.bubbleAt;
        long life = 3600L;
        if (!editor && age > life) {
            this.bubble = null;
            return;
        }
        float a = Math.min(1.0f, Math.min((float)age / 180.0f, (float)(life - age) / 350.0f));
        int shown = editor ? text.length() : (int)Math.min((long)text.length(), age / 28L);
        List<String> lines = Fonts.wrap(text, 110.0f, 6.8f, false);
        float bw = 8.0f;
        float lh = 9.0f;
        for (String l : lines) {
            bw = Math.max(bw, Fonts.width(l, 6.8f, false) + 12.0f);
        }
        float bh = (float)lines.size() * lh + 7.0f;
        boolean right = this.side.is("\u0421\u043f\u0440\u0430\u0432\u0430");
        float bx = right ? w + 4.0f : -bw - 4.0f;
        float by = 4.0f - (1.0f - a) * 4.0f;
        float prev = Render2D.alpha;
        Render2D.alpha *= a;
        Render2D.glow(ctx, bx, by, bw, bh, 6.0f, 5.0f, ColorUtil.withAlpha(acc, 0.3f));
        Render2D.roundRect(ctx, bx, by, bw, bh, 6.0f, Paint.vertical(by, by + bh, ColorUtil.withAlpha(t.bg2(), 0.95f), ColorUtil.withAlpha(t.bg(), 0.95f)));
        Render2D.outline(ctx, bx, by, bw, bh, 6.0f, 0.8f, Paint.horizontal(bx, bx + bw, acc, t.accent2()));
        float tx = right ? bx : bx + bw;
        float ty = by + bh * 0.6f;
        Render2D.triangle(ctx, tx, ty - 3.0f, tx, ty + 3.0f, tx + (float)(right ? -4 : 4), ty + 1.0f, ColorUtil.withAlpha(t.bg2(), 0.95f));
        int left = shown;
        float y = by + 4.0f;
        for (String l : lines) {
            String part = l.substring(0, Math.max(0, Math.min(l.length(), left)));
            left -= l.length() + 1;
            Fonts.draw(ctx, part, bx + 6.0f, y, 6.8f, t.text(), false);
            y += lh;
        }
        Render2D.alpha = prev;
    }

    public static enum Mood {
        NEUTRAL,
        HAPPY,
        EXCITED,
        WORRIED,
        SAD,
        SLEEPY,
        MUSIC;

    }
}

