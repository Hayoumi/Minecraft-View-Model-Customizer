package com.viewmodel.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelItemRenderScope;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.item.ItemStackRenderState$LayerRenderState")
public abstract class HeldItemModelTransformMixin {
    @Inject(method = "applyTransform", at = @At("TAIL"))
    private void viewmodel$transformAroundItem(PoseStack.Pose pose, CallbackInfo callback) {
        if (!ViewModelItemRenderScope.isActive()) return;
        ViewModelConfig config = ViewModelConfig.current;
        pose.rotate(Axis.XP.rotationDegrees(config.getRotationPitch()));
        pose.rotate(Axis.YP.rotationDegrees(config.getRotationYaw()));
        pose.rotate(Axis.ZP.rotationDegrees(config.getRotationRoll()));
    }
}
