package org.example.springdemo.ui.controller;

import jakarta.servlet.http.HttpSession;
import org.example.springdemo.data.entity.SecretoEntity;
import org.example.springdemo.domain.service.SecretoService;
import org.example.springdemo.ui.dto.*;
import org.example.springdemo.ui.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/secretos")
public class SecretoController {
    
    private final SecretoService secretoService;
    private final AuthService authService;
    
    public SecretoController(SecretoService secretoService, AuthService authService) {
        this.secretoService = secretoService;
        this.authService = authService;
    }
    
    /**
     * Create a new secret
     */
    @PostMapping
    public ResponseEntity<?> crearSecreto(@RequestBody SecretoRequest request, HttpSession session) {
        try {
            if (!authService.isAuthenticated(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            
            Long usuarioId = authService.getUsuarioFromSession(session);
            SecretoEntity secreto = secretoService.guardarSecreto(
                    usuarioId, 
                    request.titulo(), 
                    request.contenido(), 
                    request.password()
            );
            
            return ResponseEntity.ok().body(new SecretoDTO(
                    secreto.getId(),
                    secreto.getTitulo(),
                    secreto.getAutor().getUsername(),
                    secreto.getFechaCreacion(),
                    true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear secreto: " + e.getMessage());
        }
    }
    
    /**
     * List all secrets (own and shared) - metadata only
     */
    @GetMapping
    public ResponseEntity<?> listarSecretos(HttpSession session) {
        try {
            if (!authService.isAuthenticated(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            
            Long usuarioId = authService.getUsuarioFromSession(session);
            List<SecretoEntity> secretos = secretoService.listarSecretos(usuarioId);
            
            List<SecretoDTO> dtos = secretos.stream()
                    .map(s -> new SecretoDTO(
                            s.getId(),
                            s.getTitulo(),
                            s.getAutor().getUsername(),
                            s.getFechaCreacion(),
                            s.getAutor().getId().equals(usuarioId)
                    ))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al listar secretos: " + e.getMessage());
        }
    }
    
    /**
     * View a specific secret (decrypted)
     */
    @PostMapping("/{id}/ver")
    public ResponseEntity<?> verSecreto(@PathVariable Long id, 
                                        @RequestBody VerSecretoRequest request, 
                                        HttpSession session) {
        try {
            if (!authService.isAuthenticated(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            
            Long usuarioId = authService.getUsuarioFromSession(session);
            String contenido = secretoService.verSecreto(id, usuarioId, request.password());
            
            // Get secret metadata directly
            SecretoEntity secreto = secretoService.obtenerSecretoPorId(id);
            if (secreto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Secreto no encontrado");
            }
            
            return ResponseEntity.ok(new SecretoContenidoDTO(
                    secreto.getId(),
                    secreto.getTitulo(),
                    contenido,
                    secreto.getAutor().getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al ver secreto: " + e.getMessage());
        }
    }
    
    /**
     * Share a secret with another user
     */
    @PostMapping("/{id}/compartir")
    public ResponseEntity<?> compartirSecreto(@PathVariable Long id,
                                              @RequestBody CompartirSecretoRequest request,
                                              HttpSession session) {
        try {
            if (!authService.isAuthenticated(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            
            Long usuarioId = authService.getUsuarioFromSession(session);
            secretoService.compartirSecreto(id, usuarioId, request.destinatarioId(), request.password());
            
            return ResponseEntity.ok("Secreto compartido exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al compartir secreto: " + e.getMessage());
        }
    }
    
    /**
     * Stop sharing a secret with a user
     */
    @DeleteMapping("/{id}/compartir/{destinatarioId}")
    public ResponseEntity<?> dejarDeCompartirSecreto(@PathVariable Long id,
                                                      @PathVariable Long destinatarioId,
                                                      HttpSession session) {
        try {
            if (!authService.isAuthenticated(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
            }
            
            Long usuarioId = authService.getUsuarioFromSession(session);
            secretoService.dejarDeCompartirSecreto(id, usuarioId, destinatarioId);
            
            return ResponseEntity.ok("Secreto dejado de compartir exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al dejar de compartir secreto: " + e.getMessage());
        }
    }
}
