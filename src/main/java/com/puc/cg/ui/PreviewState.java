package com.puc.cg.ui;

import com.puc.cg.commons.model.Circle;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Window;
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
        double xMin = Math.min(corner1.getX(), corner2.getX());
        double xMax = Math.max(corner1.getX(), corner2.getX());
        double yMin = Math.min(corner1.getY(), corner2.getY());
        double yMax = Math.max(corner1.getY(), corner2.getY());
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
