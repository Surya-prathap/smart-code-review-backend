package com.smartreview.controller;

import com.smartreview.dto.RequestDTO;
import com.smartreview.dto.ResponseDTO;
import com.smartreview.service.CodeReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/code-review")
@RequiredArgsConstructor
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    @PostMapping
    public ResponseEntity<?> analyseCode(@RequestBody RequestDTO requestDTO){
       ResponseDTO responseDTO = codeReviewService.analyseCode(requestDTO);
       return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
