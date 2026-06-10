package com.smartreview.dto;

import com.smartreview.enums.RuleType;
import com.smartreview.enums.Severity;
import lombok.Data;

@Data
public class IssueDTO {
    private RuleType rule;
    private String message;
    private String suggestion;
    private Severity severity;
}
