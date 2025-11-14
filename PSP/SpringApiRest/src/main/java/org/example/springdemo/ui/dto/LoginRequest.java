package org.example.springdemo.ui.dto;

public record LoginRequest(
        String username,
        String password
) {
}
