package com.puc.cg.algorithms.raster;

import com.puc.cg.commons.model.Point2D;

public interface CircleRasterizerAlgorithm {
    void draw(Framebuffer buffer, Point2D center, int radius, int rgb);
}
