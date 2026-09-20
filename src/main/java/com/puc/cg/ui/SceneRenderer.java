package com.puc.cg.ui;

import com.puc.cg.algorithms.clipping.ClipWindowAlgorithm;
import com.puc.cg.algorithms.clipping.impl.CohenSutherland;
import com.puc.cg.algorithms.clipping.impl.LiangBarsky;
import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.Framebuffer;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.impl.CircleRasterizerBresenhamImpl;
import com.puc.cg.algorithms.raster.impl.LineRasterizerBresenhamImpl;
import com.puc.cg.algorithms.raster.impl.LineRasterizerDDAImpl;
import com.puc.cg.commons.enums.ClipAlgorithm;
import com.puc.cg.commons.enums.LineAlgorithm;
import com.puc.cg.commons.model.Circle;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Polygon2D;
import com.puc.cg.commons.model.Scene;
import com.puc.cg.commons.model.Window;
import com.puc.cg.commons.util.Palette;

import java.util.List;

public class SceneRenderer {
    private static final int GRID_SPACING = 50;
    private static final int MARKER_ARM_LENGTH = 3;
    private static final int DASH_LENGTH = 4;
    private static final int ORIGIN_MARKER_RADIUS = 4;

    private final LineRasterizerAlgorithm ddaRasterizer = new LineRasterizerDDAImpl();
    private final LineRasterizerAlgorithm bresenhamRasterizer = new LineRasterizerBresenhamImpl();
    private final CircleRasterizerAlgorithm circleRasterizer = new CircleRasterizerBresenhamImpl();
    private final ClipWindowAlgorithm cohenSutherlandClipper = new CohenSutherland();
    private final ClipWindowAlgorithm liangBarskyClipper = new LiangBarsky();

    public void render(
            Framebuffer framebuffer,
            Scene scene,
            Selection selection,
            PreviewState preview,
            LineAlgorithm lineAlgorithm,
            ClipAlgorithm clipAlgorithm
    ) {
        LineRasterizerAlgorithm lineRasterizer = lineRasterizerFor(lineAlgorithm);
        ClipWindowAlgorithm clipper = clipperFor(clipAlgorithm);

        framebuffer.clear(Palette.WHITE);
        drawGrid(framebuffer);

        Window clipWindow = scene.getClipWindow();
        for (LineSegment line : scene.getLines()) {
            LineSegment toDraw = clipWindow == null ? line : clipper.clip(line, clipWindow);
            if (toDraw != null) {
                int color = selection.contains(line) ? Palette.SELECTION_HIGHLIGHT : Palette.BLACK;
                lineRasterizer.draw(framebuffer, toDraw.start(), toDraw.end(), color);
            }
        }

        for (Circle circle : scene.getCircles()) {
            int color = selection.contains(circle) ? Palette.SELECTION_HIGHLIGHT : Palette.BLACK;
            circleRasterizer.draw(framebuffer, circle.center(), circle.radius(), color);
        }

        for (Polygon2D polygon : scene.getPolygons()) {
            int color = selection.contains(polygon) ? Palette.SELECTION_HIGHLIGHT : Palette.BLACK;
            drawPolygon(framebuffer, lineRasterizer, polygon, color);
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

    private LineRasterizerAlgorithm lineRasterizerFor(LineAlgorithm algorithm) {
        return algorithm == LineAlgorithm.DDA ? ddaRasterizer : bresenhamRasterizer;
    }

    private ClipWindowAlgorithm clipperFor(ClipAlgorithm algorithm) {
        return algorithm == ClipAlgorithm.COHEN_SUTHERLAND ? cohenSutherlandClipper : liangBarskyClipper;
    }

    private void drawPolygon(Framebuffer framebuffer, LineRasterizerAlgorithm lineRasterizer, Polygon2D polygon, int rgb) {
        List<Point2D> vertices = polygon.getVertices();
        for (int i = 0; i < vertices.size(); i++) {
            Point2D from = vertices.get(i);
            Point2D to = vertices.get((i + 1) % vertices.size());
            lineRasterizer.draw(framebuffer, from, to, rgb);
        }
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
            drawMarker(framebuffer, new Point2D(previewRect.getXMin(), previewRect.getYMin()), Palette.RED);
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
        int cx = (int) Math.round(point.getX());
        int cy = (int) Math.round(point.getY());
        for (int offset = -MARKER_ARM_LENGTH; offset <= MARKER_ARM_LENGTH; offset++) {
            framebuffer.setPixel(cx + offset, cy, rgb);
            framebuffer.setPixel(cx, cy + offset, rgb);
        }
    }

    private void drawWindowOutline(Framebuffer framebuffer, Window window, int rgb) {
        int xMin = (int) Math.round(window.getXMin());
        int xMax = (int) Math.round(window.getXMax());
        int yMin = (int) Math.round(window.getYMin());
        int yMax = (int) Math.round(window.getYMax());

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
        int xMin = (int) Math.round(window.getXMin());
        int xMax = (int) Math.round(window.getXMax());
        int yMin = (int) Math.round(window.getYMin());
        int yMax = (int) Math.round(window.getYMax());

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

    private void drawGrid(Framebuffer framebuffer) {
        int halfW = framebuffer.getWidth() / 2;
        int halfH = framebuffer.getHeight() / 2;

        for (int x = -halfW; x <= halfW; x += GRID_SPACING) {
            int color = x == 0 ? Palette.GRID_AXIS : Palette.GRID_LINE;
            for (int y = -halfH; y <= halfH; y++) {
                framebuffer.setPixel(x, y, color);
            }
        }
        for (int y = -halfH; y <= halfH; y += GRID_SPACING) {
            int color = y == 0 ? Palette.GRID_AXIS : Palette.GRID_LINE;
            for (int x = -halfW; x <= halfW; x++) {
                framebuffer.setPixel(x, y, color);
            }
        }

        drawOriginMarker(framebuffer);
    }

    private void drawOriginMarker(Framebuffer framebuffer) {
        for (int dx = -ORIGIN_MARKER_RADIUS; dx <= ORIGIN_MARKER_RADIUS; dx++) {
            for (int dy = -ORIGIN_MARKER_RADIUS; dy <= ORIGIN_MARKER_RADIUS; dy++) {
                if (dx * dx + dy * dy <= ORIGIN_MARKER_RADIUS * ORIGIN_MARKER_RADIUS) {
                    framebuffer.setPixel(dx, dy, Palette.ORIGIN_HIGHLIGHT);
                }
            }
        }
    }
}
