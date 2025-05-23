package com.gargujjwal.military_asset_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gargujjwal.military_asset_management.constants.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UserDto(
    String id,
    @NotBlank String username,
    @NotBlank String fullName,
    @NotNull Role role,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt) {}
