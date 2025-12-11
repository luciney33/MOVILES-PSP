package org.example.emailspring.ui.controller;

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
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;

    }



    @PostMapping(Constantes.AUTH_LOGIN)
    @RequiresAuth
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
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Constantes.MSG_LOGOUT_SUCCESS);
    }


    @PostMapping(Constantes.AUTH_REGISTER)
    public ResponseEntity<Usuario> register(@RequestBody UsuarioDTO usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(usuario));

    }


}