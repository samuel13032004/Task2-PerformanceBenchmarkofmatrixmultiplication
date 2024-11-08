package org.example;

import org.example.SparseAlgorithms.SparseMatrixMultiplicationCSR;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BigMatrixMultiplier {
    public static SparseMatrixMultiplicationCSR.CSRMatrix readMatrixFromFile(String filePath) throws IOException {
        List<Double> valuesList = new ArrayList<>();
        List<Integer> columnIndicesList = new ArrayList<>();
        List<Integer> rowPointersList = new ArrayList<>();

        int rows = 0, cols = 0;
        rowPointersList.add(0);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("%") || line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (rows == 0 && cols == 0) {
                    rows = Integer.parseInt(parts[0]);
                    cols = Integer.parseInt(parts[1]);
                    continue;
                }

                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                double value = Double.parseDouble(parts[2]);

                if (row >= rowPointersList.size() - 1) {
                    rowPointersList.add(valuesList.size());
                }

                valuesList.add(value);
                columnIndicesList.add(col);
            }
            rowPointersList.add(valuesList.size());
        }

        double[] values = valuesList.stream().mapToDouble(Double::doubleValue).toArray();
        int[] columnIndices = columnIndicesList.stream().mapToInt(Integer::intValue).toArray();
        int[] rowPointers = rowPointersList.stream().mapToInt(Integer::intValue).toArray();

        return new SparseMatrixMultiplicationCSR.CSRMatrix(values, columnIndices, rowPointers, rows, cols);
    }

    public static void main(String[] args) {
        try {
            String filePath = "mc2depi.mtx";
            SparseMatrixMultiplicationCSR.CSRMatrix matrix = readMatrixFromFile(filePath);

            long startTime = System.currentTimeMillis();
            SparseMatrixMultiplicationCSR.CSRMatrix result = matrix.multiply(matrix);
            long endTime = System.currentTimeMillis();

            long totalTimeMillis = endTime - startTime;
            long minutes = totalTimeMillis / 60000;
            long seconds = (totalTimeMillis % 60000) / 1000;

            System.out.println("Time taken for multiplication: " + minutes + " minutes and " + seconds + " seconds");
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}

