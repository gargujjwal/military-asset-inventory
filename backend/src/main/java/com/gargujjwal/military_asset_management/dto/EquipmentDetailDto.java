package com.gargujjwal.military_asset_management.dto;

public record EquipmentDetailDto(
    String id, String name, String description, EquipmentCategoryDto equipmentCategory) {}
