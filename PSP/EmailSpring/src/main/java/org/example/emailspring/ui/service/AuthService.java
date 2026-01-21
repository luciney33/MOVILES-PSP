package org.example.emailspring.ui.service;

import io.jsonwebtoken.Claims;
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
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthService {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final TotpService totpService;
    private final JwtService jwtService;

    public AuthService(UsuarioService usuarioService, UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, TotpService totpService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.totpService = totpService;
        this.jwtService = jwtService;
    }

    /**
     * Login - Paso 1: Validar username y password
     * Si tiene 2FA activado, guarda en sesión temporal y retorna null
     * Si no tiene 2FA, establece sesión completa y retorna usuario
     */
    public Usuario login(String username, String password, HttpSession session) {
        // Validar credenciales (sin establecer sesión aún)
        Usuario usuario = usuarioService.login(username, password);

        // Si el usuario tiene 2FA habilitado, guardar en sesión temporal
        if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
            // Guardar username en sesión temporal (NO establecer sesión completa)
            session.setAttribute(Constantes.PENDING_2FA_USERNAME, usuario.username());

            // NO establecer sesión completa - retornar null para indicar que se requiere 2FA
            return null;
        }

        // Si no tiene 2FA, establecer sesión completa y login exitoso
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        session.setAttribute(Constantes.ROL, usuario.rol());
        return usuario;
    }

    /**
     * Login - Paso 2: Verificar código TOTP de Google Authenticator
     * Completa el login si el código es válido
     */
    public Usuario verify2FA(String username, String codigo, HttpSession session) {
        // Validar que hay un login pendiente
        String pendingUsername = (String) session.getAttribute(Constantes.PENDING_2FA_USERNAME);

        if (pendingUsername == null || !pendingUsername.equals(username)) {
            throw new UnauthorizedException(Constantes.NO_HAY_UN_LOGIN_PENDIENTE_DE_VERIFICACION_2_FA);
        }

        // Obtener usuario y su secreto TOTP
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }


        if (usuarioEntity.getTwoFactorSecret() == null) {
            throw new UnauthorizedException(Constantes.EL_USUARIO_NO_TIENE_2_FA_HABILITADO);
        }

        // Verificar código TOTP contra el secreto
        if (!totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), codigo)) {
            throw new UnauthorizedException(Constantes.CODIGO_DE_VERIFICACION_INVALIDO);
        }


        // Código válido - completar login
        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);

        // Limpiar datos temporales y establecer sesión completa
        session.removeAttribute(Constantes.PENDING_2FA_USERNAME);
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        session.setAttribute(Constantes.ROL, usuario.rol());

        return usuario;
    }

    /**
     * Habilitar 2FA - Paso 1: Generar secreto y QR code
     * Guarda el secreto temporalmente en sesión hasta que se confirme
     */
    public Enable2FAResponse enable2FA(Long usuarioId, HttpSession session) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));


        // Generar nuevo secreto
        String secret = totpService.generateSecret();

        // Generar QR code
        String qrCodeUri = totpService.generateQrCode(secret, usuarioEntity.getUsername());

        // Guardar secreto temporalmente en sesión (NO en BD aún)
        session.setAttribute(Constantes.PENDING_2FA_SECRET, secret);

        return new Enable2FAResponse(
                secret,
                qrCodeUri,
                Constantes.MSG_ESCANEA_QR
        );
    }

    /**
     * Habilitar 2FA - Paso 2: Confirmar con código TOTP
     * Valida el código y guarda el secreto permanentemente
     */
    public void confirm2FA(Long usuarioId, String codigo, HttpSession session) {

        // Obtener secreto temporal de la sesión
        String pendingSecret = (String) session.getAttribute(Constantes.PENDING_2FA_SECRET);
        if (pendingSecret == null) {
            throw new BadRequestException(Constantes.NO_HAY_UN_PROCESO_DE_HABILITACION_2_FA_PENDIENTE);
        }

        // Verificar código
        if (!totpService.verifyCode(pendingSecret, codigo)) {
            throw new BadRequestException(Constantes.CODIGO_INVALIDO_VERIFICA_QUE_TU_APP_ESTE_SINCRONIZADA_CORRECTAMENTE);
        }


        // Código válido - guardar permanentemente en BD
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));

        usuarioEntity.setTwoFactorEnabled(true);
        usuarioEntity.setTwoFactorSecret(pendingSecret);

        UsuarioEntity saved = usuarioRepository.save(usuarioEntity);

        // Limpiar sesión temporal
        session.removeAttribute(Constantes.PENDING_2FA_SECRET);
    }


    public void disable2FA(Long usuarioId) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));

        usuarioEntity.setTwoFactorEnabled(false);
        usuarioEntity.setTwoFactorSecret(null);
        usuarioRepository.save(usuarioEntity);
    }


    public boolean get2FAStatus(Long usuarioId) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));
        return Boolean.TRUE.equals(usuarioEntity.getTwoFactorEnabled());
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
        Object usuario = session.getAttribute(Constantes.SESSION_USUARIO_ID);
        return usuario instanceof Usuario;
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

    // ==================== MÉTODOS JWT ====================

    /**
     * Genera tokens JWT (access + refresh) para un usuario
     */
    public JwtTokenPair generateTokens(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.rol().toString());
        String accessToken = jwtService.generateToken(claims, usuario.username());
        String refreshToken = jwtService.generateRefreshToken(usuario.username());
        return new JwtTokenPair(accessToken, refreshToken);
    }

    /**
     * Refresca el access token usando un refresh token válido
     */
    public JwtTokenPair refreshAccessToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);

            if (!jwtService.isTokenValid(refreshToken, username)) {
                throw new UnauthorizedException("Refresh token inválido o expirado");
            }

            UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
            if (usuarioEntity == null) {
                throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
            }

            Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
            return generateTokens(usuario);
        } catch (Exception e) {
            throw new UnauthorizedException("Refresh token inválido: " + e.getMessage());
        }
    }

    /**
     * Extrae el usuario del token JWT
     */
    public Usuario getUserFromToken(String token) {
        String username = jwtService.extractUsername(token);
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }
        return usuarioMapper.toDomain(usuarioEntity);
    }

    /**
     * Obtiene el usuario por username (usado después de validar JWT en interceptor)
     */
    public Usuario getUserByUsername(String username) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }
        return usuarioMapper.toDomain(usuarioEntity);
    }

    /**
     * Valida un access token JWT y retorna sus Claims
     * @param token Token JWT a validar
     * @return Claims del token si es válido
     * @throws io.jsonwebtoken.JwtException si el token es inválido o está expirado
     */
    public Claims validateAccessToken(String token) {
        return jwtService.extractAllClaims(token);
    }

    /**
     * Clase interna para retornar par de tokens
     */
    public record JwtTokenPair(String accessToken, String refreshToken) {}
}
