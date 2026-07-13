package com.viewmodel.client.gui;

interface NanoPaintable {
    void paint(NanoVGRenderer.Canvas canvas);

    default void paintText(NativeTextRenderer text) {}
}
