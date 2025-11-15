package org.example.springdemo.ui.dto;

public record LoginResponse(
        boolean success,
        String message,
        UsuarioDTO usuario
) {
}
