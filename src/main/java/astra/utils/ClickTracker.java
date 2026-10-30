/*
 * Decompiled with CFR 0.152.
 */
package astra.utils;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ClickTracker {
    private static final Deque<Long> LEFT = new ArrayDeque<Long>();
    private static final Deque<Long> RIGHT = new ArrayDeque<Long>();
    private static long lastLeft;
    private static long lastRight;

    private ClickTracker() {
    }

    public static synchronized void onClick(int button) {
        long now = System.currentTimeMillis();
        if (button == 0) {
            LEFT.addLast(now);
            lastLeft = now;
        } else if (button == 1) {
            RIGHT.addLast(now);
            lastRight = now;
        }
    }

    private static int count(Deque<Long> deque) {
        long cutoff = System.currentTimeMillis() - 1000L;
        while (!deque.isEmpty() && deque.peekFirst() < cutoff) {
            deque.pollFirst();
        }
        return deque.size();
    }

    public static synchronized int leftCps() {
        return ClickTracker.count(LEFT);
    }

    public static synchronized int rightCps() {
        return ClickTracker.count(RIGHT);
    }

    public static long lastClick(int button) {
        return button == 0 ? lastLeft : lastRight;
    }
}

