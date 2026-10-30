/*
 * Decompiled with CFR 0.152.
 */
package astra.particle;

public enum ParticleSprite {
    PETAL(0),
    SPARKLE(1),
    HEART(2),
    ORB(3),
    STAR(4),
    NOTE(5),
    ANGER(6),
    ZZZ(7);

    public final float u0;
    public final float v0;
    public final float u1;
    public final float v1;

    private ParticleSprite(int index) {
        int col = index % 4;
        int row = index / 4;
        float pad = 9.765625E-4f;
        this.u0 = (float)col / 4.0f + pad;
        this.u1 = (float)(col + 1) / 4.0f - pad;
        this.v0 = (float)row / 2.0f + pad;
        this.v1 = (float)(row + 1) / 2.0f - pad;
    }
}

