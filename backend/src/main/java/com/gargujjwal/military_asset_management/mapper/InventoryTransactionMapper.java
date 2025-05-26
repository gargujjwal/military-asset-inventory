package com.gargujjwal.military_asset_management.mapper;

import com.gargujjwal.military_asset_management.constants.TransactionType;
import com.gargujjwal.military_asset_management.dto.AssignmentTransactionDto;
import com.gargujjwal.military_asset_management.dto.ExpenditureTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.PurchaseTransactionDto;
import com.gargujjwal.military_asset_management.dto.TransferTransactionDto;
import com.gargujjwal.military_asset_management.entity.AssignmentTransaction;
import com.gargujjwal.military_asset_management.entity.ExpenditureTransaction;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import com.gargujjwal.military_asset_management.entity.PurchaseTransaction;
import com.gargujjwal.military_asset_management.entity.TransferTransaction;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

@Mapper(
    componentModel = "spring",
    uses = {EquipmentMapper.class, UserMapper.class, BaseMapper.class},
    builder = @Builder(disableBuilder = true))
public interface InventoryTransactionMapper {

  @SubclassMappings({
    @SubclassMapping(source = AssignmentTransaction.class, target = AssignmentTransactionDto.class),
    @SubclassMapping(source = PurchaseTransaction.class, target = PurchaseTransactionDto.class),
    @SubclassMapping(source = TransferTransaction.class, target = TransferTransactionDto.class),
    @SubclassMapping(
        source = ExpenditureTransaction.class,
        target = ExpenditureTransactionDto.class),
  })
  @Mapping(target = "transactionType", source = "transaction")
  @Mapping(target = "equipment", source = "inventory.equipment")
  InventoryTransactionDto toDto(InventoryTransaction transaction);

  @Mapping(target = "inventory", ignore = true)
  AssignmentTransaction toAssignmentTransactionEntity(AssignmentTransactionDto dto);

  @Mapping(target = "inventory", ignore = true)
  PurchaseTransaction toPurchaseTransactionEntity(PurchaseTransactionDto dto);

  @Mapping(target = "inverseTransaction", ignore = true)
  @Mapping(target = "inventory", ignore = true)
  @Mapping(target = "type", ignore = true)
  TransferTransaction toTransferTransactionEntity(TransferTransactionDto dto);

  @Mapping(target = "inventory", ignore = true)
  ExpenditureTransaction toExpenditureTransactionEntity(ExpenditureTransactionDto dto);

  List<InventoryTransactionDto> toDto(List<InventoryTransaction> inventoryTransactions);

  default TransactionType getTransactionType(InventoryTransaction transaction) {
    if (transaction instanceof AssignmentTransaction) {
      return TransactionType.ASSIGNMENT;
    } else if (transaction instanceof PurchaseTransaction) {
      return TransactionType.PURCHASE;
    } else if (transaction instanceof TransferTransaction) {
      return TransactionType.TRANSFER;
    } else if (transaction instanceof ExpenditureTransaction) {
      return TransactionType.EXPENDITURE;
    } else {
      throw new IllegalArgumentException("Unknown transaction type");
    }
  }
}
