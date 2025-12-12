package org.example.emailspring.domain.mapper;

import org.example.emailspring.data.entity.EntrenamientoEntity;
import org.example.emailspring.domain.model.Entrenamiento;
import org.springframework.stereotype.Component;

@Component
public class EntrenamientoMapper {
    public EntrenamientoEntity toEntity(Entrenamiento domain) {
        if (domain == null) return null;
        EntrenamientoEntity entity = new EntrenamientoEntity();
        if (domain.id() != null && domain.id() != 0) {
            entity.setId(domain.id());
        }
        entity.setUsuarioId(domain.usuarioId().longValue());
        entity.setNombre(domain.nombre());
        entity.setDescripcion(domain.descripcion());
        return entity;
    }

    public Entrenamiento toDomain(EntrenamientoEntity entity) {
        if (entity == null) return null;

        return new Entrenamiento(
                entity.getId(),
                entity.getUsuarioId().longValue(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }
}
