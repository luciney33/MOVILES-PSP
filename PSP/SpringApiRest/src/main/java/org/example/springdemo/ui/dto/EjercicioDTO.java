package org.example.springdemo.ui.dto;

public record EjercicioDTO(
        Long id,
        Long entrenamientoId,
        String nombre,
        Integer repeticiones,
        Integer series
) {
}
