package com.puc.cg.algorithms.transform;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Matrix3 {
    private final double[][] m;

    public static Matrix3 identity() {
        return new Matrix3(new double[][]{
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        });
    }

    public Matrix3 multiply(Matrix3 other) {
        double[][] result = new double[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                double sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += m[row][k] * other.m[k][col];
                }
                result[row][col] = sum;
            }
        }
        return new Matrix3(result);
    }

    public double[] apply(double x, double y) {
        double px = m[0][0] * x + m[0][1] * y + m[0][2];
        double py = m[1][0] * x + m[1][1] * y + m[1][2];
        return new double[]{px, py};
    }
}
