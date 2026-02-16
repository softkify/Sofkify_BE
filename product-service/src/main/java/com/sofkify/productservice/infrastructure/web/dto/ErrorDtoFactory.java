package com.sofkify.productservice.infrastructure.web.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

public class ErrorDtoFactory {
    public static ErrorResponse build(HttpStatus status, String message, WebRequest request) {
        return ErrorResponse.builder()
            .timestamp(java.time.LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
    }

    public static ErrorResponse build(HttpStatus status, String message, Object details, WebRequest request) {
        return ErrorResponse.builder()
            .timestamp(java.time.LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .details(details)
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
    }
}
