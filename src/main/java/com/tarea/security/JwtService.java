package com.tarea.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {

  @Value("${jwt.secret}") private String jwtSecret;
  @Value("${jwt.expiration}") private long jwtExpiration;

  private Key key() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  /* ====== NUEVO: generar token con claims extra (authorities[]) ====== */
  public String generateToken(Map<String,Object> extraClaims, String subject) {
    return Jwts.builder()
        .setClaims(extraClaims == null ? new HashMap<>() : new HashMap<>(extraClaims))
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  /* Overload de conveniencia: userId + authorities[] */
  public String generateToken(Long userId, List<String> authorities) {
    Map<String,Object> claims = new HashMap<>();
    claims.put("authorities", authorities == null ? List.of() : authorities);
    return generateToken(claims, String.valueOf(userId));
  }

  /* ====== Legacy (si aún generas con 'role') ====== */
  @Deprecated
  public String generateToken(Long userId, String role) {
    Map<String,Object> claims = new HashMap<>();
    claims.put("role", role);
    return generateToken(claims, String.valueOf(userId));
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
  }

  public Long getUserIdFromToken(String token) {
    return Long.parseLong(extractAllClaims(token).getSubject());
  }

  /* Helpers opcionales */
  public List<String> getAuthoritiesFromToken(String token) {
    Object raw = extractAllClaims(token).get("authorities");
    if (raw instanceof List<?> list) {
      return list.stream().map(String::valueOf).toList();
    }
    return List.of();
  }

  public String getLegacyRoleFromToken(String token) {
    Object r = extractAllClaims(token).get("role");
    return r == null ? null : r.toString();
  }
}
