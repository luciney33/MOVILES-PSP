package org.example.springdemo.ui.dto;

import org.example.springdemo.domain.model.Rol;

public record UsuarioDTO(
        Long id,
        String username,
        String email,
        String nombre,
        Rol rol
) {
}
