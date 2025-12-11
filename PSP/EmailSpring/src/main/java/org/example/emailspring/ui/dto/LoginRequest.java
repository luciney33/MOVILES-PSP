package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Petición de credenciales para iniciar sesión.")
public record LoginRequest(
        @Schema(description = "Nombre de usuario o dirección de correo electrónico.", example = "juan_perez")
        String username,
        @Schema(description = "Contraseña del usuario.", example = "unaContraseñaSegura123")
        String password
) {
}
