package com.gargujjwal.military_asset_management.filter;

import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.entity.EquipmentInventory;
import com.gargujjwal.military_asset_management.entity.InventoryTransaction;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class InventoryTransactionSpecification {

  public static Specification<InventoryTransaction> filterBy(InventoryTransactionFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Filter by startDate
      if (filter.startDate() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.startDate()));
      }

      // Filter by endDate
      if (filter.endDate() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.endDate()));
      }

      // Join inventory
      Join<InventoryTransaction, EquipmentInventory> inventoryJoin = root.join("inventory");

      // Filter by base
      if (filter.baseId() != null) {
        predicates.add(cb.equal(inventoryJoin.get("base").get("id"), filter.baseId()));
      }

      // Filter by equipment
      if (filter.equipmentId() != null) {
        predicates.add(cb.equal(inventoryJoin.get("equipment").get("id"), filter.equipmentId()));
      }

      // Filter by equipment category
      if (filter.equipmentCategoryId() != null) {
        predicates.add(
            cb.equal(
                inventoryJoin.get("equipment").get("equipmentCategory").get("id"),
                filter.equipmentCategoryId()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
