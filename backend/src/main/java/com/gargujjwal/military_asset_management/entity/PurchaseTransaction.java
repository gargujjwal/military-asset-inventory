package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase_transactions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@DiscriminatorValue("PURCHASE")
public class PurchaseTransaction extends InventoryTransaction {

  @Column(name = "vendor_name", nullable = false)
  private String vendorName;

  @Column(name = "notes")
  private String notes;
}
