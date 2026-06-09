package com.smartreview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private int score;
    private List<String> issues;
    private List<String> suggestions;
    private int numberOfIssues;
    private String complexityLevel;
    private LocalDate reviewDate;
}
