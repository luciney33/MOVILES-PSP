package org.example.springdemo.domain.model;

public record Ejercicio(
        Long id,
        Long entrenamientoId,
        String nombre,
        Integer repeticiones,
        Integer series
) {
}
