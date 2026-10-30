/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.entity.state.EntityRenderState
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  org.joml.Matrix3x2fStack
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 */
package astra.gui.pages;

import astra.animation.Animation;
import astra.gui.UiContext;
import astra.gui.components.ModuleCard;
import astra.gui.components.UiDraw;
import astra.gui.pages.ModulesPage;
import astra.gui.screens.ClickGuiScreen;
import astra.module.Category;
import astra.module.Module;
import astra.module.ModuleManager;
import astra.render.ColorUtil;
import astra.render.Fonts;
import astra.render.Icon;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CosmeticsPage
extends ModulesPage {
    private static final float PREVIEW_W = 230.0f;
    private float yaw = 180.0f;
    public static Float devYaw;
    private boolean dragging;
    private double lastMx;
    private boolean autoRotate = true;
    private float previewTop;
    private final Animation[] btnHover = new Animation[]{new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f), new Animation(0.0f, 16.0f)};

    public CosmeticsPage(ClickGuiScreen screen) {
        super(screen, Category.COSMETICS);
    }

    @Override
    protected float layout(float top) {
        this.previewTop = top;
        float cardsX = this.x + 230.0f + 8.0f;
        float end = this.layoutCards(this.cards, cardsX, top, this.w - 230.0f - 8.0f);
        return Math.max(end - top, this.previewHeight());
    }

    private float previewHeight() {
        return Math.min(this.h, 330.0f);
    }

    @Override
    protected void renderContent(UiContext ui) {
        this.renderPreview(ui);
        for (ModuleCard card : this.cards) {
            if (!(card.y + card.h >= this.y) || !(card.y <= this.y + this.h)) continue;
            card.render(ui);
        }
    }

    private void renderPreview(UiContext ui) {
        Theme t = ui.theme;
        float px = this.x;
        float py = this.previewTop;
        float pw = 230.0f;
        float ph = this.previewHeight();
        UiDraw.card(ui.ctx, t, px, py, pw, ph, 10.0f, 0.0f, true);
        Render2D.roundRect(ui.ctx, px, py, pw, ph, 10.0f, Paint.vertical(py, py + ph, ColorUtil.withAlpha(t.accent(), 0.1f), ColorUtil.withAlpha(t.accent2(), 0.16f)));
        float cx = px + pw / 2.0f;
        float floorY = py + ph - 52.0f;
        Matrix3x2fStack mat = ui.ctx.pose();
        mat.pushMatrix();
        mat.translate(cx, floorY);
        mat.scale(1.0f, 0.28f);
        Render2D.shadow(ui.ctx, -46.0f, -46.0f, 92.0f, 92.0f, 46.0f, 22.0f, ColorUtil.withAlpha(t.accent2(), 0.16f * UiDraw.glowStrength()));
        Render2D.ring(ui.ctx, 0.0f, 0.0f, 62.0f, 2.4f, ColorUtil.withAlpha(t.accent2(), 0.85f));
        Render2D.ring(ui.ctx, 0.0f, 0.0f, 48.0f, 1.6f, ColorUtil.withAlpha(t.accent(), 0.6f));
        mat.popMatrix();
        if (this.autoRotate && !this.dragging) {
            this.yaw += ui.dt * 25.0f;
        }
        if (devYaw != null) {
            this.yaw = devYaw.floatValue();
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            float size = 70.0f;
            float center = floorY - size * 0.9f;
            CosmeticsPage.drawPlayer(ui.ctx, (LivingEntity)mc.player, px + 6.0f, center - 118.0f, px + pw - 6.0f, center + 118.0f, size, this.yaw);
        } else {
            Render2D.sparkle(ui.ctx, cx, py + ph * 0.38f, 22.0f, 0.0f, ColorUtil.withAlpha(t.accent2(), 0.8f));
            Fonts.drawCentered(ui.ctx, "\u0417\u0430\u0439\u0434\u0438\u0442\u0435 \u0432 \u043c\u0438\u0440,", cx, py + ph * 0.38f + 34.0f, 7.5f, t.text(), true);
            Fonts.drawCentered(ui.ctx, "\u0447\u0442\u043e\u0431\u044b \u0443\u0432\u0438\u0434\u0435\u0442\u044c 3D-\u043f\u0440\u0435\u0432\u044c\u044e", cx, py + ph * 0.38f + 45.0f, 7.5f, t.textDim(), false);
        }
        float by = py + ph - 28.0f;
        float bw = 30.0f;
        float gap = 8.0f;
        float bx = cx - (bw * 3.0f + gap * 2.0f) / 2.0f;
        Icon[] icons = new Icon[]{Icon.USER, Icon.RESET, Icon.SPARKLE};
        for (int i = 0; i < 3; ++i) {
            float x0 = bx + (float)i * (bw + gap);
            float hv = this.btnHover[i].update(ui.hover(x0, by, bw, 22.0f) ? 1.0f : 0.0f, ui.dt);
            boolean active = i == 2 && this.autoRotate;
            UiDraw.iconButton(ui.ctx, t, x0, by, 22.0f, icons[i], active ? 1.0f : hv);
        }
        Fonts.drawCentered(ui.ctx, "\u0422\u044f\u043d\u0438\u0442\u0435 \u043c\u044b\u0448\u044c\u044e, \u0447\u0442\u043e\u0431\u044b \u0432\u0440\u0430\u0449\u0430\u0442\u044c", cx, py + 8.0f, 6.0f, t.textMuted(), false);
    }

    private static void drawPlayer(GuiGraphics ctx, LivingEntity entity, float x1, float y1, float x2, float y2, float size, float yaw) {
        Matrix3x2fStack m = ctx.pose();
        Vector2f a = m.transformPosition(x1, y1, new Vector2f());
        Vector2f b = m.transformPosition(x2, y2, new Vector2f());
        float scale = (float)Math.sqrt(m.m00 * m.m00 + m.m01 * m.m01);
        int sx1 = Math.round(a.x);
        int sy1 = Math.round(a.y);
        int sx2 = Math.round(b.x);
        int sy2 = Math.round(b.y);
        float bodyYaw = entity.yBodyRot;
        float entYaw = entity.getYRot();
        float pitch = entity.getXRot();
        float headYaw = entity.yHeadRot;
        float lastHeadYaw = entity.yHeadRotO;
        entity.yBodyRot = 180.0f + yaw;
        entity.setYRot(180.0f + yaw);
        entity.setXRot(0.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
        Quaternionf rotation = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf camera = new Quaternionf().rotateX((float)Math.toRadians(-8.0));
        rotation.mul((Quaternionfc)camera);
        float entityScale = entity.getScale();
        Vector3f translation = new Vector3f(0.0f, entity.getBbHeight() / 2.0f + 0.0625f * entityScale, 0.0f);
        EntityRenderState renderState = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer((Entity)entity).createRenderState((Entity)entity, 1.0f);
        renderState.lightCoords = 0xF000F0;
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        ctx.submitEntityRenderState(renderState, size * scale / entityScale, translation, rotation, camera, sx1, sy1, sx2, sy2);
        entity.yBodyRot = bodyYaw;
        entity.setYRot(entYaw);
        entity.setXRot(pitch);
        entity.yHeadRot = headYaw;
        entity.yHeadRotO = lastHeadYaw;
    }

    @Override
    public boolean mouseClicked(UiContext ui, double mx, double my, int button) {
        if (!this.inView(mx, my)) {
            return false;
        }
        float px = this.x;
        float py = this.previewTop;
        float pw = 230.0f;
        float ph = this.previewHeight();
        if (mx >= (double)px && mx < (double)(px + pw) && my >= (double)py && my < (double)(py + ph)) {
            float cx = px + pw / 2.0f;
            float by = py + ph - 28.0f;
            float bw = 30.0f;
            float gap = 8.0f;
            float bx = cx - (bw * 3.0f + gap * 2.0f) / 2.0f;
            for (int i = 0; i < 3; ++i) {
                float x0 = bx + (float)i * (bw + gap);
                if (!(mx >= (double)x0) || !(mx < (double)(x0 + 22.0f)) || !(my >= (double)by) || !(my < (double)(by + 22.0f))) continue;
                switch (i) {
                    case 0: {
                        this.yaw = 0.0f;
                        break;
                    }
                    case 1: {
                        this.yaw = 180.0f;
                        break;
                    }
                    default: {
                        this.autoRotate = !this.autoRotate;
                    }
                }
                return true;
            }
            this.dragging = true;
            this.lastMx = mx;
            return true;
        }
        return super.mouseClicked(ui, mx, my, button);
    }

    @Override
    public boolean mouseDragged(UiContext ui, double mx, double my, int button) {
        if (this.dragging) {
            this.yaw += (float)(mx - this.lastMx) * 1.4f;
            this.lastMx = mx;
            return true;
        }
        return super.mouseDragged(ui, mx, my, button);
    }

    @Override
    public void mouseReleased(UiContext ui, double mx, double my, int button) {
        this.dragging = false;
        super.mouseReleased(ui, mx, my, button);
    }

    private static List<Module> modules() {
        return ModuleManager.byCategory(Category.COSMETICS);
    }
}

