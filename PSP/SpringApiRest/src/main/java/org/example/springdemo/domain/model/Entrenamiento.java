package org.example.springdemo.domain.model;

public record Entrenamiento(
        Long id,
        Long userId,
        String nombre,
        String descripcion) {
}
