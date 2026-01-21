package org.example.emailspring.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

import java.time.LocalDateTime;


@Schema(description = Constantes.SCHEMA_USUARIO)
public record Usuario(
        @Schema(description = Constantes.SCHEMA_USUARIO_ID, example = Constantes.SCHEMA_USUARIO_ID_EXAMPLE)
        Long id,
        @Schema(description = Constantes.SCHEMA_USUARIO_USERNAME, example = Constantes.SCHEMA_USUARIO_USERNAME_EXAMPLE)
        String username,
        @Schema(description = Constantes.SCHEMA_USUARIO_PASSWORD, accessMode = Schema.AccessMode.WRITE_ONLY)
        String password,
        @Schema(description = Constantes.SCHEMA_USUARIO_EMAIL, example = Constantes.SCHEMA_USUARIO_EMAIL_EXAMPLE)
        String email,
        @Schema(description = Constantes.SCHEMA_USUARIO_NOMBRE, example = Constantes.SCHEMA_USUARIO_NOMBRE_EXAMPLE)
        String nombre,
        @Schema(description = Constantes.SCHEMA_USUARIO_ROL, example = Constantes.SCHEMA_USUARIO_ROL_EXAMPLE)
        Rol rol,
        @Schema(description = Constantes.SCHEMA_USUARIO_ACTIVO, example = Constantes.SCHEMA_USUARIO_ACTIVO_EXAMPLE)
        boolean activo,
        @Schema(description = Constantes.SCHEMA_USUARIO_CODIGO_ACTIVACION, accessMode = Schema.AccessMode.READ_ONLY)
        String codigoActivacion,
        @Schema(description = Constantes.SCHEMA_USUARIO_EXPIRACION_CODIGO, accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime expiracionCodigo,
        @Schema(description = Constantes.INDICA_SI_EL_USUARIO_TIENE_HABILITADA_LA_AUTENTICACION_DE_DOS_FACTORES)
        Boolean twoFactorEnabled,
        @Schema(description = Constantes.SECRETO_TOTP_PARA_AUTENTICACION_DE_DOS_FACTORES, accessMode = Schema.AccessMode.READ_ONLY)
        String twoFactorSecret
) {
    }
