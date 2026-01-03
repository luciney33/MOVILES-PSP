package org.example.emailspring.ui.service;

import dev.samstevens.totp.exceptions.QrGenerationException;
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
import org.example.emailspring.ui.dto.Enable2FAResponse;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.example.emailspring.ui.dto.UsuarioResponseDTO;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService;
    private final UsuarioMapper usuarioMapper;

    public AuthService(UsuarioService usuarioService, UsuarioRepository usuarioRepository, TotpService totpService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.totpService = totpService;
        this.usuarioMapper = usuarioMapper;
    }

    public Usuario login(String username, String password, HttpSession session) {
        Usuario usuario = usuarioService.login(username, password, session);
            session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        return usuario;
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

    private UsuarioEntity getUsuarioEntityById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));
    }

    public Enable2FAResponse enable2FA(Long usuarioId) {
        UsuarioEntity usuarioEntity = getUsuarioEntityById(usuarioId);

        try {
            String secret = totpService.generateSecret();
            String qrCodeUri = totpService.generateQrCodeImageUri(
                    secret,
                    usuarioEntity.getUsername(),
                    Constantes.NOM_APP_GOOGLEAUTHTENTICATOR
            );

            usuarioEntity.setTwoFactorEnabled(false);
            usuarioEntity.setTwoFactorSecret(secret);
            usuarioRepository.save(usuarioEntity);

            return new Enable2FAResponse(
                    secret,
                    qrCodeUri,
                    Constantes.ESCANEA_CODIGOQR_Y_CONFIRMA
            );
        } catch (QrGenerationException e) {
            throw new BadRequestException(Constantes.ERROR_GENERANDO_CODIGO_QR + e.getMessage());
        }
    }

    public void confirm2FA(Long usuarioId, String code) {
        UsuarioEntity usuarioEntity = getUsuarioEntityById(usuarioId);

        if (usuarioEntity.getTwoFactorSecret() == null) {
            throw new BadRequestException(Constantes.NO_HAY_UN_PROCESO_DE_HABILITACION_2_FA_PENDIENTE);
        }

        boolean isValid = totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), code);

        if (!isValid) {
            throw new UnauthorizedException(Constantes.CODIGO_INVALIDO_VERIFICA_QUE_TU_APP_ESTE_SINCRONIZADA_CORRECTAMENTE);
        }

        usuarioEntity.setTwoFactorEnabled(true);
        usuarioRepository.save(usuarioEntity);
    }

    public void disable2FA(Long usuarioId) {
        UsuarioEntity usuarioEntity = getUsuarioEntityById(usuarioId);
        usuarioEntity.setTwoFactorEnabled(false);
        usuarioEntity.setTwoFactorSecret(null);
        usuarioRepository.save(usuarioEntity);
    }

    public UsuarioResponseDTO verify2FA(String username, String code, HttpSession session) {
        String pendingUsername = (String) session.getAttribute(Constantes.PENDING_TWO_FACTOR_USERNAME);

        if (pendingUsername == null || !pendingUsername.equals(username)) {
            throw new UnauthorizedException(Constantes.NO_HAY_UN_LOGIN_PENDIENTE_DE_VERIFICACION_2_FA);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);

        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        if (!Boolean.TRUE.equals(usuarioEntity.getTwoFactorEnabled()) || usuarioEntity.getTwoFactorSecret() == null) {
            throw new BadRequestException(Constantes.EL_USUARIO_NO_TIENE_2_FA_HABILITADO);
        }

        boolean isValid = totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), code);

        if (!isValid) {
            throw new UnauthorizedException(Constantes.CODIGO_DE_VERIFICACION_INVALIDO);
        }

        session.removeAttribute(Constantes.PENDING_TWO_FACTOR_USERNAME);
        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);

        return new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );
    }

    public boolean get2FAStatus(Long usuarioId) {
        UsuarioEntity usuarioEntity = getUsuarioEntityById(usuarioId);
        return usuarioEntity.getTwoFactorEnabled() != null && usuarioEntity.getTwoFactorEnabled();
    }
}
