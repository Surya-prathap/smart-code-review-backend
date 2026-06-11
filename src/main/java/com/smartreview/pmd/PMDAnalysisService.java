package com.smartreview.pmd;

import com.smartreview.dto.IssueDTO;
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
        System.out.println("PMD Method Called");
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
                        System.out.println("Violation Found: " + ruleViolation.getRule().getName());
                        addPMDIssue(issuesList,ruleViolation);
                    }
                };
            }

            @Override
            public void close() throws Exception {

            }
        };

        pmdAnalysis.addListener(listener);
        pmdAnalysis.performAnalysis();

        System.out.println("PMD Issues Found: " + issuesList.size());

        return issuesList;
    }

    private void addPMDIssue(List<IssueDTO> issuesList, RuleViolation ruleViolation){
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setRuleName(ruleViolation.getRule().getName());
        issueDTO.setMessage(ruleViolation.getDescription());
        issueDTO.setSeverity(Severity.MEDIUM);
        issuesList.add(issueDTO);
    }
}
