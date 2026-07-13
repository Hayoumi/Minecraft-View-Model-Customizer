package com.viewmodel.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

final class ProfileNameScreen extends Screen {
    private final Screen parent;
    private final String initialValue;
    private final Function<String, Text> submitter;
    private TextFieldWidget field;
    private Text error = Text.empty();

    ProfileNameScreen(Screen parent, Text title, String initialValue, Function<String, Text> submitter) {
        super(title);
        this.parent = parent;
        this.initialValue = initialValue;
        this.submitter = submitter;
    }

    @Override
    protected void init() {
        field = new TextFieldWidget(textRenderer, width / 2 - 100, height / 2 - 10, 200, 22, Text.empty());
        field.setText(initialValue);
        field.setChangedListener(value -> error = Text.empty());
        addDrawableChild(field);
        setInitialFocus(field);
        addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> submit())
            .dimensions(width / 2 - 100, height / 2 + 22, 96, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> close())
            .dimensions(width / 2 + 4, height / 2 + 22, 96, 20).build());
    }

    private void submit() {
        Text result = submitter.apply(field.getText().trim());
        if (result == null) client.setScreen(parent);
        else error = result.copy().formatted(Formatting.RED);
    }

    @Override public void close() { client.setScreen(parent); }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_KP_ENTER) { submit(); return true; }
        return super.keyPressed(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, UiTheme.BACKDROP);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 42, UiTheme.TEXT);
        super.render(context, mouseX, mouseY, delta);
        if (!error.getString().isEmpty())
            context.drawCenteredTextWithShadow(textRenderer, error, width / 2, height / 2 + 50, 0xFFFF7777);
    }
}
