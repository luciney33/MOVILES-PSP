package org.example.springdemo.data.repository;

import org.example.springdemo.data.entity.EjercicioEntity;
import org.example.springdemo.data.entity.EntrenamientoEntity;

import java.util.List;

public interface EjercicioRepository {
    List<EjercicioEntity> getAll();
    EjercicioEntity getById(int id);
    List<EjercicioEntity> getByEntrenamientoId(int entrenamientoId);
    int save(EjercicioEntity ejercicio);
    void update(EjercicioEntity ejercicio);
    void deleteByEntrenamientoId(int entrenamientoId);
    boolean delete(int id);
}
