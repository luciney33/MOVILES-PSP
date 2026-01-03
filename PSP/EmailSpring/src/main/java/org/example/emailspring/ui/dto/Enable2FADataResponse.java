package org.example.emailspring.ui.dto;

/**
 * Response wrapper que contiene los datos de habilitación 2FA
 */
public record Enable2FADataResponse(
    boolean success,
    Enable2FAResponse data
) {}

