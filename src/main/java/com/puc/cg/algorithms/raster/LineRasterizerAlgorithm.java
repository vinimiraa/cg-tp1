package com.puc.cg.algorithms.raster;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Framebuffer;

public interface LineRasterizerAlgorithm {
    void draw(Framebuffer buffer, Point2D start, Point2D end, int rgb);
}
