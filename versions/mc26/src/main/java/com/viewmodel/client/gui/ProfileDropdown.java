package com.viewmodel.client.gui;

import java.util.List;
import java.util.function.IntConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

final class ProfileDropdown extends AbstractWidget implements NanoPaintable {
    private static final int MAX_VISIBLE = 5;
    private final List<String> entries;
    private final IntConsumer selected;
    private int selectedIndex;
    private int scrollOffset;
    private boolean open;

    ProfileDropdown(int x, int y, int width, List<String> entries, int selectedIndex, IntConsumer selected) {
        super(x, y, width, 16, Component.literal("Profiles"));
        this.entries = List.copyOf(entries);
        this.selectedIndex = entries.isEmpty() ? -1 : Mth.clamp(selectedIndex, 0, entries.size() - 1);
        this.selected = selected;
    }

    @Override
    public void paint(NanoVGRenderer.Canvas canvas) {
        canvas.roundedRect(getX(), getY(), getWidth(), getHeight(), UiTheme.CONTROL_RADIUS, UiTheme.CONTROL_BORDER);
        canvas.roundedRect(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2,
            UiTheme.CONTROL_RADIUS - 1, isHovered() ? UiTheme.SURFACE_HOVER : UiTheme.SURFACE);
        canvas.chevron(getX() + getWidth() - 15, getY() + getHeight() / 2f, 6, open, UiTheme.ACCENT);
    }

    void paintOverlay(NanoVGRenderer.Canvas canvas) {
        if (!open) return;
        int visible = Math.min(MAX_VISIBLE, entries.size());
        int overlayY = getY() + getHeight() + 2;
        int height = visible * getHeight() + 2;
        canvas.roundedRect(getX(), overlayY, getWidth(), height, 2, UiTheme.CONTROL_BORDER);
        canvas.roundedRect(getX() + 1, overlayY + 1, getWidth() - 2, height - 2, 1, UiTheme.SURFACE);
        for (int row = 0; row < visible; row++) {
            int index = scrollOffset + row;
            if (index != selectedIndex) continue;
            int y = overlayY + 1 + row * getHeight();
            canvas.roundedRect(getX() + 1, y, getWidth() - 2, getHeight(), 0, UiTheme.SURFACE_HOVER);
            canvas.roundedRect(getX() + 1, y, 2, getHeight(), 0, UiTheme.ACCENT);
        }
    }

    @Override
    public void paintText(NativeTextRenderer text) {
        text.textLeft(label(selectedIndex), getX() + 7, getY() + getHeight() / 2f, 8.5f, UiTheme.TEXT);
    }

    void paintOverlayText(NativeTextRenderer text) {
        if (!open) return;
        int visible = Math.min(MAX_VISIBLE, entries.size());
        int overlayY = getY() + getHeight() + 2;
        for (int row = 0; row < visible; row++) {
            int y = overlayY + 1 + row * getHeight();
            text.textLeft(label(scrollOffset + row), getX() + 7, y + getHeight() / 2f, 8.5f, UiTheme.TEXT);
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // Rendered by NanoVG in the final overlay pass.
    }

    void extractOverlayText(GuiGraphicsExtractor context) {
        // Rendered by NanoVG in the final overlay pass.
    }

    private void drawEntry(GuiGraphicsExtractor context, int y, int index) {
        // Kept for binary compatibility with older callers; no immediate rendering.
    }

    private String label(int index) {
        return index >= 0 && index < entries.size() ? entries.get(index) : "None";
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        double mouseY = click.y();
        if (!open) { open = true; return; }
        int overlayY = getY() + getHeight() + 2;
        if (mouseY >= overlayY + 1) {
            int row = (int) ((mouseY - overlayY - 1) / getHeight());
            int index = scrollOffset + row;
            if (index >= 0 && index < entries.size()) selected.accept(index);
        }
        open = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (!open || !isMouseOver(mouseX, mouseY)) return false;
        int maximum = Math.max(0, entries.size() - MAX_VISIBLE);
        scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(vertical), 0, maximum);
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        int rows = open ? Math.min(MAX_VISIBLE, entries.size()) : 0;
        return mouseX >= getX() && mouseX <= getX() + getWidth()
            && mouseY >= getY() && mouseY <= getY() + getHeight() * (rows + 1) + (open ? 4 : 0);
    }

    boolean isOpen() { return open; }
    void closeMenu() { open = false; }
    @Override protected void updateWidgetNarration(NarrationElementOutput builder) { defaultButtonNarrationText(builder); }
}
