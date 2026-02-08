package org.example.emailspring.data;

import org.example.emailspring.data.entity.SecretoCompartidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecretoCompartidoRepository extends JpaRepository<SecretoCompartidoEntity, Long> {

    @Query("SELECT sc FROM SecretoCompartidoEntity sc WHERE sc.secreto.id = :secretoId AND sc.destinatario.id = :destinatarioId")
    Optional<SecretoCompartidoEntity> findBySecretoIdAndDestinatarioId(@Param("secretoId") Long secretoId, @Param("destinatarioId") Long destinatarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SecretoCompartidoEntity sc WHERE sc.secreto.id = :secretoId AND sc.secreto.autor.id = :autorId AND sc.destinatario.id = :destinatarioId")
    void deleteBySecretoIdAndSecretoAutorIdAndDestinatarioId(@Param("secretoId") Long secretoId, @Param("autorId") Long autorId, @Param("destinatarioId") Long destinatarioId);
}

