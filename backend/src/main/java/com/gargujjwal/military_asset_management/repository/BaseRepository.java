package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository extends JpaRepository<Base, String> {
  boolean existsById(String id);
}
