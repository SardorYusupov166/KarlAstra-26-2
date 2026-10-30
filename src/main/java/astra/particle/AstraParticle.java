/*
 * Decompiled with CFR 0.152.
 */
package astra.particle;

import astra.particle.ParticleSprite;

public final class AstraParticle {
    public final ParticleSprite sprite;
    double x;
    double y;
    double z;
    double prevX;
    double prevY;
    double prevZ;
    double vx;
    double vy;
    double vz;
    float size;
    int color;
    int age;
    int life;
    float roll;
    float prevRoll;
    float spin;
    float gravity;
    float drag = 0.98f;
    float sway;
    float swayPhase;
    boolean twinkle;
    float shrink;

    AstraParticle(ParticleSprite sprite, double x, double y, double z, double vx, double vy, double vz, float size, int color, int life) {
        this.sprite = sprite;
        this.x = this.prevX = x;
        this.y = this.prevY = y;
        this.z = this.prevZ = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.size = size;
        this.color = color;
        this.life = Math.max(1, life);
        this.swayPhase = (float)(Math.random() * Math.PI * 2.0);
    }

    public AstraParticle gravity(float g) {
        this.gravity = g;
        return this;
    }

    public AstraParticle drag(float d) {
        this.drag = d;
        return this;
    }

    public AstraParticle spin(float degPerTick) {
        this.spin = degPerTick;
        this.roll = this.prevRoll = (float)(Math.random() * 360.0);
        return this;
    }

    public AstraParticle sway(float amount) {
        this.sway = amount;
        return this;
    }

    public AstraParticle twinkle() {
        this.twinkle = true;
        return this;
    }

    public AstraParticle shrink(float amount) {
        this.shrink = amount;
        return this;
    }

    void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        this.prevRoll = this.roll;
        this.vy -= (double)this.gravity;
        if (this.sway != 0.0f) {
            float t = (float)this.age * 0.12f + this.swayPhase;
            this.vx += Math.sin(t) * (double)this.sway;
            this.vz += Math.cos(t * 0.8f) * (double)this.sway;
        }
        this.vx *= (double)this.drag;
        this.vy *= (double)this.drag;
        this.vz *= (double)this.drag;
        this.x += this.vx;
        this.y += this.vy;
        this.z += this.vz;
        this.roll += this.spin;
        ++this.age;
    }

    boolean dead() {
        return this.age >= this.life;
    }

    float alpha(float tickDelta) {
        float a = (float)this.age + tickDelta;
        float in = Math.min(1.0f, a / 4.0f);
        float outStart = (float)this.life * 0.65f;
        float out = a < outStart ? 1.0f : Math.max(0.0f, 1.0f - (a - outStart) / ((float)this.life - outStart));
        float tw = this.twinkle ? 0.55f + 0.45f * (float)Math.sin(a * 0.45f + this.swayPhase * 3.0f) : 1.0f;
        return in * out * tw;
    }

    float currentSize(float tickDelta) {
        if (this.shrink <= 0.0f) {
            return this.size;
        }
        float k = ((float)this.age + tickDelta) / (float)this.life;
        return this.size * (1.0f - this.shrink * k);
    }
}

