package com.puc.cg.algorithms.raster.impl;

import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Framebuffer;

public class CircleRasterizerBresenhamImpl implements CircleRasterizerAlgorithm {

    @Override
    public void draw(Framebuffer buffer, Point2D center, int radius, int rgb) {
        int x = 0;
        int y = radius;
        int p = 3 - 2 * radius;

        this.setPixelSymmetric(buffer, center, x, y, rgb);

        while (x < y) {
            if (p < 0) {
                p += 4 * x + 6;
            } else {
                p += 4 * (x - y) + 10;
                y--;
            }
            x++;

            this.setPixelSymmetric(buffer, center, x, y, rgb);
        }
    }

    private void setPixelSymmetric(Framebuffer buffer, Point2D center, int x, int y, int rgb) {
        buffer.setPixel((int) (center.x() + x), (int) (center.y() + y), rgb);
        buffer.setPixel((int) (center.x() - x), (int) (center.y() + y), rgb);
        buffer.setPixel((int) (center.x() + x), (int) (center.y() - y), rgb);
        buffer.setPixel((int) (center.x() - x), (int) (center.y() - y), rgb);
        buffer.setPixel((int) (center.x() + y), (int) (center.y() + x), rgb);
        buffer.setPixel((int) (center.x() - y), (int) (center.y() + x), rgb);
        buffer.setPixel((int) (center.x() + y), (int) (center.y() - x), rgb);
        buffer.setPixel((int) (center.x() - y), (int) (center.y() - x), rgb);
    }

}
