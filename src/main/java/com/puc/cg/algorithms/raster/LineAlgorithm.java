package com.puc.cg.algorithms.raster;

import com.puc.cg.algorithms.raster.impl.LineRasterizerBresenhamImpl;
import com.puc.cg.algorithms.raster.impl.LineRasterizerDDAImpl;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Framebuffer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LineAlgorithm implements LineRasterizerAlgorithm {
    DDA(new LineRasterizerDDAImpl()),
    BRESENHAM(new LineRasterizerBresenhamImpl());

    private final LineRasterizerAlgorithm delegate;

    @Override
    public void draw(Framebuffer buffer, Point2D start, Point2D end, int rgb) {
        delegate.draw(buffer, start, end, rgb);
    }
}
