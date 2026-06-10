package com.smartreview.service;

import com.smartreview.dto.RequestDTO;
import com.smartreview.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
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

        int score = 100;
        int violatedRulesCount = 0;

        if (checkMethodNamingRule(code,issues,suggestions)){
            violatedRulesCount++;
        }
        if (checkPrintStatement(code,issues,suggestions)){
            violatedRulesCount++;
        }
        if (checkEmptyCatchBlocks(code,issues,suggestions)){
            violatedRulesCount++;
        }
        if (checkTodoComments(code,issues,suggestions)){
            violatedRulesCount++;
        }
        if (checkLongMethods(code,issues,suggestions)){
            violatedRulesCount++;
        }
        if (checkTooManyParameters(code,issues,suggestions)){
            violatedRulesCount++;
        }

        score -= (violatedRulesCount * 10);

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

    private boolean checkMethodNamingRule(String code,List<String> issues,List<String> suggestions){

        boolean violated = false;
        Pattern pattern = Pattern.compile("(\\w+)\\(");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()){
            String methodName = matcher.group(1);
            if (methodName.length() < 3){
                violated = true;
                issues.add("Method '" + methodName + "' is too short");
                suggestions.add("Use a meaningful method instead of " + methodName);
            }
        }
        return violated;
    }

    private boolean checkPrintStatement(String code,List<String> issues,List<String> suggestions){
        boolean violated = false;
        if (code.contains("System.out.println")){
            violated = true;
            issues.add("Use of System.out.println detected");
            suggestions.add("Use a logging framework instead of System.out.println");
        }
        return violated;
    }

    private boolean checkEmptyCatchBlocks(String code,List<String> issues,List<String> suggestions){
        boolean violated = false;
        Pattern catchPattern = Pattern.compile("catch\\s*\\([^)]*\\)\\s*\\{\\s*\\}");
        Matcher catchMatcher = catchPattern.matcher(code);

        while (catchMatcher.find()){
            violated = true;
            issues.add("Empty catch block detected");
            suggestions.add("Handle the exception or log it properly");
        }
        return violated;
    }

    private boolean checkTodoComments(String code,List<String> issues,List<String> suggestions){
        boolean violated = false;
        if (code.contains("TODO")){
            violated = true;
            issues.add("TODO comment found");
            suggestions.add("Complete the implementation or remove todo comment");
        }
        return violated;
    }

    private boolean checkLongMethods(String code,List<String> issues,List<String> suggestions){
        boolean violated = false;
        String[] lines = code.split("\n");
        if (lines.length > 20){
            violated = true;
            issues.add("Method is too long");
            suggestions.add("Break the method into smaller methods");
        }
        return violated;
    }

    private boolean checkTooManyParameters(String code,List<String> issues,List<String> suggestions){
        boolean violated = false;
        Pattern parameterPattern = Pattern.compile("\\w+\\s*\\((\\s*[^)]*\\s*)");
        Matcher parameterMatcher = parameterPattern.matcher(code);

        while (parameterMatcher.find()){
            String parameters = parameterMatcher.group(1);
            String[] parameterNames = parameters.split(",");
            if (parameterNames.length > 4){
                violated = true;
                issues.add("Method has too many parameters");
                suggestions.add("Consider using DTO or wrapper object");
            }
        }
        return violated;
    }

}
