package org.example.emailspring.domain.service;

import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.error.BadRequestException;
import org.example.emailspring.domain.mapper.UsuarioMapper;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.example.emailspring.ui.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AsymmetricEncryptionService asymmetricService;
    private final SymmetricEncryptionService symmetricService;
    private final UsuarioMapper usuarioMapper;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AsymmetricEncryptionService asymmetricService, SymmetricEncryptionService symmetricService, UsuarioMapper usuarioMapper, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.asymmetricService = asymmetricService;
        this.symmetricService = symmetricService;
        this.usuarioMapper = usuarioMapper;
        this.emailService = emailService;
    }


    public Usuario register(UsuarioDTO request) throws Exception {
        KeyPair keyPair = asymmetricService.generateKeyPair();
        byte[] salt = symmetricService.generateSalt();
        byte[] iv = symmetricService.generateIV();
        SecretKey keyDerivada = symmetricService.generateKeyFromPassword(request.password(), salt);

        byte[] privKeyRaw = keyPair.getPrivate().getEncoded();
        byte[] clavePrivCifrada = symmetricService.encrypt(privKeyRaw, keyDerivada, iv);

        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(Constantes.MSG_USERNAME_YA_EN_USO);
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(Constantes.MSG_EMAIL_YA_EN_USO);
        }

        String codigoActivacion = UUID.randomUUID().toString();
        LocalDateTime expiracionCodigo = LocalDateTime.now().plusHours(48);
        String hashedPassword = passwordEncoder.encode(request.password());

        Usuario nuevoUsuario = new Usuario(
                null,
                request.username(),
                hashedPassword,
                request.email(),
                request.nombre(),
                request.rol(),
                false,
                codigoActivacion,
                expiracionCodigo,
                false,
                null,
                salt,
                iv,
                keyPair.getPublic().getEncoded(),
                clavePrivCifrada
        );

        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuarioMapper.toEntity(nuevoUsuario));

        emailService.enviarEmailActivacion(usuarioGuardado.getEmail(), usuarioGuardado.getNombre(),  codigoActivacion);

        return usuarioMapper.toDomain(usuarioGuardado);
    }

    public Usuario activarCuenta(String codigoActivacion) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByCodigoActivacion(codigoActivacion);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.CODIGO_DE_ACTIVACION_INVALIDO);
        }
        if (usuarioEntity.getExpiracionCodigo() != null && usuarioEntity.getExpiracionCodigo().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(Constantes.EXPIRADO_CODIGO_DE_ACTIVACION);
        }

        usuarioEntity.setActivo(true);
        usuarioEntity.setCodigoActivacion(null);
        usuarioEntity.setExpiracionCodigo(null);

        UsuarioEntity usuarioActualizado = usuarioRepository.save(usuarioEntity);
        return usuarioMapper.toDomain(usuarioActualizado);
    }

    public PrivateKey obtenerClavePrivadaDescifrada(UsuarioEntity usuario, String passwordPlana) throws Exception {
        SecretKey keyDerivada = symmetricService.generateKeyFromPassword(passwordPlana, usuario.getSalt());
        byte[] privKeyRaw = symmetricService.decrypt(usuario.getClavePrivada(), keyDerivada, usuario.getIv());
        return asymmetricService.bytesToPrivateKey(privKeyRaw);
    }

}
