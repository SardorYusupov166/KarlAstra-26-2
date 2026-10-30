/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.util.Util
 */
package astra.module.modules.replay;

import astra.gui.UiContext;
import astra.gui.components.CardPreview;
import astra.module.Category;
import astra.module.Module;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Render2D;
import astra.settings.ButtonSetting;
import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

public class ReplayModule
extends Module {
    private final ButtonSetting folder = this.add(new ButtonSetting("\u0417\u0430\u043f\u0438\u0441\u0438", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u0437\u0430\u043f\u0438\u0441\u0435\u0439", this::openRecordings));

    public ReplayModule() {
        super("replays", "\u041f\u043e\u0432\u0442\u043e\u0440\u044b", "\u0418\u043d\u0442\u0435\u0433\u0440\u0430\u0446\u0438\u044f \u0441 ReplayMod: \u0441\u0442\u0430\u0442\u0443\u0441 \u0438 \u043f\u0430\u043f\u043a\u0430 \u0437\u0430\u043f\u0438\u0441\u0435\u0439.", Category.REPLAYS, Icon.FILM);
        this.alwaysOn();
    }

    public static boolean replayModLoaded() {
        return FabricLoader.getInstance().isModLoaded("replaymod");
    }

    private File recordingsDir() {
        return new File(ReplayModule.mc.gameDirectory, "replay_recordings");
    }

    private void openRecordings() {
        File dir = this.recordingsDir();
        dir.mkdirs();
        Util.getPlatform().openFile(dir);
    }

    private int recordingCount() {
        File[] files = this.recordingsDir().listFiles((d, n) -> n.endsWith(".mcpr"));
        return files == null ? 0 : files.length;
    }

    @Override
    public CardPreview getPreview() {
        return new CardPreview(){

            @Override
            public float height(float width) {
                return 40.0f;
            }

            @Override
            public void render(UiContext ui, float x, float y, float w, float h) {
                Render2D.roundRect(ui.ctx, x, y, w, h, 5.0f, ColorUtil.withAlpha(ui.theme.bg(), 0.55f));
                boolean loaded = ReplayModule.replayModLoaded();
                Render2D.circle(ui.ctx, x + 8.0f, y + 8.0f, 2.5f, loaded ? -11870592 : -340971);
                Fonts.draw(ui.ctx, loaded ? "ReplayMod \u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0451\u043d" : "ReplayMod \u043d\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d", x + 15.0f, y + 4.5f, 6.8f, ui.theme.text(), true);
                String line = loaded ? "\u0417\u0430\u043f\u0438\u0441\u0435\u0439: " + ReplayModule.this.recordingCount() + " \u00b7 \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0432 \u043c\u0435\u043d\u044e ReplayMod" : "\u0423\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u0435 ReplayMod \u0434\u043b\u044f \u0437\u0430\u043f\u0438\u0441\u0438 \u043f\u043e\u0432\u0442\u043e\u0440\u043e\u0432";
                Fonts.draw(ui.ctx, Fonts.trim(line, w - 12.0f, 6.0f, false), x + 6.0f, y + 16.0f, 6.0f, ui.theme.textMuted(), false);
                Fonts.draw(ui.ctx, "\u0421\u043e\u0431\u0441\u0442\u0432\u0435\u043d\u043d\u044b\u0439 \u0434\u0432\u0438\u0436\u043e\u043a \u043f\u043e\u0432\u0442\u043e\u0440\u043e\u0432 \u2014 \u0441\u043b\u0435\u0434\u0443\u044e\u0449\u0438\u0439 \u044d\u0442\u0430\u043f", x + 6.0f, y + 27.0f, 5.6f, ui.theme.textMuted(), false);
            }
        };
    }
}

