package com.example.helpdesk.controller;

import com.example.helpdesk.dto.CambiarEstadoRequest;
import com.example.helpdesk.dto.TicketRequest;
import com.example.helpdesk.dto.TicketResponse;
import com.example.helpdesk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<?> crearTicket(@Valid @RequestBody TicketRequest request) {
        try {
            TicketResponse response = ticketService.crearTicket(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearError(e.getMessage()));
        }
    }

    @GetMapping("/mios")
    public ResponseEntity<List<TicketResponse>> listarMisTickets() {
        return ResponseEntity.ok(ticketService.listarMisTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTicket(@PathVariable Long id) {
        try {
            TicketResponse response = ticketService.obtenerTicket(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearError(e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SOPORTE', 'ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarTodos() {
        return ResponseEntity.ok(ticketService.listarTodos());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('SOPORTE', 'ADMIN')")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        try {
            TicketResponse response = ticketService.cambiarEstado(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearError(e.getMessage()));
        }
    }

    @GetMapping("/vencidos")
    @PreAuthorize("hasAnyRole('SOPORTE', 'ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarVencidos() {
        return ResponseEntity.ok(ticketService.listarVencidos());
    }

    private Map<String, String> crearError(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
