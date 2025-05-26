package com.gargujjwal.military_asset_management.service.transaction;

import com.gargujjwal.military_asset_management.dto.InventoryTransactionDto;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;

public interface TransactionCreateDeleteStrategy {

  void createTransaction(InventoryTransactionDto transactionDto, String baseId);

  void deleteTransaction(InventoryTransaction transaction);
}
