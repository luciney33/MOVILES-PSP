package org.example.emailspring.data;

import org.example.emailspring.data.entity.EntrenamientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrenamientoRepository extends JpaRepository<EntrenamientoEntity, Long> {

    @Query("SELECT DISTINCT e FROM EntrenamientoEntity e LEFT JOIN FETCH e.ejercicios")
    List<EntrenamientoEntity> findAllWithEjercicios();

    @Query("SELECT e FROM EntrenamientoEntity e LEFT JOIN FETCH e.ejercicios WHERE e.id = :id")
    Optional<EntrenamientoEntity> findByIdWithEjercicios(@Param("id") Long id);
}

