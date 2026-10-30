/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package astra.utils;

import java.util.Arrays;
import net.minecraft.client.Minecraft;

public final class FrameStats {
    public static final int HISTORY = 60;
    private static final float[] FPS_HISTORY = new float[60];
    private static final float[] FRAME_MS = new float[240];
    private static int fpsCount;
    private static int frameIndex;
    private static int frameCount;
    private static long lastFrameNanos;
    private static long lastSecond;
    private static int framesThisSecond;
    private static float delta;

    private FrameStats() {
    }

    public static void onFrame() {
        long now = System.nanoTime();
        if (lastFrameNanos != 0L) {
            FrameStats.FRAME_MS[FrameStats.frameIndex] = (float)(now - lastFrameNanos) / 1000000.0f;
            delta = Math.min(0.1f, FRAME_MS[frameIndex] / 1000.0f);
            frameIndex = (frameIndex + 1) % FRAME_MS.length;
            frameCount = Math.min(frameCount + 1, FRAME_MS.length);
        }
        lastFrameNanos = now;
        ++framesThisSecond;
        long ms = System.currentTimeMillis();
        if (lastSecond == 0L) {
            lastSecond = ms;
        }
        if (ms - lastSecond >= 1000L) {
            float fps = (float)framesThisSecond * 1000.0f / (float)(ms - lastSecond);
            System.arraycopy(FPS_HISTORY, 1, FPS_HISTORY, 0, 59);
            FrameStats.FPS_HISTORY[59] = fps;
            fpsCount = Math.min(fpsCount + 1, 60);
            framesThisSecond = 0;
            lastSecond = ms;
        }
    }

    public static float delta() {
        return delta;
    }

    public static int fps() {
        return Minecraft.getInstance().getFps();
    }

    public static float[] history() {
        return FPS_HISTORY;
    }

    public static int historySize() {
        return fpsCount;
    }

    public static float min() {
        float min = Float.MAX_VALUE;
        for (int i = 60 - fpsCount; i < 60; ++i) {
            min = Math.min(min, FPS_HISTORY[i]);
        }
        return fpsCount == 0 ? 0.0f : min;
    }

    public static float max() {
        float max = 0.0f;
        for (int i = 60 - fpsCount; i < 60; ++i) {
            max = Math.max(max, FPS_HISTORY[i]);
        }
        return max;
    }

    public static float avg() {
        if (fpsCount == 0) {
            return 0.0f;
        }
        float sum = 0.0f;
        for (int i = 60 - fpsCount; i < 60; ++i) {
            sum += FPS_HISTORY[i];
        }
        return sum / (float)fpsCount;
    }

    public static float avgFrameMs() {
        if (frameCount == 0) {
            return 0.0f;
        }
        float sum = 0.0f;
        for (int i = 0; i < frameCount; ++i) {
            sum += FRAME_MS[i];
        }
        return sum / (float)frameCount;
    }

    public static float p99FrameMs() {
        if (frameCount == 0) {
            return 0.0f;
        }
        float[] copy = new float[frameCount];
        System.arraycopy(FRAME_MS, 0, copy, 0, frameCount);
        Arrays.sort(copy);
        return copy[Math.min(frameCount - 1, (int)((float)frameCount * 0.99f))];
    }

    static {
        delta = 0.016666668f;
    }
}

