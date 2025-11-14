package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.ui.dto.LoginRequest;
import org.example.springdemo.ui.dto.LoginResponse;
import org.example.springdemo.ui.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        return usuarioService.login(request.username(), request.password(), session)
                .map(usuario -> ResponseEntity.ok(
                        new LoginResponse(true, "Login exitoso", usuario)
                ))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "Credenciales inválidas", null)));
    }


    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(HttpSession session) {
        usuarioService.logout(session);
        return ResponseEntity.ok(new LoginResponse(true, "Logout exitoso", null));
    }

    @GetMapping("/session")
    public ResponseEntity<LoginResponse> checkSession(HttpSession session) {
        return usuarioService.getUsuarioFromSession(session)
                .map(usuario -> ResponseEntity.ok(
                        new LoginResponse(true, "Usuario autenticado", usuario)
                ))
                .orElse(ResponseEntity.ok(
                        new LoginResponse(false, "No autenticado", null)
                ));
    }
}