package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.constants.Role;
import com.gargujjwal.military_asset_management.dto.AssignmentTransactionDto;
import com.gargujjwal.military_asset_management.dto.BaseDto;
import com.gargujjwal.military_asset_management.dto.ExpenditureTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.dto.PurchaseTransactionDto;
import com.gargujjwal.military_asset_management.dto.TransactionGroupedByBaseDto;
import com.gargujjwal.military_asset_management.dto.TransferTransactionDto;
import com.gargujjwal.military_asset_management.entity.AssignmentTransaction;
import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.Equipment;
import com.gargujjwal.military_asset_management.entity.EquipmentInventory;
import com.gargujjwal.military_asset_management.entity.ExpenditureTransaction;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import com.gargujjwal.military_asset_management.entity.PurchaseTransaction;
import com.gargujjwal.military_asset_management.entity.TransferTransaction;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.InvalidRequestException;
import com.gargujjwal.military_asset_management.exception.InventoryNotEnoughException;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.exception.UnauthorizedException;
import com.gargujjwal.military_asset_management.filter.InventoryTransactionSpecification;
import com.gargujjwal.military_asset_management.mapper.BaseMapper;
import com.gargujjwal.military_asset_management.mapper.EquipmentMapper;
import com.gargujjwal.military_asset_management.mapper.InventoryTransactionMapper;
import com.gargujjwal.military_asset_management.repository.BaseRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryTransactionRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "EQUIPMENT_INVENTORY_TRANSACTION_SERVICE")
@RequiredArgsConstructor
public class EquipmentInventoryTransactionService {
  private final EquipmentInventoryTransactionRepository inventoryTransactionRepository;
  private final BaseMapper baseMapper;
  private final InventoryTransactionMapper inventoryTransactionMapper;
  private final BaseRepository baseRepository;
  private final UserService userService;
  private final BaseService baseService;
  private final EquipmentInventoryRepository equipmentInventoryRepository;
  private final EquipmentMapper equipmentMapper;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true)
  public List<TransactionGroupedByBaseDto> getAllTransactionsForAllBases() {
    // could have paginated the results here
    return convertToGroupedDto(inventoryTransactionRepository.findAll());
  }

  @PreAuthorize("@baseService.canAccessBase(#id)")
  @Transactional(readOnly = true)
  public List<InventoryTransactionDto> getAllTransactionsForBase(String baseId) {
    return baseRepository
        .findById(baseId)
        .map(
            base ->
                inventoryTransactionMapper.toDto(
                    inventoryTransactionRepository.findAllByInventory_Base(base)))
        .orElseThrow(() -> new ResourceNotFoundException("Base not found"));
  }

  @Transactional(readOnly = true)
  public List<TransactionGroupedByBaseDto> getFilteredTransactions(
      InventoryTransactionFilter filterDto) {
    List<InventoryTransaction> transactions =
        inventoryTransactionRepository.findAll(
            InventoryTransactionSpecification.filterBy(filterDto));

    // if admin is logged in, return all transactions
    User loggedInUser = userService.getLoggedInUser();
    if (loggedInUser.getRole().equals(Role.ADMIN)) {
      return convertToGroupedDto(transactions);
    }

    // otherwise return only transactions of base he is assigned to
    BaseDto base = baseService.getUserAssignedBase(loggedInUser.getId());
    return convertToGroupedDto(
        transactions.stream()
            .filter(t -> t.getInventory().getBase().getId().equals(base.id()))
            .toList());
  }

  @Transactional
  public void createTransaction(InventoryTransactionDto transactionDto, String baseId) {
    if (transactionDto.getQuantityChange().equals(0)) {
      throw new InvalidRequestException("Quantity change can't be 0");
    }

    // verify whether the user can create transaction for current base
    User loggedInUser = userService.getLoggedInUser();
    if (!(loggedInUser.isAdmin()
        || baseService.getUserAssignedBase(loggedInUser.getUsername()).id().equals(baseId))) {
      throw new UnauthorizedException("You are not authorized to create transaction for this base");
    }

    Base base =
        baseRepository
            .findById(baseId)
            .orElseThrow(() -> new ResourceNotFoundException("Base not found"));
    Equipment equipment = equipmentMapper.toEntity(transactionDto.getEquipment());
    Optional<EquipmentInventory> inventory =
        equipmentInventoryRepository.findByBaseAndEquipment(base, equipment);

    int currentBalance;
    EquipmentInventory inv;
    if (inventory.isPresent()) {
      // check if we have enough inventory
      inv = inventory.get();
      if (transactionDto.getQuantityChange() < 0
          && inv.getClosingBalance() < Math.abs(transactionDto.getQuantityChange())) {
        throw new InventoryNotEnoughException("We don't have enough inventory");
      }

      // will either increase or decrease the inventory
      currentBalance = inv.getClosingBalance() + transactionDto.getQuantityChange();
      inv.setClosingBalance(currentBalance);

    } else {
      // check if we are trying to decrease inventory of a product of which we have none
      if (transactionDto.getQuantityChange() < 0) {
        throw new InventoryNotEnoughException("We don't have this equipment in inventory");
      }

      // have to create new inventory
      currentBalance = transactionDto.getQuantityChange();
      inv =
          EquipmentInventory.builder()
              .openingBalance(currentBalance)
              .closingBalance(currentBalance)
              .equipment(equipment)
              .base(base)
              .build();
    }

    // update or create inventory
    inv = equipmentInventoryRepository.save(inv);

    switch (transactionDto.getTransactionType()) {
      case ASSIGNMENT:
        AssignmentTransaction assignmentTransaction =
            inventoryTransactionMapper.toAssignmentTransactionEntity(
                (AssignmentTransactionDto) transactionDto);
        assignmentTransaction.setResultingBalance(currentBalance);
        assignmentTransaction.setDoneBy(loggedInUser);
        assignmentTransaction.setInventory(inv);
        inventoryTransactionRepository.save(assignmentTransaction);
        break;
      case EXPENDITURE:
        ExpenditureTransaction expenditureTransaction =
            inventoryTransactionMapper.toExpenditureTransactionEntity(
                (ExpenditureTransactionDto) transactionDto);
        expenditureTransaction.setResultingBalance(currentBalance);
        expenditureTransaction.setDoneBy(loggedInUser);
        expenditureTransaction.setInventory(inv);
        inventoryTransactionRepository.save(expenditureTransaction);
        break;
      case PURCHASE:
        PurchaseTransaction purchaseTransaction =
            inventoryTransactionMapper.toPurchaseTransactionEntity(
                (PurchaseTransactionDto) transactionDto);
        purchaseTransaction.setResultingBalance(currentBalance);
        purchaseTransaction.setDoneBy(loggedInUser);
        purchaseTransaction.setInventory(inv);
        inventoryTransactionRepository.save(purchaseTransaction);
        break;
      case TRANSFER:
        TransferTransaction transferTransaction =
            inventoryTransactionMapper.toTransferTransactionEntity(
                (TransferTransactionDto) transactionDto);
        // can't move assets to same base
        if (transferTransaction.getDestBase().equals(transferTransaction.getSourceBase())) {
          throw new InvalidRequestException("Destination base can't be same as source base");
        }

        transferTransaction.setResultingBalance(currentBalance);
        transferTransaction.setDoneBy(loggedInUser);
        transferTransaction.setInventory(inv);
        inventoryTransactionRepository.save(transferTransaction);
        break;
      default:
        throw new IllegalArgumentException(
            "Transaction type not supported: " + transactionDto.getTransactionType());
    }
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteTransaction(String transactionId) {
    InventoryTransaction transaction =
        inventoryTransactionRepository
            .findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    // update the inventory accordingly
    transaction
        .getInventory()
        .setClosingBalance(
            transaction.getInventory().getClosingBalance() - transaction.getQuantityChange());
    equipmentInventoryRepository.save(transaction.getInventory());

    // delete the transaction
    inventoryTransactionRepository.delete(transaction);
  }

  private List<TransactionGroupedByBaseDto> convertToGroupedDto(
      List<InventoryTransaction> transactions) {
    return transactions.stream()
        .collect(Collectors.groupingBy(t -> t.getInventory().getBase()))
        .entrySet()
        .stream()
        .map(
            en ->
                TransactionGroupedByBaseDto.builder()
                    .base(baseMapper.toDto(en.getKey()))
                    .transactions(inventoryTransactionMapper.toDto(en.getValue()))
                    .build())
        .toList();
  }
}
