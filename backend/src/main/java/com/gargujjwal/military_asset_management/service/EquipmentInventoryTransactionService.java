package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.constants.Role;
import com.gargujjwal.military_asset_management.constants.TransactionType;
import com.gargujjwal.military_asset_management.constants.TransferType;
import com.gargujjwal.military_asset_management.dto.AssignmentTransactionDto;
import com.gargujjwal.military_asset_management.dto.BaseDto;
import com.gargujjwal.military_asset_management.dto.ExpenditureTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.dto.PurchaseTransactionDto;
import com.gargujjwal.military_asset_management.dto.TransactionGroupedByBaseDto;
import com.gargujjwal.military_asset_management.dto.TransferTransactionDto;
import com.gargujjwal.military_asset_management.entity.AssignmentTransaction;
import com.gargujjwal.military_asset_management.entity.AuditLog;
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
  private final AuditService auditService;

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

  private TransferTransaction createInverseTransaction(TransferTransaction transaction) {
    TransferTransaction inverseTransaction =
        TransferTransaction.builder()
            .sourceBase(transaction.getDestBase())
            .destBase(transaction.getSourceBase())
            .notes(transaction.getNotes())
            .build();
    inverseTransaction.setDoneBy(transaction.getDoneBy());
    inverseTransaction.setQuantityChange(transaction.getQuantityChange());
    Optional<EquipmentInventory> inv =
        equipmentInventoryRepository.findByBaseAndEquipment(
            transaction.getDestBase(), transaction.getInventory().getEquipment());
    EquipmentInventory inventory;
    if (inv.isPresent()) {
      inventory = inv.get();
      inventory.setClosingBalance(inventory.getClosingBalance() + transaction.getQuantityChange());
      inverseTransaction.setInventory(inventory);
    } else {
      inventory =
          EquipmentInventory.builder()
              .openingBalance(transaction.getQuantityChange())
              .closingBalance(transaction.getQuantityChange())
              .equipment(transaction.getInventory().getEquipment())
              .base(transaction.getDestBase())
              .build();
    }
    inverseTransaction.setInventory(equipmentInventoryRepository.save(inventory));
    inverseTransaction.setResultingBalance(
        inventory.getClosingBalance() + transaction.getQuantityChange());

    inverseTransaction.setInverseTransaction(transaction);
    return inverseTransaction;
  }

  private void createTransferTransaction(TransferTransaction transaction) {
    // set transaction type
    transaction.setType(transaction.getQuantityChange() > 0 ? TransferType.IN : TransferType.OUT);

    // set source base and dest base
    Base srcBase, destBase;
    User loggedInUser = userService.getLoggedInUser();
    if (loggedInUser.isAdmin()) {
      srcBase = transaction.getSourceBase();
      destBase = transaction.getDestBase();
    } else {
      Base assignedBase =
          baseMapper.toEntity(baseService.getUserAssignedBase(loggedInUser.getUsername()));
      if (transaction.getType().equals(TransferType.IN)) {
        // transfer in
        srcBase = transaction.getSourceBase();
        destBase = assignedBase;
      } else {
        // transfer out
        srcBase = assignedBase;
        destBase = transaction.getDestBase();
      }
    }
    transaction.setSourceBase(srcBase);
    transaction.setDestBase(destBase);

    // check inventory of source base
    EquipmentInventory srcBaseInventory =
        equipmentInventoryRepository
            .findByBaseAndEquipment(srcBase, transaction.getInventory().getEquipment())
            .orElseThrow(
                () ->
                    new InventoryNotEnoughException(
                        "Source base does not have inventory of equipment"));
    if (srcBaseInventory.getClosingBalance() < Math.abs(transaction.getQuantityChange())) {
      throw new InventoryNotEnoughException("Source base does not have enough inventory");
    }

    // create or update inventory
    EquipmentInventory destBaseInventory =
        equipmentInventoryRepository
            .findByBaseAndEquipment(destBase, transaction.getInventory().getEquipment())
            .orElse(
                EquipmentInventory.builder()
                    .openingBalance(0)
                    .closingBalance(0)
                    .base(destBase)
                    .equipment(transaction.getInventory().getEquipment())
                    .build());
    if (transaction.getType().equals(TransferType.IN)) {
      srcBaseInventory.setClosingBalance(
          srcBaseInventory.getClosingBalance() + transaction.getQuantityChange());
      destBaseInventory.setClosingBalance(
          destBaseInventory.getClosingBalance() - transaction.getQuantityChange());
    } else {
      srcBaseInventory.setClosingBalance(
          srcBaseInventory.getClosingBalance() - transaction.getQuantityChange());
      destBaseInventory.setClosingBalance(
          destBaseInventory.getClosingBalance() + transaction.getQuantityChange());
    }
    srcBaseInventory = equipmentInventoryRepository.save(srcBaseInventory);
    destBaseInventory = equipmentInventoryRepository.save(destBaseInventory);
    if (transaction.getType().equals(TransferType.IN)) {
      transaction.setInventory(srcBaseInventory);
    } else {
      transaction.setInventory(destBaseInventory);
    }

    // create inverse transaction record
    TransferTransaction inverseTrans =
        TransferTransaction.builder()
            .sourceBase(destBase)
            .destBase(srcBase)
            .notes(String.format("Assets transferred to/from %s", srcBase.getName()))
            .inverseTransaction(transaction)
            .build();
    inverseTrans.setQuantityChange(transaction.getQuantityChange() * -1);
    if (transaction.getType().equals(TransferType.IN)) {
      inverseTrans.setResultingBalance(destBaseInventory.getClosingBalance());
      inverseTrans.setInventory(destBaseInventory);
    } else {
      inverseTrans.setResultingBalance(srcBaseInventory.getClosingBalance());
      inverseTrans.setInventory(srcBaseInventory);
    }
    inverseTrans.setDoneBy(loggedInUser);

    // set inverse transaction to current transaction
    transaction.setInverseTransaction(inverseTrans);

    inventoryTransactionRepository.save(transaction);
  }

  @Transactional
  public void createTransaction(InventoryTransactionDto transactionDto, String baseId) {
    if (transactionDto.getQuantityChange().equals(0)) {
      throw new InvalidRequestException("Quantity change can't be 0");
    }

    if (transactionDto.getTransactionType().equals(TransactionType.TRANSFER)) {
      TransferTransaction trans =
          inventoryTransactionMapper.toTransferTransactionEntity(
              (TransferTransactionDto) transactionDto);
      trans.setInventory(
          EquipmentInventory.builder()
              .equipment(equipmentMapper.toEntity(transactionDto.getEquipment()))
              .build());
      createTransferTransaction(trans);
      return;
    }

    // verify whether the user can create transaction for current base
    User loggedInUser = userService.getLoggedInUser();
    // if base id equals current then change it
    if (baseId.equals("current") && !loggedInUser.isAdmin()) {
      baseId = baseService.getUserAssignedBase(loggedInUser.getUsername()).id();
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
    equipment = inv.getEquipment();

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
        if (!loggedInUser.isAdmin()) {
          BaseDto userAssignedBbase = baseService.getUserAssignedBase(loggedInUser.getUsername());
          if (transferTransaction.getQuantityChange() < 0) {
            // transfer out
            transferTransaction.setSourceBase(baseMapper.toEntity(userAssignedBbase));
          } else {
            // transfer in
            transferTransaction.setDestBase(baseMapper.toEntity(userAssignedBbase));
          }
        }
        // can't move assets to same base
        if (transferTransaction.getDestBase().equals(transferTransaction.getSourceBase())) {
          throw new InvalidRequestException("Destination base can't be same as source base");
        }

        // can't move assets if source base doesn't have them either
        if (!hasEnoughInventory(base, equipment, transferTransaction.getQuantityChange())) {
          throw new InventoryNotEnoughException("Source base doesn't have enough inventory");
        }

        transferTransaction.setResultingBalance(currentBalance);
        transferTransaction.setDoneBy(loggedInUser);
        transferTransaction.setInventory(inv);
        transferTransaction.setInverseTransaction(createInverseTransaction(transferTransaction));
        inventoryTransactionRepository.save(transferTransaction);
        break;
      default:
        throw new IllegalArgumentException(
            "Transaction type not supported: " + transactionDto.getTransactionType());
    }

    // flow of application should not stop cuz i couldn't log
    try {
      auditService.saveAuditLog(
          AuditLog.builder()
              .action("CREATE")
              .transactionType(transactionDto.getTransactionType())
              .quantityChanged(transactionDto.getQuantityChange())
              .doneBy(loggedInUser.getFullName())
              .equipmentName(equipment.getName() == null ? equipment.getId() : equipment.getName())
              .build());

    } catch (Exception e) {
      log.warn("Error while saving audit log", e);
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

    if (transaction instanceof TransferTransaction) {
      // delete the other transaction too
      TransferTransaction ttrans = (TransferTransaction) transaction;
      var inverseTrans = ttrans.getInverseTransaction();
      inverseTrans
          .getInventory()
          .setClosingBalance(
              inverseTrans.getInventory().getClosingBalance() - inverseTrans.getQuantityChange());
      equipmentInventoryRepository.save(inverseTrans.getInventory());
    }

    // delete the transaction
    inventoryTransactionRepository.delete(transaction);

    User loggedInUser = userService.getLoggedInUser();
    auditService.saveAuditLog(
        AuditLog.builder()
            .action("CREATE")
            .transactionType(getTransactionType(transaction))
            .quantityChanged(transaction.getQuantityChange())
            .doneBy(loggedInUser.getFullName())
            .equipmentName(transaction.getInventory().getEquipment().getName())
            .build());
  }

  private TransactionType getTransactionType(InventoryTransaction trans) {
    if (trans instanceof AssignmentTransaction) {
      return TransactionType.ASSIGNMENT;
    } else if (trans instanceof ExpenditureTransaction) {
      return TransactionType.EXPENDITURE;
    } else if (trans instanceof PurchaseTransaction) {
      return TransactionType.PURCHASE;
    } else if (trans instanceof TransferTransaction) {
      return TransactionType.TRANSFER;
    } else {
      throw new IllegalArgumentException("Transaction type not supported");
    }
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

  private boolean hasEnoughInventory(Base base, Equipment equipment, int quantity) {
    return base.getEquipmentInventory().stream()
        .filter(inv -> inv.getEquipment().equals(equipment))
        .findFirst()
        .map(inv -> inv.getClosingBalance() >= Math.abs(quantity))
        .orElse(false);
  }
}
