/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.Screenshot
 *  net.minecraft.util.Util
 */
package astra.module.modules.replay;

import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import astra.settings.BooleanSetting;
import astra.settings.ButtonSetting;
import astra.settings.KeybindSetting;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.io.File;
import net.minecraft.client.Screenshot;
import net.minecraft.util.Util;

public class ScreenshotModule
extends Module {
    private final KeybindSetting key = this.add((KeybindSetting)new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430", -1).desc("\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u043e \u043a \u0448\u0442\u0430\u0442\u043d\u043e\u0439 F2"));
    private final BooleanSetting hideHud = this.add(new BooleanSetting("\u0411\u0435\u0437 HUD \u043d\u0430 \u0441\u043d\u0438\u043c\u043a\u0435", false));
    private final ButtonSetting folder = this.add(new ButtonSetting("\u041f\u0430\u043f\u043a\u0430", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043f\u0430\u043f\u043a\u0443 \u0441\u043a\u0440\u0438\u043d\u0448\u043e\u0442\u043e\u0432", ScreenshotModule::openFolder));
    private boolean wasDown;
    private int pending = -1;
    private boolean hudWasHidden;

    public ScreenshotModule() {
        super("screenshots", "\u0421\u043a\u0440\u0438\u043d\u0448\u043e\u0442\u044b", "\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 \u0441\u043a\u0440\u0438\u043d\u0448\u043e\u0442\u043e\u0432 \u0438 \u0433\u0430\u043b\u0435\u0440\u0435\u044f.", Category.REPLAYS, Icon.CAMERA);
        this.alwaysOn();
    }

    public static File screenshotsDir() {
        return new File(ScreenshotModule.mc.field_1697, "screenshots");
    }

    public static void openFolder() {
        File dir = ScreenshotModule.screenshotsDir();
        dir.mkdirs();
        Util.method_668().method_672(dir);
    }

    public void requestShot() {
        this.pending = 3;
    }

    @Override
    public void onTick() {
        boolean down;
        boolean bl = down = ScreenshotModule.mc.field_1755 == null && this.key.isBound() && InputConstants.method_15987((Window)mc.method_22683(), (int)this.key.getKey());
        if (down && !this.wasDown) {
            this.requestShot();
        }
        this.wasDown = down;
        if (this.pending < 0) {
            return;
        }
        if (this.pending == 2 && ((Boolean)this.hideHud.get()).booleanValue()) {
            this.hudWasHidden = ScreenshotModule.mc.field_1690.field_1842;
            ScreenshotModule.mc.field_1690.field_1842 = true;
        }
        if (this.pending-- == 0 && ScreenshotModule.mc.field_1755 == null) {
            Screenshot.method_1659((File)ScreenshotModule.mc.field_1697, (RenderTarget)mc.method_1522(), message -> mc.execute(() -> {
                if (ScreenshotModule.mc.field_1705 != null) {
                    ScreenshotModule.mc.field_1705.method_1743().method_1812(message);
                }
            }));
            if (((Boolean)this.hideHud.get()).booleanValue()) {
                ScreenshotModule.mc.field_1690.field_1842 = this.hudWasHidden;
            }
            this.pending = -1;
        }
    }
}

