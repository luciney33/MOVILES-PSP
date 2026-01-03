package org.example.emailspring.ui.dto;

/**
 * Response genérica para operaciones de API
 */
public record ApiSuccessResponse(
    boolean success,
    String message
) {}

