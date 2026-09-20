package com.puc.cg.commons.models;

import com.puc.cg.algorithms.transform.Matrix3;

public final class Point2DTransform {

    private Point2DTransform() {
    }

    public static Point2D apply(Matrix3 matrix, Point2D point) {
        double[] result = matrix.apply(point.x(), point.y());
        return new Point2D(result[0], result[1]);
    }
}
