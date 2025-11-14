package org.example.springdemo.data.mapper;

import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.domain.model.Entrenamiento;
import org.springframework.stereotype.Component;

@Component
public class EntrenamientoMapDomain {
    public Entrenamiento toDomain(EntrenamientoEntity entity) {
        if (entity == null) return null;
        return new Entrenamiento(
                entity.getId(),
                entity.getUserId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }

    public EntrenamientoEntity toEntity(Entrenamiento domain) {
        if (domain == null) return null;
        EntrenamientoEntity entity = new EntrenamientoEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setNombre(domain.nombre());
        entity.setDescripcion(domain.descripcion());
        return entity;
    }
}
