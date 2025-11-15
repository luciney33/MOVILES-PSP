package org.example.springdemo.ui.mapper;

import org.example.springdemo.domain.model.Entrenamiento;
import org.example.springdemo.ui.dto.EntrenamientoDTO;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
@Component
public class EntrenamientoDtoMapper {
    public EntrenamientoDTO toDto(Entrenamiento domain) {
        return new EntrenamientoDTO(
                domain.id(),
                domain.usuarioId(),
                domain.nombre(),
                domain.descripcion()
        );
    }

    public List<EntrenamientoDTO> toDtoList(List<Entrenamiento> domains) {
        List<EntrenamientoDTO> dtos = new ArrayList<>();
        for (Entrenamiento domain : domains) {
            dtos.add(toDto(domain));
        }
        return  dtos;
    }

    public Entrenamiento toDomain(EntrenamientoDTO dto) {
        return new Entrenamiento(
                dto.id(),
                dto.usuarioId(),
                dto.nombre(),
                dto.descripcion()
        );
    }
}
