package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expenditure_transactions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@DiscriminatorValue("EXPENDITURE")
public class ExpenditureTransaction extends InventoryTransaction {

  @Column(name = "reason", nullable = false)
  private String reason;

  @Column(name = "description")
  private String description;
}
