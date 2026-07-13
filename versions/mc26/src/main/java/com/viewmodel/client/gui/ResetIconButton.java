package com.viewmodel.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

final class ResetIconButton extends AbstractWidget implements NanoPaintable {
    private final Runnable action;

    ResetIconButton(int x, int y, Runnable action) {
        super(x, y, 16, 16, Component.literal("Reset"));
        this.action = action;
    }

    @Override
    public void paint(NanoVGRenderer.Canvas canvas) {
        canvas.roundedRect(getX(), getY(), getWidth(), getHeight(), 2, UiTheme.CONTROL_BORDER);
        canvas.roundedRect(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 1,
            isHovered() ? UiTheme.ACCENT_HOVER : UiTheme.ACCENT);
        canvas.resetIcon(getX() + 2, getY() + 2, getWidth() - 4, UiTheme.WHITE);
    }

    @Override protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {}
    @Override public void onClick(MouseButtonEvent click, boolean doubled) { action.run(); }
    @Override protected void updateWidgetNarration(NarrationElementOutput builder) { defaultButtonNarrationText(builder); }
}
