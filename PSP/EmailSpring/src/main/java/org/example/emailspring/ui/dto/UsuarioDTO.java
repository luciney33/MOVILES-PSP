package org.example.emailspring.ui.dto;

import org.example.emailspring.domain.model.Rol;

public record UsuarioDTO(
        Long id,
        String username,
        String email,
        String nombre,
        Rol rol
) {
}
