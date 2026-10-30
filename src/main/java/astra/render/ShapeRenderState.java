/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.gui.render.TextureSetup
 *  net.minecraft.client.gui.render.state.GuiElementRenderState
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 */
package astra.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public final class ShapeRenderState
extends Record
implements GuiElementRenderState {
    private final RenderPipeline pipeline;
    private final TextureSetup textureSetup;
    private final Matrix3x2f pose;
    private final float[] positions;
    private final float[] uvs;
    private final int[] colors;
    private final int offset;
    private final int vertexCount;
    private final ScreenRectangle scissorArea;
    private final ScreenRectangle bounds;

    public ShapeRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float[] positions, float[] uvs, int[] colors, int offset, int vertexCount, ScreenRectangle scissorArea, ScreenRectangle bounds) {
        this.pipeline = pipeline;
        this.textureSetup = textureSetup;
        this.pose = pose;
        this.positions = positions;
        this.uvs = uvs;
        this.colors = colors;
        this.offset = offset;
        this.vertexCount = vertexCount;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    public void method_70917(VertexConsumer consumer) {
        int end = this.offset + this.vertexCount;
        if (this.uvs == null) {
            for (int i = this.offset; i < end; ++i) {
                consumer.method_70815((Matrix3x2fc)this.pose, this.positions[i * 2], this.positions[i * 2 + 1]).method_39415(this.colors[i]);
            }
            return;
        }
        for (int i = this.offset; i < end; ++i) {
            consumer.method_70815((Matrix3x2fc)this.pose, this.positions[i * 2], this.positions[i * 2 + 1]).method_22913(this.uvs[i * 2], this.uvs[i * 2 + 1]).method_39415(this.colors[i]);
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ShapeRenderState.class, "pipeline;textureSetup;pose;positions;uvs;colors;offset;vertexCount;scissorArea;bounds", "pipeline", "textureSetup", "pose", "positions", "uvs", "colors", "offset", "vertexCount", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShapeRenderState.class, "pipeline;textureSetup;pose;positions;uvs;colors;offset;vertexCount;scissorArea;bounds", "pipeline", "textureSetup", "pose", "positions", "uvs", "colors", "offset", "vertexCount", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShapeRenderState.class, "pipeline;textureSetup;pose;positions;uvs;colors;offset;vertexCount;scissorArea;bounds", "pipeline", "textureSetup", "pose", "positions", "uvs", "colors", "offset", "vertexCount", "scissorArea", "bounds"}, this, o);
    }

    public RenderPipeline comp_4055() {
        return this.pipeline;
    }

    public TextureSetup comp_4056() {
        return this.textureSetup;
    }

    public Matrix3x2f pose() {
        return this.pose;
    }

    public float[] positions() {
        return this.positions;
    }

    public float[] uvs() {
        return this.uvs;
    }

    public int[] colors() {
        return this.colors;
    }

    public int offset() {
        return this.offset;
    }

    public int vertexCount() {
        return this.vertexCount;
    }

    public ScreenRectangle comp_4069() {
        return this.scissorArea;
    }

    public ScreenRectangle comp_4274() {
        return this.bounds;
    }
}

