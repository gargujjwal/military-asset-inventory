package com.gargujjwal.military_asset_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gargujjwal.military_asset_management.constants.Role;
import java.time.LocalDateTime;

public record UserDto(
    String id,
    String username,
    String fullName,
    Role role,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt) {}
