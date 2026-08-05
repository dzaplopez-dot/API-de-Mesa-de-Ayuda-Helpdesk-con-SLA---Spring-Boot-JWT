package com.example.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato v?lido")
    private String email;
    
    @NotBlank(message = "La contrase?a es obligatoria")
    private String password;
}
