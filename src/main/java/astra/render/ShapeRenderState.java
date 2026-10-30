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
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public record ShapeRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, float[] positions, float[] uvs, int[] colors, int offset, int vertexCount, ScreenRectangle scissorArea, ScreenRectangle bounds) implements GuiElementRenderState
{
    public void buildVertices(VertexConsumer consumer) {
        int end = this.offset + this.vertexCount;
        if (this.uvs == null) {
            for (int i = this.offset; i < end; ++i) {
                consumer.addVertexWith2DPose((Matrix3x2fc)this.pose, this.positions[i * 2], this.positions[i * 2 + 1]).setColor(this.colors[i]);
            }
            return;
        }
        for (int i = this.offset; i < end; ++i) {
            consumer.addVertexWith2DPose((Matrix3x2fc)this.pose, this.positions[i * 2], this.positions[i * 2 + 1]).setUv(this.uvs[i * 2], this.uvs[i * 2 + 1]).setColor(this.colors[i]);
        }
    }
}

