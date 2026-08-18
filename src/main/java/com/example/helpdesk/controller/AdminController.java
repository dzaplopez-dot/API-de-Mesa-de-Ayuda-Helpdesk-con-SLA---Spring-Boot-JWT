package com.example.helpdesk.controller;

import com.example.helpdesk.model.entity.Usuario;
import com.example.helpdesk.model.enums.Rol;
import com.example.helpdesk.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    /**
     * Ascender un usuario a SOPORTE
     * POST /api/admin/soporte?email=usuario@mail.com
     */
    @PostMapping("/soporte")
    public ResponseEntity<?> ascenderASoporte(@RequestParam String email) {
        // 1. Buscar usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar que no sea ya SOPORTE o ADMIN
        if (usuario.getRol() == Rol.SOPORTE) {
            throw new RuntimeException("El usuario ya es SOPORTE");
        }
        if (usuario.getRol() == Rol.ADMIN) {
            throw new RuntimeException("No se puede cambiar el rol de un ADMIN");
        }

        // 3. Ascender a SOPORTE
        usuario.setRol(Rol.SOPORTE);
        usuarioRepository.save(usuario);

        // 4. Respuesta
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario " + email + " ascendido a SOPORTE exitosamente");
        response.put("email", email);
        response.put("nuevoRol", "SOPORTE");

        return ResponseEntity.ok(response);
    }

    /**
     * Listar todos los usuarios (solo ADMIN)
     * GET /api/admin/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    /**
     * Obtener un usuario por email (solo ADMIN)
     * GET /api/admin/usuario?email=usuario@mail.com
     */
    @GetMapping("/usuario")
    public ResponseEntity<?> obtenerUsuario(@RequestParam String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }
}
