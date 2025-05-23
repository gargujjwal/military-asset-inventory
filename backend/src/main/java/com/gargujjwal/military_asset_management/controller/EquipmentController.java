package com.gargujjwal.military_asset_management.controller;

import com.gargujjwal.military_asset_management.dto.EquipmentCategoryDetailDto;
import com.gargujjwal.military_asset_management.dto.EquipmentCategoryDto;
import com.gargujjwal.military_asset_management.dto.EquipmentDetailDto;
import com.gargujjwal.military_asset_management.dto.EquipmentDto;
import com.gargujjwal.military_asset_management.service.EquipmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/equipments",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EquipmentController {
  private final EquipmentService equipmentService;

  @GetMapping(path = "/categories", consumes = MediaType.ALL_VALUE)
  List<EquipmentCategoryDetailDto> getAllEquipmentCategories() {
    return equipmentService.getAllEquipmentCategories();
  }

  @GetMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
  EquipmentDetailDto getEquipmentDetailById(@Valid @NotBlank @PathVariable String id) {
    return equipmentService.getEquipmentDetailById(id);
  }

  @PostMapping("/categories")
  void createEquipmentCategory(@Valid @RequestBody EquipmentCategoryDto newEquipmentCategory) {
    equipmentService.createEquipmentCategory(newEquipmentCategory);
  }

  @PostMapping("/categories/{categoryId}")
  void createEquipment(
      @Valid @RequestBody EquipmentDto newEquipment,
      @Valid @NotBlank @PathVariable String categoryId) {
    equipmentService.createEquipment(newEquipment, categoryId);
  }

  @DeleteMapping(path = "/{id}", consumes = MediaType.ALL_VALUE)
  void deleteEquipment(@Valid @NotBlank @PathVariable String id) {
    equipmentService.deleteEquipment(id);
  }

  @DeleteMapping(path = "/categories/{categoryId}", consumes = MediaType.ALL_VALUE)
  void deleteEquipmentCategory(@Valid @NotBlank @PathVariable String categoryId) {
    equipmentService.deleteEquipmentCategory(categoryId);
  }
}
