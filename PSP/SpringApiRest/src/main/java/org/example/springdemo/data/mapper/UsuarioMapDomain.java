package org.example.springdemo.data.mapper;

import org.example.springdemo.data.entity.UsuarioEntity;
import org.example.springdemo.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapDomain {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getNombre(),
                entity.getRol()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.id());
        entity.setUsername(domain.username());
        entity.setEmail(domain.email());
        entity.setNombre(domain.nombre());
        entity.setRol(domain.rol());
        return entity;
    }
}
