package com.example.helpdesk.dto;

import com.example.helpdesk.model.enums.Estado;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private Estado estado;
}
