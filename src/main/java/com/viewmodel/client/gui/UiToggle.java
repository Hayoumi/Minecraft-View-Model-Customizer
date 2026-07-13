package com.viewmodel.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

final class UiToggle extends ClickableWidget implements NanoPaintable {
    private static final int SWITCH_WIDTH = 28;
    private static final int SWITCH_HEIGHT = 10;
    private static final int KNOB_SIZE = 12;
    private final BooleanSupplier source;
    private final Consumer<Boolean> changed;
    private boolean value;

    UiToggle(int x, int y, int width, Text label, BooleanSupplier source, Consumer<Boolean> changed) {
        super(x, y, width, SWITCH_HEIGHT, label);
        this.source = source;
        this.changed = changed;
        refresh();
    }

    @Override
    public void paint(NanoVGRenderer.Canvas canvas) {
        int switchX = getX() + getWidth() - SWITCH_WIDTH;
        int trackColor = value ? UiTheme.ACCENT : 0xFF343435;
        canvas.roundedRect(switchX, getY(), SWITCH_WIDTH, SWITCH_HEIGHT, SWITCH_HEIGHT / 2f, trackColor);
        int knobX = value ? switchX + SWITCH_WIDTH - KNOB_SIZE + 1 : switchX - 1;
        canvas.roundedRect(knobX, getY() - 1, KNOB_SIZE, KNOB_SIZE, KNOB_SIZE / 2f, UiTheme.WHITE);
    }

    @Override
    public void paintText(NativeTextRenderer text) {
        text.textLeft(getMessage().getString(), getX(), getY() + getHeight() / 2f, 8f, UiTheme.TEXT);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        // Rendered by NanoVG in the final overlay pass.
    }

    @Override public void onClick(Click click, boolean doubled) { value = !value; changed.accept(value); }
    void refresh() { value = source.getAsBoolean(); }
    @Override protected void appendClickableNarrations(NarrationMessageBuilder builder) { appendDefaultNarrations(builder); }
}
