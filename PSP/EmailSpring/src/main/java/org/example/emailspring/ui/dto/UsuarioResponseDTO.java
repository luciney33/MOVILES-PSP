package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Rol;

@Schema(description = Constantes.SCHEMA_USUARIO_RESPONSE_DESC)
public record UsuarioResponseDTO(
        @Schema(description = Constantes.SCHEMA_ID_DESC, example = Constantes.SCHEMA_ID_EXAMPLE)
        Long id,
        @Schema(description = Constantes.SCHEMA_USERNAME_DESC, example = Constantes.SCHEMA_USERNAME_RESPONSE_EXAMPLE)
        String username,
        @Schema(description = Constantes.SCHEMA_EMAIL_DESC, example = Constantes.SCHEMA_EMAIL_RESPONSE_EXAMPLE)
        String email,
        @Schema(description = Constantes.SCHEMA_NOMBRE_RESPONSE_DESC, example = Constantes.SCHEMA_NOMBRE_RESPONSE_EXAMPLE)
        String nombre,

        String publicKey,
        @Schema(description = Constantes.SCHEMA_ROL_DESC, example = Constantes.SCHEMA_ROL_EXAMPLE)
        Rol rol) {
}
