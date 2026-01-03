package org.example.emailspring.ui.dto;


public record TwoFactorStatusResponse(
    boolean success,
    boolean twoFactorEnabled
) {}

