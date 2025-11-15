package org.example.springdemo.ui.dto;


public record EntrenamientoDTO(
        int id,
        int usuarioId,
        String nombre,
        String descripcion
) {
}
