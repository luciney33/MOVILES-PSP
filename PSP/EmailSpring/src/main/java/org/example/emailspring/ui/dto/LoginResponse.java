package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.SCHEMA_LOGIN_RESPONSE_DESC)
public record LoginResponse(
        @Schema(description = Constantes.SCHEMA_LOGIN_SUCCESS_DESC, example = Constantes.SCHEMA_LOGIN_SUCCESS_EXAMPLE)
        boolean success,
        @Schema(description = Constantes.SCHEMA_LOGIN_MESSAGE_DESC, example = Constantes.SCHEMA_LOGIN_MESSAGE_EXAMPLE)
        String message,
        @Schema(description = Constantes.SCHEMA_LOGIN_USUARIO_DESC)
        UsuarioResponseDTO usuario
) {
    public LoginResponse(UsuarioResponseDTO usuario, String message) {
        this(true, message, usuario);
    }
}
