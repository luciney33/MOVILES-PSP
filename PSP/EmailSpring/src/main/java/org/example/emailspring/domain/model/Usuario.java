package org.example.emailspring.domain.model;

import java.time.LocalDateTime;

public record Usuario(
        Long id,
        String username,
        String password,
        String email,
        String nombre,
        Rol rol,
        boolean activo,
        String codigoActivacion,
        LocalDateTime expiracionCodigo
) {
}
