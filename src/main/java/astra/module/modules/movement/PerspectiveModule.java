/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.CameraType
 */
package astra.module.modules.movement;

import astra.module.Category;
import astra.module.Module;
import astra.render.Icon;
import astra.settings.KeybindSetting;
import astra.settings.ModeSetting;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.CameraType;

public class PerspectiveModule
extends Module {
    private final KeybindSetting key = this.add(new KeybindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430", 86));
    private final ModeSetting mode = this.add(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u0423\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435", "\u0423\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435", "\u0411\u0435\u0437 \u0432\u0438\u0434\u0430 \u0441\u043f\u0435\u0440\u0435\u0434\u0438"));
    private boolean wasDown;
    private CameraType before;

    public PerspectiveModule() {
        super("perspective", "\u041f\u0435\u0440\u0435\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u0432\u0438\u0434\u0430 \u043a\u0430\u043c\u0435\u0440\u044b", "\u0412\u0438\u0434 \u043e\u0442 \u0442\u0440\u0435\u0442\u044c\u0435\u0433\u043e \u043b\u0438\u0446\u0430 \u043f\u043e \u043a\u043b\u0430\u0432\u0438\u0448\u0435.", Category.MOVEMENT, Icon.CAMERA);
        this.setDefaultEnabled(true);
    }

    @Override
    public void onTick() {
        boolean down;
        boolean bl = down = PerspectiveModule.mc.screen == null && this.key.isBound() && InputConstants.isKeyDown((Window)mc.getWindow(), (int)this.key.getKey());
        if (this.mode.is("\u0423\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0435")) {
            if (down && !this.wasDown) {
                this.before = PerspectiveModule.mc.options.getCameraType();
                PerspectiveModule.mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            } else if (!down && this.wasDown && this.before != null) {
                PerspectiveModule.mc.options.setCameraType(this.before);
                this.before = null;
            }
        } else if (down && !this.wasDown) {
            PerspectiveModule.mc.options.setCameraType(PerspectiveModule.mc.options.getCameraType().isFirstPerson() ? CameraType.THIRD_PERSON_BACK : CameraType.FIRST_PERSON);
        }
        this.wasDown = down;
    }

    @Override
    protected void onDisable() {
        if (this.before != null) {
            PerspectiveModule.mc.options.setCameraType(this.before);
            this.before = null;
        }
    }
}

