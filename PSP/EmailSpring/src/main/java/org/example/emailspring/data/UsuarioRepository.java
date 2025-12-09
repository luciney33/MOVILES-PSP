package org.example.emailspring.data;

import org.example.emailspring.data.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    UsuarioEntity getByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    UsuarioEntity findByCodigoActivacion(String codigoActivacion);
}
