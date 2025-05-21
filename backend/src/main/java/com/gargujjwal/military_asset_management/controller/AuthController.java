package com.gargujjwal.military_asset_management.controller;

import com.gargujjwal.military_asset_management.dto.AccessTokenResponse;
import com.gargujjwal.military_asset_management.dto.LoginRequest;
import com.gargujjwal.military_asset_management.dto.PasswordChangeReq;
import com.gargujjwal.military_asset_management.dto.UserDto;
import com.gargujjwal.military_asset_management.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    path = "/api/auth",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  AccessTokenResponse login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    return authService.login(loginRequest, response);
  }

  @GetMapping(path = "/logout", consumes = MediaType.ALL_VALUE)
  void logout(HttpServletResponse response) {
    authService.logout(response);
  }

  @GetMapping(path = "/refresh-access-token", consumes = MediaType.ALL_VALUE)
  AccessTokenResponse refreshSession(
      @CookieValue(value = "refresh-token", defaultValue = "invalid") String refreshToken,
      HttpServletResponse response) {
    return authService.refreshSession(refreshToken);
  }

  @GetMapping(path = "/me", consumes = MediaType.ALL_VALUE)
  public UserDto getAuthenticatedMe() {
    return authService.getCurrentUser();
  }

  @PatchMapping("/users/{username}/password")
  public void changePassword(
      @Valid @NotBlank @PathVariable String username,
      @Valid @RequestBody PasswordChangeReq passwordChangeReq) {
    authService.changePassword(username, passwordChangeReq);
  }

  @PostMapping("/users")
  public void createNewUser(@Valid @RequestBody UserDto newUser) {
    authService.createUser(newUser);
  }
}
