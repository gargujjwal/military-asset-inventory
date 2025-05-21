package com.gargujjwal.military_asset_management.repository;

import com.gargujjwal.military_asset_management.constants.Role;
import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.entity.UserBaseAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBaseAssignmentRepository extends JpaRepository<UserBaseAssignment, String> {
  List<UserBaseAssignment> findAllByBaseAndUser(Base base, User user);

  // will tell if base is currently assigned to this user
  boolean existsByBaseAndUserAndIsActive(Base base, User user, boolean isActive);

  Optional<UserBaseAssignment> findByUserAndIsActive(User user, boolean isActive);

  Optional<UserBaseAssignment> findByBaseAndIsActiveAndUser_Role(
      Base base, boolean isActive, Role role);

  List<UserBaseAssignment> findAllByBaseAndIsActive(Base base, boolean isActive);
}
