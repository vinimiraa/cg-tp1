package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
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

        double xMin = Math.min(pendingCorner.x(), point.x());
        double xMax = Math.max(pendingCorner.x(), point.x());
        double yMin = Math.min(pendingCorner.y(), point.y());
        double yMax = Math.max(pendingCorner.y(), point.y());
        Window region = new Window(xMin, yMin, xMax, yMax);

        List<Shape> selected = new ArrayList<>();
        for (Shape shape : context.getScene().getShapes()) {
            if (fullyInside(shape.bounds(), region)) {
                selected.add(shape);
            }
        }

        context.setSelection(selected);
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

    private boolean fullyInside(Window shapeBounds, Window region) {
        return shapeBounds.xMin() >= region.xMin()
                && shapeBounds.xMax() <= region.xMax()
                && shapeBounds.yMin() >= region.yMin()
                && shapeBounds.yMax() <= region.yMax();
    }
}
