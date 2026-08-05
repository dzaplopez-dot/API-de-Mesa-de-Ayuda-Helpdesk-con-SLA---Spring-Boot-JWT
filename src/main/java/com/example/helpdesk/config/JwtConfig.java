package com.example.helpdesk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private Long expiration;        // 900000 ms = 15 minutos
    private Long refreshExpiration; // 604800000 ms = 7 días
}