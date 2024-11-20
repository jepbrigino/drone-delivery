package com.hitachi.dronedelivery.handler;

import com.hitachi.dronedelivery.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Extract field-specific error messages
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                CommonResponse.builder()
                        .message(errorMessage)
                        .data(Collections.emptyList())
                        .build()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CommonResponse<Object>> handleNotFoundException(ResponseStatusException ex) {
        return ResponseEntity.badRequest().body(
                CommonResponse.builder()
                        .message(ex.getReason())
                        .data(Collections.emptyList())
                        .build()
        );
    }
}
