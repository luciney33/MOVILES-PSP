package org.example.springdemo.ui.dto;

import org.example.springdemo.domain.model.Ejercicio;

import java.util.List;

public record EntrenamientoDTO(
        int id,
        int usuarioId,
        String nombre,
        String descripcion,
        List<Ejercicio> ejercicios
) {
}
