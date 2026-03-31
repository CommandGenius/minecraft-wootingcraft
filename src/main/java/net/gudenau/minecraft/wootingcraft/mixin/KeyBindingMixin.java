package net.gudenau.minecraft.wootingcraft.mixin;

import net.gudenau.minecraft.wootingcraft.api.AnalogKeyBinding;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public abstract class KeyBindingMixin {
    @Inject(
        method = "releaseAll",
        at = @At("RETURN")
    )
    private static void releaseAll(CallbackInfo ci) {
        AnalogKeyBinding.releaseAll();
    }
}
