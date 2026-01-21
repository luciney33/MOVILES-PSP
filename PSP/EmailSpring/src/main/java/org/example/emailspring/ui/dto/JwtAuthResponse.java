package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.RESPUESTA_DE_AUTENTICACION_CON_TOKENS_JWT)
public record JwtAuthResponse(
        @Schema(description = Constantes.TOKEN_DE_ACCESO_JWT, example = Constantes.EY_JHB_GCI_OI_JIUZ_I_1_NI_IS_IN_R_5_C_CI_6_IKP_XVCJ_9)
        String accessToken,

        @Schema(description = Constantes.TOKEN_DE_REFRESCO_JWT, example = Constantes.EY_JHB_GCI_OI_JIUZ_I_1_NI_IS_IN_R_5_C_CI_6_IKP_XVCJ_9)
        String refreshToken,

        @Schema(description = Constantes.TIPO_DE_TOKEN, example = Constantes.BEARER)
        String tokenType,

        @Schema(description = Constantes.DATOS_DEL_USUARIO_AUTENTICADO)
        UsuarioResponseDTO usuario,

        @Schema(description = Constantes.MENSAJE_DE_RESPUESTA, example = Constantes.MSG_LOGIN_SUCCESS)
        String message
) {

    public JwtAuthResponse(String accessToken, String refreshToken, UsuarioResponseDTO usuario, String message) {
        this(accessToken, refreshToken, Constantes.BEARER_TYPE, usuario, message);
    }
}

