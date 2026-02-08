package org.example.springdemo.ui.dto;

public record SecretoRequest(
        String titulo,
        String contenido,
        String password
) {}
