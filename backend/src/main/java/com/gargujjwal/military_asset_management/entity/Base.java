package com.gargujjwal.military_asset_management.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "bases")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Base {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  @EqualsAndHashCode.Include
  private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "location", nullable = false)
  private String location;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "base", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserBaseAssignment> userBaseAssignments = new HashSet<>();

  @OneToMany(mappedBy = "base", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<EquipmentInventory> equipmentInventory = new HashSet<>();
}
