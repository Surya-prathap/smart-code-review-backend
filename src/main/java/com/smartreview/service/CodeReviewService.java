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

        Pattern pattern = Pattern.compile("(\\w+)\\(");
        Matcher matcher = pattern.matcher(code);

        boolean methodNamingRuleViolated = false;
        int score = 100;
        while (matcher.find()){
            String methodName = matcher.group(1);
                if (methodName.length() < 3){
                    methodNamingRuleViolated = true;
                    issues.add("Method '" + methodName + "' is too short");
                    suggestions.add("Use a meaningful method instead of " + methodName);
                }
        }

        if (methodNamingRuleViolated){
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
