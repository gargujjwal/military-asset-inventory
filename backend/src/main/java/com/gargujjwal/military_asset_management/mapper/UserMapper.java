package com.gargujjwal.military_asset_management.mapper;

import com.gargujjwal.military_asset_management.dto.UserDto;
import com.gargujjwal.military_asset_management.entity.User;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);

  List<UserDto> toDtoList(List<User> users);

  @InheritInverseConfiguration
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "authorities", ignore = true)
  @Mapping(target = "userBaseAssignments", ignore = true)
  User toEntity(UserDto dto);
}
