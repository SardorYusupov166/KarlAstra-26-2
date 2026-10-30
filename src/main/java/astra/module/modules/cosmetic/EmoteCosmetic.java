/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.gui.screens.Screen
 */
package astra.module.modules.cosmetic;

import astra.gui.screens.EmoteWheelScreen;
import astra.module.Category;
import astra.module.modules.cosmetic.PlayerCosmetic;
import astra.online.AstraOnline;
import astra.particle.AstraParticle;
import astra.particle.AstraParticles;
import astra.particle.ParticleSprite;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import astra.settings.Setting;
import astra.sound.SoundSlot;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.Random;
import net.minecraft.client.gui.screens.Screen;

public class EmoteCosmetic
extends PlayerCosmetic {
    private final ModeSetting emote = this.add(new ModeSetting("\u042d\u043c\u043e\u0446\u0438\u044f", "\u041b\u044e\u0431\u043e\u0432\u044c", "\u041b\u044e\u0431\u043e\u0432\u044c", "\u0412\u043e\u0441\u0442\u043e\u0440\u0433", "\u041c\u0443\u0437\u044b\u043a\u0430", "\u0417\u043b\u043e\u0441\u0442\u044c", "\u0421\u043e\u043d"));
    private final KeybindSetting key = this.add(new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430", 71));
    private final BooleanSetting wheel = this.add((BooleanSetting)new BooleanSetting("\u041a\u043e\u043b\u0435\u0441\u043e \u044d\u043c\u043e\u0446\u0438\u0439", true).desc("\u0417\u0430\u0436\u043c\u0438\u0442\u0435 \u043a\u043b\u0430\u0432\u0438\u0448\u0443 \u2014 \u043e\u0442\u043a\u0440\u043e\u0435\u0442\u0441\u044f \u043a\u0440\u0443\u0433\u043e\u0432\u043e\u0435 \u043c\u0435\u043d\u044e; \u043e\u0442\u043f\u0443\u0441\u0442\u0438\u0442\u0435 \u043d\u0430\u0434 \u044d\u043c\u043e\u0446\u0438\u0435\u0439"));
    private final SoundSlot sound = new SoundSlot("\u0417\u0432\u0443\u043a \u044d\u043c\u043e\u0446\u0438\u0438", "emote", new String[]{"emote_1", "emote_2", "emote_3", "emote_4"}, 70, null);
    private final ButtonSetting play = this.add(new ButtonSetting("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c", "\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u044d\u043c\u043e\u0446\u0438\u044e", this::play));
    private boolean wasDown;
    private int burstTicks;
    private String burstEmote = "\u041b\u044e\u0431\u043e\u0432\u044c";

    public EmoteCosmetic() {
        super("emotes", "\u042d\u043c\u043e\u0446\u0438\u0438", "\u042d\u043c\u043e\u0446\u0438\u0438-\u0447\u0430\u0441\u0442\u0438\u0446\u044b \u0438 \u0437\u0432\u0443\u043a \u043d\u0430\u0434 \u0433\u043e\u043b\u043e\u0432\u043e\u0439 \u043f\u043e \u043a\u043b\u0430\u0432\u0438\u0448\u0435.", Category.COSMETICS, Icon.SMILE);
        this.setDefaultEnabled(true);
        for (Setting<?> s : this.sound.settings()) {
            this.add(s);
        }
    }

    private void play() {
        this.burstTicks = 24;
        this.burstEmote = (String)this.emote.get();
        if (EmoteCosmetic.mc.player != null) {
            this.sound.play(null);
        }
        AstraOnline.sendEmote((String)this.emote.get());
    }

    public void playEmote(String name) {
        this.burstTicks = 24;
        this.burstEmote = name;
        if (EmoteCosmetic.mc.player != null) {
            this.sound.play(null);
        }
        AstraOnline.sendEmote(name);
    }

    public void remoteBurst(String name) {
        this.burstTicks = 24;
        this.burstEmote = name;
    }

    @Override
    public void onTick() {
        ParticleSprite sprite;
        if (!this.isRemote()) {
            boolean down;
            boolean bl = down = EmoteCosmetic.mc.screen == null && this.key.isBound() && InputConstants.isKeyDown((Window)mc.getWindow(), (int)this.key.getKey());
            if (down && !this.wasDown) {
                if (((Boolean)this.wheel.get()).booleanValue()) {
                    mc.setScreen((Screen)new EmoteWheelScreen(this, this.key.getKey()));
                } else {
                    this.play();
                }
            }
            this.wasDown = down;
        }
        if (this.burstTicks <= 0 || this.player() == null || EmoteCosmetic.mc.level == null) {
            return;
        }
        --this.burstTicks;
        if (this.burstTicks % 4 != 0) {
            return;
        }
        int color = switch (this.burstEmote) {
            case "\u0412\u043e\u0441\u0442\u043e\u0440\u0433" -> {
                sprite = ParticleSprite.STAR;
                yield -8090;
            }
            case "\u041c\u0443\u0437\u044b\u043a\u0430" -> {
                sprite = ParticleSprite.NOTE;
                yield -8530948;
            }
            case "\u0417\u043b\u043e\u0441\u0442\u044c" -> {
                sprite = ParticleSprite.ANGER;
                yield -50340;
            }
            case "\u0421\u043e\u043d" -> {
                sprite = ParticleSprite.ZZZ;
                yield -3885571;
            }
            default -> {
                sprite = ParticleSprite.HEART;
                yield -41048;
            }
        };
        Random r = AstraParticles.random();
        double x = this.player().getX() + (r.nextDouble() - 0.5) * 0.5;
        double z = this.player().getZ() + (r.nextDouble() - 0.5) * 0.5;
        AstraParticle p = AstraParticles.spawn(sprite, x, this.player().getEyeY() + 0.55, z, 0.0, 0.03, 0.0, 0.16f, color, 34);
        if (p != null) {
            p.drag(0.93f).sway(0.0015f);
        }
    }
}

