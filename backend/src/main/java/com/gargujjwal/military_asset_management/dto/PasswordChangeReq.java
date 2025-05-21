package com.gargujjwal.military_asset_management.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeReq(@NotBlank String oldPassword, @NotBlank String newPassword) {}
