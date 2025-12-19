package com.viewmodel.client.gui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import com.viewmodel.ViewModelConfig;
import com.viewmodel.ViewModelProfile;
import com.viewmodel.ViewModelProfileManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class ViewmodelConfigScreen extends Screen {
    private static final int LEFT_PANEL_WIDTH = 179;
    private static final int RIGHT_PANEL_WIDTH = 424;
    private static final int LEFT_PANEL_HEIGHT = 109;
    private static final int RIGHT_PANEL_HEIGHT = 310;
    private static final int LEFT_PANEL_PADDING = 12;
    private static final int RIGHT_PANEL_PADDING = 21;
    private static final int LEFT_PANEL_Y_OFFSET = 0;
    private static final int PANEL_SPACING = 13;
    private static final int SLIDER_TOP_OFFSET = 51;
    private static final int SLIDER_ROW_SPACING = 26;
    private static final int SLIDER_GROUP_GAP = 3;
    private static final int SLIDER_LABEL_OFFSET = 9;
    private static final int SLIDER_VALUE_OFFSET = 1;
    private static final int SLIDER_TRACK_HEIGHT = 4;
    private static final int SLIDER_TRACK_RADIUS = 2;
    private static final int SLIDER_KNOB_SIZE = 8;
    private static final int RESET_ICON_SIZE = 20;
    private static final int RESET_ICON_RADIUS = 5;
    private static final int SLIDER_ICON_GAP = 15;
    private static final int VALUE_COLUMN_WIDTH = 2;
    private static final int RESET_PROFILE_BUTTON_HEIGHT = 20;
    private static final int RESET_BUTTON_Y_OFFSET = 249;
    private static final int TOGGLE_HEIGHT = 12;
    private static final int TOGGLE_Y_OFFSET = 288;
    private static final int TOGGLE_GAP = 13;
    private static final int TOGGLE_SWITCH_WIDTH = 32;
    private static final int TOGGLE_LABEL_GAP = 10;
    private static final int PANEL_RADIUS = 6;
    private static final int BUTTON_RADIUS = 4;
    private static final int DROPDOWN_RADIUS = 4;
    private static final int RESET_BUTTON_RADIUS = 6;
    private static final float TEXT_SCALE = 1.0f;
    private static final float VALUE_SCALE = 1.0f;
    private static final float TOGGLE_LABEL_SCALE = 1.15f;
    private static final boolean USE_NANOVG = true;
    private static final boolean USE_NANOVG_ICON = false;
    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat("+0.00;-0.00");
    private static final Identifier TEX_ROUNDED_R8 = Identifier.of("viewmodel", "textures/gui/rounded_r8.png");
    private static final Identifier TEX_ROUNDED_R4 = Identifier.of("viewmodel", "textures/gui/rounded_r4.png");
    private static final Identifier TEX_ROUNDED_R2 = Identifier.of("viewmodel", "textures/gui/rounded_r2.png");
    private static final Identifier TEX_REFRESH = Identifier.of("viewmodel", "textures/gui/znak.png");
    private static final int TEX_REFRESH_WIDTH = 43;
    private static final int TEX_REFRESH_HEIGHT = 41;
    private static final int RESET_ICON_GLYPH_U = 3;
    private static final int RESET_ICON_GLYPH_V = 2;
    private static final int RESET_ICON_GLYPH_REGION = 37;
    private static final int RESET_ICON_GLYPH_WIDTH = 12;
    private static final int RESET_ICON_GLYPH_HEIGHT = 12;
    private static final int TEX_R8_SIZE = 32;
    private static final int TEX_R4_SIZE = 16;
    private static final int TEX_R2_SIZE = 8;
    private static final int COLOR_BACKDROP = 0x80101012;
    private static final int COLOR_PANEL_BG = 0xFF171718;
    private static final int COLOR_PANEL_BORDER = 0xFF0F101A;
    private static final int COLOR_PANEL_SHADOW = 0x55000000;
    private static final int COLOR_TEXT_PRIMARY = 0xFFC5C5C5;
    private static final int COLOR_TEXT_MUTED = 0xFF696969;
    private static final int COLOR_ACCENT = 0xFF8F62EC;
    private static final int COLOR_ACCENT_HOVER = 0xFFA174F0;
    private static final int COLOR_TRACK_BG = 0xFF38393A;
    private static final int COLOR_TRACK_FILL = COLOR_ACCENT;
    private static final int COLOR_TOGGLE_OFF = 0xFF343435;
    private static final int COLOR_BUTTON_CREATE = 0xFF382B52;
    private static final int COLOR_BUTTON_RENAME = 0xFF3B3A3A;
    private static final int COLOR_BUTTON_DELETE = 0xFF2B1B1C;
    private static final int COLOR_BUTTON_BORDER = 0xFF2A2A2D;
    private static final int COLOR_DROPDOWN_BG = 0xFF3B3A3A;
    private static final int COLOR_DROPDOWN_SELECTED = 0xFF444346;
    private static final int COLOR_RESET_TOP = 0xFF373738;
    private static final int COLOR_RESET_BOTTOM = 0xFF2C2C2D;
    private static final int COLOR_RESET_TOP_HOVER = 0xFF424243;
    private static final int COLOR_RESET_BOTTOM_HOVER = 0xFF323234;

    private final Screen parent;
    private final ViewModelProfileManager profileManager = ViewModelConfig.profiles();
    private final List<SliderLine> sliderLines = new ArrayList<>();

    private ProfileDropdownWidget profileDropdown;
    private ToggleSwitchWidget noSwingToggle;
    private ToggleSwitchWidget scaleSwingToggle;
    private Text statusMessage = Text.empty();
    private int statusTicks;

    private int leftPanelX;
    private int leftPanelY;
    private int rightPanelX;
    private int rightPanelY;
    private int leftPanelHeight = LEFT_PANEL_HEIGHT;
    private int rightPanelHeight = RIGHT_PANEL_HEIGHT;

    public ViewmodelConfigScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.sliderLines.clear();
        this.clearChildren();

        int totalWidth = LEFT_PANEL_WIDTH + RIGHT_PANEL_WIDTH + PANEL_SPACING;
        this.leftPanelX = (this.width - totalWidth) / 2;
        this.rightPanelX = this.leftPanelX + LEFT_PANEL_WIDTH + PANEL_SPACING;
        this.rightPanelY = (this.height - this.rightPanelHeight) / 2;
        this.leftPanelY = this.rightPanelY + LEFT_PANEL_Y_OFFSET;

        buildSidebar();
        buildSliders();
    }

    private void buildSidebar() {
        int dropdownWidth = LEFT_PANEL_WIDTH - LEFT_PANEL_PADDING * 2;
        List<String> names = profileManager.profileNames();
        ProfileDropdownWidget dropdown = new ProfileDropdownWidget(
            leftPanelX + LEFT_PANEL_PADDING,
            leftPanelY + 38,
            dropdownWidth,
            18,
            names,
            profileManager.getActiveIndex(),
            this::handleProfileSelection
        );

        int buttonsY = dropdown.getY() + dropdown.getHeight() + 8;
        int buttonWidth = (dropdownWidth - 8) / 3;

        this.addDrawableChild(new AccentButton(
            leftPanelX + LEFT_PANEL_PADDING,
            buttonsY,
            buttonWidth,
            16,
            Text.literal("Create"),
            this::handleCreateProfile
        ));

        this.addDrawableChild(new MinimalButton(
            leftPanelX + LEFT_PANEL_PADDING + buttonWidth + 4,
            buttonsY,
            buttonWidth,
            16,
            Text.literal("Rename"),
            this::handleRenameProfile,
            COLOR_BUTTON_RENAME,
            COLOR_BUTTON_RENAME
        ));

        this.addDrawableChild(new DangerButton(
            leftPanelX + LEFT_PANEL_PADDING + (buttonWidth + 4) * 2,
            buttonsY,
            buttonWidth,
            16,
            Text.literal("Delete"),
            this::handleDeleteProfile
        ));
        this.leftPanelHeight = LEFT_PANEL_HEIGHT;

        this.profileDropdown = this.addDrawableChild(dropdown);
    }

    private void buildSliders() {
        int cursorY = rightPanelY + SLIDER_TOP_OFFSET;

        cursorY = addSliderRow(
            "Size",
            0.15,
            2.0,
            0.01,
            () -> ViewModelConfig.current.getSize(),
            ViewModelProfile.baseline().size(),
            value -> setAndSync(ViewModelConfig.current::setSize, value),
            cursorY
        );
        cursorY += SLIDER_GROUP_GAP;

        cursorY = addSliderRow(
            "X",
            -50.0,
            50.0,
            0.50,
            () -> ViewModelConfig.current.getPositionX(),
            ViewModelProfile.baseline().positionX(),
            value -> setAndSync(ViewModelConfig.current::setPositionX, value),
            cursorY
        );

        cursorY = addSliderRow(
            "Y",
            -50.0,
            50.0,
            0.50,
            () -> ViewModelConfig.current.getPositionY(),
            ViewModelProfile.baseline().positionY(),
            value -> setAndSync(ViewModelConfig.current::setPositionY, value),
            cursorY
        );

        cursorY = addSliderRow(
            "Z",
            -50.0,
            50.0,
            0.50,
            () -> ViewModelConfig.current.getPositionZ(),
            ViewModelProfile.baseline().positionZ(),
            value -> setAndSync(ViewModelConfig.current::setPositionZ, value),
            cursorY
        );
        cursorY += SLIDER_GROUP_GAP;

        cursorY = addSliderRow(
            "Yaw",
            -180.0,
            180.0,
            1.0,
            () -> ViewModelConfig.current.getRotationYaw(),
            ViewModelProfile.baseline().rotationYaw(),
            value -> setAndSync(ViewModelConfig.current::setRotationYaw, value),
            cursorY
        );

        cursorY = addSliderRow(
            "Pitch",
            -180.0,
            180.0,
            1.0,
            () -> ViewModelConfig.current.getRotationPitch(),
            ViewModelProfile.baseline().rotationPitch(),
            value -> setAndSync(ViewModelConfig.current::setRotationPitch, value),
            cursorY
        );

        cursorY = addSliderRow(
            "Roll",
            -180.0,
            180.0,
            1.0,
            () -> ViewModelConfig.current.getRotationRoll(),
            ViewModelProfile.baseline().rotationRoll(),
            value -> setAndSync(ViewModelConfig.current::setRotationRoll, value),
            cursorY
        );

        TextRenderer font = this.textRenderer;
        int leftLabelWidth = Math.round(font.getWidth(Text.literal("NO SWING")) * TOGGLE_LABEL_SCALE);
        int rightLabelWidth = Math.round(font.getWidth(Text.literal("SCALE SWING")) * TOGGLE_LABEL_SCALE);
        int leftToggleWidth = leftLabelWidth + TOGGLE_LABEL_GAP + TOGGLE_SWITCH_WIDTH;
        int rightToggleWidth = rightLabelWidth + TOGGLE_LABEL_GAP + TOGGLE_SWITCH_WIDTH;
        int toggleY = rightPanelY + TOGGLE_Y_OFFSET;
        int resetButtonY = rightPanelY + RESET_BUTTON_Y_OFFSET;
        int leftToggleX = rightPanelX + RIGHT_PANEL_PADDING;
        int rightToggleX = rightPanelX + RIGHT_PANEL_WIDTH - RIGHT_PANEL_PADDING - rightToggleWidth;
        if (rightToggleX < leftToggleX + leftToggleWidth + TOGGLE_GAP) {
            rightToggleX = leftToggleX + leftToggleWidth + TOGGLE_GAP;
        }

        this.addDrawableChild(new GradientButton(
            rightPanelX + RIGHT_PANEL_PADDING,
            resetButtonY,
            RIGHT_PANEL_WIDTH - RIGHT_PANEL_PADDING * 2,
            RESET_PROFILE_BUTTON_HEIGHT,
            Text.literal("Reset profile"),
            this::handleResetAll
        ));

        this.noSwingToggle = this.addDrawableChild(new ToggleSwitchWidget(
            leftToggleX,
            toggleY,
            leftToggleWidth,
            TOGGLE_HEIGHT,
            Text.literal("NO SWING"),
            () -> ViewModelConfig.current.getNoSwing(),
            value -> setAndSync(ViewModelConfig.current::setNoSwing, value)
        ));

        this.scaleSwingToggle = this.addDrawableChild(new ToggleSwitchWidget(
            rightToggleX,
            toggleY,
            rightToggleWidth,
            TOGGLE_HEIGHT,
            Text.literal("SCALE SWING"),
            () -> ViewModelConfig.current.getScaleSwing(),
            value -> setAndSync(ViewModelConfig.current::setScaleSwing, value)
        ));
    }

    private int addSliderRow(
        String label,
        double min,
        double max,
        double step,
        DoubleSupplier supplier,
        double resetValue,
        DoubleConsumer setter,
        int y
    ) {
        int resetX = rightPanelX + RIGHT_PANEL_PADDING;
        int sliderX = resetX + RESET_ICON_SIZE + SLIDER_ICON_GAP;
        int sliderWidth = RIGHT_PANEL_WIDTH
            - RIGHT_PANEL_PADDING * 2
            - RESET_ICON_SIZE
            - SLIDER_ICON_GAP
            - VALUE_COLUMN_WIDTH;
        int sliderY = y;
        int sliderHeight = SLIDER_TRACK_HEIGHT + 8;

        NeonSlider slider = this.addDrawableChild(new NeonSlider(
            sliderX,
            sliderY,
            sliderWidth,
            sliderHeight,
            min,
            max,
            step,
            supplier.getAsDouble(),
            setter
        ));

        int labelX = sliderX;
        int labelY = sliderY - SLIDER_LABEL_OFFSET;
        int valueRightX = rightPanelX + RIGHT_PANEL_WIDTH - RIGHT_PANEL_PADDING;
        int valueY = labelY + SLIDER_VALUE_OFFSET;
        sliderLines.add(new SliderLine(label, slider, supplier, labelX, labelY, valueRightX, valueY));

        int iconY = sliderY + sliderHeight / 2 - RESET_ICON_SIZE / 2;
        this.addDrawableChild(new ResetButton(
            resetX,
            iconY,
            () -> slider.resetTo(resetValue)
        ));

        return y + SLIDER_ROW_SPACING;
    }

    private void handleCreateProfile() {
        if (this.client == null) {
            return;
        }

        this.client.setScreen(new NamePromptScreen(
            this,
            Text.literal("Create profile"),
            "",
            input -> {
                if (input.isBlank()) {
                    return Text.literal("Name cannot be empty");
                }
                ViewModelProfile created = profileManager.create(input);
                if (created == null) {
                    return Text.literal("Unable to create profile");
                }
                setStatus(Text.literal("Created " + created.name()));
                this.clearAndInit();
                return null;
            }
        ));
    }

    private void handleRenameProfile() {
        if (this.client == null) {
            return;
        }
        String currentName = profileManager.getActiveProfile().name();
        this.client.setScreen(new NamePromptScreen(
            this,
            Text.literal("Rename profile"),
            currentName,
            input -> {
                if (input.isBlank()) {
                    return Text.literal("Name cannot be empty");
                }
                if (!profileManager.renameActive(input)) {
                    return Text.literal("Name already exists");
                }
                setStatus(Text.literal("Renamed to " + profileManager.getActiveProfile().name()));
                this.clearAndInit();
                return null;
            }
        ));
    }

    private void handleDeleteProfile() {
        if (!profileManager.deleteActive()) {
            setStatus(Text.literal("Cannot delete last profile").formatted(Formatting.RED));
            return;
        }
        setStatus(Text.literal("Deleted profile"));
        this.clearAndInit();
    }

    private void handleProfileSelection(int index) {
        if (index == profileManager.getActiveIndex()) {
            return;
        }
        profileManager.select(index);
        setStatus(Text.literal("Switched to " + profileManager.getActiveProfile().name()));
        this.clearAndInit();
    }

    private void handleResetAll() {
        ViewModelProfile baseline = ViewModelProfile.baseline();
        baseline.apply(ViewModelConfig.current);
        profileManager.updateActiveFromConfig();

        for (SliderLine line : sliderLines) {
            line.slider().syncFrom(line.supplier().getAsDouble());
        }
        if (noSwingToggle != null) {
            noSwingToggle.refreshFromConfig();
        }
        if (scaleSwingToggle != null) {
            scaleSwingToggle.refreshFromConfig();
        }
        setStatus(Text.literal("Reset to defaults"));
    }

    private void setAndSync(FloatSetter setter, double value) {
        setter.accept((float) value);
        profileManager.updateActiveFromConfig();
    }

    private void setAndSync(BooleanSetter setter, boolean value) {
        setter.accept(value);
        profileManager.updateActiveFromConfig();
    }

    private void setStatus(Text message) {
        this.statusMessage = message;
        this.statusTicks = 80;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (profileDropdown != null && profileDropdown.isOpen()) {
            if (profileDropdown.isMouseOver(mouseX, mouseY)) {
                if (profileDropdown.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            } else {
                profileDropdown.close();
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        super.tick();
        if (statusTicks > 0) {
            statusTicks--;
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderDimBackground(context);
        drawPanel(context, leftPanelX, leftPanelY, LEFT_PANEL_WIDTH, leftPanelHeight);
        drawPanel(context, rightPanelX, rightPanelY, RIGHT_PANEL_WIDTH, rightPanelHeight);

        TextRenderer font = this.textRenderer;
        drawScaledText(
            context,
            Text.literal("PROFILES"),
            leftPanelX + LEFT_PANEL_PADDING,
            leftPanelY + 12,
            COLOR_TEXT_PRIMARY,
            TEXT_SCALE
        );
        String header = profileManager.getActiveProfile().name().toUpperCase(Locale.ROOT);
        drawScaledText(
            context,
            Text.literal(header),
            rightPanelX + RIGHT_PANEL_PADDING,
            rightPanelY + 12,
            COLOR_TEXT_PRIMARY,
            TEXT_SCALE
        );

        super.render(context, mouseX, mouseY, delta);

        for (SliderLine line : sliderLines) {
            drawScaledText(
                context,
                Text.literal(line.label()),
                line.labelX(),
                line.labelY(),
                COLOR_TEXT_MUTED,
                TEXT_SCALE
            );
            String value = line.slider().formattedValue(VALUE_FORMAT);
            Text valueText = Text.literal(value);
            float scaledWidth = font.getWidth(valueText) * VALUE_SCALE;
            int valueX = line.valueRightX() - Math.round(scaledWidth);
            drawScaledText(context, valueText, valueX, line.valueY(), COLOR_TEXT_PRIMARY, VALUE_SCALE);
        }

        if (statusMessage != null && statusTicks > 0 && !statusMessage.getString().isEmpty()) {
            drawScaledText(
                context,
                statusMessage,
                leftPanelX + LEFT_PANEL_PADDING,
                leftPanelY + leftPanelHeight - 16,
                COLOR_ACCENT,
                TEXT_SCALE
            );
        }
        if (profileDropdown != null && profileDropdown.isOpen()) {
            profileDropdown.renderOverlay(context);
        }
    }

    private void renderDimBackground(DrawContext context) {
        context.fill(0, 0, this.width, this.height, COLOR_BACKDROP);
    }

    private void drawPanel(DrawContext context, int x, int y, int width, int height) {
        drawRoundedRect(context, x + 2, y + 3, width, height, PANEL_RADIUS, COLOR_PANEL_SHADOW);
        drawRoundedRect(context, x, y, width, height, PANEL_RADIUS, COLOR_PANEL_BORDER);
        drawRoundedRect(context, x + 1, y + 1, width - 2, height - 2, PANEL_RADIUS - 1, COLOR_PANEL_BG);
    }

    private void drawScaledText(DrawContext context, Text text, int x, int y, int color, float scale) {
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        int scaledX = Math.round(x / scale);
        int scaledY = Math.round(y / scale);
        context.drawText(this.textRenderer, text, scaledX, scaledY, color, false);
        context.getMatrices().pop();
    }

    private static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (USE_NANOVG && NanoVGHelper.drawRoundedRect(x, y, width, height, radius, color)) {
            return;
        }
        TextureSpec spec = TextureSpec.fromRadius(radius);
        if (spec == null) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }
        drawNineSlice(context, spec, x, y, width, height, color);
    }

    private static void drawNineSlice(DrawContext context, TextureSpec spec, int x, int y, int width, int height, int color) {
        int c = spec.corner;
        int innerW = Math.max(0, width - c * 2);
        int innerH = Math.max(0, height - c * 2);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        drawSlice(context, spec.texture, x, y, c, c, 0, 0, c, c, spec.size, spec.size, color);
        drawSlice(context, spec.texture, x + width - c, y, c, c, spec.size - c, 0, c, c, spec.size, spec.size, color);
        drawSlice(context, spec.texture, x, y + height - c, c, c, 0, spec.size - c, c, c, spec.size, spec.size, color);
        drawSlice(context, spec.texture, x + width - c, y + height - c, c, c, spec.size - c, spec.size - c, c, c, spec.size, spec.size, color);

        if (innerW > 0) {
            int regionW = spec.size - c * 2;
            drawSlice(context, spec.texture, x + c, y, innerW, c, c, 0, regionW, c, spec.size, spec.size, color);
            drawSlice(context, spec.texture, x + c, y + height - c, innerW, c, c, spec.size - c, regionW, c, spec.size, spec.size, color);
        }
        if (innerH > 0) {
            int regionH = spec.size - c * 2;
            drawSlice(context, spec.texture, x, y + c, c, innerH, 0, c, c, regionH, spec.size, spec.size, color);
            drawSlice(context, spec.texture, x + width - c, y + c, c, innerH, spec.size - c, c, c, regionH, spec.size, spec.size, color);
        }
        if (innerW > 0 && innerH > 0) {
            int regionW = spec.size - c * 2;
            int regionH = spec.size - c * 2;
            drawSlice(context, spec.texture, x + c, y + c, innerW, innerH, c, c, regionW, regionH, spec.size, spec.size, color);
        }
    }

    private static void drawSlice(
        DrawContext context,
        Identifier texture,
        int x,
        int y,
        int width,
        int height,
        int u,
        int v,
        int regionWidth,
        int regionHeight,
        int textureWidth,
        int textureHeight,
        int color
    ) {
        if (width <= 0 || height <= 0 || regionWidth <= 0 || regionHeight <= 0) {
            return;
        }
        context.drawTexture(
            RenderLayer::getGuiTextured,
            texture,
            x,
            y,
            (float) u,
            (float) v,
            width,
            height,
            regionWidth,
            regionHeight,
            textureWidth,
            textureHeight,
            color
        );
    }

    private static void setShaderColor(int color) {
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, a);
    }

    private record TextureSpec(Identifier texture, int size, int corner) {
        private static TextureSpec fromRadius(int radius) {
            if (radius >= 7) {
                return new TextureSpec(TEX_ROUNDED_R8, TEX_R8_SIZE, 8);
            }
            if (radius >= 4) {
                return new TextureSpec(TEX_ROUNDED_R4, TEX_R4_SIZE, 4);
            }
            if (radius >= 2) {
                return new TextureSpec(TEX_ROUNDED_R2, TEX_R2_SIZE, 2);
            }
            return null;
        }
    }

    private static final class NanoVGHelper {
        private static final int FLAGS = NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES;
        private static long vg;
        private static boolean available = true;

        private NanoVGHelper() {
        }

        private static void ensureContext() {
            if (vg != 0 || !available) {
                return;
            }
            try {
                vg = NanoVGGL3.nvgCreate(FLAGS);
                if (vg == 0) {
                    available = false;
                }
            } catch (Throwable t) {
                available = false;
            }
        }

        static boolean drawRoundedRect(int x, int y, int width, int height, int radius, int color) {
            if (!available) {
                return false;
            }
            ensureContext();
            if (vg == 0) {
                return false;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) {
                return false;
            }
            float scale = (float) client.getWindow().getScaleFactor();
            int screenW = client.getWindow().getScaledWidth();
            int screenH = client.getWindow().getScaledHeight();
            float r = Math.min(radius, Math.min(width, height) / 2f);
            if (r <= 0f) {
                return false;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                NVGColor nvgColor = NVGColor.calloc(stack);
                NanoVG.nvgRGBA(
                    (byte) ((color >> 16) & 0xFF),
                    (byte) ((color >> 8) & 0xFF),
                    (byte) (color & 0xFF),
                    (byte) ((color >> 24) & 0xFF),
                    nvgColor
                );

                NanoVG.nvgBeginFrame(vg, screenW, screenH, scale);
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgRoundedRect(vg, x, y, width, height, r);
                NanoVG.nvgFillColor(vg, nvgColor);
                NanoVG.nvgFill(vg);
                NanoVG.nvgEndFrame(vg);
            }
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            return true;
        }

        static boolean drawRefreshIcon(int x, int y, int size, int color) {
            if (!available) {
                return false;
            }
            ensureContext();
            if (vg == 0) {
                return false;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) {
                return false;
            }
            float scale = (float) client.getWindow().getScaleFactor();
            int screenW = client.getWindow().getScaledWidth();
            int screenH = client.getWindow().getScaledHeight();

            float cx = x + size / 2f;
            float cy = y + size / 2f;
            float r = size * 0.28f;
            float stroke = Math.max(1.2f, size * 0.08f);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                NVGColor nvgColor = NVGColor.calloc(stack);
                NanoVG.nvgRGBA(
                    (byte) ((color >> 16) & 0xFF),
                    (byte) ((color >> 8) & 0xFF),
                    (byte) (color & 0xFF),
                    (byte) ((color >> 24) & 0xFF),
                    nvgColor
                );

                NanoVG.nvgBeginFrame(vg, screenW, screenH, scale);
                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgStrokeWidth(vg, stroke);
                NanoVG.nvgStrokeColor(vg, nvgColor);
                NanoVG.nvgArc(vg, cx, cy, r, (float) Math.toRadians(30), (float) Math.toRadians(320), NanoVG.NVG_CW);
                NanoVG.nvgStroke(vg);

                float arrowAngle = (float) Math.toRadians(320);
                float ax = cx + MathHelper.cos(arrowAngle) * r;
                float ay = cy + MathHelper.sin(arrowAngle) * r;
                float arrowSize = size * 0.16f;

                NanoVG.nvgBeginPath(vg);
                NanoVG.nvgMoveTo(vg, ax, ay);
                NanoVG.nvgLineTo(vg, ax - arrowSize, ay + arrowSize * 0.2f);
                NanoVG.nvgLineTo(vg, ax - arrowSize * 0.2f, ay + arrowSize);
                NanoVG.nvgClosePath(vg);
                NanoVG.nvgFillColor(vg, nvgColor);
                NanoVG.nvgFill(vg);

                NanoVG.nvgEndFrame(vg);
            }
            GL11.glDisable(GL11.GL_STENCIL_TEST);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            return true;
        }
    }

    private interface FloatSetter {
        void accept(float value);
    }

    private interface BooleanSetter {
        void accept(boolean value);
    }

    private record SliderLine(
        String label,
        NeonSlider slider,
        DoubleSupplier supplier,
        int labelX,
        int labelY,
        int valueRightX,
        int valueY
    ) {}

    private static class NeonSlider extends SliderWidget {
        private final double min;
        private final double max;
        private final double step;
        private final DoubleConsumer onChange;

        NeonSlider(int x, int y, int width, int height, double min, double max, double step, double initialValue, DoubleConsumer onChange) {
            super(x, y, width, height, Text.empty(), normalize(initialValue, min, max));
            this.min = min;
            this.max = max;
            this.step = step;
            this.onChange = onChange;
            setValueFromActual(initialValue);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.empty());
        }

        @Override
        protected void applyValue() {
            double snapped = snap(getActualValue());
            setValueFromActual(snapped);
            onChange.accept(snapped);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int trackHeight = SLIDER_TRACK_HEIGHT;
            int knobSize = SLIDER_KNOB_SIZE;
            int trackY = getY() + this.height / 2 - trackHeight / 2;
            int knobX = getX() + (int) (this.value * (this.width - knobSize));
            int knobY = trackY - (knobSize - trackHeight) / 2;

            drawRoundedRect(context, getX(), trackY, this.width, trackHeight, SLIDER_TRACK_RADIUS, COLOR_TRACK_BG);
            drawRoundedRect(context, getX(), trackY, knobX + knobSize - getX(), trackHeight, SLIDER_TRACK_RADIUS, COLOR_TRACK_FILL);
            int knobRadius = Math.max(1, knobSize / 2);
            drawRoundedRect(context, knobX, knobY, knobSize, knobSize, knobRadius, 0xFFF2F2F2);
        }

        public void resetTo(double target) {
            setValueFromActual(target);
            onChange.accept(target);
        }

        public String formattedValue(DecimalFormat format) {
            return format.format(getActualValue());
        }

        private double getActualValue() {
            return MathHelper.lerp(this.value, this.min, this.max);
        }

        private void setValueFromActual(double actual) {
            double normalized = normalize(actual, min, max);
            this.value = MathHelper.clamp(normalized, 0.0, 1.0);
        }

        private double snap(double value) {
            return step <= 0 ? value : Math.round(value / step) * step;
        }

        public void syncFrom(double actualValue) {
            setValueFromActual(actualValue);
        }

        private static double normalize(double value, double min, double max) {
            return (value - min) / (max - min);
        }
    }

    private static class ResetButton extends ClickableWidget {
        private final Runnable action;

        ResetButton(int x, int y, Runnable action) {
            super(x, y, RESET_ICON_SIZE, RESET_ICON_SIZE, Text.empty());
            this.action = action;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int color = this.isHovered() ? COLOR_ACCENT_HOVER : COLOR_ACCENT;
            drawRoundedRect(context, getX(), getY(), getWidth(), getHeight(), RESET_ICON_RADIUS, color);
            if (USE_NANOVG_ICON && NanoVGHelper.drawRefreshIcon(getX(), getY(), getWidth(), 0xFFF6F3FF)) {
                return;
            }
            int glyphX = getX() + (getWidth() - RESET_ICON_GLYPH_WIDTH) / 2;
            int glyphY = getY() + (getHeight() - RESET_ICON_GLYPH_HEIGHT) / 2;
            context.drawTexture(
                RenderLayer::getGuiTextured,
                TEX_REFRESH,
                glyphX,
                glyphY,
                (float) RESET_ICON_GLYPH_U,
                (float) RESET_ICON_GLYPH_V,
                RESET_ICON_GLYPH_WIDTH,
                RESET_ICON_GLYPH_HEIGHT,
                RESET_ICON_GLYPH_REGION,
                RESET_ICON_GLYPH_REGION,
                TEX_REFRESH_WIDTH,
                TEX_REFRESH_HEIGHT
            );
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            action.run();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }
    }

    private static class MinimalButton extends PressableWidget {
        private final Runnable action;
        private final int baseColor;
        private final int hoverColor;
        private final int textColor;

        MinimalButton(int x, int y, int width, int height, Text text, Runnable action, int baseColor, int hoverColor) {
            this(x, y, width, height, text, action, baseColor, hoverColor, COLOR_TEXT_PRIMARY);
        }

        MinimalButton(int x, int y, int width, int height, Text text, Runnable action, int baseColor, int hoverColor, int textColor) {
            super(x, y, width, height, text);
            this.action = action;
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int fill = this.isHovered() ? hoverColor : baseColor;
            drawRoundedRect(context, getX(), getY(), getWidth(), getHeight(), BUTTON_RADIUS, COLOR_BUTTON_BORDER);
            drawRoundedRect(context, getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, BUTTON_RADIUS - 1, fill);
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            Text message = getMessage();
            int textWidth = font.getWidth(message);
            int textX = getX() + (getWidth() - textWidth) / 2;
            int textY = getY() + (getHeight() - font.fontHeight) / 2;
            context.drawText(font, message, textX, textY, textColor, false);
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }
    }

    private static class GradientButton extends PressableWidget {
        private final Runnable action;

        GradientButton(int x, int y, int width, int height, Text text, Runnable action) {
            super(x, y, width, height, text);
            this.action = action;
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int top = this.isHovered() ? COLOR_RESET_TOP_HOVER : COLOR_RESET_TOP;
            int bottom = this.isHovered() ? COLOR_RESET_BOTTOM_HOVER : COLOR_RESET_BOTTOM;
            drawRoundedRect(context, getX(), getY(), getWidth(), getHeight(), RESET_BUTTON_RADIUS, COLOR_BUTTON_BORDER);
            drawRoundedRect(context, getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2, RESET_BUTTON_RADIUS - 1, bottom);
            int highlightHeight = (getHeight() - 2) / 2 + RESET_BUTTON_RADIUS;
            drawRoundedRect(
                context,
                getX() + 1,
                getY() + 1,
                getWidth() - 2,
                highlightHeight,
                RESET_BUTTON_RADIUS - 1,
                top
            );
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            Text message = getMessage();
            int textWidth = font.getWidth(message);
            int textX = getX() + (getWidth() - textWidth) / 2;
            int textY = getY() + (getHeight() - font.fontHeight) / 2;
            context.drawText(font, message, textX, textY, COLOR_TEXT_PRIMARY, false);
        }

        @Override
        public void onPress() {
            action.run();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }
    }

    private static class AccentButton extends MinimalButton {
        AccentButton(int x, int y, int width, int height, Text text, Runnable action) {
            super(x, y, width, height, text, action, COLOR_BUTTON_CREATE, COLOR_BUTTON_CREATE);
        }
    }

    private static class DangerButton extends MinimalButton {
        DangerButton(int x, int y, int width, int height, Text text, Runnable action) {
            super(x, y, width, height, text.copy().formatted(Formatting.WHITE), action, COLOR_BUTTON_DELETE, COLOR_BUTTON_DELETE);
        }
    }

    private static class ToggleSwitchWidget extends ClickableWidget {
        private final Text label;
        private final BooleanSupplier supplier;
        private final BooleanSetter consumer;
        private boolean value;

        ToggleSwitchWidget(int x, int y, int width, int height, Text label, BooleanSupplier supplier, BooleanSetter consumer) {
            super(x, y, width, height, label);
            this.label = label;
            this.supplier = supplier;
            this.consumer = consumer;
            this.value = supplier.getAsBoolean();
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            Text styled = label;
            float scale = TOGGLE_LABEL_SCALE;
            int textY = getY() + (getHeight() - Math.round(font.fontHeight * scale)) / 2;
            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, 1.0f);
            context.drawText(font, styled, Math.round(getX() / scale), Math.round(textY / scale), COLOR_TEXT_PRIMARY, false);
            context.getMatrices().pop();

            int switchWidth = TOGGLE_SWITCH_WIDTH;
            int switchHeight = TOGGLE_HEIGHT;
            int labelWidth = Math.round(font.getWidth(styled) * scale);
            int switchX = getX() + labelWidth + TOGGLE_LABEL_GAP;
            int switchY = getY() + (getHeight() - switchHeight) / 2;
            int trackColor = value ? COLOR_ACCENT : COLOR_TOGGLE_OFF;

            drawRoundedRect(context, switchX, switchY, switchWidth, switchHeight, TOGGLE_HEIGHT / 2, trackColor);

            int knobSize = switchHeight + 4;
            int knobInset = (knobSize - switchHeight) / 2;
            int knobX = value ? switchX + switchWidth - knobSize + knobInset : switchX - knobInset;
            int knobY = switchY - knobInset;
            drawRoundedRect(context, knobX, knobY, knobSize, knobSize, knobSize / 2, 0xFFF2F2F2);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.value = !this.value;
            this.consumer.accept(this.value);
        }

        public void refreshFromConfig() {
            this.value = supplier.getAsBoolean();
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }
    }

    private static class ProfileDropdownWidget extends ClickableWidget {
        private static final int MAX_VISIBLE = 5;
        private List<String> entries;
        private final java.util.function.IntConsumer onSelect;
        private boolean open;
        private int selectedIndex;
        private int scrollOffset;

        ProfileDropdownWidget(
            int x,
            int y,
            int width,
            int height,
            List<String> entries,
            int selectedIndex,
            java.util.function.IntConsumer onSelect
        ) {
            super(x, y, width, height, Text.empty());
            this.entries = new ArrayList<>(entries);
            this.onSelect = onSelect;
            setSelectedIndex(selectedIndex);
        }

        public void setEntries(List<String> entries, int selectedIndex) {
            this.entries = new ArrayList<>(entries);
            setSelectedIndex(selectedIndex);
        }

        private void setSelectedIndex(int selectedIndex) {
            if (entries.isEmpty()) {
                this.selectedIndex = -1;
                return;
            }
            this.selectedIndex = MathHelper.clamp(selectedIndex, 0, entries.size() - 1);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (!open) {
                open = true;
                return;
            }

            if (mouseY >= getY() + getHeight()) {
                int row = (int) ((mouseY - (getY() + getHeight())) / getHeight());
                int index = scrollOffset + row;
                if (index >= 0 && index < entries.size()) {
                    onSelect.accept(index);
                }
            }

            open = false;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            if (!open || !isMouseOver(mouseX, mouseY)) {
                return false;
            }
            int maxOffset = Math.max(0, entries.size() - MAX_VISIBLE);
            double delta = verticalAmount != 0 ? verticalAmount : horizontalAmount;
            scrollOffset = MathHelper.clamp(scrollOffset - (int) Math.signum(delta), 0, maxOffset);
            return true;
        }

        public boolean isOpen() {
            return open;
        }

        public void close() {
            this.open = false;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            if (open) {
                int visible = Math.min(MAX_VISIBLE, entries.size());
                int height = getHeight() * (visible + 1);
                return mouseX >= getX()
                    && mouseX <= getX() + getWidth()
                    && mouseY >= getY()
                    && mouseY <= getY() + height;
            }
            return super.isMouseOver(mouseX, mouseY);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            drawBox(context, getY(), selectedIndex, true);
        }

        void renderOverlay(DrawContext context) {
            if (!open) {
                return;
            }
            int visible = Math.min(MAX_VISIBLE, entries.size());
            int listY = getY() + getHeight();
            int listHeight = getHeight() * visible;
            int listX = getX();
            int listWidth = getWidth();
            context.fill(listX, listY, listX + listWidth, listY + listHeight, COLOR_BUTTON_BORDER);
            context.fill(listX + 1, listY + 1, listX + listWidth - 1, listY + listHeight - 1, COLOR_DROPDOWN_BG);
            for (int i = 0; i < visible; i++) {
                int idx = scrollOffset + i;
                int rowY = getY() + getHeight() * (i + 1);
                drawBox(context, rowY, idx, false);
            }
        }

        private void drawBox(DrawContext context, int y, int index, boolean drawBorder) {
            String label = (index >= 0 && index < entries.size()) ? entries.get(index) : "None";
            boolean isSelected = index == selectedIndex;
            int background = isSelected ? COLOR_DROPDOWN_SELECTED : COLOR_DROPDOWN_BG;
            if (drawBorder) {
                drawRoundedRect(context, getX(), y, getWidth(), getHeight(), DROPDOWN_RADIUS, COLOR_BUTTON_BORDER);
                drawRoundedRect(context, getX() + 1, y + 1, getWidth() - 2, getHeight() - 2, DROPDOWN_RADIUS - 1, background);
            } else {
                context.fill(getX(), y, getX() + getWidth(), y + getHeight(), background);
            }
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            int textY = y + (getHeight() - font.fontHeight) / 2;
            context.drawText(
                font,
                Text.literal(label),
                getX() + 6,
                textY,
                COLOR_TEXT_PRIMARY,
                false
            );
            if (y == getY()) {
                context.drawText(
                    font,
                    Text.literal("\u25bc"),
                    getX() + getWidth() - 10,
                    textY,
                    COLOR_ACCENT,
                    false
                );
            }
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            appendDefaultNarrations(builder);
        }
    }

    private static class NamePromptScreen extends Screen {
        private final Screen parent;
        private final Function<String, Text> submitter;
        private final String initialValue;
        private TextFieldWidget textField;
        private Text errorMessage = Text.empty();

        protected NamePromptScreen(Screen parent, Text title, String initialValue, Function<String, Text> submitter) {
            super(title);
            this.parent = parent;
            this.initialValue = initialValue;
            this.submitter = submitter;
        }

        @Override
        protected void init() {
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            this.textField = new TextFieldWidget(font, this.width / 2 - 90, this.height / 2 - 10, 180, 20, Text.empty());
            this.textField.setText(initialValue);
            this.textField.setChangedListener(value -> this.errorMessage = Text.empty());
            this.setInitialFocus(this.textField);
            this.addDrawableChild(textField);

            this.addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), button -> submit())
                .dimensions(this.width / 2 - 90, this.height / 2 + 20, 85, 20)
                .build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> close())
                .dimensions(this.width / 2 + 5, this.height / 2 + 20, 85, 20)
                .build());
        }

        private void submit() {
            Text result = submitter.apply(textField.getText().trim());
            if (result == null) {
                this.client.setScreen(parent);
            } else {
                this.errorMessage = result.copy().formatted(Formatting.RED);
            }
        }

        @Override
        public void close() {
            this.client.setScreen(parent);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                submit();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                close();
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context, mouseX, mouseY, delta);
            context.fill(0, 0, this.width, this.height, 0xAA05080C);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFFFF);
            super.render(context, mouseX, mouseY, delta);
            if (errorMessage != null && !errorMessage.getString().isEmpty()) {
                context.drawCenteredTextWithShadow(this.textRenderer, errorMessage, this.width / 2, this.height / 2 + 46, 0xFFFF6B6B);
            }
        }
    }
}
