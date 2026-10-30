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
import com.mojang.blaze3d.platform.DepthTestFunction;
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
        result = RenderType.method_75940((String)("astra_canvas_" + n), (RenderSetup)RenderSetup.method_75927((RenderPipeline)pipelines[n]).method_75934("Sampler0", texture).method_75929(4096).method_75937().method_75938());
        layers.put(key, result);
        if (layers.size() > 128) {
            layers.remove(layers.keySet().iterator().next());
        }
        return result;
    }

    static {
        for (int i = 0; i < 4; ++i) {
            CanvasRender.pipelines[i] = RenderPipelines.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RenderPipelines.field_56864}).withLocation(Identifier.method_60655((String)"astra", (String)("pipeline/canvas_" + i))).withVertexFormat(DefaultVertexFormat.field_1575, VertexFormat.Mode.field_27382).withCull(false).withBlend(BlendFunction.TRANSLUCENT).withDepthWrite((i & 1) != 0).withDepthTestFunction((i & 2) != 0 ? DepthTestFunction.NO_DEPTH_TEST : DepthTestFunction.LEQUAL_DEPTH_TEST).build());
        }
    }
}

