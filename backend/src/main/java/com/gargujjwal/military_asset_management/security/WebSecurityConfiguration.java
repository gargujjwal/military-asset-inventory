package com.gargujjwal.military_asset_management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargujjwal.military_asset_management.dto.ErrorResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {

  private final JWTAuthenticationFilter jwtAuthenticationFilter;
  private final ObjectMapper mapper;

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

  @Value("${app.cors.allowed-origins:}")
  private List<String> allowedOrigins;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    // disable cors
    httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
    // disable csrf attack
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    // disable sessions
    httpSecurity.sessionManagement(
        config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    // allowlist certain urls
    httpSecurity.authorizeHttpRequests(
        config ->
            config
                .requestMatchers("/api/auth/**", "/error", "/swagger-ui/**", "/v3/api-docs/**")
                .permitAll()
                .anyRequest()
                .authenticated());
    httpSecurity.exceptionHandling(
        exception ->
            exception
                // Return 401 Unauthorized for unauthenticated users
                .authenticationEntryPoint(
                (request, response, authException) -> {
                  response.setStatus(HttpStatus.UNAUTHORIZED.value());
                  response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                  mapper.writeValue(
                      response.getWriter(), new ErrorResponse("Unauthorized access to resource"));
                }));
    httpSecurity.addFilterBefore(
        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    if ("dev".equals(activeProfile)) {
      // Development configuration - more permissive
      configuration.setAllowedOriginPatterns(
          Arrays.asList("http://localhost:*", "http://127.0.0.1:*", "https://localhost:*"));
      configuration.setAllowedMethods(Arrays.asList("*"));
      configuration.setAllowedHeaders(Arrays.asList("*"));
    } else {
      // Production configuration - restrictive
      configuration.setAllowedOrigins(allowedOrigins);
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
      configuration.setAllowedHeaders(
          Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
    }

    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
