package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.constants.Role;
import com.gargujjwal.military_asset_management.dto.BaseDto;
import com.gargujjwal.military_asset_management.entity.Base;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.entity.UserBaseAssignment;
import com.gargujjwal.military_asset_management.exception.ConflictingResourceException;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.mapper.BaseMapper;
import com.gargujjwal.military_asset_management.repository.BaseRepository;
import com.gargujjwal.military_asset_management.repository.TransferTransactionRepository;
import com.gargujjwal.military_asset_management.repository.UserBaseAssignmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "BASE_SERVICE")
@RequiredArgsConstructor
public class BaseService {
  private final UserBaseAssignmentRepository ubAssignmentRepository;
  private final BaseRepository baseRepository;
  private final BaseMapper baseMapper;
  private final UserService userService;
  private final TransferTransactionRepository transferTransactionRepository;

  public List<BaseDto> getAllBases() {
    return baseMapper.toDtoList(baseRepository.findAll());
  }

  @PreAuthorize("@baseService.canAccessBase(#id)")
  public BaseDto getBaseById(String id) {
    return baseMapper.toDto(findBaseById(id));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void createBase(BaseDto newBase) {
    Base base = baseMapper.toEntity(newBase);
    base.setId(null);
    baseRepository.save(base);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteBase(String id) {
    // make sure base exists
    Base base = findBaseById(id);
    baseRepository.deleteById(id);

    // delete any transfer transaction to maintain referential integrity
    transferTransactionRepository.deleteBySourceBaseOrDestBase(base, base);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void assignUserToBase(String baseId, String username) {
    Base base = findBaseById(baseId);
    User user = (User) userService.loadUserByUsername(username);

    // user should not be already assigned to this base already
    if (ubAssignmentRepository.existsByBaseAndUserAndIsActive(base, user, true)) {
      throw new ConflictingResourceException("User is already assigned to this base");
    }

    // unassign user base if user was already assigned to any base
    ubAssignmentRepository
        .findByUserAndIsActive(user, true)
        .ifPresent(
            (ass) -> {
              ass.setIsActive(false);
              ubAssignmentRepository.save(ass);
            });

    // unassign other user assignment of same role from this base
    ubAssignmentRepository
        .findByBaseAndIsActiveAndUser_Role(base, true, user.getRole())
        .ifPresent(
            (ass) -> {
              ass.setIsActive(false);
              ubAssignmentRepository.save(ass);
            });

    // create a new assignment for current user
    UserBaseAssignment newAssignment =
        UserBaseAssignment.builder().isActive(true).user(user).base(base).build();
    ubAssignmentRepository.save(newAssignment);
  }

  public BaseDto getLoggedInUserAssignedBase() {
    User loggedInUser = userService.getLoggedInUser();
    return getUserAssignedBase(loggedInUser.getUsername());
  }

  public BaseDto getUserAssignedBase(String username) {
    User user = (User) userService.loadUserByUsername(username);
    return baseMapper.toDto(
        ubAssignmentRepository
            .findByUserAndIsActive(user, true)
            .orElseThrow(() -> new ResourceNotFoundException("No based assigned to user"))
            .getBase());
  }

  public boolean canAccessBase(String baseId) {
    // a base can be accessed by admin or commanders who are current
    // incharge of the base or logistic officer if he is currently logistic officer
    // of the base
    User loggedInUser = userService.getLoggedInUser();
    if (loggedInUser.getRole().equals(Role.ADMIN)) {
      return true;
    }
    Base base = findBaseById(baseId);
    return ubAssignmentRepository.existsByBaseAndUserAndIsActive(base, loggedInUser, true);
  }

  private Base findBaseById(String id) {
    return baseRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No base found with id: " + id));
  }
}
