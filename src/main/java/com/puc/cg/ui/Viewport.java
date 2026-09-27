package com.puc.cg.ui;

import com.puc.cg.commons.models.Point2D;
import lombok.Getter;

@Getter
public class Viewport {
    private static final double MIN_ZOOM = 0.25;
    private static final double INITIAL_ZOOM = 4.0;
    private static final double MAX_ZOOM = 32.0;
    private static final double ZOOM_STEP = 1.1;

    private double zoom = INITIAL_ZOOM;
    private double panX = 0;
    private double panY = 0;

    public void panBy(int dxScreen, int dyScreen) {
        panX += dxScreen;
        panY += dyScreen;
    }

    public void zoomAt(int screenX, int screenY, double notches, int framebufferWidth, int framebufferHeight) {
        double factor = Math.pow(ZOOM_STEP, -notches);
        double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));

        double modelX = toModelX(screenX, framebufferWidth);
        double modelY = toModelY(screenY, framebufferHeight);

        zoom = newZoom;
        panX = screenX - (modelX + halfWidth(framebufferWidth)) * zoom;
        panY = screenY - (halfHeight(framebufferHeight) - modelY) * zoom;
    }

    public Point2D toModelPoint(int screenX, int screenY, int framebufferWidth, int framebufferHeight) {
        return new Point2D(toModelX(screenX, framebufferWidth), toModelY(screenY, framebufferHeight));
    }

    public double toScreenX(double modelX, int framebufferWidth) {
        return (modelX + halfWidth(framebufferWidth)) * zoom + panX;
    }

    public double toScreenY(double modelY, int framebufferHeight) {
        return (halfHeight(framebufferHeight) - modelY) * zoom + panY;
    }

    private double toModelX(int screenX, int framebufferWidth) {
        return (screenX - panX) / zoom - halfWidth(framebufferWidth);
    }

    private double toModelY(int screenY, int framebufferHeight) {
        return halfHeight(framebufferHeight) - (screenY - panY) / zoom;
    }

    private int halfWidth(int framebufferWidth) {
        return framebufferWidth / 2;
    }

    private int halfHeight(int framebufferHeight) {
        return framebufferHeight / 2;
    }
}
