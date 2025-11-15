package org.example.springdemo.ui.dto;

public record EjercicioDTO(
        int id,
        int entrenamientoId,
        String nombre,
        Integer repeticiones,
        Integer series
) {
}
