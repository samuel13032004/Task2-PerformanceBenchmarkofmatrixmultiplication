package org.example.DenseAlgorithms;

public class LoopUnrollingMultiplication {


    public static double[][] multiply(double[][] A, double[][] B) {
        int size = A.length;
        double[][] C = new double[size][size];

        double[][] B_T = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                B_T[j][i] = B[i][j];
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double sum = 0.0;
                int k;

                for (k = 0; k <= size - 4; k += 4) {
                    sum += A[i][k] * B_T[j][k]
                            + A[i][k + 1] * B_T[j][k + 1]
                            + A[i][k + 2] * B_T[j][k + 2]
                            + A[i][k + 3] * B_T[j][k + 3];
                }

                for (; k < size; k++) {
                    sum += A[i][k] * B_T[j][k];
                }

                C[i][j] = sum;
            }
        }
        return C;
    }
}