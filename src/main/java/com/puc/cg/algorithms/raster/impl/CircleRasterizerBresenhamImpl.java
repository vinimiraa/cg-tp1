package com.puc.cg.algorithms.raster.impl;

import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.Framebuffer;
import com.puc.cg.commons.model.Point2D;

public class CircleRasterizerBresenhamImpl implements CircleRasterizerAlgorithm {

    @Override
    public void draw(Framebuffer buffer, Point2D center, int radius, int rgb) {
        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        this.setPixelSymmetric(buffer, center, x, y, rgb);

        while (x < y) {
            if (d < 0) {
                d += 4 * x + 6;
            } else {
                d += 4 * (x - y) + 10;
                y--;
            }
            x++;

            this.setPixelSymmetric(buffer, center, x, y, rgb);
        }
    }

    private void setPixelSymmetric(Framebuffer buffer, Point2D center, int x, int y, int rgb) {
        buffer.setPixel((int) (center.getX() + x), (int) (center.getY() + y), rgb);
        buffer.setPixel((int) (center.getX() - x), (int) (center.getY() + y), rgb);
        buffer.setPixel((int) (center.getX() + x), (int) (center.getY() - y), rgb);
        buffer.setPixel((int) (center.getX() - x), (int) (center.getY() - y), rgb);
        buffer.setPixel((int) (center.getX() + y), (int) (center.getY() + x), rgb);
        buffer.setPixel((int) (center.getX() - y), (int) (center.getY() + x), rgb);
        buffer.setPixel((int) (center.getX() + y), (int) (center.getY() - x), rgb);
        buffer.setPixel((int) (center.getX() - y), (int) (center.getY() - x), rgb);
    }

}
