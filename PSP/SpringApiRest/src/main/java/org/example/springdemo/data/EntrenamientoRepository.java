package org.example.springdemo.data;

import org.example.springdemo.data.entity.EntrenamientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrenamientoRepository extends JpaRepository<EntrenamientoEntity, Long> {
    List<EntrenamientoEntity> getByUserId(Integer userId);
    List<EntrenamientoEntity> getByNombre(String nombre);
}

