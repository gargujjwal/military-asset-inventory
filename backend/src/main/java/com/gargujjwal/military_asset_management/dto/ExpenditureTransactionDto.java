package com.gargujjwal.military_asset_management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenditureTransactionDto extends InventoryTransactionDto {
  @NotBlank private String reason;
  private String description;
}
