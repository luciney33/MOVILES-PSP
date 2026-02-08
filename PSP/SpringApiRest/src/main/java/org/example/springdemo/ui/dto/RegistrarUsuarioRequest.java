package org.example.springdemo.ui.dto;

public record RegistrarUsuarioRequest(
        String username,
        String password,
        String email,
        String nombre,
        String rol
) {}
