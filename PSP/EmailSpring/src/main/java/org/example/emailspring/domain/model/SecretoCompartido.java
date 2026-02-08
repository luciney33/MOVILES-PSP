package org.example.emailspring.domain.model;


public record SecretoCompartido(
        Long id,
        Secreto secretoId,
        Usuario destinatarioId,
        byte[] claveSimétricaCifradaDestinatario) {
}

