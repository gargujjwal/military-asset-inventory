package com.gargujjwal.military_asset_management.service.transaction;

import com.gargujjwal.military_asset_management.constants.TransactionType;
import com.gargujjwal.military_asset_management.dto.AssignmentTransactionDto;
import com.gargujjwal.military_asset_management.dto.ExpenditureTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.PurchaseTransactionDto;
import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.EquipmentInventory;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.InvalidRequestException;
import com.gargujjwal.military_asset_management.exception.InventoryNotEnoughException;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.mapper.BaseMapper;
import com.gargujjwal.military_asset_management.mapper.EquipmentMapper;
import com.gargujjwal.military_asset_management.mapper.InventoryTransactionMapper;
import com.gargujjwal.military_asset_management.repository.BaseRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryTransactionRepository;
import com.gargujjwal.military_asset_management.service.BaseService;
import com.gargujjwal.military_asset_management.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("assignmentPurchaseExpenditureTransactionCreateDeleteStrategy")
@RequiredArgsConstructor
public class AssignmentPurchaseExpenditureTransactionCreateDeleteStrategy
    implements TransactionCreateDeleteStrategy {
  private final InventoryTransactionMapper transactionMapper;
  private final UserService uServ;
  private final EquipmentMapper equipmentMapper;
  private final BaseRepository baseRepo;
  private final BaseService baseService;
  private final BaseMapper baseMapper;
  private final EquipmentInventoryRepository inventoryRepo;
  private final EquipmentInventoryTransactionRepository transRepo;

  @Override
  @Transactional
  public void createTransaction(InventoryTransactionDto transactionDto, String baseId) {
    InventoryTransaction transaction = convertToInventoryTransaction(transactionDto);
    transaction.setTransactionDate(null);
    transaction.setInventory(
        EquipmentInventory.builder()
            .equipment(equipmentMapper.toEntity(transactionDto.getEquipment()))
            .build());

    // set user
    User loggedInUser = uServ.getLoggedInUser();
    transaction.setDoneBy(loggedInUser);

    // set the base
    Base base;
    if (loggedInUser.isAdmin()) {
      base =
          baseRepo
              .findById(baseId)
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Base for which transaction is being done does not exist"));
    } else {
      base = baseMapper.toEntity(baseService.getUserAssignedBase(loggedInUser.getUsername()));
    }

    if (List.of(TransactionType.ASSIGNMENT, TransactionType.EXPENDITURE)
        .contains(transactionDto.getTransactionType())) {
      completeBalanceReducingTransaction(transaction, base);
    } else {
      completeBalanceIncreasingTransaction(transaction, base);
    }
  }

  private InventoryTransaction convertToInventoryTransaction(InventoryTransactionDto dto) {
    switch (dto.getTransactionType()) {
      case ASSIGNMENT:
        return transactionMapper.toAssignmentTransactionEntity((AssignmentTransactionDto) dto);
      case EXPENDITURE:
        return transactionMapper.toExpenditureTransactionEntity((ExpenditureTransactionDto) dto);
      case PURCHASE:
        return transactionMapper.toPurchaseTransactionEntity((PurchaseTransactionDto) dto);
      default:
        throw new InvalidRequestException("Transaction type was not set in dto");
    }
  }

  private void completeBalanceReducingTransaction(InventoryTransaction trans, Base base) {
    int quantityChange = Math.abs(trans.getQuantityChange());

    // update inventory, inventory should always exist in this case
    EquipmentInventory inv =
        inventoryRepo
            .findByBaseAndEquipment(base, trans.getInventory().getEquipment())
            .orElseThrow(
                () ->
                    new InventoryNotEnoughException(
                        "Base does not have this equipment in inventory"));
    inv.setClosingBalance(inv.getClosingBalance() - quantityChange);
    trans.setInventory(inv);

    // record transaction
    trans.setQuantityChange(-quantityChange);
    trans.setResultingBalance(inv.getClosingBalance());

    transRepo.save(trans);
  }

  private void completeBalanceIncreasingTransaction(InventoryTransaction trans, Base base) {
    int quantityChange = Math.abs(trans.getQuantityChange());

    // update inventory, inventory might not always exist in this case
    EquipmentInventory inv =
        inventoryRepo
            .findByBaseAndEquipment(base, trans.getInventory().getEquipment())
            .orElse(
                EquipmentInventory.builder()
                    .openingBalance(0)
                    .closingBalance(0)
                    .base(base)
                    .equipment(trans.getInventory().getEquipment())
                    .build());
    inv.setClosingBalance(inv.getClosingBalance() + quantityChange);
    inv = inventoryRepo.save(inv);
    trans.setInventory(inv);

    // record transaction
    trans.setQuantityChange(quantityChange);
    trans.setResultingBalance(inv.getClosingBalance());

    transRepo.save(trans);
  }

  @Override
  @Transactional
  public void deleteTransaction(InventoryTransaction transaction) {
    EquipmentInventory inv = transaction.getInventory();
    inv.setClosingBalance(inv.getClosingBalance() - transaction.getQuantityChange());
    inventoryRepo.save(inv);

    transRepo.delete(transaction);
  }
}
