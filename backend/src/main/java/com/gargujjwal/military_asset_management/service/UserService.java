package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.dto.UserDto;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.exception.UnauthorizedException;
import com.gargujjwal.military_asset_management.mapper.UserMapper;
import com.gargujjwal.military_asset_management.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new ResourceNotFoundException("User not found with username: " + username));
  }

  public User getById(String id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  public UserDto getUserByUsername(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new ResourceNotFoundException("User not found with username: " + username));
    return userMapper.toDto(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<UserDto> getAllUsers() {
    return userMapper.toDtoList(userRepository.findAll());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteUser(String username) {
    userRepository.deleteByUsername(username);
  }

  public User getLoggedInUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      throw new UnauthorizedException("No user logged in");
    }
    return (User) auth.getPrincipal();
  }
}
