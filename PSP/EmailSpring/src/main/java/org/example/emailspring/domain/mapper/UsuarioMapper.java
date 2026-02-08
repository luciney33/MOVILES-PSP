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
                entity.getEmail(),
                entity.getNombre(),
                entity.getRol(),
                entity.isActivo(),
                entity.getCodigoActivacion(),
                entity.getExpiracionCodigo(),
                entity.getTwoFactorEnabled(),
                entity.getTwoFactorSecret(),
                entity.getPublicKey(),
                entity.getPrivateKeyEncrypted()
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
                u.activo(),
                u.codigoActivacion(),
                u.expiracionCodigo(),
                u.rol(),
                u.twoFactorEnabled(),
                u.twoFactorSecret(),
                u.publicKey(),
                u.privateKeyEncrypted(),
                null
        );
    }
}
