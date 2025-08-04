package com.ali.amara.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;
    private int status;
    private Map<String, Object> details;
    private Object[] args;

    public ErrorResponse(String message, String errorCode, String path, int status) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.status = status;
    }
}
