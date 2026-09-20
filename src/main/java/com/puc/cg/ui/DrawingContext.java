package com.puc.cg.ui;

import com.puc.cg.commons.model.Circle;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Polygon2D;
import com.puc.cg.commons.model.Scene;

import java.util.List;

public interface DrawingContext {
    Scene getScene();

    void requestRedraw();

    void setPreviewLine(Point2D start, Point2D end);

    void setPreviewCircle(Point2D center, int radius);

    void setPreviewRect(Point2D corner1, Point2D corner2);

    void setPreviewPolygon(List<Point2D> vertices, Point2D current);

    void clearPreview();

    void setSelection(List<LineSegment> lines, List<Circle> circles, List<Polygon2D> polygons);
}
