package com.viewmodel.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.viewmodel.ViewModelConfig;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemSwingMixin {
    @Shadow protected abstract void applyItemArmAttackTransform(PoseStack matrices, HumanoidArm arm, float swingProgress);

    @Inject(method = "swingArm", at = @At("HEAD"), cancellable = true)
    private void viewmodel$disableSwing(float swingProgress, PoseStack matrices,
                                        int armX, HumanoidArm arm, CallbackInfo callback) {
        if (!ViewModelConfig.current.getNoSwing()) return;
        callback.cancel();
        applyItemArmAttackTransform(matrices, arm, swingProgress);
    }

    @Redirect(method = "swingArm", at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void viewmodel$scaleTranslation(PoseStack matrices, float x, float y, float z) {
        float scale = ViewModelConfig.current.getScaleSwing() ? ViewModelConfig.current.getSize() : 1;
        matrices.translate(x * scale, y * scale, z * scale);
    }

    @Inject(method = "applyItemArmAttackTransform", at = @At("HEAD"), cancellable = true)
    private void viewmodel$scaleRotation(PoseStack matrices, HumanoidArm arm, float progress, CallbackInfo callback) {
        if (!ViewModelConfig.current.getScaleSwing()) return;
        int direction = arm == HumanoidArm.RIGHT ? 1 : -1;
        float scale = ViewModelConfig.current.getSize();
        float squared = Mth.sin(progress * progress * (float) Math.PI);
        float rooted = Mth.sin(Mth.sqrt(progress) * (float) Math.PI);
        matrices.mulPose(Axis.YP.rotationDegrees(direction * (45 - squared * 20 * scale)));
        matrices.mulPose(Axis.ZP.rotationDegrees(direction * rooted * -20 * scale));
        matrices.mulPose(Axis.XP.rotationDegrees(rooted * -80 * scale));
        matrices.mulPose(Axis.YP.rotationDegrees(direction * -45));
        callback.cancel();
    }
}
