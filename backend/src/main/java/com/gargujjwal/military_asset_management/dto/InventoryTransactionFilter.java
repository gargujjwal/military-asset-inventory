package com.gargujjwal.military_asset_management.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InventoryTransactionFilter(
    LocalDateTime startDate,
    LocalDateTime endDate,
    String baseId,
    String equipmentCategoryId,
    String equipmentId) {}
