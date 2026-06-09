package com.smartreview.service;

import com.smartreview.dto.RequestDTO;
import com.smartreview.dto.ResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeReviewService {

    public ResponseDTO analyseCode(RequestDTO requestDTO){
        String code = requestDTO.getCode();
        List<String> issues = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        boolean methodNamingRuleViolated = false;
        boolean printStatementRuleViolated = false;
        boolean emptyCatchRuleViolated = false;
        boolean todoCommentRuleViolated = false;
        boolean longMethodRuleViolated = false;

        Pattern pattern = Pattern.compile("(\\w+)\\(");
        Matcher matcher = pattern.matcher(code);

        Pattern catchPattern = Pattern.compile("catch\\s*\\([^)]*\\)\\s*\\{\\s*\\}");
        Matcher catchMatcher = catchPattern.matcher(code);


        int score = 100;

        while (matcher.find()){
            String methodName = matcher.group(1);
                if (methodName.length() < 3){
                    methodNamingRuleViolated = true;
                    issues.add("Method '" + methodName + "' is too short");
                    suggestions.add("Use a meaningful method instead of " + methodName);
                }
        }

        if (code.contains("System.out.println")){
            printStatementRuleViolated = true;
            issues.add("Use of System.out.println detected");
            suggestions.add("Use a logging framework instead of System.out.println");
        }

        while (catchMatcher.find()){
            emptyCatchRuleViolated = true;
            issues.add("Empty catch block detected");
            suggestions.add("Handle the exception or log it properly");
        }

        if (code.contains("TODO")){
            todoCommentRuleViolated = true;
            issues.add("TODO comment found");
            suggestions.add("Complete the implementation or remove todo comment");
        }

        String[] lines = code.split("\n");
        if (lines.length > 20){
            longMethodRuleViolated = true;
            issues.add("Method is too long");
            suggestions.add("Break the method into smaller methods");
        }

        if (methodNamingRuleViolated){
            score -= 10;
        }
        if (printStatementRuleViolated){
            score -= 10;
        }
        if (emptyCatchRuleViolated){
            score -= 10;
        }
        if (todoCommentRuleViolated){
            score -= 10;
        }
        if (longMethodRuleViolated){
            score -= 10;
        }

        String complexityLevel = "";
        if (issues.isEmpty()){
            complexityLevel = "Easy";
        } else if (issues.size() <= 3) {
            complexityLevel = "Medium";
        } else {
            complexityLevel = "Hard";
        }

        ResponseDTO responseDTO = new ResponseDTO();

        responseDTO.setScore(score);
        responseDTO.setIssues(issues);
        responseDTO.setSuggestions(suggestions);
        responseDTO.setNumberOfIssues(issues.size());
        responseDTO.setComplexityLevel(complexityLevel);
        responseDTO.setReviewDate(LocalDate.now());

        return responseDTO;
    }
}
