package com.anthony.failsafeapi.exception;

public class AiClassificationException extends RuntimeException {
    public AiClassificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
