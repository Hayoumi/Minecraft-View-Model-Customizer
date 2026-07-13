package com.viewmodel.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

final class NativeTextRenderer {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final FontDescription.Resource UI_FONT = new FontDescription.Resource(
        Identifier.fromNamespaceAndPath("viewmodel", "ui"));
    private static final FontDescription.Resource UI_FONT_SMALL = new FontDescription.Resource(
        Identifier.fromNamespaceAndPath("viewmodel", "ui_small"));

    private final Font font = Minecraft.getInstance().font;
    private final MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();

    void textLeft(String text, float x, float centerY, float size, int color) {
        draw(text, x, centerY, size, color, 0);
    }

    void textCenter(String text, float centerX, float centerY, float size, int color) {
        draw(text, centerX, centerY, size, color, 1);
    }

    void textRight(String text, float rightX, float centerY, float size, int color) {
        draw(text, rightX, centerY, size, color, 2);
    }

    private void draw(String text, float anchorX, float centerY, float size, int color, int alignment) {
        FontDescription.Resource selectedFont = size <= 8.5f ? UI_FONT_SMALL : UI_FONT;
        Component styled = Component.literal(text).withStyle(style -> style.withFont(selectedFont));
        float width = font.width(styled);
        float x = switch (alignment) {
            case 1 -> anchorX - width / 2;
            case 2 -> anchorX - width;
            default -> anchorX;
        };
        float y = centerY - font.lineHeight / 2f;
        Matrix4f matrix = new Matrix4f().translation(0, 0, -11000);
        font.drawInBatch(styled, x, y, color, false, matrix, buffers,
            Font.DisplayMode.NORMAL, 0, FULL_BRIGHT);
    }

    void flush() {
        buffers.endBatch();
    }
}
