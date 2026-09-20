package com.puc.cg.algorithms.transform;

public class Transformations {

    private Transformations() {
    }

    public static Matrix3 translation(double dx, double dy) {
        return new Matrix3(new double[][]{
                {1, 0, dx},
                {0, 1, dy},
                {0, 0, 1}
        });
    }

    public static Matrix3 rotation(double angleDegrees) {
        double rad = Math.toRadians(angleDegrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Matrix3(new double[][]{
                {cos, -sin, 0},
                {sin, cos, 0},
                {0, 0, 1}
        });
    }

    public static Matrix3 scale(double sx, double sy) {
        return new Matrix3(new double[][]{
                {sx, 0, 0},
                {0, sy, 0},
                {0, 0, 1}
        });
    }

    public static Matrix3 reflectionX() {
        return scale(1, -1);
    }

    public static Matrix3 reflectionY() {
        return scale(-1, 1);
    }

    public static Matrix3 reflectionXY() {
        return scale(-1, -1);
    }
}
