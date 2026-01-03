package org.example.emailspring.ui.dto;


public record Verify2FARequest(
    String username,
    String code
) {}

