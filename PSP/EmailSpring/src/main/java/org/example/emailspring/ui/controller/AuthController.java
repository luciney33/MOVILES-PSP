package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.dto.*;
import org.example.emailspring.ui.interceptor.RequiresAuth;
import org.example.emailspring.ui.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(Constantes.API_AUTH)
@Tag(name = Constantes.TAG_AUTENTICACION, description = Constantes.TAG_AUTENTICACION_DESC)
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(Constantes.AUTH_LOGIN)
    @Operation(summary = Constantes.OP_INICIAR_SESION,
               description = "Autentica a un usuario. Si tiene 2FA activado, retorna requiresTwoFactor=true.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = "Login exitoso o se requiere código 2FA"),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.MSG_LOGIN_INVALID)
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        Usuario usuario = authService.login(request.username(), request.password(), session);

        // Si usuario es null, significa que se requiere 2FA
        if (usuario == null) {
            return ResponseEntity.ok(new Login2FARequiredResponse(true, Constantes.MSG_2FA_REQUERIDO));
        }

        // Login exitoso sin 2FA
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

    @PostMapping(Constantes.AUTH_2FA_ENABLE)
    @RequiresAuth
    @Operation(summary = "Habilitar 2FA - Paso 1: Generar QR",
               description = "Genera un secreto TOTP y devuelve el QR code para escanear con Google Authenticator/Authy")
    @ApiResponse(responseCode = Constantes.HTTP_200, description = "Secreto y QR code generados")
    public ResponseEntity<Enable2FADataResponse> enable2FA(HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        Enable2FAResponse data = authService.enable2FA(usuarioId, session);
        return ResponseEntity.ok(new Enable2FADataResponse(true, data));
    }

    @PostMapping(Constantes.AUTH_2FA_CONFIRM)
    @RequiresAuth
    @Operation(summary = "Habilitar 2FA - Paso 2: Confirmar código",
               description = "Verifica el código TOTP generado por la app autenticadora y activa 2FA permanentemente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = "2FA activado exitosamente"),
            @ApiResponse(responseCode = Constantes.HTTP_400, description = "Código inválido")
    })
    public ResponseEntity<ApiSuccessResponse> confirm2FA(@RequestBody Confirm2FARequest request, HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        authService.confirm2FA(usuarioId, request.code(), session);
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_2FA_ACTIVADA));
    }

    @PostMapping(Constantes.AUTH_2FA_DISABLE)
    @RequiresAuth
    @Operation(summary = "Desactivar 2FA",
               description = "Desactiva la autenticación de dos factores para el usuario actual")
    @ApiResponse(responseCode = Constantes.HTTP_200, description = "2FA desactivado")
    public ResponseEntity<ApiSuccessResponse> disable2FA(HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        authService.disable2FA(usuarioId);
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_2FA_DESACTIVADA));
    }

    @PostMapping(Constantes.AUTH_2FA_VERIFY)
    @Operation(summary = "Login - Paso 2: Verificar código TOTP",
               description = "Completa el login verificando el código TOTP de Google Authenticator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = "Código verificado, login completado"),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = "Código inválido o expirado")
    })
    public ResponseEntity<LoginResponse> verify2FA(@RequestBody Verify2FALoginRequest request, HttpSession session) {
        Usuario usuario = authService.verify2FA(request.username(), request.codigo(), session);

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );

        LoginResponse response = new LoginResponse(usuarioResponseDTO, Constantes.LOGIN_COMPLETADO_EXITOSAMENTE);
        return ResponseEntity.ok(response);
    }

    @GetMapping(Constantes.AUTH_2FA_STATUS)
    @RequiresAuth
    @Operation(summary = "Obtener estado del 2FA",
               description = "Consulta si el usuario tiene activada la autenticación de dos factores")
    @ApiResponse(responseCode = Constantes.HTTP_200, description = "Estado del 2FA obtenido")
    public ResponseEntity<TwoFactorStatusResponse> get2FAStatus(HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        boolean twoFactorEnabled = authService.get2FAStatus(usuarioId);
        return ResponseEntity.ok(new TwoFactorStatusResponse(true, twoFactorEnabled));
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

}