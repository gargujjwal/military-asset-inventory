package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.Equipment;
import com.gargujjwal.military_asset_management.entity.EquipmentInventory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentInventoryRepository extends JpaRepository<EquipmentInventory, String> {
  Optional<EquipmentInventory> findByBaseAndEquipment(Base base, Equipment equipment);
}
