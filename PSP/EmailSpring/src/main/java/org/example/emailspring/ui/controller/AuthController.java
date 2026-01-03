package org.example.emailspring.ui.controller;

import dev.samstevens.totp.exceptions.QrGenerationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.data.entity.UsuarioEntity;
import org.example.emailspring.domain.error.BadRequestException;
import org.example.emailspring.domain.error.UnauthorizedException;
import org.example.emailspring.domain.mapper.UsuarioMapper;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.dto.*;
import org.example.emailspring.ui.interceptor.RequiresAuth;
import org.example.emailspring.ui.service.AuthService;
import org.example.emailspring.ui.service.TotpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping(Constantes.API_AUTH)
@Tag(name = Constantes.TAG_AUTENTICACION, description = Constantes.TAG_AUTENTICACION_DESC)
public class AuthController {
    private final AuthService authService;
    private final TotpService totpService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthController(AuthService authService, TotpService totpService, UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.authService = authService;
        this.totpService = totpService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }



    @PostMapping(Constantes.AUTH_LOGIN)
    @Operation(summary = Constantes.OP_INICIAR_SESION, description = Constantes.OP_INICIAR_SESION_DESC)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.MSG_LOGIN_SUCCESS),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.MSG_LOGIN_INVALID)
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Usuario usuario = authService.login(request.username(), request.password(),session);


        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );

        LoginResponse response = new LoginResponse(usuarioResponseDTO, Constantes.MSG_LOGIN_SUCCESS);
        return ResponseEntity.ok(response);
    }


    @PostMapping(Constantes.AUTH_LOGOUT)
    @Operation(summary = Constantes.OP_CERRAR_SESION, description = Constantes.OP_CERRAR_SESION_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.MSG_LOGOUT_SUCCESS)
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Constantes.MSG_LOGOUT_SUCCESS);
    }


    @PostMapping(Constantes.AUTH_REGISTER)
    @Operation(summary = Constantes.OP_REGISTRAR_USUARIO, description = Constantes.OP_REGISTRAR_USUARIO_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_201, description = Constantes.RESP_USUARIO_REGISTRADO_EXITOSAMENTE)
    public ResponseEntity<Usuario> register(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(usuario));

    }


    @RequiresAuth
    @PostMapping(Constantes.FA_ENABLE)
    public ResponseEntity<Enable2FADataResponse> enable2FA(HttpSession session) {

        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);

        if (usuarioEntity.isEmpty()) {
            throw new BadRequestException("Usuario no encontrado");
        }

        UsuarioEntity usuEntity = usuarioEntity.get();

        try {
            String secret = totpService.generateSecret();
            String qrCodeUri = totpService.generateQrCodeImageUri(
                    secret,
                    usuEntity.getUsername(),
                    "EmailSpring con Autenticacion de dos factores"
            );

            usuEntity.setTwoFactorEnabled(false);
            usuEntity.setTwoFactorSecret(secret);
            usuarioRepository.save(usuEntity);

            Enable2FAResponse data = new Enable2FAResponse(
                    secret,
                    qrCodeUri,
                    "Escanea el código QR con tu aplicación autenticadora (Google Authenticator, Authy, etc.) y confirma con un código"
            );

            return ResponseEntity.ok(new Enable2FADataResponse(true, data));
        } catch (QrGenerationException e) {
            throw new BadRequestException("Error generando código QR: " + e.getMessage());
        }
    }

    @RequiresAuth
    @PostMapping("/2fa/confirm")
    public ResponseEntity<ApiSuccessResponse> confirm2FA(@RequestBody Confirm2FARequest request, HttpSession session) {
        // Verificar que el usuario esté autenticado
        if (!authService.isAuthenticated(session)) {
            throw new UnauthorizedException("No autenticado");
        }

        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Optional<UsuarioEntity> usuarioEntityOpt = usuarioRepository.findById(usuarioId);

        if (usuarioEntityOpt.isEmpty()) {
            throw new BadRequestException("Usuario no encontrado");
        }

        UsuarioEntity usuarioEntity = usuarioEntityOpt.get();

        // Verificar que tiene un secreto pendiente
        if (usuarioEntity.getTwoFactorSecret() == null) {
            throw new BadRequestException("No hay un proceso de habilitación 2FA pendiente");
        }

        // Verificar el código TOTP
        boolean isValid = totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), request.code());

        if (!isValid) {
            throw new UnauthorizedException("Código inválido. Verifica que tu app esté sincronizada correctamente.");
        }

        // Activar 2FA
        usuarioEntity.setTwoFactorEnabled(true);
        usuarioRepository.save(usuarioEntity);

        return ResponseEntity.ok(new ApiSuccessResponse(true, "Autenticación de dos factores activada correctamente"));
    }

    @RequiresAuth
    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiSuccessResponse> disable2FA(HttpSession session) {
        // Verificar que el usuario esté autenticado
        if (!authService.isAuthenticated(session)) {
            throw new UnauthorizedException("No autenticado");
        }

        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Optional<UsuarioEntity> usuarioEntityOpt = usuarioRepository.findById(usuarioId);

        if (usuarioEntityOpt.isEmpty()) {
            throw new BadRequestException("Usuario no encontrado");
        }

        UsuarioEntity usuarioEntity = usuarioEntityOpt.get();
        usuarioEntity.setTwoFactorEnabled(false);
        usuarioEntity.setTwoFactorSecret(null);
        usuarioRepository.save(usuarioEntity);

        return ResponseEntity.ok(new ApiSuccessResponse(true, "Autenticación de dos factores desactivada"));
    }

    @RequiresAuth
    @PostMapping("/2fa/verify")
    public ResponseEntity<Verify2FAResponse> verify2FA(@RequestBody Verify2FARequest request, HttpSession session) {
        // Verificar que hay un login pendiente de 2FA
        String pendingUsername = (String) session.getAttribute("pendingTwoFactorUsername");

        if (pendingUsername == null || !pendingUsername.equals(request.username())) {
            throw new UnauthorizedException("No hay un login pendiente de verificación 2FA");
        }

        UsuarioEntity usuarioEntity = usuarioRepository.findByUsername(request.username());

        if (usuarioEntity == null) {
            throw new UnauthorizedException("Usuario no encontrado");
        }

        // Verificar que tiene 2FA habilitado
        if (!Boolean.TRUE.equals(usuarioEntity.getTwoFactorEnabled()) || usuarioEntity.getTwoFactorSecret() == null) {
            throw new BadRequestException("El usuario no tiene 2FA habilitado");
        }

        // Verificar el código TOTP
        boolean isValid = totpService.verifyCode(usuarioEntity.getTwoFactorSecret(), request.code());

        if (!isValid) {
            throw new UnauthorizedException("Código de verificación inválido");
        }

        // Código válido - completar el login
        session.removeAttribute("pendingTwoFactorUsername");
        Usuario usuario = usuarioMapper.toDomain(usuarioEntity);
        session.setAttribute(Constantes.SESSION_USUARIO_ID, usuario);

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );

        return ResponseEntity.ok(new Verify2FAResponse(true, "Login completado exitosamente", usuarioDTO));
    }


    @RequiresAuth
    @GetMapping("/2fa/status")
    public ResponseEntity<TwoFactorStatusResponse> get2FAStatus(HttpSession session) {
        if (!authService.isAuthenticated(session)) {
            throw new UnauthorizedException("No autenticado");
        }

        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Optional<UsuarioEntity> usuarioEntityOpt = usuarioRepository.findById(usuarioId);

        if (usuarioEntityOpt.isEmpty()) {
            throw new BadRequestException("Usuario no encontrado");
        }

        UsuarioEntity usuarioEntity = usuarioEntityOpt.get();
        boolean twoFactorEnabled = usuarioEntity.getTwoFactorEnabled() != null && usuarioEntity.getTwoFactorEnabled();

        return ResponseEntity.ok(new TwoFactorStatusResponse(true, twoFactorEnabled));
    }

}