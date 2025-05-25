package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.constants.TransactionType;
import com.gargujjwal.military_asset_management.dto.DashboardDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.dto.TransactionGroupedByBaseDto;
import com.gargujjwal.military_asset_management.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "DASHBOARD_SERVICE")
@RequiredArgsConstructor
public class DashboardService {
  private final UserService userService;
  private final EquipmentInventoryTransactionService transactionService;
  private final BaseService baseService;

  public List<DashboardDto> initialDashboard() {
    User loggedInUser = userService.getLoggedInUser();
    InventoryTransactionFilter filter = null;
    if (loggedInUser.isAdmin()) {
      filter = InventoryTransactionFilter.builder().build();
    } else {
      filter =
          InventoryTransactionFilter.builder()
              .baseId(baseService.getUserAssignedBase(loggedInUser.getUsername()).id())
              .build();
    }

    List<TransactionGroupedByBaseDto> transactions =
        transactionService.getFilteredTransactions(filter);
    return transactions.stream().map(this::buildDashboardDto).toList();
  }

  public List<DashboardDto> filteredDashboard(InventoryTransactionFilter filter) {
    User loggedInUser = userService.getLoggedInUser();
    if (!loggedInUser.isAdmin()) {
      filter =
          InventoryTransactionFilter.builder()
              .startDate(filter.startDate())
              .endDate(filter.endDate())
              .baseId(baseService.getUserAssignedBase(loggedInUser.getUsername()).id())
              .equipmentCategoryId(filter.equipmentCategoryId())
              .equipmentId(filter.equipmentId())
              .build();
    }

    List<TransactionGroupedByBaseDto> transactions =
        transactionService.getFilteredTransactions(filter);
    return transactions.stream().map(this::buildDashboardDto).toList();
  }

  private DashboardDto buildDashboardDto(TransactionGroupedByBaseDto transactions) {
    // transactions are ordered by transaction date in descending order
    InventoryTransactionDto lastTransaction = transactions.getTransactions().getLast();
    int openingBalance =
        lastTransaction.getResultingBalance() - lastTransaction.getQuantityChange();
    InventoryTransactionDto firstTransaction = transactions.getTransactions().getFirst();
    int closingBalance = firstTransaction.getResultingBalance();
    int purchases =
        transactions.getTransactions().stream()
            .filter(t -> t.getTransactionType().equals(TransactionType.PURCHASE))
            .mapToInt(t -> t.getQuantityChange())
            .sum();
    int transferIn =
        transactions.getTransactions().stream()
            .filter(
                t ->
                    t.getTransactionType().equals(TransactionType.TRANSFER)
                        && t.getQuantityChange() > 0)
            .mapToInt(t -> t.getQuantityChange())
            .sum();
    int transferOut =
        transactions.getTransactions().stream()
            .filter(
                t ->
                    t.getTransactionType().equals(TransactionType.TRANSFER)
                        && t.getQuantityChange() < 0)
            .mapToInt(t -> -t.getQuantityChange())
            .sum();

    return DashboardDto.builder()
        .openingBalance(openingBalance)
        .closingBalance(closingBalance)
        .purchases(purchases)
        .transferIn(transferIn)
        .transferOut(transferOut)
        .base(transactions.getBase())
        .transactions(transactions.getTransactions())
        .build();
  }
}
