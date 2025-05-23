package com.gargujjwal.military_asset_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentTransactionDto extends InventoryTransactionDto {
  @NotBlank private String assignedTo;
  @NotNull private Integer quantityExpended;
  @NotNull private Integer quantityAssigned;
  private String notes;
}
