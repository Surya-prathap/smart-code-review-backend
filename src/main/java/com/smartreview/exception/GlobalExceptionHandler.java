package com.smartreview.exception;

import com.smartreview.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleReviewNotFoundException(ReviewNotFoundException ex){
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(LocalDateTime.now(),HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }
}
