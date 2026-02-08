package org.example.emailspring.domain.mapper;

import org.example.emailspring.data.entity.EjercicioEntity;
import org.example.emailspring.domain.model.Ejercicio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EjercicioMapper {

    public Ejercicio toDomain(EjercicioEntity entity) {
        if (entity == null) return null;
        return new Ejercicio(
                entity.getId(),
                entity.getNombre(),
                entity.getTipoEntrenamiento(),
                entity.getImagenUrl(),
                entity.getDescripcion()
        );
    }

    public List<Ejercicio> toDomainList(Set<EjercicioEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}

