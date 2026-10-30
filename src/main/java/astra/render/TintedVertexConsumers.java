/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.rendertype.RenderType
 */
package astra.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public record TintedVertexConsumers(MultiBufferSource delegate, int tint) implements MultiBufferSource
{
    public VertexConsumer method_73477(RenderType layer) {
        return new Tinted(this.delegate.method_73477(layer), this.tint);
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

        public VertexConsumer method_22912(float x, float y, float z) {
            this.delegate.method_22912(x, y, z);
            return this;
        }

        public VertexConsumer method_1336(int red, int green, int blue, int alpha) {
            int c = this.mul(alpha << 24 | red << 16 | green << 8 | blue);
            this.delegate.method_1336(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, c >>> 24);
            return this;
        }

        public VertexConsumer method_39415(int argb) {
            this.delegate.method_39415(this.mul(argb));
            return this;
        }

        public VertexConsumer method_75298(float width) {
            this.delegate.method_75298(width);
            return this;
        }

        public VertexConsumer method_22913(float u, float v) {
            this.delegate.method_22913(u, v);
            return this;
        }

        public VertexConsumer method_60796(int u, int v) {
            this.delegate.method_60796(u, v);
            return this;
        }

        public VertexConsumer method_22921(int u, int v) {
            this.delegate.method_22921(u, v);
            return this;
        }

        public VertexConsumer method_22914(float x, float y, float z) {
            this.delegate.method_22914(x, y, z);
            return this;
        }

        public void method_23919(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny, float nz) {
            this.delegate.method_23919(x, y, z, this.mul(color), u, v, overlay, light, nx, ny, nz);
        }
    }
}

