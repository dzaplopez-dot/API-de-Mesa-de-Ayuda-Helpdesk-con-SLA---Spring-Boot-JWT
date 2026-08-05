package com.example.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato v?lido")
    private String email;
    
    @NotBlank(message = "La contrase?a es obligatoria")
    @Size(min = 6, message = "La contrase?a debe tener al menos 6 caracteres")
    private String password;
}
