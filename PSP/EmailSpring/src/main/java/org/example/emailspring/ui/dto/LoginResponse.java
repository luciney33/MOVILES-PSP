package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta devuelta tras un intento de login exitoso.")
public record LoginResponse(
        @Schema(description = "Indica si la operación fue exitosa (siempre true en esta respuesta).", example = "true")
        boolean success,
        @Schema(description = "Mensaje descriptivo del resultado.", example = "Login exitoso")
        String message,
        @Schema(description = "Detalles del usuario autenticado.")
        UsuarioResponseDTO usuario
) {
    public LoginResponse(UsuarioResponseDTO usuario, String message) {
        this(true, message, usuario);
    }
}
