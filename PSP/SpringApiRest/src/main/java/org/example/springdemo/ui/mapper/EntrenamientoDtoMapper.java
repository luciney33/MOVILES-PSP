package org.example.springdemo.ui.mapper;

import org.example.springdemo.domain.model.Ejercicio;
import org.example.springdemo.domain.model.Entrenamiento;
import org.example.springdemo.ui.dto.EntrenamientoDTO;

import java.util.Collections;
import java.util.List;

public class EntrenamientoDtoMapper {
    public EntrenamientoDTO toDto(Entrenamiento domain, List<Ejercicio> ejercicios) {
        return new EntrenamientoDTO(
                domain.id(),
                domain.usuarioId(),
                domain.nombre(),
                domain.descripcion(),
                ejercicios != null ? ejercicios : Collections.emptyList()
        );
    }

    public Entrenamiento fromDto(EntrenamientoDTO dto) {
        return new Entrenamiento(
                dto.id(),
                dto.usuarioId(),
                dto.nombre(),
                dto.descripcion()
        );
    }
}
