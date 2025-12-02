package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.Constantes;
import org.example.springdemo.ui.service.AuthService;
import org.example.springdemo.ui.dto.LoginRequest;
import org.example.springdemo.ui.dto.LoginResponse;
import org.example.springdemo.domain.model.Usuario;
import org.example.springdemo.ui.dto.UsuarioDTO;
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Usuario usuario = authService.login(request.username(), request.password(), session);

        if (usuario != null) {
            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    usuario.id(),
                    usuario.username(),
                    usuario.email(),
                    usuario.nombre(),
                    usuario.rol()
            );

            LoginResponse response = new LoginResponse(usuarioDTO, Constantes.MSG_LOGIN_SUCCESS);
            return ResponseEntity.ok(response);
        }

        LoginResponse errorResponse = new LoginResponse(Constantes.MSG_LOGIN_INVALID);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }


    @PostMapping(Constantes.AUTH_LOGOUT)
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Constantes.MSG_LOGOUT_SUCCESS);
    }

}