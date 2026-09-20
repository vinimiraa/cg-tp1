package com.puc.cg.commons.models.impl;

import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Point2DTransform;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.util.Framebuffer;

public record Circle(Point2D center, int radius) implements Shape {

    @Override
    public void draw(
            Framebuffer framebuffer,
            LineRasterizerAlgorithm lineRasterizer, CircleRasterizerAlgorithm circleRasterizer,
            int rgb
    ) {
        circleRasterizer.draw(framebuffer, center, radius, rgb);
    }

    @Override
    public Window bounds() {
        return new Window(
                center.x() - radius,
                center.y() - radius,
                center.x() + radius,
                center.y() + radius
        );
    }

    @Override
    public Circle transform(Matrix3 matrix) {
        Point2D newCenter = Point2DTransform.apply(matrix, center);
        Point2D edgePoint = new Point2D(center.x() + radius, center.y());
        Point2D newEdge = Point2DTransform.apply(matrix, edgePoint);
        int newRadius = (int) Math.round(Math.hypot(newEdge.x() - newCenter.x(), newEdge.y() - newCenter.y()));
        return new Circle(newCenter, newRadius);
    }
}
