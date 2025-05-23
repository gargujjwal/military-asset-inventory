package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assignment_transactions")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@DiscriminatorValue("ASSIGNMENT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentTransaction extends InventoryTransaction {

  @Column(name = "assigned_to", nullable = false)
  private String assignedTo;

  @Column(name = "quantity_assigned", nullable = false, updatable = false)
  private Integer quantityAssigned;

  @Column(name = "quantity_expended", nullable = false, updatable = false)
  private Integer quantityExpended;

  @Column(name = "notes")
  private String notes;
}
