package com.puc.cg.algorithms.clipping.impl;

import com.puc.cg.algorithms.clipping.ClipWindowAlgorithm;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.models.impl.LineSegment;

public class LiangBarsky implements ClipWindowAlgorithm {

    @Override
    public LineSegment clip(LineSegment line, Window window) {
        LineSegment result = null;

        double[] u1 = {0.0};
        double[] u2 = {1.0};

        double x1 = line.start().x();
        double y1 = line.start().y();
        double x2 = line.end().x();
        double y2 = line.end().y();

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (cliptest(-dx, x1 - window.xMin(), u1, u2)) {
            if (cliptest(dx, window.xMax() - x1, u1, u2)) {
                if (cliptest(-dy, y1 - window.yMin(), u1, u2)) {
                    if (cliptest(dy, window.yMax() - y1, u1, u2)) {
                        if (u2[0] < 1.0) {
                            x2 = x1 + u2[0] * dx;
                            y2 = y1 + u2[0] * dy;
                        }
                        if (u1[0] > 0.0) {
                            x1 += u1[0] * dx;
                            y1 += u1[0] * dy;
                        }
                        result = new LineSegment(new Point2D(x1, y1), new Point2D(x2, y2));
                    }
                }
            }
        }

        return result;
    }

    private boolean cliptest(double p, double q, double[] u1, double[] u2) {
        boolean result = true;
        double r = q / p;

        if (p < 0) { // fora para dentro
            if (r > u2[0]) {
                result = false;
            } else if (r > u1[0]) {
                u1[0] = r; // Update u1
            }
        } else if (p > 0) { // dentro para fora
            if (r < u1[0]) {
                result = false;
            } else if (r < u2[0]) {
                u2[0] = r;
            }
        } else if (q < 0) { // paralela e fora
            result = false;
        }

        return result;
    }

}
