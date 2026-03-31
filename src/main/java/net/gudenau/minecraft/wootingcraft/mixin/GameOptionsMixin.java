package net.gudenau.minecraft.wootingcraft.mixin;

import net.gudenau.minecraft.wootingcraft.api.AnalogKeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Options.class)
public abstract class GameOptionsMixin {
    /**
     * Redirects the movement keys to support analog inputs.
     */
    @Redirect(
        method = "<init>",
        at = @At(
            value = "NEW",
            target = "net/minecraft/client/KeyMapping"
        ),
        slice = @Slice(
            to = @At(
                value = "CONSTANT",
                args = "stringValue=key.right"
            )
        ),
        require = 4,
        expect = 4,
        allow = 4
    )
    private KeyMapping createAnalogBindings(String translationKey, int code, KeyMapping.Category category) {
        return new AnalogKeyBinding(translationKey, code, category);
    }
}
