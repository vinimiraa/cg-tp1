package com.puc.cg.algorithms.clipping.impl;

import com.puc.cg.algorithms.clipping.ClipWindowAlgorithm;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.models.impl.LineSegment;

public class CohenSutherland implements ClipWindowAlgorithm {

    @Override
    public LineSegment clip(LineSegment line, Window window) {
        double x1 = line.start().x();
        double y1 = line.start().y();
        double x2 = line.end().x();
        double y2 = line.end().y();

        LineSegment result = null;
        boolean done = false;

        while (!done) {
            int code1 = getRegionCode(x1, y1, window);
            int code2 = getRegionCode(x2, y2, window);

            if (code1 == 0 && code2 == 0) {
                result = new LineSegment(new Point2D(x1, y1), new Point2D(x2, y2));
                done = true;
            } else if ((code1 & code2) != 0) {
                done = true;
            } else {
                double intersectionX = 0.0;
                double intersectionY = 0.0;
                int codeOut = code1 != 0 ? code1 : code2;

                if ((codeOut & 8) != 0) { // acima
                    intersectionX = x1 + (x2 - x1) * (window.yMax() - y1) / (y2 - y1);
                    intersectionY = window.yMax();
                } else if ((codeOut & 4) != 0) { // abaixo
                    intersectionX = x1 + (x2 - x1) * (window.yMin() - y1) / (y2 - y1);
                    intersectionY = window.yMin();
                } else if ((codeOut & 2) != 0) { // direita
                    intersectionY = y1 + (y2 - y1) * (window.xMax() - x1) / (x2 - x1);
                    intersectionX = window.xMax();
                } else if ((codeOut & 1) != 0) { // esquerda
                    intersectionY = y1 + (y2 - y1) * (window.xMin() - x1) / (x2 - x1);
                    intersectionX = window.xMin();
                }

                if (codeOut == code1) {
                    x1 = intersectionX;
                    y1 = intersectionY;
                } else {
                    x2 = intersectionX;
                    y2 = intersectionY;
                }
            }
        }

        return result;
    }

    private int getRegionCode(double x, double y, Window window) {
        int code = 0;

        if (x < window.xMin()) code += 1; // esq
        if (x > window.xMax()) code += 2; // dir
        if (y < window.yMin()) code += 4; // inf
        if (y > window.yMax()) code += 8; // sup

        return code;
    }
}
