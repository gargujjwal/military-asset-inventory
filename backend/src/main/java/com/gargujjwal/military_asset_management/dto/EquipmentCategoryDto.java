package com.gargujjwal.military_asset_management.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipmentCategoryDto(
    String id, @NotBlank String name, String description, @NotBlank String unitOfMeasure) {}
