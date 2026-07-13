package com.viewmodel.client.gui;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

final class UiToggle extends AbstractWidget implements NanoPaintable {
    private static final int SWITCH_WIDTH = 28;
    private static final int SWITCH_HEIGHT = 10;
    private static final int KNOB_SIZE = 12;
    private final BooleanSupplier source;
    private final Consumer<Boolean> changed;
    private boolean value;

    UiToggle(int x, int y, int width, Component label, BooleanSupplier source, Consumer<Boolean> changed) {
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // Rendered by NanoVG in the final overlay pass.
    }

    @Override public void onClick(MouseButtonEvent click, boolean doubled) { value = !value; changed.accept(value); }
    void refresh() { value = source.getAsBoolean(); }
    @Override protected void updateWidgetNarration(NarrationElementOutput builder) { defaultButtonNarrationText(builder); }
}
