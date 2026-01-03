package org.example.emailspring.ui.dto;


public record Verify2FAResponse(
    boolean success,
    String message,
    UsuarioResponseDTO usuario
) {}

