package com.viewmodel.mixin;

import com.viewmodel.ViewModelConfig;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemSwingMixin {
    @Shadow protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void viewmodel$disableSwing(float swingProgress, MatrixStack matrices,
                                        int armX, Arm arm, CallbackInfo callback) {
        if (!ViewModelConfig.current.getNoSwing()) return;
        callback.cancel();
        applySwingOffset(matrices, arm, swingProgress);
    }

    @Redirect(method = "swingArm", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
    private void viewmodel$scaleTranslation(MatrixStack matrices, float x, float y, float z) {
        float scale = ViewModelConfig.current.getScaleSwing() ? ViewModelConfig.current.getSize() : 1;
        matrices.translate(x * scale, y * scale, z * scale);
    }

    @Inject(method = "applySwingOffset", at = @At("HEAD"), cancellable = true)
    private void viewmodel$scaleRotation(MatrixStack matrices, Arm arm, float progress, CallbackInfo callback) {
        if (!ViewModelConfig.current.getScaleSwing()) return;
        int direction = arm == Arm.RIGHT ? 1 : -1;
        float scale = ViewModelConfig.current.getSize();
        float squared = MathHelper.sin(progress * progress * (float) Math.PI);
        float rooted = MathHelper.sin(MathHelper.sqrt(progress) * (float) Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(direction * (45 - squared * 20 * scale)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(direction * rooted * -20 * scale));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rooted * -80 * scale));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(direction * -45));
        callback.cancel();
    }
}
