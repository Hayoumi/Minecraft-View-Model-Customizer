package com.viewmodel.client.gui;

import org.lwjgl.glfw.GLFW;

import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

final class ProfileNameScreen extends Screen {
    private final Screen parent;
    private final String initialValue;
    private final Function<String, Component> submitter;
    private EditBox field;
    private Component error = Component.empty();

    ProfileNameScreen(Screen parent, Component title, String initialValue, Function<String, Component> submitter) {
        super(title);
        this.parent = parent;
        this.initialValue = initialValue;
        this.submitter = submitter;
    }

    @Override
    protected void init() {
        field = new EditBox(font, width / 2 - 100, height / 2 - 10, 200, 22, Component.empty());
        field.setValue(initialValue);
        field.setResponder(value -> error = Component.empty());
        addRenderableWidget(field);
        setInitialFocus(field);
        addRenderableWidget(Button.builder(Component.literal("Confirm"), button -> submit())
            .bounds(width / 2 - 100, height / 2 + 22, 96, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> onClose())
            .bounds(width / 2 + 4, height / 2 + 22, 96, 20).build());
    }

    private void submit() {
        Component result = submitter.apply(field.getValue().trim());
        if (result == null) minecraft.setScreen(parent);
        else error = result.copy().withStyle(ChatFormatting.RED);
    }

    @Override public void onClose() { minecraft.setScreen(parent); }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_KP_ENTER) { submit(); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, UiTheme.BACKDROP);
        context.centeredText(font, title, width / 2, height / 2 - 42, UiTheme.TEXT);
        super.extractRenderState(context, mouseX, mouseY, delta);
        if (!error.getString().isEmpty())
            context.centeredText(font, error, width / 2, height / 2 + 50, 0xFFFF7777);
    }
}
