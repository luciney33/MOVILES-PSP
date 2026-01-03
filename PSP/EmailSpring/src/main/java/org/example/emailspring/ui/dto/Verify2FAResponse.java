package org.example.emailspring.ui.dto;

/**
 * Response para la verificación 2FA durante el login
 */
public record Verify2FAResponse(
    boolean success,
    String message,
    UsuarioResponseDTO usuario
) {}

