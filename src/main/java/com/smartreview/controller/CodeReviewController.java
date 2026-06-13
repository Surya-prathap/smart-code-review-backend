package com.smartreview.controller;

import com.smartreview.dto.RequestDTO;
import com.smartreview.dto.ResponseDTO;
import com.smartreview.entity.CodeReview;
import com.smartreview.service.CodeReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    @PostMapping("/code-review")
    public ResponseEntity<?> analyseCode(@RequestBody RequestDTO requestDTO) throws IOException {
       ResponseDTO responseDTO = codeReviewService.analyseCode(requestDTO);
       return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/reviews")
    public List<CodeReview> getAllReviews(){
        return codeReviewService.getAllReviews();
    }

    @GetMapping("/reviews/{id}")
    public CodeReview getReviewById(@PathVariable Long id){
        return codeReviewService.getReviewById(id);
    }
}
