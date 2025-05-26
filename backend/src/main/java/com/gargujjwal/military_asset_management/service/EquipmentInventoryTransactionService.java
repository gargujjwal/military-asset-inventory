package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.constants.Role;
import com.gargujjwal.military_asset_management.dto.BaseDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.dto.TransactionGroupedByBaseDto;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import com.gargujjwal.military_asset_management.entity.TransferTransaction;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.InvalidRequestException;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.filter.InventoryTransactionSpecification;
import com.gargujjwal.military_asset_management.mapper.BaseMapper;
import com.gargujjwal.military_asset_management.mapper.InventoryTransactionMapper;
import com.gargujjwal.military_asset_management.repository.BaseRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentInventoryTransactionRepository;
import com.gargujjwal.military_asset_management.service.transaction.TransactionCreateDeleteStrategy;
import com.gargujjwal.military_asset_management.service.transaction.TransactionCreateDeleteStrategyFactory;
import java.util.List;
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
  private final EquipmentInventoryTransactionRepository transRepo;
  private final BaseMapper baseMapper;
  private final InventoryTransactionMapper transMapper;
  private final BaseRepository baseRepository;
  private final UserService userService;
  private final BaseService baseService;
  private final TransactionCreateDeleteStrategyFactory transactionCreateDeleteStrategyFactory;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true)
  public List<TransactionGroupedByBaseDto> getAllTransactionsForAllBases() {
    // could have paginated the results here
    return convertToGroupedDto(transRepo.findAll());
  }

  @PreAuthorize("@baseService.canAccessBase(#id)")
  @Transactional(readOnly = true)
  public List<InventoryTransactionDto> getAllTransactionsForBase(String baseId) {
    return baseRepository
        .findById(baseId)
        .map(base -> transMapper.toDto(transRepo.findAllByInventory_Base(base)))
        .orElseThrow(() -> new ResourceNotFoundException("Base not found"));
  }

  @Transactional(readOnly = true)
  public List<TransactionGroupedByBaseDto> getFilteredTransactions(
      InventoryTransactionFilter filterDto) {
    List<InventoryTransaction> transactions =
        transRepo.findAll(InventoryTransactionSpecification.filterBy(filterDto));

    // if admin is logged in, return all transactions
    User loggedInUser = userService.getLoggedInUser();
    if (loggedInUser.getRole().equals(Role.ADMIN)) {
      return convertToGroupedDto(transactions);
    }

    // otherwise return only transactions of base he is assigned to
    BaseDto base = baseService.getUserAssignedBase(loggedInUser.getUsername());
    return convertToGroupedDto(
        transactions.stream()
            .filter(t -> t.getInventory().getBase().getId().equals(base.id()))
            .toList());
  }

  @Transactional
  public void createTransaction(InventoryTransactionDto transactionDto, String baseId) {
    TransactionCreateDeleteStrategy strategy = null;
    switch (transactionDto.getTransactionType()) {
      case TRANSFER:
        strategy =
            transactionCreateDeleteStrategyFactory.getStrategy(
                "transferTransactionCreateDeleteStrategy");
        break;
      case PURCHASE:
      case ASSIGNMENT:
      case EXPENDITURE:
        strategy =
            transactionCreateDeleteStrategyFactory.getStrategy(
                "assignmentPurchaseExpenditureTransactionCreateDeleteStrategy");
        break;
      default:
        throw new InvalidRequestException("Invalid transaction type");
    }
    strategy.createTransaction(transactionDto, baseId);
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteTransaction(String transactionId) {
    InventoryTransaction transaction =
        transRepo
            .findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    TransactionCreateDeleteStrategy strategy = null;
    if (transaction instanceof TransferTransaction) {
      strategy =
          transactionCreateDeleteStrategyFactory.getStrategy(
              "transferTransactionCreateDeleteStrategy");
    } else {
      strategy =
          transactionCreateDeleteStrategyFactory.getStrategy(
              "assignmentPurchaseExpenditureTransactionCreateDeleteStrategy");
    }

    strategy.deleteTransaction(transaction);
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
                    .transactions(transMapper.toDto(en.getValue()))
                    .build())
        .toList();
  }
}
