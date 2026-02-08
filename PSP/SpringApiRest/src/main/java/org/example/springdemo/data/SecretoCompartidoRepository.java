package org.example.springdemo.data;

import org.example.springdemo.data.entity.SecretoCompartidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretoCompartidoRepository extends JpaRepository<SecretoCompartidoEntity, Long> {
    
    List<SecretoCompartidoEntity> findByDestinatarioId(Long destinatarioId);
    
    @Query("SELECT sc FROM SecretoCompartidoEntity sc LEFT JOIN FETCH sc.secreto WHERE sc.destinatario.id = :destinatarioId")
    List<SecretoCompartidoEntity> findByDestinatarioIdWithSecreto(@Param("destinatarioId") Long destinatarioId);
    
    Optional<SecretoCompartidoEntity> findBySecretoIdAndDestinatarioId(Long secretoId, Long destinatarioId);
    
    void deleteBySecretoIdAndDestinatarioId(Long secretoId, Long destinatarioId);
}
