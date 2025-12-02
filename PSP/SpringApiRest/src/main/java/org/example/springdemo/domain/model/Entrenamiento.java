package org.example.springdemo.domain.model;

public record Entrenamiento(
        Long id,
        Integer usuarioId,
        String nombre,
        String descripcion) {
}
