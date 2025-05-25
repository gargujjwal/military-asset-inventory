package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, String> {
  void deleteBySourceBaseOrDestBase(Base source, Base dest);
}
