package com.puc.cg.commons.models;

import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.util.Framebuffer;

public interface Shape {
    void draw(
            Framebuffer framebuffer,
            LineRasterizerAlgorithm lineRasterizer, CircleRasterizerAlgorithm circleRasterizer,
            int rgb
    );

    Window bounds();

    Shape transform(Matrix3 matrix);
}
