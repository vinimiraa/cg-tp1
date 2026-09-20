package com.puc.cg.algorithms.raster.impl;

import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Framebuffer;

public class LineRasterizerBresenhamImpl implements LineRasterizerAlgorithm {

    @Override
    public void draw(Framebuffer buffer, Point2D start, Point2D end, int rgb) {
        int dx = (int) (end.x() - start.x());
        int dy = (int) (end.y() - start.y());

        int xIncrement = resolveIncrement(dx);
        int yIncrement = resolveIncrement(dy);
        dx = normalizeDelta(dx);
        dy = normalizeDelta(dy);

        if (dx > dy) {
            rasterizeStepByX(buffer, start, dx, dy, xIncrement, yIncrement, rgb);
        } else {
            rasterizeStepByY(buffer, start, dx, dy, xIncrement, yIncrement, rgb);
        }
    }

    private int resolveIncrement(int delta) {
        if (delta >= 0) {
            return 1;
        }
        return -1;
    }

    private int normalizeDelta(int delta) {
        if (delta >= 0) {
            return delta;
        }
        return -delta;
    }

    private void rasterizeStepByX(
            Framebuffer buffer,
            Point2D start,
            int dx, int dy,
            int xIncrement, int yIncrement,
            int rgb
    ) {
        int x = (int) start.x();
        int y = (int) start.y();

        int p = 2 * dy - dx;
        int c1 = 2 * dy;
        int c2 = 2 * (dy - dx);

        buffer.setPixel(x, y, rgb);

        for (int i = 0; i < dx; i++) {
            x += xIncrement;

            if (p < 0) {
                p += c1;
            } else {
                p += c2;
                y += yIncrement;
            }

            buffer.setPixel(x, y, rgb);
        }
    }

    private void rasterizeStepByY(
            Framebuffer buffer,
            Point2D start,
            int dx, int dy,
            int xIncrement, int yIncrement,
            int rgb
    ) {
        int x = (int) start.x();
        int y = (int) start.y();

        int p = 2 * dx - dy;
        int c1 = 2 * dx;
        int c2 = 2 * (dx - dy);

        buffer.setPixel(x, y, rgb);
        for (int i = 0; i < dy; i++) {
            y += yIncrement;

            if (p < 0) {
                p += c1;
            } else {
                p += c2;
                x += xIncrement;
            }

            buffer.setPixel(x, y, rgb);
        }
    }

}
