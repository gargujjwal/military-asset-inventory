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

      // Join inventory for all downstream filters
      Join<InventoryTransaction, EquipmentInventory> inventoryJoin = root.join("inventory");

      // Match all if startDate is null
      if (filter.startDate() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.startDate()));
      }

      // Match all if endDate is null
      if (filter.endDate() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.endDate()));
      }

      // Match all bases if baseId is null
      if (filter.baseId() != null) {
        predicates.add(cb.equal(inventoryJoin.get("base").get("id"), filter.baseId()));
      }

      // Match all equipment if equipmentId is null
      if (filter.equipmentId() != null) {
        predicates.add(cb.equal(inventoryJoin.get("equipment").get("id"), filter.equipmentId()));
      }

      // Match all categories if equipmentCategoryId is null
      if (filter.equipmentCategoryId() != null) {
        predicates.add(
            cb.equal(
                inventoryJoin.get("equipment").get("equipmentCategory").get("id"),
                filter.equipmentCategoryId()));
      }

      // sort them by transaction date in descending order
      query.orderBy(cb.desc(root.get("transactionDate")));

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
