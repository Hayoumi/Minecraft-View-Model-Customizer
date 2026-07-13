package com.viewmodel.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.viewmodel.client.gui.ViewmodelConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/gui/render/GuiRenderer;draw(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
        shift = At.Shift.AFTER))
    private void viewmodel$renderNanoLayer(GpuBufferSlice projection, CallbackInfo callback) {
        if (Minecraft.getInstance().screen instanceof ViewmodelConfigScreen screen) {
            screen.renderNanoLayer();
        }
    }
}
