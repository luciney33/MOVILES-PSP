package org.example.emailspring.ui.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.SCHEMA_LOGIN_RESPONSE_DESC_COMPLETO)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        @Schema(description = Constantes.SCHEMA_LOGIN_SUCCESS_2FA_DESC)
        boolean success,

        @Schema(description = Constantes.SCHEMA_LOGIN_MENSAJE_DESC)
        String message,

        @Schema(description = Constantes.SCHEMA_LOGIN_REQUIRES_2FA_DESC, example = Constantes.SCHEMA_LOGIN_REQUIRES_2FA_EXAMPLE)
        Boolean requires2FA,

        @Schema(description = Constantes.SCHEMA_LOGIN_ACCESS_TOKEN_DESC)
        String accessToken,

        @Schema(description = Constantes.SCHEMA_LOGIN_REFRESH_TOKEN_DESC)
        String refreshToken,

        @Schema(description = Constantes.SCHEMA_LOGIN_TOKEN_TYPE_DESC, example = Constantes.SCHEMA_LOGIN_TOKEN_TYPE_EXAMPLE)
        String tokenType,

        @Schema(description = Constantes.SCHEMA_LOGIN_USUARIO_AUTH_DESC)
        UsuarioResponseDTO usuario
) {
    public LoginResponse(String accessToken, String refreshToken, UsuarioResponseDTO usuario, String message) {
        this(true, message, false, accessToken, refreshToken, Constantes.BEARER_TYPE, usuario);
    }

    public LoginResponse(String message, Boolean requires2FA) {
        this(false, message, requires2FA, null, null, null, null);
    }
}

