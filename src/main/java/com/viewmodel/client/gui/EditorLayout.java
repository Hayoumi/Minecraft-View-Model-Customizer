package com.viewmodel.client.gui;

public record EditorLayout(Rect profiles, Rect editor) {
    // V3's layout, kept deliberately compact for Minecraft's scaled UI.
    private static final int GAP = 10;
    private static final int PROFILE_WIDTH = 123;
    private static final int PROFILE_HEIGHT = 77;
    private static final int EDITOR_WIDTH = 305;
    private static final int HEIGHT = 234;

    public static EditorLayout centered(int screenWidth, int screenHeight) {
        int availableWidth = Math.max(340, screenWidth - 16);
        int editorWidth = Math.min(EDITOR_WIDTH, Math.max(240, availableWidth - PROFILE_WIDTH - GAP));
        int profileWidth = Math.min(PROFILE_WIDTH, Math.max(100, availableWidth - editorWidth - GAP));
        int editorX = Math.max((screenWidth - editorWidth) / 2, profileWidth + GAP + 8);
        int profileX = editorX - GAP - profileWidth;
        int y = Math.max(8, (screenHeight - HEIGHT) / 2);
        return new EditorLayout(new Rect(profileX, y, profileWidth, PROFILE_HEIGHT),
            new Rect(editorX, y, editorWidth, HEIGHT));
    }

    public record Rect(int x, int y, int width, int height) {
        public int right() { return x + width; }
        public int bottom() { return y + height; }
        public Rect inset(int amount) {
            return new Rect(x + amount, y + amount, width - amount * 2, height - amount * 2);
        }
    }
}
