package com.example.helpdesk.security;

import com.example.helpdesk.config.JwtConfig;
import com.example.helpdesk.model.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = (Usuario) userDetails;

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .subject(usuario.getEmail())          // ← NUEVO: subject()
                .claim("rol", usuario.getRol().name())
                .claim("usuarioId", usuario.getId())
                .issuedAt(now)                        // ← NUEVO: issuedAt()
                .expiration(expiryDate)               // ← NUEVO: expiration()
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshExpiration());

        return Jwts.builder()
                .subject(usuario.getEmail())          // ← NUEVO: subject()
                .claim("usuarioId", usuario.getId())
                .claim("tipo", "REFRESH")
                .issuedAt(now)                        // ← NUEVO: issuedAt()
                .expiration(expiryDate)               // ← NUEVO: expiration()
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())      // ← NUEVO: verifyWith()
                    .build()
                    .parseSignedClaims(token);        // ← NUEVO: parseSignedClaims()
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())          // ← NUEVO: verifyWith()
                .build()
                .parseSignedClaims(token)             // ← NUEVO: parseSignedClaims()
                .getPayload();                        // ← NUEVO: getPayload()
        return claims.getSubject();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}