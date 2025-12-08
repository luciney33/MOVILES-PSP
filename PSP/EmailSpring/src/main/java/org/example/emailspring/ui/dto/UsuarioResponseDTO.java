package org.example.emailspring.ui.dto;

import org.example.emailspring.domain.model.Rol;

public record UsuarioResponseDTO(
        Long id,
        String username,
        String email,
        String nombre,
        Rol rol) {
}
