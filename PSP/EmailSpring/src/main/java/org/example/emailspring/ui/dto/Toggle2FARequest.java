package org.example.emailspring.ui.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para activar/desactivar 2FA")
public record Toggle2FARequest(
        @Schema(description = "True para activar, false para desactivar", example = "true")
        boolean enabled
) {
}