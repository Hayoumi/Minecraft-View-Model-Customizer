package com.viewmodel;

/** Limits model-local transforms to first-person held-item rendering. */
public final class ViewModelItemRenderScope {
    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private ViewModelItemRenderScope() {}

    public static void enter() {
        DEPTH.set(DEPTH.get() + 1);
    }

    public static void exit() {
        int depth = DEPTH.get() - 1;
        if (depth <= 0) DEPTH.remove();
        else DEPTH.set(depth);
    }

    public static boolean isActive() {
        return DEPTH.get() > 0;
    }
}
