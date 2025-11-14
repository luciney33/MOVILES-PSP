package org.example.springdemo.data.mapper;

import org.example.springdemo.data.entity.EjercicioEntity;
import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.example.springdemo.domain.model.Ejercicio;
import org.springframework.stereotype.Component;

@Component
public class EjercicioMapDomain {

    public Ejercicio toDomain(EjercicioEntity entity) {
        if (entity == null) return null;
        return new Ejercicio(
                entity.getId(),
                entity.getEntrenamiento().getId(),
                entity.getNombre(),
                entity.getRepeticiones(),
                entity.getSeries()
        );
    }

    public EjercicioEntity toEntity(Ejercicio domain, EntrenamientoEntity entrenamientoEntity) {
        if (domain == null) return null;
        EjercicioEntity entity = new EjercicioEntity();
        entity.setId(domain.id());
        entity.setNombre(domain.nombre());
        entity.setRepeticiones(domain.repeticiones());
        entity.setSeries(domain.series());
        entity.setEntrenamiento(entrenamientoEntity);
        return entity;
    }
}
