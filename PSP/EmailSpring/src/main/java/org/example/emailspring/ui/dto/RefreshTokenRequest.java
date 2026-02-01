package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.REQUEST_PARA_REFRESCAR_EL_TOKEN_DE_ACCESO)
public record RefreshTokenRequest(
        @Schema(description = Constantes.REFRESH_TOKEN, example = Constantes.EY_JHB_GCI_OI_JIUZ_I_1_NI_IS_IN_R_5_C_CI_6_IKP_XVCJ_9)
        String refreshToken,

        @Schema(description = "Access token anterior a revocar", example = "eyJhbGciOiJIUzUxMiJ9...")
        String accessToken
) {
}

