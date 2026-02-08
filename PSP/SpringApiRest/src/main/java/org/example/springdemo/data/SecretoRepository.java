package org.example.springdemo.data;

import org.example.springdemo.data.entity.SecretoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecretoRepository extends JpaRepository<SecretoEntity, Long> {
    
    List<SecretoEntity> findByAutorId(Long autorId);
    
    @Query("SELECT s FROM SecretoEntity s LEFT JOIN FETCH s.compartidos WHERE s.autor.id = :autorId")
    List<SecretoEntity> findByAutorIdWithCompartidos(@Param("autorId") Long autorId);
}
