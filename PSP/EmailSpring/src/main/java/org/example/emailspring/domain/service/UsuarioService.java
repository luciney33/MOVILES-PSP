package org.example.emailspring.domain.service;

import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.error.BadRequestException;
import org.example.emailspring.domain.mapper.UsuarioMapper;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.domain.error.BadCredentialsException;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.example.emailspring.ui.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class UsuarioService {
    public static final String CÓDIGO_DE_ACTIVACIÓN_INVÁLIDO = "Código de activación inválido.";
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioMapper usuarioMapper, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
        this.emailService = emailService;
    }

    public Usuario login(String username, String password) {
        UsuarioEntity entity = usuarioRepository.getByUsername(username);
        if (entity == null || !passwordEncoder.matches(password, entity.getPassword())) {
            throw new BadCredentialsException(Constantes.MSG_LOGIN_INVALID);
        }

        return usuarioMapper.toDomain(entity);
    }

    public Usuario register(UsuarioDTO request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }

        String codigoActivacion = UUID.randomUUID().toString();
        LocalDateTime expiracionCodigo = LocalDateTime.now().plusHours(24);
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
                expiracionCodigo
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
            throw new BadRequestException(Constantes.EXPORADO_CODIGO_DE_ACTIVACION);
        }

        usuarioEntity.setActivo(true);
        usuarioEntity.setCodigoActivacion(null);
        usuarioEntity.setExpiracionCodigo(null);

        UsuarioEntity usuarioActualizado = usuarioRepository.save(usuarioEntity);
        return usuarioMapper.toDomain(usuarioActualizado);
    }

}
