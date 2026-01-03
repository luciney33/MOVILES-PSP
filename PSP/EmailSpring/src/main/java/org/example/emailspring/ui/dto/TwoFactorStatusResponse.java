package org.example.emailspring.ui.dto;

/**
 * Response para el estado de autenticación de dos factores
 */
public record TwoFactorStatusResponse(
    boolean success,
    boolean twoFactorEnabled
) {}

