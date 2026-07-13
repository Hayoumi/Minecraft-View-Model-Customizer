package com.viewmodel.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemEquipMixin {
    @Shadow private float equipProgressMainHand;
    @Shadow private float lastEquipProgressMainHand;
    @Shadow private float equipProgressOffHand;
    @Shadow private float lastEquipProgressOffHand;

    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    private void viewmodel$lockEquipProgress(CallbackInfo callback) {
        if (!com.viewmodel.ViewModelConfig.current.getSkipEquipAnimation()) return;
        equipProgressMainHand = lastEquipProgressMainHand = 1;
        equipProgressOffHand = lastEquipProgressOffHand = 1;
    }

    @Inject(method = "shouldSkipHandAnimationOnSwap", at = @At("HEAD"), cancellable = true)
    private void viewmodel$skipSwapAnimation(ItemStack oldStack, ItemStack newStack,
                                             CallbackInfoReturnable<Boolean> callback) {
        if (!com.viewmodel.ViewModelConfig.current.getSkipEquipAnimation()) return;
        callback.setReturnValue(true);
    }
}
