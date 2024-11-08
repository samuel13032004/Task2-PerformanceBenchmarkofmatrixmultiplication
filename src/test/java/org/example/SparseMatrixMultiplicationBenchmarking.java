package org.example;

import org.example.SparseAlgorithms.SparseMatrixMultiplicationCSC;
import org.example.SparseAlgorithms.SparseMatrixMultiplicationCSR;
import org.openjdk.jmh.annotations.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class SparseMatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands {

        @Param({"10", "50", "100","200", "500", "1000","2000","5000"})
        private int n;

        @Param({"0.0","0.2","0.5","0.9"})
        private double sparsity;

        private double[][] a;
        private double[][] b;

        public boolean memoryPrintedSparse = false;

        @Setup(Level.Trial)
        public void setup() {
            Random random = new Random();
            a = new double[n][n];
            b = new double[n][n];

            int nonZeroElements = (int) ((1.0 - sparsity) * n * n);

            for (int i = 0; i < nonZeroElements; i++) {
                int row = random.nextInt(n);
                int col = random.nextInt(n);
                a[row][col] = random.nextDouble() * 10;
                b[row][col] = random.nextDouble() * 10;
            }

            System.out.println("Sparsity Level: " + (sparsity * 100) + "%");
        }

        private void printMemoryUsage(String methodName, int matrixSize, long memoryUsedBytes, boolean printedFlag) {
            if (!printedFlag) {
                double memoryUsedMB = memoryUsedBytes / (1024.0 * 1024.0);
                System.out.println(methodName + " - Matrix size: " + matrixSize + "x" + matrixSize +
                        " | Sparsity Level: " + (sparsity * 100) + "% | Memory used: " + String.format("%.2f", memoryUsedMB) + " MB");
            }
        }
    }

    @Benchmark
    public void sparseMultiplicationCSCBenchmark(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        SparseMatrixMultiplicationCSC sparseMultiplication = new SparseMatrixMultiplicationCSC();
        double[][] result = sparseMultiplication.multiply(operands.a, operands.b);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.abs(memoryAfter - memoryBefore);

        operands.printMemoryUsage("Sparse CSC Multiplication", operands.n, memoryUsed, operands.memoryPrintedSparse);
        operands.memoryPrintedSparse = true;
    }

    @Benchmark
    public void sparseMultiplicationCSRBenchmark(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        SparseMatrixMultiplicationCSR.CSRMatrix csrA = SparseMatrixMultiplicationCSR.convertToCSR(operands.a);
        SparseMatrixMultiplicationCSR.CSRMatrix csrB = SparseMatrixMultiplicationCSR.convertToCSR(operands.b);

        SparseMatrixMultiplicationCSR.CSRMatrix resultMatrix = csrA.multiply(csrB);

        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = Math.abs(memoryAfter - memoryBefore);

        operands.printMemoryUsage("Sparse CSR Multiplication", operands.n, memoryUsed, operands.memoryPrintedSparse);
        operands.memoryPrintedSparse = true;
    }

}
