package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.common.constantes;
import org.example.springdemo.ui.dto.LoginRequest;
import org.example.springdemo.ui.dto.LoginResponse;
import org.example.springdemo.ui.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(constantes.API_AUTH)
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping(constantes.AUTH_LOGIN)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        return usuarioService.login(request.username(), request.password(), session)
                .map(usuario -> ResponseEntity.ok(
                        new LoginResponse(true, constantes.MSG_LOGIN_SUCCESS, usuario)
                ))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, constantes.MSG_LOGIN_INVALID, null)));
    }


    @PostMapping(constantes.AUTH_LOGOUT)
    public ResponseEntity<LoginResponse> logout(HttpSession session) {
        usuarioService.logout(session);
        return ResponseEntity.ok(new LoginResponse(true, constantes.MSG_LOGOUT_SUCCESS, null));
    }

    @GetMapping(constantes.AUTH_SESSION)
    public ResponseEntity<LoginResponse> checkSession(HttpSession session) {
        return usuarioService.getUsuarioFromSession(session)
                .map(usuario -> ResponseEntity.ok(
                        new LoginResponse(true, constantes.MSG_USER_AUTHENTICATED, usuario)
                ))
                .orElse(ResponseEntity.ok(
                        new LoginResponse(false, constantes.MSG_USER_NOT_AUTHENTICATED, null)
                ));
    }
}