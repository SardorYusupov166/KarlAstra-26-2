/*
 * Decompiled with CFR 0.152.
 */
package astra.animation;

public final class AnimationUtils {
    private static float speedMultiplier = 1.0f;
    private static boolean enabled = true;

    private AnimationUtils() {
    }

    public static void configure(boolean animationsEnabled, float speed) {
        enabled = animationsEnabled;
        speedMultiplier = speed;
    }

    public static float speedMultiplier() {
        return enabled ? speedMultiplier : 0.0f;
    }

    public static boolean enabled() {
        return enabled;
    }

    public static float clamp01(float t) {
        return t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float easeOutCubic(float t) {
        t = AnimationUtils.clamp01(t);
        float f = 1.0f - t;
        return 1.0f - f * f * f;
    }

    public static float easeInOutCubic(float t) {
        return (t = AnimationUtils.clamp01(t)) < 0.5f ? 4.0f * t * t * t : 1.0f - (float)Math.pow(-2.0f * t + 2.0f, 3.0) / 2.0f;
    }

    public static float easeOutBack(float t) {
        t = AnimationUtils.clamp01(t);
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float)Math.pow(t - 1.0f, 3.0) + c1 * (float)Math.pow(t - 1.0f, 2.0);
    }

    public static float pulse(float periodSeconds) {
        double t = (double)(System.currentTimeMillis() % 1000000L) / 1000.0;
        return (float)(0.5 + 0.5 * Math.sin(t * Math.PI * 2.0 / (double)periodSeconds));
    }

    public static float time() {
        return (float)(System.currentTimeMillis() % 10000000L) / 1000.0f;
    }
}

