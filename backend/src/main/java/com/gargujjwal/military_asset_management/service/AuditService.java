package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.entity.AuditLog;
import com.gargujjwal.military_asset_management.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditService {
  private final AuditLogRepository auditLogRepository;

  public void saveAuditLog(AuditLog auditLog) {
    try {
      auditLogRepository.save(auditLog);
    } catch (Exception e) {
      log.error("Failed to save audit log: {}", e.getMessage());
    }
  }
}
