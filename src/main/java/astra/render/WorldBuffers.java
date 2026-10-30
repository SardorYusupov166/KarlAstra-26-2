package astra.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

/** Records the mod's geometry for Minecraft's deferred submit phase. */
public interface WorldBuffers {
    VertexConsumer getBuffer(RenderType type);

    static BufferSource forContext(LevelRenderContext context) {
        return new BufferSource(context.submitNodeCollector());
    }

    static void drawText(Font font, Component text, float x, float y, int color,
                         boolean shadow, Matrix4f matrix, WorldBuffers buffers,
                         Font.DisplayMode mode, int background, int light) {
        drawText(font, text.getVisualOrderText(), x, y, color, shadow, matrix, buffers, mode, background, light);
    }

    static void drawText(Font font, String text, float x, float y, int color,
                         boolean shadow, Matrix4f matrix, WorldBuffers buffers,
                         Font.DisplayMode mode, int background, int light) {
        drawText(font, Component.literal(text), x, y, color, shadow, matrix, buffers, mode, background, light);
    }

    static void drawText(Font font, FormattedCharSequence text, float x, float y, int color,
                         boolean shadow, Matrix4f matrix, WorldBuffers buffers,
                         Font.DisplayMode mode, int background, int light) {
        if (buffers instanceof BufferSource source) {
            PoseStack pose = new PoseStack();
            pose.mulPose(matrix);
            source.collector.submitText(pose, x, y, text, shadow, mode, light, color, background, 0);
        }
    }

    final class BufferSource implements WorldBuffers {
        private final SubmitNodeCollector collector;
        private final Map<RenderType, RecordingConsumer> batches = new LinkedHashMap<>();

        private BufferSource(SubmitNodeCollector collector) { this.collector = collector; }

        public VertexConsumer getBuffer(RenderType type) {
            return batches.computeIfAbsent(type, ignored -> new RecordingConsumer());
        }

        public void endBatch(RenderType type) {
            RecordingConsumer recording = batches.remove(type);
            if (recording == null || recording.vertices.isEmpty()) return;
            List<Vertex> vertices = List.copyOf(recording.vertices);
            collector.submitCustomGeometry(new PoseStack(), type, (pose, consumer) -> {
                for (Vertex v : vertices) {
                    consumer.addVertex(v.x, v.y, v.z).setColor(v.color).setUv(v.u, v.v)
                            .setUv1(v.overlayU, v.overlayV).setUv2(v.lightU, v.lightV)
                            .setNormal(v.nx, v.ny, v.nz).setLineWidth(v.width);
                }
            });
        }

        public void endBatch() {
            for (RenderType type : List.copyOf(batches.keySet())) endBatch(type);
        }
    }

    final class Vertex {
        float x, y, z, u, v, nx, ny, nz, width = 1;
        int color = -1, overlayU, overlayV, lightU, lightV;
    }

    final class RecordingConsumer implements VertexConsumer {
        private final List<Vertex> vertices = new ArrayList<>();
        private Vertex current;
        public VertexConsumer addVertex(float x, float y, float z) {
            current = new Vertex(); current.x=x; current.y=y; current.z=z;
            vertices.add(current); return this;
        }
        public VertexConsumer setColor(int r, int g, int b, int a) { return setColor(a<<24|r<<16|g<<8|b); }
        public VertexConsumer setColor(int color) { current.color=color; return this; }
        public VertexConsumer setUv(float u, float v) { current.u=u; current.v=v; return this; }
        public VertexConsumer setUv1(int u, int v) { current.overlayU=u; current.overlayV=v; return this; }
        public VertexConsumer setUv2(int u, int v) { current.lightU=u; current.lightV=v; return this; }
        public VertexConsumer setNormal(float x, float y, float z) { current.nx=x;current.ny=y;current.nz=z;return this; }
        public VertexConsumer setLineWidth(float width) { current.width=width;return this; }
    }
}
