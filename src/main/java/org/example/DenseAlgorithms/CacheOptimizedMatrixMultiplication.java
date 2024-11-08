package org.example.DenseAlgorithms;

public class CacheOptimizedMatrixMultiplication {

    private int blockSize;

    public CacheOptimizedMatrixMultiplication(int blockSize) {
        this.blockSize = blockSize;
    }

    public double[][] multiply(double[][] A, double[][] B) {
        int N = A.length;
        double[][] C = new double[N][N];

        for (int i = 0; i < N; i += blockSize) {
            for (int j = 0; j < N; j += blockSize) {
                for (int k = 0; k < N; k += blockSize) {
                    multiplyBlock(A, B, C, i, j, k, N);
                }
            }
        }

        return C;
    }

    private void multiplyBlock(double[][] A, double[][] B, double[][] C, int rowBlock, int colBlock, int kBlock, int N) {
        for (int i = rowBlock; i < Math.min(rowBlock + blockSize, N); i++) {
            for (int j = colBlock; j < Math.min(colBlock + blockSize, N); j++) {
                double sum = 0;
                for (int k = kBlock; k < Math.min(kBlock + blockSize, N); k++) {
                    sum += A[i][k] * B[k][j];
                }
                C[i][j] += sum;
            }
        }
    }
}
