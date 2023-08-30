package com.example.LaterCalculator.service;

import com.example.LaterCalculator.model.Operation;
import com.example.LaterCalculator.model.ResultView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationService {
    public static final String RESULTS_DIRECTORY = "results/";
    public String getResultsDirectory() {
        return RESULTS_DIRECTORY;
    }
    public double DoOps(Operation op) {

        double result = 0.0;

        List<Double> operands = op.getOperands();

        switch (op.getOperation()) {
            case "sum":
                for (double operand : operands) {
                    result += operand;
                }
                break;
            case "sub":
                result = operands.get(0);
                for (int i = 1; i < operands.size(); i++) {
                    result -= operands.get(i);
                }
                break;
            case "mul":
                result = 1.0;
                for (Double operand : operands) {
                    result *= operand;
                }
                break;
            case "div":
                if (operands.size() < 2) {
                    throw new IllegalArgumentException("Please provide more operands.");
                }
                else {
                    boolean NullDenominator = false;
                    result = operands.get(0);
                    for (int i = 1; i < operands.size(); i++) {
                        if (operands.get(i) == 0) {
                            NullDenominator = true;
                            break;
                        }
                        result /= operands.get(i);
                    }
                    if (NullDenominator) {
                        throw new ArithmeticException("Not allowed. Denominator cannot be 0");
                    }
                }
                break;
            case "pow":
                if (operands.size() != 2) {
                    throw new IllegalArgumentException("Please provide 2 operands.");
                }
                double base = operands.get(0);
                double exp = operands.get(1);
                result = Math.pow(base, exp);
                break;
            case "sqrt":
                if (operands.size() != 1) {
                    throw new IllegalArgumentException("Please provide 1 operand");
                }
                result = Math.sqrt(operands.get(0));
                break;
            default:
                System.out.println("Unknown operation");
                break;
        }
        return result;
    }
    public boolean checkResults(String operation, double result, String filePath) {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length != 2) {
                    continue;
                }
                String lineOperation = parts[0];
                double lineResult = Double.parseDouble(parts[1]);
                if (operation.equals(lineOperation) && Math.abs(result - lineResult) < 1e-6) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<ResultView> FileResult(String filePath) {
        List<ResultView> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String operation = parts[0];
                    double resultValue = Double.parseDouble(parts[1]);
                    results.add(new ResultView(operation, resultValue));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }
}
