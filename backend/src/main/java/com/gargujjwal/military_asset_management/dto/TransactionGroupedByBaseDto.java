package com.gargujjwal.military_asset_management.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionGroupedByBaseDto {
  private BaseDto base;
  private List<InventoryTransactionDto> transactions;
}
