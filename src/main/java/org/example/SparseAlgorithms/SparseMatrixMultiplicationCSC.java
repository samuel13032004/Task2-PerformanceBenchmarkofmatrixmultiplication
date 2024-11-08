package org.example.SparseAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SparseMatrixMultiplicationCSC {

    static class CSCMatrix {
        double[] values;
        int[] rowIndices;
        int[] colPointers;
        int rows, cols;

        CSCMatrix(double[] values, int[] rowIndices, int[] colPointers, int rows, int cols) {
            this.values = values;
            this.rowIndices = rowIndices;
            this.colPointers = colPointers;
            this.rows = rows;
            this.cols = cols;
        }
    }

    public double[][] multiply(double[][] denseA, double[][] denseB) {

        CSCMatrix cscA = convertToCSC(denseA);
        CSCMatrix cscB = convertToCSC(denseB);

        CSCMatrix resultCSC = multiplyCSC(cscA, cscB);

        return convertToDense(resultCSC);
    }

    private CSCMatrix multiplyCSC(CSCMatrix A, CSCMatrix B) {
        if (A.cols != B.rows) {
            throw new IllegalArgumentException("The dimensions of the matrices do not match for multiplication.");
        }

        List<Double> resultValues = new ArrayList<>();
        List<Integer> resultRowIndices = new ArrayList<>();
        List<Integer> resultColPointers = new ArrayList<>();
        resultColPointers.add(0);

        double[] colResult = new double[A.rows];

        for (int jB = 0; jB < B.cols; jB++) {
            Arrays.fill(colResult, 0.0);

            for (int k = B.colPointers[jB]; k < B.colPointers[jB + 1]; k++) {
                int rowB = B.rowIndices[k];
                double valB = B.values[k];

                for (int i = A.colPointers[rowB]; i < A.colPointers[rowB + 1]; i++) {
                    int rowA = A.rowIndices[i];
                    double valA = A.values[i];
                    colResult[rowA] += valA * valB;
                }
            }

            int nonZeroCount = 0;
            for (int i = 0; i < A.rows; i++) {
                if (colResult[i] != 0.0) {
                    resultValues.add(colResult[i]);
                    resultRowIndices.add(i);
                    nonZeroCount++;
                }
            }

            resultColPointers.add(resultColPointers.get(resultColPointers.size() - 1) + nonZeroCount);
        }

        double[] resultValuesArray = resultValues.stream().mapToDouble(Double::doubleValue).toArray();
        int[] resultRowIndicesArray = resultRowIndices.stream().mapToInt(Integer::intValue).toArray();
        int[] resultColPointersArray = resultColPointers.stream().mapToInt(Integer::intValue).toArray();

        return new CSCMatrix(resultValuesArray, resultRowIndicesArray, resultColPointersArray, A.rows, B.cols);
    }

    public static CSCMatrix convertToCSC(double[][] matrix) {
        List<Double> valuesList = new ArrayList<>();
        List<Integer> rowIndicesList = new ArrayList<>();
        List<Integer> colPointersList = new ArrayList<>();

        int rows = matrix.length;
        int cols = matrix[0].length;
        colPointersList.add(0);

        for (int j = 0; j < cols; j++) {
            int nonZeroCount = 0;
            for (int i = 0; i < rows; i++) {
                if (matrix[i][j] != 0) {
                    valuesList.add(matrix[i][j]);
                    rowIndicesList.add(i);
                    nonZeroCount++;
                }
            }
            colPointersList.add(colPointersList.get(colPointersList.size() - 1) + nonZeroCount);
        }

        double[] values = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] rowIndices = rowIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] colPointers = colPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new CSCMatrix(values, rowIndices, colPointers, rows, cols);
    }

    public double[][] convertToDense(CSCMatrix cscMatrix) {
        double[][] denseMatrix = new double[cscMatrix.rows][cscMatrix.cols];

        for (int j = 0; j < cscMatrix.cols; j++) {
            for (int i = cscMatrix.colPointers[j]; i < cscMatrix.colPointers[j + 1]; i++) {
                denseMatrix[cscMatrix.rowIndices[i]][j] = cscMatrix.values[i];
            }
        }

        return denseMatrix;
    }
}
