package org.example.emailspring.ui.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
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


    public Usuario login(String username, String password, HttpSession session) {
        Usuario usuario = usuarioService.login(username, password);
        if (Boolean.TRUE.equals(usuario.twoFactorEnabled())) {
            session.setAttribute(Constantes.PENDING_2FA_USERNAME, usuario.username());
            return null;
        }
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        session.setAttribute(Constantes.ROL, usuario.rol());
        return usuario;
    }


    public Usuario verify2FA(String username, String codigo, HttpSession session) {
        String pendingUsername = (String) session.getAttribute(Constantes.PENDING_2FA_USERNAME);

        if (pendingUsername == null || !pendingUsername.equals(username)) {
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

        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
        session.removeAttribute(Constantes.PENDING_2FA_USERNAME);
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);
        session.setAttribute(Constantes.ROL, usuario.rol());

        return usuario;
    }


    public Enable2FAResponse enable2FA(Long usuarioId, HttpSession session) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));

        String secret = totpService.generateSecret();
        String qrCodeUri = totpService.generateQrCode(secret, usuarioEntity.getUsername());
        session.setAttribute(Constantes.PENDING_2FA_SECRET, secret);

        return new Enable2FAResponse(
                secret,
                qrCodeUri,
                Constantes.MSG_ESCANEA_QR
        );
    }


    public void confirm2FA(Long usuarioId, String codigo, HttpSession session) {
        String pendingSecret = (String) session.getAttribute(Constantes.PENDING_2FA_SECRET);
        if (pendingSecret == null) {
            throw new BadRequestException(Constantes.NO_HAY_UN_PROCESO_DE_HABILITACION_2_FA_PENDIENTE);
        }

        if (!totpService.verifyCode(pendingSecret, codigo)) {
            throw new BadRequestException(Constantes.CODIGO_INVALIDO_VERIFICA_QUE_TU_APP_ESTE_SINCRONIZADA_CORRECTAMENTE);
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException(Constantes.USUARIO_NO_ENCONTRADO));

        usuarioEntity.setTwoFactorEnabled(true);
        usuarioEntity.setTwoFactorSecret(pendingSecret);

        usuarioRepository.save(usuarioEntity);

        session.removeAttribute(Constantes.PENDING_2FA_SECRET);
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

    public JwtTokenPair generateTokens(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constantes.ROL, usuario.rol().toString());
        String accessToken = jwtService.generateToken(claims, usuario.username());
        String refreshToken = jwtService.generateRefreshToken(usuario.username());
        return new JwtTokenPair(accessToken, refreshToken);
    }


    public JwtTokenPair refreshAccessToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);

            if (!jwtService.isTokenValid(refreshToken, username)) {
                throw new UnauthorizedException(Constantes.REFRESH_TOKEN_INVALIDO_O_EXPIRADO);
            }

            UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(username);
            if (usuarioEntity == null) {
                throw new UnauthorizedException(Constantes.USUARIO_NO_ENCONTRADO);
            }

            Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
            return generateTokens(usuario);
        } catch (Exception e) {
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


    public Usuario getUserByUsername(String username) {
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
