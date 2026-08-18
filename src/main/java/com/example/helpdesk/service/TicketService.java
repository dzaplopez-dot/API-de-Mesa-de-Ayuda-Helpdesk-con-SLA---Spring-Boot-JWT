package com.example.helpdesk.service;

import com.example.helpdesk.dto.CambiarEstadoRequest;
import com.example.helpdesk.dto.TicketRequest;
import com.example.helpdesk.dto.TicketResponse;
import com.example.helpdesk.model.entity.Ticket;
import com.example.helpdesk.model.entity.Usuario;
import com.example.helpdesk.model.enums.Estado;
import com.example.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    @Transactional
    public TicketResponse crearTicket(TicketRequest request) {
        Usuario creador = getUsuarioAutenticado();

        Ticket ticket = new Ticket();
        ticket.setTitulo(request.getTitulo());
        ticket.setDescripcion(request.getDescripcion());
        ticket.setPrioridad(request.getPrioridad());
        ticket.setCreadoPor(creador);

        Ticket ticketGuardado = ticketRepository.save(ticket);
        return convertirAResponse(ticketGuardado);
    }

    public List<TicketResponse> listarMisTickets() {
        Usuario usuario = getUsuarioAutenticado();
        return ticketRepository.findByCreadoPor(usuario)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public TicketResponse obtenerTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        Usuario usuario = getUsuarioAutenticado();

        boolean esDueño = ticket.getCreadoPor().getId().equals(usuario.getId());
        boolean esSoporte = usuario.getRol().name().equals("SOPORTE");
        boolean esAdmin = usuario.getRol().name().equals("ADMIN");

        if (!esDueño && !esSoporte && !esAdmin) {
            throw new RuntimeException("No tienes permiso para ver este ticket");
        }

        return convertirAResponse(ticket);
    }

    public List<TicketResponse> listarTodos() {
        return ticketRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketResponse cambiarEstado(Long id, CambiarEstadoRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        ticket.setEstado(request.getEstado());
        Ticket ticketActualizado = ticketRepository.save(ticket);

        return convertirAResponse(ticketActualizado);
    }

    public List<TicketResponse> listarVencidos() {
        LocalDateTime ahora = LocalDateTime.now();
        return ticketRepository.findByEstadoNotAndSlaVenceEnBefore(Estado.RESUELTO, ahora)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private TicketResponse convertirAResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitulo(ticket.getTitulo());
        response.setDescripcion(ticket.getDescripcion());
        response.setPrioridad(ticket.getPrioridad());
        response.setEstado(ticket.getEstado());
        response.setCreadoEn(ticket.getCreadoEn());
        response.setSlaVenceEn(ticket.getSlaVenceEn());
        response.setCreadoPor(ticket.getCreadoPor().getEmail());
        response.setVencido(ticket.isVencido());
        return response;
    }
}
