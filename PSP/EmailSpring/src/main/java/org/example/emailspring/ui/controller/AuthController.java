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
               description = "Autentica a un usuario. Si tiene 2FA activado, se enviará un código por email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = "Login exitoso o código 2FA enviado"),
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

    @PostMapping(Constantes.AUTH_VERIFY_2FA)
    @Operation(summary = "Verificar código 2FA",
               description = "Completa el login verificando el código enviado por email.")
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

    @RequiresAuth
    @PostMapping(Constantes.AUTH_TOGGLE_2FA)
    @Operation(summary = "Activar/Desactivar 2FA",
               description = "Permite al usuario activar o desactivar la autenticación de dos factores por email.")
    @ApiResponse(responseCode = Constantes.HTTP_200, description = "2FA actualizado exitosamente")
    public ResponseEntity<ApiSuccessResponse> toggle2FA(@RequestBody Toggle2FARequest request, HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        authService.toggle2FA(usuarioId, request.enabled());

        String message = request.enabled()
                ? Constantes.DE_DOS_FACTORES_ACTIVADA_CORRECTAMENTE
                : Constantes.DE_DOS_FACTORES_DESACTIVADA;

        return ResponseEntity.ok(new ApiSuccessResponse(true, message));
    }

    @RequiresAuth
    @GetMapping(Constantes.FA_STATUS)
    @Operation(summary = "Obtener estado del 2FA",
               description = "Consulta si el usuario tiene activada la autenticación de dos factores.")
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