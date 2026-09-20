package com.puc.cg.commons.models.impl;

import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Point2DTransform;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.util.Framebuffer;

public record LineSegment(Point2D start, Point2D end) implements Shape {

    @Override
    public void draw(
            Framebuffer framebuffer,
            LineRasterizerAlgorithm lineRasterizer, CircleRasterizerAlgorithm circleRasterizer,
            int rgb
    ) {
        lineRasterizer.draw(framebuffer, start, end, rgb);
    }

    @Override
    public Window bounds() {
        double minX = Math.min(start.x(), end.x());
        double maxX = Math.max(start.x(), end.x());
        double minY = Math.min(start.y(), end.y());
        double maxY = Math.max(start.y(), end.y());
        return new Window(minX, minY, maxX, maxY);
    }

    @Override
    public LineSegment transform(Matrix3 matrix) {
        return new LineSegment(Point2DTransform.apply(matrix, start), Point2DTransform.apply(matrix, end));
    }
}
