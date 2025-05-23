package com.gargujjwal.military_asset_management.dto;

import com.gargujjwal.military_asset_management.constants.TransferType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferTransactionDto extends InventoryTransactionDto {
  @NotNull private BaseDto sourceBase;
  @NotNull private BaseDto destBase;
  private TransferType type;
  private String notes;
}
