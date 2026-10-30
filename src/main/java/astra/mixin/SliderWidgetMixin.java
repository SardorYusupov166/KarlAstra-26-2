/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSliderButton
 *  net.minecraft.client.gui.components.AbstractWidget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package astra.mixin;

import astra.gui.vanilla.AstraWidgets;
import astra.mixin.ClickableWidgetAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AbstractSliderButton.class})
public abstract class SliderWidgetMixin {
    @Shadow
    protected double field_22753;

    @Inject(method={"method_48579"}, at={@At(value="HEAD")}, cancellable=true)
    private void astra$render(GuiGraphics ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!AstraWidgets.enabled()) {
            return;
        }
        AbstractWidget w = (AbstractWidget)this;
        AstraWidgets.slider(ctx, this, w.method_46426(), w.method_46427(), w.method_25368(), w.method_25364(), w.method_25369(), this.field_22753, w.field_22763, w.method_25367(), ((ClickableWidgetAccessor)w).astra$getAlpha());
        ci.cancel();
    }
}

