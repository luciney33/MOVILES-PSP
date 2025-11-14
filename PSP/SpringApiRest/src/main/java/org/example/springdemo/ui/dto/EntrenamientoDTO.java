package org.example.springdemo.ui.dto;

import java.util.List;

public record EntrenamientoDTO(
        Long id,
        Long userId,
        String nombre,
        String descripcion,
        List<EjercicioDTO> ejercicios
) {
}
