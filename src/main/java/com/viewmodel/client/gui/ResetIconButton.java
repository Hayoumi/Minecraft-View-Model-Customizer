package com.viewmodel.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

final class ResetIconButton extends ClickableWidget implements NanoPaintable {
    private final Runnable action;

    ResetIconButton(int x, int y, Runnable action) {
        super(x, y, 16, 16, Text.literal("Reset"));
        this.action = action;
    }

    @Override
    public void paint(NanoVGRenderer.Canvas canvas) {
        canvas.roundedRect(getX(), getY(), getWidth(), getHeight(), 2, UiTheme.CONTROL_BORDER);
        canvas.roundedRect(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 1,
            isHovered() ? UiTheme.ACCENT_HOVER : UiTheme.ACCENT);
        canvas.resetIcon(getX() + 2, getY() + 2, getWidth() - 4, UiTheme.WHITE);
    }

    @Override protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {}
    @Override public void onClick(Click click, boolean doubled) { action.run(); }
    @Override protected void appendClickableNarrations(NarrationMessageBuilder builder) { appendDefaultNarrations(builder); }
}
