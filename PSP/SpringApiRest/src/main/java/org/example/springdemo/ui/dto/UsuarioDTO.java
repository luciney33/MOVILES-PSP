package org.example.springdemo.ui.dto;

public record UsuarioDTO(
        int id,
        String username,
        String email,
        String nombre,
        String rol
) {
}
