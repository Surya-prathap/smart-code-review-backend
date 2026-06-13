package com.smartreview.pmd;

import com.smartreview.dto.IssueDTO;
import com.smartreview.enums.RuleType;
import com.smartreview.enums.Severity;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.RuleViolation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.PMDConfiguration;

import static net.sourceforge.pmd.lang.LanguageRegistry.PMD;

@Service
public class PMDAnalysisService {

    public List<IssueDTO> analyzeWithPMD(String code) throws IOException {
        List<IssueDTO> issuesList = new ArrayList<>();

        PMDConfiguration pmdConfiguration = new PMDConfiguration();
        pmdConfiguration.setDefaultLanguageVersion(PMD.getLanguageVersionById("java","17"));

        Path tempFile = Files.createTempFile("CodeReview",".java");
        Files.writeString(tempFile,code);

        pmdConfiguration.addRuleSet("category/java/bestpractices.xml");

        PmdAnalysis pmdAnalysis = PmdAnalysis.create(pmdConfiguration);
        pmdAnalysis.files().addFile(tempFile);

        GlobalAnalysisListener listener = new GlobalAnalysisListener() {
            @Override
            public FileAnalysisListener startFileAnalysis(TextFile textFile) {
                return new FileAnalysisListener() {
                    @Override
                    public void onRuleViolation(RuleViolation ruleViolation) {
                        addPMDIssue(issuesList,ruleViolation);
                    }
                };
            }

            @Override
            public void close(){

            }
        };

        pmdAnalysis.addListener(listener);
        try{
            pmdAnalysis.performAnalysis();
        }finally {
            Files.deleteIfExists(tempFile);
        }

        return issuesList;
    }

    private void addPMDIssue(List<IssueDTO> issuesList, RuleViolation ruleViolation){
        IssueDTO issueDTO = new IssueDTO();
        String ruleName = ruleViolation.getRule().getName();
        issueDTO.setRuleName(ruleName);
        issueDTO.setMessage(ruleViolation.getDescription());
        issueDTO.setSeverity(mapSeverity(ruleViolation));

        switch (ruleName) {

            case "SystemPrintln":
                issueDTO.setRule(RuleType.PRINT_STATEMENT);
                issueDTO.setSuggestion(
                        "Use a logger instead of System.out.println");
                break;

            case "EmptyCatchBlock":
                issueDTO.setRule(RuleType.EMPTY_CATCH_BLOCK);
                issueDTO.setSuggestion(
                        "Handle or log the exception");
                break;

            case "UnusedLocalVariable":
                issueDTO.setSuggestion(
                        "Remove the unused variable or use it appropriately");
                break;

            case "UnusedPrivateField":
                issueDTO.setSuggestion(
                        "Remove the unused field or use it appropriately");
                break;

            case "UnusedFormalParameter":
                issueDTO.setSuggestion(
                        "Remove the unused parameter or use it in the method");
                break;

            case "CommentRequired":
                issueDTO.setSuggestion(
                        "Add proper comments or documentation");
                break;

            case "UnitTestShouldUseTestAnnotation":
                issueDTO.setSuggestion(
                        "Use the @Test annotation for test methods");
                break;

            case "AvoidDuplicateLiterals":
                issueDTO.setSuggestion(
                        "Extract repeated literals into constants");
                break;

            case "ShortVariable":
                issueDTO.setSuggestion(
                        "Use meaningful variable names");
                break;

            default:
                issueDTO.setSuggestion(
                        "Review and fix this PMD issue");
        }
        issuesList.add(issueDTO);
    }

    private Severity mapSeverity(RuleViolation ruleViolation){
        String priorityName = ruleViolation.getRule().getPriority().getName();

        switch (priorityName){
            case "High":
            case "Medium High":
                return Severity.HIGH;

            case "Medium":
                return Severity.MEDIUM;

            case "Medium Low":
            case "Low":
                return Severity.LOW;

            default:
                return Severity.MEDIUM;
        }
    }
}
