package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.Equipment;
import com.gargujjwal.military_asset_management.entity.TransferTransaction;
import com.gargujjwal.military_asset_management.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, String> {
  void deleteBySourceBaseOrDestBase(Base source, Base dest);

  Optional<TransferTransaction>
      findBySourceBaseAndDestBaseAndQuanityChangeAndInventory_EquipmentAndInventory_Base(
          Base src, Base dest, int quantityChanged, User doneBy, Equipment equipment, Base base);
}
