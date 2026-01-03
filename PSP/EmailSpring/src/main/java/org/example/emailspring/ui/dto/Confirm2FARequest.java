package org.example.emailspring.ui.dto;


public record Confirm2FARequest(
    String code  // Código TOTP de 6 dígitos
) {}

