package com.example.helpdesk.service;

import com.example.helpdesk.dto.AuthRequest;
import com.example.helpdesk.dto.AuthResponse;
import com.example.helpdesk.dto.RegisterRequest;
import com.example.helpdesk.model.entity.RefreshToken;
import com.example.helpdesk.model.entity.Usuario;
import com.example.helpdesk.model.enums.Rol;
import com.example.helpdesk.repository.RefreshTokenRepository;
import com.example.helpdesk.repository.UsuarioRepository;
import com.example.helpdesk.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Usuario registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail().toLowerCase().trim());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(Rol.USUARIO);

        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUsuario(usuario);
        refreshTokenEntity.setExpiraEn(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setRevocado(false);
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResponse(accessToken, refreshToken, "Bearer", 900L);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token inválido"));

        if (tokenEntity.isRevocado()) {
            throw new RuntimeException("Refresh Token revocado");
        }

        if (tokenEntity.isExpirado()) {
            throw new RuntimeException("Refresh Token expirado");
        }

        Usuario usuario = tokenEntity.getUsuario();
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);

        return new AuthResponse(newAccessToken, refreshToken, "Bearer", 900L);
    }

    @Transactional
    public void logout(String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh Token no encontrado"));

        tokenEntity.setRevocado(true);
        refreshTokenRepository.save(tokenEntity);
    }
}
