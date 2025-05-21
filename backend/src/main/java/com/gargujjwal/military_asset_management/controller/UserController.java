package com.gargujjwal.military_asset_management.controller;

import com.gargujjwal.military_asset_management.dto.UserDto;
import com.gargujjwal.military_asset_management.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/users",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping
  public List<UserDto> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{username}")
  public UserDto getUserByUsername(@Valid @NotBlank @PathVariable String username) {
    return userService.getUserByUsername(username);
  }

  @DeleteMapping("/{username}")
  public void deleteUser(@Valid @NotBlank @PathVariable String username) {
    userService.deleteUser(username);
  }
}
