package org.example.emailspring.ui.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = "Respuesta del endpoint de login que puede ser exitoso o requerir 2FA")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        @Schema(description = "Indica si el login fue exitoso o se requiere 2FA")
        boolean success,

        @Schema(description = "Mensaje descriptivo del resultado")
        String message,

        @Schema(description = "Indica si se requiere verificación 2FA", example = "false")
        Boolean requires2FA,

        @Schema(description = "Token de acceso JWT (solo si login exitoso)")
        String accessToken,

        @Schema(description = "Token de refresco JWT (solo si login exitoso)")
        String refreshToken,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tokenType,

        @Schema(description = "Datos del usuario autenticado (solo si login exitoso)")
        UsuarioResponseDTO usuario
) {
    // Constructor para login exitoso con tokens
    public LoginResponse(String accessToken, String refreshToken, UsuarioResponseDTO usuario, String message) {
        this(true, message, false, accessToken, refreshToken, Constantes.BEARER_TYPE, usuario);
    }

    // Constructor para caso que requiere 2FA
    public LoginResponse(String message, Boolean requires2FA) {
        this(false, message, requires2FA, null, null, null, null);
    }
}

