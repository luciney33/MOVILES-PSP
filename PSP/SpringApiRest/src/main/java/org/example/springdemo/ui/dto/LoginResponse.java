package org.example.springdemo.ui.dto;

public record LoginResponse(
        boolean success,
        String message,
        UsuarioDTO usuario
) {
    public LoginResponse(UsuarioDTO usuario, String message) {
        this(true, message, usuario);
    }

    public LoginResponse(String message) {
        this(false, message, null);
    }
}
