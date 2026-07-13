package com.viewmodel.client.gui;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Minecraft;

public final class NanoVGRenderer implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger("ViewModel/NanoVG");
    private static final int FLAGS = NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES;
    private long context;
    private int framebuffer;
    private boolean loggedReady;

    public void render(Consumer<Canvas> draw) {
        GlStateSnapshot state = GlStateSnapshot.capture();
        try {
            ensureContext();
            Minecraft client = Minecraft.getInstance();
            float pixelRatio = (float) client.getWindow().getGuiScale();
            int width = client.getWindow().getGuiScaledWidth();
            int height = client.getWindow().getGuiScaledHeight();
            RenderTarget target = client.getMainRenderTarget();
            bind(target);
            NanoVG.nvgBeginFrame(context, width, height, pixelRatio);
            try (MemoryStack stack = MemoryStack.stackPush()) {
                draw.accept(new Canvas(context, stack));
            } finally {
                NanoVG.nvgEndFrame(context);
            }
        } finally {
            state.restore();
        }
    }

    private void bind(RenderTarget target) {
        if (!(target.getColorTexture() instanceof GlTexture color)) {
            throw new IllegalStateException("Minecraft render target is not OpenGL-backed");
        }
        if (framebuffer == 0) framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, color.glId(), 0);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("NanoVG framebuffer is incomplete: 0x" + Integer.toHexString(status));
        }
        GL11.glViewport(0, 0, target.width, target.height);
        if (!loggedReady) {
            LOGGER.info("NanoVG attached to Minecraft framebuffer {} ({}x{})", framebuffer, target.width, target.height);
            loggedReady = true;
        }
    }

    private void ensureContext() {
        if (context != 0) return;
        context = NanoVGGL3.nvgCreate(FLAGS);
        if (context == 0) throw new IllegalStateException("Unable to create NanoVG context");
    }

    @Override
    public void close() {
        if (context != 0) {
            NanoVGGL3.nvgDelete(context);
            context = 0;
        }
        if (framebuffer != 0) {
            GL30.glDeleteFramebuffers(framebuffer);
            framebuffer = 0;
        }
    }

    public static final class Canvas {
        private final long vg;
        private final MemoryStack stack;
        private Canvas(long vg, MemoryStack stack) {
            this.vg = vg;
            this.stack = stack;
        }

        public void roundedRect(float x, float y, float width, float height, float radius, int color) {
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRoundedRect(vg, x, y, width, height, Math.min(radius, Math.min(width, height) / 2));
            NanoVG.nvgFillColor(vg, color(color));
            NanoVG.nvgFill(vg);
        }

        public void panel(EditorLayout.Rect rect) {
            roundedRect(rect.x() + 2, rect.y() + 4, rect.width(), rect.height(), UiTheme.PANEL_RADIUS, UiTheme.PANEL_SHADOW);
            roundedRect(rect.x(), rect.y(), rect.width(), rect.height(), UiTheme.PANEL_RADIUS, UiTheme.PANEL_BORDER);
            roundedRect(rect.x() + 1, rect.y() + 1, rect.width() - 2, rect.height() - 2,
                UiTheme.PANEL_RADIUS - 1, UiTheme.PANEL);
        }

        public void verticalGradient(float x, float y, float width, float height, float radius, int top, int bottom) {
            NVGPaint paint = NVGPaint.calloc(stack);
            NanoVG.nvgLinearGradient(vg, x, y, x, y + height, color(top), color(bottom), paint);
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgRoundedRect(vg, x, y, width, height, radius);
            NanoVG.nvgFillPaint(vg, paint);
            NanoVG.nvgFill(vg);
        }

        public void resetIcon(float x, float y, float size, int color) {
            float scale = size / 24f;
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgMoveTo(vg, x + 2 * scale, y + 10 * scale);
            NanoVG.nvgBezierTo(vg, x + 2 * scale, y + 10 * scale, x + 4.005f * scale, y + 7.268f * scale,
                x + 5.634f * scale, y + 5.638f * scale);
            NanoVG.nvgBezierTo(vg, x + 7.263f * scale, y + 4.008f * scale, x + 9.514f * scale, y + 3 * scale,
                x + 12 * scale, y + 3 * scale);
            NanoVG.nvgBezierTo(vg, x + 16.971f * scale, y + 3 * scale, x + 21 * scale, y + 7.029f * scale,
                x + 21 * scale, y + 12 * scale);
            NanoVG.nvgBezierTo(vg, x + 21 * scale, y + 16.971f * scale, x + 16.971f * scale, y + 21 * scale,
                x + 12 * scale, y + 21 * scale);
            NanoVG.nvgBezierTo(vg, x + 7.897f * scale, y + 21 * scale, x + 4.435f * scale, y + 18.254f * scale,
                x + 3.352f * scale, y + 14.5f * scale);
            NanoVG.nvgMoveTo(vg, x + 2 * scale, y + 10 * scale);
            NanoVG.nvgLineTo(vg, x + 2 * scale, y + 4 * scale);
            NanoVG.nvgMoveTo(vg, x + 2 * scale, y + 10 * scale);
            NanoVG.nvgLineTo(vg, x + 8 * scale, y + 10 * scale);
            NanoVG.nvgStrokeWidth(vg, 2 * scale);
            NanoVG.nvgLineCap(vg, NanoVG.NVG_ROUND);
            NanoVG.nvgLineJoin(vg, NanoVG.NVG_ROUND);
            NanoVG.nvgStrokeColor(vg, color(color));
            NanoVG.nvgStroke(vg);
        }

        public void chevron(float x, float y, float size, boolean up, int color) {
            float direction = up ? -1 : 1;
            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgMoveTo(vg, x, y - direction * size * .25f);
            NanoVG.nvgLineTo(vg, x + size / 2, y + direction * size * .25f);
            NanoVG.nvgLineTo(vg, x + size, y - direction * size * .25f);
            NanoVG.nvgStrokeWidth(vg, 1.4f);
            NanoVG.nvgStrokeColor(vg, color(color));
            NanoVG.nvgStroke(vg);
        }

        private NVGColor color(int argb) {
            return NanoVG.nvgRGBA((byte) (argb >> 16), (byte) (argb >> 8), (byte) argb, (byte) (argb >> 24), NVGColor.calloc(stack));
        }
    }
}
