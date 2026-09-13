package com.puc.cg.algorithms.raster;

import com.puc.cg.commons.model.Point2D;

public interface LineRasterizerAlgorithm {
    void draw(Framebuffer buffer, Point2D start, Point2D end, int rgb);
}
