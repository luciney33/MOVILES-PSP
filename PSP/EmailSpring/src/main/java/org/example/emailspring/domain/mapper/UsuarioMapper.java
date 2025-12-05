package org.example.emailspring.domain.mapper;


import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        return new Usuario(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getRol()
        );
    }

    public UsuarioEntity toEntity(Usuario u) {
        if (u == null) return null;
        return new UsuarioEntity(
                u.id(),
                u.username(),
                u.password(),
                u.email(),
                u.nombre(),
                u.rol()
        );
    }
}
