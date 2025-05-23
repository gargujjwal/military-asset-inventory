package com.gargujjwal.military_asset_management.entity;

import com.gargujjwal.military_asset_management.constants.TransferType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transfer_transactions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@DiscriminatorValue("TRANSFER")
public class TransferTransaction extends InventoryTransaction {

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "source_base_id", referencedColumnName = "id")
  private Base sourceBase;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "dest_base_id", referencedColumnName = "id")
  private Base destBase;

  @Transient private TransferType type;

  @Column(name = "notes")
  private String notes;

  @PostLoad
  private void setTransferType() {
    if (this.quantityChange > 0) this.type = TransferType.IN;
    else this.type = TransferType.OUT;
  }
}
