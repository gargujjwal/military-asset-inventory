package com.gargujjwal.military_asset_management.dto;

import com.gargujjwal.military_asset_management.constants.TransferType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferTransactionDto extends InventoryTransactionDto {
  private BaseDto sourceBase;
  private BaseDto destBase;
  private TransferType type;
  private String notes;
}
