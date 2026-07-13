package com.viewmodel.mixin;

import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelItemRenderScope;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemTransformMixin {
    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private void viewmodel$position(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                    Hand hand, float swingProgress, ItemStack stack, float equipProgress,
                                    MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                                    CallbackInfo callback) {
        ViewModelConfig config = ViewModelConfig.current;
        float handSign = hand == Hand.MAIN_HAND ? 1 : -1;
        matrices.translate(config.getPositionX() / 100f * handSign,
            config.getPositionY() / 100f, config.getPositionZ() / 100f);
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
        shift = At.Shift.BEFORE))
    private void viewmodel$scale(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                 Hand hand, float swingProgress, ItemStack stack, float equipProgress,
                                 MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                                 CallbackInfo callback) {
        float scale = ViewModelConfig.current.getSize();
        if (scale != 1.0f) matrices.scale(scale, scale, scale);
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void viewmodel$enterModelRender(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                             MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                                             CallbackInfo callback) {
        ViewModelItemRenderScope.enter();
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void viewmodel$exitModelRender(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                            MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                                            CallbackInfo callback) {
        ViewModelItemRenderScope.exit();
    }
}
