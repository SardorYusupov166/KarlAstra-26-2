/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.player.PlayerModel
 *  net.minecraft.client.renderer.SubmitNodeCollector
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.entity.player.AvatarRenderer
 *  net.minecraft.client.renderer.entity.state.AvatarRenderState
 *  net.minecraft.client.renderer.rendertype.RenderType
 *  net.minecraft.client.renderer.rendertype.RenderTypes
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.resources.Identifier
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  org.joml.Quaternionfc
 */
package astra.cosmetic;

import astra.AstraClient;
import astra.cosmetic.CosmeticTextures;
import astra.module.ModuleManager;
import astra.module.modules.cosmetic.AuraCosmetic;
import astra.module.modules.cosmetic.HaloCosmetic;
import astra.module.modules.cosmetic.WingsCosmetic;
import astra.online.RemoteCosmetics;
import astra.render.ArtManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionfc;

public final class CosmeticRenderer {
    private CosmeticRenderer() {
    }

    public static void init() {
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, renderer, helper, context) -> {
            if (renderer instanceof AvatarRenderer) {
                AvatarRenderer playerRenderer = (AvatarRenderer)renderer;
                helper.register((RenderLayer)new Feature((RenderLayerParent<AvatarRenderState, PlayerModel>)playerRenderer));
            }
        });
    }

    static final class Feature
    extends RenderLayer<AvatarRenderState, PlayerModel> {
        Feature(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
            super(context);
        }

        public void submit(PoseStack matrices, SubmitNodeCollector vcp, int light, AvatarRenderState state, float limbAngle, float limbDistance) {
            block13: {
                AuraCosmetic aura;
                HaloCosmetic halo;
                WingsCosmetic wings;
                boolean remote;
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null || mc.level == null || state.isInvisible) {
                    return;
                }
                float time = (float)(System.currentTimeMillis() % 1000000L) / 1000.0f;
                boolean bl = remote = state.id != mc.player.getId();
                if (!remote) {
                    wings = ModuleManager.get(WingsCosmetic.class);
                    halo = ModuleManager.get(HaloCosmetic.class);
                    aura = ModuleManager.get(AuraCosmetic.class);
                } else {
                    Entity entity = mc.level.getEntity(state.id);
                    if (!(entity instanceof Player)) {
                        return;
                    }
                    Player other = (Player)entity;
                    wings = (WingsCosmetic)RemoteCosmetics.get(other, "wings");
                    halo = (HaloCosmetic)RemoteCosmetics.get(other, "halo");
                    aura = (AuraCosmetic)RemoteCosmetics.get(other, "aura");
                    if (wings == null && halo == null && aura == null) {
                        return;
                    }
                }
                try {
                    if (wings != null && wings.isEnabled()) {
                        this.renderWings(matrices, vcp, light, wings, time, state);
                    }
                    if (halo != null && halo.isEnabled()) {
                        this.renderHalo(matrices, vcp, halo, time);
                    }
                    if (aura != null && aura.isEnabled() && aura.drawCircle()) {
                        this.renderAura(matrices, vcp, aura, time);
                    }
                }
                catch (RuntimeException e) {
                    if (remote) {
                        AstraClient.LOGGER.debug("\u041e\u0448\u0438\u0431\u043a\u0430 \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u043a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0438 \u0434\u0440\u0443\u0433\u0430", (Throwable)e);
                        return;
                    }
                    AstraClient.LOGGER.error("\u041e\u0448\u0438\u0431\u043a\u0430 \u043e\u0442\u0440\u0438\u0441\u043e\u0432\u043a\u0438 \u043a\u043e\u0441\u043c\u0435\u0442\u0438\u043a\u0438, \u044d\u0444\u0444\u0435\u043a\u0442\u044b \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u044b", (Throwable)e);
                    if (wings != null) {
                        wings.setEnabled(false);
                    }
                    if (halo != null) {
                        halo.setEnabled(false);
                    }
                    if (aura == null) break block13;
                    aura.setEnabled(false);
                }
            }
        }

        private static int dim(int color) {
            int a = color >>> 24;
            int r = (int)((float)(color >> 16 & 0xFF) * 0.42f + 23.199999f);
            int g = (int)((float)(color >> 8 & 0xFF) * 0.32f + 12.24f);
            int b = (int)((float)(color & 0xFF) * 0.5f + 35.0f);
            return a << 24 | r << 16 | g << 8 | b;
        }

        private void renderWings(PoseStack matrices, SubmitNodeCollector vcp, int light, WingsCosmetic wings, float time, AvatarRenderState state) {
            boolean art;
            Identifier tex = CosmeticTextures.WING_ART;
            boolean bl = art = wings.design().equals("Astra") && ArtManager.exists(tex);
            if (!art) {
                tex = CosmeticTextures.wing(wings.design().equals("Astra") ? "\u0410\u043d\u0433\u0435\u043b\u044c\u0441\u043a\u0438\u0435" : wings.design());
            }
            RenderType layer = wings.solid() ? RenderTypes.entityCutout((Identifier)tex) : (wings.glow() ? RenderTypes.entityTranslucentEmissive((Identifier)tex) : RenderTypes.entityTranslucent((Identifier)tex));
            int lightValue = wings.glow() ? 0xF000F0 : light;
            int color = wings.color();
            float scale = wings.scale();
            float speed = state.isFallFlying ? 0.5f : 1.0f;
            float flap = (float)Math.sin((double)(time * wings.flapSpeed() * speed) * Math.PI) * wings.flapAmount();
            if (state.isDiscrete) {
                flap -= 10.0f;
            }
            matrices.pushPose();
            ((PlayerModel)this.getParentModel()).body.translateAndRotate(matrices);
            matrices.translate(0.0f, 0.16f - wings.heightOffset(), 0.22f + wings.distanceOffset());
            if (wings.pitchDegrees() != 0.0f) {
                matrices.mulPose((Quaternionfc)Axis.XP.rotationDegrees(wings.pitchDegrees()));
            }
            for (int side = -1; side <= 1; side += 2) {
                matrices.pushPose();
                matrices.mulPose((Quaternionfc)Axis.YP.rotationDegrees((float)(-side) * (wings.sweepDegrees() + flap)));
                matrices.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)side * -wings.liftDegrees()));
                float w = 1.05f * scale;
                float h = 1.05f * scale;
                float base = Math.max(0.0f, 0.04f + wings.spreadOffset());
                float x0 = (float)side * base;
                float x1 = (float)side * (base + w);
                float y0 = art ? -h * 0.62f : -h * 0.55f;
                float y1 = art ? h * 0.38f : h * 0.45f;
                float u0 = 0.0f;
                float u1 = 1.0f;
                float eps = 0.004f;
                int inner = Feature.dim(color);
                vcp.submitCustomGeometry(matrices, layer, (e, vc) -> {
                    Feature.vertex(vc, e, x0, y0, eps, u0, 0.0f, color, lightValue);
                    Feature.vertex(vc, e, x0, y1, eps, u0, 1.0f, color, lightValue);
                    Feature.vertex(vc, e, x1, y1, eps, u1, 1.0f, color, lightValue);
                    Feature.vertex(vc, e, x1, y0, eps, u1, 0.0f, color, lightValue);
                    Feature.vertex(vc, e, x1, y0, -eps, u1, 0.0f, inner, lightValue);
                    Feature.vertex(vc, e, x1, y1, -eps, u1, 1.0f, inner, lightValue);
                    Feature.vertex(vc, e, x0, y1, -eps, u0, 1.0f, inner, lightValue);
                    Feature.vertex(vc, e, x0, y0, -eps, u0, 0.0f, inner, lightValue);
                });
                matrices.popPose();
            }
            matrices.popPose();
        }

        private void renderHalo(PoseStack matrices, SubmitNodeCollector vcp, HaloCosmetic halo, float time) {
            RenderType layer = RenderTypes.entityTranslucentEmissive((Identifier)CosmeticTextures.white());
            int color = halo.color();
            matrices.pushPose();
            ((PlayerModel)this.getParentModel()).body.translateAndRotate(matrices);
            float bob = (float)Math.sin((double)time * 2.2) * 0.02f;
            matrices.translate(0.0f, -0.66f + bob, 0.0f);
            int full = 0xF000F0;
            float big = 0.22f;
            float tube = 0.022f;
            int segs = 48;
            int ring = 8;
            vcp.submitCustomGeometry(matrices, layer, (e, vc) -> {
                for (int i = 0; i < segs; ++i) {
                    double a0 = Math.PI * 2 * (double)i / (double)segs;
                    double a1 = Math.PI * 2 * (double)(i + 1) / (double)segs;
                    for (int j = 0; j < ring; ++j) {
                        double b0 = Math.PI * 2 * (double)j / (double)ring;
                        double b1 = Math.PI * 2 * (double)(j + 1) / (double)ring;
                        Feature.torus(vc, e, a0, b0, big, tube, color, full);
                        Feature.torus(vc, e, a0, b1, big, tube, color, full);
                        Feature.torus(vc, e, a1, b1, big, tube, color, full);
                        Feature.torus(vc, e, a1, b0, big, tube, color, full);
                    }
                }
            });
            matrices.popPose();
        }

        private static void torus(VertexConsumer vc, PoseStack.Pose e, double a, double b, float big, float tube, int color, int light) {
            float rr = big + tube * (float)Math.cos(b);
            float x = rr * (float)Math.cos(a);
            float z = rr * (float)Math.sin(a);
            float y = tube * (float)Math.sin(b);
            Feature.vertex(vc, e, x, y, z, 0.5f, 0.5f, color, light);
        }

        private void renderAura(PoseStack matrices, SubmitNodeCollector vcp, AuraCosmetic aura, float time) {
            RenderType layer = RenderTypes.entityTranslucentEmissive((Identifier)CosmeticTextures.auraCircle());
            int color = aura.color();
            float pulse = 0.9f + 0.1f * (float)Math.sin(time * 3.0f);
            float r = 0.85f * aura.size() * pulse;
            matrices.pushPose();
            matrices.translate(0.0f, 1.44f, 0.0f);
            matrices.mulPose((Quaternionfc)Axis.YP.rotationDegrees(time * 25.0f));
            int full = 0xF000F0;
            vcp.submitCustomGeometry(matrices, layer, (e, vc) -> {
                Feature.vertex(vc, e, -r, 0.0f, -r, 0.0f, 0.0f, color, full);
                Feature.vertex(vc, e, -r, 0.0f, r, 0.0f, 1.0f, color, full);
                Feature.vertex(vc, e, r, 0.0f, r, 1.0f, 1.0f, color, full);
                Feature.vertex(vc, e, r, 0.0f, -r, 1.0f, 0.0f, color, full);
            });
            matrices.popPose();
        }

        private static void vertex(VertexConsumer vc, PoseStack.Pose e, float x, float y, float z, float u, float v, int color, int light) {
            vc.addVertex(e, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(e, 0.0f, -1.0f, 0.0f);
        }
    }
}

