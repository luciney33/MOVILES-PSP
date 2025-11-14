package org.example.springdemo.ui.mapper;

import org.example.springdemo.domain.model.Usuario;
import org.example.springdemo.ui.dto.UsuarioDTO;

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

    public Usuario fromDto(UsuarioDTO dto) {
        if (dto == null) return null;
        return new Usuario(
                dto.id(),
                dto.username(),
                dto.email(),
                dto.nombre(),
                dto.rol()
        );
    }
}
