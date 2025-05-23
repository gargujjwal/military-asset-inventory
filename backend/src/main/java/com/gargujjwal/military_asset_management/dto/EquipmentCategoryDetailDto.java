package com.gargujjwal.military_asset_management.dto;

import java.util.Set;

public record EquipmentCategoryDetailDto(
    String id,
    String name,
    String description,
    String unitOfMeasure,
    Set<EquipmentDto> equipments) {}
