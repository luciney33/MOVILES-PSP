package org.example.emailspring.domain.mapper;


import org.example.emailspring.data.entity.SecretoEntity;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.model.Secreto;
import org.example.emailspring.domain.model.Usuario;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SecretoMapper {
    private UsuarioMapper usuarioMapper;

    public SecretoMapper(@Lazy UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }
    public Secreto toDomain(SecretoEntity entity) {
        Usuario usuario = usuarioMapper.toDomain(entity.getAutor());
        return new Secreto(
                entity.getId(),
                usuario,
                entity.getSalt(),
                entity.getIv(),
                entity.getContenidoCifrado(),
                entity.getClaveSimetricaCifrada(),
                entity.getFirma()
        );
    }

    public SecretoEntity toEntity(Secreto secreto) {
        UsuarioEntity usuario = usuarioMapper.toEntity(secreto.autorId());

        return new SecretoEntity(
                secreto.id(),
                usuario,
                secreto.salt(),
                secreto.iv(),
                secreto.contenidoCifrado(),
                secreto.claveSimétricaCifrada(),
                secreto.firma(),
                null
        );
    }
}
