package com.viewmodel.client.gui;

import java.text.DecimalFormat;
import java.util.function.DoubleConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

final class UiSlider extends AbstractSliderButton implements NanoPaintable {
    private static final DecimalFormat FORMAT = new DecimalFormat("+0.00;-0.00");
    private final double min;
    private final double max;
    private final double step;
    private final DoubleConsumer changed;

    UiSlider(int x, int y, int width, double min, double max, double step, double initial, DoubleConsumer changed) {
        super(x, y, width, 12, Component.empty(), normalize(initial, min, max));
        this.min = min;
        this.max = max;
        this.step = step;
        this.changed = changed;
        sync(initial);
    }

    @Override protected void updateMessage() { setMessage(Component.empty()); }

    @Override
    protected void applyValue() {
        double actual = snap(Mth.lerp(value, min, max));
        sync(actual);
        changed.accept(actual);
    }

    @Override
    public void paint(NanoVGRenderer.Canvas canvas) {
        float trackY = getY() + getHeight() / 2f - 2;
        float knobX = (float) (getX() + value * (getWidth() - 8));
        canvas.roundedRect(getX(), trackY, getWidth(), 4, 2, UiTheme.TRACK);
        canvas.roundedRect(getX(), trackY, knobX + 4 - getX(), 4, 2, UiTheme.ACCENT);
        canvas.roundedRect(knobX, trackY - 2, 8, 8, 4, isHovered() ? UiTheme.WHITE : 0xFFF2F2F2);
    }

    @Override public void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {}

    void reset(double actual) { sync(actual); changed.accept(actual); }
    void sync(double actual) { value = Mth.clamp(normalize(actual, min, max), 0, 1); }
    String formattedValue() { return FORMAT.format(Mth.lerp(value, min, max)); }

    private double snap(double actual) { return step <= 0 ? actual : Math.round(actual / step) * step; }
    private static double normalize(double actual, double min, double max) { return (actual - min) / (max - min); }
}
