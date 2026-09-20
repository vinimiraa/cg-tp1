package com.puc.cg.ui;

import com.puc.cg.commons.models.Point2D;
import lombok.Getter;

@Getter
public class Viewport {
    private static final double MIN_ZOOM = 0.2;
    private static final double MAX_ZOOM = 8.0;
    private static final double ZOOM_STEP = 1.1;

    private double zoom = 1.0;
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
        panX = screenX - (modelX + framebufferWidth / 2.0) * zoom;
        panY = screenY - (modelY + framebufferHeight / 2.0) * zoom;
    }

    public Point2D toModelPoint(int screenX, int screenY, int framebufferWidth, int framebufferHeight) {
        return new Point2D(toModelX(screenX, framebufferWidth), toModelY(screenY, framebufferHeight));
    }

    private double toModelX(int screenX, int framebufferWidth) {
        return (screenX - panX) / zoom - framebufferWidth / 2.0;
    }

    private double toModelY(int screenY, int framebufferHeight) {
        return (screenY - panY) / zoom - framebufferHeight / 2.0;
    }
}
