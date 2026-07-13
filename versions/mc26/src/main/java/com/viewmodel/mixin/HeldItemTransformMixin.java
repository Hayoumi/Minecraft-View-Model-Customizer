package com.viewmodel.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelItemRenderScope;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemTransformMixin {
    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER))
    private void viewmodel$position(AbstractClientPlayer player, float tickDelta, float pitch,
                                    InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress,
                                    PoseStack matrices, SubmitNodeCollector queue, int light,
                                    CallbackInfo callback) {
        // Empty hands are rendered through the same matrix scope; keeping the item offset here
        // would make the vanilla arm jump forward when the selected hand has no stack.
        if (stack.isEmpty()) return;
        ViewModelConfig config = ViewModelConfig.current;
        float handSign = hand == InteractionHand.MAIN_HAND ? 1 : -1;
        matrices.translate(config.getPositionX() / 100f * handSign,
            config.getPositionY() / 100f, config.getPositionZ() / 100f);
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
        shift = At.Shift.BEFORE))
    private void viewmodel$scale(AbstractClientPlayer player, float tickDelta, float pitch,
                                 InteractionHand hand, float swingProgress, ItemStack stack, float equipProgress,
                                 PoseStack matrices, SubmitNodeCollector queue, int light,
                                 CallbackInfo callback) {
        float scale = ViewModelConfig.current.getSize();
        if (scale != 1.0f) matrices.scale(scale, scale, scale);
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void viewmodel$enterModelRender(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                             PoseStack matrices, SubmitNodeCollector queue, int light,
                                             CallbackInfo callback) {
        ViewModelItemRenderScope.enter();
    }

    @Inject(method = "renderItem", at = @At("RETURN"))
    private void viewmodel$exitModelRender(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext,
                                            PoseStack matrices, SubmitNodeCollector queue, int light,
                                            CallbackInfo callback) {
        ViewModelItemRenderScope.exit();
    }
}
