package org.example.emailspring.ui.dto;

public record CompartirRequest(
        Long receptorId,
        String password
) {}
