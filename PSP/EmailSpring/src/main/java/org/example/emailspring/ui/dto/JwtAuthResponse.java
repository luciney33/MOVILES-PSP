package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticación con tokens JWT")
public record JwtAuthResponse(
        @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Token de refresco JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tokenType,

        @Schema(description = "Datos del usuario autenticado")
        UsuarioResponseDTO usuario,

        @Schema(description = "Mensaje de respuesta", example = "Login exitoso")
        String message
) {
    public JwtAuthResponse(String accessToken, String refreshToken, UsuarioResponseDTO usuario, String message) {
        this(accessToken, refreshToken, "Bearer", usuario, message);
    }
}

