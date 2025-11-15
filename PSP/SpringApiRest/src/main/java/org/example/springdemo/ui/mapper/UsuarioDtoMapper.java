package org.example.springdemo.ui.mapper;

import org.example.springdemo.domain.model.Usuario;
import org.example.springdemo.ui.dto.UsuarioDTO;
import org.springframework.stereotype.Component;

@Component
public class UsuarioDtoMapper {
    public UsuarioDTO toDto(Usuario domain) {
        if (domain == null) return null;
        return new UsuarioDTO(
                domain.id(),
                domain.username(),
                domain.email(),
                domain.nombre(),
                domain.rol()
        );
    }

}
