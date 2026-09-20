package com.puc.cg.algorithms.raster.impl;

import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Framebuffer;

public class LineRasterizerDDAImpl implements LineRasterizerAlgorithm {

    @Override
    public void draw(Framebuffer buffer, Point2D start, Point2D end, int rgb) {
        double x1 = start.x();
        double y1 = start.y();
        double x2 = end.x();
        double y2 = end.y();

        int dx = (int) (x2 - x1);
        int dy = (int) (y2 - y1);

        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        double xIncrement = (double) dx / steps;
        double yIncrement = (double) dy / steps;

        double x = x1;
        double y = y1;

        buffer.setPixel((int) Math.round(x), (int) Math.round(y), rgb);

        for (int i = 1; i <= steps; i++) {
            x += xIncrement;
            y += yIncrement;
            buffer.setPixel((int) Math.round(x), (int) Math.round(y), rgb);
        }
    }

}
