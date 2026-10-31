/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 */
package astra.render;

import astra.render.ColorUtil;
import astra.render.Mesh;
import astra.render.Paint;
import astra.render.Render2D;
import astra.theme.Theme;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class Decorations {
    private final Random random = new Random();
    private final float[][] petals;
    private final float[][] sparkles;
    private boolean initialized;

    public Decorations(int count) {
        this.petals = new float[count][];
        this.sparkles = new float[count / 2][];
    }

    private float[] newPetal(float w, float h, boolean anywhere) {
        return new float[]{this.random.nextFloat() * w * 1.1f - w * 0.1f, anywhere ? this.random.nextFloat() * h : -10.0f - this.random.nextFloat() * 40.0f, 8.0f + this.random.nextFloat() * 14.0f, 14.0f + this.random.nextFloat() * 18.0f, this.random.nextFloat() * 360.0f, (this.random.nextFloat() - 0.5f) * 90.0f, 2.6f + this.random.nextFloat() * 3.2f, this.random.nextFloat() * 6.28f, this.random.nextFloat()};
    }

    public void update(float dt, float w, float h) {
        int i;
        if (!this.initialized) {
            for (i = 0; i < this.petals.length; ++i) {
                this.petals[i] = this.newPetal(w, h, true);
            }
            for (i = 0; i < this.sparkles.length; ++i) {
                this.sparkles[i] = new float[]{this.random.nextFloat() * w, this.random.nextFloat() * h, this.random.nextFloat() * 6.28f, 1.2f + this.random.nextFloat() * 2.2f, 0.6f + this.random.nextFloat() * 1.2f};
            }
            this.initialized = true;
        }
        for (i = 0; i < this.petals.length; ++i) {
            float[] p = this.petals[i];
            p[7] = p[7] + dt * 1.6f;
            p[0] = p[0] + (p[2] + (float)Math.sin(p[7]) * 10.0f) * dt;
            p[1] = p[1] + p[3] * dt;
            p[4] = p[4] + p[5] * dt;
            if (!(p[1] > h + 10.0f) && !(p[0] > w + 20.0f)) continue;
            this.petals[i] = this.newPetal(w, h, false);
        }
        for (float[] s : this.sparkles) {
            s[2] = s[2] + dt * s[4];
        }
    }

    public void render(GuiGraphicsExtractor ctx, Theme t, float alpha) {
        if (!this.initialized) {
            return;
        }
        Mesh m = Render2D.begin();
        float px = Render2D.pixel(ctx);
        for (float[] p : this.petals) {
            int base = ColorUtil.lerp(-22308, t.accent2(), p[8] * 0.5f);
            int c = ColorUtil.withAlpha(base, 0.8f * alpha);
            Render2D.addPetal(m, p[0], p[1], p[6], p[4], Paint.vertical(p[1] - p[6], p[1] + p[6], c, ColorUtil.withAlpha(-6412, 0.45f * alpha)), px);
        }
        for (float[] s : this.sparkles) {
            float tw = (float)(0.5 + 0.5 * Math.sin(s[2]));
            Render2D.addSparkle(m, s[0], s[1], s[3] * (0.6f + 0.4f * tw), 0.0f, ColorUtil.withAlpha(-1, 0.6f * tw * alpha), px);
        }
        Render2D.end(ctx);
    }
}

