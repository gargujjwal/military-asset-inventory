package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Equipment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<EquipmentInventory> equipmentInventories = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "equipment_category_id", referencedColumnName = "id")
  EquipmentCategory equipmentCategory;
}
