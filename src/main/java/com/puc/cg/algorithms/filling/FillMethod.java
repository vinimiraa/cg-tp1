package com.puc.cg.algorithms.filling;

import com.puc.cg.algorithms.filling.impl.BoundaryFillImpl;
import com.puc.cg.algorithms.filling.impl.FloodFillImpl;
import com.puc.cg.commons.util.Framebuffer;

public enum FillMethod implements FillAlgorithm {
    FLOOD_FILL(new FloodFillImpl()),
    BOUNDARY_FILL(new BoundaryFillImpl());

    private final FillAlgorithm delegate;

    FillMethod(FillAlgorithm delegate) {
        this.delegate = delegate;
    }

    @Override
    public void fill(Framebuffer framebuffer, int x, int y, int fillColor, int refColor, Connectivity connectivity) {
        delegate.fill(framebuffer, x, y, fillColor, refColor, connectivity);
    }
}
