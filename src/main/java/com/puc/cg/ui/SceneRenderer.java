package com.puc.cg.ui;

import com.puc.cg.algorithms.clipping.ClipWindowAlgorithm;
import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.impl.CircleRasterizerBresenhamImpl;
import com.puc.cg.commons.models.FillAction;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.models.impl.Circle;
import com.puc.cg.commons.models.impl.LineSegment;
import com.puc.cg.commons.util.Framebuffer;
import com.puc.cg.commons.util.Palette;
import com.puc.cg.commons.util.Scene;

import java.util.List;

public class SceneRenderer {
    private static final int MARKER_ARM_LENGTH = 3;
    private static final int DASH_LENGTH = 4;

    private final CircleRasterizerAlgorithm circleRasterizer = new CircleRasterizerBresenhamImpl();

    public void render(
            Framebuffer framebuffer,
            Scene scene,
            Selection selection,
            PreviewState preview,
            LineRasterizerAlgorithm lineRasterizer,
            ClipWindowAlgorithm clipper
    ) {
        framebuffer.clear(Palette.WHITE);

        Window clipWindow = scene.getClipWindow();
        for (Shape shape : scene.getShapes()) {
            Shape toDraw = shape;
            if (clipWindow != null && shape instanceof LineSegment line) {
                toDraw = clipper.clip(line, clipWindow);
                if (toDraw == null) {
                    continue;
                }
            }
            toDraw.draw(framebuffer, lineRasterizer, circleRasterizer, Palette.BLACK);
        }

        for (FillAction fill : scene.getFills()) {
            int seedX = (int) Math.floor(fill.seed().x());
            int seedY = (int) Math.floor(fill.seed().y());
            fill.method().fill(framebuffer, seedX, seedY, fill.fillColor(), fill.refColor(), fill.connectivity());
        }

        if (clipWindow != null) {
            drawWindowOutline(framebuffer, clipWindow, Palette.BLUE);
        }

        Window selectionBounds = selection.bounds();
        if (selectionBounds != null) {
            drawDashedWindowOutline(framebuffer, selectionBounds, Palette.SELECTION_HIGHLIGHT);
        }

        drawActivePreview(framebuffer, lineRasterizer, preview);
    }

    private void drawActivePreview(Framebuffer framebuffer, LineRasterizerAlgorithm lineRasterizer, PreviewState preview) {
        LineSegment previewLine = preview.getLine();
        if (previewLine != null) {
            lineRasterizer.draw(framebuffer, previewLine.start(), previewLine.end(), Palette.RED);
            drawMarker(framebuffer, previewLine.start(), Palette.RED);
        }

        Circle previewCircle = preview.getCircle();
        if (previewCircle != null) {
            circleRasterizer.draw(framebuffer, previewCircle.center(), previewCircle.radius(), Palette.RED);
            drawMarker(framebuffer, previewCircle.center(), Palette.RED);
        }

        Window previewRect = preview.getRect();
        if (previewRect != null) {
            drawWindowOutline(framebuffer, previewRect, Palette.RED);
            drawMarker(framebuffer, new Point2D(previewRect.xMin(), previewRect.yMin()), Palette.RED);
        }

        List<Point2D> polygonVertices = preview.getPolygonVertices();
        if (polygonVertices != null) {
            for (int i = 0; i < polygonVertices.size() - 1; i++) {
                lineRasterizer.draw(framebuffer, polygonVertices.get(i), polygonVertices.get(i + 1), Palette.RED);
            }
            Point2D lastVertex = polygonVertices.get(polygonVertices.size() - 1);
            lineRasterizer.draw(framebuffer, lastVertex, preview.getPolygonCurrent(), Palette.RED);
            drawMarker(framebuffer, polygonVertices.get(0), Palette.RED);
        }
    }

    private void drawMarker(Framebuffer framebuffer, Point2D point, int rgb) {
        int cx = (int) Math.floor(point.x());
        int cy = (int) Math.floor(point.y());
        for (int offset = -MARKER_ARM_LENGTH; offset <= MARKER_ARM_LENGTH; offset++) {
            framebuffer.setPixel(cx + offset, cy, rgb);
            framebuffer.setPixel(cx, cy + offset, rgb);
        }
    }

    private void drawWindowOutline(Framebuffer framebuffer, Window window, int rgb) {
        int xMin = (int) Math.round(window.xMin());
        int xMax = (int) Math.round(window.xMax());
        int yMin = (int) Math.round(window.yMin());
        int yMax = (int) Math.round(window.yMax());

        for (int x = xMin; x <= xMax; x++) {
            framebuffer.setPixel(x, yMin, rgb);
            framebuffer.setPixel(x, yMax, rgb);
        }
        for (int y = yMin; y <= yMax; y++) {
            framebuffer.setPixel(xMin, y, rgb);
            framebuffer.setPixel(xMax, y, rgb);
        }
    }

    private void drawDashedWindowOutline(Framebuffer framebuffer, Window window, int rgb) {
        int xMin = (int) Math.round(window.xMin());
        int xMax = (int) Math.round(window.xMax());
        int yMin = (int) Math.round(window.yMin());
        int yMax = (int) Math.round(window.yMax());

        for (int x = xMin; x <= xMax; x++) {
            if (Math.floorDiv(x, DASH_LENGTH) % 2 == 0) {
                framebuffer.setPixel(x, yMin, rgb);
                framebuffer.setPixel(x, yMax, rgb);
            }
        }
        for (int y = yMin; y <= yMax; y++) {
            if (Math.floorDiv(y, DASH_LENGTH) % 2 == 0) {
                framebuffer.setPixel(xMin, y, rgb);
                framebuffer.setPixel(xMax, y, rgb);
            }
        }
    }

}
