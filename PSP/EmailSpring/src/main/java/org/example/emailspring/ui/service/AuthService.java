package org.example.emailspring.ui.service;

import io.jsonwebtoken.Claims;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.error.BadRequestException;
import org.example.emailspring.domain.error.UnauthorizedException;
import org.example.emailspring.domain.mapper.UsuarioMapper;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.domain.service.UsuarioService;
import org.example.emailspring.ui.dto.Enable2FAResponse;
import org.example.emailspring.ui.dto.JwtTokenPair;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final TotpService totpService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final TwoFactorService twoFactorService;

    public AuthService(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
                      UsuarioMapper usuarioMapper, TotpService totpService, JwtService jwtService,
                      TokenBlacklistService tokenBlacklistService, TwoFactorService twoFactorService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.totpService = totpService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.twoFactorService = twoFactorService;
    }

    public record LoginResult(Usuario usuario, boolean requires2FA) {}

    public LoginResult login(String username, String password) {
        log.info("Intento de login para usuario: {}", username);
        Usuario usuario = usuarioService.login(username, password);

        if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
            log.info("Usuario {} tiene 2FA habilitado, guardando estado pendiente en Redis", username);
            twoFactorService.setPending2FAUsername(usuario.username());
            return new LoginResult(usuario, true);
        }

        log.info("Login exitoso para usuario {} sin 2FA", username);
        return new LoginResult(usuario, false);
    }

    public Usuario verify2FA(String username, String codigo) {
        log.info("Verificando código 2FA para usuario: {}", username);

        if (!twoFactorService.hasPending2FA(username)) {
            log.warn("No hay login pendiente de 2FA para usuario: {}", username);
            throw new UnauthorizedException(Constantes.NO_HAY_UN_LOGIN_PENDIENTE_DE_VERIFICACION_2_FA);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            log.error("Usuario no encontrado durante verificación 2FA: {}", username);
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        if (usuarioEntity.getTwoFactorSecret() == null) {
            log.error("Usuario {} no tiene 2FA habilitado", username);
            throw new UnauthorizedException(Constantes.EL_USUARIO_NO_TIENE_2_FA_HABILITADO);
        }

        if (!totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), codigo)) {
            log.warn("Código 2FA inválido para usuario: {}", username);
            throw new UnauthorizedException(Constantes.CODIGO_DE_VERIFICACION_INVALIDO);
        }

        twoFactorService.removePending2FA(username);
        log.info("Verificación 2FA exitosa para usuario: {}", username);

        return usuarioMapper.toDomain(usuarioEntity);
    }

    public Enable2FAResponse enable2FA(String username) {
        log.info("Habilitando 2FA para usuario: {}", username);
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        String secret = totpService.generateSecret();
        String qrCodeUri = totpService.generateQrCode(secret, usuarioEntity.getUsername());

        twoFactorService.setPending2FASecret(username, secret);
        log.debug("Secret temporal guardado en Redis para usuario: {}", username);

        return new Enable2FAResponse(
                secret,
                qrCodeUri,
                Constantes.MSG_ESCANEA_QR
        );
    }

    public void confirm2FA(String username, String codigo) {
        log.info("Confirmando activación 2FA para usuario: {}", username);
        String pendingSecret = twoFactorService.getPending2FASecret(username);
        if (pendingSecret == null) {
            log.warn("No hay proceso de habilitación 2FA pendiente para usuario: {}", username);
            throw new BadRequestException(Constantes.NO_HAY_UN_PROCESO_DE_HABILITACION_2_FA_PENDIENTE);
        }

        if (!totpService.verifyCode(pendingSecret, codigo)) {
            log.warn("Código inválido durante confirmación 2FA para usuario: {}", username);
            throw new BadRequestException(Constantes.CODIGO_INVALIDO_VERIFICA_QUE_TU_APP_ESTE_SINCRONIZADA_CORRECTAMENTE);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        usuarioEntity.setTwoFactorEnabled(true);
        usuarioEntity.setTwoFactorSecret(pendingSecret);
        usuarioRepository.save(usuarioEntity);

        twoFactorService.removePending2FASecret(username);
        log.info("2FA activado exitosamente para usuario: {}", username);
    }

    public void disable2FA(String username) {
        log.info("Desactivando 2FA para usuario: {}", username);
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        usuarioEntity.setTwoFactorEnabled(false);
        usuarioEntity.setTwoFactorSecret(null);
        usuarioRepository.save(usuarioEntity);
        log.info("2FA desactivado para usuario: {}", username);
    }

    public boolean get2FAStatus(String username) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }
        return Boolean.TRUE.equals(usuarioEntity.getTwoFactorEnabled());
    }

    public void logout(String token) {
        String username = jwtService.extractUsername(token);
        log.info("Logout para usuario: {}", username);
        tokenBlacklistService.revokeToken(token);
        log.debug("Token revocado exitosamente para usuario: {}", username);
    }

    public Usuario register(UsuarioDTO usuario) {
        log.info("Registrando nuevo usuario: {}", usuario.username());
        return usuarioService.register(usuario);
    }

    public Usuario activarCuenta(String codigoActivacion) {
        return usuarioService.activarCuenta(codigoActivacion);
    }

    public JwtTokenPair generateTokens(Usuario usuario) {
        log.debug("Generando tokens JWT para usuario: {}", usuario.username());
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constantes.ROL, usuario.rol().toString());
        String accessToken = jwtService.generateToken(claims, usuario.username());
        String refreshToken = jwtService.generateRefreshToken(usuario.username());
        return new JwtTokenPair(accessToken, refreshToken);
    }

    public JwtTokenPair refreshAccessToken(String refreshToken, String oldAccessToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            log.info("Refrescando tokens para usuario: {}", username);

            if (!jwtService.isTokenValid(refreshToken, username)) {
                log.warn("Refresh token inválido para usuario: {}", username);
                throw new UnauthorizedException(Constantes.REFRESH_TOKEN_INVALIDO_O_EXPIRADO);
            }

            UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
            if (usuarioEntity == null) {
                log.error("Usuario no encontrado durante refresh: {}", username);
                throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
            }

            if (oldAccessToken != null && !oldAccessToken.isEmpty()) {
                log.debug("Revocando access token anterior para usuario: {}", username);
                tokenBlacklistService.revokeToken(oldAccessToken);
            }

            Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
            log.info("Tokens refrescados exitosamente para usuario: {}", username);
            return generateTokens(usuario);
        } catch (Exception e) {
            log.error("Error al refrescar tokens: {}", e.getMessage());
            throw new UnauthorizedException(Constantes.REFRESH_TOKEN_INVALIDO + e.getMessage());
        }
    }

    public Usuario getUserFromToken(String token) {
        String username = jwtService.extractUsername(token);
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }
        return usuarioMapper.toDomain(usuarioEntity);
    }

    public Claims validateAccessToken(String token) {
        return jwtService.extractAllClaims(token);
    }
}

