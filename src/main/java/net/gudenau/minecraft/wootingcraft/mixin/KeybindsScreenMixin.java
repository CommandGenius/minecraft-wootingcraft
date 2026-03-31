package net.gudenau.minecraft.wootingcraft.mixin;

import net.gudenau.minecraft.wootingcraft.impl.KeybindsScreenState;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBindsScreen.class)
public abstract class KeybindsScreenMixin extends OptionsSubScreen {
    @Shadow @Nullable public KeyMapping selectedKey;

    @Unique
    private KeybindsScreenState gud_wootingcraft$state;

    public KeybindsScreenMixin() {
        super(null, null, null);
        throw new AssertionError();
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void init(Screen parent, Options gameOptions, CallbackInfo ci) {
        gud_wootingcraft$state = new KeybindsScreenState();
        for(KeyMapping binding : gameOptions.keyMappings) {
            var boundKey = ((KeyBindingAccessor) binding).getKey();
            gud_wootingcraft$state.incrementKey(boundKey);
        }
        gud_wootingcraft$state.flush();
    }

    @Inject(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
            shift = At.Shift.BEFORE
        )
    )
    private void mouseClickedUnbind(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        var boundKey = ((KeyBindingAccessor) selectedKey).getKey();
        gud_wootingcraft$state.decrementKey(boundKey);
    }

    @Inject(
        method = "mouseClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
            shift = At.Shift.AFTER
        )
    )
    private void mouseClickedBind(MouseButtonEvent click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        var boundKey = ((KeyBindingAccessor) selectedKey).getKey();
        gud_wootingcraft$state.incrementKey(boundKey);
        gud_wootingcraft$state.flush();
    }

    @Inject(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
            shift = At.Shift.BEFORE
        )
    )
    private void keyPressedUnbind(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        var boundKey = ((KeyBindingAccessor) selectedKey).getKey();
        gud_wootingcraft$state.decrementKey(boundKey);
    }

    @Inject(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V",
            shift = At.Shift.AFTER
        )
    )
    private void keyPressedBind(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        var boundKey = ((KeyBindingAccessor) selectedKey).getKey();
        gud_wootingcraft$state.incrementKey(boundKey);
        gud_wootingcraft$state.flush();
    }

    // private synthetic method_60342(Lnet/minecraft/client/gui/widget/ButtonWidget;)V
    @Inject(
        method = "lambda$addFooter$0(Lnet/minecraft/client/gui/components/Button;)V",
        at = @At("TAIL")
    )
    private void resetAll(Button buttonWidget, CallbackInfo ci) {
        gud_wootingcraft$state.clear();
        for(KeyMapping binding : options.keyMappings) {
            var boundKey = ((KeyBindingAccessor) binding).getKey();
            gud_wootingcraft$state.incrementKey(boundKey);
        }
        gud_wootingcraft$state.flush();
    }

    @Override
    public void onClose() {
        super.onClose();
        gud_wootingcraft$state.close();
    }
}
