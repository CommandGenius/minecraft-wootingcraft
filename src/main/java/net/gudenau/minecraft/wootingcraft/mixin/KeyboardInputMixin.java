package net.gudenau.minecraft.wootingcraft.mixin;

import net.gudenau.minecraft.wootingcraft.api.AnalogKeyBinding;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {
    @Shadow @Final private Options options;

    @Unique
    private static float gud_wootingcraft$getMovement(@NotNull KeyMapping positive, @NotNull KeyMapping negative) {
        if(positive instanceof AnalogKeyBinding analogPositive && negative instanceof AnalogKeyBinding analogNegative) {
            return analogPositive.pressedAmount() - analogNegative.pressedAmount();
        }

        var positivePressed = positive.isDown();
        var negativePressed = negative.isDown();
        if(positivePressed == negativePressed) {
            return 0;
        } else {
            return positivePressed ? 1 : -1;
        }
    }

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        cancellable = true
    )
    private void tick(CallbackInfo ci) {
        AnalogKeyBinding.setAll();
        var longMovement = gud_wootingcraft$getMovement(options.keyUp, options.keyDown);
        var latMovement = gud_wootingcraft$getMovement(options.keyLeft, options.keyRight);

        keyPresses = new Input(
            longMovement > 0,
            longMovement < 0,
            latMovement > 0,
            latMovement < 0,
            options.keyJump.isDown(),
            options.keyShift.isDown(),
            options.keySprint.isDown()
        );

        moveVector = (new Vec2(latMovement, longMovement)).normalized();
        
        Vec2 inputVector = new Vec2(latMovement, longMovement);
        ((InputAccessor)this).setMoveVector(inputVector);

        ci.cancel();
    }
}
