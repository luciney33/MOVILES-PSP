package org.example.springdemo.ui.dto;

public record CompartirSecretoRequest(
        Long destinatarioId,
        String password
) {}
