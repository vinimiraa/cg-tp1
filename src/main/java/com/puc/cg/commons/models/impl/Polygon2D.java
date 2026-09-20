package com.puc.cg.commons.models.impl;

import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Point2DTransform;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.util.Framebuffer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Polygon2D implements Shape {
    private final List<Point2D> vertices = new ArrayList<>();

    public void addVertex(Point2D point) {
        vertices.add(point);
    }

    @Override
    public void draw(
            Framebuffer framebuffer,
            LineRasterizerAlgorithm lineRasterizer, CircleRasterizerAlgorithm circleRasterizer,
            int rgb
    ) {
        for (int i = 0; i < vertices.size(); i++) {
            Point2D from = vertices.get(i);
            Point2D to = vertices.get((i + 1) % vertices.size());
            lineRasterizer.draw(framebuffer, from, to, rgb);
        }
    }

    @Override
    public Window bounds() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Point2D vertex : vertices) {
            minX = Math.min(minX, vertex.x());
            maxX = Math.max(maxX, vertex.x());
            minY = Math.min(minY, vertex.y());
            maxY = Math.max(maxY, vertex.y());
        }
        return new Window(minX, minY, maxX, maxY);
    }

    @Override
    public Polygon2D transform(Matrix3 matrix) {
        Polygon2D transformed = new Polygon2D();
        for (Point2D vertex : vertices) {
            transformed.addVertex(Point2DTransform.apply(matrix, vertex));
        }
        return transformed;
    }
}
