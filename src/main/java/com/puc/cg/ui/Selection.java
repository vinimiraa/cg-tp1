package com.puc.cg.ui;

import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.util.Scene;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    private List<Shape> shapes = new ArrayList<>();

    public void set(List<Shape> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    public void clear() {
        shapes.clear();
    }

    public boolean contains(Shape shape) {
        return shapes.contains(shape);
    }

    public Window bounds() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        boolean any = false;

        for (Shape shape : shapes) {
            any = true;
            Window bounds = shape.bounds();
            minX = Math.min(minX, bounds.xMin());
            maxX = Math.max(maxX, bounds.xMax());
            minY = Math.min(minY, bounds.yMin());
            maxY = Math.max(maxY, bounds.yMax());
        }

        return any ? new Window(minX, minY, maxX, maxY) : null;
    }

    public Point2D center() {
        Window bounds = bounds();
        if (bounds == null) {
            return null;
        }
        return new Point2D((bounds.xMin() + bounds.xMax()) / 2, (bounds.yMin() + bounds.yMax()) / 2);
    }

    public void applyTransform(Matrix3 matrix, Scene scene) {
        List<Shape> sceneShapes = scene.getShapes();
        for (int i = 0; i < sceneShapes.size(); i++) {
            int selIndex = shapes.indexOf(sceneShapes.get(i));
            if (selIndex >= 0) {
                Shape transformed = sceneShapes.get(i).transform(matrix);
                sceneShapes.set(i, transformed);
                shapes.set(selIndex, transformed);
            }
        }
    }
}
