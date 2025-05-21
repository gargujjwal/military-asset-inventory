package com.gargujjwal.military_asset_management.mapper;

import com.gargujjwal.military_asset_management.dto.BaseDto;
import com.gargujjwal.military_asset_management.entity.Base;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BaseMapper {

  BaseDto toDto(Base base);

  List<BaseDto> toDtoList(List<Base> bases);

  @Mapping(target = "userBaseAssignments", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Base toEntity(BaseDto dto);
}
