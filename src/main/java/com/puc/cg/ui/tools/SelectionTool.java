package com.puc.cg.ui.tools;

import com.puc.cg.commons.model.Circle;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Polygon2D;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

import java.util.ArrayList;
import java.util.List;

public class SelectionTool implements DrawingTool {

    private Point2D pendingCorner;

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        if (pendingCorner == null) {
            pendingCorner = point;
            context.setPreviewRect(point, point);
            return;
        }

        double xMin = Math.min(pendingCorner.getX(), point.getX());
        double xMax = Math.max(pendingCorner.getX(), point.getX());
        double yMin = Math.min(pendingCorner.getY(), point.getY());
        double yMax = Math.max(pendingCorner.getY(), point.getY());

        List<LineSegment> lines = new ArrayList<>();
        for (LineSegment line : context.getScene().getLines()) {
            if (inside(line.start(), xMin, xMax, yMin, yMax) && inside(line.end(), xMin, xMax, yMin, yMax)) {
                lines.add(line);
            }
        }

        List<Circle> circles = new ArrayList<>();
        for (Circle circle : context.getScene().getCircles()) {
            if (inside(circle.center(), xMin, xMax, yMin, yMax)) {
                circles.add(circle);
            }
        }

        List<Polygon2D> polygons = new ArrayList<>();
        for (Polygon2D polygon : context.getScene().getPolygons()) {
            if (allInside(polygon, xMin, xMax, yMin, yMax)) {
                polygons.add(polygon);
            }
        }

        context.setSelection(lines, circles, polygons);
        pendingCorner = null;
        context.clearPreview();
    }

    @Override
    public void onMouseMoved(Point2D point, DrawingContext context) {
        if (pendingCorner != null) {
            context.setPreviewRect(pendingCorner, point);
        }
    }

    @Override
    public void onCancel(DrawingContext context) {
        pendingCorner = null;
        context.clearPreview();
    }

    @Override
    public void reset() {
        pendingCorner = null;
    }

    private boolean allInside(Polygon2D polygon, double xMin, double xMax, double yMin, double yMax) {
        for (Point2D vertex : polygon.getVertices()) {
            if (!inside(vertex, xMin, xMax, yMin, yMax)) {
                return false;
            }
        }
        return true;
    }

    private boolean inside(Point2D p, double xMin, double xMax, double yMin, double yMax) {
        return p.getX() >= xMin && p.getX() <= xMax && p.getY() >= yMin && p.getY() <= yMax;
    }
}
