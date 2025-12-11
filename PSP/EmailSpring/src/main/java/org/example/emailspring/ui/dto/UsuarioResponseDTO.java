package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.domain.model.Rol;

@Schema(description = "Representación pública y segura de los datos de un usuario.")
public record UsuarioResponseDTO(
        @Schema(description = "ID del usuario.", example = "1")
        Long id,
        @Schema(description = "Nombre de usuario.", example = "juan_perez")
        String username,
        @Schema(description = "Correo electrónico del usuario.", example = "juan@ejemplo.com")
        String email,
        @Schema(description = "Nombre visible del usuario.", example = "Juan Perez")
        String nombre,
        @Schema(description = "Rol del usuario en el sistema.", example = "USER")
        Rol rol) {
}
