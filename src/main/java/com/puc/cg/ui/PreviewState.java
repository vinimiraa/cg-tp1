package com.puc.cg.ui;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.models.impl.Circle;
import com.puc.cg.commons.models.impl.LineSegment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PreviewState {
    private LineSegment line;
    private Circle circle;
    private Window rect;
    private List<Point2D> polygonVertices;
    private Point2D polygonCurrent;

    public void setLine(Point2D start, Point2D end) {
        clear();
        line = new LineSegment(start, end);
    }

    public void setCircle(Point2D center, int radius) {
        clear();
        circle = new Circle(center, radius);
    }

    public void setRect(Point2D corner1, Point2D corner2) {
        clear();
        double xMin = Math.min(corner1.x(), corner2.x());
        double xMax = Math.max(corner1.x(), corner2.x());
        double yMin = Math.min(corner1.y(), corner2.y());
        double yMax = Math.max(corner1.y(), corner2.y());
        rect = new Window(xMin, yMin, xMax, yMax);
    }

    public void setPolygon(List<Point2D> vertices, Point2D current) {
        clear();
        polygonVertices = new ArrayList<>(vertices);
        polygonCurrent = current;
    }

    public void clear() {
        line = null;
        circle = null;
        rect = null;
        polygonVertices = null;
        polygonCurrent = null;
    }
}
