package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, String> {}
