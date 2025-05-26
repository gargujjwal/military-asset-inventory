package com.gargujjwal.military_asset_management.service;

import com.gargujjwal.military_asset_management.dto.AccessTokenResponse;
import com.gargujjwal.military_asset_management.dto.LoginRequest;
import com.gargujjwal.military_asset_management.dto.LoginResponse;
import com.gargujjwal.military_asset_management.dto.PasswordChangeReq;
import com.gargujjwal.military_asset_management.dto.SuccessResponse;
import com.gargujjwal.military_asset_management.dto.UserDto;
import com.gargujjwal.military_asset_management.entity.User;
import com.gargujjwal.military_asset_management.exception.ConflictingResourceException;
import com.gargujjwal.military_asset_management.exception.ResourceNotFoundException;
import com.gargujjwal.military_asset_management.exception.UnauthorizedException;
import com.gargujjwal.military_asset_management.mapper.UserMapper;
import com.gargujjwal.military_asset_management.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "AUTH_SERVICE")
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JWTService jwtService;
  private final UserMapper userMapper;

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

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
    ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
    response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    return new LoginResponse(accessToken, userMapper.toDto((User) user));
  }

  public SuccessResponse<Object> logout(HttpServletResponse response) {
    // get rid of the refresh token cookie
    ResponseCookie refreshTokenCookie = createRefreshTokenCookie("");
    // Set the cookie to expire immediately
    refreshTokenCookie = refreshTokenCookie.mutate().maxAge(0).build();
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    SecurityContextHolder.clearContext();
    return new SuccessResponse<>(null, LocalDateTime.now());
  }

  public AccessTokenResponse refreshSession(String refreshToken) {
    if (!jwtService.isValidToken(refreshToken)) {
      throw new BadCredentialsException("Invalid refresh token");
    }
    String username = jwtService.getUsernameFromToken(refreshToken);
    UserDetails user = userService.loadUserByUsername(username);
    if (user == null) {
      throw new ResourceNotFoundException("User not found");
    }
    return new AccessTokenResponse(jwtService.generateAccessToken(user));
  }

  public UserDto getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !(auth.getPrincipal() instanceof User)) {
      throw new UnauthorizedException("No Logged in user found");
    }

    return userMapper.toDto((User) auth.getPrincipal());
  }

  @Transactional
  @PreAuthorize("#username == authentication.principal.username")
  public void changePassword(String username, PasswordChangeReq passwordChangeReq) {
    User user = (User) userService.loadUserByUsername(username);
    // see if old password matches
    if (!passwordEncoder.matches(passwordChangeReq.oldPassword(), user.getPasswordHash())) {
      throw new BadCredentialsException("Old password does not match for user: " + username);
    }

    // update password
    user.setPasswordHash(passwordEncoder.encode(passwordChangeReq.newPassword()));
    userRepository.save(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void createUser(UserDto newUser) {
    User user = userMapper.toEntity(newUser);
    // check if there exists user with same username
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new ConflictingResourceException(
          "User already exists with username: " + user.getUsername());
    }

    // set the first password as thier username, later they can change it
    user.setPasswordHash(passwordEncoder.encode(user.getUsername()));
    user.setId(null);
    userRepository.save(user);
  }

  private ResponseCookie createRefreshTokenCookie(String refreshToken) {
    return ResponseCookie.from("refresh-token", refreshToken)
        .maxAge(JWTService.REFRESH_TOKEN_VALIDITY_IN_SECS)
        .path("/")
        .httpOnly(true)
        .secure("prod".equals(activeProfile))
        .sameSite("none")
        .build();
  }
}
