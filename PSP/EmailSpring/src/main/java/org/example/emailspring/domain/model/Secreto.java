package org.example.emailspring.domain.model;


public record Secreto(
        Long id,
        Usuario autorId,
        byte[] salt,
        byte[] iv,
        byte[] contenidoCifrado,
        byte[] claveSimétricaCifrada,
        byte[] firma
) {
}

