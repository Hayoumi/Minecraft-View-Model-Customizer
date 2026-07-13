package com.viewmodel.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;

final class UiButton extends ClickableWidget implements NanoPaintable {
    enum Style { PRIMARY, SECONDARY, DANGER, WIDE }

    private final Runnable action;
    private final Style style;

    UiButton(int x, int y, int width, int height, Text label, Style style, Runnable action) {
        super(x, y, width, height, label);
        this.style = style;
        this.action = action;
    }

    @Override
    public void paint(NanoVGRenderer.Canvas canvas) {
        int base = switch (style) {
            case PRIMARY -> UiTheme.ACCENT_SOFT;
            case DANGER -> UiTheme.DANGER;
            default -> UiTheme.SURFACE;
        };
        int fill = isHovered() ? switch (style) {
            case PRIMARY -> UiTheme.ACCENT;
            case DANGER -> UiTheme.DANGER_HOVER;
            default -> UiTheme.SURFACE_HOVER;
        } : base;
        if (style == Style.WIDE) {
            canvas.roundedRect(getX(), getY(), getWidth(), getHeight(), 6, UiTheme.CONTROL_BORDER);
            canvas.verticalGradient(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, 5,
                isHovered() ? 0xFF424243 : 0xFF373738, isHovered() ? 0xFF323234 : 0xFF2C2C2D);
        } else {
            canvas.roundedRect(getX(), getY(), getWidth(), getHeight(), UiTheme.CONTROL_RADIUS, UiTheme.CONTROL_BORDER);
            canvas.roundedRect(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2,
                UiTheme.CONTROL_RADIUS - 1, fill);
        }
    }

    @Override
    public void paintText(NativeTextRenderer text) {
        text.textCenter(getMessage().getString(), getX() + getWidth() / 2f, getY() + getHeight() / 2f,
            style == Style.WIDE ? 9f : 8f, UiTheme.TEXT);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // NanoVG owns all visuals; the widget only handles input and narration.
    }

    @Override public void onClick(Click click, boolean doubled) { action.run(); }
    @Override protected void appendClickableNarrations(NarrationMessageBuilder builder) { appendDefaultNarrations(builder); }
}
