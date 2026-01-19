package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para verificar código 2FA durante el login")
public record Verify2FALoginRequest(
        @Schema(description = "Nombre de usuario", example = "admin")
        String username,
        @Schema(description = "Código de 6 dígitos recibido por email", example = "123456")
        String codigo
) {
}

