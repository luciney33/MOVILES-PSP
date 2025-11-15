package org.example.springdemo.ui.mapper;

import org.example.springdemo.domain.model.Ejercicio;
import org.example.springdemo.ui.dto.EjercicioDTO;
import org.springframework.stereotype.Component;

@Component
public class EjercicioDtoMapper {
    public EjercicioDTO toDto(Ejercicio domain) {
        if (domain == null) return null;
        return new EjercicioDTO(
                domain.id(),
                domain.entrenamientoId(),
                domain.nombre(),
                domain.repeticiones(),
                domain.series()
        );
    }

    public Ejercicio fromDto(EjercicioDTO dto) {
        if (dto == null) return null;
        return new Ejercicio(
                dto.id(),
                dto.entrenamientoId(),
                dto.nombre(),
                dto.repeticiones(),
                dto.series()
        );
    }
}
