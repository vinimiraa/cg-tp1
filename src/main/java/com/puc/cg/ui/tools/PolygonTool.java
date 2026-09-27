package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.impl.Polygon2D;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

import java.util.ArrayList;
import java.util.List;

public class PolygonTool implements DrawingTool {

    private final List<Point2D> pendingVertices = new ArrayList<>();

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        pendingVertices.add(point);
        context.setPreviewPolygon(pendingVertices, point);
    }

    @Override
    public void onMouseMoved(Point2D point, DrawingContext context) {
        if (!pendingVertices.isEmpty()) {
            context.setPreviewPolygon(pendingVertices, point);
        }
    }

    @Override
    public void onCancel(DrawingContext context) {
        if (pendingVertices.size() >= 3) {
            Polygon2D polygon = new Polygon2D();
            for (Point2D vertex : pendingVertices) {
                polygon.addVertex(vertex);
            }
            context.getScene().pushHistory();
            context.getScene().getShapes().add(polygon);
        }
        pendingVertices.clear();
        context.clearPreview();
    }

    @Override
    public void reset() {
        pendingVertices.clear();
    }
}
