package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "equipment_inventories",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"base_id", "equipment_id"})})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentInventory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "opening_balance", nullable = false)
  private Integer openingBalance;

  @Column(name = "closing_balance", nullable = false)
  private Integer closingBalance;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "last_updated", nullable = false)
  private LocalDateTime lastUpdated;

  @ManyToOne(optional = false)
  @JoinColumn(name = "equipment_id", referencedColumnName = "id")
  private Equipment equipment;

  @ManyToOne(optional = false)
  @JoinColumn(name = "base_id", referencedColumnName = "id")
  private Base base;

  @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<InventoryTransaction> transactions = new HashSet<>();
}
