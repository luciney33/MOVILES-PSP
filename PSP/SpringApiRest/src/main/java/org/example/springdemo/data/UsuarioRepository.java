package org.example.springdemo.data;

import org.example.springdemo.data.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    UsuarioEntity getByUsername(String username);
}
