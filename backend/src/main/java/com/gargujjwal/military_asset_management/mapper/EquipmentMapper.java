package com.gargujjwal.military_asset_management.mapper;

import com.gargujjwal.military_asset_management.dto.EquipmentCategoryDetailDto;
import com.gargujjwal.military_asset_management.dto.EquipmentCategoryDto;
import com.gargujjwal.military_asset_management.dto.EquipmentDetailDto;
import com.gargujjwal.military_asset_management.dto.EquipmentDto;
import com.gargujjwal.military_asset_management.entity.Equipment;
import com.gargujjwal.military_asset_management.entity.EquipmentCategory;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

  EquipmentCategoryDto toDto(EquipmentCategory entity);

  @Mapping(target = "equipments", ignore = true)
  EquipmentCategory toEntity(EquipmentCategoryDto dto);

  EquipmentCategoryDetailDto toDetailDto(EquipmentCategory entity);

  List<EquipmentCategoryDetailDto> toDetailDto(List<EquipmentCategory> entityList);

  EquipmentDto toDto(Equipment entity);

  @Mapping(target = "equipmentInventories", ignore = true)
  @Mapping(target = "equipmentCategory", ignore = true)
  Equipment toEntity(EquipmentDto dto);

  @Mapping(source = "equipmentCategory", target = "equipmentCategory")
  EquipmentDetailDto toDetailDto(Equipment equipment);
}
