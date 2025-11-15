package org.example.springdemo.domain.model;

public record Ejercicio(
        int id,
        int entrenamientoId,
        String nombre,
        Integer repeticiones,
        Integer series
) {
}
