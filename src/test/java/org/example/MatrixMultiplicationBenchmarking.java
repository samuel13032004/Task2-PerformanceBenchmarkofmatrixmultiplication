package org.example;

import org.example.DenseAlgorithms.*;
import org.openjdk.jmh.annotations.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class MatrixMultiplicationBenchmarking {

	@State(Scope.Thread)
	public static class Operands {

		@Param({"10", "50", "100","200", "500", "1000","2000"})
		private int n;


		private double[][] a;
		private double[][] b;

		public boolean memoryPrintedBasic = false;
		public boolean memoryPrintedCache = false;
		public boolean memoryPrintedLoopUnrolling = false;

		@Setup(Level.Trial)
		public void setup() {
			Random random = new Random();
			a = new double[n][n];
			b = new double[n][n];


			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					a[i][j] = random.nextDouble()*10;
					b[i][j] = random.nextDouble()*10;
				}
			}
		}

		private void printMemoryUsage(String methodName, int matrixSize, long memoryUsedBytes, boolean printedFlag) {
			if (!printedFlag) {
				double memoryUsedMB = memoryUsedBytes / (1024.0 * 1024.0);
				System.out.println(methodName + " - Matrix size: " + matrixSize + "x" + matrixSize +
						" | Memory used: " + String.format("%.2f", memoryUsedMB) + " MB");
			}
		}
	}

@Benchmark
public void basicMultiplicationBenchmark(Operands operands) {
	Runtime runtime = Runtime.getRuntime();
	System.gc();
	long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

	MatrixMultiplication basicMultiplication = new MatrixMultiplication();
	double[][] result = basicMultiplication.execute(operands.a, operands.b);

	long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
	long memoryUsed = Math.abs(memoryAfter - memoryBefore);
	operands.printMemoryUsage("Basic Multiplication", operands.n, memoryUsed, operands.memoryPrintedBasic);
	operands.memoryPrintedBasic = true;
}


@Benchmark
public void cacheOptimizedMultiplicationBenchmark(Operands operands) {
	Runtime runtime = Runtime.getRuntime();
	System.gc();
	long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

	CacheOptimizedMatrixMultiplication cacheOptimizedMultiplication = new CacheOptimizedMatrixMultiplication(64);
	double[][] result = cacheOptimizedMultiplication.multiply(operands.a, operands.b);

	long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
	long memoryUsed = Math.abs(memoryAfter - memoryBefore);
	operands.printMemoryUsage("Cache Optimized Multiplication", operands.n, memoryUsed, operands.memoryPrintedCache);
	operands.memoryPrintedCache = true;
}


@Benchmark
public void loopUnrollingMultiplicationBenchmark(Operands operands) {
	Runtime runtime = Runtime.getRuntime();
	System.gc();
	long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

	LoopUnrollingMultiplication loopUnrollingMultiplication = new LoopUnrollingMultiplication();
	double[][] result = loopUnrollingMultiplication.multiply(operands.a, operands.b);

	long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
	long memoryUsed = Math.abs(memoryAfter - memoryBefore);
	operands.printMemoryUsage("Loop Unrolling Multiplication", operands.n, memoryUsed, operands.memoryPrintedLoopUnrolling);
	operands.memoryPrintedLoopUnrolling = true;
}

}
