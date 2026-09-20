package com.puc.cg.algorithms.filling;

import com.puc.cg.commons.util.Framebuffer;

public interface FillAlgorithm {
    void fill(Framebuffer framebuffer, int x, int y, int fillColor, int refColor, Connectivity connectivity);
}
