package org.example.springdemo.domain.model;

public record Usuario(
    Long id,
    String username,
    String email,
    String nombre,
    String rol
) {
}
