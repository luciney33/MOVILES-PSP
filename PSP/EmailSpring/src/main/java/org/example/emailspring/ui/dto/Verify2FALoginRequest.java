package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.SCHEMA_VERIFY_2FA_LOGIN_REQUEST_DESC)
public record Verify2FALoginRequest(
        @Schema(description = Constantes.SCHEMA_NOMBRE_USUARIO_DESC, example = Constantes.SCHEMA_ADMIN_EXAMPLE)
        String username,
        @Schema(description = Constantes.SCHEMA_CODIGO_6_DIGITOS_DESC, example = Constantes.SCHEMA_CODIGO_EXAMPLE)
        String codigo
) {
}

