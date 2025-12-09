package org.example.emailspring.ui.dto;

public record LoginResponse(
        boolean success,
        String message,
        UsuarioResponseDTO usuario
) {
    public LoginResponse(UsuarioResponseDTO usuario, String message) {
        this(true, message, usuario);
    }
}
