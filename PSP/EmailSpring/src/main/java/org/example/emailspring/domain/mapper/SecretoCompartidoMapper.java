package org.example.emailspring.domain.mapper;


import org.example.emailspring.data.entity.SecretoCompartidoEntity;
import org.example.emailspring.data.entity.SecretoEntity;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.model.Secreto;
import org.example.emailspring.domain.model.SecretoCompartido;
import org.example.emailspring.domain.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class SecretoCompartidoMapper {
    private SecretoMapper secretoMapper;
    private UsuarioMapper usuarioMapper;

    public SecretoCompartido toDomain(SecretoCompartidoEntity entity) {
        Secreto secreto = secretoMapper.toDomain(entity.getSecreto());
        Usuario usuario = usuarioMapper.toDomain(entity.getDestinatario());

        return new SecretoCompartido(
                entity.getId(),
                secreto,
                usuario,
                entity.getClaveSimétricaCifradaDestinatario()
                );
    }

    public SecretoCompartidoEntity toEntity(SecretoCompartido secretoCompartido) {
        UsuarioEntity usuario = usuarioMapper.toEntity(secretoCompartido.destinatarioId());
        SecretoEntity secretoEntity = secretoMapper.toEntity(secretoCompartido.secretoId());

        return new SecretoCompartidoEntity(
                secretoCompartido.id(),
                secretoEntity,
                usuario,
                secretoCompartido.claveSimétricaCifradaDestinatario()
        );
    }
}
