package com.example.hms.security;

import com.example.hms.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expire-seconds:1800}")
    private long accessTokenExpireSeconds;

    @Value("${app.jwt.refresh-token-expire-seconds:604800}")
    private long refreshTokenExpireSeconds;

    public String generateAccessToken(SysUser user) {
        return buildToken(user, "access", accessTokenExpireSeconds);
    }

    public String generateRefreshToken(SysUser user) {
        return buildToken(user, "refresh", refreshTokenExpireSeconds);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public int getAccessTokenExpireSeconds() {
        return (int) accessTokenExpireSeconds;
    }

    private String buildToken(SysUser user, String tokenType, long expireSeconds) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + (expireSeconds * 1000));

        return Jwts.builder()
                .subject(String.valueOf(user.getUserId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("realName", user.getRealName())
                .claim("type", tokenType)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
