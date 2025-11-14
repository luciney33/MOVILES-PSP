package org.example.springdemo.ui.dto;

import org.example.springdemo.domain.model.Usuario;

public record LoginResponse(
        boolean success,
        String message,
        Usuario usuario
) {
}
