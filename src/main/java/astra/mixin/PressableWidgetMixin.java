/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ActiveTextCollector
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.SpriteIconButton$CenteredIcon
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.gui.vanilla.AstraWidgets;
import astra.mixin.ClickableWidgetAccessor;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractButton.class})
public abstract class PressableWidgetMixin {
    @Unique
    private boolean astra$labelDrawn;

    @Inject(method={"method_75794"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$background(GuiGraphics ctx, CallbackInfo ci) {
        if (!AstraWidgets.enabled()) {
            return;
        }
        AbstractWidget w = (AbstractWidget)this;
        boolean iconOnly = this instanceof SpriteIconButton.CenteredIcon;
        AstraWidgets.button(ctx, this, w.method_46426(), w.method_46427(), w.method_25368(), w.method_25364(), (Component)(iconOnly ? Component.method_43473() : w.method_25369()), w.field_22763, w.method_25367(), ((ClickableWidgetAccessor)w).astra$getAlpha());
        this.astra$labelDrawn = true;
        ci.cancel();
    }

    @Inject(method={"method_75793"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$label(ActiveTextCollector drawer, CallbackInfo ci) {
        if (this.astra$labelDrawn) {
            this.astra$labelDrawn = false;
            ci.cancel();
        }
    }
}

