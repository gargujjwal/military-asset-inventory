package com.gargujjwal.military_asset_management.controller;

import com.gargujjwal.military_asset_management.dto.DashboardDto;
import com.gargujjwal.military_asset_management.dto.InventoryTransactionFilter;
import com.gargujjwal.military_asset_management.service.DashboardService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/dashboard",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DashboardController {
  private final DashboardService dashboardServ;

  @GetMapping(consumes = MediaType.ALL_VALUE)
  List<DashboardDto> getInitalDashboardStats() {
    return dashboardServ.initialDashboard();
  }

  @GetMapping(path = "/filtered", consumes = MediaType.ALL_VALUE)
  List<DashboardDto> getFilteredDashboard(
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
          LocalDateTime startDate,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
          LocalDateTime endDate,
      @RequestParam(required = false) String baseId,
      @RequestParam(required = false) String equipmentCategoryId,
      @RequestParam(required = false) String equipmentId) {
    var filter =
        InventoryTransactionFilter.builder()
            .startDate(startDate)
            .endDate(endDate)
            .baseId(baseId)
            .equipmentCategoryId(equipmentCategoryId)
            .equipmentId(equipmentId)
            .build();
    return dashboardServ.filteredDashboard(filter);
  }
}
