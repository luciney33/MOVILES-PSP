package org.example.springdemo.domain.model;

public record Usuario(
        int id,
        String username,
        String email,
        String nombre,
        String rol
) {
}
