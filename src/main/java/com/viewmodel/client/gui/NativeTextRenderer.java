package com.viewmodel.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

final class NativeTextRenderer {
    private static final int FULL_BRIGHT = 0xF000F0;
    private static final StyleSpriteSource.Font UI_FONT = new StyleSpriteSource.Font(Identifier.of("viewmodel", "ui"));
    private static final StyleSpriteSource.Font UI_FONT_SMALL = new StyleSpriteSource.Font(Identifier.of("viewmodel", "ui_small"));

    private final TextRenderer font = MinecraftClient.getInstance().textRenderer;
    private final VertexConsumerProvider.Immediate buffers = MinecraftClient.getInstance()
        .getBufferBuilders().getEntityVertexConsumers();

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
        StyleSpriteSource.Font selectedFont = size <= 8.5f ? UI_FONT_SMALL : UI_FONT;
        Text styled = Text.literal(text).styled(style -> style.withFont(selectedFont));
        float width = font.getWidth(styled);
        float x = switch (alignment) {
            case 1 -> anchorX - width / 2;
            case 2 -> anchorX - width;
            default -> anchorX;
        };
        float y = centerY - font.fontHeight / 2f;
        Matrix4f matrix = new Matrix4f().translation(0, 0, -11000);
        font.draw(styled, x, y, color, false, matrix, buffers,
            TextRenderer.TextLayerType.NORMAL, 0, FULL_BRIGHT);
    }

    void flush() {
        buffers.draw();
    }
}
