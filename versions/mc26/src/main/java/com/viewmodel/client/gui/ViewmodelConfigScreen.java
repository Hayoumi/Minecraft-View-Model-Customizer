package com.viewmodel.client.gui;

import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelProfile;
import com.viewmodel.ViewModelProfileManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class ViewmodelConfigScreen extends Screen {
    private static final int PROFILE_PADDING = 6;
    private static final int EDITOR_PADDING = 12;

    private final Screen parent;
    private final ViewModelProfileManager profiles = ViewModelConfig.profiles();
    private final NanoVGRenderer renderer = new NanoVGRenderer();
    private final List<NanoPaintable> painted = new ArrayList<>();
    private final List<SliderRow> sliders = new ArrayList<>();

    private EditorLayout layout;
    private ProfileDropdown dropdown;
    private UiToggle noSwing;
    private UiToggle scaleSwing;
    private UiToggle skipEquipAnimation;
    private Component status = Component.empty();
    private int statusTicks;

    public ViewmodelConfigScreen(Screen parent) {
        super(Component.translatable("viewmodel.gui.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearWidgets();
        painted.clear();
        sliders.clear();
        layout = EditorLayout.centered(width, height);
        buildProfiles();
        buildEditor();
    }

    private void buildProfiles() {
        EditorLayout.Rect card = layout.profiles().inset(PROFILE_PADDING);
        dropdown = add(new ProfileDropdown(card.x(), card.y() + 18, card.width(), profiles.profileNames(),
            profiles.getActiveIndex(), this::selectProfile));

        int gap = 3;
        int buttonWidth = (card.width() - gap * 2) / 3;
        int y = dropdown.getY() + dropdown.getHeight() + 5;
        add(new UiButton(card.x(), y, buttonWidth, 17, Component.translatable("viewmodel.gui.create"),
            UiButton.Style.PRIMARY, this::createProfile));
        add(new UiButton(card.x() + buttonWidth + gap, y, buttonWidth, 17,
            Component.translatable("viewmodel.gui.rename"), UiButton.Style.SECONDARY, this::renameProfile));
        add(new UiButton(card.x() + (buttonWidth + gap) * 2, y, buttonWidth, 17,
            Component.translatable("viewmodel.gui.delete"), UiButton.Style.DANGER, this::deleteProfile));
    }

    private void buildEditor() {
        EditorLayout.Rect card = layout.editor().inset(EDITOR_PADDING);
        int y = layout.editor().y() + 38;
        for (ViewModelOption option : ViewModelOption.values()) {
            addSlider(card, option, y);
            y += 20;
        }

        int resetY = layout.editor().y() + 179;
        add(new UiButton(card.x(), resetY, card.width(), 16, Component.translatable("viewmodel.gui.reset"),
            UiButton.Style.WIDE, this::resetProfile));

        int toggleGap = 8;
        int toggleWidth = (card.width() - toggleGap * 2) / 3;
        int toggleY = resetY + 28;
        noSwing = add(new UiToggle(card.x(), toggleY, toggleWidth, Component.translatable("viewmodel.gui.no_swing"),
            () -> ViewModelConfig.current.getNoSwing(), value -> update(() -> ViewModelConfig.current.setNoSwing(value))));
        scaleSwing = add(new UiToggle(card.x() + toggleWidth + toggleGap, toggleY, toggleWidth,
            Component.translatable("viewmodel.gui.scale_swing"), () -> ViewModelConfig.current.getScaleSwing(),
            value -> update(() -> ViewModelConfig.current.setScaleSwing(value))));
        skipEquipAnimation = add(new UiToggle(card.x() + (toggleWidth + toggleGap) * 2, toggleY, toggleWidth,
            Component.translatable("viewmodel.gui.no_equip"), () -> ViewModelConfig.current.getSkipEquipAnimation(),
            value -> update(() -> ViewModelConfig.current.setSkipEquipAnimation(value))));
    }

    private void addSlider(EditorLayout.Rect card, ViewModelOption option, int y) {
        int resetX = card.x();
        int sliderX = resetX + 27;
        UiSlider slider = add(new UiSlider(sliderX, y, card.width() - 27, option.min, option.max, option.step,
            option.value.getAsDouble(), value -> update(() -> option.setter.accept(value))));
        add(new ResetIconButton(resetX, y - 2, () -> slider.reset(option.baseline)));
        sliders.add(new SliderRow(option, slider, sliderX, y - 4, card.right()));
    }

    private <T extends net.minecraft.client.gui.components.events.GuiEventListener & net.minecraft.client.gui.components.Renderable & net.minecraft.client.gui.narration.NarratableEntry & NanoPaintable> T add(T widget) {
        addRenderableWidget(widget);
        painted.add(widget);
        return widget;
    }

    private void update(Runnable mutation) {
        mutation.run();
        profiles.updateActiveFromConfig();
    }

    private void createProfile() {
        minecraft.setScreen(new ProfileNameScreen(this, Component.literal("Create profile"), "", name -> {
            if (name.isBlank()) return Component.literal("Name cannot be empty");
            var created = profiles.create(name);
            setStatus(Component.literal("Created " + created.name()));
            rebuildWidgets();
            return null;
        }));
    }

    private void renameProfile() {
        minecraft.setScreen(new ProfileNameScreen(this, Component.literal("Rename profile"), profiles.getActiveProfile().name(), name -> {
            if (name.isBlank()) return Component.literal("Name cannot be empty");
            if (!profiles.renameActive(name)) return Component.literal("Name already exists");
            setStatus(Component.literal("Renamed to " + profiles.getActiveProfile().name()));
            rebuildWidgets();
            return null;
        }));
    }

    private void deleteProfile() {
        if (!profiles.deleteActive()) {
            setStatus(Component.literal("Cannot delete the last profile").withStyle(ChatFormatting.RED));
            return;
        }
        setStatus(Component.literal("Profile deleted"));
        rebuildWidgets();
    }

    private void selectProfile(int index) {
        if (index == profiles.getActiveIndex()) return;
        profiles.select(index);
        setStatus(Component.literal("Switched to " + profiles.getActiveProfile().name()));
        rebuildWidgets();
    }

    private void resetProfile() {
        ViewModelProfile.baseline().apply(ViewModelConfig.current);
        profiles.updateActiveFromConfig();
        sliders.forEach(row -> row.slider.sync(row.option.value.getAsDouble()));
        noSwing.refresh();
        scaleSwing.refresh();
        skipEquipAnimation.refresh();
        setStatus(Component.literal("Profile reset"));
    }

    private void setStatus(Component text) {
        status = text;
        statusTicks = 80;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    public void renderNanoLayer() {
        renderer.render(canvas -> {
            canvas.roundedRect(0, 0, width, height, 0, UiTheme.BACKDROP);
            canvas.panel(layout.profiles());
            canvas.panel(layout.editor());
            painted.forEach(widget -> widget.paint(canvas));
            if (dropdown.isOpen()) dropdown.paintOverlay(canvas);
        });
        NativeTextRenderer text = new NativeTextRenderer();
        for (NanoPaintable widget : painted) {
            if (dropdown.isOpen() && widget instanceof UiButton button
                && button.getY() < layout.profiles().bottom()) continue;
            widget.paintText(text);
        }
        drawLabels(text);
        if (dropdown.isOpen()) dropdown.paintOverlayText(text);
        text.flush();
    }

    private void drawLabels(NativeTextRenderer canvas) {
        EditorLayout.Rect profilesCard = layout.profiles().inset(PROFILE_PADDING);
        EditorLayout.Rect editorCard = layout.editor().inset(EDITOR_PADDING);
        canvas.textLeft(Component.translatable("viewmodel.gui.configs").getString().toUpperCase(), profilesCard.x(), profilesCard.y() + 5, 9f, UiTheme.TEXT);
        String title = profiles.getActiveProfile().name().toUpperCase();
        canvas.textLeft(title, editorCard.x(), layout.editor().y() + 16, 9f, UiTheme.TEXT);

        for (SliderRow row : sliders) {
            canvas.textLeft(row.option.label, row.labelX, row.labelY, 9f, UiTheme.TEXT);
            String value = row.slider.formattedValue();
            canvas.textRight(value, row.valueRight, row.labelY + 1, 9f, UiTheme.TEXT);
        }
        if (statusTicks > 0 && !status.getString().isEmpty()) {
            canvas.textLeft(status.getString(), profilesCard.x(), layout.profiles().bottom() + 8, 8f, UiTheme.ACCENT);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (dropdown.isOpen() && !dropdown.isMouseOver(click.x(), click.y())) dropdown.closeMenu();
        return super.mouseClicked(click, doubled);
    }

    @Override public void tick() { super.tick(); if (statusTicks > 0) statusTicks--; }
    @Override public void onClose() { minecraft.setScreen(parent); }
    @Override public void removed() { ViewModelConfig.save(); renderer.close(); }

    private record SliderRow(ViewModelOption option, UiSlider slider, int labelX, int labelY, int valueRight) {}
}
