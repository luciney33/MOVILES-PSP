package org.example.emailspring.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.common.Constantes;

@Schema(description = Constantes.SCHEMA_EJERCICIO_DESC)
public record Ejercicio(
        @Schema(description = Constantes.SCHEMA_EJERCICIO_ID_DESC, example = Constantes.SCHEMA_EJERCICIO_ID_EXAMPLE)
        Long id,
        @Schema(description = Constantes.SCHEMA_EJERCICIO_NOMBRE_DESC, example = Constantes.SCHEMA_EJERCICIO_NOMBRE_EXAMPLE)
        String nombre,
        @Schema(description = Constantes.SCHEMA_EJERCICIO_TIPO_DESC, example = Constantes.SCHEMA_EJERCICIO_TIPO_EXAMPLE)
        String tipoEntrenamiento,
        @Schema(description = Constantes.SCHEMA_EJERCICIO_IMAGEN_URL_DESC, example = Constantes.SCHEMA_EJERCICIO_IMAGEN_URL_EXAMPLE)
        String imagenUrl,
        @Schema(description = Constantes.SCHEMA_EJERCICIO_DESCRIPCION_DESC, example = Constantes.SCHEMA_EJERCICIO_DESCRIPCION_EXAMPLE)
        String descripcion
) {
}

