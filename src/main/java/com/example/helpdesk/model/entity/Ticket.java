package com.example.helpdesk.model.entity;

import com.example.helpdesk.model.enums.Estado;
import com.example.helpdesk.model.enums.Prioridad;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private LocalDateTime creadoEn;

    private LocalDateTime slaVenceEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.estado = Estado.ABIERTO;
        calcularSLA();
    }

    private void calcularSLA() {
        if (this.prioridad != null && this.creadoEn != null) {
            switch (this.prioridad) {
                case ALTA -> this.slaVenceEn = this.creadoEn.plusHours(4);
                case MEDIA -> this.slaVenceEn = this.creadoEn.plusHours(24);
                case BAJA -> this.slaVenceEn = this.creadoEn.plusHours(72);
                default -> this.slaVenceEn = this.creadoEn.plusHours(72);
            }
        }
    }

    public boolean isVencido() {
        if (this.estado == Estado.RESUELTO) {
            return false;
        }
        if (this.slaVenceEn == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.slaVenceEn);
    }
}