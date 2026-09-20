package com.puc.cg.algorithms.raster;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Framebuffer;

public interface CircleRasterizerAlgorithm {
    void draw(Framebuffer buffer, Point2D center, int radius, int rgb);
}
