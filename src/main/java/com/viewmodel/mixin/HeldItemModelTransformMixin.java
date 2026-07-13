package com.viewmodel.mixin;

import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelItemRenderScope;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.render.item.ItemRenderState$LayerRenderState")
public abstract class HeldItemModelTransformMixin {
    @Inject(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/render/model/json/Transformation;apply(ZLnet/minecraft/client/util/math/MatrixStack$Entry;)V",
        shift = At.Shift.AFTER))
    private void viewmodel$transformAroundItem(MatrixStack matrices, OrderedRenderCommandQueue queue,
                                               int light, int overlay, int seed, CallbackInfo callback) {
        if (!ViewModelItemRenderScope.isActive()) return;
        ViewModelConfig config = ViewModelConfig.current;
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(config.getRotationPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(config.getRotationYaw()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(config.getRotationRoll()));
    }
}
