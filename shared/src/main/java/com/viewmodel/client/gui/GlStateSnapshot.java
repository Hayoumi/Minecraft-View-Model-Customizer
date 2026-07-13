package com.viewmodel.client.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

/**
 * NanoVG talks to OpenGL directly, while Minecraft keeps a cache of the state
 * it last set. Restoring every state NanoVG changes keeps that cache truthful.
 */
final class GlStateSnapshot {
    private final int readFramebuffer;
    private final int drawFramebuffer;
    private final int[] viewport;
    private final int program;
    private final int vertexArray;
    private final int arrayBuffer;
    private final int uniformBuffer;
    private final int uniformBuffer0;
    private final long uniformBuffer0Offset;
    private final long uniformBuffer0Size;
    private final int activeTexture;
    private final int texture0;
    private final int activeTextureBinding;
    private final int unpackAlignment;
    private final int unpackRowLength;
    private final int unpackSkipPixels;
    private final int unpackSkipRows;
    private final boolean blend;
    private final int blendEquationRgb;
    private final int blendEquationAlpha;
    private final int blendSourceRgb;
    private final int blendDestinationRgb;
    private final int blendSourceAlpha;
    private final int blendDestinationAlpha;
    private final boolean cull;
    private final int cullFace;
    private final int frontFace;
    private final boolean depth;
    private final int depthFunction;
    private final boolean depthMask;
    private final boolean scissor;
    private final int[] scissorBox;
    private final boolean stencil;
    private final int stencilWriteMask;
    private final int stencilBackWriteMask;
    private final int stencilFunction;
    private final int stencilReference;
    private final int stencilValueMask;
    private final int stencilBackFunction;
    private final int stencilBackReference;
    private final int stencilBackValueMask;
    private final int stencilFail;
    private final int stencilDepthFail;
    private final int stencilDepthPass;
    private final int stencilBackFail;
    private final int stencilBackDepthFail;
    private final int stencilBackDepthPass;
    private final boolean[] colorMask;

    private GlStateSnapshot() {
        readFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        drawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        viewport = ints(GL11.GL_VIEWPORT);
        program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        vertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        arrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        uniformBuffer = GL11.glGetInteger(GL31.GL_UNIFORM_BUFFER_BINDING);
        uniformBuffer0 = GL30.glGetIntegeri(GL31.GL_UNIFORM_BUFFER_BINDING, 0);
        uniformBuffer0Offset = GL32.glGetInteger64i(GL31.GL_UNIFORM_BUFFER_START, 0);
        uniformBuffer0Size = GL32.glGetInteger64i(GL31.GL_UNIFORM_BUFFER_SIZE, 0);

        activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        activeTextureBinding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        if (activeTexture == GL13.GL_TEXTURE0) {
            texture0 = activeTextureBinding;
        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            GL13.glActiveTexture(activeTexture);
        }

        unpackAlignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        unpackRowLength = GL11.glGetInteger(GL12.GL_UNPACK_ROW_LENGTH);
        unpackSkipPixels = GL11.glGetInteger(GL12.GL_UNPACK_SKIP_PIXELS);
        unpackSkipRows = GL11.glGetInteger(GL12.GL_UNPACK_SKIP_ROWS);
        blend = GL11.glIsEnabled(GL11.GL_BLEND);
        blendEquationRgb = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
        blendEquationAlpha = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
        blendSourceRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        blendDestinationRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        blendSourceAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        blendDestinationAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        cullFace = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
        frontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE);
        depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        depthFunction = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
        scissorBox = ints(GL11.GL_SCISSOR_BOX);
        stencil = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
        stencilWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
        stencilBackWriteMask = GL11.glGetInteger(GL20.GL_STENCIL_BACK_WRITEMASK);
        stencilFunction = GL11.glGetInteger(GL11.GL_STENCIL_FUNC);
        stencilReference = GL11.glGetInteger(GL11.GL_STENCIL_REF);
        stencilValueMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK);
        stencilBackFunction = GL11.glGetInteger(GL20.GL_STENCIL_BACK_FUNC);
        stencilBackReference = GL11.glGetInteger(GL20.GL_STENCIL_BACK_REF);
        stencilBackValueMask = GL11.glGetInteger(GL20.GL_STENCIL_BACK_VALUE_MASK);
        stencilFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL);
        stencilDepthFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL);
        stencilDepthPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS);
        stencilBackFail = GL11.glGetInteger(GL20.GL_STENCIL_BACK_FAIL);
        stencilBackDepthFail = GL11.glGetInteger(GL20.GL_STENCIL_BACK_PASS_DEPTH_FAIL);
        stencilBackDepthPass = GL11.glGetInteger(GL20.GL_STENCIL_BACK_PASS_DEPTH_PASS);
        colorMask = colorMask();
    }

    static GlStateSnapshot capture() {
        return new GlStateSnapshot();
    }

    void restore() {
        GL20.glUseProgram(program);
        GL30.glBindVertexArray(vertexArray);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, arrayBuffer);
        if (uniformBuffer0 == 0 || uniformBuffer0Size == 0) {
            GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, uniformBuffer0);
        } else {
            GL30.glBindBufferRange(GL31.GL_UNIFORM_BUFFER, 0, uniformBuffer0, uniformBuffer0Offset, uniformBuffer0Size);
        }
        GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, uniformBuffer);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0);
        GL13.glActiveTexture(activeTexture);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, activeTextureBinding);

        GL20.glBlendEquationSeparate(blendEquationRgb, blendEquationAlpha);
        GL14.glBlendFuncSeparate(blendSourceRgb, blendDestinationRgb, blendSourceAlpha, blendDestinationAlpha);
        setEnabled(GL11.GL_BLEND, blend);
        GL11.glCullFace(cullFace);
        GL11.glFrontFace(frontFace);
        setEnabled(GL11.GL_CULL_FACE, cull);
        GL11.glDepthFunc(depthFunction);
        GL11.glDepthMask(depthMask);
        setEnabled(GL11.GL_DEPTH_TEST, depth);
        GL11.glScissor(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3]);
        setEnabled(GL11.GL_SCISSOR_TEST, scissor);
        GL11.glColorMask(colorMask[0], colorMask[1], colorMask[2], colorMask[3]);
        GL20.glStencilMaskSeparate(GL11.GL_FRONT, stencilWriteMask);
        GL20.glStencilMaskSeparate(GL11.GL_BACK, stencilBackWriteMask);
        GL20.glStencilFuncSeparate(GL11.GL_FRONT, stencilFunction, stencilReference, stencilValueMask);
        GL20.glStencilFuncSeparate(GL11.GL_BACK, stencilBackFunction, stencilBackReference, stencilBackValueMask);
        GL20.glStencilOpSeparate(GL11.GL_FRONT, stencilFail, stencilDepthFail, stencilDepthPass);
        GL20.glStencilOpSeparate(GL11.GL_BACK, stencilBackFail, stencilBackDepthFail, stencilBackDepthPass);
        setEnabled(GL11.GL_STENCIL_TEST, stencil);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, unpackAlignment);
        GL11.glPixelStorei(GL12.GL_UNPACK_ROW_LENGTH, unpackRowLength);
        GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_PIXELS, unpackSkipPixels);
        GL11.glPixelStorei(GL12.GL_UNPACK_SKIP_ROWS, unpackSkipRows);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
        GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    private static int[] ints(int parameter) {
        int[] values = new int[4];
        GL11.glGetIntegerv(parameter, values);
        return values;
    }

    private static boolean[] colorMask() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer values = stack.malloc(4);
            GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, values);
            return new boolean[] { values.get(0) != 0, values.get(1) != 0, values.get(2) != 0, values.get(3) != 0 };
        }
    }

    private static void setEnabled(int capability, boolean enabled) {
        if (enabled) GL11.glEnable(capability);
        else GL11.glDisable(capability);
    }
}
