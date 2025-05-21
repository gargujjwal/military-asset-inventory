package com.gargujjwal.military_asset_management.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gargujjwal.military_asset_management.exception.InvalidTokenException;
import com.gargujjwal.military_asset_management.exception.TokenGenerationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "JWT_SERVICE")
public class JWTService {
    public static final int ACCESS_TOKEN_VALIDITY_IN_SECS = 60 * 5; // 5 mins
    public static final int REFRESH_TOKEN_VALIDITY_IN_SECS = 60 * 60 * 24 * 30 * 6; // 6 months
    @Value("${my.jwt.secret.key}")
    private String jwtSecret;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails user) throws TokenGenerationException {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_IN_SECS * 1000L);

            String token = Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSecretKey())
                    .compact();

            log.info("Generated access token for user: {}", user.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Failed to generate access token for user: {}, error: {}", user.getUsername(),
                    e.getMessage());
            throw new TokenGenerationException("Could not generate access token", e);
        }
    }

    public String generateRefreshToken(UserDetails user) throws TokenGenerationException {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_IN_SECS * 1000L);

            String token = Jwts.builder()
                    .subject(user.getUsername())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .signWith(getSecretKey())
                    .compact();

            log.info("Generated refresh token for user: {}", user.getUsername());
            return token;
        } catch (Exception e) {
            log.error("Failed to generate refresh token for user: {}, error: {}", user.getUsername(),
                    e.getMessage());
            throw new TokenGenerationException("Could not generate refresh token", e);
        }
    }

    public String getUsernameFromToken(String token) throws InvalidTokenException {
        try {
            Claims claims = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token)
                    .getPayload();
            String email = claims.getSubject();

            log.debug("Extracted email from token: {}", email);
            return email;
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}, error: {}", token, e.getMessage());
            throw new InvalidTokenException("Could not extract email from token", e);
        }
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            log.debug("Token validated successfully: {}", token);
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", token);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", token);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token: {}", token);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", token);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", token);
        }
        return false;
    }

}
