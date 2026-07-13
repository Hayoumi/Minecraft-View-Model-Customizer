package com.viewmodel.mixin;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemEquipMixin {
    @Shadow private float mainHandHeight;
    @Shadow private float oMainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private float oOffHandHeight;

    @Inject(method = "tick", at = @At("TAIL"))
    private void viewmodel$lockEquipProgress(CallbackInfo callback) {
        if (!com.viewmodel.ViewModelConfig.current.getSkipEquipAnimation()) return;
        mainHandHeight = oMainHandHeight = 1;
        offHandHeight = oOffHandHeight = 1;
    }

    @Inject(method = "shouldInstantlyReplaceVisibleItem", at = @At("HEAD"), cancellable = true)
    private void viewmodel$skipSwapAnimation(ItemStack oldStack, ItemStack newStack,
                                             CallbackInfoReturnable<Boolean> callback) {
        if (!com.viewmodel.ViewModelConfig.current.getSkipEquipAnimation()) return;
        callback.setReturnValue(true);
    }
}
