package org.example.emailspring.ui.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.model.Usuario;
import org.example.emailspring.ui.dto.LoginRequest;
import org.example.emailspring.ui.dto.LoginResponse;
import org.example.emailspring.ui.dto.UsuarioDTO;
import org.example.emailspring.ui.dto.UsuarioResponseDTO;
import org.example.emailspring.ui.interceptor.RequiresAuth;
import org.example.emailspring.ui.service.AuthService;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(Constantes.API_AUTH)
@Tag(name = "Autenticación", description = "Operaciones de registro, login y logout de usuarios.")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }



    @PostMapping(Constantes.AUTH_LOGIN)
    @RequiresAuth
    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y establece una sesión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Constantes.MSG_LOGIN_SUCCESS),
            @ApiResponse(responseCode = "401", description = Constantes.MSG_LOGIN_INVALID)
    })
    public ResponseEntity<@NonNull LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
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
    @Operation(summary = "Cerrar sesión", description = "Invalida la sesión actual del usuario.")
    @ApiResponse(responseCode = "200", description = Constantes.MSG_LOGOUT_SUCCESS)
    public ResponseEntity<@NonNull String> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Constantes.MSG_LOGOUT_SUCCESS);
    }


    @PostMapping(Constantes.AUTH_REGISTER)
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario y envía correo de activación.")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente")
    public ResponseEntity<@NonNull Usuario> register(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(usuario));

    }


}