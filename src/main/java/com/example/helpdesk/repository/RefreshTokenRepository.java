package com.example.helpdesk.repository;

import com.example.helpdesk.model.entity.RefreshToken;
import com.example.helpdesk.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Buscar un Refresh Token por su valor (token string)
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Eliminar todos los Refresh Tokens de un usuario
     */
    void deleteByUsuario(Usuario usuario);

    /**
     * Buscar el Refresh Token de un usuario (útil para verificar si ya tiene uno)
     */
    Optional<RefreshToken> findByUsuario(Usuario usuario);
}
