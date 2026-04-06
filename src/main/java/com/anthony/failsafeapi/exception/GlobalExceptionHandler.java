package com.anthony.failsafeapi.exception;

import com.anthony.failsafeapi.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AiClassificationException.class)
    public ResponseEntity<ErrorResponse> handleAiClassificationException(AiClassificationException ex) {
        log.error("Error en clasificación AI: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(
                        "NORMAL",
                        "ERROR_HANDLER",
                        "AI_CLASSIFICATION_FAILED"
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Solicitud inválida: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        null,
                        null,
                        "INVALID_REQUEST: " + ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "NORMAL",
                        "ERROR_HANDLER",
                        "UNEXPECTED_ERROR"
                ));
    }
}