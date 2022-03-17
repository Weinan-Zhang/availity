package com.availity.assessment.web;

import com.availity.assessment.service.FileParsingService;
import com.availity.assessment.util.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GeneralController {
    private static final String CSV_PARSE = "csv_parse";
    private static final String CHECK_PARENTHESES = "check_parentheses";
    private FileParsingService fileParsingService;

    public GeneralController(final FileParsingService fileParsingService) {
        this.fileParsingService = fileParsingService;
    }

    @GetMapping(value = CSV_PARSE)
    public ResponseEntity<List<String>> parseCSV() {
        try {
            List<String> parsedFiles = this.fileParsingService.parse();
            return ResponseEntity.status(HttpStatus.OK).body(parsedFiles);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(List.of());
        }
    }

    @GetMapping(value = CHECK_PARENTHESES)
    public ResponseEntity<Map<String, Boolean>> checkParentheses() {

        String codeToCheck_1 = "(defun fibonacci (N)\n" +
                "  \"Compute the N'th Fibonacci number.\"\n" +
                "  (if (or (zerop N) (= N 1))\n" +
                "      1\n" +
                "    (let\n" +
                "\t((F1 (fibonacci (- N 1)))\n" +
                "\t (F2 (fibonacci (- N 2))))\n" +
                "      (+ F1 F2))))";

        String codeToCheck_2 = "(let\n" +
                "\t((F1 (fibonacci (- N 1)))\n" +
                "\t (F2 (fibonacci (- N 2))))\n" +
                "      (+ F1 F2))";

        String codeToCheck_3 = "(member 'b '(perhaps today is a good day to die)))";
        Map<String, Boolean> checkResult = new HashMap<>();
        checkResult.put(codeToCheck_1, Utils.checkParentheses(codeToCheck_1));
        checkResult.put(codeToCheck_2, Utils.checkParentheses(codeToCheck_2));
        checkResult.put(codeToCheck_3, Utils.checkParentheses(codeToCheck_3));

        return ResponseEntity.status(HttpStatus.OK).body(checkResult);
    }
}
