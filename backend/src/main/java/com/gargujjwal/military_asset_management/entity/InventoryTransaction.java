package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
public abstract class InventoryTransaction {

  @Id
  @PrimaryKeyJoinColumn
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  protected String id;

  @CreationTimestamp
  @Column(name = "transaction_date", nullable = false, updatable = false)
  protected LocalDateTime transactionDate;

  @Column(name = "quantity_change", nullable = false, updatable = false)
  protected Integer quantityChange;

  @Column(name = "resulting_balance", nullable = false, updatable = false)
  protected Integer resultingBalance;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "inventory_id", referencedColumnName = "id")
  protected EquipmentInventory inventory;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  protected User doneBy;
}
