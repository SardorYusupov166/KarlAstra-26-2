package astra.mixin;
import astra.module.modules.visual.WorldVisuals;
import net.minecraft.client.ClientClockManager;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ClientClockManager.class)
public abstract class ClientWorldPropertiesMixin {
    @Inject(method="getTotalTicks", at=@At("HEAD"), cancellable=true)
    private void astra$timeOfDay(Holder<WorldClock> clock, CallbackInfoReturnable<Long> cir) {
        long time = WorldVisuals.overrideTime();
        if (time >= 0 && clock.is(WorldClocks.OVERWORLD)) cir.setReturnValue(time);
    }
}
