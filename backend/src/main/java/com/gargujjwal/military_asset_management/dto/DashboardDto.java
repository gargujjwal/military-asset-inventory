package com.gargujjwal.military_asset_management.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record DashboardDto(
    int openingBalance,
    int closingBalance,
    int purchases,
    int transferIn,
    int transferOut,
    BaseDto base,
    List<InventoryTransactionDto> transactions) {}
