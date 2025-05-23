package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "equipment_categories")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EquipmentCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "unit_of_measure", nullable = false)
  private String unitOfMeasure;

  @OneToMany(mappedBy = "equipmentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
  Set<Equipment> equipments = new HashSet<>();
}
