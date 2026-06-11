package com.smartreview.service;

import com.smartreview.dto.IssueDTO;
import com.smartreview.dto.RequestDTO;
import com.smartreview.dto.ResponseDTO;
import com.smartreview.enums.RuleType;
import com.smartreview.enums.Severity;
import com.smartreview.pmd.PMDAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CodeReviewService {

    private final PMDAnalysisService pmdAnalysisService;

    public ResponseDTO analyseCode(RequestDTO requestDTO) throws IOException {

        String code = requestDTO.getCode();
        List<IssueDTO> issuesList = new ArrayList<>();

        issuesList.addAll(pmdAnalysisService.analyzeWithPMD(code));

        int score = 100;
        int violatedRulesCount = 0;

        if (checkMethodNamingRule(code,issuesList)){
            violatedRulesCount++;
        }
        if (checkPrintStatement(code,issuesList)){
            violatedRulesCount++;
        }
        if (checkEmptyCatchBlocks(code,issuesList)){
            violatedRulesCount++;
        }
        if (checkTodoComments(code,issuesList)){
            violatedRulesCount++;
        }
        if (checkLongMethods(code,issuesList)){
            violatedRulesCount++;
        }
        if (checkTooManyParameters(code,issuesList)){
            violatedRulesCount++;
        }

        score -= (violatedRulesCount * 10);

        String complexityLevel = "";
        if (issuesList.isEmpty()){
            complexityLevel = "Easy";
        } else if (issuesList.size() <= 3) {
            complexityLevel = "Medium";
        } else {
            complexityLevel = "Hard";
        }

        ResponseDTO responseDTO = new ResponseDTO();

        responseDTO.setScore(score);
        responseDTO.setIssues(issuesList);
        responseDTO.setNumberOfIssues(issuesList.size());
        responseDTO.setComplexityLevel(complexityLevel);
        responseDTO.setReviewDate(LocalDate.now());

        return responseDTO;
    }

    private boolean checkMethodNamingRule(String code,List<IssueDTO> issuesList){

        boolean violated = false;
        Pattern pattern = Pattern.compile("(\\w+)\\(");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()){
            String methodName = matcher.group(1);
            if (methodName.length() < 3){
                violated = true;
                addIssue(issuesList,RuleType.METHOD_NAMING,"Method name '" +  methodName + "' is too short","Use a meaningful method instead of " + methodName,Severity.LOW);
            }
        }
        return violated;
    }

    private boolean checkPrintStatement(String code,List<IssueDTO> issuesList){
        boolean violated = false;
        if (code.contains("System.out.println")){
            violated = true;
            addIssue(issuesList,RuleType.PRINT_STATEMENT,"Use of System.out.println detected","Use a logging framework instead of System.out.println",Severity.LOW);
        }
        return violated;
    }

    private boolean checkEmptyCatchBlocks(String code,List<IssueDTO> issuesList){
        boolean violated = false;
        Pattern catchPattern = Pattern.compile("catch\\s*\\([^)]*\\)\\s*\\{\\s*\\}");
        Matcher catchMatcher = catchPattern.matcher(code);

        while (catchMatcher.find()){
            violated = true;
            addIssue(issuesList,RuleType.EMPTY_CATCH_BLOCK,"Empty catch block detected","Handle the exception or log it properly",Severity.HIGH);
        }
        return violated;
    }

    private boolean checkTodoComments(String code,List<IssueDTO> issuesList){
        boolean violated = false;
        if (code.contains("TODO")){
            violated = true;
            addIssue(issuesList,RuleType.TODO_COMMENT,"TODO comment found","Complete the implementation or remove todo comment",Severity.LOW);
        }
        return violated;
    }

    private boolean checkLongMethods(String code,List<IssueDTO> issuesList){
        boolean violated = false;
        String[] lines = code.split("\n");
        if (lines.length > 20){
            violated = true;
            addIssue(issuesList,RuleType.LONG_METHOD,"Method is too long","Break the method into smaller methods",Severity.MEDIUM);
        }
        return violated;
    }

    private boolean checkTooManyParameters(String code,List<IssueDTO> issuesList){
        boolean violated = false;
        Pattern parameterPattern = Pattern.compile("\\w+\\s*\\((\\s*[^)]*\\s*)");
        Matcher parameterMatcher = parameterPattern.matcher(code);

        while (parameterMatcher.find()){
            String parameters = parameterMatcher.group(1);
            String[] parameterNames = parameters.split(",");
            if (parameterNames.length > 4){
                violated = true;
                addIssue(issuesList,RuleType.TOO_MANY_PARAMETERS,"Method has too many parameters","Consider using DTO or wrapper object",Severity.MEDIUM);
            }
        }
        return violated;
    }

    private void addIssue(List<IssueDTO> issuesList, RuleType rule, String message, String suggestion, Severity severity){
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setRule(rule);
        issueDTO.setMessage(message);
        issueDTO.setSuggestion(suggestion);
        issueDTO.setSeverity(severity);
        issuesList.add(issueDTO);
    }

}
