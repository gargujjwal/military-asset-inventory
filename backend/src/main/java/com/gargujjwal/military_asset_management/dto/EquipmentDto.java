package com.gargujjwal.military_asset_management.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipmentDto(String id, @NotBlank String name, String description) {}
