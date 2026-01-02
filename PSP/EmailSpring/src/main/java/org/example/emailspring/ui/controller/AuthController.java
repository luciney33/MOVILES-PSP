package org.example.emailspring.ui.controller;

import dev.samstevens.totp.exceptions.QrGenerationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.data.UsuarioRepository;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.dto.*;
import org.example.emailspring.ui.service.AuthService;
import org.example.emailspring.ui.service.TotpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping(Constantes.API_AUTH)
@Tag(name = Constantes.TAG_AUTENTICACION, description = Constantes.TAG_AUTENTICACION_DESC)
public class AuthController {
    private final AuthService authService;
    private final TotpService totpService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthService authService, TotpService totpService, UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.totpService = totpService;
        this.usuarioRepository = usuarioRepository;
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

    // ============== ENDPOINTS 2FA (TOTP) ==============

    @PostMapping("/2fa/enable")
    public ResponseEntity<?> enable2FA(HttpSession session) {
        // Verificar que el usuario esté autenticado
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "No autenticado"));
        }

        boolean usuarioId = authService.isAuthenticated(session);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        try {
            // Generar secreto TOTP
            String secret = totpService.generateSecret();

            // Generar QR code
            String qrCodeUri = totpService.generateQrCodeImageUri(
                    secret,
                    usuario.username(),
                    "MiAplicacion" // Nombre de tu app que aparecerá en Google Authenticator
            );

            // Guardar el secreto temporalmente (aún no activado)
            usuario = usuario.set2FA(false,secret); // Aún no activado hasta confirmar
            usuarioRepository.save(usuario);

            Enable2FAResponse response = new Enable2FAResponse(
                    secret,
                    qrCodeUri,
                    "Escanea el código QR con tu aplicación autenticadora (Google Authenticator, Authy, etc.) y confirma con un código"
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", response
            ));
        } catch (QrGenerationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error generando código QR: " + e.getMessage()));
        }
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<?> confirm2FA(@RequestBody Confirm2FARequest request, HttpSession session) {
        // Verificar que el usuario esté autenticado
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "No autenticado"));
        }

        Long usuarioId = authService.isAuthenticated(session);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que tiene un secreto pendiente
        if (usuario.twoFactorSecret() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "No hay un proceso de habilitación 2FA pendiente"));
        }

        // Verificar el código TOTP
        boolean isValid = totpService.verifyCode(usuario.twoFactorSecret(), request.code());

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Código inválido. Verifica que tu app esté sincronizada correctamente."));
        }

        // Activar 2FA
        usuario = usuario.set2FA(true,usuario.twoFactorSecret());
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Autenticación de dos factores activada correctamente"
        ));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<?> disable2FA(HttpSession session) {
        // Verificar que el usuario esté autenticado
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "No autenticado"));
        }

        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get().set2FA(false,null);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Autenticación de dos factores desactivada"
        ));
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<?> verify2FA(@RequestBody Verify2FARequest request, HttpSession session) {
        // Verificar que hay un login pendiente de 2FA
        String pendingUsername = (String) session.getAttribute("pendingTwoFactorUsername");

        if (pendingUsername == null || !pendingUsername.equals(request.username())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "No hay un login pendiente de verificación 2FA"));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(request.username());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar que tiene 2FA habilitado
        if (!Boolean.TRUE.equals(usuario.twoFactorEnabled()) || usuario.twoFactorSecret() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "El usuario no tiene 2FA habilitado"));
        }

        // Verificar el código TOTP
        boolean isValid = totpService.verifyCode(usuario.twoFactorSecret(), request.code());

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Código de verificación inválido"));
        }

        // Código válido - completar el login
        session.removeAttribute("pendingTwoFactorUsername");
        session.setAttribute("usuarioId", usuario.id());
        session.setAttribute("username", usuario.username());
        session.setAttribute("rol", usuario.rol());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login completado exitosamente",
                "usuario", usuario
        ));
    }


    @GetMapping("/2fa/status")
    public ResponseEntity<?> get2FAStatus(HttpSession session) {
        if (!authService.isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "No autenticado"));
        }

        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "twoFactorEnabled", usuario.twoFactorEnabled()
        ));
    }

}