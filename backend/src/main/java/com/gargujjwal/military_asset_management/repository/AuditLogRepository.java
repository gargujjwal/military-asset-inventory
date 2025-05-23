package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, String> {}
