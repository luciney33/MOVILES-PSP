package org.example.emailspring.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

import java.util.List;

@Schema(description = Constantes.SCHEMA_ENTRENAMIENTO)
public record Entrenamiento(
        @Schema(description = Constantes.SCHEMA_ENTRENAMIENTO_ID, example = Constantes.SCHEMA_ENTRENAMIENTO_ID_EXAMPLE)
        Long id,
        @Schema(description = Constantes.SCHEMA_ENTRENAMIENTO_USUARIO_ID, example = Constantes.SCHEMA_ENTRENAMIENTO_USUARIO_ID_EXAMPLE)
        Long usuarioId,
        @Schema(description = Constantes.SCHEMA_ENTRENAMIENTO_NOMBRE, example = Constantes.SCHEMA_ENTRENAMIENTO_NOMBRE_EXAMPLE)
        String nombre,
        @Schema(description = Constantes.SCHEMA_ENTRENAMIENTO_DESCRIPCION, example = Constantes.SCHEMA_ENTRENAMIENTO_DESCRIPCION_EXAMPLE)
        String descripcion,
        @Schema(description = "Lista de ejercicios del entrenamiento")
        List<Ejercicio> ejercicios) {
}
