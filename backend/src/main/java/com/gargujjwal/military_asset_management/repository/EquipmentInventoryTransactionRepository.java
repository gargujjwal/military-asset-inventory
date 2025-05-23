package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EquipmentInventoryTransactionRepository
    extends JpaRepository<InventoryTransaction, String>,
        JpaSpecificationExecutor<InventoryTransaction> {

  List<InventoryTransaction> findAllByInventory_Base(Base base);
}
