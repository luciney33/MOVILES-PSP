package org.example.emailspring.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(description = "Detalles completos de un usuario del sistema.")
public record Usuario(
        @Schema(description = "Identificador único del usuario", example = "1")
        Long id,
        @Schema(description = "Nombre de usuario (login)", example = "jane_doe")
        String username,
        @Schema(description = "Contraseña (solo escritura, no se muestra en respuestas)", accessMode = Schema.AccessMode.WRITE_ONLY)
        String password,
        @Schema(description = "Correo electrónico del usuario", example = "jane@example.com")
        String email,
        @Schema(description = "Nombre completo del usuario", example = "Jane Doe")
        String nombre,
        @Schema(description = "Rol del usuario (ADMIN o USER)", example = "USER")
        Rol rol,
        @Schema(description = "Estado de activación de la cuenta", example = "true")
        boolean activo,
        // Ocultamos estos campos de la vista pública de la API si no son relevantes en la respuesta
        @Schema(description = "Código de activación (oculto en respuestas públicas)", accessMode = Schema.AccessMode.READ_ONLY)
        String codigoActivacion,
        @Schema(description = "Fecha/hora de expiración del código de activación", accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime expiracionCodigo
) {
}
