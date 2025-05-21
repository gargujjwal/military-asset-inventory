package com.gargujjwal.military_asset_management.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(String message, List<String> errors,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp) {
    public ErrorResponse(String message, List<String> errors) {
        this(message, errors, LocalDateTime.now());
    }

    public ErrorResponse(String message) {
        this(message, List.of(), LocalDateTime.now());
    }

}
