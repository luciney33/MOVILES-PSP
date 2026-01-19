package org.example.emailspring.ui.service;

import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.error.BadRequestException;
import org.example.emailspring.domain.error.UnauthorizedException;
import org.example.emailspring.domain.mapper.UsuarioMapper;
import org.example.emailspring.domain.model.Rol;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.domain.service.UsuarioService;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;


@Service
public class AuthService {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final EmailService emailService;

    public AuthService(UsuarioService usuarioService, UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.emailService = emailService;
    }

    public Usuario login(String username, String password, HttpSession session) {
        // Validar credenciales
        Usuario usuario = usuarioService.login(username, password, session);

        // Si el usuario tiene 2FA habilitado, generar y enviar código
        if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
            String codigo = generarCodigo2FA();

            // Guardar código y datos en sesión temporal
            session.setAttribute(Constantes.PENDING_2FA_CODE, codigo);
            session.setAttribute(Constantes.PENDING_2FA_USERNAME, usuario.username());
            session.setAttribute(Constantes.PENDING_2FA_EXPIRY, LocalDateTime.now().plusMinutes(Constantes.CODIGO_2FA_EXPIRY_MINUTES));

            // Enviar código por email
            emailService.enviarCodigo2FA(usuario.email(), usuario.nombre(), codigo);

            // NO establecer sesión completa aún
            return null; // Indica que se requiere verificación 2FA
        }

        // Si no tiene 2FA, login completo
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        return usuario;
    }

    public Usuario verify2FA(String username, String codigo, HttpSession session) {
        String pendingUsername = (String) session.getAttribute(Constantes.PENDING_2FA_USERNAME);
        String pendingCode = (String) session.getAttribute(Constantes.PENDING_2FA_CODE);
        LocalDateTime expiry = (LocalDateTime) session.getAttribute(Constantes.PENDING_2FA_EXPIRY);

        // Validaciones
        if (pendingUsername == null || !pendingUsername.equals(username)) {
            throw new UnauthorizedException(Constantes.NO_HAY_UN_LOGIN_PENDIENTE_DE_VERIFICACION_2_FA);
        }

        if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
            limpiarSesion2FA(session);
            throw new BadRequestException(Constantes.MSG_2FA_CODE_INVALIDO);
        }

        if (!codigo.equals(pendingCode)) {
            throw new UnauthorizedException(Constantes.CODIGO_DE_VERIFICACION_INVALIDO);
        }

        // Código válido, completar login
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);

        // Limpiar datos temporales y establecer sesión completa
        limpiarSesion2FA(session);
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        session.setAttribute(Constantes.ROL, usuario.rol());

        return usuario;
    }

    public void toggle2FA(Long usuarioId, boolean enabled) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));

        usuarioEntity.setTwoFactorEnabled(enabled);
        usuarioRepository.save(usuarioEntity);
    }

    public boolean get2FAStatus(Long usuarioId) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));
        return Boolean.TRUE.equals(usuarioEntity.getTwoFactorEnabled());
    }

    private String generarCodigo2FA() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000); // Genera número de 6 dígitos
        return String.valueOf(codigo);
    }

    private void limpiarSesion2FA(HttpSession session) {
        session.removeAttribute(Constantes.PENDING_2FA_CODE);
        session.removeAttribute(Constantes.PENDING_2FA_USERNAME);
        session.removeAttribute(Constantes.PENDING_2FA_EXPIRY);
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public Usuario register(UsuarioDTO usuario) {
        return usuarioService.register(usuario);
    }

    public Usuario activarCuenta(String codigoActivacion) {
        return usuarioService.activarCuenta(codigoActivacion);
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(Constantes.SESSION_USUARIO_ID) != null;
    }

    public Long getUsuarioIdFromSession(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute(Constantes.SESSION_USUARIO_ID);
        if (usuario == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }
        return usuario.id();
    }

    public Rol getRolFromSession(HttpSession session) {
        return session.getAttribute(Constantes.SESSION_USUARIO_ID) != null
                ? ((Usuario) session.getAttribute(Constantes.SESSION_USUARIO_ID)).rol()
                : null;
    }

    public boolean isAdmin(HttpSession session) {
        return Rol.ADMIN.equals(getRolFromSession(session));
    }
}
