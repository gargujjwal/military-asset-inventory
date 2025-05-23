package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.dto.EquipmentCategoryDetailDto;
import com.gargujjwal.military_asset_management.dto.EquipmentCategoryDto;
import com.gargujjwal.military_asset_management.dto.EquipmentDetailDto;
import com.gargujjwal.military_asset_management.dto.EquipmentDto;
import com.gargujjwal.military_asset_management.entity.Equipment;
import com.gargujjwal.military_asset_management.entity.EquipmentCategory;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.mapper.EquipmentMapper;
import com.gargujjwal.military_asset_management.repository.EquipmentCategoryRepository;
import com.gargujjwal.military_asset_management.repository.EquipmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "EQUIPMENT_SERVICE")
@RequiredArgsConstructor
public class EquipmentService {
  private final EquipmentRepository equipmentRepository;
  private final EquipmentCategoryRepository equipmentCategoryRepository;
  private final EquipmentMapper equipmentMapper;

  public EquipmentDetailDto getEquipmentDetailById(String id) {
    return equipmentMapper.toDetailDto(findEquipmentById(id));
  }

  public List<EquipmentCategoryDetailDto> getAllEquipmentCategories() {
    return equipmentMapper.toDetailDto(equipmentCategoryRepository.findAll());
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Transactional
  public void createEquipmentCategory(EquipmentCategoryDto newEquipmentCategory) {
    EquipmentCategory equipmentCategory = equipmentMapper.toEntity(newEquipmentCategory);
    equipmentCategory.setId(null);
    equipmentCategoryRepository.save(equipmentCategory);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Transactional
  public void createEquipment(EquipmentDto newEquipment, String categoryId) {
    Equipment equipment = equipmentMapper.toEntity(newEquipment);
    equipment.setId(null);
    EquipmentCategory equipCategory = findCategoryById(categoryId);

    // for hibernate
    equipment.setEquipmentCategory(equipCategory);

    equipmentRepository.save(equipment);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Transactional
  public void deleteEquipmentCategory(String equipmentCategoryId) {
    findCategoryById(equipmentCategoryId);
    equipmentCategoryRepository.deleteById(equipmentCategoryId);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @Transactional
  public void deleteEquipment(String equipmentId) {
    findEquipmentById(equipmentId);
    equipmentRepository.deleteById(equipmentId);
  }

  private EquipmentCategory findCategoryById(String id) {
    return equipmentCategoryRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Equipment category not found"));
  }

  private Equipment findEquipmentById(String id) {
    return equipmentRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
  }
}
