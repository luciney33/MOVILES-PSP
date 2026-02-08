package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Respuesta con información de un secreto descifrado")
public record SecretoResponse(
        @Schema(description = "ID del secreto")
        Long id,

        @Schema(description = "Contenido descifrado del secreto")
        String contenido,

        @Schema(description = "Nombre del autor del secreto")
        String autorName,

        @Schema(description = "Firma del autor")
        String firma,

        @Schema(description = "Username del usuario con quien compartir")
        String usernameDestinatario,

        @Schema(description = "Indica si el secreto está compartido conmigo")
        boolean esCompartido
) {
    public SecretoResponse(String contenido, String autorName, String usernameDestinatario) {
        this(null, contenido, autorName, null ,usernameDestinatario, false);
    }

}

