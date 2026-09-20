package com.puc.cg.ui;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.util.Scene;

import java.util.List;

public interface DrawingContext {
    Scene getScene();

    void requestRedraw();

    void setPreviewLine(Point2D start, Point2D end);

    void setPreviewCircle(Point2D center, int radius);

    void setPreviewRect(Point2D corner1, Point2D corner2);

    void setPreviewPolygon(List<Point2D> vertices, Point2D current);

    void clearPreview();

    void setSelection(List<Shape> shapes);

    void applyFill(Point2D seed);
}
