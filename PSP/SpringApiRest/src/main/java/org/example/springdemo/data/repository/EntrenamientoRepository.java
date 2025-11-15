package org.example.springdemo.data.repository;

import org.example.springdemo.data.entity.EntrenamientoEntity;

import java.util.List;

public interface EntrenamientoRepository {

    List<EntrenamientoEntity> getAll();

    EntrenamientoEntity getById(int id);

    List<EntrenamientoEntity> getByUsuarioId(int usuarioId);

    int save(EntrenamientoEntity entrenamiento);

    void update(EntrenamientoEntity entrenamiento);

    void deleteByUsuarioId(int usuarioId);
    boolean delete(int id);
}
