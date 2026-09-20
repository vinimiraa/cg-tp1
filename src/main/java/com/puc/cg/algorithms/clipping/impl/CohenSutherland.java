package com.puc.cg.algorithms.clipping.impl;

import com.puc.cg.algorithms.clipping.ClipWindowAlgorithm;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Window;

public class CohenSutherland implements ClipWindowAlgorithm {

    @Override
    public LineSegment clip(LineSegment line, Window window) {
        double x1 = line.start().getX();
        double y1 = line.start().getY();
        double x2 = line.end().getX();
        double y2 = line.end().getY();

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
                    intersectionX = x1 + (x2 - x1) * (window.getYMax() - y1) / (y2 - y1);
                    intersectionY = window.getYMax();
                } else if ((codeOut & 4) != 0) { // abaixo
                    intersectionX = x1 + (x2 - x1) * (window.getYMin() - y1) / (y2 - y1);
                    intersectionY = window.getYMin();
                } else if ((codeOut & 2) != 0) { // direita
                    intersectionY = y1 + (y2 - y1) * (window.getXMax() - x1) / (x2 - x1);
                    intersectionX = window.getXMax();
                } else if ((codeOut & 1) != 0) { // esquerda
                    intersectionY = y1 + (y2 - y1) * (window.getXMin() - x1) / (x2 - x1);
                    intersectionX = window.getXMin();
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

        if (x < window.getXMin()) code += 1; // esq
        if (x > window.getXMax()) code += 2; // dir
        if (y < window.getYMin()) code += 4; // inf
        if (y > window.getYMax()) code += 8; // sup

        return code;
    }
}
