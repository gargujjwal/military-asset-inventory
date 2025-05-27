package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private String id;

  @Column(name = "action", nullable = false, updatable = false)
  private String action;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false, updatable = false)
  private String transactionType;

  @Column(name = "quantity_changed", nullable = false, updatable = false)
  private Integer quantityChanged;

  @Column(name = "done_by", nullable = false, updatable = false)
  private String doneBy;

  @Column(name = "equipment_name", nullable = false, updatable = false)
  private String equipmentName;

  @CreationTimestamp
  @Column(name = "timestamp", nullable = false, updatable = false)
  private LocalDateTime timestamp;
}
