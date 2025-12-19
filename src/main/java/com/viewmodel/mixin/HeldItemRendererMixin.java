package com.viewmodel.mixin;

import com.viewmodel.ViewModelConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow
    protected abstract void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress);

    @Shadow
    protected abstract void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress);

    @Shadow
    private float equipProgressMainHand;

    @Shadow
    private float prevEquipProgressMainHand;

    @Shadow
    private float equipProgressOffHand;

    @Shadow
    private float prevEquipProgressOffHand;

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.AFTER
            )
    )
    private void viewmodel$applyOffsets(
            AbstractClientPlayerEntity player,
            float tickDelta,
            float pitch,
            Hand hand,
            float swingProgress,
            ItemStack stack,
            float equipProgress,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        ViewModelConfig config = ViewModelConfig.current;

        float x = config.getPositionX();
        float y = config.getPositionY();
        float z = config.getPositionZ();
        if (x != 0.0f || y != 0.0f || z != 0.0f) {
            float handSign = hand == Hand.MAIN_HAND ? 1.0f : -1.0f;
            matrices.translate((x / 100.0f) * handSign, y / 100.0f, z / 100.0f);
        }

        float rotX = config.getRotationPitch();
        float rotY = config.getRotationYaw();
        float rotZ = config.getRotationRoll();
        if (rotX != 0.0f || rotY != 0.0f || rotZ != 0.0f) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        }
    }

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void viewmodel$applyScale(
            AbstractClientPlayerEntity player,
            float tickDelta,
            float pitch,
            Hand hand,
            float swingProgress,
            ItemStack stack,
            float equipProgress,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        float scale = ViewModelConfig.current.getSize();
        if (scale != 1.0f) {
            matrices.scale(scale, scale, scale);
        }
    }

    @Inject(
            method = "swingArm",
            at = @At("HEAD"),
            cancellable = true
    )
    private void viewmodel$odinNoSwing(
            float swingProgress,
            float equipProgress,
            MatrixStack matrices,
            int armX,
            Arm arm,
            CallbackInfo ci
    ) {
        if (!ViewModelConfig.current.getNoSwing()) {
            return;
        }
        ci.cancel();
        applyEquipOffset(matrices, arm, equipProgress);
        applySwingOffset(matrices, arm, swingProgress);
    }

    @Redirect(
            method = "swingArm",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"
            )
    )
    private void viewmodel$scaleSwingTranslate(MatrixStack matrices, float x, float y, float z) {
        float scale = ViewModelConfig.current.getScaleSwing() ? ViewModelConfig.current.getSize() : 1.0f;
        matrices.translate(x * scale, y * scale, z * scale);
    }

    @Redirect(
            method = "updateHeldItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"
            )
    )
    private float viewmodel$forceAttackCooldown(ClientPlayerEntity player, float tickDelta) {
        return 1.0F;
    }

    @Inject(method = "updateHeldItems", at = @At("TAIL"))
    private void viewmodel$lockEquipProgress(CallbackInfo ci) {
        equipProgressMainHand = 1.0f;
        prevEquipProgressMainHand = 1.0f;
        equipProgressOffHand = 1.0f;
        prevEquipProgressOffHand = 1.0f;
    }

    @Inject(
            method = "shouldSkipHandAnimationOnSwap",
            at = @At("HEAD"),
            cancellable = true
    )
    private void viewmodel$skipSwapAnimation(ItemStack oldItem, ItemStack newItem, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(
            method = "applySwingOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private void viewmodel$scaleSwingRotation(MatrixStack matrices, Arm arm, float swingProgress, CallbackInfo ci) {
        if (!ViewModelConfig.current.getScaleSwing()) {
            return;
        }

        int dir = (arm == Arm.RIGHT) ? 1 : -1;
        float scale = ViewModelConfig.current.getSize();

        float sinSq = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float sinSqrt = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(dir * (45.0F + (sinSq * -20.0F * scale))));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(dir * (sinSqrt * -20.0F * scale)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sinSqrt * -80.0F * scale));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(dir * -45.0F));

        ci.cancel();
    }
}
