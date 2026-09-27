package com.puc.cg.ui;

import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.models.FillAction;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Point2DTransform;
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
        List<Shape> selectedBeforeTransform = new ArrayList<>(shapes);

        List<Shape> sceneShapes = scene.getShapes();
        for (int i = 0; i < sceneShapes.size(); i++) {
            int selIndex = shapes.indexOf(sceneShapes.get(i));
            if (selIndex >= 0) {
                Shape transformed = sceneShapes.get(i).transform(matrix);
                sceneShapes.set(i, transformed);
                shapes.set(selIndex, transformed);
            }
        }

        moveFillsWithShapes(matrix, scene.getFills(), selectedBeforeTransform);
    }

    private void moveFillsWithShapes(Matrix3 matrix, List<FillAction> fills, List<Shape> movedShapes) {
        for (int i = 0; i < fills.size(); i++) {
            FillAction fill = fills.get(i);
            if (isInsideAny(fill.seed(), movedShapes)) {
                Point2D transformedSeed = Point2DTransform.apply(matrix, fill.seed());
                fills.set(i, new FillAction(transformedSeed, fill.method(), fill.fillColor(), fill.refColor(), fill.connectivity()));
            }
        }
    }

    private boolean isInsideAny(Point2D point, List<Shape> candidates) {
        for (Shape shape : candidates) {
            Window bounds = shape.bounds();
            if (point.x() >= bounds.xMin() && point.x() <= bounds.xMax()
                    && point.y() >= bounds.yMin() && point.y() <= bounds.yMax()) {
                return true;
            }
        }
        return false;
    }
}
