package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.dto.LoginRequest;
import com.gargujjwal.military_asset_management.dto.LoginResponse;
import com.gargujjwal.military_asset_management.dto.UserDto;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "AUTH_SERVICE")
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JWTService jwtService;
  private final UserMapper userMapper;

  public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
    log.info("Login request for user: {}", loginRequest.username());
    UserDetails user = userService.loadUserByUsername(loginRequest.username());
    if (user == null) throw new ResourceNotFoundException("User not found");

    if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }
    String accessToken = jwtService.generateAccessToken(user);

    // Set the refresh token in a cookie
    String refreshToken = jwtService.generateRefreshToken(user);
    Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
    response.addCookie(refreshTokenCookie);

    return new LoginResponse(accessToken);
  }

  public void logout(HttpServletResponse response) {
    // get rid of the refresh token cookie
    Cookie refreshTokenCookie = createRefreshTokenCookie("");
    refreshTokenCookie.setMaxAge(0); // Set the cookie to expire immediately
    response.addCookie(refreshTokenCookie);

    SecurityContextHolder.clearContext();
  }

  public String refreshSession(String refreshToken) {
    log.info("Refreshing session with refresh token: {}", refreshToken);
    if (!jwtService.isValidToken(refreshToken)) {
      throw new BadCredentialsException("Invalid refresh token");
    }
    String username = jwtService.getUsernameFromToken(refreshToken);
    UserDetails user = userService.loadUserByUsername(username);
    if (user == null) {
      throw new ResourceNotFoundException("User not found");
    }
    return jwtService.generateAccessToken(user);
  }

  public UserDto getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof User)) {
      throw new ResourceNotFoundException("User not found");
    }

    return userMapper.toDto((User) auth.getPrincipal());
  }

  private Cookie createRefreshTokenCookie(String refreshToken) {
    Cookie refreshTokenCookie = new Cookie("refresh-token", refreshToken);
    refreshTokenCookie.setMaxAge(JWTService.REFRESH_TOKEN_VALIDITY_IN_SECS);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    return refreshTokenCookie;
  }
}
