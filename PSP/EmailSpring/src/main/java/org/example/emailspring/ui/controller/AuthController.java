package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
               description = Constantes.AUTENTICA_A_UN_USUARIO_SI_TIENE_2_FA_ACTIVADO_RETORNA_REQUIRES_TWO_FACTOR_TRUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.LOGIN_EXITOSO_O_SE_REQUIERE_CODIGO_2_FA),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.MSG_LOGIN_INVALID)
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        Usuario usuario = authService.login(request.username(), request.password(), session);

        // Si usuario es null, significa que se requiere 2FA
        if (usuario == null) {
            return ResponseEntity.ok(new Login2FARequiredResponse(true, Constantes.MSG_2FA_REQUERIDO));
        }

        // Login exitoso sin 2FA - Generar tokens JWT
        AuthService.JwtTokenPair tokens = authService.generateTokens(usuario);

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );

        JwtAuthResponse response = new JwtAuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                usuarioResponseDTO,
                Constantes.MSG_LOGIN_SUCCESS
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping(Constantes.AUTH_2FA_ENABLE)
    @RequiresAuth
    @Operation(summary = Constantes.HABILITAR_2_FA_PASO_1_GENERAR_QR,
               description = Constantes.GENERA_UN_SECRETO_TOTP_Y_DEVUELVE_EL_QR_CODE_PARA_ESCANEAR_CON_GOOGLE_AUTHENTICATOR)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.SECRETO_Y_QR_CODE_GENERADOS)
    public ResponseEntity<Enable2FADataResponse> enable2FA(HttpSession session, HttpServletRequest request) {
        // Obtener username del token JWT (ya validado por el interceptor)
        String username = (String) request.getAttribute("username");
        Usuario usuario = authService.getUserByUsername(username);

        Enable2FAResponse data = authService.enable2FA(usuario.id(), session);
        return ResponseEntity.ok(new Enable2FADataResponse(true, data));
    }

    @PostMapping(Constantes.AUTH_2FA_CONFIRM)
    @RequiresAuth
    @Operation(summary = Constantes.HABILITAR_2_FA_PASO_2_CONFIRMAR_CODIGO,
               description = Constantes.VERIFICA_EL_CODIGO_TOTP_GENERADO_POR_LA_APP_AUTENTICADORA_Y_ACTIVA_2_FA_PERMANENTEMENTE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.FA_ACTIVADO_EXITOSAMENTE),
            @ApiResponse(responseCode = Constantes.HTTP_400, description = Constantes.CODIGO_INVALIDO)
    })
    public ResponseEntity<ApiSuccessResponse> confirm2FA(@RequestBody Confirm2FARequest request, HttpSession session, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        Usuario usuario = authService.getUserByUsername(username);
        authService.confirm2FA(usuario.id(), request.code(), session);
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_2FA_ACTIVADA));
    }

    @PostMapping(Constantes.AUTH_2FA_DISABLE)
    @RequiresAuth
    @Operation(summary = Constantes.OP_DESACTIVAR_2FA,
               description = Constantes.OP_DESACTIVAR_2FA_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_2FA_DESACTIVADO)
    public ResponseEntity<ApiSuccessResponse> disable2FA(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        Usuario usuario = authService.getUserByUsername(username);
        authService.disable2FA(usuario.id());
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_2FA_DESACTIVADA));
    }

    @PostMapping(Constantes.AUTH_2FA_VERIFY)
    @Operation(summary = Constantes.OP_LOGIN_PASO_2_VERIFICAR_CODIGO_TOTP,
               description = Constantes.OP_LOGIN_PASO_2_DESC)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_CODIGO_VERIFICADO_LOGIN_COMPLETADO),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.RESP_CODIGO_INVALIDO_O_EXPIRADO)
    })
    public ResponseEntity<JwtAuthResponse> verify2FA(@RequestBody Verify2FALoginRequest request, HttpSession session) {
        Usuario usuario = authService.verify2FA(request.username(), request.codigo(), session);

        // Generar tokens JWT
        AuthService.JwtTokenPair tokens = authService.generateTokens(usuario);

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );

        JwtAuthResponse response = new JwtAuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                usuarioResponseDTO,
                Constantes.LOGIN_COMPLETADO_EXITOSAMENTE
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(Constantes.AUTH_2FA_STATUS)
    @RequiresAuth
    @Operation(summary = Constantes.OP_OBTENER_ESTADO_2FA,
               description = Constantes.OP_OBTENER_ESTADO_2FA_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_ESTADO_2FA_OBTENIDO)
    public ResponseEntity<TwoFactorStatusResponse> get2FAStatus(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        Usuario usuario = authService.getUserByUsername(username);
        boolean twoFactorEnabled = authService.get2FAStatus(usuario.id());
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

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar access token",
               description = "Genera un nuevo access token usando un refresh token válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = "Tokens refrescados exitosamente"),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<JwtAuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthService.JwtTokenPair tokens = authService.refreshAccessToken(request.refreshToken());
        Usuario usuario = authService.getUserFromToken(tokens.accessToken());

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                usuario.rol()
        );

        JwtAuthResponse response = new JwtAuthResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                usuarioResponseDTO,
                "Token refrescado exitosamente"
        );
        return ResponseEntity.ok(response);
    }
}