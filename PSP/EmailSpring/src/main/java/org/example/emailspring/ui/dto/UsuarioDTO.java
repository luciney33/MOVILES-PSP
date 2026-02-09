package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Rol;

@Schema(description = Constantes.SCHEMA_USUARIO_DTO_DESC)
public record UsuarioDTO(
        @Schema(description = Constantes.SCHEMA_USERNAME_DESC, example = Constantes.SCHEMA_USERNAME_EXAMPLE)
        String username,
        @Schema(description = Constantes.SCHEMA_PASSWORD_DESC, example = Constantes.SCHEMA_PASSWORD_EXAMPLE)
        String password,
        @Schema(description = Constantes.SCHEMA_EMAIL_DESC, example = Constantes.SCHEMA_EMAIL_EXAMPLE)
        String email,
        @Schema(description = Constantes.SCHEMA_NOMBRE_DESC, example = Constantes.SCHEMA_NOMBRE_EXAMPLE)
        String nombre,
        @Schema(description = Constantes.ROL_ASIGNADO_AL_USUARIO_OPCIONAL_SI_EL_REGISTRO_PERMITE_ELEGIR_ROL, example = Constantes.USER)
        Rol rol
) {
    }
