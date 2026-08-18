package com.example.helpdesk.dto;

import com.example.helpdesk.model.enums.Estado;
import com.example.helpdesk.model.enums.Prioridad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private Estado estado;
    private LocalDateTime creadoEn;
    private LocalDateTime slaVenceEn;
    private String creadoPor;
    private boolean vencido;
}