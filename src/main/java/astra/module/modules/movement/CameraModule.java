/*
 * Decompiled with CFR 0.152.
 */
package astra.module.modules.movement;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.module.modules.movement.ZoomModule;
import astra.render.Icon;

public class CameraModule
extends Module {
    public CameraModule() {
        super("smooth_camera", "\u041f\u043b\u0430\u0432\u043d\u0430\u044f \u043a\u0430\u043c\u0435\u0440\u0430", "\u0423\u043c\u0435\u043d\u044c\u0448\u0430\u0435\u0442 \u0440\u0435\u0437\u043a\u0438\u0435 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u044f \u043a\u0430\u043c\u0435\u0440\u044b (\u0448\u0442\u0430\u0442\u043d\u0430\u044f \u043a\u0438\u043d\u043e\u043a\u0430\u043c\u0435\u0440\u0430).", Category.MOVEMENT, Icon.CAMERA);
    }

    @Override
    protected void onEnable() {
        CameraModule.mc.field_1690.field_1914 = true;
    }

    @Override
    protected void onDisable() {
        ZoomModule zoom = ModuleManager.get(ZoomModule.class);
        if (zoom == null || !zoom.isZooming()) {
            CameraModule.mc.field_1690.field_1914 = false;
        }
    }
}

