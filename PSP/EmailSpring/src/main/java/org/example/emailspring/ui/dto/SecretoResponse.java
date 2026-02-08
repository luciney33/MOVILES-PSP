package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;


@Schema(description = Constantes.SCHEMA_SECRETO_RESPONSE_DESC)
public record SecretoResponse(
        @Schema(description = Constantes.SCHEMA_SECRETO_ID_DESC)
        Long id,

        @Schema(description = Constantes.SCHEMA_SECRETO_CONTENIDO_DESC)
        String contenido,

        @Schema(description = Constantes.SCHEMA_SECRETO_AUTOR_NAME_DESC)
        String autorName,

        @Schema(description = Constantes.SCHEMA_SECRETO_FIRMA_DESC)
        String firma,

        @Schema(description = Constantes.SCHEMA_SECRETO_USERNAME_DESTINATARIO_DESC)
        String usernameDestinatario,

        @Schema(description = Constantes.SCHEMA_SECRETO_ES_COMPARTIDO_DESC)
        boolean esCompartido
) {

    public SecretoResponse(Long id, String contenidoPlano, String autorVerificado, boolean esCompartido) {
        this(id, contenidoPlano, autorVerificado, null, null, esCompartido);
    }
}

