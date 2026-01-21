package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.RESPONSE_CUANDO_SE_REQUIERE_VERIFICACION_2_FA)
public record Login2FARequiredResponse(
        @Schema(description = Constantes.INDICA_QUE_SE_REQUIERE_2_FA, example = Constantes.TRUE)
        boolean requires2FA,
        @Schema(description = Constantes.MENSAJE_INFORMATIVO, example = Constantes.ELECTRONICO)
        String message
) {
    }


