package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.emailspring.domain.model.Rol;

@Schema(description = "Datos necesarios para registrar un nuevo usuario en el sistema.")
public record UsuarioDTO(
        @Schema(description = "Nombre de usuario único.", example = "nuevo_usuario")
        String username,
        @Schema(description = "Contraseña segura para la cuenta.", example = "MiPasswordFuerte123")
        String password,
        @Schema(description = "Dirección de correo electrónico válida.", example = "correo@ejemplo.com")
        String email,
        @Schema(description = "Nombre completo o apodo del usuario.", example = "Nuevo Usuario Demo")
        String nombre,
        @Schema(description = "Rol asignado al usuario (opcional, si el registro permite elegir rol).", example = "USER")
        Rol rol
) {
}
