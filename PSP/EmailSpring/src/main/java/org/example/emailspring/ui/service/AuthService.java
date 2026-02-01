package org.example.emailspring.ui.service;

import io.jsonwebtoken.Claims;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.error.BadCredentialsException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
                      UsuarioMapper usuarioMapper, TotpService totpService, JwtService jwtService,
                      @Autowired(required = false) TokenBlacklistService tokenBlacklistService,
                      @Autowired(required = false) TwoFactorService twoFactorService,
                      AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.totpService = totpService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.twoFactorService = twoFactorService;
        this.authenticationManager = authenticationManager;
    }

    public record LoginResult(Usuario usuario, boolean requires2FA) {}

    public LoginResult login(String username, String password) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

        } catch (AuthenticationException e) {
            log.warn(Constantes.FALLO_DE_AUTENTICACION_PARA_USUARIO , username);
            throw new BadCredentialsException(Constantes.MSG_LOGIN_INVALID);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadCredentialsException(Constantes.MSG_LOGIN_INVALID);
        }

        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);

        if (Boolean.TRUE.equals(usuario.twoFactorEnabled()) && twoFactorService != null) {
            twoFactorService.setPending2FAUsername(usuario.username());
            return new LoginResult(usuario, true);
        }
        return new LoginResult(usuario, false);
    }

    public Usuario verify2FA(String username, String codigo) {

        if (twoFactorService == null || !twoFactorService.hasPending2FA(username)) {
            throw new UnauthorizedException(Constantes.NO_HAY_UN_LOGIN_PENDIENTE_DE_VERIFICACION_2_FA);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        if (usuarioEntity.getTwoFactorSecret() == null) {
            throw new UnauthorizedException(Constantes.EL_USUARIO_NO_TIENE_2_FA_HABILITADO);
        }

        if (!totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), codigo)) {
            throw new UnauthorizedException(Constantes.CODIGO_DE_VERIFICACION_INVALIDO);
        }

        twoFactorService.removePending2FA(username);
        return usuarioMapper.toDomain(usuarioEntity);
    }

    public Enable2FAResponse enable2FA(String username) {
        if (twoFactorService == null) {
            throw new BadRequestException(Constantes.MSG_2FA_SERVICE_NOT_AVAILABLE);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        String secret = totpService.generateSecret();
        String qrCodeUri = totpService.generateQrCode(secret, usuarioEntity.getUsername());

        twoFactorService.setPending2FASecret(username, secret);
        return new Enable2FAResponse(
                secret,
                qrCodeUri,
                Constantes.MSG_ESCANEA_QR
        );
    }

    public void confirm2FA(String username, String codigo) {
        if (twoFactorService == null) {
            throw new BadRequestException(Constantes.MSG_2FA_SERVICE_NOT_AVAILABLE);
        }

        String pendingSecret = twoFactorService.getPending2FASecret(username);
        if (pendingSecret == null) {
            throw new BadRequestException(Constantes.NO_HAY_UN_PROCESO_DE_HABILITACION_2_FA_PENDIENTE);
        }

        if (!totpService.verifyCode(pendingSecret, codigo)) {
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
    }

    public void disable2FA(String username) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }

        usuarioEntity.setTwoFactorEnabled(false);
        usuarioEntity.setTwoFactorSecret(null);
        usuarioRepository.save(usuarioEntity);
    }

    public boolean get2FAStatus(String username) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
        if (usuarioEntity == null) {
            throw new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO);
        }
        return Boolean.TRUE.equals(usuarioEntity.getTwoFactorEnabled());
    }

    public void logout(String token) {
        if (tokenBlacklistService != null) {
            tokenBlacklistService.revokeToken(token);
        }
    }

    public Usuario register(UsuarioDTO usuario) {
        return usuarioService.register(usuario);
    }

    public Usuario activarCuenta(String codigoActivacion) {
        return usuarioService.activarCuenta(codigoActivacion);
    }

    public JwtTokenPair generateTokens(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constantes.ROL, usuario.rol().toString());
        String accessToken = jwtService.generateToken(claims, usuario.username());
        String refreshToken = jwtService.generateRefreshToken(usuario.username());
        return new JwtTokenPair(accessToken, refreshToken);
    }

    public JwtTokenPair refreshAccessToken(String refreshToken, String oldAccessToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            if (!jwtService.isTokenValid(refreshToken, username)) {
                throw new UnauthorizedException(Constantes.REFRESH_TOKEN_INVALIDO_O_EXPIRADO);
            }
            UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
            if (usuarioEntity == null) {
                throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
            }
            if (oldAccessToken != null && !oldAccessToken.isEmpty() && tokenBlacklistService != null) {
                tokenBlacklistService.revokeToken(oldAccessToken);
            }

            Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
            return generateTokens(usuario);
        } catch (Exception e) {
            log.error(Constantes.ERROR_AL_REFRESCAR_TOKENS, e.getMessage());
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

