package org.example.springdemo.ui.mapper;

import org.example.springdemo.domain.model.Entrenamiento;
import org.example.springdemo.ui.dto.EjercicioDTO;
import org.example.springdemo.ui.dto.EntrenamientoDTO;

import java.util.Collections;
import java.util.List;

public class EntrenamientoDtoMapper {
    public EntrenamientoDTO toDto(Entrenamiento domain, List<EjercicioDTO> ejercicios) {
        return new EntrenamientoDTO(
                domain.id(),
                domain.userId(),
                domain.nombre(),
                domain.descripcion(),
                ejercicios != null ? ejercicios : Collections.emptyList()
        );
    }

    public Entrenamiento fromDto(EntrenamientoDTO dto) {
        return new Entrenamiento(
                dto.id(),
                dto.userId(),
                dto.nombre(),
                dto.descripcion()
        );
    }
}
