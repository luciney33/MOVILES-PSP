package org.example.emailspring.data;

import org.example.emailspring.data.entity.EntrenamientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrenamientoRepository extends JpaRepository<EntrenamientoEntity, Long> {
}

