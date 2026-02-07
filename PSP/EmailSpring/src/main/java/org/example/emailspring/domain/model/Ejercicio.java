package org.example.emailspring.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo de ejercicio")
public record Ejercicio(
        @Schema(description = "ID del ejercicio", example = "1")
        Long id,
        @Schema(description = "Nombre del ejercicio", example = "Press de Banca")
        String nombre,
        @Schema(description = "Tipo de entrenamiento", example = "Pecho")
        String tipoEntrenamiento,
        @Schema(description = "URL de la imagen del ejercicio", example = "/images/ejercicios/PressBanca.gif")
        String imagenUrl,
        @Schema(description = "Descripción del ejercicio", example = "Ejercicio básico para pectoral")
        String descripcion
) {
}

