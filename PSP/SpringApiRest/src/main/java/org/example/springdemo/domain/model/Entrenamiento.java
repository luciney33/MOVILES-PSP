package org.example.springdemo.domain.model;

public record Entrenamiento(
        int id,
        int usuarioId,
        String nombre,
        String descripcion) {
}
