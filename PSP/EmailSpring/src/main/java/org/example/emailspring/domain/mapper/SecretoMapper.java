package org.example.emailspring.domain.mapper;


import org.example.emailspring.data.entity.SecretoEntity;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.model.Secreto;
import org.example.emailspring.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class SecretoMapper {
    private UsuarioMapper usuarioMapper;
    public Secreto toDomain(SecretoEntity entity) {
        Usuario usuario = usuarioMapper.toDomain(entity.getAutor());
        if (entity == null) return null;
        return new Secreto(
                entity.getId(),
                usuario,
                entity.getContenidoCifrado(),
                entity.getClaveSimétricaCifrada(),
                entity.getFirma()
        );
    }

    public SecretoEntity toEntity(Secreto secreto) {
        UsuarioEntity usuario = usuarioMapper.toEntity(secreto.autorId());

        if (secreto == null) return null;
        return new SecretoEntity(
                secreto.id(),
                usuario,
                secreto.contenidoCifrado(),
                secreto.claveSimétricaCifrada(),
                secreto.firma(),
                null
        );
    }
}
