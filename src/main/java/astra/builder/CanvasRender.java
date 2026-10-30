/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.renderer.RenderPipelines
 *  net.minecraft.client.renderer.rendertype.RenderSetup
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.resources.Identifier
 */
package astra.builder;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public final class CanvasRender {
    private static final RenderPipeline[] pipelines = new RenderPipeline[4];
    private static final Map<String, RenderType> layers = new LinkedHashMap<String, RenderType>();

    public static void init() {
    }

    public static RenderType layer(Identifier texture, boolean overlay, boolean opaque) {
        int n = (overlay ? 2 : 0) | (opaque ? 1 : 0);
        String key = String.valueOf(texture) + "/" + n;
        RenderType result = layers.get(key);
        if (result != null) {
            return result;
        }
        result = RenderType.create((String)("astra_canvas_" + n), (RenderSetup)RenderSetup.builder((RenderPipeline)pipelines[n]).withTexture("Sampler0", texture).sortOnUpload().createRenderSetup());
        layers.put(key, result);
        if (layers.size() > 128) {
            layers.remove(layers.keySet().iterator().next());
        }
        return result;
    }

    static {
        for (int i = 0; i < 4; ++i) {
            CanvasRender.pipelines[i] = RenderPipelines.register((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RenderPipelines.GUI_TEXTURED_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"astra", (String)("pipeline/canvas_" + i))).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).withCull(false).withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).withDepthStencilState(new DepthStencilState((i & 2) != 0 ? CompareOp.ALWAYS_PASS : CompareOp.LESS_THAN_OR_EQUAL, (i & 1) != 0)).build());
        }
    }
}

