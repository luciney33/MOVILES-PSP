package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.SCHEMA_LOGIN_REQUEST_DESC)
public record LoginRequest(
        @Schema(description = Constantes.SCHEMA_LOGIN_USERNAME_DESC, example = Constantes.SCHEMA_LOGIN_USERNAME_EXAMPLE)
        String username,
        @Schema(description = Constantes.SCHEMA_LOGIN_PASSWORD_DESC, example = Constantes.SCHEMA_LOGIN_PASSWORD_EXAMPLE)
        String password
) {
}
