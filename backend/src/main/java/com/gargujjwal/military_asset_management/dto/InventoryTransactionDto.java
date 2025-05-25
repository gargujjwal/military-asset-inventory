package com.gargujjwal.military_asset_management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gargujjwal.military_asset_management.constants.TransactionType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "transactionType",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = PurchaseTransactionDto.class, name = "PURCHASE"),
  @JsonSubTypes.Type(value = TransferTransactionDto.class, name = "TRANSFER"),
  @JsonSubTypes.Type(value = AssignmentTransactionDto.class, name = "ASSIGNMENT"),
  @JsonSubTypes.Type(value = ExpenditureTransactionDto.class, name = "EXPENDITURE"),
})
@ToString
public class InventoryTransactionDto {
  protected String id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  protected LocalDateTime transactionDate;

  @NotNull protected Integer quantityChange;
  @NotNull protected EquipmentDto equipment;
  protected Integer resultingBalance;
  protected UserDto doneBy;
  @NotNull protected TransactionType transactionType;
}
