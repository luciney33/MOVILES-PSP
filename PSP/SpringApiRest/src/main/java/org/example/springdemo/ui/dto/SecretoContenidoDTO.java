package org.example.springdemo.ui.dto;

public record SecretoContenidoDTO(
        Long id,
        String titulo,
        String contenido,
        String autorUsername
) {}
