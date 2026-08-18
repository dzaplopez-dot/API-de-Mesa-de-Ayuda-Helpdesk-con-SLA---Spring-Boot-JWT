package com.example.helpdesk.repository;

import com.example.helpdesk.model.entity.Ticket;
import com.example.helpdesk.model.entity.Usuario;
import com.example.helpdesk.model.enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Buscar tickets por usuario creador
     * Usado en: GET /api/tickets/mios
     */
    List<Ticket> findByCreadoPor(Usuario usuario);

    /**
     * Buscar tickets vencidos (no resueltos y con SLA vencido)
     * Usado en: GET /api/tickets/vencidos
     */
    List<Ticket> findByEstadoNotAndSlaVenceEnBefore(Estado estado, LocalDateTime fecha);
}
