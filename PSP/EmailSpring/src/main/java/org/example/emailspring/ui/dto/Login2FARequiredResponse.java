package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response cuando se requiere verificación 2FA")
public record Login2FARequiredResponse(
        @Schema(description = "Indica que se requiere 2FA", example = "true")
        boolean requires2FA,
        @Schema(description = "Mensaje informativo", example = "Se ha enviado un código de verificación a tu correo electrónico")
        String message
) {
}


