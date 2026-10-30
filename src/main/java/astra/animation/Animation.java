/*
 * Decompiled with CFR 0.152.
 */
package astra.animation;

import astra.animation.AnimationUtils;

public final class Animation {
    private float value;
    private final float speed;

    public Animation(float initial, float speed) {
        this.value = initial;
        this.speed = speed;
    }

    public float update(float target, float dt) {
        float s = this.speed * AnimationUtils.speedMultiplier();
        if (s <= 0.0f || dt <= 0.0f) {
            this.value = target;
            return this.value;
        }
        this.value += (target - this.value) * (1.0f - (float)Math.exp(-s * dt));
        if (Math.abs(target - this.value) < 5.0E-4f) {
            this.value = target;
        }
        return this.value;
    }

    public float get() {
        return this.value;
    }

    public void set(float v) {
        this.value = v;
    }
}

