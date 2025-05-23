package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, String> {}
