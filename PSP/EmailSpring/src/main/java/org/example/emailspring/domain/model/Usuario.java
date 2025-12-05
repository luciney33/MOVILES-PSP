package org.example.emailspring.domain.model;

public record Usuario(
        Long id,
        String username,
        String password,
        String email,
        String nombre,
        Rol rol
) {
}
