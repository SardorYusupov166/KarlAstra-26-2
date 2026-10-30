/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package astra.module.modules.movement;

import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.Icon;
import astra.settings.NumberSetting;
import com.mojang.blaze3d.vertex.PoseStack;

public class ArmAnimation
extends Module {
    private final NumberSetting offsetX = this.add(new NumberSetting("\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 X", 0.0, -0.5, 0.5, 0.02));
    private final NumberSetting offsetY = this.add(new NumberSetting("\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 Y", 0.0, -0.5, 0.5, 0.02));
    private final NumberSetting offsetZ = this.add(new NumberSetting("\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 Z", 0.0, -0.5, 0.5, 0.02));
    private final NumberSetting scale = this.add(new NumberSetting("\u0420\u0430\u0437\u043c\u0435\u0440 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u0430", 100.0, 40.0, 150.0, 5.0).unit("%"));
    private final NumberSetting swingSpeed = this.add(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0437\u043c\u0430\u0445\u0430", 100.0, 40.0, 200.0, 10.0).unit("%"));

    public ArmAnimation() {
        super("arm_animation", "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0440\u0443\u043a", "\u041f\u043e\u043b\u043e\u0436\u0435\u043d\u0438\u0435 \u0438 \u0440\u0430\u0437\u043c\u0435\u0440 \u0440\u0443\u043a, \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0432\u0437\u043c\u0430\u0445\u0430.", Category.MOVEMENT, Icon.HAND);
    }

    public static void apply(PoseStack matrices, boolean rightArm) {
        ArmAnimation m = ModuleManager.get(ArmAnimation.class);
        if (m == null || !m.isEnabled()) {
            return;
        }
        float dir = rightArm ? 1.0f : -1.0f;
        matrices.method_46416(m.offsetX.getFloat() * dir, m.offsetY.getFloat(), m.offsetZ.getFloat());
        float s = m.scale.getFloat() / 100.0f;
        matrices.method_22905(s, s, s);
    }

    public static int swingDuration(int vanilla) {
        ArmAnimation m = ModuleManager.get(ArmAnimation.class);
        if (m == null || !m.isEnabled()) {
            return vanilla;
        }
        return Math.max(1, Math.round((float)vanilla * 100.0f / m.swingSpeed.getFloat()));
    }
}

