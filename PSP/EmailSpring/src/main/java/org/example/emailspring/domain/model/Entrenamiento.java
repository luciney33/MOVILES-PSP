package org.example.emailspring.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa una rutina de entrenamiento asignada a un usuario.")
public record Entrenamiento(
        @Schema(description = "Identificador único del entrenamiento", example = "1")
        Long id,
        @Schema(description = "ID del usuario propietario del entrenamiento", example = "101")
        Integer usuarioId,
        @Schema(description = "Nombre del entrenamiento", example = "Rutina de Piernas Avanzada")
        String nombre,
        @Schema(description = "Descripción detallada de la rutina y ejercicios", example = "Incluye sentadillas, peso muerto y zancadas.")
        String descripcion) {
}
