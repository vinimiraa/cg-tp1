package com.puc.cg.ui;

import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.model.Circle;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Polygon2D;
import com.puc.cg.commons.model.Scene;
import com.puc.cg.commons.model.Window;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    private List<LineSegment> lines = new ArrayList<>();
    private List<Circle> circles = new ArrayList<>();
    private List<Polygon2D> polygons = new ArrayList<>();

    public void set(List<LineSegment> lines, List<Circle> circles, List<Polygon2D> polygons) {
        this.lines = new ArrayList<>(lines);
        this.circles = new ArrayList<>(circles);
        this.polygons = new ArrayList<>(polygons);
    }

    public void clear() {
        lines.clear();
        circles.clear();
        polygons.clear();
    }

    public boolean contains(LineSegment line) {
        return lines.contains(line);
    }

    public boolean contains(Circle circle) {
        return circles.contains(circle);
    }

    public boolean contains(Polygon2D polygon) {
        return polygons.contains(polygon);
    }

    public Window bounds() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        boolean any = false;

        for (LineSegment line : lines) {
            any = true;
            minX = Math.min(minX, Math.min(line.start().getX(), line.end().getX()));
            maxX = Math.max(maxX, Math.max(line.start().getX(), line.end().getX()));
            minY = Math.min(minY, Math.min(line.start().getY(), line.end().getY()));
            maxY = Math.max(maxY, Math.max(line.start().getY(), line.end().getY()));
        }
        for (Circle circle : circles) {
            any = true;
            minX = Math.min(minX, circle.center().getX() - circle.radius());
            maxX = Math.max(maxX, circle.center().getX() + circle.radius());
            minY = Math.min(minY, circle.center().getY() - circle.radius());
            maxY = Math.max(maxY, circle.center().getY() + circle.radius());
        }
        for (Polygon2D polygon : polygons) {
            for (Point2D vertex : polygon.getVertices()) {
                any = true;
                minX = Math.min(minX, vertex.getX());
                maxX = Math.max(maxX, vertex.getX());
                minY = Math.min(minY, vertex.getY());
                maxY = Math.max(maxY, vertex.getY());
            }
        }

        return any ? new Window(minX, minY, maxX, maxY) : null;
    }

    public Point2D center() {
        Window bounds = bounds();
        if (bounds == null) {
            return null;
        }
        return new Point2D((bounds.getXMin() + bounds.getXMax()) / 2, (bounds.getYMin() + bounds.getYMax()) / 2);
    }

    public void applyTransform(Matrix3 matrix, Scene scene) {
        List<LineSegment> sceneLines = scene.getLines();
        for (int i = 0; i < sceneLines.size(); i++) {
            int selIndex = lines.indexOf(sceneLines.get(i));
            if (selIndex >= 0) {
                LineSegment transformed = transformLine(matrix, sceneLines.get(i));
                sceneLines.set(i, transformed);
                lines.set(selIndex, transformed);
            }
        }

        List<Circle> sceneCircles = scene.getCircles();
        for (int i = 0; i < sceneCircles.size(); i++) {
            int selIndex = circles.indexOf(sceneCircles.get(i));
            if (selIndex >= 0) {
                Circle transformed = transformCircle(matrix, sceneCircles.get(i));
                sceneCircles.set(i, transformed);
                circles.set(selIndex, transformed);
            }
        }

        List<Polygon2D> scenePolygons = scene.getPolygons();
        for (int i = 0; i < scenePolygons.size(); i++) {
            int selIndex = polygons.indexOf(scenePolygons.get(i));
            if (selIndex >= 0) {
                Polygon2D transformed = transformPolygon(matrix, scenePolygons.get(i));
                scenePolygons.set(i, transformed);
                polygons.set(selIndex, transformed);
            }
        }
    }

    private static LineSegment transformLine(Matrix3 matrix, LineSegment line) {
        return new LineSegment(transformPoint(matrix, line.start()), transformPoint(matrix, line.end()));
    }

    private static Circle transformCircle(Matrix3 matrix, Circle circle) {
        Point2D newCenter = transformPoint(matrix, circle.center());
        Point2D edgePoint = new Point2D(circle.center().getX() + circle.radius(), circle.center().getY());
        Point2D newEdge = transformPoint(matrix, edgePoint);
        int newRadius = (int) Math.round(Math.hypot(newEdge.getX() - newCenter.getX(), newEdge.getY() - newCenter.getY()));
        return new Circle(newCenter, newRadius);
    }

    private static Polygon2D transformPolygon(Matrix3 matrix, Polygon2D polygon) {
        Polygon2D transformed = new Polygon2D();
        for (Point2D vertex : polygon.getVertices()) {
            transformed.addVertex(transformPoint(matrix, vertex));
        }
        return transformed;
    }

    private static Point2D transformPoint(Matrix3 matrix, Point2D point) {
        double[] result = matrix.apply(point.getX(), point.getY());
        return new Point2D(result[0], result[1]);
    }
}
