package com.backend.api.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJson(
            HttpMessageNotReadableException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        log.debug("Validation exception: {}",exception.toString());

        if (exception.getCause() instanceof InvalidFormatException ife) {
            String field = ife.getPath().getFirst().getFieldName();
            Class<?> targetType = ife.getTargetType();

            if (targetType.equals(LocalDate.class)) {
                errors.put(field, "Invalid date format. Expected format: yyyy-MM-dd");
            } else {
                errors.put(field, "Invalid value");
            }
        }

        return ResponseEntity.badRequest().body(errors);
    }
}