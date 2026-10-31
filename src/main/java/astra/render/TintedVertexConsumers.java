/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  astra.render.WorldBuffers
 *  net.minecraft.client.renderer.rendertype.RenderType
 */
package astra.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import astra.render.WorldBuffers;
import net.minecraft.client.renderer.rendertype.RenderType;

public record TintedVertexConsumers(WorldBuffers delegate, int tint) implements WorldBuffers
{
    public VertexConsumer getBuffer(RenderType layer) {
        return new Tinted(this.delegate.getBuffer(layer), this.tint);
    }

    private record Tinted(VertexConsumer delegate, int tint) implements VertexConsumer
    {
        private int mul(int c) {
            int a = (c >>> 24) * (this.tint >>> 24) / 255;
            int r = (c >> 16 & 0xFF) * (this.tint >> 16 & 0xFF) / 255;
            int g = (c >> 8 & 0xFF) * (this.tint >> 8 & 0xFF) / 255;
            int b = (c & 0xFF) * (this.tint & 0xFF) / 255;
            return a << 24 | r << 16 | g << 8 | b;
        }

        public VertexConsumer addVertex(float x, float y, float z) {
            this.delegate.addVertex(x, y, z);
            return this;
        }

        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            int c = this.mul(alpha << 24 | red << 16 | green << 8 | blue);
            this.delegate.setColor(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, c >>> 24);
            return this;
        }

        public VertexConsumer setColor(int argb) {
            this.delegate.setColor(this.mul(argb));
            return this;
        }

        public VertexConsumer setLineWidth(float width) {
            this.delegate.setLineWidth(width);
            return this;
        }

        public VertexConsumer setUv(float u, float v) {
            this.delegate.setUv(u, v);
            return this;
        }

        public VertexConsumer setUv1(int u, int v) {
            this.delegate.setUv1(u, v);
            return this;
        }

        public VertexConsumer setUv2(int u, int v) {
            this.delegate.setUv2(u, v);
            return this;
        }

        public VertexConsumer setNormal(float x, float y, float z) {
            this.delegate.setNormal(x, y, z);
            return this;
        }

        public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
            this.delegate.addVertex(x, y, z, this.mul(color), u, v, overlay, light, nx, ny, nz);
        }
    }
}

