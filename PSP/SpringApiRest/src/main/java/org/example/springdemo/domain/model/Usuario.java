package org.example.springdemo.domain.model;

public record Usuario(
        Long id,
        String username,
        String password,
        String email,
        String nombre,
        Rol rol
) {
}
