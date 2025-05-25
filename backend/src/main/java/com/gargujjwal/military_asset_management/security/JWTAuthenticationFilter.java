package com.gargujjwal.military_asset_management.security;

import com.gargujjwal.military_asset_management.service.JWTService;
import com.gargujjwal.military_asset_management.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT_AUTHENTICATION_FILTER")
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
  private final JWTService jwtService;
  private final UserService userService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);

      if (StringUtils.hasText(jwt) && jwtService.isValidToken(jwt)) {
        String username = jwtService.getUsernameFromToken(jwt);
        UserDetails userDetails = userService.loadUserByUsername(username);

        var authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User authenticated successfully, username: {}", userDetails.getUsername());
      }
    } catch (Exception ex) {
      log.error("Authentication failed, ex: {}", ex.getClass().getSimpleName());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    String requestTokenHeader = request.getHeader("Authorization");
    if (!requestTokenHeader.startsWith("Bearer")) {
      return null;
    }

    return requestTokenHeader.split("Bearer ")[1];
  }
}
