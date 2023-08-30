package com.example.LaterCalculator.controller;

import ch.qos.logback.core.model.Model;
import com.example.LaterCalculator.model.Operation;
import com.example.LaterCalculator.model.ResultView;
import com.example.LaterCalculator.service.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OperationController {
    private final OperationService operationService;
    @PostMapping("/do-math")
    public ResponseEntity<String> doMath(@RequestBody List<Operation> operations) throws IOException {
        String resultsDirectory = "results/";
        String fileName = resultsDirectory + "results.txt";

        Path filePath = Path.of(fileName);

        List<String> lines = new ArrayList<>();

        for (Operation op : operations) {
            double result = operationService.DoOps(op);
            String operationName = op.getOperation();
            String content = operationName + ": " + Double.toString(result);
            lines.add(content);
        }

        Files.write(filePath, lines);

        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/check-finished")
    public ResponseEntity<Boolean> checkFinished(@RequestParam String filename) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String filePath = operationService.getResultsDirectory() + filename;
        File resultFile = new File(filePath);

        if (resultFile.exists()) {
            if (resultFile.length() == 0) {
                return ResponseEntity.ok(false);
            }

            boolean done = true;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(": ");
                    if (parts.length != 2) {
                        done = false;
                        break;
                    }
                    String lineOperation = parts[0];
                    double lineResult = Double.parseDouble(parts[1]);

                    if (!operationService.checkResults(lineOperation, lineResult, filePath)) {
                        done = false;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(done);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/results")
    public ModelAndView getResults(@RequestParam String filename) {
        String filePath = operationService.getResultsDirectory() + filename;
        List<ResultView> results = operationService.FileResult(filePath);

        ModelAndView modelAndView = new ModelAndView("result");
        modelAndView.addObject("results", results);

        return modelAndView;
    }
}
