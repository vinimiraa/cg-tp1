package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.impl.LineSegment;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

public class LineTool implements DrawingTool {

    private Point2D pendingStart;

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        if (pendingStart == null) {
            pendingStart = point;
            context.setPreviewLine(point, point);
            return;
        }

        context.getScene().pushHistory();
        context.getScene().getShapes().add(new LineSegment(pendingStart, point));
        pendingStart = null;
        context.clearPreview();
    }

    @Override
    public void onMouseMoved(Point2D point, DrawingContext context) {
        if (pendingStart != null) {
            context.setPreviewLine(pendingStart, point);
        }
    }

    @Override
    public void onCancel(DrawingContext context) {
        pendingStart = null;
        context.clearPreview();
    }

    @Override
    public void reset() {
        pendingStart = null;
    }
}
