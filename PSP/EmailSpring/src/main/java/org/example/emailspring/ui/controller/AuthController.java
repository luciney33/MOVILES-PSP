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
        Enable2FAResponse data = authService.enable2FA(usuarioId);
        return ResponseEntity.ok(new Enable2FADataResponse(true, data));
    }

    @RequiresAuth
    @PostMapping(Constantes.FA_CONFIRM)
    public ResponseEntity<ApiSuccessResponse> confirm2FA(@RequestBody Confirm2FARequest request, HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        authService.confirm2FA(usuarioId, request.code());
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.DE_DOS_FACTORES_ACTIVADA_CORRECTAMENTE));
    }

    @RequiresAuth
    @PostMapping(Constantes.FA_DISABLE)
    public ResponseEntity<ApiSuccessResponse> disable2FA(HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        authService.disable2FA(usuarioId);
        return ResponseEntity.ok(new ApiSuccessResponse(true, Constantes.DE_DOS_FACTORES_DESACTIVADA));
    }

    @RequiresAuth
    @PostMapping(Constantes.FA_VERIFY)
    public ResponseEntity<Verify2FAResponse> verify2FA(@RequestBody Verify2FARequest request, HttpSession session) {
        UsuarioResponseDTO usuarioDTO = authService.verify2FA(request.username(), request.code(), session);
        return ResponseEntity.ok(new Verify2FAResponse(true, Constantes.LOGIN_COMPLETADO_EXITOSAMENTE, usuarioDTO));
    }


    @RequiresAuth
    @GetMapping(Constantes.FA_STATUS)
    public ResponseEntity<TwoFactorStatusResponse> get2FAStatus(HttpSession session) {
        Long usuarioId = authService.getUsuarioIdFromSession(session);
        boolean twoFactorEnabled = authService.get2FAStatus(usuarioId);
        return ResponseEntity.ok(new TwoFactorStatusResponse(true, twoFactorEnabled));
    }

}