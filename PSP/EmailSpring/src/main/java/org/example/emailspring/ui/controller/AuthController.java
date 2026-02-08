package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.dto.*;
import org.example.emailspring.ui.security.IsUser;
import org.example.emailspring.ui.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

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
            @ApiResponse(responseCode = Constantes.HTTP_202, description = Constantes.MSG_2FA_REQUERIDO),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.MSG_LOGIN_INVALID)
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        AuthService.LoginResult loginResult = authService.login(request.username(), request.password());

        if (loginResult.requires2FA()) {
            LoginResponse response = new LoginResponse(Constantes.MSG_2FA_REQUERIDO, true);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }

        Usuario usuario = loginResult.usuario();
        JwtTokenPair tokens = authService.generateTokens(usuario);
        String pubKeyBase64 = Base64.getEncoder().encodeToString(usuario.publicKey());


        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                pubKeyBase64,
                usuario.rol()
                );

        return ResponseEntity.ok(new LoginResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                usuarioResponseDTO,
                Constantes.MSG_LOGIN_SUCCESS
        ));
    }

    @PostMapping(Constantes.AUTH_2FA_ENABLE)
    @IsUser
    @Operation(summary = Constantes.HABILITAR_2_FA_PASO_1_GENERAR_QR,
               description = Constantes.GENERA_UN_SECRETO_TOTP_Y_DEVUELVE_EL_QR_CODE_PARA_ESCANEAR_CON_GOOGLE_AUTHENTICATOR)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.SECRETO_Y_QR_CODE_GENERADOS)
    public ResponseEntity<Enable2FADataResponse> enable2FA(@AuthenticationPrincipal UserDetails userDetails) {
        Enable2FAResponse data = authService.enable2FA(userDetails.getUsername());
        return ResponseEntity.ok(new Enable2FADataResponse(true, data));
    }

    @PostMapping(Constantes.AUTH_2FA_CONFIRM)
    @IsUser
    @Operation(summary = Constantes.HABILITAR_2_FA_PASO_2_CONFIRMAR_CODIGO,
               description = Constantes.VERIFICA_EL_CODIGO_TOTP_GENERADO_POR_LA_APP_AUTENTICADORA_Y_ACTIVA_2_FA_PERMANENTEMENTE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.FA_ACTIVADO_EXITOSAMENTE),
            @ApiResponse(responseCode = Constantes.HTTP_400, description = Constantes.CODIGO_INVALIDO)
    })
    public ResponseEntity<ApiSuccessResponse> confirm2FA(@RequestBody Confirm2FARequest request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        authService.confirm2FA(userDetails.getUsername(), request.code());
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_2FA_ACTIVADA));
    }

    @PostMapping(Constantes.AUTH_2FA_DISABLE)
    @IsUser
    @Operation(summary = Constantes.OP_DESACTIVAR_2FA,
               description = Constantes.OP_DESACTIVAR_2FA_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_2FA_DESACTIVADO)
    public ResponseEntity<ApiSuccessResponse> disable2FA(@AuthenticationPrincipal UserDetails userDetails) {
        authService.disable2FA(userDetails.getUsername());
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_2FA_DESACTIVADA));
    }

    @PostMapping(Constantes.AUTH_2FA_VERIFY)
    @Operation(summary = Constantes.OP_LOGIN_PASO_2_VERIFICAR_CODIGO_TOTP,
               description = Constantes.OP_LOGIN_PASO_2_DESC)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_CODIGO_VERIFICADO_LOGIN_COMPLETADO),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.RESP_CODIGO_INVALIDO_O_EXPIRADO)
    })
    public ResponseEntity<LoginResponse> verify2FA(@RequestBody Verify2FALoginRequest request) {
        Usuario usuario = authService.verify2FA(request.username(), request.codigo());
        JwtTokenPair tokens = authService.generateTokens(usuario);
        String pubKeyBase64 = Base64.getEncoder().encodeToString(usuario.publicKey());

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                pubKeyBase64,
                usuario.rol()
        );

        return ResponseEntity.ok(new LoginResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                usuarioResponseDTO,
                Constantes.LOGIN_COMPLETADO_EXITOSAMENTE
        ));
    }


    @GetMapping(Constantes.AUTH_2FA_STATUS)
    @IsUser
    @Operation(summary = Constantes.OP_OBTENER_ESTADO_2FA,
               description = Constantes.OP_OBTENER_ESTADO_2FA_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_ESTADO_2FA_OBTENIDO)
    public ResponseEntity<TwoFactorStatusResponse> get2FAStatus(@AuthenticationPrincipal UserDetails userDetails) {
        boolean enabled = authService.get2FAStatus(userDetails.getUsername());
        return ResponseEntity.ok(new TwoFactorStatusResponse(true, enabled));
    }

    @PostMapping(Constantes.AUTH_LOGOUT)
    @IsUser
    @Operation(summary = Constantes.OP_CERRAR_SESION, description = Constantes.OP_CERRAR_SESION_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.MSG_LOGOUT_SUCCESS)
    public ResponseEntity<ApiSuccessResponse> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(Constantes.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(Constantes.BEARER)) {
            String token = authHeader.substring(Constantes.BEARER_PREFIX_LENGTH);
            authService.logout(token);
        }
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.MSG_LOGOUT_SUCCESS));
    }

    @PostMapping(Constantes.AUTH_REGISTER)
    @Operation(summary = Constantes.OP_REGISTRAR_USUARIO, description = Constantes.OP_REGISTRAR_USUARIO_DESC)
    @ApiResponse(responseCode = Constantes.HTTP_201, description = Constantes.RESP_USUARIO_REGISTRADO_EXITOSAMENTE)
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody UsuarioDTO request) throws Exception {
        Usuario usuario = authService.register(request);
        String pubKeyBase64 = Base64.getEncoder().encodeToString(usuario.publicKey());

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                pubKeyBase64,
                usuario.rol()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(Constantes.REFRESH_ENDPOINT)
    @Operation(summary = Constantes.OP_REFRESCAR_ACCESS_TOKEN,
               description = Constantes.OP_REFRESCAR_ACCESS_TOKEN_DESC)
    @ApiResponses(value = {
            @ApiResponse(responseCode = Constantes.HTTP_200, description = Constantes.RESP_TOKENS_REFRESCADOS),
            @ApiResponse(responseCode = Constantes.HTTP_401, description = Constantes.REFRESH_TOKEN_INVALIDO_O_EXPIRADO)
    })
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        JwtTokenPair tokens = authService.refreshAccessToken(request.refreshToken(), request.accessToken());
        Usuario usuario = authService.getUserFromToken(tokens.accessToken());
        String pubKeyBase64 = Base64.getEncoder().encodeToString(usuario.publicKey());

        UsuarioResponseDTO usuarioResponseDTO = new UsuarioResponseDTO(
                usuario.id(),
                usuario.username(),
                usuario.email(),
                usuario.nombre(),
                pubKeyBase64,
                usuario.rol()
        );

        LoginResponse response = new LoginResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                usuarioResponseDTO,
                Constantes.MSG_TOKEN_REFRESCADO
        );
        return ResponseEntity.ok(response);
    }
}

