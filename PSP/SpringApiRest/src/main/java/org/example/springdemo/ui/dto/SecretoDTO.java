package org.example.springdemo.ui.dto;

import java.time.LocalDateTime;

public record SecretoDTO(
        Long id,
        String titulo,
        String autorUsername,
        LocalDateTime fechaCreacion,
        boolean esPropio
) {}
