package org.example.springdemo.ui.dto;

public record UsuarioDTO(
        Long id,
        String username,
        String email,
        String nombre,
        String rol
) {
}
