package org.example.emailspring.ui.dto;

public record Enable2FAResponse(
    String secret,
    String qrCodeUri,
    String message
) {}

