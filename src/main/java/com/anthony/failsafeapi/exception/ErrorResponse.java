package com.anthony.failsafeapi.exception;

public record ErrorResponse(
    String status,
    String source,
    String message
) {}
